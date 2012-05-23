package info.nizheg.game.management;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

public class PMF {

	private static final PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");

	private static final PersistenceManager pmInstance = pmfInstance
			.getPersistenceManager();

	private PMF() {
	}

	public static PersistenceManagerFactory instance() {
		return pmfInstance;
	}

	public static PersistenceManager manager() {
		return pmInstance;
	}
}
