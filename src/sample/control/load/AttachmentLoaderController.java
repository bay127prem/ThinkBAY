package sample.control.load;

import java.io.File;
import java.util.Map.Entry;

import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import sample.control.Controller;
import sample.control.idea.creator.IdeaCreatorController;
import sample.kernel.util.ThinkStr;

public class AttachmentLoaderController extends Controller {
	@FXML
	private Text extension;
	@FXML
	private MenuItem delete;
	@FXML
	private CheckMenuItem toUpload;
	@FXML
	private Menu source;
	@FXML
	private Text sourceText;
	
	private File file;
	
	@FXML
	public void initialize() {
		
	}

	public File getFile() {
		return file;
	}
	
	public Entry<File, Boolean> getAttachmentEntry() {
		return new Entry<File, Boolean>() {
			@Override
			public File getKey() {
				return getFile();
			}

			@Override
			public Boolean getValue() {
				return toUpload.isSelected();
			}

			@Override
			public Boolean setValue(Boolean value) {
				toUpload.setSelected(value);
				return value;
			}
		};
	}
		
	private String getExtention(File file) {
		String[] fileStrs = (file.getName().split("\\."));
		if (fileStrs.length <= 1) return "N/A";
		return fileStrs[(fileStrs.length - 1)];
	}

	public void setFile(File file) {
		this.file = file;
		sourceText.setText(file.getAbsolutePath());
		extension.setText(getExtention(file).toUpperCase());
		delete.setText(ThinkStr.delete.toString());
		toUpload.setText(ThinkStr.upload.toString());
		source.setText(ThinkStr.source.toString());
	}
	
	private IdeaCreatorController controller;
	
	public void delete() {
		((HBox) getPane().getParent()).getChildren().remove(getPane());
		controller.removeAttachLoader(this);
	}

	public IdeaCreatorController getController() {
		return controller;
	}

	public void setController(IdeaCreatorController controller) {
		this.controller = controller;
	}
	
}
