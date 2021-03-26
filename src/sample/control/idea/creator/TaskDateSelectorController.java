package sample.control.idea.creator;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import sample.Main;
import sample.control.Controller;
import sample.kernel.entity.Task;
import sample.kernel.exception.DateException;
import sample.kernel.util.Notification;
import sample.kernel.util.TaskPeriod;
import sample.kernel.util.ThinkStr;
import sample.kernel.util.ThinkUtil;

public class TaskDateSelectorController extends Controller {
	@FXML
	private Text taskTitle;
	@FXML
	private Text startDateText;
	@FXML
	private DatePicker startDate;
	@FXML
	private TextField hourStart;
	@FXML
	private TextField minuteStart;
	@FXML
	private Text finishDateText;
	@FXML
	private DatePicker finishDate;
	@FXML
	private TextField hourFinish;
	@FXML
	private TextField minuteFinish;
	@FXML
	private Button submit;
	@FXML
	private HBox dateFinishBox;
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
	
	private Task task;
	private Map<File, Boolean> attachments;
	private TaskPeriod period;
	
	private final int maxValueHour = 23;
	private final int maxValueMinute = 59;
	
	@FXML @Override
	public void initialize() {
		taskTitle.setText(ThinkStr.task_configure.toString());
		startDateText.setText(ThinkStr.startDate.toString());
		finishDateText.setText(ThinkStr.finishDate.toString());
		startDate.setValue(LocalDate.now());
		finishDate.setValue(LocalDate.now());
		hourStart.setPromptText(ThinkStr.hour.toString());
		minuteStart.setPromptText(ThinkStr.minute.toString());
		hourFinish.setPromptText(ThinkStr.hour.toString());
		minuteFinish.setPromptText(ThinkStr.minute.toString());
		submit.setText(ThinkStr.submit.toString());
		addListnerTime(hourStart, maxValueHour);
		addListnerTime(minuteStart, maxValueMinute);
		addListnerTime(hourFinish, maxValueHour);
		addListnerTime(minuteFinish, maxValueMinute);
		once.setText(ThinkStr.once.toString());
		daily.setText(ThinkStr.daily.toString());
		weekly.setText(ThinkStr.weekly.toString());
		monthly.setText(ThinkStr.monthly.toString());
		yearly.setText(ThinkStr.yearly.toString());
		once.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					finishDate.setValue(task.getFinishTime().toLocalDate());
					minuteFinish.setText(String.valueOf(task.getFinishTime().getMinute()));
					hourFinish.setText(String.valueOf(task.getFinishTime().getHour()));
				}
				else {
					finishDate.setValue(ThinkUtil.MAX_DATETIME);
					minuteFinish.setText(Integer.toString(maxValueMinute));
					hourFinish.setText(Integer.toString(maxValueHour));
				}
			}
		});
		
		submit.disableProperty().bind(Bindings.or(Bindings.or(Bindings.or(
				Bindings.isEmpty(minuteFinish.textProperty()),
				Bindings.isEmpty(hourFinish.textProperty())), 
				Bindings.isEmpty(minuteStart.textProperty())), 
				Bindings.isEmpty(hourStart.textProperty())
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
	
	public void submit() {
		if (once.isSelected()) period = TaskPeriod.ONCE;
		else if (daily.isSelected()) period = TaskPeriod.DAILY;
		else if (weekly.isSelected()) period = TaskPeriod.WEEKLY;
		else if (monthly.isSelected()) period = TaskPeriod.MONTHLY;
		else period = TaskPeriod.YEARLY;
		LocalDateTime startTime = LocalDateTime.of(startDate.getValue(), 
				LocalTime.of(Integer.parseInt(hourStart.getText()), Integer.parseInt(minuteStart.getText())));
		LocalDateTime finishTime = LocalDateTime.of(finishDate.getValue(), 
				LocalTime.of(Integer.parseInt(hourFinish.getText()), Integer.parseInt(minuteFinish.getText())));
		try {
			task.setTimesAttachsConstructor(startTime, period, finishTime, attachments);
			ThinkUtil.addIdeaToSave(getTask());
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
		} catch (DateException e) {
			Notification.addNotification(e);
		}
	}
	
	public Task getTask() {
		return task;
	}

	public Map<File, Boolean> getAttachments() {
		return attachments;
	}
	
	public void setTaskAttachs(Task task, Map<File, Boolean> attachments) {
		this.task = task;
		this.attachments = attachments;
	}
	
}
