package sample.kernel.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import javafx.scene.paint.Color;
import sample.control.Controller;
import sample.control.load.LoadsManagerController;
import sample.control.util.LoadingController;
import sample.kernel.entity.Account;
import sample.kernel.entity.Attachment;
import sample.kernel.entity.Idea;
import sample.kernel.entity.Task;
import sample.kernel.entity.WorkNote;
import sample.kernel.exception.ConnectionException;
import sample.kernel.service.AccountService;
import sample.kernel.service.IdeaService;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class ThinkUtil {
	
	private static Language lang = Language.EN;
	private static File directory = new File("/");
	
	private static Deque<Attachment> toUploadS = new ArrayDeque<Attachment>();
	private static Deque<Attachment> toDownloadS = new ArrayDeque<Attachment>();
	
	private static Boolean loadProcess = false;
	private static Boolean trayPopup = true;
	private static List<ScheduledExecutorService> appTasks = new ArrayList<ScheduledExecutorService>();
	private static Integer ideasPerPage = 10;
	
	private static DBConnection localDBConnection = new DBConnection();
	private static DBConnection externDBConnection = new DBConnection();
	
	private static Boolean localConnection = true;
	
	private static Boolean userConnection = false;
	
	private static Boolean defaultheme = false;
	
	//TODO: to save in database
	/**Replace the auto increment of Ids**/
	/*
	private static Integer countIdeas = 0;
	private static Integer countAttachs = 0;
	private static Integer countWNs = 0;
	*/
	
	//MySQL MAX Date
	public final static LocalDate MAX_DATETIME = LocalDate.of(9999, 12, 31);
	/*
	public static void setCountIdea(int count) {
		countIdeas = count;
	}
	
	public static Integer countIdea() {
		++ countIdeas;
		return countIdeas;
	}
	
	public static void setCountAttachs(int count) {
		countAttachs = count;
	}
	
	public static Integer countAttach() {
		++ countAttachs;
		return countAttachs;
	}
	
	public static void setCountWN(int count) {
		countWNs = count;
	}
	
	public static Integer countWN() {
		++ countWNs;
		return countWNs;
	}
		*/
	public static final String datetimeFormatter = "yyyy/MM/dd - HH:mm";
	public static final String dateFormatter = "yyyy/MM/dd";
	public static final int maxContentSize = Integer.MAX_VALUE;
	
	public static Language getLang() {
		return lang;
	}

	public static void setLang(Language lang) {
		ThinkUtil.lang = lang;
	}

	public static File getDirectory() {
		if (!directory.exists())
			try {
				directory.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return directory;
	}

	public static void setDirectory(File directory) {
		ThinkUtil.directory = directory;
	}
	
	public static Boolean getLoadProcess() {
		return loadProcess;
	}
	
	public static void addAppTask(ScheduledExecutorService appTask) {
		appTasks.add(appTask);
	}

	public static void shutdownTasks() {
		for (ScheduledExecutorService task : appTasks) {
			task.shutdown();
		}
		stopLoadTask();
	}
	
	public static void addToUpload(Attachment attach) {
		if (!toUploadS.contains(attach)) {
			toUploadS.addLast(attach);
		}
	}
	
	public static void addToDownload(Attachment attach) {
		if (!toDownloadS.contains(attach)) {
			toDownloadS.addLast(attach);
		}
	}
	
	public static void removeToUpload(Attachment attach) {
		toUploadS.remove(attach);
		if (toUploadS.isEmpty()) stopLoadTask();
	}
	
	public static void removeToDownload(Attachment attach) {
		toDownloadS.remove(attach);
		if (toDownloadS.isEmpty()) stopLoadTask();
	}
	
	public static void removeToUploadPane(Attachment attach) {
		toUploadS.remove(attach);
		if (loadController != null) loadController.removeUploadPane(attach);
		if (toUploadS.isEmpty()) stopLoadTask();
	}
	
	public static void removeToDownloadPane(Attachment attach) {
		toDownloadS.remove(attach);
		if (loadController != null) loadController.removeDownloadPane(attach);
		if (toDownloadS.isEmpty()) stopLoadTask();
	}
	
	private static void loadtoLoadS() {
		Deque<Attachment> toLoadS = new ArrayDeque<Attachment>();
		toLoadS.addAll(toDownloadS);
		toLoadS.addAll(toUploadS);
		try {
			if (!toLoadS.isEmpty() && getLoadProcess() && FTPUtil.checkConnection()) {
				Attachment attach = toLoadS.getFirst();
				if (toDownloadS.contains(attach)) {
					try {
						attach.setDownloadControllerProgress();
						FTPUtil.download(attach);
						removeToDownloadPane(attach);
						attach.setDownloadControllerProgress();
					} catch (Exception e) {
						removeToDownload(attach);
						addToDownload(attach);
						attach.setDownloadControllerProgress();
					}
					finally {
						loadtoLoadS();
						setLoadProcess(false);
					}
				}
				else {
					try {
						attach.setUploadControllerProgress();
						FTPUtil.upload(attach);
						removeToUploadPane(attach);
						attach.setUploadControllerProgress();
					} catch (Exception e) {
						removeToUpload(attach);
						addToUpload(attach);
						attach.setUploadControllerProgress();
					}
					finally {
						loadtoLoadS();
						setLoadProcess(false);
					}
				}
			}
			else
				setLoadProcess(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setLoadProcess(false);
	}

	public static Deque<Attachment> getToUploadS() {
		return toUploadS;
	}
	
	public static Deque<Attachment> getToDownloadS() {
		return toDownloadS;
	}

	private static Thread loadThread;
	
	public static Boolean loadTaskIsStopped() {
		return (loadThread == null || loadThread.isInterrupted() || !loadThread.isAlive());
	}

	public static void runLoadTask() {
		if (loadTaskIsStopped()) {
			javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
				@Override
				protected Void call() throws Exception {
					loadtoLoadS();
					return null;
				}
			};
			loadThread = new Thread(task);
			loadThread.start();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void stopLoadTask() {
		if (!loadTaskIsStopped()) {
			loadThread.interrupt();
			loadThread.stop();
		}
		loadThread = null;
	}
	
	public static void initialize() {
		runLoadTask();
		refreshDB();
	}
	
	/**Files and Buffers**/
	
	public static final String idea_extension = "idea";
	
	/**Hibernate**/
	public static void addIdeaToSave(Idea idea) {
		try {
			LoadingController.load();
			IdeaService.add(idea);
			Notification.addNotification(ThinkStr.idea_saved.toString());
			LoadingController.unload();
		}
		catch (Exception e) {
			e.printStackTrace();
			Notification.addNotification(new ConnectionException());
			LoadingController.unload();
		}
	}
	
	public static void addIdeaToRemove(Idea idea) {
		try {
			LoadingController.load();
			idea.clearRelatedIdeas();
			IdeaService.delete(idea);
			Notification.addNotification(ThinkStr.idea_deleted.toString());
			LoadingController.unload();
		}
		catch (Exception e) {
			e.printStackTrace();
			Notification.addNotification(new ConnectionException());
			LoadingController.unload();
		}
	}
	
	public static void addIdeaToModify(Idea idea) {
		try {
			LoadingController.load();
			IdeaService.modify(idea);
			IdeaService.refresh(idea);
			Notification.addNotification(ThinkStr.idea_modified.toString());
			LoadingController.unload();
		}
		catch (Exception e) {
			e.printStackTrace();
			Notification.addNotification(new ConnectionException());
			LoadingController.unload();
		}
	}
	
	public static void addIdeaToModify(Idea idea, Boolean tray) {
		try {
			LoadingController.load();
			IdeaService.modify(idea);
			IdeaService.refresh(idea);
			if (tray) Notification.addNotification(ThinkStr.idea_modified.toString());
			LoadingController.unload();
		}
		catch (Exception e) {
			e.printStackTrace();
			if (tray) Notification.addNotification(new ConnectionException());
			LoadingController.unload();
		}
	}
	
	public static void refreshIdea(Idea idea) {
		try {
			LoadingController.load();
			IdeaService.refresh(idea);
			Notification.addNotification(ThinkStr.idea_refreshed.toString());
			LoadingController.unload();
		}
		catch (Exception e) {
			e.printStackTrace();
			Notification.addNotification(new ConnectionException());
			LoadingController.unload();
		}
	}
	
	public static Idea searchIdea(int id) {
		try {
			LoadingController.load();
			Idea idea = IdeaService.search(id);
			LoadingController.unload();
			return idea;
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
			return ThinkUtil.getIdeaList().stream().filter(
					idea -> idea.getId() == id
					).findFirst().orElse(null);
		}
	}
	
	public static Idea searchIdea(String str) {
		try {
			LoadingController.load();
			List<Idea> ideas = IdeaService.completerSearch(str);
			LoadingController.unload();
			if (ideas.isEmpty()) return null;
			else 
				return ideas.get(0);
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
			return ThinkUtil.getIdeaList().stream().filter(
					idea -> idea.getId() == Idea.getId(str)
					).findFirst().orElse(null);
		}
	}
	
	public static List<Idea> searchIdea(String title, int page) {
		try {
			LoadingController.load();
			List<Idea> ideas = IdeaService.search(title, page);
			LoadingController.unload();
			return ideas;
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
			return ThinkUtil.getIdeaList().stream().filter(
					idea -> idea.getTitle().contains(title)
					).collect(Collectors.toList());
		}
	}
	
	public static List<Idea> filterIdeas(String title,LocalDate date, Boolean task, Boolean done, Year year, int page) {
		try {
			LoadingController.load();
			List<Idea> ideas = IdeaService.search(title, date, task, done, year, page);
			Notification.addNotification(ThinkStr.results.toString());
			LoadingController.unload();
			return ideas;
		}
		catch (Exception e) {
			e.printStackTrace();
			Notification.addNotification(new ConnectionException());
			LoadingController.unload();
			List<Idea> ideas = getIdeaList();
			if (!title.isEmpty() && !title.isBlank())
				ideas.retainAll(ThinkUtil.getIdeaList().stream().filter(
						idea -> idea.getTitle().contains(title)
						).collect(Collectors.toList()));
			if (date != null)
				ideas.retainAll(ThinkUtil.getIdeaList().stream().filter(
						idea -> idea.getTime().toLocalDate().equals(date)
						).collect(Collectors.toList()));
			if (task)
				ideas.retainAll(ThinkUtil.getIdeaList().stream().filter(
						idea -> idea instanceof Task
						).collect(Collectors.toList()));
			if (year != null)
				ideas.retainAll(ThinkUtil.getIdeaList().stream().filter(
						idea -> idea.getTime().getYear() == year.getValue()
						).collect(Collectors.toList()));
			if (done != null)
				ideas.retainAll(ThinkUtil.getIdeaList().stream().filter(
						idea -> idea.isDone() == done
						).collect(Collectors.toList()));
			return ideas;
		}
	}
	
	public static Year getFirstYear() {
		try {
			LoadingController.load();
			Year firstYear = IdeaService.getFirstYear();
			LoadingController.unload();
			return firstYear;
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
			Idea idea = ThinkUtil.getIdeaList().stream().min(Comparator.comparing(Idea::getTime)).orElse(null);
			if (idea == null)
				return null;
			else
				return Year.of(idea.getTime().getYear());
		}
	}
	
	public static List<Idea> completerSearchIdea(String str) {
		try {
			LoadingController.load();
			List<Idea> ideas = IdeaService.completerSearch(str);
			LoadingController.unload();
			return ideas;
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
			return ThinkUtil.getIdeaList().stream().filter(
					idea -> idea.toString().contains(str)
					).collect(Collectors.toList());
		}
	}
	
	public static Long getSizeSearchIdeas(String title) {
		try {
			LoadingController.load();
			Long sizeSearch = IdeaService.getSizeSearch(title);
			LoadingController.unload();
			return sizeSearch;
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
			return (long) ThinkUtil.getIdeaList().stream().filter(
					idea -> idea.getTitle().contains(title)
					).collect(Collectors.toList()).size();
		}
	}
	
	public static Long getPagesSearchIdeas(String title) {
		try {
			LoadingController.load();
			Long pagesSearch = IdeaService.getPagesSearch(title);
			LoadingController.unload();
			return pagesSearch;
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
			return (long) 1;
		}
	}
	
	public static Long getIdeasLastPageNumber() {
		try {
			LoadingController.load();
			Long lastPage = IdeaService.getLastPageNumber();
			LoadingController.unload();
			return lastPage;
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
			return (long) 1;
		}
	}
	
	public static List<Idea> requestIdeasList(int page) {
		try {
			LoadingController.load();
			List<Idea> ideas = IdeaService.requestList(page);
			LoadingController.unload();
			return ideas;
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
			return getIdeaList();
		}
	}
	
	public static List<Object> requestEvents(LocalDateTime startDate, LocalDateTime finishDate) {
		try {
			LoadingController.load();
			List<Object> events = IdeaService.requestEvents(startDate, finishDate);
			LoadingController.unload();
			return events;
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
			List<Object> events = new ArrayList<Object>();
			for (Idea idea : getIdeaList()) {
				for (WorkNote wN : idea.getWorkNotes())
					events.add(new WorkNote.WorkNoteEvent(idea.getId(), wN.getDate(), idea.getColor()));
			}
			for (Idea idea : getIdeaList()) {
				if (idea instanceof Task) {
					Task task = (Task) idea;
					events.add(new Task.TaskEvent(task.getId(), task.getStartTime(), task.getFinishTime(),
							task.getColor(), task.getTaskPeriod()));
				}
			}
			return events;
		}
	}
	
	//TODO: for settings!!
	public static void initializeDB(String host, String port, String databaseName, String username, String password,
			int mySQLVersion, @SuppressWarnings("rawtypes") List<Class> annotatedClasses, String mode, String timeZone)  {
		try {
			LoadingController.load();
			if (localConnection)
				localDBConnection.initialize(host, port, databaseName, username, password, mySQLVersion, annotatedClasses, mode, timeZone);
			else
				externDBConnection.initialize(host, port, databaseName, username, password, mySQLVersion, annotatedClasses, mode, timeZone);
			LoadingController.unload();
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
		}
	}
	
	public static void refreshDB() {
		try {
			LoadingController.load();
			if (!DBConnection.isDBConnected())
				if (localConnection)
					localDBConnection.initialize(DBConnection.getAnnotatedClasses(), "update", "UTC");
				else
					externDBConnection.initialize(DBConnection.getAnnotatedClasses(), "update", "UTC");
			LoadingController.unload();
		}
		catch (Exception e) {
			e.printStackTrace();
			Notification.addNotification(new ConnectionException());
			LoadingController.unload();
		}
	}
	
	public static Long getIdeasNumber() {
		try {
			LoadingController.load();
			Long number = IdeaService.getIdeasNumber();
			LoadingController.unload();
			return number;
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
			return null;
		}
	}
	
	public static Long getTasksNumber() {
		try {
			LoadingController.load();
			Long number = IdeaService.getTasksNumber();
			LoadingController.unload();
			return number;
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
			return null;
		}
	}
	
	public static Long getWorkNotesNumber() {
		try {
			LoadingController.load();
			Long number = IdeaService.getWorkNotesNumber();
			LoadingController.unload();
			return number;
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
			return null;
		}
	}
	
	public static Long getAttachsNumber() {
		try {
			LoadingController.load();
			Long number = IdeaService.getAttachsNumber();
			LoadingController.unload();
			return number;
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
			return null;
		}
	}
	
	public static String getExtention(File file) {
		String[] fileStrs = (file.getName().split("\\."));
		if (fileStrs.length <= 1) return "";
		return fileStrs[(fileStrs.length - 1)];
	}
	
	public static void exportIdeas(List<Idea> ideas, String path) {
		for (Idea idea : ideas) {
			idea.exportIdea(new File(path + "/" + idea.toString() + "." + idea_extension));
		}
	}
	
	public static void importIdeas(File file) {
		for (File f : file.listFiles()) {
			if (getExtention(f).equals(idea_extension)) {
				Idea idea = new Idea();
				idea.importIdea(f);
				//idea.setId(countIdea());
				if (idea.isEmpty()) return;
				try {
					LoadingController.load();
					IdeaService.add(idea);
					LoadingController.unload();
				}
				catch (Exception e) {
					e.printStackTrace();
					Notification.addNotification(e);
					LoadingController.unload();
				}
			}
		}
	}
	
	public static List<Idea> getIdeaList() {
		return new ArrayList<Idea>();
	}
	
	public static Integer getIdeasPerPage() {
		return ideasPerPage;
	}
	
	public static void setIdeasPerPage(Integer ideasPerPage) {
		ThinkUtil.ideasPerPage = ideasPerPage;
	}

	/**FXML**/
	private static LoadsManagerController loadController;
	
	public static void setLoadProcess(Boolean loadProcess) {
		ThinkUtil.loadProcess = loadProcess;
		if (loadProcess)
			runLoadTask();
		else
			stopLoadTask();
		if (loadController != null) loadController.setDisableButtons();
	}

	public static Boolean getTrayPopup() {
		return trayPopup;
	}

	public static void setTrayPopup(Boolean trayPopup) {
		ThinkUtil.trayPopup = trayPopup;
	}

	public static void setLoadController(LoadsManagerController loadController) {
		ThinkUtil.loadController = loadController;
	}
	
	public static String toHexString(Color color) {
		  int r = ((int) Math.round(color.getRed()     * 255)) << 24;
		  int g = ((int) Math.round(color.getGreen()   * 255)) << 16;
		  int b = ((int) Math.round(color.getBlue()    * 255)) << 8;
		  int a = ((int) Math.round(color.getOpacity() * 255));
		  return String.format("#%08X", (r + g + b + a));
	}

	public static DBConnection getLocalDBConnection() {
		return localDBConnection;
	}

	public static void setLocalDBConnection(DBConnection localConnection) {
		ThinkUtil.localDBConnection = localConnection;
	}

	public static DBConnection getExternDBConnection() {
		return externDBConnection;
	}

	public static void setExternDBConnection(DBConnection externConnection) {
		ThinkUtil.externDBConnection = externConnection;
	}

	public static Boolean getLocalConnection() {
		return localConnection;
	}

	public static void setLocalConnection(Boolean localConnection) {
		ThinkUtil.localConnection = localConnection;
	}

	public static Boolean userConnected() {
		return userConnection;
	}

	public static void setUserConnection(Boolean userConnection) {
		ThinkUtil.userConnection = userConnection;
	}
	
	public static Boolean checkDBConnection() {
		Boolean test = true;
		try {
			LoadingController.load();
			IdeaService.check();
			LoadingController.unload();
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
			test = false;
		}
		return test;
	}
	
	public static void connectAccount(String password) {
		try {
			LoadingController.load();
			setUserConnection(AccountService.connect(password));
			LoadingController.unload();
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
		}
	}
	
	public static void disconnectAccount() {
		try {
			LoadingController.load();
			if (userConnected()) {
				AccountService.disconnect();
				setUserConnection(false);
			}
			LoadingController.unload();
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
		}
	}
	
	public static void modifyAccount(Account account) {
		try {
			LoadingController.load();
			AccountService.modify(account);
			Notification.addNotification(ThinkStr.modified.toString());
			LoadingController.unload();
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
		}
	}
	
	public static List<Account> requestAccountList() {
		try {
			LoadingController.load();
			List<Account> accounts = AccountService.requestList();
			LoadingController.unload();
			return accounts;
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
			return new ArrayList<Account>();
		}
	}

	public static Boolean getDefaultheme() {
		return defaultheme;
	}

	public static void setDefaultheme(Boolean defaultheme) {
		ThinkUtil.defaultheme = defaultheme;
	}
	
	private final static File data = new File("ThinkBAY.dat");
	public final static File log = new File("log.txt");
	
	public static void serialize() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(data)));
			out.writeObject(Account.getAddress());
			out.writeObject(lang);
			out.writeObject(directory);
			out.writeObject(trayPopup);
			out.writeObject(ideasPerPage);
			out.writeObject(localDBConnection);
			out.writeObject(externDBConnection);
			out.writeObject(localConnection);
			out.writeObject(userConnected());
			out.writeObject(defaultheme);
			out.writeObject(Controller.height);
			out.writeObject(Controller.width);
			out.writeObject(FTPUtil.getServer());
			out.writeObject(FTPUtil.getPort());
			out.writeObject(FTPUtil.getUser());
			out.writeObject(FTPUtil.getPass());
			out.close();
			encryptFile(data.getAbsolutePath(), "passwordYoucef");
		} catch (IOException | GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void deserialize() {
		if (data.exists()) {
			ObjectInputStream in;
			try {
				decryptFile(data.getAbsolutePath(), "passwordYoucef");
				in = new ObjectInputStream(new BufferedInputStream(
						new FileInputStream(tempFileDec)));
				tempFileDec.deleteOnExit();
				String address = (String) in.readObject();
				setLang((Language) in.readObject());
				setDirectory((File) in.readObject());
				setTrayPopup((Boolean) in.readObject());
				setIdeasPerPage((Integer) in.readObject());
				setLocalDBConnection((DBConnection) in.readObject());
				setExternDBConnection((DBConnection) in.readObject());
				setLocalConnection((Boolean) in.readObject());
				setUserConnection((Boolean) in.readObject());
				if (! address.equals(Account.getAddress()))
					setUserConnection(false);
				setDefaultheme((Boolean) in.readObject());
				Controller.height = (Double) in.readObject();
				Controller.width = (Double) in.readObject();
				FTPUtil.setServer((String) in.readObject());
				FTPUtil.setPort((Integer) in.readObject());
				FTPUtil.setUser((String) in.readObject());
				FTPUtil.setPass((String) in.readObject());
				in.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

    //Arbitrarily selected 8-byte salt sequence:
    private static final byte[] salt = {
        (byte) 0x43, (byte) 0x76, (byte) 0x95, (byte) 0xc7,
        (byte) 0x5b, (byte) 0xd7, (byte) 0x45, (byte) 0x17 
    };

    private static Cipher makeCipher(String pass, Boolean decryptMode) throws GeneralSecurityException{

        //Use a KeyFactory to derive the corresponding key from the passphrase:
        PBEKeySpec keySpec = new PBEKeySpec(pass.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(keySpec);
        //Create parameters from the salt and an arbitrary number of iterations:
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 42);
        /*Dump the key to a file for testing: */
        //Set up the cipher:
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        //Set the cipher mode to decryption or encryption:
        if(decryptMode){
            cipher.init(Cipher.ENCRYPT_MODE, key, pbeParamSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key, pbeParamSpec);
        }
        return cipher;
    }


    /**Encrypts one file to a second file using a key derived from a passphrase:**/
    public static void encryptFile(String fileName, String pass)
                                throws IOException, GeneralSecurityException{
        byte[] decData;
        byte[] encData;
        File inFile = new File(fileName);
        //Generate the cipher using pass:
        Cipher cipher = makeCipher(pass, true);
        //Read in the file:
        FileInputStream inStream = new FileInputStream(inFile);
        int blockSize = 8;
        //Figure out how many bytes are padded
        int paddedCount = blockSize - ((int)inFile.length()  % blockSize );
        //Figure out full size including padding
        int padded = (int)inFile.length() + paddedCount;
        decData = new byte[padded];
        inStream.read(decData);
        inStream.close();
        //Write out padding bytes as per PKCS5 algorithm
        for( int i = (int)inFile.length(); i < padded; ++i ) {
            decData[i] = (byte)paddedCount;
        }
        //Encrypt the file data:
        encData = cipher.doFinal(decData);
        //Write the encrypted data to a new file:
        FileOutputStream outStream = new FileOutputStream(inFile);
        outStream.write(encData);
        outStream.close();
    }
    
    private static File tempFileDec = null;
    
    /**Decrypts one file to a second file using a key derived from a passphrase:**/
    public static void decryptFile(String fileName, String pass)
                            throws GeneralSecurityException, IOException{
        byte[] encData;
        byte[] decData;
        File inFile = new File(fileName);
        //Generate the cipher using pass:
        Cipher cipher = makeCipher(pass, false);
        //Read in the file:
        FileInputStream inStream = new FileInputStream(inFile );
        encData = new byte[(int)inFile.length()];
        inStream.read(encData);
        inStream.close();
        //Decrypt the file data:
        decData = cipher.doFinal(encData);
        //Figure out how much padding to remove
        int padCount = (int)decData[decData.length - 1];
        //Naive check, will fail if plaintext file actually contained
        //this at the end
        //For robust check, check that padCount bytes at the end have same value
        if( padCount >= 1 && padCount <= 8 ) {
            decData = Arrays.copyOfRange( decData , 0, decData.length - padCount);
        }
        //Write the decrypted data to a new file:
        tempFileDec = File.createTempFile(Account.getAddress(), null);
        FileOutputStream target = new FileOutputStream(tempFileDec);
        target.write(decData);
        target.close();
    }

}
