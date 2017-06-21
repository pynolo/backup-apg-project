package it.giunti.apg.core.persistence;

import org.hibernate.Session;

public class SessionFactory {

	public static Session getSession() {
		return HibernateSessionFactory.getSession();
	}
	
//	public static void closeSession() {
//		HibernateSessionFactory.closeSession();
//	}

}