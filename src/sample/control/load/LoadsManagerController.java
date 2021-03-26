package sample.control.load;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sample.Main;
import sample.control.Controller;
import sample.kernel.entity.Attachment;
import sample.kernel.util.ThinkStr;
import sample.kernel.util.ThinkUtil;


public class LoadsManagerController extends Controller {
	@FXML
	private Text loadsManagerText;
	@FXML
	private Button play;
	@FXML
	private Button stop;
	@FXML
	private VBox loadsBox;
		
	/**
	 *Set the nodes + Set the language settings
	 */
	@FXML
	public void initialize() {
		setDisableButtons();
		
		loadsManagerText.setText(ThinkStr.load_manager.toString());
		play.setText(ThinkStr.play.toString());
		stop.setText(ThinkStr.stop.toString());
		
		super.initialize();
		initializeLoadPaneS();
	}
	
	private void initializeLoadPaneS() {
		for (Attachment attach : ThinkUtil.getToDownloadS()) {
			addDownloadPane(attach);
		}
		
		for (Attachment attach : ThinkUtil.getToUploadS()) {
			addUploadPane(attach);
		}
	}
	
	public void stop() {
		ThinkUtil.setLoadProcess(false);
		setDisableButtons();
	}
	
	public void play() {
		ThinkUtil.setLoadProcess(true);
		setDisableButtons();
	}
	
	public void setDisableButtons() {
		play.setDisable(ThinkUtil.getLoadProcess());
		stop.setDisable(!ThinkUtil.getLoadProcess());
	}
	
	public void removeDownloadPane(Attachment attach) {
		if (attach.getDownloadController() != null) {
			BorderPane pane = attach.getDownloadController().getPane();
			pane.setVisible(false);
			pane.setPrefSize(0, 0);
		}
	}
	
	public void removeUploadPane(Attachment attach) {
		if (attach.getUploadController() != null) {
			BorderPane pane = attach.getUploadController().getPane();
			pane.setVisible(false);
			pane.setPrefSize(0, 0);
		}
	}
	
	public void addUploadPane(Attachment attach) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("vue/LoadPane.fxml"));
			Parent pane = loader.load();
			LoadPaneController controller = loader.getController();
			attach.setUploadController(controller);
			controller.setAttach(attach);
			loadsBox.getChildren().add(pane);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addDownloadPane(Attachment attach) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("vue/LoadPane.fxml"));
			Parent pane = loader.load();
			LoadPaneController controller = loader.getController();
			controller.setDownload();
			attach.setDownloadController(controller);
			controller.setAttach(attach);
			loadsBox.getChildren().add(pane);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
