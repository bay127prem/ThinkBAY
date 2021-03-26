package sample.control.idea.creator;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import sample.Main;
import sample.control.Controller;
import sample.control.load.AttachmentLoaderController;
import sample.kernel.entity.Idea;
import sample.kernel.entity.Task;
import sample.kernel.util.ThinkStr;
import sample.kernel.util.ThinkUtil;

public class IdeaCreatorController extends Controller {
	@FXML
	private Text ideaCText;
	@FXML
	private TextField titleField;
	@FXML
	private TextArea descArea;
	@FXML
	private Button attachmentsChooser;
	@FXML
	private Button save;
	@FXML
	private TextField ideaCompleter;
	@FXML
	private ListView<Idea> ideasBox;
	@FXML
	private ToggleButton dateToggle;
	@FXML
	private ToggleButton taskToggle;
	@FXML
	private DatePicker datePicker;
	@FXML
	private TextField hourField;
	@FXML
	private TextField minuteField;
	@FXML
	private HBox attachmentsBox;
		
	private final int maxValueHour = 23;
	private final int maxValueMinute = 59;
	private final String attachAddSymbol = "+";

	private List<AttachmentLoaderController> attachLoaders = new ArrayList<AttachmentLoaderController>();
		
	@FXML
	public void initialize() {
		ideaCText.setText(ThinkStr.idea_create.toString());
		titleField.setPromptText(ThinkStr.title.toString());
		descArea.setPromptText(ThinkStr.description.toString());
		attachmentsChooser.setText(attachAddSymbol);
		save.setText(ThinkStr.save.toString());
		ideasBox.setTooltip(new Tooltip(ThinkStr.double_click_remove.toString()));
		ideaCompleter.setPromptText(ThinkStr.related_ideas.toString());
		ideaCompleter.setTooltip(new Tooltip(ThinkStr.enter_id_ok.toString()));
		taskToggle.setText(ThinkStr.task.toString());
		taskToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) save.setText(ThinkStr.continuee.toString());
				else save.setText(ThinkStr.save.toString());
			}
		});
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
					if (idea != null && !ideasBox.getItems().contains(idea)) ideasBox.getItems().add(idea);
					ideaCompleter.setText("");
					ideasBox.scrollTo(idea);
				}
			}
		});
		ideasBox.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if ((event.getClickCount() > 1) && (! ideasBox.getItems().isEmpty())) {
					int index = ideasBox.getFocusModel().getFocusedIndex();
					if (index >= 0) ideasBox.getItems().remove(index);
				}
			}
		});
		dateToggle.setText(ThinkStr.date_and_time.toString());
		dateToggle.setSelected(false);
		datePicker.setDisable(true);
		datePicker.setValue(LocalDate.now());
		hourField.setDisable(true);
		hourField.setPromptText(ThinkStr.hour.toString());
		minuteField.setDisable(true);
		minuteField.setPromptText(ThinkStr.minute.toString());
		addListnerTime(hourField, maxValueHour);
		addListnerTime(minuteField, maxValueMinute);
		dateToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					save.disableProperty().bind(Bindings.or(
							Bindings.isEmpty(minuteField.textProperty()),
							Bindings.isEmpty(hourField.textProperty()))
						);
					datePicker.setDisable(false);
					hourField.setDisable(false);
					minuteField.setDisable(false);
				}
				else {
					save.disableProperty().unbind();
					save.setDisable(false);
					datePicker.setDisable(true);
					hourField.setDisable(true);
					minuteField.setDisable(true);
				}
			}
		});
		super.initialize();
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
	
	public void save() {
		Map<File, Boolean> attachments = new HashMap<File, Boolean>();
		for (AttachmentLoaderController attachLoder : attachLoaders) 
			attachments.put(attachLoder.getAttachmentEntry().getKey(),
					attachLoder.getAttachmentEntry().getValue());
		LocalDateTime dateTime;
		List<Idea> relatedIdeas = new ArrayList<Idea>();
		if (dateToggle.isSelected()) {
			dateTime = LocalDateTime.of(datePicker.getValue(), 
					LocalTime.of(Integer.parseInt(hourField.getText()), Integer.parseInt(minuteField.getText())));
		}
		else dateTime = LocalDateTime.now();
		for (Idea idea : ideasBox.getItems()) relatedIdeas.add(idea);	
		if (taskToggle.isSelected()) {
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(Main.class.getResource("vue/TaskDateSelector.fxml"));
				Parent root = loader.load();
				Stage stage = Controller.getStage();
				TaskDateSelectorController controller = loader.getController();
				Task task = new Task(titleField.getText(), descArea.getText(), dateTime, relatedIdeas, attachments);
				controller.setTaskAttachs(task, attachments);
				stage.getScene().setRoot(root);
				stage.setResizable(true);
				stage.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			Idea idea = new Idea(titleField.getText(), descArea.getText(), dateTime, relatedIdeas, attachments);
			ThinkUtil.addIdeaToSave(idea);
			try {
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
	}
	
	public void attachmentChoose() {
		 FileChooser fileChooser = new FileChooser();
		 fileChooser.setTitle(ThinkStr.browser_attachments.toString());
		 fileChooser.setInitialDirectory(ThinkUtil.getDirectory());
		 fileChooser.getExtensionFilters().addAll(new ExtensionFilter(ThinkStr.all_files.toString(), "*"));
		 List<File> selectedFiles = fileChooser.showOpenMultipleDialog(ideaCText.getScene().getWindow());
		 if (selectedFiles != null) 
			 for (File file : selectedFiles) addFilePane(file);
		 setAttachChooserEnd();
	}
	
	private void addFilePane(File file) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("vue/AttachmentLoader.fxml"));
			BorderPane pane = loader.load();
			AttachmentLoaderController controller = loader.getController();
			controller.setPane(pane);
			controller.setFile(file);
			controller.setController(this);
			attachLoaders.add(controller);
			attachmentsBox.getChildren().add(pane);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void removeAttachLoader(AttachmentLoaderController attachLoader) {
		attachLoaders.remove(attachLoader);
	}
	
	@SuppressWarnings("unused")
	private void clearAttachments() {
		 attachmentsBox.getChildren().clear();
		 attachLoaders.clear();
	}
	
	private void setAttachChooserEnd() {
		attachmentsBox.getChildren().remove(attachmentsChooser);
		attachmentsBox.getChildren().add(attachmentsChooser);
	}
	
	public void back() {
		try {
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
}
