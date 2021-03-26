package sample.control;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import sample.Main;
import sample.control.idea.IdeaInfoController;
import sample.kernel.entity.Idea;
import sample.kernel.util.ThinkStr;
import sample.kernel.util.ThinkUtil;

public class Controller {
	
	private static Stage stage;
	public static Boolean fullScreen = false;
	@FXML
	private BorderPane pane;
	
	public static Double height = Double.valueOf(550);
	public static Double width = Double.valueOf(800);
	
	public static final String icon_path = "/sample/sources/img/icon.png";
	public static final String ico_path = "/sample/sources/img/icon.ico";
	public static final String icon_gif_path = "/sample/sources/img/icon.gif";
	
	@FXML
	public void initialize() {
		if (ThinkUtil.getDefaultheme())
			pane.getStylesheets().clear();
		else
			pane.getStylesheets().addAll(
	                Main.class
	                .getResource("sources/css/theme/bootstrap2.css").toExternalForm(),
	                Main.class
	                .getResource("sources/css/theme/bootstrap3.css").toExternalForm());
		if (ThinkUtil.getLang().isRTL())
			pane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		else
			pane.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
	}

	public BorderPane getPane() {
		return pane;
	}

	public void setPane(BorderPane pane) {
		this.pane = pane;
	}

	public static Stage getStage() {
		stage.setFullScreen(fullScreen);
		stage.setHeight(height);
		stage.setWidth(width);
		return stage;
	}

	public static void setStage(Stage stage) {
		Controller.stage = stage;
		Controller.stage.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				width = newValue.doubleValue();
			}
		});
		Controller.stage.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				height = newValue.doubleValue();
			}
		});
	}
	
	public static void setStageFullSecreen(Boolean fullScreen) {
		Controller.fullScreen = fullScreen;
		getStage().setFullScreen(fullScreen);
	}
	
	public static void setStageFullSecreen() {
		getStage().setFullScreen(fullScreen);
	}
	
	public static void callPage(String XMLFile) {
		try {
			URL url = new URL(XMLFile);
			Parent root = FXMLLoader.load(url);
			stage.getScene().setRoot(root);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public static TrayIcon trayIcon = null;
	public static SystemTray tray = null;
	
	
	public static void addTrayIcon() {
		//Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        URL url = Main.class.getResource(icon_gif_path);
        Image image = Toolkit.getDefaultToolkit().getImage(url);
        trayIcon = new TrayIcon(image, ThinkStr.thinkBAY.toString());
        tray = SystemTray.getSystemTray();
        final PopupMenu popup = new PopupMenu();
        MenuItem exitItem = new MenuItem(ThinkStr.close.toString());
        popup.add(exitItem);
        trayIcon.setPopupMenu(popup);
        trayIcon.setImageAutoSize(true);
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getButton() == MouseEvent.BUTTON1) {
                	  if (event.getClickCount() > 1) {
                          Platform.runLater(new Runnable() {
                              @Override
                              public void run() {
                                  if (stage.isShowing()) {
                                      stage.hide();
                                      for (Idea idea : IdeaInfoController.getOpenedIdeas().keySet()) {
                                    	  Stage stage = IdeaInfoController.getOpenedIdeas().get(idea);
                                    	  stage.hide();
                                      }
                                  } else {
                                      stage.show();
                                      for (Idea idea : IdeaInfoController.getOpenedIdeas().keySet()) {
                                    	  Stage stage = IdeaInfoController.getOpenedIdeas().get(idea);
                                    	  stage.show();
                                      }
                                  }
                              }
                          });
              		  }
                }
            }
        });
        exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Platform.exit();
				tray.remove(trayIcon);
			}
		});
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
	}

}
