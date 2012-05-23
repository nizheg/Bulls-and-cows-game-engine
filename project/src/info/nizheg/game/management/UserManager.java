package info.nizheg.game.management;

import info.nizheg.game.entity.User;

import java.util.List;
import java.util.UUID;

import javax.jdo.FetchPlan;
import javax.jdo.PersistenceManager;

public class UserManager {

	private static UserManager userManager = new UserManager();

	private UserManager() {
	}

	public static UserManager instance() {
		return userManager;
	}

	public List<User> getAllUsers() {
		PersistenceManager pm = PMF.manager();
		pm.getFetchPlan().setGroup(FetchPlan.ALL);
		@SuppressWarnings("unchecked")
		List<User> users = (List<User>) pm.newQuery(
				"select from " + User.class.getName()).execute();
		return users;
	}

	public User getUser(String id) {
		if (!isIdValid(id))
			return null;

		PersistenceManager pm = PMF.manager();

		@SuppressWarnings("unchecked")
		List<User> users = (List<User>) pm.newQuery(
				"select from " + User.class.getName() + " where uid == '" + id
						+ "'").execute();
		if (users.isEmpty()) {
			return null;
		}
		return users.get(0);

	}

	private boolean isIdValid(String id) {
		if (id == null || id.isEmpty()) {
			return false;
		}
		try {
			UUID.fromString(id);
		} catch (IllegalArgumentException ex) {
			return false;
		}
		return true;
	}

	public User addUser(User user) {
		String userUid = user.getUid();
		if (!isIdValid(userUid)) {
			userUid = UUID.randomUUID().toString();
			user.setUid(userUid);
		}
		PersistenceManager pm = PMF.manager();
		return pm.makePersistent(user);
	}

	public void deleteUser(String id) {
		User deletingUser = getUser(id);
		if (deletingUser == null)
			return;
		PersistenceManager pm = PMF.manager();
		pm.deletePersistent(deletingUser);
	}
}
