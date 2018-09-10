package it.giunti.apg.core.persistence;


import java.io.Serializable;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

public class GenericDao {

//	private static final Logger LOG = LoggerFactory.getLogger(GenericDao.class);
	
	private GenericDao() {}
	
	public static void updateGeneric(Session ses, Serializable key, Object instance) throws HibernateException {
		Object persistent = ses.get(instance.getClass(), key);
		try {
			PropertyUtils.copyProperties(persistent, instance);
		} catch (Exception e) {
			throw new HibernateException(e);
		}
		ses.update(persistent);
	}
	
	public static Serializable saveGeneric(Session ses, Object transientInstance) throws HibernateException {
		ses.setFlushMode(FlushMode.ALWAYS);
		//ses.evict(transientInstance);//Rimuove dalla cache eventuali altri oggetti con identico id
		Serializable key = ses.save(transientInstance);
		return key;
	}

	public static void deleteGeneric(Session ses, Serializable id, Object instance) throws HibernateException {
		@SuppressWarnings("rawtypes")
		Class instanceClass = instance.getClass();
		String qs = "delete from "+instanceClass.getSimpleName()+" where id=:id";
		Query q = ses.createQuery(qs);
		if (id instanceof Integer) {
			q.setParameter("id", id, IntegerType.INSTANCE);
			q.executeUpdate();
			return;
		}
		if (id instanceof String) {
			q.setParameter("id", id, StringType.INSTANCE);
			q.executeUpdate();
			return;
		}
		throw new HibernateException("Delete: id is not Integer nor String type");
	}

	@SuppressWarnings("unchecked")
	public static <S> S findById(Session ses, Class<S> findClass, Serializable key) throws HibernateException {
		S obj = (S)ses.get(findClass, key);
		return obj;
	}

	@SuppressWarnings("unchecked")
	public static <S> List<S> findByProperty(Session ses, Class<S> findClass, String propertyName, Object value) throws HibernateException {
		List<S> listS = null;
		QueryFactory qf = new QueryFactory(ses, "from " + findClass.getCanonicalName() + " o");
		qf.addWhere("o." + propertyName + " = :p1");
		qf.addParam("p1", (Serializable) value);
		Query q = qf.getQuery();
		listS = (List<S>) q.list();
		return listS;
	}
	
	@SuppressWarnings("unchecked")
	public static <S> List<S> findByPropertyIgnoreCase(Session ses, Class<S> findClass, String propertyName, String value) throws HibernateException {
		List<S> listS = null;
		QueryFactory qf = new QueryFactory(ses, "select o from "
				+ findClass.getCanonicalName() + " o");
		qf.addWhere("o." + propertyName + " like :p1");
		qf.addParam("p1", value);
		Query q = qf.getQuery();
		listS = (List<S>) q.list();
		return listS;
	}
	
	@SuppressWarnings("unchecked")
	public static <S> List<S> findByClass(Session ses, Class<S> findClass, String orderProperty) throws HibernateException {
		List<S> listS = null;
		String hql = "select o from " + findClass.getCanonicalName() + " as o " +
				"order by o."+orderProperty+" asc";
		Query q = ses.createQuery(hql);
		listS = (List<S>) q.list();
		return listS;
	}
	
	@SuppressWarnings("unchecked")
	public static <S> S findUniqueResult(Session ses, Class<S> findClass, String propertyName, Object value) throws NonUniqueResultException {
		S obj = null;
		QueryFactory qf = new QueryFactory(ses, "select o from "
				+ findClass.getCanonicalName() + " o");
		qf.addWhere("o." + propertyName + " = :p1");
		qf.addParam("p1", (Serializable) value);
		Query q = qf.getQuery();
		obj = (S) q.uniqueResult();
		return obj;
	}

}