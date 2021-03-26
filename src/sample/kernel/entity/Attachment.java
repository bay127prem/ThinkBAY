package sample.kernel.entity;

import java.io.File;
import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import sample.control.load.LoadPaneController;
import sample.kernel.util.ThinkStr;
import sample.kernel.util.ThinkUtil;

@Entity
public class Attachment implements Serializable {
	private static final long serialVersionUID = 600588786573256759L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "AttachID")
	private int idAttach;
	private String localURL;
	private String externURL; // null means the file is not to upload
	@ManyToOne
	private Idea idea;
	
	/**
	 * @param localURL the location of the file to upload or to be saved
	 * @param toUpload if the file is to upload, the file will be uploaded by adding it to ThinkUtil.toUploads
	 */
	public Attachment(String localURL, Boolean toUpload, Idea idea) {
		//setId(ThinkUtil.countAttach());
		this.setLocalURL(localURL);
		this.setIdea(idea);
		if (toUpload) {
			toUpload();
		}
	}
	
	public Attachment() {
		
	}
	
	public Boolean isUploading() {
		if (ThinkUtil.getLoadProcess()) 
			if (!ThinkUtil.getToUploadS().isEmpty() && this.equals(ThinkUtil.getToUploadS().getFirst()))
				return true;
			else 
				return false;
		else 
			return false;
	}
	
	public Boolean isDownloading() {
		if (ThinkUtil.getLoadProcess()) 
			if (!ThinkUtil.getToDownloadS().isEmpty() && this.equals(ThinkUtil.getToDownloadS().getFirst()))
				return true;
			else 
				return false;
		else 
			return false;
	}
	
	public Boolean isToUpload() {
		return (externURL != null && !externURL.isBlank());
	}
	
	public void toUpload() {
		if (!isToUpload()) {
			File file = new File(localURL);
			this.setExternURL(UUID.randomUUID().toString() + "_" + file.getName());
		}
		ThinkUtil.addToUpload(this);
	}
	
	public void toDownload() {
		ThinkUtil.addToDownload(this);
	}

	public int getId() {
		return idAttach;
	}
	
	public void setId(int id) {
		this.idAttach = id;
	}

	public String getLocalURL() {
		return localURL;
	}

	public void setLocalURL(String localURL) {
		this.localURL = localURL;
	}

	public String getExternURL() {
		return externURL;
	}

	public void setExternURL(String externURL) {
		this.externURL = externURL;
	}

	public Idea getIdea() {
		return idea;
	}

	public void setIdea(Idea idea) {
		this.idea = idea;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idAttach;
		result = prime * result + ((localURL == null) ? 0 : localURL.hashCode());
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
		Attachment other = (Attachment) obj;
		if (idAttach != other.idAttach)
			return false;
		if (localURL == null) {
			if (other.localURL != null)
				return false;
		} else if (!localURL.equals(other.localURL))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return idAttach + " (" + getLocalURL() + ")  -  (" + getExternURL() + ")";
	}
	
	public String getInfo() {
		return ThinkStr.id + ": " + getId() + "\n" +
				ThinkStr.local_path + ": " + getLocalURL() + "\n" +
				ThinkStr.destination + ": " + getExternURL();
	}

	/**JavaFX**/
	@Transient
	private LoadPaneController paneUploadController;
	@Transient
	private LoadPaneController paneDownloadController;

	public LoadPaneController getUploadController() {
		return paneUploadController;
	}
	
	public LoadPaneController getDownloadController() {
		return paneDownloadController;
	}

	public void setUploadController(LoadPaneController uploadController) {
		this.paneUploadController = uploadController;
	}
	
	public void setDownloadController(LoadPaneController downloadController) {
		this.paneDownloadController = downloadController;
	}
	
	public void setUploadControllerProgress() {
		if (paneUploadController != null) paneUploadController.setProgressVisibility(isUploading());
	}
	
	public void setDownloadControllerProgress() {
		if (paneDownloadController != null) paneDownloadController.setProgressVisibility(isDownloading());
	}
	
	public SimpleObjectProperty<Integer> getIdProperty() {
		return new SimpleObjectProperty<Integer>(idAttach);
	}
	
	public StringProperty getLocalURLProperty() {
		return new SimpleStringProperty(localURL);
	}
	
	public StringProperty getExternURLProperty() {
		return new SimpleStringProperty(externURL);
	}

}
