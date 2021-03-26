package sample.control.idea;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sample.control.Controller;
import sample.kernel.util.ThinkStr;

public class FindReplaceController extends Controller {
	@FXML
	private TextField findField;
	@FXML
	private TextField replaceWithField;
	@FXML
	private Button nextB;
	@FXML
	private Button replaceB;
	@FXML
	private Button replaceAllB;
	@FXML
	private Label result;
	@FXML
	private CheckBox caseS;
	
	private TextArea area;
	
	@FXML
	public void initialize() {
		result.setText("");
		findField.setPromptText(ThinkStr.to_find.toString());
		replaceWithField.setPromptText(ThinkStr.to_replace_with.toString());
		nextB.setText(ThinkStr.next.toString());
		replaceB.setText(ThinkStr.replace.toString());
		replaceAllB.setText(ThinkStr.replace_all.toString());
		caseS.setText(ThinkStr.case_sensitive.toString());
		super.initialize();
	}
	
	public int next() {
		int begin = area.getCaretPosition();
		area.end();
		int end = area.getCaretPosition();
		int length = findField.getLength();
		String toFind = findField.getText();
		String word = null;
		int i = 0;
		Boolean found = false;
		if (length != 0) {
			for (i = begin; (i <= end) && (!found); ++i) {
				if (i + length <= end)
					word = area.getText(i, i + length);
				else
					break;
				if (caseS.isSelected())
					found = toFind.equals(word);
				else
					found = toFind.equalsIgnoreCase(word);
			}
		}
		if (found) {
			result.setText("");
			area.selectRange(i - 1, i + length - 1);
			return i + length - 1;
		}
		else {
			area.positionCaret(begin);
			area.deselect();
			result.setText(ThinkStr.not_found.toString());
			return begin;
		}
	}
	
	public int replace() {
		int currentPos = area.getCaretPosition();
		int pos = next();
		if (currentPos != pos) //means it is found because it will be a selection
			area.replaceSelection(replaceWithField.getText());
		return pos;
	}
	
	public void replaceAll() {
		area.end();
		int currentPos = area.getCaretPosition();
		int endPos = area.getCaretPosition();
		area.positionCaret(0);
		int searchPos = replace();
		while (searchPos != endPos && searchPos != currentPos) {
			currentPos = searchPos;
			searchPos = replace();
		}
	}

	public TextArea getArea() {
		return area;
	}

	public void setArea(TextArea area) {
		this.area = area;
	}
}
