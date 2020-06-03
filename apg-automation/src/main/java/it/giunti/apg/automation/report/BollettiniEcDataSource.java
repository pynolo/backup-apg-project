package it.giunti.apg.automation.report;

import it.giunti.apg.core.persistence.ModelliBollettiniDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.shared.model.ModelliBollettini;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class BollettiniEcDataSource {

	private static List<EvasioniComunicazioni> _ecList = null;
	
	public static void initDataSource(List<EvasioniComunicazioni> ecList) {
		_ecList = ecList;
	}
	
	public static List<Bollettino> createBeanCollection() throws BusinessException {
  		List<Bollettino> list = new ArrayList<Bollettino>();
  		if (_ecList != null) {
	  		Session ses = SessionFactory.getSession();
	  		Transaction trn = ses.beginTransaction();
	  		try {
				list = createBeanCollection(ses);
				trn.commit();
			} catch (HibernateException e) {
				trn.rollback();
				throw new BusinessException(e.getMessage(), e);
			} finally {
				ses.close();
			}
  		}
  		return list;
	}
	
	public static List<Bollettino> createBeanCollection(Session ses)
			throws HibernateException, BusinessException {
  		List<Bollettino> list = new ArrayList<Bollettino>();
  		ModelliBollettiniDao bmDao = new ModelliBollettiniDao();
  		if (_ecList != null) {
  			for (EvasioniComunicazioni ec:_ecList) {
				Integer idPeriodico = null;
				if (ec.getIstanzaAbbonamento() != null)
						idPeriodico = ec.getIstanzaAbbonamento().getAbbonamento().getPeriodico().getId();
				if (ec.getMaterialeProgrammazione() != null)
						idPeriodico = ec.getMaterialeProgrammazione().getPeriodico().getId();
				if (idPeriodico == null)
						throw new BusinessException("L'evasione di comunicazione "+ec.getId()+" della comunicazione "+ec.getComunicazione().getId()+" non e' associabile a un periodico e quindi a un modello di bollettino");
				//Controlla se stampare o meno
				if (!ec.getEliminato()) {
					ModelliBollettini modello = null;
					if (ec.getComunicazione() != null) {
						modello = ec.getComunicazione().getModelloBollettino();
					} else {
						modello = bmDao.createModelliBollettini(ses,
								idPeriodico, "\n\n"+ec.getMessaggio());
					}
					Bollettino bean = new Bollettino(modello, ec);
					list.add(bean);
				}
			}
  		}
  		return list;
	}
	
	//private static void prepareEvasioniComunicazioniForPrint(
	//		Session ses, EvasioniComunicazioni ec) throws HibernateException {
	//	IstanzeAbbonamenti ia = ec.getIstanzaAbbonamento();
	//	boolean isNdd = ec.getComunicazione().getIdTipoMedia().equals(AppConstants.COMUN_MEDIA_NDD);
	//	PagamentiDao pagDao = new PagamentiDao();
	//	boolean daVersare = true;
	//	Double importo = null;
	//	String note = "";
	//	if (ec.getNote() != null) note += ec.getNote();
	//	if (ia.getFatturaDifferita()) {
	//		daVersare = false; 
	//		note = ia.getAbbonamento().getCodiceAbbonamento()+
	//				" e' in fatturazione";
	//	}
	//	if (ia.getInvioBloccato()) {
	//		daVersare = false; 
	//		note = ia.getAbbonamento().getCodiceAbbonamento()+
	//				" e' bloccato";
	//	}
	//	//importo
	//	Double pagato = pagDao.sumPagamentiByIstanzaAbbonamento(ses, ia.getId());
	//	Double listino = ia.getTipoAbbonamentoListino().getPrezzo();
	//	if (listino.doubleValue() > AppConstants.SOGLIA) {//eventuali opzioni
	//		for(Opzioni sup:ia.getOpzioniList()) {
	//			listino += sup.getPrezzo();
	//		}
	//	}
	//	listino = listino * ia.getCopie();
	//	importo = listino - pagato;
	//	//Non stampa se importo < 1 euro
	//	if (importo < AppConstants.SOGLIA){
	//		daVersare = false; 
	//		note = 	ia.getAbbonamento().getCodiceAbbonamento()+
	//				" e' stato pagato "+ServerConstants.FORMAT_CURRENCY.format(pagato)+
	//				" a fronte di un dovuto di "+ServerConstants.FORMAT_CURRENCY.format(listino)+
	//				" ("+ia.getCopie()+" copie)";
	//		importo = 0D;
	//	}
	//	//Modifica di EC in base a daVersare e importo:
	//	ec.setNote(note);
	//	ec.setImportoStampato(importo);
	//	if (!daVersare) {
	//		//Quando il pagamento non è necessario
	//		if (isNdd) {
	//			//le ndd non sono eliminate ma comunque estratte come "annullate"
	//			ec.setEstrattoComeAnnullato(true);
	//		} else {
	//			//tutti i bollettini sono marcati come eliminati: da non spedire
	//			ec.setEliminato(true);
	//		}
	//	}
	//}
	
}
