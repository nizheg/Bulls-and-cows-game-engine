package info.nizheg.game;

import info.nizheg.game.entity.User;
import info.nizheg.game.management.UserManager;

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

public class UserManagementServlet extends HttpServlet {

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
			createUser(req, resp);
		} else if (DELETE_ACTION.equals(action)) {
			deleteUser(req, resp);
		} else if (GET_ALL_ACTION.equals(action)) {
			getAllUsers(req, resp);
		} else if (GET_ACTION.equals(action)) {
			getUser(req, resp);
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	private void createUser(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String userName = req.getParameter("name");
		UserManager userManager = UserManager.instance();
		User user = new User();
		user.setName(userName);
		User createdUser = userManager.addUser(user);
		if (createdUser == null)
			return;
		JSONObject obj = createdUser.toJSON();
		writeObject(resp, obj);
	}

	private void deleteUser(HttpServletRequest req, HttpServletResponse resp) {
		String userUid = req.getParameter("uid");
		UserManager userManager = UserManager.instance();
		userManager.deleteUser(userUid);
	}

	private void getUser(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String userUid = req.getParameter("uid");
		UserManager userManager = UserManager.instance();
		User user = userManager.getUser(userUid);
		if (user == null)
			return;
		writeObject(resp, user.toJSON());
	}

	private void getAllUsers(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserManager userManager = UserManager.instance();
		List<User> users = userManager.getAllUsers();
		Collection<JSONObject> jsonUsers = new ArrayList<JSONObject>();
		for (User user : users) {
			jsonUsers.add(user.toJSON());
		}
		JSONArray array = new JSONArray(jsonUsers);
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
