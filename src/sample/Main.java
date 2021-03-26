package sample;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.control.Controller;
import sample.kernel.util.DBConnection;
import sample.kernel.util.Language;
import sample.kernel.util.ThinkStr;
import sample.kernel.util.ThinkUtil;

public class Main extends Application {

	public static void main(String[] args) {
		try {
			if (!ThinkUtil.log.exists()) ThinkUtil.log.createNewFile();
			System.setErr(new PrintStream(new FileOutputStream(ThinkUtil.log)));
			System.setOut(new PrintStream(new FileOutputStream(ThinkUtil.log)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ThinkUtil.setLang(Language.EN);
		System.out.println(ThinkStr.welcome);
		launch(args);
		ThinkUtil.serialize();
		ThinkUtil.shutdownTasks();
		DBConnection.closeSessionFactories();
	}

	@Override
	public void start(Stage primStage) throws Exception {
		try {
			Platform.setImplicitExit(false);
			primStage.setMinHeight(Controller.height);
			primStage.setMinWidth(Controller.width);
			Controller.setStage(primStage);
			ThinkUtil.deserialize();
			ThinkUtil.initialize();
			Controller.addTrayIcon();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("vue/ThinkMenu.fxml"));
			Parent root = loader.load();
			primStage.setWidth(Controller.width);
			primStage.setHeight(Controller.height);
			primStage.setScene(new Scene(root));
			primStage.setResizable(true);
			primStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
