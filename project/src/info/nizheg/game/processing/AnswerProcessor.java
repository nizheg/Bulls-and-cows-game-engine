package info.nizheg.game.processing;

import info.nizheg.game.entity.Word;
import info.nizheg.game.management.WordManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AnswerProcessor {

	public static class ProcessingException extends Exception {

		private static final long serialVersionUID = 1L;

		public ProcessingException(String code) {
			super(code);
		}

	}

	private static AnswerProcessor instance = new AnswerProcessor();

	private AnswerProcessor() {
	}

	public static AnswerProcessor instance() {
		return instance;
	}

	public Result calculateWord(String expectedWord, String actualWord)
			throws ProcessingException, IOException {

		if (expectedWord.length() != actualWord.length()) {
			throw new ProcessingException("word.incorrect.length");
		}

		Set<Character> chars = new TreeSet<Character>();
		for (char letter : actualWord.toCharArray()) {
			chars.add(letter);
		}
		if (chars.size() != actualWord.length()) {
			throw new ProcessingException("word.duplicate.chars");
		}

		if (!isWordExistsInDictionary(actualWord)) {
			throw new ProcessingException("word.nonExistent");
		}

		return calculateDifference(expectedWord, actualWord);

	}

	public boolean isWordExistsInDictionary(String word) throws IOException {
		WordManager wordManager = WordManager.instance();
		return wordManager.isWordExist(new Word(word.toLowerCase()));
	}

	private Result calculateDifference(String expectedWord, String actualWord) {
		List<Character> actualWordChars = new ArrayList<Character>(
				actualWord.length());
		for (char letter : actualWord.toCharArray()) {
			actualWordChars.add(letter);
		}
		List<Character> expectedWordChars = new ArrayList<Character>(
				expectedWord.length());
		for (char letter : expectedWord.toCharArray()) {
			expectedWordChars.add(letter);
		}

		int bulls = 0;
		int cows = 0;
		for (Character letter : actualWordChars) {
			if (expectedWordChars.contains(letter)) {
				bulls++;
			}
			if (actualWordChars.indexOf(letter) == expectedWordChars
					.indexOf(letter)) {
				cows++;
			}
		}
		return new Result(bulls, cows);
	}

	public static class Result {
		private final int bulls;
		private final int cows;

		public Result(int bulls, int cows) {
			super();
			this.bulls = bulls;
			this.cows = cows;
		}

		public int getBulls() {
			return bulls;
		}

		public int getCows() {
			return cows;
		}

	}
}
