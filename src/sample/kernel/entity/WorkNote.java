package sample.kernel.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import sample.kernel.exception.DateException;
import sample.kernel.util.ThinkStr;
import sample.kernel.util.ThinkUtil;

@Entity
public class WorkNote implements Serializable {
	private static final long serialVersionUID = -4974423554653377190L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "WNID")
	private int idWN;
	@Column(name = "WNTime")
	private LocalDateTime date;
	@Column(name = "WNTitle")
	private String title;
	@Column(name = "WNNote", length = ThinkUtil.maxContentSize)
	private String note;
	@Column(name = "WNDone")
	private Boolean done;
	@ManyToOne
	private Idea idea;
	
	public WorkNote(String title, LocalDateTime date, String note, Idea idea, Boolean done) throws DateException {
		//this.setId(ThinkUtil.countWN());
		setTitle(title);
		setDate(date);
		this.setNote(note);
		setIdea(idea);
		if (idea instanceof Task) {
			Task task = (Task) idea;
			if (date.isBefore(task.getStartTime()) && date.isAfter(task.getFinishTime()))
				throw new DateException(ThinkStr.date_worknote_task_false.toString());
		}
		this.idea.addWorkNote(this);
		setDone(done);
	}
	
	public WorkNote() {
		
	}
	
	public void setWorkNote(String title, LocalDateTime date, String note, Boolean done) throws DateException {
		setTitle(title);
		setDate(date);
		this.setNote(note);
		if (idea instanceof Task) {
			Task task = (Task) idea;
			if (date.isBefore(task.getStartTime()) && date.isAfter(task.getFinishTime()))
				throw new DateException(ThinkStr.date_worknote_task_false.toString());
		}
		setDone(done);
	}

	public Boolean isDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getId() {
		return idWN;
	}

	public void setId(int id) {
		this.idWN = id;
	}

	public WorkNote(int id) {
		super();
		this.idWN = id;
	}
	
	public void removeIdea() {
		idea = null;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Idea getIdea() {
		return idea;
	}

	public void setIdea(Idea idea) {
		this.idea = idea;
	}

	public Boolean getDone() {
		return done;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idWN;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WorkNote other = (WorkNote) obj;
		if (idWN != other.idWN)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return idWN + " (" + title + ")";
	}
	
	public String getInfo() {
		return ThinkStr.id + " (" +ThinkStr.title + "): " + toString() + "\n" +
				ThinkStr.date_and_time + ": " + getDate().format(DateTimeFormatter.ofPattern(ThinkUtil.datetimeFormatter)) + "\n" +
				ThinkStr.description + ": \n \t" + getNote();
	}
	
	/**JavaFX**/
	public SimpleObjectProperty<Integer> getIdProperty() {
		return new SimpleObjectProperty<Integer>(idWN);
	}
	
	public StringProperty getTitleProperty() {
		return new SimpleStringProperty(title);
	}
	
	public StringProperty getDoneProperty() {
		String str = "";
		if (done) str = "✓";
		return new SimpleStringProperty(str);
	}
	
	public StringProperty getIdeaProperty() {
		return new SimpleStringProperty(idea.toString());
	}

	public StringProperty getDateProperty() {
		return new SimpleStringProperty(date.format(DateTimeFormatter.ofPattern(ThinkUtil.datetimeFormatter)));
	}
	
	public static class WorkNoteEvent {
		
		private int ideaId;
		private LocalDateTime date;
		private String color;
		
		public WorkNoteEvent(int ideaId, LocalDateTime date, String color) {
			super();
			this.ideaId = ideaId;
			this.date = date;
			this.color = color;
		}

		@Override
		public String toString() {
			return "WorkNoteEvent [ideaId=" + ideaId + ", date=" + date + "]";
		}

		public int getIdeaId() {
			return ideaId;
		}
		
		public void setIdeaId(int ideaId) {
			this.ideaId = ideaId;
		}
		
		public LocalDateTime getDate() {
			return date;
		}
		
		public void setDate(LocalDateTime date) {
			this.date = date;
		}
		
		public String getColor() {
			return color;
		}
		
		public void setColor(String color) {
			this.color = color;
		}
		
		public Boolean ofDate(LocalDate date) {
			if (getDate().toLocalDate().equals(date))
				return true;
			else
				return false;
		}
	}
}
