package sample.kernel.entity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import sample.kernel.util.Notification;
import sample.kernel.util.ThinkStr;
import sample.kernel.util.ThinkUtil;

@Entity
@Table(name="ideas")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "ideaTask")
public class Idea implements Serializable, Comparable<Idea> {
	private static final long serialVersionUID = 479056088301117866L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ideaID")
	private int id;
	@Column(name = "ideaTitle")
	private String title;
	@Column(name = "ideaDesc", length = ThinkUtil.maxContentSize)
	private String desc;
	@Column(name = "ideaTime")
	private LocalDateTime time;
	@Column(name = "ideaColor")
	private String color = "transparent";
	@Column(name = "ideaWNotify")
	private Boolean notifyWN = false;
	
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	private Set<Idea> relatedIdeas= new HashSet<Idea>();
	@ManyToMany(mappedBy = "relatedIdeas", fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	private List<Idea> parentIdeas= new ArrayList<Idea>();
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "idea")
	@Fetch(value = FetchMode.SUBSELECT)
	private List<WorkNote> workNotes= new ArrayList<WorkNote>();
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "idea")
	@Fetch(value = FetchMode.SUBSELECT)
	private List<Attachment> attachments = new ArrayList<Attachment>();
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Idea other = (Idea) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/**
	 * @return the ID from the result of toString
	 */
	public static int getId(String str) {
		int id = -1;
		String[] string = str.split(" ");
		try {
			id = Integer.parseInt(string[0]);
		}
		catch (java.lang.NumberFormatException e) {
			
		}
		return id;
	}

	public Idea(String title, String desc, LocalDateTime time, List<Idea> relatedIdeas, Map<File,Boolean> attachments) {
		//setId(ThinkUtil.countIdea());
		this.setTitle(title);
		this.setDesc(desc);
		this.time = time;
		for (Idea idea : relatedIdeas) addRelatedIdea(idea);
		for (File attach : attachments.keySet())
			this.attachments.add(new Attachment(attach.getPath(), attachments.get(attach), this));
	}
	
	public void set(String title, String desc, LocalDateTime time) {
		setTitle(title);
		setDesc(desc);
		setTime(time);
	}
	
	public void set(Idea idea) {
		setTitle(idea.getTitle());
		setDesc(idea.getDesc());
		setTime(idea.getTime());
		attachments.clear();
		clearRelatedIdeas();
		workNotes.clear();
		for (Attachment attach : idea.getAttachments()) {
			attach.setIdea(this);
			attach.setId(0);
			attachments.add(attach);
		}
		
		for (Idea ideaElem : idea.getRelatedIdeas()) {
			if (! ideaElem.equals(this))
				addRelatedIdea(ideaElem);
		}
		
		for (WorkNote wN : idea.getWorkNotes()) {
			wN.setIdea(this);
			wN.setId(0);
			workNotes.add(wN);
		}
	}

	public Idea() {
		
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return id + " (" + title + ")";
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public LocalDateTime getTime() {
		return time;
	}
	
	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public Double getWorkDegree() {
		if (workNotes.isEmpty()) return (-1.0);
		return (double) (workNotes.stream().filter(workNote -> workNote.isDone())
				.collect(Collectors.toList()).size()) / workNotes.size();
	}

	public Boolean isDone() {
		if (getWorkDegree() == - 1) return null;
		else if (getWorkDegree() == 1) return true;
		else return false;
	}
	
	public void addWorkNote(WorkNote wN) {
		workNotes.add(wN);
	}
	
	public void removeWorkNote(WorkNote wN) {
		//wN.removeIdea();
		workNotes.remove(wN);
	}
	
	public void clearWNS() {
		List<WorkNote> wNs = new ArrayList<WorkNote>();
		for (WorkNote wN : wNs) {
			removeWorkNote(wN);
		}
	}
	
	public void addRelatedIdea(Idea idea) {
		if (!relatedIdeas.contains(idea)) {
			relatedIdeas.add(idea);
			idea.addParentIdea(this);
		}
	}
	
	public void addParentIdea(Idea idea) {
		if (!parentIdeas.contains(idea)) {
			parentIdeas.add(idea);		
		}
	}
	
	public void removeRelatedIdea(Idea idea) {
		relatedIdeas.remove(idea);
		idea.removeParentIdea(this);
	}
	
	public void clearRelatedIdeas() {
		Set<Idea> relatedIdeas = new HashSet<Idea>(getRelatedIdeas());
		for (Idea ideaRelated : relatedIdeas)
			removeRelatedIdea(ideaRelated);
	}
	
	public void removeParentIdea(Idea idea) {
		parentIdeas.remove(idea);
	}
	
	public void addAttachment(Attachment attach) {
		attachments.add(attach);
	}
	
	public void removeAttachment(Attachment attach) {
		attachments.remove(attach);
	}

	public Set<Idea> getRelatedIdeas() {
		return relatedIdeas;
	}

	public List<WorkNote> getWorkNotes() {
		return workNotes;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}
	
	public List<Attachment> getAttachsToUpload() {
		return attachments.stream().filter(attach -> attach.isToUpload()).collect(Collectors.toList());
	}
	
	public String getInfo() {
		return ThinkStr.id + " (" +ThinkStr.title + "): " + toString() + "\n" +
				ThinkStr.date_and_time + ": " + getTime().format(DateTimeFormatter.ofPattern(ThinkUtil.datetimeFormatter)) + "\n" +
				ThinkStr.description + ": \n \t" + getDesc();
	}
	
	/**JavaFX**/
	public String getDegreeColor() {
		if (getWorkDegree() == -1) return "#d0d0d0";
		int v = (int) Math.round(((1183466976 - 16777215) * (getWorkDegree() / 100)) + 16777215);
		return "#" + Integer.toHexString(v);
	}
	
	public SimpleObjectProperty<Integer> getIdProperty() {
		return new SimpleObjectProperty<Integer>(id);
	}
	
	public StringProperty getTitleProperty() {
		return new SimpleStringProperty(title);
	}
	
	public StringProperty getTaskProperty() {
		String str = "";
		if (this instanceof Task) str = "✓";
		return new SimpleStringProperty(str);
	}
	
	public StringProperty getTimeProperty() {
		return new SimpleStringProperty(time.format(DateTimeFormatter.ofPattern(ThinkUtil.datetimeFormatter)));
	}
	
	public StringProperty getStartTimeProperty() {
		return new SimpleStringProperty("");
	}
	
	public StringProperty getFinishTimeProperty() {
		return new SimpleStringProperty("");
	}
	
	/**Import & Export**/
	public void exportIdea(File file) {
		try {
			ObjectOutputStream outIdea = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(file)));
			outIdea.writeObject(this);
			outIdea.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void importIdea(File file) {
		ObjectInputStream inIdea;
		try {
			inIdea = new ObjectInputStream(new BufferedInputStream(
					new FileInputStream(file)));
			Idea idea = (Idea) inIdea.readObject();
			this.set(idea);
			inIdea.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setTitleDocx(XWPFParagraph paragraph, String title) {
		XWPFRun tmpRun = paragraph.createRun();
		tmpRun.setFontFamily("Segoe UI");
		tmpRun.setFontSize(14);
		tmpRun.setItalic(true);
		tmpRun.setBold(true);
		tmpRun.setText(title);
		tmpRun.addCarriageReturn();
	}
	
	private void setContentDocx(XWPFParagraph paragraph, String content) {
		XWPFRun tmpRun = paragraph.createRun();
		tmpRun.setFontFamily("Segoe UI");
		tmpRun.setFontSize(12);
		tmpRun.setItalic(false);
		tmpRun.setBold(false);
		tmpRun.addTab();
		String[] textLines = content.split(System.lineSeparator());
		for (String s : textLines) {
			tmpRun.setText(s);
			tmpRun.addCarriageReturn();
		}
		tmpRun.addCarriageReturn();
	}
	
	private void setParagraph(XWPFParagraph paragraph) {
		CTP ctp = paragraph.getCTP();
		CTPPr ctppr = ctp.getPPr();
		if (ctppr == null) ctppr = ctp.addNewPPr();
		ctppr.addNewBidi().setVal(STOnOff.ON);
		if (ThinkUtil.getLang().isRTL())
			paragraph.setAlignment(ParagraphAlignment.LEFT);
		else
			paragraph.setAlignment(ParagraphAlignment.RIGHT);
	}
	
	private void setTable(XWPFTable table) {
		if (table.getCTTbl().getTblPr() == null) {
			table.getCTTbl().addNewTblPr().addNewBidiVisual().setVal(STOnOff.ON);
		} else {
			table.getCTTbl().getTblPr().addNewBidiVisual().setVal(STOnOff.ON);
		}
	}
	
	private void setTableRow(XWPFTableRow tableRow) {
		for (int col = 0 ; col < tableRow.getTableCells().size() ; col++) {
			XWPFParagraph paragraph = tableRow.getCell(col).getParagraphs().get(0);
			CTP ctp = paragraph.getCTP();
			CTPPr ctppr = ctp.getPPr();
			if (ctppr == null) ctppr = ctp.addNewPPr();
			ctppr.addNewBidi().setVal(STOnOff.ON);
		}
	}
	
	public void exportDoc(File file) {
		try {
			XWPFDocument document = new XWPFDocument();
			XWPFHeader header = document.createHeader(HeaderFooterType.FIRST);
			XWPFFooter footer = document.createFooter(HeaderFooterType.DEFAULT);
			
			XWPFParagraph dateParagraph = header.createParagraph();
			if (ThinkUtil.getLang().isRTL())
				dateParagraph.setAlignment(ParagraphAlignment.RIGHT);
			else
				dateParagraph.setAlignment(ParagraphAlignment.LEFT);
			CTP ctp = dateParagraph.getCTP();
			CTPPr ctppr = ctp.getPPr();
			if (ctppr == null) ctppr = ctp.addNewPPr();
			ctppr.addNewBidi().setVal(STOnOff.ON);
			XWPFRun tmpRun = dateParagraph.createRun();
			tmpRun.setFontSize(9);
			tmpRun.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern(ThinkUtil.datetimeFormatter)));
			tmpRun.setBold(true);
			
			XWPFParagraph thinkParagraph = footer.createParagraph();
			ctp = thinkParagraph.getCTP();
			ctppr = ctp.getPPr();
			if (ctppr == null) ctppr = ctp.addNewPPr();
			ctppr.addNewBidi().setVal(STOnOff.ON);
			if (ThinkUtil.getLang().isRTL())
				thinkParagraph.setAlignment(ParagraphAlignment.RIGHT);
			else
				thinkParagraph.setAlignment(ParagraphAlignment.LEFT);
			tmpRun = thinkParagraph.createRun();
			tmpRun.setFontSize(9);
			tmpRun.setText(ThinkStr.thinkBAY.toString());
			tmpRun.setBold(true);
			
			XWPFParagraph ideaParagraph = document.createParagraph();
			setParagraph(ideaParagraph);		
			
			setTitleDocx(ideaParagraph, ThinkStr.id.toString() + ": ");
			setContentDocx(ideaParagraph, String.valueOf(id));
			
			setTitleDocx(ideaParagraph, ThinkStr.title.toString() + ": ");
			setContentDocx(ideaParagraph, getTitle());
			
			setTitleDocx(ideaParagraph, ThinkStr.work_degree.toString() + ": ");
			setContentDocx(ideaParagraph, (getWorkDegree() * 100) + "%");
			
			setTitleDocx(ideaParagraph, ThinkStr.date_and_time.toString() + ": ");
			setContentDocx(ideaParagraph, getTime().format(DateTimeFormatter.ofPattern(ThinkUtil.datetimeFormatter)));
			
			if (this instanceof Task) {
				Task task = (Task) this;
				setTitleDocx(ideaParagraph, ThinkStr.startDate.toString() + ": ");
				setContentDocx(ideaParagraph, task.getStartTime().format(DateTimeFormatter.ofPattern(ThinkUtil.datetimeFormatter)));
				setTitleDocx(ideaParagraph, ThinkStr.finishDate.toString() + ": ");
				setContentDocx(ideaParagraph, task.getFinishTime().format(DateTimeFormatter.ofPattern(ThinkUtil.datetimeFormatter)));
			}
			
			setTitleDocx(ideaParagraph, ThinkStr.description.toString() + ": ");
			setContentDocx(ideaParagraph, getDesc());
			
			if (! getRelatedIdeas().isEmpty()) {
				XWPFParagraph ideasRelatedParagraph = document.createParagraph();
				setParagraph(ideasRelatedParagraph);
				tmpRun = ideasRelatedParagraph.createRun();
				tmpRun.addBreak(BreakType.PAGE);
				setTitleDocx(ideasRelatedParagraph, ThinkStr.related_ideas.toString() + ": ");
				for (Idea idea : getRelatedIdeas()) {
					setContentDocx(ideasRelatedParagraph, idea.toString());
				}
			}
			
			if (! getAttachments().isEmpty()) {
				XWPFParagraph attachsParagraph = document.createParagraph();
				setParagraph(attachsParagraph);
				tmpRun = attachsParagraph.createRun();
				tmpRun.addBreak(BreakType.PAGE);
				setTitleDocx(attachsParagraph, ThinkStr.attachments.toString() + ": ");
				XWPFTable table = document.createTable();
				setTable(table);
			    XWPFTableRow tableRowOne = table.getRow(0);
			    tableRowOne.getCell(0).setText(ThinkStr.id.toString());
			    tableRowOne.addNewTableCell().setText(ThinkStr.local_path.toString());
			    tableRowOne.addNewTableCell().setText(ThinkStr.extern_path.toString());
			    setTableRow(tableRowOne);
			    for (Attachment attach : getAttachments()) {
			        XWPFTableRow tableRow = table.createRow();
			        tableRow.getCell(0).setText(String.valueOf(attach.getId()));
			        tableRow.getCell(1).setText(attach.getLocalURL());
			        tableRow.getCell(2).setText(attach.getExternURL());
			        setTableRow(tableRow);
			    }
			}
			
			if (! getWorkNotes().isEmpty()) {
				XWPFParagraph wNsParagraph = document.createParagraph();
				setParagraph(wNsParagraph);
				tmpRun = wNsParagraph.createRun();
				tmpRun.addBreak(BreakType.PAGE);
				setTitleDocx(wNsParagraph, ThinkStr.workNotes.toString() + ": ");
				XWPFTable table = document.createTable();
				setTable(table);
			    XWPFTableRow tableRowOne = table.getRow(0);
			    tableRowOne.getCell(0).setText(ThinkStr.id.toString());
			    tableRowOne.addNewTableCell().setText(ThinkStr.title.toString());
			    tableRowOne.addNewTableCell().setText(ThinkStr.date_and_time.toString());
			    tableRowOne.addNewTableCell().setText(ThinkStr.done.toString());
			    setTableRow(tableRowOne);
			    for (WorkNote wN : getWorkNotes()) {
			        XWPFTableRow tableRow = table.createRow();
			        tableRow.getCell(0).setText(String.valueOf(wN.getId()));
			        tableRow.getCell(1).setText(wN.getTitle());
			        tableRow.getCell(2).setText(wN.getDate().format(DateTimeFormatter.ofPattern(ThinkUtil.datetimeFormatter)));
			        setTableRow(tableRow);
			        String done = "";
			        if (wN.getDone())
			        	done = "✓";
			        tableRow.getCell(3).setText(done);
			        tableRow = table.createRow();
					tableRow.getCell(0).setText(wN.getNote());
					setMergeCell(tableRow, 0, true);
					setMergeCell(tableRow, 1, false);
					setMergeCell(tableRow, 2, false);
					setMergeCell(tableRow, 3, false);
			    }
			}
			
			document.write(new FileOutputStream(file));
			document.close();
			Notification.addNotification("");
		} catch (Exception e) {
			Notification.addNotification("", true);
			e.printStackTrace();
		}
	}
	
	private void setMergeCell(XWPFTableRow tableRow, int cell, Boolean stMerge) {
		CTHMerge hMerge = CTHMerge.Factory.newInstance();
		if (stMerge)
			hMerge.setVal(STMerge.RESTART);
		else
			hMerge.setVal(STMerge.CONTINUE);
		CTTcPr cttcpr = tableRow.getCell(cell).getCTTc().getTcPr();
		if (cttcpr == null)
			tableRow.getCell(cell).getCTTc().addNewTcPr().setHMerge(hMerge);
		else
			cttcpr.setHMerge(hMerge);
		XWPFParagraph paragraph = tableRow.getCell(cell).getParagraphs().get(0);
		CTP ctp = paragraph.getCTP();
		CTPPr ctppr = ctp.getPPr();
		if (ctppr == null) ctppr = ctp.addNewPPr();
		ctppr.addNewBidi().setVal(STOnOff.ON);
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Boolean getNotifyWN() {
		return notifyWN;
	}

	public void setNotifyWN(Boolean notifyWN) {
		this.notifyWN = notifyWN;
	}

	@Override
	public int compareTo(Idea o) {
		return Integer.valueOf(getId()).compareTo(Integer.valueOf(o.getId()));
	}
	
	public Boolean isEmpty() {
		return (getTitle() == null && getDesc() == null && getTime() == null);
	}
}
