package it.giunti.apg.server.persistence;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Configures and provides access to Hibernate sessions, tied to the
 * current thread of execution.  Follows the Thread Local Session
 * pattern, see {@link http://hibernate.org/42.html }.
 */
//@SuppressWarnings("deprecation")
public class HibernateSessionFactory {

	private static final String HIBERNATE_CONFIG_FILE="/hibernate.cfg.xml";
	private static final Logger LOG = LoggerFactory.getLogger(HibernateSessionFactory.class);
	/** 
	 * Location of hibernate.cfg.xml file.
	 * Location should be on the classpath as Hibernate uses  
	 * #resourceAsStream style lookup for its configuration file. 
	 * The default classpath location of the hibernate config file is 
	 * in the default package. Use #setConfigFile() to update 
	 * the location of the configuration file for the current session.   
	 */

	private static final ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();
	//private static AnnotationConfiguration configuration = new AnnotationConfiguration();
	private static final org.hibernate.SessionFactory sessionFactory;

	static {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			Configuration conf = new Configuration().configure(HIBERNATE_CONFIG_FILE);
			String connectionUrl = conf.getProperty("hibernate.connection.url");
			LOG.info("HIBERNATE ORM connected to: "+connectionUrl);
			sessionFactory = conf.buildSessionFactory();
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	private HibernateSessionFactory() {
	}

	/**
	 * Returns the ThreadLocal Session instance.  Lazy initialize
	 * the <code>SessionFactory</code> if needed.
	 *
	 *  @return Session
	 *  @throws HibernateException
	 */
	public static Session getSession() throws HibernateException {
		Session session = (Session) threadLocal.get();
		if (session == null || !session.isOpen()) {
			//			if (sessionFactory == null) {
			//				rebuildSessionFactory();
			//			}
			session = (sessionFactory != null) ? sessionFactory.openSession() : null;
			threadLocal.set(session);
		}

		return session;
	}

	//	/**
	//     *  Rebuild hibernate session factory
	//     *
	//     */
	//	public static void rebuildSessionFactory() throws HibernateException {
	//		configuration.configure(configFile);
	//		sessionFactory = configuration.buildSessionFactory();
	//	}
	//
	//	/**
	//     *  Close the single hibernate session instance.
	//     *
	//     *  @throws HibernateException
	//     */
	//    public static void closeSession() throws HibernateException {
	//        Session session = (Session) threadLocal.get();
	//        threadLocal.set(null);
	//
	//        if (session != null) {
	//            session.close();
	//        }
	//    }
	
		/**
	     *  return session factory
	     *
	     */
		public static org.hibernate.SessionFactory getSessionFactory() {
			return sessionFactory;
		}
	
	//	/**
	//     *  return session factory
	//     *
	//     *	session factory will be rebuilded in the next call
	//     */
	//	public static void setConfigFile(String configFile) {
	//		HibernateSessionFactory.configFile = configFile;
	//		sessionFactory = null;
	//	}
	//
	//	/**
	//     *  return hibernate configuration
	//     *
	//     */
	//	public static Configuration getConfiguration() {
	//		return configuration;
	//	}

}