package info.nizheg.game;

import static java.lang.Math.max;
import static java.lang.Math.min;
import info.nizheg.game.entity.Answer;
import info.nizheg.game.entity.Task;
import info.nizheg.game.entity.User;
import info.nizheg.game.management.AnswerManager;
import info.nizheg.game.processing.AnswerProcessor;
import info.nizheg.game.processing.AnswerProcessor.ProcessingException;
import info.nizheg.game.processing.AnswerProcessor.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game {
	private static int MAX_COUNT = 20;
	private static int ANSWERS_WITHOUT_PENALTY_COUNT = 3;

	private final User user;
	private final Task task;

	private List<Answer> answers = null;

	public Game(Task task, User user) {
		if (task == null) {
			throw new NullPointerException("Task should be not null");
		}
		if (user == null) {
			throw new NullPointerException("User should be not null");
		}
		this.task = task;
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public Task getTask() {
		return task;
	}

	public List<Answer> getAnswers() {
		if (answers == null) {
			answers = new ArrayList<Answer>();
			AnswerManager answerManager = AnswerManager.instance();
			answers.addAll(answerManager.getAnswers(getUser(), getTask()));
		}
		return answers;
	}

	public void processWord(String word) throws IOException,
			ProcessingException {
		word = word.toLowerCase().trim();
		AnswerProcessor processor = AnswerProcessor.instance();
		Answer answer = new Answer(user.getId(), task.getId(), word);
		if (getAnswers().contains(answer))
			throw new ProcessingException("word.duplicate");

		Result result = processor.calculateWord(task.getWord(), word);
		answer.setBulls(result.getBulls());
		answer.setCows(result.getCows());
		answer.setNumber(getAnswers().size() + 1);
		AnswerManager.instance().addAnswer(answer);
		getAnswers().add(answer);
	}

	public boolean isWordGuessed(Answer answer) {
		int wordLength = task.getWord().length();
		return wordLength == answer.getBulls()
				&& wordLength == answer.getCows();

	}

	public String generateCodeAnswer(int answersCount) {
		String code = task.getCode();
		if (code == null || code.isEmpty()) {
			return "";
		}
		int length = max(answersCount - ANSWERS_WITHOUT_PENALTY_COUNT, 0);
		length = min(length, min(code.length(), MAX_COUNT));
		char[] firstPart = new char[length];
		Arrays.fill(firstPart, '*');
		return new String(firstPart) + code.substring(length);
	}

}
