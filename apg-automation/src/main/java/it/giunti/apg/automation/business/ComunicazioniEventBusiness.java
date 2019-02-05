package it.giunti.apg.automation.business;

import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.ComunicazioniBusiness;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Comunicazioni;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.TipiAbbonamento;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;

public class ComunicazioniEventBusiness {

	/**
	 * Crea le comunicazioni al cliente che devono partire quando un abbonamento nuovo viene creato.
	 * Gli oggetti nella List<EvasioniComunicazioni> sono transienti e quindi non salvati su db.
	 */
	public static List<EvasioniComunicazioni> createMissingEvasioniComOnCreation(Session ses,
			Comunicazioni com, Date fromDay, Date dataCreazione, String idUtente, int idRapporto) 
			throws BusinessException {
		List<EvasioniComunicazioni> result;
		try {
			Date startDay = fromDay;
			if (com.getDataInizio().after(fromDay)) startDay = com.getDataInizio();
			result = new ArrayList<EvasioniComunicazioni>();
			IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
			String[] idsArray = com.getTipiAbbonamentoList().split(AppConstants.COMUN_TIPI_ABB_SEPARATOR);
			//Cicla tutti i tipi abbonamento abbinati alla comunicazione
			for (String ids:idsArray) {
				if (ids.length() > 0) {
					//Cerca le istanze di questo tipo modificate dal giorno "fromDay" e senza evasioni
					Integer idTa = Integer.parseInt(ids);
					String tagOpzione = null;
					if (com.getTagOpzione() != null) {
						if (com.getTagOpzione().length() > 0) tagOpzione = com.getTagOpzione();
					}
					List<IstanzeAbbonamenti> iaList = iaDao.findIstanzeByMissingEvasioneOnCreationOrCambioTipo(ses,
							idTa, com, tagOpzione, startDay);
					for (IstanzeAbbonamenti ia:iaList) {
						boolean ok = !ia.getInvioBloccato();//Comunque non deve essere bloccato
						if (com.getSoloNonPagati()) ok = (ok && !ia.getPagato() && !ia.getFatturaDifferita());
						if (com.getSoloUnaCopia()) ok = (ok && (ia.getCopie() == 1));
						if (com.getSoloPiuCopie()) ok = (ok && (ia.getCopie() > 1));
						if (com.getSoloUnaIstanza()) ok = (ok && (countIstanze(ses, ia) == 1));
						if (com.getSoloMolteIstanze()) ok = (ok && (countIstanze(ses, ia) > 1));
						if (com.getSoloConPagante()) ok = (ok && (ia.getPagante() != null));
						if (com.getSoloSenzaPagante()) ok = (ok && (ia.getPagante() == null));
						if (com.getRichiestaRinnovo())
							ok = (ok && ia.getUltimaDellaSerie() && (ia.getDataDisdetta() == null));
						if (com.getIdFascicoloInizio() != null)
							ok = (ok && (ia.getFascicoloInizio().getId().equals(com.getIdFascicoloInizio())));
						if (ok) {
							result.add(createTransientEvasioniComunicazioni(ia, com, null, dataCreazione, idUtente));
						}
					}
				}
			}
		} catch (NumberFormatException e) {
			throw new BusinessException(e.getMessage(), e);
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Comunic. creazione "+com.getPeriodico().getUid()+
				" '"+com.getTitolo()+"': "+result.size());
		return result;
	}
	
	/**
	 * Crea le comunicazioni al cliente che devono partire quando un abbonamento passa allo stato di "pagato"
	 * Gli oggetti nella List<EvasioniComunicazioni> sono transienti e quindi non salvati su db.
	 */
	public static List<EvasioniComunicazioni> createMissingEvasioniComOnPayment(Session ses,
			Comunicazioni com, Date fromDay, Date dataCreazione, String idUtente, int idRapporto)
			throws BusinessException {
		List<EvasioniComunicazioni> result;
		try {
			Date startDay = fromDay;
			if (com.getDataInizio().after(fromDay)) startDay = com.getDataInizio();
			result = new ArrayList<EvasioniComunicazioni>();
			IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
			String[] idsArray = com.getTipiAbbonamentoList().split(AppConstants.COMUN_TIPI_ABB_SEPARATOR);
			//Cicla tutti i tipi abbonamento abbinati alla comunicazione
			for (String ids:idsArray) {
				if (ids.length() > 0) {
					//Cerca le istanze di questo tipo modificate dal giorno "fromDay"
					Integer idTa = Integer.parseInt(ids);
					String tagOpzione = null;
					if (com.getTagOpzione() != null) {
						if (com.getTagOpzione().length() > 0) tagOpzione = com.getTagOpzione();
					}
					List<IstanzeAbbonamenti> iaList = iaDao.findIstanzeByMissingEvasioneOnPayment(ses,
							idTa, com, tagOpzione, startDay);
					for (IstanzeAbbonamenti ia:iaList) {
						//Condizioni della comunicazione:
						boolean ok = !ia.getInvioBloccato();
						if (com.getSoloNonPagati()) ok = (ok && !ia.getPagato() && !ia.getFatturaDifferita());
						if (com.getSoloUnaCopia()) ok = (ok && (ia.getCopie() == 1));
						if (com.getSoloPiuCopie()) ok = (ok && (ia.getCopie() > 1));
						if (com.getSoloUnaIstanza()) ok = (ok && (countIstanze(ses, ia) == 1));
						if (com.getSoloMolteIstanze()) ok = (ok && (countIstanze(ses, ia) > 1));
						if (com.getSoloConPagante()) ok = (ok && (ia.getPagante() != null));
						if (com.getSoloSenzaPagante()) ok = (ok && (ia.getPagante() == null));
						if (com.getRichiestaRinnovo())
							ok = (ok && ia.getUltimaDellaSerie() && (ia.getDataDisdetta() == null));
						if (com.getIdFascicoloInizio() != null)
							ok = (ok && (ia.getFascicoloInizio().getId().equals(com.getIdFascicoloInizio())));
						if (ok) {
							result.add(createTransientEvasioniComunicazioni(ia, com, null, dataCreazione, idUtente));
						}
					}
				}
			}
		} catch (NumberFormatException e) {
			throw new BusinessException(e.getMessage(), e);
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Comunic. pagamento "+com.getPeriodico().getUid()+
				" '"+com.getTitolo()+"': "+result.size());
		return result;
	}
	
	/**
	 * Crea le comunicazioni al cliente che devono partire in corrispondenza delle evasioni di fascicoli.
	 * Sono presi in considerazione i fascicoli evasi ma con flag "comunicazioni_inviate" falso.
	 * Le comunicazioni al cliente partono per le istanze che hanno il fascicolo in oggetto tra i fascicoli ricevuti.
	 * 
	 * Gli oggetti nella List<EvasioniComunicazioni> sono transienti e quindi non salvati su db.
	 */
	public static List<EvasioniComunicazioni> createMissingEvasioniComunicazioniByFascicoli(Session ses,
			List<Fascicoli> fascicoliList, List<Comunicazioni> comList, Date dataCreazione,
			String idUtente, int idRapporto)
					throws BusinessException{
		List<EvasioniComunicazioni> result = new ArrayList<EvasioniComunicazioni>();
		IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
		for (Fascicoli fas:fascicoliList) {
			for (Comunicazioni com:comList) {
				if (com.getIdTipoAttivazione().equals(AppConstants.COMUN_ATTIVAZ_DA_INIZIO) ||
						com.getIdTipoAttivazione().equals(AppConstants.COMUN_ATTIVAZ_DA_FINE)) {
					//Solo le comunicazioni riferibili all'evasione di
					//un fascicolo: COMUN_ATTIVAZ_DA_INIZIO e COMUN_ATTIVAZ_DA_FINE
					if (fas.getPeriodico().equals(com.getPeriodico())) {
						List<TipiAbbonamento> taList = ComunicazioniBusiness
								.getTipiAbbListFromComunicazione(ses, com.getId());
						for (TipiAbbonamento ta:taList) {
							List<IstanzeAbbonamenti> iaList = new ArrayList<IstanzeAbbonamenti>();
							//Comunicazione riferita all'inizio abbonamento
							if (com.getIdTipoAttivazione().equals(AppConstants.COMUN_ATTIVAZ_DA_INIZIO)) {
								String tagOpzione = null;
								if (com.getTagOpzione() != null) {
									if (com.getTagOpzione().length() > 0) tagOpzione = com.getTagOpzione();
								}
								////Query paginata: istanze che devono ancora ricevere la comunicazione
								//int offset = 0;
								//int size = 0;
								//do {
								//	List<IstanzeAbbonamenti> list = 
								//			iaDao.findIstanzeByFascicoloInizioMissingComunicazione(ses,
								//			fas.getId(), ta, com, tagOpzione,
								//			offset, pageSize);
								//	size = list.size();
								//	offset += size;
								//	iaList.addAll(list);
								//} while (size > 0);
								
								//Query NON paginata
								iaList = iaDao.findIstanzeByFascicoloInizioMissingComunicazione(ses,
										fas.getId(), ta, com, tagOpzione);
								
							}
							//Comunicazione riferita alla fine abbonamento
							if (com.getIdTipoAttivazione().equals(AppConstants.COMUN_ATTIVAZ_DA_FINE)) {
								String tagOpzione = null;
								if (com.getTagOpzione() != null) {
									if (com.getTagOpzione().length() > 0) tagOpzione = com.getTagOpzione();
								}
								////Query paginata: istanze che devono ancora ricevere la comunicazione
								//int offset = 0;
								//int size = 0;
								//do {
								//	List<IstanzeAbbonamenti> list =
								//			iaDao.findIstanzeByFascicoloFineMissingComunicazione(ses,
								//			fas.getId(), fasFine.getId(), ta, com, tagOpzione,
								//			offset, pageSize);
								//	size = list.size();
								//	offset += size;
								//	iaList.addAll(list);
								//} while (size > 0);
								
								//Query NON paginata
								iaList = iaDao.findIstanzeByFascicoloFineMissingComunicazione(ses,
										fas.getId(), ta, com, tagOpzione);
							}
							
							int count = 0;
							//Filtraggio del risultato sulle condizioni aggiuntive della comunicazione
							for (IstanzeAbbonamenti ia:iaList) {
								//Condizioni della comunicazione:
								boolean ok = !ia.getInvioBloccato();
								if (com.getSoloNonPagati()) ok = (ok && !ia.getPagato() && !ia.getFatturaDifferita());
								if (com.getSoloUnaCopia()) ok = (ok && (ia.getCopie() == 1));
								if (com.getSoloPiuCopie()) ok = (ok && (ia.getCopie() > 1));
								if (com.getSoloUnaIstanza()) ok = (ok && (countIstanze(ses, ia) == 1));
								if (com.getSoloMolteIstanze()) ok = (ok && (countIstanze(ses, ia) > 1));
								if (com.getSoloConPagante()) ok = (ok && (ia.getPagante() != null));
								if (com.getSoloSenzaPagante()) ok = (ok && (ia.getPagante() == null));
								if (com.getRichiestaRinnovo())
									ok = (ok && ia.getUltimaDellaSerie() && (ia.getDataDisdetta() == null));
								if (com.getIdFascicoloInizio() != null)
									ok = (ok && (ia.getFascicoloInizio().getId().equals(com.getIdFascicoloInizio())));
								if (ok) {
									result.add(createTransientEvasioniComunicazioni(ia, com, fas, dataCreazione, idUtente));
									count++;
								}
							}
							
							VisualLogger.get().addHtmlInfoLine(idRapporto, "Comunic. per "+
									fas.getPeriodico().getUid()+" "+fas.getTitoloNumero()+" '"+
									com.getTitolo()+"' ("+ta.getCodice()+" "+ta.getNome()+"): "+count);
						}
					}
				}
			}
		}
		return result;
	}
	
	private static EvasioniComunicazioni createTransientEvasioniComunicazioni(IstanzeAbbonamenti ia,
			Comunicazioni com, Fascicoli fas, Date dataCreazione, String idUtente) {
		EvasioniComunicazioni ec = new EvasioniComunicazioni();
		ec.setComunicazione(com);
		ec.setIdTipoDestinatario(com.getIdTipoDestinatario());
		ec.setIdTipoMedia(com.getIdTipoMedia());
		ec.setRichiestaRinnovo(com.getRichiestaRinnovo());
		ec.setFascicolo(fas);
		ec.setDataCreazione(dataCreazione);
		ec.setDataModifica(dataCreazione);
		ec.setDataEstrazione(null);
		ec.setEliminato(false);
		ec.setNote(null);
		ec.setMessaggio(null);
		ec.setIstanzaAbbonamento(ia);
		ec.setIdUtente(idUtente);
		return ec;
	}
	
	//private static boolean isDopoGiugno(IstanzeAbbonamenti ia) {
	//	//nuovi: da giugno scorso
	//	Calendar cal = new GregorianCalendar();
	//	if (cal.get(Calendar.MONTH) < Calendar.JUNE) {
	//		cal.add(Calendar.YEAR, -1);
	//	}
	//	cal.set(Calendar.MONTH, Calendar.JUNE);
	//	cal.set(Calendar.DAY_OF_MONTH, 1);//cal contiene il 1° giugno scorso
	//	return ia.getAbbonamento().getDataCreazione().after(cal.getTime());//la creazione segue giugno scorso
	//}
	//
	//private static boolean isPrimaGiugno(IstanzeAbbonamenti ia) {
	//	//nuovi: da giugno scorso
	//	Calendar cal = new GregorianCalendar();
	//	if (cal.get(Calendar.MONTH) < Calendar.JUNE) {
	//		cal.add(Calendar.YEAR, -1);
	//	}
	//	cal.set(Calendar.MONTH, Calendar.JUNE);
	//	cal.set(Calendar.DAY_OF_MONTH, 1);//cal contiene il 1° giugno scorso
	//	return ia.getAbbonamento().getDataCreazione().before(cal.getTime());//la creazione precede giugno scorso
	//}
	
	private static int countIstanze(Session ses, IstanzeAbbonamenti ia) {
		IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
		return iaDao.countIstanzeByCodice(ses, ia.getAbbonamento().getCodiceAbbonamento());
	}
	
}
