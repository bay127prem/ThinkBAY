package sample.kernel.service;

import org.hibernate.Session;

import sample.kernel.entity.Attachment;

public class AttachmentService {
	
	public static void fixId(Attachment attach, Session session) {
		int id = attach.getId();
		while (session.get(Attachment.class, id) != null)
			++id;
		if (id != attach.getId()) {
			attach.setId(id);
			//ThinkUtil.setCountAttachs(id);
		}
	}
	
}
