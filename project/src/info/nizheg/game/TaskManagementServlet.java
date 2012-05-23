package info.nizheg.game;

import info.nizheg.game.entity.Task;
import info.nizheg.game.management.TaskManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

public class TaskManagementServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String CREATE_ACTION = "create";
	private static final String DELETE_ACTION = "delete";
	private static final String GET_ALL_ACTION = "getAll";
	private static final String GET_ACTION = "get";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-16");
		resp.setContentType("application/json");
		String action = req.getParameter("action");
		if (CREATE_ACTION.equals(action)) {
			createTask(req, resp);
		} else if (DELETE_ACTION.equals(action)) {
			deleteTask(req, resp);
		} else if (GET_ALL_ACTION.equals(action)) {
			getAllTasks(req, resp);
		} else if (GET_ACTION.equals(action)) {
			getTask(req, resp);
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	private void createTask(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String word = req.getParameter("word");
		String code = req.getParameter("code");
		TaskManager userManager = TaskManager.instance();
		Task task = new Task();
		task.setWord(word);
		task.setCode(code);
		Task createdTask = userManager.addTask(task);
		if (createdTask == null)
			return;
		JSONObject obj = createdTask.toJSON();
		writeObject(resp, obj);
	}

	private void deleteTask(HttpServletRequest req, HttpServletResponse resp) {
		String taskUid = req.getParameter("uid");
		TaskManager taskManager = TaskManager.instance();
		taskManager.deleteTask(taskUid);
	}

	private void getTask(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String taskUid = req.getParameter("uid");
		TaskManager taskManager = TaskManager.instance();
		Task task = taskManager.getTask(taskUid);
		if (task == null)
			return;
		writeObject(resp, task.toJSON());
	}

	private void getAllTasks(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		TaskManager taskManager = TaskManager.instance();
		List<Task> tasks = taskManager.getAllTasks();
		Collection<JSONObject> jsonTasks = new ArrayList<JSONObject>();
		for (Task task : tasks) {
			jsonTasks.add(task.toJSON());
		}
		JSONArray array = new JSONArray(jsonTasks);
		writeObject(resp, array);
	}

	private void writeObject(HttpServletResponse resp, JSONObject obj)
			throws IOException {
		try {
			obj.write(resp.getWriter());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void writeObject(HttpServletResponse resp, JSONArray obj)
			throws IOException {
		try {
			obj.write(resp.getWriter());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
