package sample.kernel.util;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import sample.control.util.LoadingController;
import sample.kernel.entity.Idea;
import sample.kernel.entity.Task;
import sample.kernel.service.IdeaService;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

public class Notification {
	
	public static void addNotification(Exception e) {
		ajouterTrayNotif(ThinkStr.error.toString(), e.getMessage(), NotificationType.ERROR);
	}
	
	public static void addNotification(String str) {
		ajouterTrayNotif(ThinkStr.success.toString(), str, NotificationType.INFORMATION);
	}
	
	public static void addNotification(String str, Boolean error) {
		NotificationType type = NotificationType.INFORMATION;
		if (error)
			type = NotificationType.ERROR;
		ajouterTrayNotif(ThinkStr.success.toString(), str, type);
	}
	
	public static void addNotificationWN(Idea idea) {
		idea.setNotifyWN(true);
	}
	
	public static void removeNotificationWN(Idea idea) {
		idea.setNotifyWN(false);
	}
	
	public static void addNotification(Task task) {
		task.setNotifyTask(true);
	}
	
	public static void removeNotification(Task task) {
		task.setNotifyTask(false);
	}
	
	private static void ajouterTrayNotif(String titre, String description, NotificationType type) {
		if (ThinkUtil.getTrayPopup()) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					TrayNotification tray = new TrayNotification(titre, description, type);
					tray.setRectangleFill(Paint.valueOf("#ffc107"));
					try {
						switch (type) {
						case ERROR:
							tray.setImage(new Image(Notification.class.getResourceAsStream("/sample/sources/img/error.png")));
							break;
						default:
							tray.setImage(new Image(Notification.class.getResourceAsStream("/sample/sources/img/info.png")));
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					tray.setAnimationType(AnimationType.POPUP);
					tray.showAndWait();
				}
			});
		}
	}

	public static List<Task> getTasks() {
		try {
			LoadingController.load();
			List<Task> tasks = IdeaService.tasksNotifs();
			LoadingController.unload();
			return tasks;
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
			return new ArrayList<Task>();
		}
	}

	public static List<Idea> getIdeasWN() {
		try {
			LoadingController.load();
			List<Idea> ideas = IdeaService.ideasWNotifs();
			LoadingController.unload();
			return ideas;
		}
		catch (Exception e) {
			e.printStackTrace();
			LoadingController.unload();
			return new ArrayList<Idea>();
		}
	}
	
}
