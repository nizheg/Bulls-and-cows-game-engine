package info.nizheg.game.management;

import info.nizheg.game.entity.Answer;
import info.nizheg.game.entity.Task;
import info.nizheg.game.entity.User;

import java.util.List;

import javax.jdo.FetchPlan;
import javax.jdo.PersistenceManager;

public class AnswerManager {

	private static AnswerManager userManager = new AnswerManager();

	private AnswerManager() {
	}

	public static AnswerManager instance() {
		return userManager;
	}

	public List<Answer> getAnswers(User user, Task task) {
		PersistenceManager pm = PMF.manager();
		pm.getFetchPlan().setGroup(FetchPlan.ALL);
		@SuppressWarnings("unchecked")
		List<Answer> answers = (List<Answer>) pm.newQuery(
				"select from " + Answer.class.getName() + " where taskId=="
						+ task.getId() + " && userId==" + user.getId()
						+ " order by number").execute();
		return answers;
	}

	public List<Answer> getAnswers(User user, Task task, String word) {
		PersistenceManager pm = PMF.manager();
		pm.getFetchPlan().setGroup(FetchPlan.ALL);
		@SuppressWarnings("unchecked")
		List<Answer> answers = (List<Answer>) pm.newQuery(
				"select from " + Answer.class.getName() + " where taskId=="
						+ task.getId() + " && userId==" + user.getId()
						+ " && answer=='" + word + "'").execute();
		return answers;
	}

	public Answer addAnswer(Answer answer) {
		PersistenceManager pm = PMF.manager();
		return pm.makePersistent(answer);
	}

}
