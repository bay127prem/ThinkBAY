package sample.control.load;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import sample.control.Controller;
import sample.control.idea.IdeaInfoController;
import sample.kernel.entity.Attachment;
import sample.kernel.util.FTPUtil;
import sample.kernel.util.ThinkStr;
import sample.kernel.util.ThinkUtil;

public class LoadPaneController extends Controller {

	@FXML
	private ProgressBar progress;
	@FXML
	private Label fileNameText;
	@FXML
	private Label sourceLink;
	@FXML
	private Label destLink;
	@FXML
	private MenuItem delete;
	@FXML
	private MenuItem idea;
	@FXML
	private Text extension;
	
	private Attachment attach;
	
	private Boolean upload = true;  // else download
	
	/**
	 *Set the nodes + Set the language settings
	 */
	private String getExtention(File file) {
		String[] fileStrs = (file.getName().split("\\."));
		if (fileStrs.length <= 1) return "";
		return fileStrs[(fileStrs.length - 1)];
	}
	
	@FXML
	public void initialize() {
		
	}

	public Attachment getAttach() {
		return attach;
	}
	
	public void setProgressVisibility(Boolean visible) {
		progress.setVisible(visible);
		disableDelete(visible);
	}

	public void setAttach(Attachment attach) {
		this.attach = attach;
		File file = new File(attach.getLocalURL());
		if (upload) {
			sourceLink.setText(ThinkStr.source + ": " + attach.getLocalURL());
			destLink.setText(ThinkStr.destination + ": " + attach.getExternURL());
		}
		else {
			sourceLink.setText(ThinkStr.source + ": " + attach.getExternURL()); 
			destLink.setText(ThinkStr.destination + ": " + attach.getLocalURL());
		}
		delete.setText(ThinkStr.delete.toString());
		idea.setText(ThinkStr.ideaRelated.toString());
		extension.setText(getExtention(file).toUpperCase());
		if (upload) {
			fileNameText.setText("▲ " + file.getName() + " (" + attach.getId() + ")");
			attach.setUploadControllerProgress();
		}
		else {
			fileNameText.setText("▼ " + file.getName() + " (" + attach.getId() + ")");
			attach.setDownloadControllerProgress();
		}
	}
	
	private void disableDelete(Boolean disable) {
		delete.setDisable(disable);
	}
	
	public void setDownload() {
		upload = false;
	}
	
	public Boolean getDownload() {
		return (!upload);
	}
	
	public void delete() {
		if (upload) {
			ThinkUtil.removeToUploadPane(attach);
			try {
				if (!FTPUtil.checkFileExists(attach.getExternURL())) 
					attach.setExternURL("");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			ThinkUtil.removeToDownloadPane(attach);
		}
	}
	
	public void idea() {
		IdeaInfoController.openIdeaInfo(attach.getIdea(), getStage());
	}
}
