package sample.kernel.service;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import sample.kernel.entity.Account;
import sample.kernel.util.DBConnection;

public class AccountService {
	
	public static void add(Account account) {
		Session session = DBConnection.openSession();
		Transaction transaction = session.beginTransaction();
		account = session.get(Account.class, account.getId());	
		if (account == null)
				session.save(account);
		transaction.commit();
		session.close();
	}
	
	public static void modify(Account account) {
		Session session = DBConnection.openSession();
		Transaction transaction = session.beginTransaction();
		session.update(account);
		transaction.commit();
		session.close();
	}
	
	public static void delete(Account account) {
		Session session = DBConnection.openSession();
		Transaction transaction = session.beginTransaction();
		account = session.get(Account.class, account.getId());
		if (account != null) {
			session.delete(account);
		}
		transaction.commit();
		session.close();
	}
	
	public static Boolean connect(String password) {
		Account account = null;
		Boolean access = false;
		Transaction transaction = null;
		String address = Account.getAddress();
		Session session = DBConnection.openSession();
		Query<Account> query = session.createQuery("From sample.kernel.entity.Account", Account.class);
		List<Account> accounts = query.list();
		if (accounts.isEmpty()) {
			account = new Account(password).withAdmin().withConnect();
			transaction = session.beginTransaction();
			session.save(account);
			transaction.commit();
			access = true;
		}
		else {
			account = accounts.stream()
			.filter(acc -> acc.getId().equals(address)).findAny().orElse(null);
			if (account != null) {
				account = accounts.stream()
						.filter(acc -> acc.getId().equals(address) && acc.getPassword().equals(Account.crypt(password))).findAny().orElse(null);
				if (account != null) {
					if (account.getAllowed()) {
						Account connectedAccount = accounts.stream()
								.filter(acc -> acc.getConnected() && !acc.getId().equals(address))
								.findAny().orElse(null);
						if (connectedAccount == null) {
							account = account.withConnect().withUpgradedTime();
							transaction = session.beginTransaction();
							session.update(account);
							transaction.commit();
							access = true;
						}
					}
				}
			}
			else {
				account = new Account(password);
				transaction = session.beginTransaction();
				session.save(account);
				transaction.commit();
			}
		}		
		session.close();
		return access;
	}
	
	public static void disconnect() {
		Session session = DBConnection.openSession();
		Transaction transaction = session.beginTransaction();
		Account account = session.get(Account.class, Account.getAddress());
		if (account != null) {
			account.setConnected(false);
			session.update(account);
		}
		transaction.commit();
		session.close();
	}
	
	public static List<Account> requestList() {
		Session session = DBConnection.openSession();
		Query<Account> query = session.createQuery("From sample.kernel.entity.Account", Account.class);
		List<Account> accounts = query.list();
		session.close();
		return accounts;
	}
}
