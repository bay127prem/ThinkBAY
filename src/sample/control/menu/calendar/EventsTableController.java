package sample.control.menu.calendar;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import sample.control.Controller;
import sample.control.idea.IdeaInfoController;
import sample.kernel.entity.Idea;
import sample.kernel.entity.Task;
import sample.kernel.entity.WorkNote;
import sample.kernel.util.ThinkStr;
import sample.kernel.util.ThinkUtil;

public class EventsTableController extends Controller {
	
    @FXML
    private TableView<Object> eventsT;
    @FXML
    private TableColumn<Object, String> typeC;
    @FXML
    private TableColumn<Object, Integer> ideaIdC;
    @FXML
    private TableColumn<Object, String> startDateC;
    @FXML
    private TableColumn<Object, String> finishDateC;
    @FXML
    private TableColumn<Object, ThinkStr> period;
    
    private List<Object> events = new ArrayList<Object>();
    
    @FXML
    public void initialize() {
    	typeC.setText(ThinkStr.type.toString());
    	ideaIdC.setText(ThinkStr.ideaRelated.toString());
    	startDateC.setText(ThinkStr.startDate.toString());
    	finishDateC.setText(ThinkStr.finishDate.toString());
    	period.setText(ThinkStr.period.toString());
    	typeC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Object, String> param) {
				if (param.getValue() instanceof WorkNote.WorkNoteEvent)
					return new SimpleStringProperty(ThinkStr.workNote.toString());
				else
					return new SimpleStringProperty(ThinkStr.task.toString());
			}
		});
    	ideaIdC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object,Integer>, ObservableValue<Integer>>() {
			@Override
			public ObservableValue<Integer> call(CellDataFeatures<Object, Integer> param) {
				if (param.getValue() instanceof WorkNote.WorkNoteEvent) {
					WorkNote.WorkNoteEvent wNEvent = (WorkNote.WorkNoteEvent) param.getValue();
					return new SimpleObjectProperty<Integer>(wNEvent.getIdeaId());
				}
				else {
					Task.TaskEvent taskEvent = (Task.TaskEvent) param.getValue();
					return new SimpleObjectProperty<Integer>(taskEvent.getIdeaId());
				}
			}
		});
    	startDateC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Object, String> param) {
				if (param.getValue() instanceof WorkNote.WorkNoteEvent) {
					WorkNote.WorkNoteEvent wNEvent = (WorkNote.WorkNoteEvent) param.getValue();
					return new SimpleStringProperty(wNEvent.getDate().format(DateTimeFormatter.ofPattern(ThinkUtil.datetimeFormatter)));
				}
				else {
					Task.TaskEvent taskEvent = (Task.TaskEvent) param.getValue();
					return new SimpleStringProperty(taskEvent.getStartDate().format(DateTimeFormatter.ofPattern(ThinkUtil.datetimeFormatter)));
				}
			}
		});
    	finishDateC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Object, String> param) {
				if (param.getValue() instanceof WorkNote.WorkNoteEvent) {
					return new SimpleStringProperty("");
				}
				else {
					Task.TaskEvent taskEvent = (Task.TaskEvent) param.getValue();
					return new SimpleStringProperty(taskEvent.getFinishDate().format(DateTimeFormatter.ofPattern(ThinkUtil.datetimeFormatter)));
				}
			}
		});
    	
    	period.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object,ThinkStr>, ObservableValue<ThinkStr>>() {
			@Override
			public ObservableValue<ThinkStr> call(CellDataFeatures<Object, ThinkStr> param) {
				if (param.getValue() instanceof WorkNote.WorkNoteEvent) {
					return new SimpleObjectProperty<ThinkStr>(null);
				}
				else {
					Task.TaskEvent taskEvent = (Task.TaskEvent) param.getValue();
					return new SimpleObjectProperty<ThinkStr>(taskEvent.getPeriod().getThinkStr());
				}
			}
		});
    	
    	eventsT.setRowFactory(tv -> {
			TableRow<Object> row = new TableRow<>() {
				@Override
				public void updateItem(Object item, boolean empty) {
					super.updateItem(item, empty);
					if (item != null) {
						setStyle("");
						if (item instanceof WorkNote.WorkNoteEvent) {
							WorkNote.WorkNoteEvent wNEvent = (WorkNote.WorkNoteEvent) item;
							setStyle("	  -fx-background-color: " + wNEvent.getColor() + ";\r\n" + 
									 "    -fx-accent: #0093ff;\r\n" +
									 "    -fx-focus-color: #0093ff;");
						}
						else {
							Task.TaskEvent taskEvent = (Task.TaskEvent) item;
							setStyle("	  -fx-background-color: " + taskEvent.getColor() + ";\r\n" + 
									 "    -fx-accent: #0093ff;\r\n" +
									 "    -fx-focus-color: #0093ff;");
						}
					}
				}
			};
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() > 1 && (!row.isEmpty())) {
					int id = -1;
					Object item = row.getItem();
					if (item instanceof WorkNote.WorkNoteEvent) {
						WorkNote.WorkNoteEvent wNEvent = (WorkNote.WorkNoteEvent) item;
						id = wNEvent.getIdeaId();
					}
					else {
						Task.TaskEvent taskEvent = (Task.TaskEvent) item;
						id = taskEvent.getIdeaId();
					}
					Idea idea = ThinkUtil.searchIdea(id);
					if (idea != null)
						IdeaInfoController.openIdeaInfo(idea, getStage());
				}
			});
			return row;
		});
    	super.initialize();
    }

	public List<Object> getEvents() {
		return events;
	}

	public void setEvents(List<Object> events) {
		this.events = events;
		eventsT.setItems(FXCollections.observableArrayList(events));
	}
}