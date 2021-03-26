package sample.kernel.util;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import sample.kernel.entity.Account;
import sample.kernel.entity.Attachment;
import sample.kernel.entity.Idea;
import sample.kernel.entity.Task;
import sample.kernel.entity.WorkNote;

// A set of methods that help with the configuration of the Hibernate framework

public class DBConnection implements Serializable {
	private static final long serialVersionUID = -1709608998071809732L;
	private String host;
	private String databaseName;
	private String port;
	private Properties settings = new Properties();
	transient private static SessionFactory sessionFactory;
	transient private static List<ServiceRegistry> serviceRegistries = new ArrayList<ServiceRegistry>();
		
	public DBConnection() {
		settings.put(AvailableSettings.SHOW_SQL, "true");
		settings.put(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "thread");
		settings.put(AvailableSettings.HBM2DDL_AUTO, "update");
	}
	
	public void initialize(@SuppressWarnings("rawtypes") List<Class> annotatedClasses, String mode, String timeZone) {
		Configuration configuration = new Configuration();
		settings.put(AvailableSettings.URL, "jdbc:mysql://" + host + ":" + port + "/" + databaseName
				+ "?useUnicode=true&characterEncoding=utf8&useLegacyDatetimeCode=false&serverTimezone=" + timeZone);
		settings.put(AvailableSettings.HBM2DDL_AUTO, mode);
		configuration.setProperties(settings);
		for (@SuppressWarnings("rawtypes") Class annotatedClass : annotatedClasses)
			configuration.addAnnotatedClass(annotatedClass);
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties()).build();
		serviceRegistries.add(serviceRegistry);
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	}
	
	public static final String dialect_prefix = "sample.kernel.util.dialect.MySQL";

	public void initialize(String host, String port, String databaseName, String username, String password,
			int mySQLVersion, @SuppressWarnings("rawtypes") List<Class> annotatedClasses, String mode, String timeZone) {
		this.host = host;
		this.databaseName = databaseName;
		this.port = port;
		Configuration configuration = new Configuration();
		settings.put(AvailableSettings.DRIVER, "com.mysql.jdbc.Driver");
		settings.put(AvailableSettings.URL, "jdbc:mysql://" + host + ":" + port + "/" + databaseName
				+ "?useUnicode=true&characterEncoding=utf8&useLegacyDatetimeCode=false&serverTimezone=" + timeZone);
		settings.put(AvailableSettings.USER, username);
		settings.put(AvailableSettings.PASS, password);
		settings.put(AvailableSettings.DIALECT,
				dialect_prefix + Integer.toString(mySQLVersion) + "Dialect");
		settings.put(AvailableSettings.SHOW_SQL, "true");
		settings.put(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "thread");
		settings.put(AvailableSettings.HBM2DDL_AUTO, mode);
		configuration.setProperties(settings);
		for (@SuppressWarnings("rawtypes") Class annotatedClass : annotatedClasses)
			configuration.addAnnotatedClass(annotatedClass);
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties()).build();
		serviceRegistries.add(serviceRegistry);
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public static Session openSession() {
		Session session = DBConnection.getSessionFactory().openSession();
		return session;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public void setSettings(Properties settings) {
		this.settings = settings;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public Properties getSettings() {
		return settings;
	}

	public static Boolean isDBConnected() {
		return (getSessionFactory() != null);
	}

	public void destroySettings() {
		host = null;
		databaseName = null;
		port = null;
		settings = new Properties();
	}

	public static void closeSessionFactories() {
		if (isDBConnected()) {
			for (ServiceRegistry serviceRegistry : serviceRegistries) {
				if (serviceRegistry != null) StandardServiceRegistryBuilder.destroy(serviceRegistry);
			}
			sessionFactory = null;
		}
	}

	@SuppressWarnings("rawtypes")
	public static List<Class> getAnnotatedClasses() {
		List<Class> annotatedClasses = new ArrayList<Class>();
		annotatedClasses.add(Account.class);
		annotatedClasses.add(Attachment.class);
		annotatedClasses.add(Idea.class);
		annotatedClasses.add(Task.class);
		annotatedClasses.add(WorkNote.class);
		return annotatedClasses;
	}
}
