package it.giunti.apg.core.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.DateType;

import it.giunti.apg.shared.model.FattureInvioSap;

public class FattureInvioSapDao implements BaseDao<FattureInvioSap> {


	@Override
	public void update(Session ses, FattureInvioSap instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, FattureInvioSap transientInstance)
			throws HibernateException {
		Serializable key = ses.save(transientInstance);
		return key;
	}
	
	@Override
	public void delete(Session ses, FattureInvioSap instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}

	@SuppressWarnings("unchecked")
	public List<FattureInvioSap> findFattureInvioSap(Session ses, long startDt, long finishDt,
			int offset, int pageSize) throws HibernateException {
		Date startDate = new Date(startDt);
		Date finishDate = new Date(finishDt);
		
		String qs = "from FattureInvioSap fis where " +
				"fis.dataCreazione >= :dt1 and " +
				"fis.dataCreazione <= :dt2 "+
				"order by fis.id desc ";
		Query q = ses.createQuery(qs);
		q.setParameter("dt1", startDate, DateType.INSTANCE);
		q.setParameter("dt2", finishDate, DateType.INSTANCE);
		List<FattureInvioSap> fisList= (List<FattureInvioSap>) q.list();
		if (fisList != null) {
			return fisList;
		} else {
			throw new HibernateException("Nessun invio fattura nell'intervallo");
		}
	}
	
}
