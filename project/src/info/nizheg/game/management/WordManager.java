package info.nizheg.game.management;

import info.nizheg.game.entity.Word;

import java.util.Collection;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class WordManager {

	private static WordManager instance = new WordManager();

	private WordManager() {
	}

	public static WordManager instance() {
		return instance;
	}

	public boolean isWordExist(Word word) {
		PersistenceManager pm = PMF.manager();
		Query query = pm
				.newQuery("select count(this) from info.nizheg.game.entity.Word where name == expectWord parameters String expectWord");
		return ((Integer) query.execute(word.getName())) > 0;

	}

	public Word addWord(Word word) {
		PersistenceManager pm = PMF.manager();
		return pm.makePersistent(word);
	}

	public void addWords(Collection<Word> words) {
		PersistenceManager pm = PMF.manager();
		pm.makePersistentAll(words);
	}

}
