package sample.kernel.entity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.DatatypeConverter;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import sample.kernel.util.ThinkUtil;

@Entity
public class Account {
	@Id
	@Column(name = "deviceID")
	private String id;
	private String password;
	private LocalDateTime lastVisit;
	private Boolean connected;
	private Boolean allowed;
	private Boolean admin;
	
	@Transient
	public final static String defaultDevice = "ThinkBAY";
	
	public Account(String password) {
		id = getAddress();
		this.setPassword(password);
		setLastVisit(LocalDateTime.now());
		setConnected(false);
		setAllowed(false);
		setAdmin(false);
	}
	
	public Account() {
		
	}
	
	public Account withAdmin() {
		setAdmin(true);
		allowed = true;
		return this;
	}
	
	public Account withConnect() {
		connected = true;
		return this;
	}
	
	public Account withUpgradedTime() {
		setLastVisit(LocalDateTime.now());
		return this;
	}
	
	public static String crypt(String string) {
		String s = string;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(s.getBytes());
			byte[] digest = md.digest();
			s = DatatypeConverter.printHexBinary(digest).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return s;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Account other = (Account) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", lastVisit=" + lastVisit + ", connected=" + connected + "]";
	}

	public Boolean getConnected() {
		return connected;
	}

	public void setConnected(Boolean connected) {
		this.connected = connected;
	}

	public LocalDateTime getLastVisit() {
		return lastVisit;
	}

	public void setLastVisit(LocalDateTime lastVisit) {
		this.lastVisit = lastVisit;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = crypt(password);
	}

	public Boolean getAllowed() {
		return allowed;
	}

	public void setAllowed(Boolean allowed) {
		this.allowed = allowed;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}
	
	/**JavaFX**/
	public StringProperty getIdProperty() {
		return new SimpleStringProperty(getId());
	}
	
	public StringProperty getConnectedProperty() {
		String str = "";
		if (connected) str = "✓";
		return new SimpleStringProperty(str);
	}
	
	public StringProperty getLastVisitProperty() {
		return new SimpleStringProperty(lastVisit.format(DateTimeFormatter.ofPattern(ThinkUtil.datetimeFormatter)));
	}
	
	private static String OS = System.getProperty("os.name").toLowerCase();
	
    public static String getAddress() {
        try {
            if (isWindows()) {
                return getWindowsIdentifier();
            } else if (isMac()) {
                return getMacOsIdentifier();
            } else if (isLinux()) {
                return getLinuxMacAddress();
            } else {
                return defaultDevice;
            }
        } catch (Exception e) {
            return defaultDevice;
        }
    }

    private static boolean isWindows() {
        return (OS.contains("win"));
    }

    private static boolean isMac() {
        return (OS.contains("mac"));
    }

    private static boolean isLinux() {
        return (OS.contains("inux"));
    }

    private static String getLinuxMacAddress() throws FileNotFoundException, NoSuchAlgorithmException {
        File machineId = new File("/var/lib/dbus/machine-id");
        if (!machineId.exists()) {
            machineId = new File("/etc/machine-id");
        }
        if (!machineId.exists()) {
            return defaultDevice;
        }

        Scanner scanner = null;
        try {
            scanner = new Scanner(machineId);
            String id = scanner.useDelimiter("\\A").next();
            return hexStringify(sha256Hash(id.getBytes()));
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private static String getMacOsIdentifier() throws SocketException, NoSuchAlgorithmException {
        NetworkInterface networkInterface = NetworkInterface.getByName("en0");
        byte[] hardwareAddress = networkInterface.getHardwareAddress();
        return hexStringify(sha256Hash(hardwareAddress));
    }

    private static String getWindowsIdentifier() throws IOException, NoSuchAlgorithmException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(new String[] { "wmic", "csproduct", "get", "UUID" });
        String result = null;
        InputStream is = process.getInputStream();
        @SuppressWarnings("resource")
		Scanner sc = new Scanner(process.getInputStream());
        try {
            while (sc.hasNext()) {
                String next = sc.next();
                if (next.contains("UUID")) {
                    result = sc.next().trim();
                    break;
                }
            }
        } finally {
            is.close();
        }

        return result==null?defaultDevice:hexStringify(sha256Hash(result.getBytes()));
    }
    
    private static byte[] sha256Hash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        return messageDigest.digest(data);
    }
    
    private static String hexStringify(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte singleByte : data) {
            stringBuilder.append(Integer.toString((singleByte & 0xff) + 0x100, 16).substring(1));
        }
        return stringBuilder.toString();
    }
}
