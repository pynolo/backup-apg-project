package it.giunti.apg.core.persistence;

import it.giunti.apg.shared.model.RinnoviMassivi;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;

public class RinnoviMassiviDao implements BaseDao<RinnoviMassivi> {

	@Override
	public void update(Session ses, RinnoviMassivi instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, RinnoviMassivi transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, RinnoviMassivi instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public List<RinnoviMassivi> findByPeriodico(Session ses,
			Integer idPeriodico) throws HibernateException {
		String hql = "from RinnoviMassivi rm where " +
				"rm.idPeriodico = :id1 "+
				"order by rm.id asc";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		List<RinnoviMassivi> rmList = q.list();
		return rmList;
	}
	
}
