package sample.control.idea;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import sample.Main;
import sample.control.Controller;
import sample.control.idea.creator.WorkNoteCreatorController;
import sample.kernel.entity.Attachment;
import sample.kernel.entity.Idea;
import sample.kernel.entity.Task;
import sample.kernel.entity.WorkNote;
import sample.kernel.exception.DateException;
import sample.kernel.util.Notification;
import sample.kernel.util.TaskPeriod;
import sample.kernel.util.ThinkStr;
import sample.kernel.util.ThinkUtil;

public class IdeaInfoController extends Controller {
	/**Tabs**/
	@FXML
	private TabPane tabPane;
	@FXML
	private Tab general;
	@FXML
	private Tab editor;
	@FXML
	private Tab relatedIdeas;
	@FXML
	private Tab workNotes;
	@FXML
	private Tab attachs;
	@FXML
	private Tab taskConf;
	/**General**/
	@FXML
	private Label idLabel;
	@FXML
	private Label titleLabel;
	@FXML
	private Label dateLabel;
	@FXML
	private Label relIdeasLabel;
	@FXML
	private Label workNotesLabel;
	@FXML
	private Label attachLabel;
	@FXML
	private Label toUploadLabel;
	@FXML
	private ProgressIndicator progressIndicator;
	@FXML
	private Text idText;
	@FXML
	private Text titleText;
	@FXML
	private Text dateText;
	@FXML
	private Text relIdeasText;
	@FXML
	private Text workNotesText;
	@FXML
	private Text attachText;
	@FXML
	private Text toUploadText;
	@FXML
	private TextField titleField;
	@FXML
	private HBox dateTimeBox;
	@FXML
	private DatePicker datePicker;
	@FXML
	private TextField hourField;
	@FXML
	private TextField minuteField;
	@FXML
	private ColorPicker colorPicker;
	/**Menu**/
	@FXML
	private Menu ideaMenu;
	@FXML
	private Menu editMenu;
	@FXML
	private MenuItem back;
	@FXML
	private MenuItem save;
	@FXML
	private MenuItem refresh;
	@FXML
	private MenuItem importItem;
	@FXML
	private MenuItem exportItem;
	@FXML
	private MenuItem findReplace;
	@FXML
	private MenuItem orientItem;
	/**Editor**/
	@FXML
	private TextArea descArea;
	/**RelatedIdeas**/
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
    private TextField ideaCompleter;
    /**WorkNotes**/
	@FXML
	private TableView<WorkNote> wNT;
    @FXML
    private TableColumn<WorkNote, Integer> idWNC;
    @FXML
    private TableColumn<WorkNote, String> titleWNC;
    @FXML
    private TableColumn<WorkNote, String> dateWNC;
    @FXML
    private TableColumn<WorkNote, String> doneWNC;
    @FXML
    private TableColumn<WorkNote, Boolean> deleteWNC;
    @FXML
    private Button addWN;
    @FXML
    private ToggleButton notifyWN;
    /**Attachs**/
	@FXML
	private TableView<Attachment> attachT;
    @FXML
    private TableColumn<Attachment, Integer> idAttachC;
    @FXML
    private TableColumn<Attachment, String> localPathC;
    @FXML
    private TableColumn<Attachment, String> externPathC;
    @FXML
    private TableColumn<Attachment, Boolean> uploadAttachC;
    @FXML
    private TableColumn<Attachment, Boolean> deleteAttachC;
	@FXML
	private Button attachmentChooser;
	/**Task Config**/
	@FXML
	private Label startDateLabel;
	@FXML
	private Label finishDateLabel;
	@FXML
	private Text startDateText;
	@FXML
	private Text finishDateText;
	@FXML
	private DatePicker dateStartPicker;
	@FXML
	private DatePicker dateFinishPicker;
	@FXML
	private TextField hourStartField;
	@FXML
	private TextField minuteStartField;
	@FXML
	private TextField hourFinishField;
	@FXML
	private TextField minuteFinishField;
	@FXML
	private HBox dateStartBox;
	@FXML
	private HBox dateFinishBox;
	@FXML
	private BorderPane dateFinishPane;
	@FXML
	private ToggleButton notifyTask;
	@FXML
	private Label taskPeriodLabel;
	@FXML
	private RadioButton once;
	@FXML
	private RadioButton daily;
	@FXML
	private RadioButton weekly;
	@FXML
	private RadioButton monthly;
	@FXML
	private RadioButton yearly;
	
	private Idea idea;
	private final int maxValueHour = 23;
	private final int maxValueMinute = 59;
	TaskPeriod period;
	private static Map<Idea, Stage> openedIdeas = new HashMap<Idea, Stage>();
	
	@FXML
	public void initialize() {
		super.initialize();
	}
	
	private void general() {
		ideaMenu.setText(ThinkStr.ideaRelated.toString());
		editMenu.setText(ThinkStr.edit.toString());
		save.setText(ThinkStr.save.toString());
		refresh.setText(ThinkStr.refresh.toString());
		importItem.setText(ThinkStr.import_.toString());
		exportItem.setText(ThinkStr.export.toString());
		back.setText(ThinkStr.back.toString());
		general.setText(ThinkStr.general.toString());
		idLabel.setText(ThinkStr.id.toString());
		titleLabel.setText(ThinkStr.title.toString());
		dateLabel.setText(ThinkStr.date_and_time.toString());
		relIdeasLabel.setText(ThinkStr.related_ideas.toString());
		workNotesLabel.setText(ThinkStr.workNotes.toString());
		attachLabel.setText(ThinkStr.attachments.toString());
		toUploadLabel.setText(ThinkStr.toUploads.toString());
		hourField.setPromptText(ThinkStr.hour.toString());
		minuteField.setPromptText(ThinkStr.minute.toString());
		titleLabel.setTooltip(new Tooltip(ThinkStr.double_click_edit.toString()));
		dateLabel.setTooltip(new Tooltip(ThinkStr.double_click_edit.toString()));
		colorPicker.setValue(Color.web(getIdea().getColor()));
		idText.setText(String.valueOf(getIdea().getId()));
		titleField.setText(getIdea().getTitle());
		if (titleText.isVisible()) 
			titleText.setText(getIdea().getTitle());
		else 
			titleText.setText("");
		if (dateText.isVisible()) 
			dateText.setText(getIdea().getTime().format(DateTimeFormatter.ofPattern(ThinkUtil.datetimeFormatter)));
		else 
			dateText.setText("");
		relIdeasText.setText(String.valueOf(getIdea().getRelatedIdeas().size()));
		workNotesText.setText(String.valueOf(getIdea().getWorkNotes().size()));
		attachText.setText(String.valueOf(getIdea().getAttachments().size()));
		toUploadText.setText(String.valueOf(getIdea().getAttachsToUpload().size()));
		progressIndicator.setProgress(getIdea().getWorkDegree());
		datePicker.setValue(getIdea().getTime().toLocalDate());
		hourField.setText(String.valueOf(getIdea().getTime().getHour()));
		minuteField.setText(String.valueOf(getIdea().getTime().getMinute()));
		addListnerTime(hourField, maxValueHour);
		addListnerTime(minuteField, maxValueMinute);
		titleText.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() > 1) {
					titleText.setVisible(false);
					titleText.setText("");
					titleField.setVisible(true);
				}
			}
		});
		
		dateText.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() > 1) {
					dateText.setVisible(false);
					dateText.setText("");
					dateTimeBox.setVisible(true);
				}
			}
		});
	}
	
	private void editor() {
		editor.setText(ThinkStr.editor.toString());
		findReplace.setText(ThinkStr.replace_find.toString());
		orientItem.setText(ThinkStr.orientation.toString());
		descArea.setText(getIdea().getDesc());	
	}
	
	private void relatedIdeas() {
		relatedIdeas.setText(ThinkStr.related_ideas.toString());
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
		ideasT.setItems(FXCollections.observableArrayList(getIdea().getRelatedIdeas()));
		ideaCompleter.setPromptText(ThinkStr.related_ideas.toString());
		ideaCompleter.setTooltip(new Tooltip(ThinkStr.enter_id_ok.toString()));
		SuggestionProvider<Idea> provider = SuggestionProvider.create(new ArrayList<Idea>());
		new AutoCompletionTextFieldBinding<>(ideaCompleter, provider);
		ideaCompleter.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				provider.clearSuggestions();
				provider.addPossibleSuggestions(ThinkUtil.completerSearchIdea(newValue));
			}
		});
		ideaCompleter.setOnKeyTyped(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCharacter().equals("\r")) {
					Idea idea = ThinkUtil.searchIdea(ideaCompleter.getText());
					System.out.println(idea);
					if (idea != null) addRelatedIdeaT(idea);
					ideaCompleter.setText("");
					ideasT.refresh();
				}
			}
		});
		
		ideasT.setRowFactory(tv -> {
			TableRow<Idea> row = new TableRow<>() {
				@Override
				public void updateItem(Idea item, boolean empty) {
					super.updateItem(item, empty);
					if (item != null) {
						setStyle("");
						if (item.isDone() != null) {
							setTooltip(new Tooltip((item.getWorkDegree() * 100) + "%"));
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
					openIdeaInfo(rowData, new Stage());
				}
			});
			return row;
		});
	}
	
    public static void openIdeaInfo(Idea idea, Stage stage) {
    	if (ideaInfoIsOpened(idea))
    		getOpenedIdeas().get(idea).requestFocus();
    	else {
    		try {
    			FXMLLoader loader = new FXMLLoader();
    			loader.setLocation(Main.class.getResource("vue/IdeaInfo.fxml"));
    			Parent root;
    			root = loader.load();
    			IdeaInfoController controller = loader.getController();
    			controller.setIdea(idea);
    			IdeaInfoController.getOpenedIdeas().put(idea, stage);
    			stage.setScene(new Scene(root));
    			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
    				@Override
    				public void handle(WindowEvent event) {
    					getOpenedIdeas().remove(idea);
    				}
    			});
    			stage.setResizable(true);
    			stage.getIcons().add(new Image(Main.class.getResourceAsStream(Controller.icon_path)));
    			stage.setTitle(idea.toString());
    			stage.show();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    }
	
	private void workNotes() {
		workNotes.setText(ThinkStr.workNotes.toString());
		idWNC.setText(ThinkStr.id.toString());
		titleWNC.setText(ThinkStr.title.toString());
		dateWNC.setText(ThinkStr.date_and_time.toString());
		doneWNC.setText(ThinkStr.done.toString());
		deleteWNC.setText(ThinkStr.delete.toString());
		addWN.setText(ThinkStr.add.toString());
		notifyWN.setText(ThinkStr.notify.toString());
		notifyWN.setSelected(getIdea().getNotifyWN());
		
		idWNC.setCellValueFactory(cellData -> cellData.getValue().getIdProperty());
		titleWNC.setCellValueFactory(cellData -> cellData.getValue().getTitleProperty());
		dateWNC.setCellValueFactory(cellData -> cellData.getValue().getDateProperty());
		doneWNC.setCellValueFactory(cellData -> cellData.getValue().getDoneProperty());
		deleteWNC.setCellFactory(new Callback<TableColumn<WorkNote, Boolean>, TableCell<WorkNote, Boolean>>() {
			@Override
			public TableCell<WorkNote, Boolean> call(TableColumn<WorkNote, Boolean> column) {
				return new ButtonWNCell(wNT, "✘");
			}
		});
		wNT.setItems(FXCollections.observableArrayList(getIdea().getWorkNotes()));
		
		wNT.setRowFactory(tv -> {
			TableRow<WorkNote> row = new TableRow<>();

			row.setOnMouseClicked(event -> {
				if (event.getClickCount() > 1 && (!row.isEmpty())) {
					WorkNote rowData = row.getItem();
					try {
						FXMLLoader loader = new FXMLLoader();
						loader.setLocation(Main.class.getResource("vue/WorkNoteCreator.fxml"));
						Parent root = loader.load();
						WorkNoteCreatorController controller = loader.getController();
						controller.setWorkNote(rowData);
						Stage stage = getOpenedIdeas().get(getIdea());
						stage.setScene(new Scene(root));
						stage.setResizable(true);
						stage.getIcons().add(new Image(Main.class.getResourceAsStream(Controller.icon_path)));
						stage.show();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			return row;
		});
	}
	
	private void attachs() {
		attachs.setText(ThinkStr.attachments.toString());
		idAttachC.setText(ThinkStr.id.toString());
		localPathC.setText(ThinkStr.local_path.toString());
		externPathC.setText(ThinkStr.extern_path.toString());
		uploadAttachC.setText(ThinkStr.download_upload.toString());
		deleteAttachC.setText(ThinkStr.delete.toString());
		attachmentChooser.setText(ThinkStr.add.toString());
		
		idAttachC.setCellValueFactory(cellData -> cellData.getValue().getIdProperty());
		localPathC.setCellValueFactory(cellData -> cellData.getValue().getLocalURLProperty());
		externPathC.setCellValueFactory(cellData -> cellData.getValue().getExternURLProperty());
		uploadAttachC.setCellFactory(new Callback<TableColumn<Attachment, Boolean>, TableCell<Attachment, Boolean>>() {
			@Override
			public TableCell<Attachment, Boolean> call(TableColumn<Attachment, Boolean> column) {
				return new ButtonAttachCell(true);
			}
		});
		deleteAttachC.setCellFactory(new Callback<TableColumn<Attachment, Boolean>, TableCell<Attachment, Boolean>>() {
			@Override
			public TableCell<Attachment, Boolean> call(TableColumn<Attachment, Boolean> column) {
				return new ButtonAttachCell(false);
			}
		});
		attachT.setItems(FXCollections.observableArrayList(getIdea().getAttachments()));
		
		attachT.setRowFactory(tv -> {
			TableRow<Attachment> row = new TableRow<>();

			row.setOnMouseClicked(event -> {
				if (event.getClickCount() > 1 && (!row.isEmpty())) {
					Attachment rowData = row.getItem();
					File file = new File(rowData.getLocalURL());
					if (file.exists() && Desktop.isDesktopSupported()) {
						Desktop desktop = Desktop.getDesktop();
						try {
							desktop.open(file);
						} catch (IOException e) {
							Notification.addNotification(e);
						}
					}
					
				}
			});
			return row;
		});
	}
	
	public void taskConf() {
		taskConf.setText(ThinkStr.task_configure.toString());		
		taskConf.setDisable(true);
		if (getIdea() instanceof Task) {
			taskConf.setDisable(false);
			Task task = (Task) idea;
			startDateLabel.setText(ThinkStr.startDate.toString());
			finishDateLabel.setText(ThinkStr.finishDate.toString());
			hourStartField.setPromptText(ThinkStr.hour.toString());
			minuteStartField.setPromptText(ThinkStr.minute.toString());
			hourFinishField.setPromptText(ThinkStr.hour.toString());
			minuteFinishField.setPromptText(ThinkStr.minute.toString());
			startDateLabel.setTooltip(new Tooltip(ThinkStr.double_click_edit.toString()));
			finishDateLabel.setTooltip(new Tooltip(ThinkStr.double_click_edit.toString()));
			notifyTask.setText(ThinkStr.notify.toString());
			taskPeriodLabel.setText(ThinkStr.period.toString());
			once.setText(ThinkStr.once.toString());
			daily.setText(ThinkStr.daily.toString());
			weekly.setText(ThinkStr.weekly.toString());
			monthly.setText(ThinkStr.monthly.toString());
			yearly.setText(ThinkStr.yearly.toString());
			notifyTask.setSelected(task.getNotifyTask());
			switch (task.getTaskPeriod()) {
				case DAILY:
					daily.setSelected(true);
					break;
				case MONTHLY:
					monthly.setSelected(true);
					break;
				case ONCE:
					once.setSelected(true);
					break;
				case WEEKLY:
					weekly.setSelected(true);
					break;
				case YEARLY:
					yearly.setSelected(true);
					break;
				default:
					break;
			}
			once.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if (newValue) {
						dateFinishPicker.setValue(task.getFinishTime().toLocalDate());
						minuteFinishField.setText(String.valueOf(task.getFinishTime().getMinute()));
						hourFinishField.setText(String.valueOf(task.getFinishTime().getHour()));
						if (finishDateText.getText() != "")
							finishDateText.setText(task.getFinishTime().format(DateTimeFormatter.ofPattern(ThinkUtil.datetimeFormatter)));
					}
					else {
						dateFinishPicker.setValue(ThinkUtil.MAX_DATETIME);
						minuteFinishField.setText(Integer.toString(maxValueMinute));
						hourFinishField.setText(Integer.toString(maxValueHour));
						if (finishDateText.getText() != "")
							finishDateText.setText("∞");
					}
				}
			});
			dateStartPicker.setValue(task.getStartTime().toLocalDate());
			dateFinishPicker.setValue(task.getFinishTime().toLocalDate());
			hourStartField.setText(String.valueOf(task.getStartTime().getHour()));
			minuteStartField.setText(String.valueOf(task.getStartTime().getMinute()));
			hourFinishField.setText(String.valueOf(task.getFinishTime().getHour()));
			minuteFinishField.setText(String.valueOf(task.getFinishTime().getMinute()));
			addListnerTime(hourField, maxValueHour);
			addListnerTime(minuteField, maxValueMinute);
			if (!dateStartBox.isVisible())
				startDateText.setText(task.getStartTime().format(DateTimeFormatter.ofPattern(ThinkUtil.datetimeFormatter)));
			else
				startDateText.setText("");
			if (dateFinishBox.isVisible()) 
				finishDateText.setText("");
			else {
				if (task.getFinishTime().toLocalDate().equals(ThinkUtil.MAX_DATETIME))
					finishDateText.setText("∞");
				else
					finishDateText.setText(task.getFinishTime().format(DateTimeFormatter.ofPattern(ThinkUtil.datetimeFormatter)));
			}
			startDateText.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (event.getClickCount() > 1) {
						startDateText.setVisible(false);
						startDateText.setText("");
						dateStartBox.setVisible(true);
					}
				}
			});
			finishDateText.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (event.getClickCount() > 1) {
						finishDateText.setVisible(false);
						finishDateText.setText("");
						dateFinishBox.setVisible(true);
					}
				}
			});
		}
	}
	
	/**
	 * Correct the input of the text field of hours and minutes
	 * @param field
	 * @param maxValue 23 for hours and 59 for minutes
	 */
	private static void addListnerTime(TextField field, int maxValue) {
		field.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d{0,9}?") || Integer.parseInt((newValue.isBlank() ? "0" : newValue)) > maxValue) {
					field.setText(oldValue);
				}
			}
		});
	}
	
	public void selectWNTab() {
		tabPane.getSelectionModel().select(workNotes);
	}
	
	public void selectTaskTab() {
		tabPane.getSelectionModel().select(taskConf);
	}

	public Idea getIdea() {
		return idea;
	}

	public void setIdea(Idea idea) {
		this.idea = idea;
		reset();
	}
	
	private void reset() {
		general();
		editor();
		relatedIdeas();
		workNotes();
		attachs();
		taskConf();
	}
	
	/**Add Remove Buttons**/
	
	private void removeWNT(WorkNote wN) {
		wNT.getItems().remove(wN);
		getIdea().removeWorkNote(wN);
		general();
	}
	
	private void removeAttachT(Attachment attach) {
		attachT.getItems().remove(attach);
		getIdea().removeAttachment(attach);
		general();
	}
	
	public void addAttach() {
		 FileChooser fileChooser = new FileChooser();
		 fileChooser.setTitle(ThinkStr.browser_attachments.toString());
		 fileChooser.getExtensionFilters().addAll(new ExtensionFilter(ThinkStr.all_files.toString(), "*"));
		 List<File> selectedFiles = fileChooser.showOpenMultipleDialog(attachT.getScene().getWindow());
		 if (selectedFiles != null) {
			 for (File selectedFile : selectedFiles) {
				 Attachment attach = new Attachment(selectedFile.getAbsolutePath(), false, getIdea());
				 getIdea().addAttachment(attach);
				 attachT.getItems().add(attach);
			 }
			 attachT.refresh();
			 general();
		 }
	}
	
	public void addWN() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("vue/WorkNoteCreator.fxml"));
			Parent root = loader.load();
			WorkNoteCreatorController controller = loader.getController();
			controller.setIdea(idea);
			getStage().setScene(new Scene(root));
			getStage().setResizable(true);
			getStage().show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void addRelatedIdeaT(Idea idea) {
		if (! idea.equals(getIdea())) {
			if (! ideasT.getItems().contains(idea)) ideasT.getItems().add(idea);
			getIdea().addRelatedIdea(idea);
			general();
		}
	}
	
	private void removeRelatedIdeaT(Idea idea) {
		ideasT.getItems().remove(idea);
		getIdea().removeRelatedIdea(idea);
		general();
	}
	
	/**Button Cells**/
	
	// For delete and download
	private class ButtonAttachCell extends TableCell<Attachment, Boolean> {
		HBox loadBox;
		Button cellUpButton;
		Button cellDownButton;
		Button cellDeleteButton;
		Boolean downupload;

		ButtonAttachCell(Boolean downupload) {
			this.downupload = downupload;
		}

		@Override
		protected void updateItem(Boolean t, boolean empty) {
			if (!empty) {
				Attachment attach = attachT.getItems().get(getIndex());
				if (downupload) {
					loadBox = new HBox();
					File file = new File(attach.getLocalURL());
					if (file.exists()) {
						cellUpButton = new Button("▲");
						cellUpButton.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								attach.toUpload();
								general();
								attachT.refresh();
							}
						});
						loadBox.getChildren().add(cellUpButton); // upgrade or new upload
					}
					if (attach.isToUpload()) {
						cellDownButton = new Button("▼");
						cellDownButton.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								attach.toDownload();
								general();
								attachT.refresh();
							}
						});
						loadBox.getChildren().add(cellDownButton);
					}
					setGraphic(loadBox);
				}
				else {
					cellDeleteButton = new Button("✘");
					cellDeleteButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent t) {
							removeAttachT(attach);
							general();
							attachT.refresh();
						}
					});
					setGraphic(cellDeleteButton);
				}
			}
		}
	}
	
	private class ButtonWNCell extends TableCell<WorkNote, Boolean> {
		Button cellButton = new Button();

		ButtonWNCell(TableView<WorkNote> tblView, String text) {
			cellButton.setText(text);
			cellButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent t) {
					removeWNT(tblView.getItems().get(getIndex()));
					tblView.refresh();
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
	
	private class ButtonCell extends TableCell<Idea, Boolean> {
		Button cellButton = new Button();

		ButtonCell(TableView<Idea> tblView, String text) {
			cellButton.setText(text);
			cellButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent t) {
					removeRelatedIdeaT(tblView.getItems().get(getIndex()));
					tblView.refresh();
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
	
	/**Menu Items**/
	
	private Stage findStage = new Stage();
	
	public void findReplace() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("vue/FindReplaceBox.fxml"));
			Parent root;
			root = loader.load();
			FindReplaceController controller = loader.getController();
			controller.setArea(descArea);
			findStage.setAlwaysOnTop(true);
			findStage.setTitle(ThinkStr.replace_find.toString() + " " + getIdea());
			findStage.setScene(new Scene(root));
			findStage.setResizable(false);
			findStage.getIcons().add(new Image(Main.class.getResourceAsStream(Controller.icon_path)));
			findStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void changeOrientation() {
		switch (descArea.getNodeOrientation()) {
		case LEFT_TO_RIGHT:
			descArea.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
			break;
		case RIGHT_TO_LEFT:
			descArea.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
			break;
		case INHERIT:
			switch (getPane().getNodeOrientation()) {
				case LEFT_TO_RIGHT:
					descArea.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
					break;
				case RIGHT_TO_LEFT:
					descArea.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
					break;
				case INHERIT:
					descArea.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
					break;
			}
			break;
		}
	}
	
	public void save() {
		if (once.isSelected()) period = TaskPeriod.ONCE;
		else if (daily.isSelected()) period = TaskPeriod.DAILY;
		else if (weekly.isSelected()) period = TaskPeriod.WEEKLY;
		else if (monthly.isSelected()) period = TaskPeriod.MONTHLY;
		else period = TaskPeriod.YEARLY;
		LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue(), 
				LocalTime.of(Integer.parseInt(hourField.getText()), Integer.parseInt(minuteField.getText())));
		if (getIdea() instanceof Task) {
			LocalDateTime startTime = LocalDateTime.of(dateStartPicker.getValue(), 
					LocalTime.of(Integer.parseInt(hourStartField.getText()), Integer.parseInt(minuteStartField.getText())));
			LocalDateTime finishTime = LocalDateTime.of(dateFinishPicker.getValue(), 
					LocalTime.of(Integer.parseInt(hourFinishField.getText()), Integer.parseInt(minuteFinishField.getText())));
			Task task = (Task) getIdea();
			try {
				task.set(titleField.getText(), descArea.getText(), dateTime, startTime, period, finishTime);
				task.setColor(ThinkUtil.toHexString(colorPicker.getValue()));
				if (notifyTask.isSelected())
					Notification.addNotification(task);
				else 
					Notification.removeNotification(task);
				if (notifyWN.isSelected())
					Notification.addNotificationWN(getIdea());
				else
					Notification.removeNotificationWN(getIdea());
				ThinkUtil.addIdeaToModify(task);
			} catch (DateException e) {
				Notification.addNotification(e);
			}
		}
		else {
			getIdea().set(titleField.getText(), descArea.getText(), dateTime);
			getIdea().setColor(ThinkUtil.toHexString(colorPicker.getValue()));
			if (notifyWN.isSelected())
				Notification.addNotificationWN(getIdea());
			else
				Notification.removeNotificationWN(getIdea());
			ThinkUtil.addIdeaToModify(getIdea());
		}
		reset();
	}
    
	public MenuItem getBackItem() {
		return back;
	}
	
	public void back() {
		try {
			for (Idea idea : getOpenedIdeas().keySet())
				getOpenedIdeas().get(idea).close();
			getOpenedIdeas().clear();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("vue/ThinkMenu.fxml"));
			Parent root;
			root = loader.load();
			getStage().setScene(new Scene(root));
			getStage().setResizable(true);
			getStage().show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void refresh() {
		ThinkUtil.refreshIdea(getIdea());
		reset();
	}
	
	public void importIdea() {
		 FileChooser fileChooser = new FileChooser();
		 fileChooser.setInitialDirectory(ThinkUtil.getDirectory());
		 fileChooser.setTitle(ThinkStr.import_.toString());
		 fileChooser.setInitialFileName(getIdea().toString() + "." + ThinkUtil.idea_extension);
		 fileChooser.getExtensionFilters().addAll(new ExtensionFilter(ThinkStr.idea.toString(),"*." + ThinkUtil.idea_extension));
		 File selectedFile = fileChooser.showOpenDialog(attachT.getScene().getWindow());
		 if (selectedFile != null) {
			 getIdea().importIdea(selectedFile);
			 reset();
		 }
	}
	
	public void exportIdea() {
		 FileChooser fileChooser = new FileChooser();
		 fileChooser.setInitialDirectory(ThinkUtil.getDirectory());
		 fileChooser.setTitle(ThinkStr.export.toString());
		 fileChooser.getExtensionFilters().addAll(new ExtensionFilter(ThinkStr.idea.toString(),"*." + ThinkUtil.idea_extension), new ExtensionFilter("DOCX","*." + "docx"));
		 fileChooser.setInitialFileName(getIdea().toString());
		 File selectedFile = fileChooser.showSaveDialog(attachT.getScene().getWindow());
		 if (selectedFile != null) {
			 if (getExtention(selectedFile).equals(ThinkUtil.idea_extension))
				 getIdea().exportIdea(selectedFile);
			 else
				 if (getExtention(selectedFile).equalsIgnoreCase("docx")) 
					 getIdea().exportDoc(selectedFile);
		 }
	}

	private String getExtention(File file) {
		String[] fileStrs = (file.getName().split("\\."));
		if (fileStrs.length <= 1) return "";
		return fileStrs[(fileStrs.length - 1)];
	}
	
	public static Map<Idea, Stage> getOpenedIdeas() {
		return openedIdeas;
	}

	public static void setOpenedIdeas(Map<Idea, Stage> openedIdeas) {
		IdeaInfoController.openedIdeas = openedIdeas;
	}
	
	public static Boolean ideaInfoIsOpened(Idea idea) {
		return openedIdeas.containsKey(idea);
	}
	
	public static void closeAllOpenedIdeas() {
		for (Idea idea : getOpenedIdeas().keySet())
			getOpenedIdeas().get(idea).close();
	}
	
}
