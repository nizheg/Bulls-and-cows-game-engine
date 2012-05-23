package info.nizheg.game;

import info.nizheg.game.entity.Answer;
import info.nizheg.game.entity.Task;
import info.nizheg.game.entity.User;
import info.nizheg.game.management.TaskManager;
import info.nizheg.game.management.UserManager;
import info.nizheg.game.processing.AnswerProcessor.ProcessingException;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

public class GameServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private String callback;
	private static final Logger logger = Logger.getLogger(GameServlet.class
			.getName());

	Properties properties = new Properties();
	{
		properties
				.put("incorrect.user",
						"Попытка идентификации провалилась. Введите ваш идентификационный код.");
		properties.put("incorrect.task",
				"Произошла попытка обратиться к несуществующему заданию");
		properties.put("word.incorrect.length",
				"Ваше слово имеет неверную длину");
		properties.put("word.duplicate.chars",
				"Ваше слово содержит дублирующиеся буквы");
		properties.put("word.nonExistent", "Вашего слова нет в словаре");
		properties.put("word.empty", "Необходимо ввести слово");
		properties.put("word.duplicate", "Вы уже проверяли это слово");
		properties.put("error.unexpected", "Произошел непредвиденный сбой");
	}

	private String getMessage(String code) {
		return properties.getProperty(code);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			callback = req.getParameter("callback");
			if (callback == null || callback.isEmpty())
				return;

			Game game = createGame(req, resp);
			if (game == null) {
				return;
			}

			String word = req.getParameter("word");
			if (word != null && !word.trim().isEmpty()) {
				try {
					game.processWord(word);
				} catch (ProcessingException ex) {
					writeError(resp, getMessage(ex.getMessage()));
					return;
				}
			}

			JSONObject result = generateResponseObject(game);
			writeResponse(resp, result);
		} catch (Exception ex) {
			logger.severe(ex.getMessage());
			ex.printStackTrace();
			writeError(resp, getMessage("error.unexpected"));
		}
	}

	private Game createGame(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		HttpSession session = req.getSession();

		String userId = req.getParameter("user");
		User user = (User) session.getAttribute("user");
		boolean isUserIdEmpty = userId == null || userId.isEmpty();
		if (user == null || !isUserIdEmpty && !userId.equals(user.getId())) {
			user = null;
			user = UserManager.instance().getUser(userId);
			session.setAttribute("user", user);
		}

		if (user == null) {
			Map<String, Object> additionalParameters = new TreeMap<String, Object>();
			additionalParameters.put("isUserError", true);
			writeError(resp, getMessage("incorrect.user"), additionalParameters);
			return null;
		}

		String taskId = req.getParameter("task");
		Task task = TaskManager.instance().getTask(taskId);
		if (task == null) {
			writeError(resp, getMessage("incorrect.task"));
			return null;
		}

		return new Game(task, user);
	}

	private JSONObject generateResponseObject(Game game) {
		Collection<Answer> answers = game.getAnswers();
		boolean isWordGuessed = false;
		List<JSONObject> jsonAnswers = new ArrayList<JSONObject>(answers.size());
		int number = 0;
		for (Answer answer : answers) {
			jsonAnswers.add(answer.toJSON());
			if (game.isWordGuessed(answer)) {
				isWordGuessed = true;
				number = answer.getNumber();
			}
		}
		JSONObject result = new JSONObject();
		try {
			if (isWordGuessed) {
				String resultCode = game.generateCodeAnswer(number);
				result.put("code", resultCode);
			}
			result.put("answers", new JSONArray(jsonAnswers));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	private void writeError(HttpServletResponse resp, String message,
			Map<String, Object> additionalParameters) throws IOException {
		try {
			JSONObject json = new JSONObject();
			json.put("errorMessage", message);
			if (additionalParameters != null) {
				for (Entry<String, Object> additionalParameter : additionalParameters
						.entrySet()) {
					json.put(additionalParameter.getKey(),
							additionalParameter.getValue());
				}
			}
			writeResponse(resp, json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void writeError(HttpServletResponse resp, String message)
			throws IOException {
		writeError(resp, message, null);
	}

	private void writeResponse(HttpServletResponse resp, JSONObject obj)
			throws IOException {
		try {
			resp.setCharacterEncoding("UTF-16");
			resp.setContentType("application/json");
			if (callback == null || callback.isEmpty())
				throw new IllegalStateException("Illegal callback " + callback);
			Writer writer = resp.getWriter();
			writer.write(callback + "(");
			obj.write(writer);
			writer.write(")");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
