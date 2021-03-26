package sample.kernel.entity;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import sample.kernel.exception.DateException;
import sample.kernel.util.TaskPeriod;
import sample.kernel.util.ThinkStr;
import sample.kernel.util.ThinkUtil;

@SuppressWarnings("serial")
@Entity
public class Task extends Idea {

	private LocalDateTime startTime = LocalDateTime.now().plusDays(1);
	private TaskPeriod taskPeriod = TaskPeriod.ONCE;
	private LocalDateTime finishTime = LocalDateTime.now().plusDays(1);
	private Boolean notifyTask = false;
	
	public Task(String title, String desc, LocalDateTime time, List<Idea> relatedIdeas, Map<File, Boolean> attachments) {
		super();
		//this.setId(ThinkUtil.countIdea());
		this.setTitle(title);
		this.setDesc(desc);
		this.setTime(time);
		for (Idea idea : relatedIdeas) addRelatedIdea(idea);
	}
	
	public Task() {
		
	}
	
	/**
	 * Warning: it's used just after constructor (used to fix the incompletion and uploading)
	 */
	public void setTimesAttachsConstructor(LocalDateTime startTime, TaskPeriod taskPeriod, LocalDateTime finishTime, Map<File, Boolean> attachments) throws DateException {
		for (File attach : attachments.keySet())
			addAttachment(new Attachment(attach.getPath(), attachments.get(attach), this));
		if (startTime.isAfter(finishTime) || !verifiedWNTimes()) throw new DateException(ThinkStr.task_date_false.toString());
		this.startTime = startTime;
		this.taskPeriod = taskPeriod;
		this.finishTime = finishTime;
	}
	
	public Task(String title, String desc, LocalDateTime time, List<Idea> relatedIdeas, Map<File, Boolean> attachments,
			LocalDateTime startTime, TaskPeriod taskPeriod, LocalDateTime finishTime) throws DateException {
		super();
		//this.setId(ThinkUtil.countIdea());
		if (startTime.isAfter(finishTime) || !verifiedWNTimes()) throw new DateException(ThinkStr.task_date_false.toString());
		this.setTitle(title);
		this.setDesc(desc);
		this.setTime(time);
		for (Idea idea : relatedIdeas) addRelatedIdea(idea);
		for (File attach : attachments.keySet())
			addAttachment(new Attachment(attach.getPath(), attachments.get(attach), this));
		this.startTime = startTime;
		this.taskPeriod = taskPeriod;
		this.finishTime = finishTime;
	}

	public void setTimes(LocalDateTime startTime, TaskPeriod taskPeriod, LocalDateTime finishTime) throws DateException {
		if (startTime.isAfter(finishTime) || !verifiedWNTimes()) throw new DateException(ThinkStr.task_date_false.toString());
		this.startTime = startTime;
		this.taskPeriod = taskPeriod;
		this.finishTime = finishTime;
	}
	
	public void set(String title, String desc, LocalDateTime time, LocalDateTime startTime, TaskPeriod taskPeriod, LocalDateTime finishTime) throws DateException {
		if (startTime.isAfter(finishTime) || !verifiedWNTimes()) throw new DateException(ThinkStr.task_date_false.toString());
		this.startTime = startTime;
		this.taskPeriod = taskPeriod;
		this.finishTime = finishTime;
		setTitle(title);
		setDesc(desc);
		setTime(time);
	}
	
	public void set(Task task) {
		super.set(task);
		this.startTime = task.getStartTime();
		this.taskPeriod = task.getTaskPeriod();
		this.finishTime = task.getFinishTime();
	}
	
	public LocalDateTime getStartTime() {
		return startTime;
	}

	public TaskPeriod getTaskPeriod() {
		return taskPeriod;
	}

	public void setTaskPeriod(TaskPeriod taskPeriod) {
		this.taskPeriod = taskPeriod;
	}

	public LocalDateTime getFinishTime() {
		return finishTime;
	}
	
	private Boolean verifiedWNTimes() {
		for (WorkNote wN : getWorkNotes()) {
			if (wN.getDate().isBefore(startTime) || wN.getDate().isAfter(finishTime))
				System.out.println(wN);
		}
		return getWorkNotes().stream().filter(wN -> wN.getDate().isBefore(startTime) || wN.getDate().isAfter(finishTime)).findAny().isEmpty();
	}
	
	 /**FXML**/
	
	public StringProperty getStartTimeProperty() {
		return new SimpleStringProperty(startTime.format(DateTimeFormatter.ofPattern(ThinkUtil.datetimeFormatter)));
	}
	
	public StringProperty getFinishTimeProperty() {
		return new SimpleStringProperty(finishTime.format(DateTimeFormatter.ofPattern(ThinkUtil.datetimeFormatter)));
	}
	
	public Boolean getNotifyTask() {
		return notifyTask;
	}

	public void setNotifyTask(Boolean notifyTask) {
		this.notifyTask = notifyTask;
	}

	public static class TaskEvent {
		
		private int ideaId;
		private LocalDateTime startDate;
		private LocalDateTime finishDate;
		private String color;
		private TaskPeriod period;
		
		public TaskEvent(int ideaId, LocalDateTime startDate, LocalDateTime finishDate, String color, TaskPeriod period) {
			super();
			this.ideaId = ideaId;
			this.startDate = startDate;
			this.finishDate = finishDate;
			this.color = color;
			this.period = period;
		}

		public int getIdeaId() {
			return ideaId;
		}
		
		public void setIdeaId(int ideaId) {
			this.ideaId = ideaId;
		}
		
		public LocalDateTime getStartDate() {
			return startDate;
		}

		public void setStartDate(LocalDateTime startDate) {
			this.startDate = startDate;
		}

		public LocalDateTime getFinishDate() {
			return finishDate;
		}

		public void setFinishDate(LocalDateTime finishDate) {
			this.finishDate = finishDate;
		}

		public String getColor() {
			return color;
		}
		
		public void setColor(String color) {
			this.color = color;
		}
		
		public TaskPeriod getPeriod() {
			return period;
		}

		public void setPeriod(TaskPeriod period) {
			this.period = period;
		}

		/*private Long getPeriodMinutes() {
			switch (period) {
				case DAILY:
					return getStartDate().until(getStartDate().plusDays(1), ChronoUnit.MINUTES);
				case MONTHLY:
					return getStartDate().until(getStartDate().plusMonths(1), ChronoUnit.MINUTES);
				case WEEKLY:
					return getStartDate().until(getStartDate().plusWeeks(1), ChronoUnit.MINUTES);
				case YEARLY:
					return getStartDate().until(getStartDate().plusYears(1), ChronoUnit.MINUTES);
				default:
					return Long.valueOf(0);
			}
		}
		*/
		private Boolean verifyPeriod(LocalDate date) {
			switch (period) {
				case DAILY:
					return true;
				case MONTHLY:
					return (getStartDate().getDayOfMonth() == date.getDayOfMonth());
				case WEEKLY:
					return (getStartDate().getDayOfWeek().equals(date.getDayOfWeek()));
				case YEARLY:
					return ((getStartDate().getDayOfMonth() == date.getDayOfMonth()) && (getStartDate().getMonth().equals(date.getMonth())));
				default:
					return true;
			}
		}
		
		public Boolean ofDate(LocalDate date) {
			if (!date.isBefore(startDate.toLocalDate()) 
					&& !date.isAfter(finishDate.toLocalDate())) {
				if (period != TaskPeriod.ONCE) {
					/*Long duration = getStartDate().until(date.atTime(getStartDate().toLocalTime()), ChronoUnit.MINUTES);
					if (duration % getPeriodMinutes() == 0) {
						return true;
					}
					else
						return false;*/
					return verifyPeriod(date);
				}
				else return true;
			}
			else
				return false;
		}
	}
}
