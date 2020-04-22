package it.giunti.apg.updater;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Anagrafiche;

public class InsertAnagraficaFromOldMerge {
	
	private static final Logger LOG = LoggerFactory.getLogger(InsertAnagraficaFromOldMerge.class);
	
	private static String utente = ServerConstants.DEFAULT_SYSTEM_USER;
	
	public static void execute() throws BusinessException {
		int created = 0;
		int skipped = 0;
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		AnagraficheDao anagDao = new AnagraficheDao();
		try {
			int c = 0;
			List<Anagrafiche> anagList = new ArrayList<Anagrafiche>();
			do {
				String hql = "from Anagrafiche a where "+
						"a.uidMergeListOld is not null and "+
						"a.uidMergeListOld is not empty "+
						"order by a.id";
				Query q = ses.createQuery(hql);
				q.setMaxResults(500);
				q.setFirstResult(anagList.size());
				@SuppressWarnings("unchecked")
				List<Anagrafiche> list = (List<Anagrafiche>) q.list();
				c = list.size();
				if (c > 0) anagList.addAll(list);
				System.out.println("Trovate "+anagList.size()+" anagrafiche");
			} while (c > 0);
			System.out.println("TOTALE: "+anagList.size());
			
			for (Anagrafiche a:anagList) {
				//Recupera i UID eliminati
				Set<String> codiciSet = new HashSet<String>();
				if (a.getUidMergeListOld() != null) {
					String[] mergedArray = a.getUidMergeListOld().split(AppConstants.STRING_SEPARATOR);
					for (String codice:mergedArray) codiciSet.add(codice);
				}
				//Ricrea ogni anagrafica eliminata
				for (String uid:codiciSet) {
					//Cerco se esiste
					Anagrafiche found = anagDao.findByUid(ses, uid, true);
					if (found == null) {
						Anagrafiche newAnag = anagDao.createAnagrafiche(ses);
						newAnag.setUid(uid);
						newAnag.setMergedIntoUid(a.getUid());
						newAnag.setDeleted(true);
						newAnag.setConsensoTos(false);
						newAnag.setIdTipoAnagrafica(null);
						newAnag.setDataAggiornamentoConsenso(DateUtil.longAgo());
						newAnag.setDataCreazione(DateUtil.longAgo());
						newAnag.setDataModifica(DateUtil.longAgo());
						newAnag.setIdUtente(utente);
						newAnag.getIndirizzoPrincipale().setNazione(null);
						newAnag.getIndirizzoPrincipale().setIdUtente(utente);
						newAnag.getIndirizzoFatturazione().setNazione(null);
						newAnag.getIndirizzoFatturazione().setIdUtente(utente);
						anagDao.save(ses, newAnag);
						created ++;
					} else {
						//row with UID exists
						skipped ++;
					}
				}
			}
			
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		LOG.info(created+" created, "+skipped+" skipped");
	}
	
}
