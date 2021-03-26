package sample.kernel.service;

import org.hibernate.Session;

import sample.kernel.entity.WorkNote;

public class WorkNoteService {

	public static void fixId(WorkNote wN, Session session) {
		int id = wN.getId();
		while (session.get(WorkNote.class, id) != null)
			++id;
		if (id != wN.getId()) {
			wN.setId(id);
			//ThinkUtil.setCountWN(id);
		}			
	}
	
}
