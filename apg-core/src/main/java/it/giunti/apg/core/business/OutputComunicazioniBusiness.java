package it.giunti.apg.core.business;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.persistence.EvasioniComunicazioniDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.shared.model.Periodici;

public class OutputComunicazioniBusiness {

	private static final Logger LOG = LoggerFactory.getLogger(OutputComunicazioniBusiness.class);
	
	public static List<EvasioniComunicazioni> findOrCreateEvasioniComunicazioniProgrammate(Date date,
			Integer idPeriodico, String idTipoMedia, String idTipoAttivazione,
			Integer idFasc, int idRapporto, String idUtente)
			throws BusinessException {
		List<EvasioniComunicazioni> ecList = null;
		Session ses = SessionFactory.getSession();
		EvasioniComunicazioniDao ecDao = new EvasioniComunicazioniDao();
		try {
			//Estrae
			ecList = ecDao.findOrCreateEvasioniComunicazioniProgrammate(ses,
					date, idPeriodico, idTipoMedia, idTipoAttivazione,
					idFasc, idRapporto, idUtente);
		} catch (HibernateException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return ecList;
	}

	public static List<EvasioniComunicazioni> findEvasioniComunicazioniManuali(Date date,
			Integer idPeriodico, String idTipoMedia, int idRapporto, String idUtente)
			throws BusinessException, EmptyResultException {
		List<EvasioniComunicazioni> ecList = null;
		Session ses = SessionFactory.getSession();
		EvasioniComunicazioniDao ecDao = new EvasioniComunicazioniDao();
		try {
			//Estrae
			ecList = ecDao.findEvasioniComunicazioniManuali(ses,
					idPeriodico, idTipoMedia, idRapporto);
		} catch (HibernateException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		//if (ecList == null) throw new EmptyResultException("Nessuna comunicazione da estrarre");
		if (ecList.size() == 0) throw new EmptyResultException("Nessuna comunicazione da estrarre");
		return ecList;
	}
	
	public static void writeEvasioniComunicazioniOnDb(
			List<EvasioniComunicazioni> ecList, Date dataInvio,
			int idRapporto, String idUtente) 
					throws BusinessException {
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Inizio scrittura su DB delle comunicazioni");
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			writeEvasioniComunicazioniOnDb(ses, ecList, dataInvio, idRapporto, idUtente);
			trn.commit();
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Fine scrittura su DB delle comunicazioni");
		} catch (HibernateException e) {
			trn.rollback();
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	public static void writeEvasioniComunicazioniOnDb(
			Session ses,
			List<EvasioniComunicazioni> ecList, Date dataInvio,
			int idRapporto, String idUtente) 
					throws HibernateException {
		//Integer progressivoNdd = -1;
		//Salva una per una tutte le ec
		EvasioniComunicazioniDao ecDao = new EvasioniComunicazioniDao();
		for (EvasioniComunicazioni ec:ecList) {
			ec.setDataEstrazione(dataInvio);
			////allinea il progressivo al valore più grande assegnato
			//if (ec.getProgressivo() != null) {
			//	if (ec.getProgressivo() > progressivoNdd) {
			//		progressivoNdd = ec.getProgressivo();
			//	}
			//}
			if (ec.getId() == null) {
				ecDao.save(ses, ec); //Sostituito da SQL
				//ecDao.sqlInsert(ses, ec);
			} else {
				ecDao.update(ses, ec); //Sostituito da SQL
				//ecDao.sqlUpdate(ses, ec);
			}
		}
		////Salva il progressivo NDD se necessario
		//if (progressivoNdd > 0) {
		//	new ContatoriDao().updateProgressivo(ses, progressivoNdd, ServerConstants.CONTATORE_NDD);
		//}
	}
	

	public static String findNomePeriodico(Integer idPeriodico) throws BusinessException {
		String result = "";
		Session ses = SessionFactory.getSession();
		try {
			Periodici p = GenericDao.findById(ses, Periodici.class, idPeriodico);
			if (p != null) result = p.getNome();
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}

}
