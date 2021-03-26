package sample.control.menu;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.Main;
import sample.control.Controller;
import sample.control.idea.IdeaInfoController;
import sample.control.idea.creator.WorkNoteCreatorController;
import sample.control.load.LoadsManagerController;
import sample.kernel.entity.Account;
import sample.kernel.entity.Idea;
import sample.kernel.entity.Task;
import sample.kernel.util.FTPUtil;
import sample.kernel.util.Notification;
import sample.kernel.util.ThinkStr;
import sample.kernel.util.ThinkUtil;

public class ThinkMenuController extends Controller {
	
    @FXML
    private SplitPane split;
	/**Menu**/
	@FXML
	private MenuBar menu;
	@FXML
	private Menu general;
	@FXML
	private MenuItem newIdea;
	@FXML
	private MenuItem newWN;
	@FXML
	private MenuItem refresh;
	@FXML
	private CheckMenuItem focus;
	@FXML
	private MenuItem disconnect;
	@FXML
	private MenuItem close;
	@FXML
	private Menu importExport;
	@FXML
	private MenuItem importIdeas;
	@FXML
	private MenuItem exportIdeasTable;
	@FXML
	private Menu loadManager;
	@FXML
	private MenuItem loads;
	@FXML
	private Menu help;
	@FXML
	private MenuItem aboutUS;
	/**Accordion**/
	@FXML
	private Accordion sideBar;
	@FXML
	private TitledPane infos;
	@FXML
	private VBox infosBox;
	@FXML
	private TitledPane undoneIdeas;
	@FXML
	private VBox undoneIdeasBox;
	@FXML
	private TitledPane taskNotifs;
	@FXML
	private VBox taskNoifsBox;
	/**Tabs**/
	@FXML
	private TabPane tabPane;
	@FXML
	private Tab calendar;
	@FXML
	private Tab ideasTable;
	@FXML
	private Tab settings;
	/**IdeasTable**/
	@FXML
	private TableView<Idea> ideasT;
    @FXML
    private TableColumn<Idea, Integer> idC;
    @FXML
    private TableColumn<Idea, String> titleC;
    @FXML
    private TableColumn<Idea, String> taskC;
    @FXML
    private TableColumn<Idea, String> dateC;
    @FXML
    private TableColumn<Idea, String> startDateC;
    @FXML
    private TableColumn<Idea, String> finishDateCC;
    @FXML
    private TableColumn<Idea, Boolean> deleteC;
    @FXML
    private Spinner<Integer> spinner;
    @FXML
    private TextField ideaCompleter;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ToggleButton task;
    @FXML
    private ComboBox<Done> done;
    @FXML
    private ComboBox<Year> yearBox;
    @FXML
    private Button reset;
    @FXML
    private Button filter;
    
    private Boolean filterBool = false;
        
    @FXML
    public void initialize() {
		getStage().setTitle(ThinkStr.thinkBAY.toString());
		getStage().getIcons().add(new Image(Main.class.getResourceAsStream(Controller.icon_path)));
    	calendar();
    	menuBar();
    	initNotifs();
    	initIdeasTable();
    	settings();
    	disableEditing(!ThinkUtil.userConnected());
    	super.initialize();
    }
    
    private void menuBar() {
    	general.setText(ThinkStr.general.toString());
    	newIdea.setText(ThinkStr.new_idea.toString());
    	newWN.setText(ThinkStr.new_worknote.toString());
    	refresh.setText(ThinkStr.refresh.toString());
    	focus.setText(ThinkStr.focus.toString());
    	importExport.setText(ThinkStr.import_export.toString());
    	importIdeas.setText(ThinkStr.ideas_import.toString());
    	exportIdeasTable.setText(ThinkStr.table_export.toString());
    	loadManager.setText(ThinkStr.load_manager.toString());
    	loads.setText(ThinkStr.load_manager.toString());
    	help.setText(ThinkStr.help.toString());
    	aboutUS.setText(ThinkStr.about_us.toString());
    	disconnect.setText(ThinkStr.disconnect.toString());
    	close.setText(ThinkStr.close.toString());
    }
    
    private void calendar() {
    	calendar.setText(ThinkStr.calendar.toString());
    	try {
    		FXMLLoader loader = new FXMLLoader();
    		loader.setLocation(Main.class.getResource("vue/Calendar.fxml"));
			calendar.setContent(loader.load());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void initIdeasTable() {
    	ideasTable.setText(ThinkStr.ideas_table.toString());
		idC.setText(ThinkStr.id.toString());
		titleC.setText(ThinkStr.title.toString());
		taskC.setText(ThinkStr.task.toString());
		dateC.setText(ThinkStr.date_and_time.toString());
		startDateC.setText(ThinkStr.startDate.toString());
		finishDateCC.setText(ThinkStr.finishDate.toString());
		deleteC.setText(ThinkStr.delete.toString());
		
		idC.setCellValueFactory(cellData -> cellData.getValue().getIdProperty());
		titleC.setCellValueFactory(cellData -> cellData.getValue().getTitleProperty());
		taskC.setCellValueFactory(cellData -> cellData.getValue().getTaskProperty());
		dateC.setCellValueFactory(cellData -> cellData.getValue().getTimeProperty());
		startDateC.setCellValueFactory(cellData -> cellData.getValue().getStartTimeProperty());
		finishDateCC.setCellValueFactory(cellData -> cellData.getValue().getFinishTimeProperty());
		deleteC.setCellFactory(new Callback<TableColumn<Idea, Boolean>, TableCell<Idea, Boolean>>() {
			@Override
			public TableCell<Idea, Boolean> call(TableColumn<Idea, Boolean> column) {
				return new ButtonCell(ideasT, "✘");
			}
		});
		ideasT.setItems(FXCollections.observableArrayList(ThinkUtil.requestIdeasList(1)));
		ideaCompleter.setPromptText(ThinkStr.search_idea.toString());
		int firstPage = 1;
		int lastPage = ThinkUtil.getIdeasLastPageNumber().intValue();
		if (lastPage == 0) {
			firstPage = 1;
			lastPage = 1;
		}
		 SpinnerValueFactory<Integer> valueFactory =
	                new SpinnerValueFactory.IntegerSpinnerValueFactory(firstPage, lastPage, firstPage);
		spinner.setValueFactory(valueFactory);
		spinner.valueProperty().addListener(new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				if (newValue != oldValue) {
					if (! filterBool) {
						ideasT.setItems(FXCollections.observableArrayList(ThinkUtil.requestIdeasList(newValue)));
						ideasT.refresh();
					}
					else {
						ideasT.setItems(FXCollections.observableArrayList(ThinkUtil.filterIdeas(ideaCompleter.getText(), datePicker.getValue(),
				    			task.isSelected(), done.getValue().toBoolean(), yearBox.getValue(), newValue)));
						ideasT.refresh();
					}
				}
			}
		});
		
		datePicker.setPromptText(ThinkStr.choose_date.toString());
		task.setText(ThinkStr.task.toString());
		done.setPromptText(ThinkStr.done.toString());
		done.setValue(Done.indeterminate);
		done.setItems(FXCollections.observableArrayList(Done.values()));
		yearBox.setPromptText(ThinkStr.year.toString());
		List<Year> years = new ArrayList<Year>();
		Year firstYear = ThinkUtil.getFirstYear();
		if (firstYear != null) {
			for (Year i = firstYear; !i.isAfter(Year.now()); i = i.plusYears(1)) {
				years.add(i);
			}
		}
		yearBox.setItems(FXCollections.observableArrayList(years));
		reset.setText(ThinkStr.reset.toString());
		filter.setText(ThinkStr.filter.toString());
	 	
		ideasT.setRowFactory(tv -> {
			TableRow<Idea> row = new TableRow<>() {
				@Override
				public void updateItem(Idea item, boolean empty) {
					super.updateItem(item, empty);
					if (item != null) {
						setStyle("");
						setTooltip(new Tooltip((item.getWorkDegree() * 100) + "%"));
						if (item.isDone() != null) {
							if (item.isDone())
								setStyle("   -fx-background-color: " + ThinkUtil.toHexString(Color.MEDIUMSEAGREEN) +";\r\n" + 
										 "   -fx-text-fill: white;");
						}
					}
				}
			};
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() > 1 && (!row.isEmpty())) {
					Idea rowData = row.getItem();
					IdeaInfoController.openIdeaInfo(rowData, getStage());
				}
			});
			return row;
		});
    }
    
    private void initNotifs() {
    	infos.setText(ThinkStr.infos.toString());
    	infos();
    	undoneIdeas.setText(ThinkStr.ideas_undone_notifs.toString());
    	taskNotifs.setText(ThinkStr.task_notifs.toString());
    	sideBar.setExpandedPane(infos);
    	taskNoifsBox.getChildren().clear();
    	undoneIdeasBox.getChildren().clear();
    	for (Idea idea : Notification.getIdeasWN())
    		addUndoneIdea(idea);
    	for (Task task : Notification.getTasks())
    		addTaskNotif(task);
    }
    
    private void infos() {
    	VBox box = new VBox(10);
    	infos.setContent(box);
    	String hDate = HijrahDate.now().format(DateTimeFormatter.ofPattern(ThinkUtil.dateFormatter));
    	String gDate = LocalDate.now().format(DateTimeFormatter.ofPattern(ThinkUtil.dateFormatter));
    	Label label = new Label ("بسم الله الرّحمن الرّحيم\nإن شاء الله");
    	label.setContentDisplay(ContentDisplay.CENTER);
    	Label dateLabel = new Label (hDate + " " + ThinkStr.similary_to.toString() + " " + gDate);
    	Boolean check = ThinkUtil.checkDBConnection();
    	String type = (ThinkUtil.getLocalConnection()) ? ThinkStr.local.toString() : ThinkStr.extern.toString();
    	String checkS = (check) ? ThinkStr.available.toString() : ThinkStr.unavailable.toString();
    	Label databaseConnection = new Label(ThinkStr.database_status + " (" + type + ") : " + checkS);
    	if (check)
    		databaseConnection.setTextFill(Color.LIMEGREEN);
    	else
    		databaseConnection.setTextFill(Color.RED);
    	check = true;
    	try {
			check = FTPUtil.checkConnection();
		} catch (Exception e) {
			check = false;
		}
    	checkS = (check) ? ThinkStr.available.toString() : ThinkStr.unavailable.toString();
    	Label ftpConnection = new Label(ThinkStr.ftp + ": " + checkS);
    	if (check)
    		ftpConnection.setTextFill(Color.LIMEGREEN);
    	else
    		ftpConnection.setTextFill(Color.RED);
    	check = ThinkUtil.userConnected();
    	checkS = (check) ? ThinkStr.connected.toString() : ThinkStr.not_connected.toString();
    	Label userConnection = new Label(ThinkStr.account + ": " + checkS);
    	if (check)
    		userConnection.setTextFill(Color.LIMEGREEN);
    	else
    		userConnection.setTextFill(Color.RED);
    	Label language = new Label(ThinkStr.language + ": " + ThinkUtil.getLang());
    	Label lastVisit = new Label();
    	lastVisit.setText(ThinkStr.device_id + ": " + Account.getAddress());
    	Label ideasNumLabel = new Label(ThinkStr.ideas_num + ": " + ThinkUtil.getIdeasNumber());
    	Label tasksNumLabel = new Label(ThinkStr.tasksnum + ": " + ThinkUtil.getTasksNumber());
    	Label wNumLabel = new Label(ThinkStr.worknotes_num + ": " + ThinkUtil.getWorkNotesNumber());
    	Label attachsNumLabel = new Label(ThinkStr.attachs_num + ": " + ThinkUtil.getAttachsNumber());
    	
    	box.getChildren().addAll(label, dateLabel, databaseConnection, ftpConnection, userConnection,
    			language, lastVisit, ideasNumLabel, tasksNumLabel, wNumLabel, attachsNumLabel);
    }
    
    private void settings() {
    	settings.setText(ThinkStr.settings.toString());
    	try {
    		FXMLLoader loader = new FXMLLoader();
    		loader.setLocation(Main.class.getResource("vue/Settings.fxml"));
    		Parent root = loader.load();
    		SettingsController controller = loader.getController();
			controller.setMenu(this);
			controller.disableEditing(!ThinkUtil.userConnected());
    		settings.setContent(root);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void addTaskNotif(Task task) {
    	BorderPane pane = getTaskNotif(task);
    	taskNoifsBox.getChildren().add(pane);
    }
    
    public void addUndoneIdea(Idea idea) {
    	BorderPane pane = getUndoneIdeaNotif(idea);
    	undoneIdeasBox.getChildren().add(pane);
    }
    
    private BorderPane getUndoneIdeaNotif(Idea idea) {
    	Label title = new Label(idea.getTitle());
    	title.setFont(Font.font(13));
    	title.setPadding(new Insets(10));
    	ProgressBar progress = new ProgressBar(idea.getWorkDegree());
    	ProgressIndicator progressInd = new ProgressIndicator(idea.getWorkDegree());
    	progress.setPrefSize(2000, 10);
    	progressInd.setPrefSize(50, 50);
    	if (progressInd.isIndeterminate())
    		progressInd = null;
    	else
    		progressInd.setPadding(new Insets(10,0,0,0));
    	BorderPane pane = new BorderPane(title, null, null, progress, progressInd);
		pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() > 1)
					IdeaInfoController.openIdeaInfo(idea, getStage());
			}
		});
		pane.prefWidth(10000);
    	return pane;
    }
    private BorderPane getTaskNotif(Task task) {
    	Label title = new Label(task.getTitle());
    	title.setFont(Font.font(13));
    	title.setPadding(new Insets(10));
    	ProgressBar progress = new ProgressBar(task.getWorkDegree());
    	progress.setPrefSize(2000, 10);
    	Label close = new Label("X");
    	close.setFont(Font.font(7));
    	BorderPane.setAlignment(close, Pos.TOP_RIGHT);
    	Label taskPeriodLabel = new Label(task.getTaskPeriod().getThinkStr().toString());
    	taskPeriodLabel.setFont(Font.font(9));
    	BorderPane.setAlignment(taskPeriodLabel, Pos.CENTER);
    	BorderPane pane  = new BorderPane(title, null, close, progress, taskPeriodLabel);
    	if (close != null) {
    		close.setOnMouseClicked(new EventHandler<MouseEvent>() {
    			@Override
    			public void handle(MouseEvent event) {
    				taskNoifsBox.getChildren().remove(pane);
    				Notification.removeNotification(task);
    				ThinkUtil.addIdeaToModify(task);
    			}
    		});
    	}
    	pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() > 1)
					IdeaInfoController.openIdeaInfo(task, getStage());
			}
		});
    	pane.prefWidth(10000);
    	return pane;
    }
    
	/**Upload/Download**/
	
	public void loads() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("vue/LoadsManager.fxml"));
			Parent root;
			root = loader.load();
			LoadsManagerController controller = loader.getController();
			ThinkUtil.setLoadController(controller);
			Stage loadStage = new Stage();
			loadStage.setScene(new Scene(root));
			loadStage.initOwner(getStage());
			loadStage.initModality(Modality.APPLICATION_MODAL);
			loadStage.setResizable(false);
			loadStage.getIcons().add(new Image(Main.class.getResourceAsStream(Controller.icon_path)));
			loadStage.showAndWait();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    public void newIdea() {
		try {
	    	FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("vue/IdeaCreator.fxml"));
			Parent root;
			root = loader.load();
			getStage().setScene(new Scene(root));
			getStage().setResizable(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void newWN() {
		try {
	    	FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("vue/WorkNoteCreator.fxml"));
			Parent root;
			root = loader.load();
			WorkNoteCreatorController controller = loader.getController();
			controller.setWorkNote(null);
			getStage().setScene(new Scene(root));
			getStage().setResizable(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**Search: filter and reset**/
    public void filter() {
    	filterBool = true;
    	ideasT.setItems(FXCollections.observableArrayList(ThinkUtil.filterIdeas(ideaCompleter.getText(), datePicker.getValue(),
    			task.isSelected(), done.getValue().toBoolean(), yearBox.getValue(), spinner.getValue())));
    	ideasT.refresh();
    }
    
    public void reset() {
    	filterBool = false;
    	ideaCompleter.setText("");
    	datePicker.setValue(null);
    	task.setSelected(false);
    	done.setValue(Done.indeterminate);
    	yearBox.setValue(null);
		ideasT.setItems(FXCollections.observableArrayList(ThinkUtil.requestIdeasList(spinner.getValue())));
		ideasT.refresh();
    }
    
    public void disableEditing(Boolean disable) {
    	menu.setDisable(disable);
    	calendar.setDisable(disable);
    	ideasTable.setDisable(disable);
    	sideBar.setDisable(disable);
    	if (disable)
    		tabPane.getSelectionModel().select(settings);
    }
    
    /**ButtonCell of remove**/
    
	private class ButtonCell extends TableCell<Idea, Boolean> {
		Button cellButton = new Button();

		ButtonCell(TableView<Idea> tblView, String text) {
			cellButton.setText(text);
			cellButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent t) {
					Idea idea = ideasT.getItems().get(getIndex());
					ThinkUtil.addIdeaToRemove(idea);
					ideasT.getItems().remove(idea);
					tblView.refresh();
					calendar();
					initIdeasTable();
					initNotifs();
					settings();
				}
			});
		}

		@Override
		protected void updateItem(Boolean t, boolean empty) {
			//super.updateItem(t, empty);
			if (!empty) {
				setGraphic(cellButton);
			}
		}
	}
	
	public void about() {
	       String msg =
	                "Features: \n- date events \n- filters \n- tasks and ideas \n- workNotes" +
	                "\n- PDF/IDEA format save/load \n- notifications" +
	                "\n- attachments (Upload/Download) \n- secure login (privacy)" +
	                "\n- calendar \n- flat design" +
	                "\n- configurable settings \n- database" +
	                "\n\nAuthors: " + "\nBenhalima Ahmed Youcef" ;

	        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
	        alert.setHeaderText(ThinkStr.thinkBAY.toString());
	        alert.setTitle(ThinkStr.about_us.toString());
	        alert.show();
	}
	
	public void importIdeas() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(ThinkUtil.getDirectory());
		directoryChooser.setTitle(ThinkStr.ideas_import.toString());
		File file = directoryChooser.showDialog(ideasT.getScene().getWindow());
		if (file != null) {
			ThinkUtil.importIdeas(file);
			refresh();
		}
	}
	
	public void exportCurrentTable() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(ThinkUtil.getDirectory());
		directoryChooser.setTitle(ThinkStr.table_export.toString());
		File file = directoryChooser.showDialog(ideasT.getScene().getWindow());
		if (file != null) {
			ThinkUtil.exportIdeas(ideasT.getItems(), file.getAbsolutePath());
		}
	}
	
	public void refresh() {
		ThinkUtil.refreshDB();
		calendar();
		initIdeasTable();
		initNotifs();
		settings();
		getStage().setTitle(ThinkStr.thinkBAY.toString());
		getStage().getIcons().add(new Image(Main.class.getResourceAsStream(Controller.icon_path)));
	}
	
	public void focus() {
		if (focus.isSelected()) {
			Controller.setStageFullSecreen(true);
		}
		else {
			Controller.setStageFullSecreen(false);
		}
	}
	
	public void disconnect() {
		ThinkUtil.disconnectAccount();
		initialize();
	}
	
	public void close() {
		Platform.exit();
		if (Controller.tray != null && Controller.trayIcon != null)
			Controller.tray.remove(Controller.trayIcon);
	}
	
	public static enum Done {
		
		indeterminate(null, ThinkStr.done_undone),
		done(true, ThinkStr.done),
		undone(false, ThinkStr.undone);
		
		private Boolean bool;
		private ThinkStr str;
		
		private Done(Boolean bool, ThinkStr str) {
			this.bool = bool;
			this.str = str;
		}
		
		public Boolean toBoolean() {
			return bool;
		}
		
		public String toString() {
			return str.toString();
		}
	}
}