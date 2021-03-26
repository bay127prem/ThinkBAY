package sample.control.menu.calendar;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.PopOver;

import com.sun.javafx.scene.control.DatePickerContent;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.util.Callback;
import sample.Main;
import sample.control.Controller;
import sample.kernel.entity.Task;
import sample.kernel.entity.WorkNote;
import sample.kernel.util.ThinkStr;
import sample.kernel.util.ThinkUtil;

public class CalendarController extends Controller {

	private DatePicker datePicker = new DatePicker(LocalDate.now());
	private DatePickerContent datePickerContent = new DatePickerContent(datePicker);
	private List<Object> events = ThinkUtil.requestEvents(getStartDateOfMonth(), getEndDateOfMonth());
	
	@FXML
	public void initialize() {
		datePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
			@Override
			public DateCell call(DatePicker param) {
				DateCellExt dateCell = new DateCellExt();
				dateCell.setOnMouseClicked(event -> {
					if (event.getClickCount() > 1) {
						try {
							FXMLLoader loader = new FXMLLoader();
							loader.setLocation(Main.class.getResource("vue/EventsTable.fxml"));
							Parent root;
							root = loader.load();
							EventsTableController controller = loader.getController();
							controller.setEvents(dateCell.getEvents());
							PopOver eventsPopOver = new PopOver(root);
							eventsPopOver.setHeaderAlwaysVisible(true);
							eventsPopOver.setAutoFix(true);
							eventsPopOver.setAutoHide(true);
							eventsPopOver.setHideOnEscape(true);
							eventsPopOver.setDetachable(true);
							eventsPopOver.setTitle(ThinkStr.events + " (" + dateCell.getItem().format(DateTimeFormatter.ofPattern(ThinkUtil.dateFormatter)) + ")");
							eventsPopOver.show(dateCell);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
		        });
				dateCell.setPrefHeight(300);
				return dateCell;
			}
		});
		Node popupContent = new DatePickerSkin(datePicker).getPopupContent();
		getPane().setCenter(popupContent);
		((DatePickerContent) popupContent).displayedYearMonthProperty().addListener(new ChangeListener<YearMonth>() {
			@Override
			public void changed(ObservableValue<? extends YearMonth> observable, YearMonth oldValue,
					YearMonth newValue) {
				events = ThinkUtil.requestEvents(getStartDateOfMonth(), getEndDateOfMonth());
				datePickerContent.updateValues();
			}
		});
	}
	
	public void refresh() {
		datePickerContent.updateValues();
	}
	
	private LocalDateTime getStartDateOfMonth() {
		return datePickerContent.displayedYearMonthProperty().get().atDay(1).atStartOfDay();
	}
	
	private LocalDateTime getEndDateOfMonth() {
		return datePickerContent.displayedYearMonthProperty().get().atEndOfMonth().atTime(LocalTime.MAX);
	}
		
	class DateCellExt extends DateCell {
		private List<Object> cellEvents = new ArrayList<Object>();
		
		public List<Object> getEvents() {
			return cellEvents;
		}
		
		public void addEvent(LocalDate item, Object event) {
        	if (event instanceof WorkNote.WorkNoteEvent) {
        		WorkNote.WorkNoteEvent wNEvent = (WorkNote.WorkNoteEvent) event;
        		if (wNEvent.ofDate(item)) {
        			cellEvents.add(event);
        		}
        	}
        	else {
        		Task.TaskEvent taskEvent = (Task.TaskEvent) event;
        		if (taskEvent.ofDate(item)) {
        			cellEvents.add(event);
        		}
        	}
		}
		
		@Override
	    public void updateItem(LocalDate item, boolean empty) {
	        super.updateItem(item, empty);
	        if (!empty) {
	        	cellEvents.clear();
	        	for (Object event : events) {
	        		addEvent(item, event);
	        	}
	        	if (!cellEvents.isEmpty()) {
	        		if (item.equals(LocalDate.now()))
		        		setStyle("   -fx-font-weight: bold;\r\n" + 
		        				"    -fx-background-color: indianred;\r\n" + 
		        				"    -fx-text-fill: white;");
	        		else
		        		setStyle("   -fx-background-color: lightcoral;\r\n" + 
		        				"    -fx-text-fill: white;");
	        	}
	        	else {
	        		setStyle("");
	        	}
	        }
	    }
	}
}
