package sample.control.menu;

import java.io.File;

import org.hibernate.cfg.AvailableSettings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import sample.control.Controller;
import sample.kernel.entity.Account;
import sample.kernel.util.DBConnection;
import sample.kernel.util.FTPUtil;
import sample.kernel.util.Language;
import sample.kernel.util.ThinkStr;
import sample.kernel.util.ThinkUtil;

public class SettingsController extends Controller {

	/**Connection**/
    @FXML
    private TitledPane connection;
    @FXML
    private Label ftpLabel;
    @FXML
    private TextField externHost;
    @FXML
    private TextField ftpHost;
    @FXML
    private TextField localPort;
    @FXML
    private TextField externPort;
    @FXML
    private TextField ftpPort;
    @FXML
    private TextField localDB;
    @FXML
    private TextField externDB;
    @FXML
    private PasswordField localPassword;
    @FXML
    private PasswordField externPassword;
    @FXML
    private PasswordField ftpPassword;
    @FXML
    private TextField locarUser;
    @FXML
    private TextField externUser;
    @FXML
    private TextField ftpUser;
    @FXML
    private RadioButton localRadio;
    @FXML
    private ToggleGroup db;
    @FXML
    private RadioButton externRadio;
    @FXML
    private ComboBox<Integer> localVerBox;
    @FXML
    private ComboBox<Integer> externVerBox;
    /**General**/
    @FXML
    private TitledPane general;
    @FXML
    private ComboBox<Language> languageBox;
    @FXML
    private TextField dirField;
    @FXML
    private Button dirButton;
    @FXML
    private ComboBox<Integer> resultsBox;
    @FXML
    private RadioButton trayRadio;
    @FXML
    private RadioButton defaultTheme;
    @FXML
    private RadioButton customTheme;
    @FXML
    private ToggleGroup theme;
    /**Accounts**/
    @FXML
    private TitledPane accounts;
    @FXML
    private TableView<Account> accountsT;
    @FXML
    private TableColumn<Account, String> device;
    @FXML
    private TableColumn<Account, Boolean> allow;
    @FXML
    private TableColumn<Account, String> connected;
    @FXML
    private TableColumn<Account, String> lastVisit;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button passwordButton;
    /**Submit**/
    @FXML
    private Accordion accordion;
    @FXML
    private Button submit;
    @FXML
    private Button reset;
    
    private ThinkMenuController menu;
    
    @FXML
    public void initialize() {
    	reset();
    	super.initialize();
    }
    
    private void general() {
    	accordion.setExpandedPane(general);
    	general.setText(ThinkStr.general.toString());
    	languageBox.setPromptText(ThinkStr.language.toString());
    	dirButton.setText(ThinkStr.choose_ideas_dir.toString());
    	resultsBox.setPromptText(ThinkStr.max_results.toString());
    	resultsBox.setTooltip(new Tooltip(ThinkStr.max_results.toString()));
    	trayRadio.setText(ThinkStr.tray_notifs.toString());
    	customTheme.setText(ThinkStr.custom_theme.toString());
    	defaultTheme.setText(ThinkStr.default_theme.toString());
    	defaultTheme.setSelected(ThinkUtil.getDefaultheme());
		submit.setText(ThinkStr.submit.toString());
    	reset.setText(ThinkStr.reset.toString());
    	
    	languageBox.setItems(FXCollections.observableArrayList(Language.values()));
    	languageBox.setValue(ThinkUtil.getLang());
    	dirField.setText(ThinkUtil.getDirectory().getAbsolutePath());
    	Integer [] results = {10, 20 ,30};
    	resultsBox.setItems(FXCollections.observableArrayList(results));
    	resultsBox.setValue(ThinkUtil.getIdeasPerPage());
    	trayRadio.setSelected(ThinkUtil.getTrayPopup());
    }
    
    private void connection() {
    	connection.setText(ThinkStr.connection.toString());
    	localRadio.setText(ThinkStr.local_db.toString());
    	externRadio.setText(ThinkStr.extern_db.toString());
    	ftpLabel.setText(ThinkStr.ftp.toString());
    	localPort.setPromptText(ThinkStr.port.toString());
    	localDB.setPromptText(ThinkStr.database.toString());
    	locarUser.setPromptText(ThinkStr.user.toString());
    	localPassword.setPromptText(ThinkStr.password.toString());
    	localVerBox.setPromptText(ThinkStr.mysql_version.toString());
    	localVerBox.setTooltip(new Tooltip(ThinkStr.mysql_version.toString()));

    	externHost.setPromptText(ThinkStr.host.toString());
    	externPort.setPromptText(ThinkStr.port.toString());
    	externDB.setPromptText(ThinkStr.database.toString());
    	externUser.setPromptText(ThinkStr.user.toString());
    	externPassword.setPromptText(ThinkStr.password.toString());
    	externVerBox.setPromptText(ThinkStr.mysql_version.toString());
    	externVerBox.setTooltip(new Tooltip(ThinkStr.mysql_version.toString()));
    	
    	ftpHost.setPromptText(ThinkStr.host.toString());
    	ftpPort.setPromptText(ThinkStr.port.toString());
    	ftpUser.setPromptText(ThinkStr.user.toString());
    	ftpPassword.setPromptText(ThinkStr.password.toString());
    	
    	Integer [] versions = {5, 8};
    	localVerBox.setItems(FXCollections.observableArrayList(versions));
    	externVerBox.setItems(FXCollections.observableArrayList(versions));
    	localVerBox.setValue(getMySQLVersion((String) ThinkUtil.getLocalDBConnection().getSettings().get(AvailableSettings.DIALECT)));
    	externVerBox.setValue(getMySQLVersion((String) ThinkUtil.getExternDBConnection().getSettings().get(AvailableSettings.DIALECT)));
    	
    	localRadio.setSelected(ThinkUtil.getLocalConnection());
    	localPort.setText(getString(ThinkUtil.getLocalDBConnection().getPort()));
    	localDB.setText(getString(ThinkUtil.getLocalDBConnection().getDatabaseName()));
    	locarUser.setText(getString(ThinkUtil.getLocalDBConnection().getSettings().getProperty(AvailableSettings.USER)));
    	localPassword.setText(getString(ThinkUtil.getLocalDBConnection().getSettings().getProperty(AvailableSettings.PASS)));
  
    	externRadio.setSelected(!ThinkUtil.getLocalConnection());
    	externHost.setText(getString(ThinkUtil.getExternDBConnection().getHost()));
    	externPort.setText(getString(ThinkUtil.getExternDBConnection().getPort()));
    	externDB.setText(getString(ThinkUtil.getExternDBConnection().getDatabaseName()));
    	externUser.setText(getString(ThinkUtil.getExternDBConnection().getSettings().getProperty(AvailableSettings.USER)));
    	externPassword.setText(getString(ThinkUtil.getExternDBConnection().getSettings().getProperty(AvailableSettings.PASS)));
    	
    	ftpHost.setText(getString(FTPUtil.getServer()));
    	ftpPort.setText(getString(FTPUtil.getPort()));
    	ftpUser.setText(getString(FTPUtil.getUser()));
    	ftpPassword.setText(getString(FTPUtil.getPass()));
    	
    	addListnerNumber(localPort);
    	addListnerNumber(externPort);
    	addListnerNumber(ftpPort);
    }
    
    public void accounts() {
    	accounts.setText(ThinkStr.accounts.toString());
    	device.setText(ThinkStr.device_id.toString());
    	allow.setText(ThinkStr.allowed.toString());
    	connected.setText(ThinkStr.connected.toString());
    	lastVisit.setText(ThinkStr.last_visit.toString());
    	passwordField.setPromptText(ThinkStr.password.toString());
		if (ThinkUtil.userConnected()) 
			passwordButton.setText(ThinkStr.change.toString());
		else
			passwordButton.setText(ThinkStr.enter.toString());
    	
    	device.setCellValueFactory(cellData -> cellData.getValue().getIdProperty());
    	allow.setCellFactory(new Callback<TableColumn<Account, Boolean>, TableCell<Account, Boolean>>() {
			@Override
			public TableCell<Account, Boolean> call(TableColumn<Account, Boolean> column) {
				return new ButtonAllowCell();
			}
		});
    	connected.setCellValueFactory(cellData -> cellData.getValue().getConnectedProperty());
    	lastVisit.setCellValueFactory(cellData -> cellData.getValue().getLastVisitProperty());
    	accountsT.setItems(FXCollections.observableArrayList(ThinkUtil.requestAccountList()));
    }
    
    /**turn the null string to blank**/
    private static String getString(String string) {
    	return ((string == null) ? "" : string);
    }
    
    /**turn the null Integer to blank**/
    private static String getString(Integer integer) {
    	return ((integer == null) ? "" : integer.toString());
    }
    
    private static Integer getMySQLVersion(String dialect) {
    	if (dialect == null)
    		return null;
    	if (dialect.contains("5"))
    		return 5;
    	else
    		if (dialect.contains("8"))
    			return 8;
    	return 8;
    }
    
	private static void addListnerNumber(TextField field) {
		field.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d{0,9}?")) {
					field.setText(oldValue);
				}
			}
		});
	}

    public void dirChoose() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(ThinkUtil.getDirectory());
		directoryChooser.setTitle(ThinkStr.browser_dir.toString());
		File file = directoryChooser.showDialog(general.getScene().getWindow());
		if (file != null) {
			dirField.setText(file.getAbsolutePath());
		}
    }

    public void submit() {
    	ThinkUtil.setLang(languageBox.getValue());
    	ThinkUtil.setDirectory(new File(dirField.getText()));
    	ThinkUtil.setIdeasPerPage(resultsBox.getValue());
    	ThinkUtil.setTrayPopup(trayRadio.isSelected());
    	ThinkUtil.setDefaultheme(defaultTheme.isSelected());
    	
    	ThinkUtil.setLocalConnection(localRadio.isSelected());
		ThinkUtil.getLocalDBConnection().setHost("localhost");
		ThinkUtil.getLocalDBConnection().setPort(localPort.getText());
		ThinkUtil.getLocalDBConnection().setDatabaseName(localDB.getText());
		ThinkUtil.getLocalDBConnection().getSettings().put(AvailableSettings.USER, locarUser.getText());
		ThinkUtil.getLocalDBConnection().getSettings().put(AvailableSettings.PASS, localPassword.getText());
		Integer localVer = (localVerBox.getValue() == null) ? 8 : localVerBox.getValue();
		ThinkUtil.getLocalDBConnection().getSettings().put(AvailableSettings.DIALECT,
				DBConnection.dialect_prefix + localVer + "Dialect");
		ThinkUtil.getLocalDBConnection().setSettings(ThinkUtil.getLocalDBConnection().getSettings());
		
		ThinkUtil.getExternDBConnection().setHost(externHost.getText());
		ThinkUtil.getExternDBConnection().setPort(externPort.getText());
		ThinkUtil.getExternDBConnection().setDatabaseName(externDB.getText());
		ThinkUtil.getExternDBConnection().getSettings().put(AvailableSettings.USER, externUser.getText());
		ThinkUtil.getExternDBConnection().getSettings().put(AvailableSettings.PASS, externPassword.getText());
		Integer externVer = (externVerBox.getValue() == null) ? 8 : externVerBox.getValue();
		ThinkUtil.getExternDBConnection().getSettings().put(AvailableSettings.DIALECT,
				DBConnection.dialect_prefix + externVer + "Dialect");
		ThinkUtil.getExternDBConnection().setSettings(ThinkUtil.getExternDBConnection().getSettings());
		
		FTPUtil.setServer(ftpHost.getText());
		Integer port = (ftpPort.getText().equals("")) ? null : Integer.valueOf(ftpPort.getText());
		FTPUtil.setPort(port);
		FTPUtil.setUser(ftpUser.getText());
		FTPUtil.setPass(ftpPassword.getText());
    	
		ThinkUtil.serialize();
    	ThinkUtil.disconnectAccount();
		
		try {
			FTPUtil.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		DBConnection.closeSessionFactories();		
    	ThinkUtil.refreshDB();
    	
    	getMenu().initialize();
    }
    
    public void reset() {
    	general();
    	accounts();
    	connection();
    }

	public ThinkMenuController getMenu() {
		return menu;
	}

	public void setMenu(ThinkMenuController menu) {
		this.menu = menu;
	}
	
	public void disableEditing(Boolean disable) {
		dirButton.setDisable(disable);
		dirField.setDisable(disable);
		resultsBox.setDisable(disable);
		trayRadio.setDisable(disable);
		accountsT.setDisable(disable);
		if (disable)
			accordion.setExpandedPane(accounts);
	}
	
	public void passwordChange() {
		if (ThinkUtil.userConnected()) {
			Account account = accountsT.getSelectionModel().getSelectedItem();
			if (account != null) {
				account.setPassword(passwordField.getText());
				ThinkUtil.modifyAccount(account);
			}
		}
		else {
			ThinkUtil.connectAccount(passwordField.getText());
			getMenu().initialize();
		}
	}

	private class ButtonAllowCell extends TableCell<Account, Boolean> {
		ToggleButton cellButton = new ToggleButton();

		ButtonAllowCell() {
			cellButton.setText(ThinkStr.allowed.toString());
		}

		@Override
		protected void updateItem(Boolean t, boolean empty) {
			super.updateItem(t, empty);
			if (!empty) {
				Account account = accountsT.getItems().get(getIndex());
				cellButton.setSelected(account.getAllowed());
				cellButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						account.setAllowed(cellButton.isSelected());
						ThinkUtil.modifyAccount(account);
						accounts();
					}
				});
				if (account.getAdmin())
					cellButton.setDisable(true);
				setGraphic(cellButton);
			}
		}
	}
}
