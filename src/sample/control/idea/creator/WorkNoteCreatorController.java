package sample.control.idea.creator;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.Main;
import sample.control.Controller;
import sample.control.idea.IdeaInfoController;
import sample.kernel.entity.Idea;
import sample.kernel.entity.WorkNote;
import sample.kernel.exception.DateException;
import sample.kernel.util.Notification;
import sample.kernel.util.ThinkStr;
import sample.kernel.util.ThinkUtil;

public class WorkNoteCreatorController extends Controller {
	@FXML
	private Text wNCText;
	@FXML
	private TextField titleField;
	@FXML
	private TextArea descArea;
	@FXML
	private DatePicker datePicker;
	@FXML
	private TextField hourField;
	@FXML
	private TextField minuteField;
	@FXML
	private ToggleButton doneToggle;
	@FXML
	private ToggleButton dateToggle;
	@FXML
	private Button save;
	@FXML
	private TextField ideaCompleter;
	
	private Idea idea;
	
	private final int maxValueHour = 23;
	private final int maxValueMinute = 59;
	
	@FXML
	public void initialize() {
		save.setDisable(ideaCompleter.isEditable());
		wNCText.setText(ThinkStr.workNote_create.toString());
		titleField.setPromptText(ThinkStr.title.toString());
		descArea.setPromptText(ThinkStr.description.toString());
		save.setText(ThinkStr.save.toString());
		ideaCompleter.setPromptText(ThinkStr.related_ideas.toString());
		doneToggle.setText(ThinkStr.done.toString());
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
					if (idea != null) {
						ideaCompleter.setEditable(false);
						setIdea(idea);
					}
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
					datePicker.setDisable(false);
					hourField.setDisable(false);
					minuteField.setDisable(false);
				}
				else {
					datePicker.setDisable(true);
					hourField.setDisable(true);
					minuteField.setDisable(true);
				}
			}
		});
		save.disableProperty().bind(Bindings.or(Bindings.and(Bindings.or(
				Bindings.isEmpty(minuteField.textProperty()),
				Bindings.isEmpty(hourField.textProperty())),
				dateToggle.selectedProperty()),
				ideaCompleter.editableProperty()
			));
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
		LocalDateTime dateTime;
		if (dateToggle.isSelected()) {
			dateTime = LocalDateTime.of(datePicker.getValue(), 
					LocalTime.of(Integer.parseInt(hourField.getText()), Integer.parseInt(minuteField.getText())));
		}
		else dateTime = LocalDateTime.now();
		try {
			if (modify()) {
				workNote.setWorkNote(titleField.getText(), dateTime, descArea.getText(), doneToggle.isSelected());
				Stage stage = IdeaInfoController.getOpenedIdeas().get(getIdea());
	    		try {
	    			FXMLLoader loader = new FXMLLoader();
	    			loader.setLocation(Main.class.getResource("vue/IdeaInfo.fxml"));
	    			Parent root;
	    			root = loader.load();
	    			IdeaInfoController controller = loader.getController();
	    			controller.setIdea(idea);
	    			controller.selectWNTab();
	    			stage.setScene(new Scene(root));
	    			stage.setResizable(true);
	    			stage.show();
	    		} catch (IOException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
			}
			else {
				new WorkNote(titleField.getText(), dateTime, descArea.getText(), getIdea(), doneToggle.isSelected());
				Stage stage = IdeaInfoController.getOpenedIdeas().get(getIdea());
				if (stage == null) IdeaInfoController.openIdeaInfo(getIdea(), getStage());
				else {
		    		try {
		    			FXMLLoader loader = new FXMLLoader();
		    			loader.setLocation(Main.class.getResource("vue/IdeaInfo.fxml"));
		    			Parent root;
		    			root = loader.load();
		    			IdeaInfoController controller = loader.getController();
		    			controller.setIdea(idea);
		    			controller.selectWNTab();
		    			stage.setScene(new Scene(root));
		    			stage.setResizable(true);
		    			stage.show();
		    		} catch (IOException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
				}
			}
		} catch (DateException e) {
			Notification.addNotification(e);
		}
	}

	public Idea getIdea() {
		return idea;
	}

	public void setIdea(Idea idea) {
		if (idea != null) {
			ideaCompleter.setText(idea.toString());
			ideaCompleter.setEditable(false);
		}
		this.idea = idea;
	}
	
	private WorkNote workNote;
	
	public void setWorkNote(WorkNote workNote) {
		this.workNote = workNote;
		if (modify()) {
			titleField.setText(workNote.getTitle());
			doneToggle.setSelected(workNote.getDone());
			descArea.setText(workNote.getNote());
			datePicker.setValue(workNote.getDate().toLocalDate());
			hourField.setText(String.valueOf(workNote.getDate().getHour()));
			minuteField.setText(String.valueOf(workNote.getDate().getMinute()));
			dateToggle.setSelected(true);
			setIdea(workNote.getIdea());
		}
		else {
			ideaCompleter.setTooltip(new Tooltip(ThinkStr.enter_id_ok_remove.toString()));
			ideaCompleter.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if ((event.getClickCount() > 1) && (! ideaCompleter.getText().isEmpty())) {
						ideaCompleter.setText("");
						ideaCompleter.setEditable(true);
						setIdea(null);
					}
				}
			});
		}
	}
	
	private Boolean modify() {
		return (workNote != null);
	}
	
	public void back() {
		if (modify()) {
			Stage stage = IdeaInfoController.getOpenedIdeas().get(getIdea());
    		try {
    			FXMLLoader loader = new FXMLLoader();
    			loader.setLocation(Main.class.getResource("vue/IdeaInfo.fxml"));
    			Parent root;
    			root = loader.load();
    			IdeaInfoController controller = loader.getController();
    			controller.setIdea(idea);
    			controller.selectWNTab();
    			stage.setScene(new Scene(root));
    			stage.setResizable(true);
    			stage.show();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
		}
		else {
			if (getIdea() == null) {
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
			else {
				Stage stage = IdeaInfoController.getOpenedIdeas().get(getIdea());
				if (stage == null) IdeaInfoController.openIdeaInfo(getIdea(), getStage());
				else {
		    		try {
		    			FXMLLoader loader = new FXMLLoader();
		    			loader.setLocation(Main.class.getResource("vue/IdeaInfo.fxml"));
		    			Parent root;
		    			root = loader.load();
		    			IdeaInfoController controller = loader.getController();
		    			controller.setIdea(idea);
		    			controller.selectWNTab();
		    			stage.setScene(new Scene(root));
		    			stage.setResizable(true);
		    			stage.show();
		    		} catch (IOException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
				}
			}
		}
	}
}
