package it.giunti.apg.core.persistence;

import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.LogDeletion;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;

public class LogDeletionDao implements BaseDao<LogDeletion> {


	@Override
	public void update(Session ses, LogDeletion instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, LogDeletion transientInstance)
			throws HibernateException {
		Serializable key = ses.save(transientInstance);
		return key;
	}
	
	@Override
	public void delete(Session ses, LogDeletion instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("rawtypes")
	public static void writeDeletionLog(Session ses, Class entityClass, Integer entityId, String entityUid, String idUtente)
			throws HibernateException {
		if ((entityClass == null) || (entityId == null) || (idUtente == null)) throw new HibernateException("Impossibile scrivere un ediLog, parametri mancanti");
		if (idUtente.length() == 0) throw new HibernateException("Impossibile scrivere un deletionLog, parametri mancanti");
		LogDeletion log = new LogDeletion();
		log.setLogDatetime(DateUtil.now());
		String entityName = entityClass.getSimpleName();
		log.setEntityName(entityName);
		log.setEntityId(entityId);
		log.setEntityUid(entityUid);
		log.setIdUtente(idUtente);
		ses.save(log);
	}
	
	public List<LogDeletion> findByClassNameAndDate(Session ses, String classSimpleName, Date startDt, Date endDt)
			 throws HibernateException {
		if (classSimpleName == null) throw new HibernateException("LogEditing classSimpleName is null");
		if (classSimpleName.equals("") || classSimpleName.equals("null"))
				throw new HibernateException("LogEditing classSimpleName is null");
		String qs = "from LogDeletion as ld where " +
				"ld.entityName = :s1 and " +
				"ld.logDatetime >= :dt1 and " +
				"ld.logDatetime <= :dt2 " +
				"order by dl.logDatetime asc ";
		Query q = ses.createQuery(qs);
		q.setParameter("s1", classSimpleName, StringType.INSTANCE);
		q.setParameter("dt1", startDt, TimestampType.INSTANCE);
		q.setParameter("dt2", endDt, TimestampType.INSTANCE);
		@SuppressWarnings("unchecked")
		List<LogDeletion> ldList = (List<LogDeletion>) q.list();
		return ldList;
	}
	
}
