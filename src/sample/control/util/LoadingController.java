package sample.control.util;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Main;
import sample.control.Controller;
import sample.kernel.util.ThinkStr;

public class LoadingController extends Controller {
	
	@FXML
	private Label wait;
	
	private static Stage stage;
	
	@FXML
	public void initialize() {
		wait.setText(ThinkStr.load.toString());
		super.initialize();
	}
	
	public static void load() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(Main.class.getResource("vue/LoadingPane.fxml"));
					Parent root;
					root = loader.load();
					root.setOnMouseClicked(event -> {
						if (event.getClickCount() > 1) {
							unload();
						}
					});
					stage = new Stage();
					stage.setScene(new Scene(root));
					stage.initStyle(StageStyle.UNDECORATED);
					stage.setAlwaysOnTop(true);
					stage.setResizable(false);
					stage.show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public static void unload() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				stage.close();
			}
		});
	}
}
