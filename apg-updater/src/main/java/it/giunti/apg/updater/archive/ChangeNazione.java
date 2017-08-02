package it.giunti.apg.updater.archive;

import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IndirizziDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.Nazioni;
import it.giunti.apg.shared.model.Province;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeNazione {
	
	private static final Logger LOG = LoggerFactory.getLogger(ChangeNazione.class);

	public static void change() 
			throws FileNotFoundException, BusinessException, IOException {
		IndirizziDao indDao = new IndirizziDao();
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			Nazioni scv = GenericDao.findById(ses, Nazioni.class, "VAT");
			List<Indirizzi> scvList = findIndirizzi(ses, "SCV");
			for (Indirizzi ind:scvList) {
				ind.setNazione(scv);
				ind.setProvincia(null);
				indDao.update(ses, ind);
			}
			Nazioni rsm = GenericDao.findById(ses, Nazioni.class, "SMR");
			List<Indirizzi> rsmList = findIndirizzi(ses, "RSM");
			for (Indirizzi ind:rsmList) {
				ind.setNazione(rsm);
				ind.setProvincia(null);
				indDao.update(ses, ind);
			}
			String deleteHql = "delete from Cap c where c.idProvincia = :s1";
			
			Query scvQ = ses.createQuery(deleteHql);
			scvQ.setParameter("s1", "SCV");
			int scvProvCount = scvQ.executeUpdate();
			LOG.info(scvProvCount+" cap SCV eliminati");
			
			Query rsmQ = ses.createQuery(deleteHql);
			rsmQ.setParameter("s1", "RSM");
			int rsmProvCount = rsmQ.executeUpdate();
			LOG.info(rsmProvCount+" cap RSM eliminati");

			Province provScv = GenericDao.findById(ses, Province.class, "SCV");
			if (provScv != null) GenericDao.deleteGeneric(ses, "SCV", provScv);
			Province provRsm = GenericDao.findById(ses, Province.class, "RSM");
			if (provRsm != null) GenericDao.deleteGeneric(ses, "RSM", provRsm);
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	private static List<Indirizzi> findIndirizzi(Session ses, String idProvincia) {
		String adeHql = "from Indirizzi i where "+
				"i.provincia like :s1";
		Query q = ses.createQuery(adeHql);
		q.setParameter("s1", idProvincia, StringType.INSTANCE);
		@SuppressWarnings("unchecked")
		List<Indirizzi> list = (List<Indirizzi>) q.list();
		return list;
	}
	
}
