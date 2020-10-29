package it.giunti.apg.core.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.core.OpzioniUtil;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.ListiniDao;
import it.giunti.apg.core.persistence.MaterialiProgrammazioneDao;
import it.giunti.apg.core.persistence.MaterialiSpedizioneDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.core.persistence.TipiAbbonamentoRinnovoDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.MaterialiProgrammazione;
import it.giunti.apg.shared.model.TipiAbbonamento;

public class RinnovoBusiness {
	
	private static final Logger LOG = LoggerFactory.getLogger(RinnovoBusiness.class);
	
	public static IstanzeAbbonamenti makeBasicRenewal(Integer idOldIst,
			boolean inizioFollowsOldIst, boolean createArretrati, String idUtente)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
		MaterialiSpedizioneDao msDao = new MaterialiSpedizioneDao();
		IstanzeAbbonamenti result = null;
		try {
			result = makeBasicTransientRenewal(ses, idOldIst, inizioFollowsOldIst, idUtente);
			iaDao.save(ses, result);
			iaDao.markUltimaDellaSerie(ses, result.getAbbonamento());
			if (createArretrati) {
				msDao.enqueueMissingArretratiByStatus(ses, result);
			}
			msDao.setupAdditionalMateriali(ses, result);
			//Opzioni
			OpzioniUtil.addOpzioniObbligatorie(ses, result, false);
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		//} catch (ValidationException e) {//Nel caso di rimozione di opzione fatturata
		//	trn.rollback();
		//	LOG.error(e.getMessage(), e);
		//	throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
		
	public static IstanzeAbbonamenti makeBasicTransientRenewal(Session ses,
			Integer idOldIa, boolean inizioFollowsOldIst, String idUtente) throws BusinessException {
		//Nuova istanza
		IstanzeAbbonamenti iaT;
		try {
			Date today = DateUtil.now();
			IstanzeAbbonamenti oldIa = (IstanzeAbbonamenti)ses.get(IstanzeAbbonamenti.class, idOldIa);
			Abbonamenti abb = oldIa.getAbbonamento();
			Anagrafiche anagrafica = oldIa.getAbbonato();
			Anagrafiche pagante = oldIa.getPagante();
			
			iaT = new IstanzeAbbonamenti();
			iaT.setAbbonamento(abb);
			iaT.setAbbonato(anagrafica);
			iaT.setPagante(pagante);
			Date dataInizio = new Date(oldIa.getDataFine().getTime()+AppConstants.DAY);
			iaT.setDataInizio(dataInizio);
						
			//Tipo abbonamento (listino) successivo
			TipiAbbonamento tipoAbbRinnovo = new TipiAbbonamentoRinnovoDao()
					.findFirstTipoRinnovoByIdListino(ses, oldIa.getListino().getId());
			if (tipoAbbRinnovo == null) tipoAbbRinnovo = oldIa.getListino().getTipoAbbonamento();
			ListiniDao lDao = new ListiniDao();
			Listini lst = lDao.findListinoByTipoAbbDate(ses, tipoAbbRinnovo.getId(),
					iaT.getDataInizio());

			if (lst == null) throw new BusinessException(oldIa.getAbbonamento().getCodiceAbbonamento()+
					": Impossibile trovare il listino per "+
					tipoAbbRinnovo.getCodice()+" al "+ServerConstants.FORMAT_DAY.format(iaT.getDataInizio()));
			iaT.setListino(lst);
			if (lst.getMeseInizio() != null) {
				MaterialiProgrammazione fasInizio = new MaterialiProgrammazioneDao().changeFascicoloToMatchStartingMonth(ses, lst);
				dataInizio = fasInizio.getDataNominale();
			}
			iaT.setDataInizio(dataInizio);
			iaT.setCopie(oldIa.getCopie());
			iaT.setDataCreazione(today);
			iaT.setDataCambioTipo(today);
			iaT.setDataModifica(today);
			iaT.setPagato(false);
			iaT.setPropostaAcquisto(false);
			iaT.setFatturaDifferita(lst.getFatturaDifferita());
			if (lst.getFatturaDifferita()) iaT.setDataSaldo(today);
			iaT.setInvioBloccato(false);
			iaT.setAdesione(null);//oldIa.getAdesione());
			iaT.setIdUtente(idUtente);
			//Data fine
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(dataInizio);
			cal.add(Calendar.MONTH, lst.getDurataMesi());
			iaT.setDataFine(cal.getTime());
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		return iaT;
	}

//	public static IstanzeAbbonamenti renewTransientIstanzeAbbonamenti(Session ses, Integer idOldIst,
//			boolean inizioFollowsOldIst, String userId) throws PagamentiException {
//		return renewTransientIstanzeAbbonamenti(ses, idOldIst, null, inizioFollowsOldIst, userId);
//	}
//	
//	public static IstanzeAbbonamenti renewTransientIstanzeAbbonamenti(Session ses,
//			Integer idOldIst, Double importoUnitario, boolean inizioFollowsOldIst,
//			String userId) throws PagamentiException {
//		//Nuova istanza
//		IstanzeAbbonamenti ia;
//		try {
//			Date today = DateUtil.now();
//			IstanzeAbbonamenti oldIa = (IstanzeAbbonamenti)ses.get(IstanzeAbbonamenti.class, idOldIst);
//			Utenti utente = (Utenti)ses.get(Utenti.class, userId);
//			Abbonamenti abb = oldIa.getAbbonamento();
//			Anagrafiche anagrafica = oldIa.getAbbonato();
//			Anagrafiche pagante = oldIa.getPagante();
//			boolean fattura = anagrafica.getRichiedeFattura();
//			if (pagante != null) {
//				fattura = fattura || pagante.getRichiedeFattura();
//			}
//
//			//Tipo abbonamento successivo
//			TipiAbbonamento tipoAbbRinnovo = oldIa.getTipoAbbonamentoListino().getTipoAbbonamentoRinnovo();
//			if (tipoAbbRinnovo == null) {
//				tipoAbbRinnovo = oldIa.getTipoAbbonamentoListino().getTipoAbbonamento();
//			}
//			TipiAbbonamentoDao tipiDao = new TipiAbbonamentoDao();
//			Listini tal = tipiDao.findLastListinoByIdTipoAbbonamento(ses, tipoAbbRinnovo.getId());
//			//Sceglie l'alternativo se l'importo corrisponde
//			if (importoUnitario != null) {
//				TipiAbbonamento tipoAbbAlternativo = oldIa.getTipoAbbonamentoListino().getTipoAbbonamentoRinnovoAlternativa();
//				if (tipoAbbAlternativo != null) {
//					Listini talAlternativo = tipiDao.findLastListinoByIdTipoAbbonamento(ses, tipoAbbAlternativo.getId());
//					if ( ((talAlternativo.getPrezzo()-AppConstants.SOGLIA) <= importoUnitario) &&
//							((talAlternativo.getPrezzo()+AppConstants.SOGLIA) >= importoUnitario) ) {
//						tipoAbbRinnovo = tipoAbbAlternativo;
//						tal = talAlternativo;
//					}
//				}
//			}
//			
//			ia = new IstanzeAbbonamenti();
//			ia.setAbbonamento(abb);
//			ia.setAbbonato(anagrafica);
//			ia.setPagante(pagante);
//			ia.setTipoAbbonamentoListino(tal);
//			ia.setCopie(oldIa.getCopie());
//			ia.setFascicoliSpediti(0);
//			ia.setFascicoliTotali(tal.getNumFascicoli());
//			ia.setDataCreazione(today);
//			ia.setDataCambioTipo(today);
//			ia.setDataModifica(today);
//			ia.setPagato(false);
//			ia.setFatturaDifferita(tal.getPagatoConFattura());
//			ia.setInvioBloccato(false);
//			ia.setAdesione(null);//oldIa.getAdesione());
//			ia.setUtente(utente);
//			//Inizio e fine
//			FascicoliDao fasDao = new FascicoliDao();
//			if (inizioFollowsOldIst) {
//				Fascicoli fasInizio;
//				Fascicoli fasFine;
//				try {
//					fasInizio = fasDao.findFascicoliAfterFascicolo(ses, oldIa.getFascicoloFine(), 1);
//					fasFine = fasDao.findFascicoliAfterFascicolo(ses, fasInizio, tal.getNumFascicoli()-1);
//					ia.setFascicoloInizio(fasInizio);
//					ia.setFascicoloFine(fasFine);
//				} catch (HibernateException e) {
//					throw new PagamentiException(e.getMessage(), e);
//				}
//			} else {
//				fasDao.setupFascicoliInizioFineByPeriodicoDate(ses, ia, today);
//			//	if (tal.getMeseInizio() != null) {
//			//		Date inizio = ServerUtil.getInizioByMonth(tal.getMeseInizio(), DateUtil.now());
//			//		fasInizio = fasDao.findFascicoloByPeriodicoDataNominale(ses,
//			//				oldIa.getAbbonamento().getPeriodico().getId(), inizio);
//			//	} else {
//			//		fasInizio = fasDao.findPrimoFascicoloNonSpedito(ses, oldIa.getAbbonamento().getPeriodico().getId(), DateUtil.now());
//			//	}
//			}
//		} catch (HibernateException e) {
//			throw new PagamentiException(e.getMessage(), e);
//		}
//		return ia;
//	}
}
