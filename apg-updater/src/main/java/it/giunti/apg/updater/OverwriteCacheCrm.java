package it.giunti.apg.updater;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;

import it.giunti.apg.core.business.CacheBusiness;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.CacheCrm;

public class OverwriteCacheCrm {

	//private static final Logger LOG = LoggerFactory.getLogger(CreateCacheCrm.class);
	
	private static final int P = 0;
	
	@SuppressWarnings("unchecked")
	public static void update() 
			throws BusinessException, IOException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		Date dtStart = new Date();
		try {
			//Pick up entities to overwrite
			String ccHql = "from CacheCrm where "+
					"ownSubscriptionIdentifier"+P+" is not null and "+
					"ownSubscriptionBlocked"+P+" = :b"+P+" and "+
					"ownSubscriptionEnd"+P+" >= :dt"+P;
			Query ccQ = ses.createQuery(ccHql);
			ccQ.setParameter("b"+P, Boolean.TRUE, BooleanType.INSTANCE);
			ccQ.setParameter("dt"+P, dtStart, DateType.INSTANCE);
			List<CacheCrm> ccList = ccQ.list();
			System.out.println();
			for (CacheCrm cc:ccList) {
				Anagrafiche a = GenericDao.findById(ses, Anagrafiche.class, cc.getIdAnagrafica());
				CacheBusiness.saveOrUpdateCacheThreadless(ses, a);
			}
			trn.commit();
			trn = ses.beginTransaction();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
}
