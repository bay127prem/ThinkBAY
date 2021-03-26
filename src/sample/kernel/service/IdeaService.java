package sample.kernel.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import sample.kernel.entity.Idea;
import sample.kernel.entity.Task;
import sample.kernel.entity.WorkNote;
import sample.kernel.util.DBConnection;
import sample.kernel.util.TaskPeriod;
import sample.kernel.util.ThinkUtil;

//TODO: reimplement the methods in ThinkUtil according to this
/**Used to interact directly with database but ThinkUtil is used 
 * with the same methods to decide the true results when the connection is lost**/
public class IdeaService {
	
/*	private static void fixId(Idea idea, Session session) {
		int id = idea.getId();
		while (session.get(Idea.class, id) != null)
			++id;
		if (id != idea.getId()) {
			idea.setId(id);
			//ThinkUtil.setCountIdea(id);
		}
	}*/
	
	public static void add(Idea idea) {
		Session session = DBConnection.openSession();
		Transaction transaction = session.beginTransaction();
		//fixId(idea, session);
		session.save(idea);
		transaction.commit();
		session.close();
	}
	
	public static void add(Collection<Idea> ideas) {
		Session session = DBConnection.openSession();
		Transaction transaction = session.beginTransaction();
		for (Idea idea: ideas) {
			//fixId(idea, session);
			session.save(idea);
		}
		transaction.commit();
		session.close();
	}
	
	public static void delete(Idea idea) {
		Session session = DBConnection.openSession();
		Transaction transaction = session.beginTransaction();
		idea = session.get(Idea.class, idea.getId());
		if (idea != null)
			session.delete(idea);
		transaction.commit();
		session.close();
	}
	
	public static void delete(Collection<Idea> ideas) {
		Session session = DBConnection.openSession();
		Transaction transaction = session.beginTransaction();
		for (Idea idea: ideas) {
			idea = session.get(Idea.class, idea.getId());
			if (idea != null)
				session.delete(idea);
		}
		transaction.commit();
		session.close();
	}
	
	public static void modify(Idea idea) {
		Session session = DBConnection.openSession();
		Transaction transaction = session.beginTransaction();
		session.update(idea);
		transaction.commit();
		session.close();
	}
	
	public static void modify(Collection<Idea> ideas) {
		Session session = DBConnection.openSession();
		Transaction transaction = session.beginTransaction();
		for (Idea idea: ideas) 
			session.update(idea);
		transaction.commit();
		session.close();
	}
	
	public static void refresh(Idea idea) {
		Session session = DBConnection.openSession();
		Transaction transaction = session.beginTransaction();
		session.refresh(idea);
		transaction.commit();
		session.close();
	}
	
	public static void refresh(Collection<Idea> ideas) {
		Session session = DBConnection.openSession();
		Transaction transaction = session.beginTransaction();
		for (Idea idea: ideas) 
			session.refresh(idea);
		transaction.commit();
		session.close();
	}
	
	public static Idea search(int id) {
		Session session = DBConnection.openSession();
		Idea idea = session.get(Idea.class, id);
		session.close();
		return idea;
	}

	public static List<Idea> search(String title, int page) {
		Session session = DBConnection.openSession();
		Query<Idea> query = session
				.createQuery("From sample.kernel.entity.Idea as idea where idea.title like :title", Idea.class);
		query.setParameter("title", "%" + title + "%");
		query.setFirstResult((page - 1) * ThinkUtil.getIdeasPerPage());
		query.setMaxResults(ThinkUtil.getIdeasPerPage());
		List<Idea> ideas = query.getResultList();
		session.close();
		return ideas;
	}
	
	public static List<Idea> search(String title,LocalDate date, Boolean task, Boolean done, Year year, int page) {
		Session session = DBConnection.openSession();
		String queryStr = "From sample.kernel.entity.Idea as idea where";
		Boolean [] booleans = {false, false, false, false};
		if (!title.isEmpty() && !title.isBlank()) {
			queryStr = queryStr.concat(" concat(idea.id,' (',idea.title,')') like :title and");
			booleans[0] = true;
		}
		if (date != null) {
			queryStr = queryStr.concat(" year(idea.time) = :yearD and month(idea.time) = :monthD and day(idea.time) = :dayD and");
			booleans[1] = true;
		}
		if (task) {
			queryStr = queryStr.concat(" type(idea) = sample.kernel.entity.Task and");
		}
		if (year != null) {
			queryStr = queryStr.concat(" year(idea.time) = :year and");
			booleans[2] = true;
		}
		if (done != null) {
			if (done) queryStr = queryStr.concat(" exists elements(idea.workNotes) and not exists(from idea.workNotes wN where not wN.done = :done)");
			else queryStr = queryStr.concat(" not exists elements(idea.workNotes) or exists(from idea.workNotes wN where wN.done = :done)");
			booleans[3] = true;
		}
		if (queryStr.endsWith(" and")) {
			int index= queryStr.lastIndexOf(" ");
			queryStr = queryStr.substring(0, index);
		}
		else {
			if (queryStr.endsWith(" where"))
				queryStr = "From sample.kernel.entity.Idea";
		}
		System.out.println(queryStr);
		Query<Idea> query = session.createQuery(queryStr, Idea.class);
		if (booleans[0])
			query.setParameter("title", "%" + title + "%");
		if (booleans[1]) {
			query.setParameter("yearD", date.getYear());
			query.setParameter("monthD", date.getMonth().getValue());
			query.setParameter("dayD", date.getDayOfMonth());
		}
		if (booleans[2])
			query.setParameter("year", year.getValue());
		if (booleans[3])
			query.setParameter("done", done);
		query.setFirstResult((page - 1) * ThinkUtil.getIdeasPerPage());
		query.setMaxResults(ThinkUtil.getIdeasPerPage());
		List<Idea> ideas = query.getResultList();
		session.close();
		return ideas;
	}
	
	public static Year getFirstYear() {
		Session session = DBConnection.openSession();
		Query<LocalDateTime> query = session.createQuery("Select min(idea.time) from sample.kernel.entity.Idea as idea", LocalDateTime.class);
		LocalDateTime date = (LocalDateTime) query.uniqueResult();
		session.close();
		if (date == null)
			return null;
		else
			return Year.of(date.getYear());
	}
	
	public static final Integer completerIdeasSize = 10;
	
	public static List<Idea> completerSearch(String str) {
		Session session = DBConnection.openSession();
		Query<Idea> query = session
				.createQuery("From sample.kernel.entity.Idea as idea where concat(idea.id,' (',idea.title,')') like :str", Idea.class);
		query.setParameter("str", "%" + str + "%");
		query.setFirstResult(0);
		query.setMaxResults(completerIdeasSize);
		List<Idea> ideas = query.getResultList();
		session.close();
		return ideas;
	}
	
	public static Long getSizeSearch(String title) {
		Session session = DBConnection.openSession();
		Query<Long> countQuery = session.createQuery("Select count (idea.id) from sample.kernel.entity.Idea as idea where idea.title like :title", Long.class);
		countQuery.setParameter("title", "%" + title + "%");
		Long countResults = (Long) countQuery.uniqueResult();
		session.close();
		return countResults;
	}
	
	public static Long getPagesSearch(String title) {
		Session session = DBConnection.openSession();
		Query<Long> countQuery = session.createQuery("Select count (idea.id) from sample.kernel.entity.Idea as idea where idea.title like :title", Long.class);
		countQuery.setParameter("title", "%" + title + "%");
		Long countResults = (Long) countQuery.uniqueResult();
		session.close();
		double pages = (countResults / ThinkUtil.getIdeasPerPage());
		if (countResults % ThinkUtil.getIdeasPerPage() == 0)
			return (long) pages;
		else 
			return (long) (pages + 1);
	}
	
	public static Long getLastPageNumber() {
		Session session = DBConnection.openSession();
		Query<Long> countQuery = session.createQuery("Select count (idea.id) from sample.kernel.entity.Idea idea", Long.class);
		Long countResults = (Long) countQuery.uniqueResult();
		session.close();
		double pages = (countResults / ThinkUtil.getIdeasPerPage());
		if (countResults % ThinkUtil.getIdeasPerPage() == 0)
			return (long) pages;
		else
			return (long) (pages + 1);
	}
	
	public static List<Idea> requestList(int page) {
		Session session = DBConnection.openSession();
		Query<Idea> query = session.createQuery("From sample.kernel.entity.Idea", Idea.class);
		query.setFirstResult((page - 1) * ThinkUtil.getIdeasPerPage());
		query.setMaxResults(ThinkUtil.getIdeasPerPage());
		List<Idea> ideas = query.list();
		session.close();
		return ideas;
	}
	
	public static List<Idea> ideasWNotifs() {
		Session session = DBConnection.openSession();
		Query<Idea> query = session.createQuery("From sample.kernel.entity.Idea as idea where idea.notifyWN = :notif and ( not exists elements(idea.workNotes) or exists(from idea.workNotes wN where wN.done = :done) )", Idea.class);
		query.setParameter("notif", true);
		query.setParameter("done", false);
		List<Idea> ideas = query.list();
		session.close();
		return ideas;
	}
	
	public static List<Task> tasksNotifs() {
		Session session = DBConnection.openSession();
		Query<Task> query = session.createQuery("From sample.kernel.entity.Task as task where task.notifyTask = :notif"
				+ " and ( current_time() between task.startTime and task.finishTime )"
				+ " and ( task.taskPeriod = :once"
				+ " or ( task.taskPeriod = :daily and hour(current_time()) = hour(task.startTime) )" 
				+ " or ( task.taskPeriod = :weekly and weekday(current_time()) = weekday(task.startTime) )" 
				+ " or ( task.taskPeriod = :monthly and day(current_time()) = day(task.startTime) )" 
				+ " or ( task.taskPeriod = :yearly and month(current_time()) = month(task.startTime) and day(current_time()) = day(task.startTime) )" 
				+ " )"
				, Task.class);
		query.setParameter("once", TaskPeriod.ONCE);
		query.setParameter("daily", TaskPeriod.DAILY);
		query.setParameter("weekly", TaskPeriod.WEEKLY);
		query.setParameter("monthly", TaskPeriod.MONTHLY);
		query.setParameter("yearly", TaskPeriod.YEARLY);
		query.setParameter("notif", true);
		List<Task> tasks = query.list();
		session.close();
		return tasks;
	}
	
	/**The object is TaskEvent or WorkNoteEvent**/
	public static List<Object> requestEvents(LocalDateTime startDate, LocalDateTime finishDate) {
		List<Object> events = new ArrayList<Object>();
		Session session = DBConnection.openSession();
		@SuppressWarnings("rawtypes")
		Query workNoteQuery = session.createQuery("Select workNote.idea.id, workNote.date, workNote.idea.color from sample.kernel.entity.WorkNote as workNote where workNote.date between :start and :end");
		workNoteQuery.setParameter("start", startDate);
		workNoteQuery.setParameter("end", finishDate);
		@SuppressWarnings("unchecked")
		List<Object[]> listWNs = (List<Object[]>) workNoteQuery.getResultList();
		for (Object[] event : listWNs) {
			events.add(new WorkNote.WorkNoteEvent((int) event[0],(LocalDateTime) event[1],(String) event[2]));
		}
		@SuppressWarnings("rawtypes")
		Query taskQuery = session.createQuery("Select task.id, task.startTime, task.finishTime, task.color, task.taskPeriod from sample.kernel.entity.Task as task where ( task.startTime between :start and :end ) OR ( task.finishTime between :start and :end ) OR ( :start between task.startTime and task.finishTime ) OR ( :end between task.startTime and task.finishTime )");
		taskQuery.setParameter("start", startDate);
		taskQuery.setParameter("end", finishDate);
		@SuppressWarnings("unchecked")
		List<Object[]> listTasks = (List<Object[]>) taskQuery.getResultList();
		for (Object[] event : listTasks) {
			events.add(new Task.TaskEvent((int) event[0],(LocalDateTime) event[1],(LocalDateTime) event[2],(String) event[3], (TaskPeriod) event[4]));
		}
		session.close();
		return events;
	}		
	
	public static Long getIdeasNumber() {
		Session session = DBConnection.openSession();
		Query<Long> countQuery = session.createQuery("Select count (idea.id) from sample.kernel.entity.Idea as idea", Long.class);
		Long countResults = (Long) countQuery.uniqueResult();
		session.close();
		return countResults;
	}
	
	public static Long getTasksNumber() {
		Session session = DBConnection.openSession();
		Query<Long> countQuery = session.createQuery("Select count (task.id) from sample.kernel.entity.Task as task", Long.class);
		Long countResults = (Long) countQuery.uniqueResult();
		session.close();
		return countResults;
	}
	
	public static Long getWorkNotesNumber() {
		Session session = DBConnection.openSession();
		Query<Long> countQuery = session.createQuery("Select count (wN.id) from sample.kernel.entity.WorkNote as wN", Long.class);
		Long countResults = (Long) countQuery.uniqueResult();
		session.close();
		return countResults;
	}
	
	public static Long getAttachsNumber() {
		Session session = DBConnection.openSession();
		Query<Long> countQuery = session.createQuery("Select count (attach.id) from sample.kernel.entity.Attachment as attach", Long.class);
		Long countResults = (Long) countQuery.uniqueResult();
		session.close();
		return countResults;
	}
	
	public static void check() {
		Session session = DBConnection.openSession();
		Transaction transaction = session.beginTransaction();
		transaction.commit();
		session.close();
	}
	
}
