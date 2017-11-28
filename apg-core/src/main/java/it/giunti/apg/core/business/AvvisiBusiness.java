package it.giunti.apg.core.business;

import it.giunti.apg.core.persistence.AvvisiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Avvisi;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AvvisiBusiness {

	private static final Logger LOG = LoggerFactory.getLogger(AvvisiBusiness.class);
	
	public static void writeAvviso(String testoAvviso, boolean importante, String idUtente)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			Avvisi avviso = new Avvisi();
			avviso.setData(DateUtil.now());
			avviso.setImportante(importante);
			avviso.setMessaggio(testoAvviso);
			avviso.setIdUtente(idUtente);
			new AvvisiDao().save(ses, avviso);
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
}
