package info.nizheg.game;

import info.nizheg.game.entity.Word;
import info.nizheg.game.management.WordManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WordServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ServletContext context = getServletContext();
		InputStream stream = context.getResourceAsStream("/dictionary");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				stream, "UTF-8"));
		WordManager wordManager = WordManager.instance();
		Collection<Word> words = new ArrayList<Word>(40000);
		String word = reader.readLine();
		while (word != null) {
			if (!word.isEmpty()) {
				words.add(new Word(word));
			}
			word = reader.readLine();
		}
		wordManager.addWords(words);
	}
}
