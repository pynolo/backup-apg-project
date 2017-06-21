package it.giunti.apg.core.persistence;

import it.giunti.apg.shared.model.LogEditing;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

public class EditLogDao implements BaseDao<LogEditing> {


	@Override
	public void update(Session ses, LogEditing instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, LogEditing transientInstance)
			throws HibernateException {
		Serializable key = ses.save(transientInstance);
		return key;
	}
	
	@Override
	public void delete(Session ses, LogEditing instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("rawtypes")
	public static void writeEditLog(Session ses, Class entityClass, Integer entityId, String idUtente)
			throws HibernateException {
		if ((entityClass == null) || (entityId == null) || (idUtente == null)) throw new HibernateException("Impossibile scrivere un ediLog, parametri mancanti");
		if (idUtente.length() == 0) throw new HibernateException("Impossibile scrivere un ediLog, parametri mancanti");
		LogEditing log = new LogEditing();
		log.setLogDatetime(new Date());
		String entityName = entityClass.getSimpleName();
		log.setEntityName(entityName);
		log.setEntityId(entityId);
		log.setIdUtente(idUtente);
		ses.save(log);
	}
	
	public List<LogEditing> findByClassNameAndId(Session ses, String classSimpleName, Integer entityId)
			 throws HibernateException {
		if (classSimpleName == null) throw new HibernateException("LogEditing classSimpleName is null");
		if (classSimpleName.equals("") || classSimpleName.equals("null"))
				throw new HibernateException("LogEditing classSimpleName is null");
		String qs = "from LogEditing as el where " +
				"el.entityName = :s1 and " +
				"el.entityId = :i1 " +
				"order by el.logDatetime desc ";
		Query q = ses.createQuery(qs);
		q.setParameter("s1", classSimpleName, StringType.INSTANCE);
		q.setParameter("i1", entityId, IntegerType.INSTANCE);
		@SuppressWarnings("unchecked")
		List<LogEditing> elList = (List<LogEditing>) q.list();
		return elList;
	}
	
}
