package info.nizheg.game.management;

import info.nizheg.game.entity.Task;

import java.util.List;
import java.util.UUID;

import javax.jdo.FetchPlan;
import javax.jdo.PersistenceManager;

public class TaskManager {

	private static TaskManager taskManager = new TaskManager();

	private TaskManager() {
	}

	public static TaskManager instance() {
		return taskManager;
	}

	public List<Task> getAllTasks() {
		PersistenceManager pm = PMF.manager();
		pm.getFetchPlan().setGroup(FetchPlan.ALL);
		@SuppressWarnings("unchecked")
		List<Task> tasks = (List<Task>) pm.newQuery(
				"select from " + Task.class.getName()).execute();
		return tasks;
	}

	public Task getTask(String id) {
		if (!isIdValid(id))
			return null;

		PersistenceManager pm = PMF.manager();
		@SuppressWarnings("unchecked")
		List<Task> tasks = (List<Task>) pm.newQuery(
				"select from " + Task.class.getName() + " where uid == '" + id
						+ "'").execute();
		if (tasks.isEmpty()) {
			return null;
		}
		return tasks.get(0);

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

	public Task addTask(Task task) {
		String taskUid = task.getUid();
		if (!isIdValid(taskUid)) {
			taskUid = UUID.randomUUID().toString();
			task.setUid(taskUid);
		}
		if (task.getCode() == null || task.getCode().trim().isEmpty()) {
			task.setCode(UUID.randomUUID().toString().replace("-", ""));
		}
		PersistenceManager pm = PMF.manager();
		return pm.makePersistent(task);
	}

	public void deleteTask(String id) {
		Task deletingTask = getTask(id);
		if (deletingTask == null)
			return;
		PersistenceManager pm = PMF.manager();
		pm.deletePersistent(deletingTask);
	}
}
