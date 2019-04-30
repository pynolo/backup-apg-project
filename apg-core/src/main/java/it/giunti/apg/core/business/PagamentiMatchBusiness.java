package it.giunti.apg.core.business;

import it.giunti.apg.core.CombinationGenerator;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.persistence.EvasioniFascicoliDao;
import it.giunti.apg.core.persistence.FascicoliDao;
import it.giunti.apg.core.persistence.FattureDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.ListiniDao;
import it.giunti.apg.core.persistence.OpzioniIstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.PagamentiCreditiDao;
import it.giunti.apg.core.persistence.PagamentiDao;
import it.giunti.apg.core.persistence.TipiAbbonamentoRinnovoDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.IstanzeStatusUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureArticoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Opzioni;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.shared.model.OpzioniListini;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.PagamentiCrediti;
import it.giunti.apg.shared.model.TipiAbbonamentoRinnovo;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;

public class PagamentiMatchBusiness {

	//private static final Logger LOG = LoggerFactory.getLogger(PagamentiMatchBusiness.class);
	//private static SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");
	private static DecimalFormat DF = new DecimalFormat("#0.00");
	
	private static PagamentiDao pagDao = new PagamentiDao();
	
	public static List<Pagamenti> matchBollettiniToIstanze(Session ses, List<Pagamenti> persPagaList,
			String idUtente, int idRapporto) throws BusinessException {
		IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
		List<Pagamenti> pagAbbinati = new ArrayList<Pagamenti>();
		Date today = DateUtil.now();
		int saldatiCount = 0;
		int rinnovatiCount = 0;
		int erroriCount = 0;
		VisualLogger.get().addHtmlInfoLine(idRapporto, "<b>Fase 3</b>: abbinamento con abbonamenti");
		for (Pagamenti p:persPagaList) {
			//Imposta i valori predefiniti per il pagamento
			p.setIdUtente(idUtente);
			p.setDataModifica(today);
			p.setIdErrore(null);
			//Tenta di caricare l'istanza corrispondente
			IstanzeAbbonamenti oldIa = iaDao.findUltimaIstanzaByCodice(ses, p.getCodiceAbbonamentoMatch());
			IstanzeAbbonamenti workingIa = null;
			try {
				if (oldIa == null) {
					//L'istanza non può essere caricata, non esiste!
					VisualLogger.get().addHtmlInfoLine(idRapporto, "ERR: EUR "+DF.format(p.getImporto())+" " +
							p.getCodiceAbbonamentoMatch() + " non esistente ");
					throw new PagamentiException(AppConstants.PAGAMENTO_ERR_INESISTENTE);
				}
				//Check fatturazione o blocco
				checkFatturaDifferita(oldIa, idRapporto);
				checkBloccoDisdetta(oldIa, idRapporto);
				//Check finestra temporale
				checkBollettiniTimeFrame(ses, oldIa, p, idRapporto);
				//Check scolastico con pagamento diverso dalle opzioni ricevute
				checkBollettiniMatchingSentOptions(ses, oldIa, p, idRapporto);
				//Ottiene l'istanza di lavoro, che può essere o meno un rinnovo
				workingIa = getWorkingInstance(ses, oldIa, idUtente, idRapporto);
				List<TipiAbbonamentoRinnovo> tarList = new TipiAbbonamentoRinnovoDao()
						.findByIdListinoOrdine(ses, oldIa.getListino().getId());
				//Somma il pagamento con crediti precedenti societa
				Set<Pagamenti> pagSet = new HashSet<Pagamenti>();
				pagSet.add(p);
				List<PagamentiCrediti> credList =
						new PagamentiCreditiDao().findByAnagraficaSocieta(ses, workingIa.getId(),
						workingIa.getAbbonamento().getPeriodico().getIdSocieta(), true, false);
				Set<PagamentiCrediti> credSet = new HashSet<>();
				credSet.addAll(credList);
				Double pagato = getTotalAmount(pagSet, credSet);
				//Salva o aggiorna
				if (workingIa.getId() == null) {
					iaDao.save(ses, workingIa);
				} else {
					iaDao.updateUnlogged(ses, workingIa);
				}
				//Riscontra prezzo con opzioni e tipo abbonamento
				//Se il riscontro è positivo cambia tipoAbb e METTE LE OPZIONI in idOpzioniSetT
				matchAndSetOpzioniTipoAbb(ses, workingIa, tarList, pagato, idRapporto);

				//Se non ci sono errori, workingIa è stato riconfigurato con le opzioni compatibili
				Set<Integer> idPagSet = new HashSet<Integer>();
				idPagSet.add(p.getId());
				Set<Integer> idCredSet = new HashSet<Integer>();
				for (PagamentiCrediti cred:credList) idCredSet.add(cred.getId());
				Set<Integer> idOpzSet = new HashSet<Integer>();
				for (OpzioniIstanzeAbbonamenti oia:workingIa.getOpzioniIstanzeAbbonamentiSet())
					idOpzSet.add(oia.getOpzione().getId());
				
				// *** GESTIONE PAGAMENTO ***
				Fatture fatt = processFinalPayment(ses, p.getDataPagamento(), today,
						idPagSet, idCredSet,
						workingIa.getId(), idOpzSet, idUtente);
				Integer workingId = workingIa.getId();
				//come da interfaccia grafica
				Integer idIa = iaDao.update(ses, workingIa, false);
				IstanzeAbbonamenti ia = (IstanzeAbbonamenti) GenericDao.findById(ses, IstanzeAbbonamenti.class, idIa);

				//Logging
				String opzMessage = "";
				if (ia.getOpzioniIstanzeAbbonamentiSet() != null) {
					if (ia.getOpzioniIstanzeAbbonamentiSet().size() > 0) {
						for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
							opzMessage += oia.getOpzione().getUid()+" ";
						}
					}
				}
				if (workingId == null) {
					rinnovatiCount++;
					String msg = "OK: Fattura "+fatt.getNumeroFattura()+
							" &euro;"+DF.format(p.getImporto())+" abbinati a " +
							p.getCodiceAbbonamentoMatch() + " come rinnovo '"+
							ia.getListino().getTipoAbbonamento().getCodice()+"'. ";
					if (opzMessage.length() > 0) msg += "Opz: "+opzMessage;
					VisualLogger.get().addHtmlInfoLine(idRapporto, msg);
				} else {
					saldatiCount++;
					String msg = "OK: Fattura "+fatt.getNumeroFattura()+
							" &euro;"+DF.format(p.getImporto())+" abbinati a " +
							p.getCodiceAbbonamentoMatch() + " come saldo '"+
							ia.getListino().getTipoAbbonamento().getCodice()+"'. ";
					if (opzMessage.length() > 0) msg += "Opz: "+opzMessage;
					VisualLogger.get().addHtmlInfoLine(idRapporto, msg);
				}
			} catch (PagamentiException e) {
				//C'E' UN ERRORE
				erroriCount++;
				try {
					p.setIdErrore(e.getIdError());
					pagDao.update(ses, p);
					String msg = "ERR: &euro;"+DF.format(p.getImporto())+" " +
							p.getCodiceAbbonamentoBollettino() + " "+
							e.getMessage()+" ";
					VisualLogger.get().addHtmlInfoLine(idRapporto, msg);
				} catch (HibernateException eh) {
					throw new BusinessException("Impossibile salvare pagamento errato", eh);
				}
			//} catch (ValidationException e) {//Nel caso di rimozione di opzione fatturata
			//	//C'E' UN ERRORE
			//	erroriCount++;
			//	try {
			//		p.setIdErrore(AppConstants.PAGAMENTO_ERR_NON_ABBINABILE);
			//		pagDao.update(ses, p);
			//	} catch (HibernateException eh) {
			//		throw new BusinessException("Impossibile salvare pagamento errato", eh);
			//	}
			}
		}//for persPagaList
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Saldi importati: "+saldatiCount);
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Rinnovi effettuati: "+rinnovatiCount);
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Errori: "+erroriCount);
		VisualLogger.get().addHtmlInfoLine(idRapporto, "<b>Fase 3</b>: abbinamento terminato");
		return pagAbbinati;
	}
	
	private static void checkFatturaDifferita(IstanzeAbbonamenti ia, int idRapporto) 
			throws PagamentiException {
		if (ia.getListino().getFatturaDifferita() || ia.getFatturaDifferita()) {
			VisualLogger.get().addHtmlInfoLine(idRapporto, "ERR: "+ia.getAbbonamento().getCodiceAbbonamento()+
					" emessa fattura a pagamento differito ");
			throw new PagamentiException(AppConstants.PAGAMENTO_ERR_NON_ABBINABILE);
		}
	}
	
	private static void checkBloccoDisdetta(IstanzeAbbonamenti ia, int idRapporto) 
			throws PagamentiException {
		if (ia.getInvioBloccato() || (ia.getDataDisdetta() != null)) {
			VisualLogger.get().addHtmlInfoLine(idRapporto, "ERR: "+ia.getAbbonamento().getCodiceAbbonamento()+
					" bloccato o con disdetta ");
			throw new PagamentiException(AppConstants.PAGAMENTO_ERR_NON_ABBINABILE);
		}
	}
	
	private static IstanzeAbbonamenti getWorkingInstance(Session ses, IstanzeAbbonamenti originalIa, String idUser,
			int idRapporto) throws BusinessException {
		IstanzeAbbonamenti workingIa = null;
		if (originalIa.getPagato()) {
			//Se e' pagato rinnova
			workingIa = RinnovoBusiness.makeBasicTransientRenewal(ses, originalIa.getId(), true, idUser);
		} else {
			//Se e' da saldare:
			workingIa = originalIa;
		}
		return workingIa;
	}
	
	private static void checkBollettiniTimeFrame(Session ses, IstanzeAbbonamenti ia, Pagamenti pag, int idRapporto) throws PagamentiException {
		Calendar cal = new GregorianCalendar();
		FascicoliDao fasDao = new FascicoliDao();
		boolean invioSenzaPag = ia.getListino().getInvioSenzaPagamento();
		boolean inRegola = IstanzeStatusUtil.isInRegola(ia);
		Date timeFrameStart = null;
		Date timeFrameEnd = null;
		if (inRegola) {
			if (invioSenzaPag) {
				// ** CASO 1: Scolastico e pagato **
				// il pagamento deve essere rifiutato
				VisualLogger.get().addHtmlInfoLine(idRapporto, "ERR: "+ia.getAbbonamento().getCodiceAbbonamento()+
						" e' pagato e non puo' essere rinnovato");
				throw new PagamentiException(AppConstants.PAGAMENTO_ERR_NON_RINNOVABILE);
			} else {
				// ** CASO 2: Varia e pagato **
				// è rinnovabile da fine-X fino a gracingF+X
				Fascicoli ultimo = ia.getFascicoloFine();
				Fascicoli gracingF = fasDao.findFascicoliAfterFascicolo(ses, ia.getFascicoloFine(),
						ia.getListino().getGracingFinale());
				cal.setTime(ultimo.getDataInizio());
				cal.add(Calendar.MONTH, (-1)*AppConstants.PAGAMENTO_MIN_MESI_ANTICIPO);
				timeFrameStart = cal.getTime();
				cal.setTime(gracingF.getDataInizio());
				cal.add(Calendar.MONTH, AppConstants.PAGAMENTO_MAX_MESI_RITARDO_DA_GRACING);
				timeFrameEnd = cal.getTime();
			}
		} else {
			if (invioSenzaPag) {
				// ** CASO 3: Scolastico e non pagato **
				// è saldabile da inizio-X fino a gracingF+X
				Fascicoli inizio = ia.getFascicoloInizio();
				Fascicoli gracingF = fasDao.findFascicoliAfterFascicolo(ses, ia.getFascicoloFine(),
						ia.getListino().getGracingFinale());
				cal.setTime(inizio.getDataInizio());
				cal.add(Calendar.MONTH, (-1)*AppConstants.PAGAMENTO_MIN_MESI_ANTICIPO);
				timeFrameStart = cal.getTime();
				cal.setTime(gracingF.getDataInizio());
				cal.add(Calendar.MONTH, AppConstants.PAGAMENTO_MAX_MESI_RITARDO_DA_GRACING);
				timeFrameEnd = cal.getTime();
			} else {
				// ** CASO 4: Varia e non pagato **
				// è rinnovabile da inizio-X fino a gracingI+X
				Fascicoli inizio = ia.getFascicoloInizio();
				Fascicoli gracingI = fasDao.findFascicoliAfterFascicolo(ses, ia.getFascicoloInizio(),
						ia.getListino().getGracingIniziale());
				cal.setTime(inizio.getDataInizio());
				cal.add(Calendar.MONTH, (-1)*AppConstants.PAGAMENTO_MIN_MESI_ANTICIPO);
				timeFrameStart = cal.getTime();
				cal.setTime(gracingI.getDataInizio());
				cal.add(Calendar.MONTH, AppConstants.PAGAMENTO_MAX_MESI_RITARDO_DA_GRACING);
				timeFrameEnd = cal.getTime();
			}
		}
		//Il pagamento non deve arrivare prima di timeFrameStart
		if (pag.getDataPagamento().before(timeFrameStart)) {
			VisualLogger.get().addHtmlInfoLine(idRapporto, "ERR: "+
					"EUR "+DF.format(pag.getImporto())+" non abbinabili. "+
					pag.getCodiceAbbonamentoMatch() + " pagabile dal "+
					ServerConstants.FORMAT_DAY.format(timeFrameStart));
			throw new PagamentiException(AppConstants.PAGAMENTO_ERR_NON_ABBINABILE);
		}
		//Il pagamento non deve arrivare dopo timeFrameEnd
		if (pag.getDataPagamento().after(timeFrameEnd)) {
			VisualLogger.get().addHtmlInfoLine(idRapporto, "ERR: "+
					"EUR "+DF.format(pag.getImporto())+" non abbinabili. "+
					pag.getCodiceAbbonamentoMatch() + " era pagabile fino al "+
					ServerConstants.FORMAT_DAY.format(timeFrameEnd));
			throw new PagamentiException(AppConstants.PAGAMENTO_ERR_NON_ABBINABILE);
		}
	}
	
	private static void checkBollettiniMatchingSentOptions(Session ses,
			IstanzeAbbonamenti existingIa, Pagamenti pag, int idRapporto) throws PagamentiException {
		//Verifica solo i tipi abbonamento con invio senza pagamento
		if (existingIa.getListino().getInvioSenzaPagamento()) {
			//Controlla solo se c'erano opzioni prenotate (probabilmente già spedite)
			if (existingIa.getOpzioniIstanzeAbbonamentiSet().size() > 0) {
				//Calcolo del prezzo dovuto (esclude le opzioni obbligatorie)
				Double dovuto = existingIa.getListino().getPrezzo()*existingIa.getCopie();
				for (OpzioniIstanzeAbbonamenti oia:existingIa.getOpzioniIstanzeAbbonamentiSet()) {
					boolean mandatoryOpz = false;
					for (OpzioniListini ol:existingIa.getListino().getOpzioniListiniSet()) {
						if (oia.getOpzione().getId().equals(ol.getOpzione().getId())) mandatoryOpz = true;
					}
					if (!mandatoryOpz) dovuto += oia.getOpzione().getPrezzo()*existingIa.getCopie();
				}
				//Somma di tutti i pagamenti effettuati per questa istanza
				Double pagato = pag.getImporto() + new PagamentiCreditiDao()
						.getCreditoByAnagraficaSocieta(ses, existingIa.getId(),
						existingIa.getAbbonamento().getPeriodico().getIdSocieta(), true, false);
				//Ha pagato una cifra diversa dal prenotato
				if (Math.abs(pagato-dovuto) > AppConstants.SOGLIA) {
					VisualLogger.get().addHtmlInfoLine(idRapporto, "ERR: "+
							"EUR "+DF.format(pagato)+" "+
							pag.getCodiceAbbonamentoMatch() + " opzioni non compatibili");
					throw new PagamentiException(AppConstants.PAGAMENTO_ERR_NON_ABBINABILE);
				}
			}
		}
	}
	
	public static void matchAndSetOpzioniTipoAbb(Session ses, IstanzeAbbonamenti ia,
			List<TipiAbbonamentoRinnovo> tarList, Double pagato, Integer idRapporto) 
			throws BusinessException, PagamentiException {
		String idErrore = AppConstants.PAGAMENTO_ERR_NON_ABBINABILE;
		//Calcolo listini alternativi
		List<Listini> listiniList = new ArrayList<Listini>();
		for (TipiAbbonamentoRinnovo tar:tarList) {
			Listini lst = new ListiniDao().findListinoByTipoAbbDate(ses,
					tar.getTipoAbbonamento().getId(),
					ia.getFascicoloInizio().getDataInizio());
			listiniList.add(lst);
		}
		//Calcolo opzioni possibili
		Map<Double, Set<Opzioni>> opzExtraSetMap;
		opzExtraSetMap = CombinationGenerator.getPrezziOpzioniExtraMapByListino(
				ses, ia.getListino(),
				ia.getFascicoloInizio().getDataInizio());
		//Test 1: con il tipo abbonamento preimpostato
		Double prezzoLst = ia.getListino().getPrezzo();
		for (Double przCombo:opzExtraSetMap.keySet()) {
			Double dovuto = ia.getCopie() * (prezzoLst+przCombo);
			if ((pagato >= dovuto-AppConstants.SOGLIA) &&
					(pagato <= dovuto+AppConstants.SOGLIA)) {
				//tal1 & opzioni match pagato
				idErrore = null;
				updateListinoAndOpzioni(ses, ia, ia.getListino(), opzExtraSetMap.get(przCombo));
			}
		}
		//Test 2: con i tipi abbonamento alternativi
		if((idErrore != null) && (listiniList != null)) {
			Listini matching = null;
			int counter = 0;
			do {
				Listini lstAlt = listiniList.get(counter);
				Map<Double, Set<Opzioni>> opzExtraAltSetMap;
				opzExtraAltSetMap = CombinationGenerator.getPrezziOpzioniExtraMapByListino(
							ses, lstAlt,
							ia.getFascicoloInizio().getDataInizio());
				Double prezzoLstAlt = lstAlt.getPrezzo();
				for (Double przCombo:opzExtraAltSetMap.keySet()) {
					Double dovuto = ia.getCopie() * (prezzoLstAlt+przCombo);
					if ((pagato >= dovuto-AppConstants.SOGLIA) &&
							(pagato <= dovuto+AppConstants.SOGLIA)) {
						//tal2 & opzioni match pagato
						idErrore = null;
						matching = lstAlt;
						updateListinoAndOpzioni(ses, ia, lstAlt, opzExtraAltSetMap.get(przCombo));
					}
				}
				counter++;
			} while ((matching == null) && (counter < listiniList.size()));
		}
		
		//Controlla errore
		if (idErrore != null) {
			VisualLogger.get().addHtmlInfoLine(idRapporto, "ERR: "+
					"EUR "+DF.format(pagato)+" non abbinabili a " +
					ia.getAbbonamento().getCodiceAbbonamento());
			throw new PagamentiException(idErrore);
		}
	}

	private static void updateListinoAndOpzioni(Session ses,
			IstanzeAbbonamenti ia, Listini lst, Set<Opzioni> opzSet)
			throws BusinessException {
		OpzioniIstanzeAbbonamentiDao oiaDao = new OpzioniIstanzeAbbonamentiDao();
		IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
		if (ia.getOpzioniIstanzeAbbonamentiSet() == null) 
			ia.setOpzioniIstanzeAbbonamentiSet(new HashSet<OpzioniIstanzeAbbonamenti>());
		
		//Cambia durata e numeri se cambia il listino
		if (!ia.getListino().equals(lst)) {
			ia.setListino(lst);
			ia.setFascicoliTotali(lst.getNumFascicoli());
			Fascicoli fasInizio = ia.getFascicoloInizio();
			try {
				Fascicoli fasFine = new FascicoliDao()
					.findFascicoliAfterFascicolo(ses, fasInizio, lst.getNumFascicoli()-1);
				ia.setFascicoloFine(fasFine);
			} catch (HibernateException e) {
				throw new BusinessException(e.getMessage(), e);
			}
		}
		Set<Integer> idOpzFinalSet = new HashSet<Integer>();
		Set<OpzioniListini> inclOpzSet = lst.getOpzioniListiniSet();
		if (opzSet == null) opzSet = new HashSet<Opzioni>();
		if (inclOpzSet == null) inclOpzSet = new HashSet<OpzioniListini>();
		if ((opzSet.size() > 0) || (inclOpzSet.size() > 0)) {
			//Opzioni obbligatorie
			for (OpzioniListini ol:inclOpzSet) {
				idOpzFinalSet.add(ol.getOpzione().getId());
			}
			//Opzioni richieste
			for (Opzioni opz:opzSet) {
				if (!idOpzFinalSet.contains(opz.getId())) {
					idOpzFinalSet.add(opz.getId());
				}
			}
		}
		//Conserva opzioni mantenute e aggiunge nuove
		Set<OpzioniIstanzeAbbonamenti> newOiaSet = new HashSet<OpzioniIstanzeAbbonamenti>();
		for (Integer idOpz:idOpzFinalSet) {
			//Se oia era già presente in ia, lo ri-aggiunge
			boolean found = false;
			for (OpzioniIstanzeAbbonamenti iaOia:ia.getOpzioniIstanzeAbbonamentiSet()) {
				if (idOpz.equals(iaOia.getId())) {
					//ri-aggiunge
					newOiaSet.add(iaOia);
					//verifica se inclusa
					iaOia.setInclusa(false);
					for (OpzioniListini ol:inclOpzSet) {
						if (iaOia.getOpzione().equals(ol.getOpzione())) {
							iaOia.setInclusa(true);
						}
					}
					oiaDao.update(ses, iaOia);
					found = true;
				}
			}
			if (!found) {
				OpzioniIstanzeAbbonamenti oia = new OpzioniIstanzeAbbonamenti();
				oia.setIstanza(ia);
				Opzioni opzione = GenericDao.findById(ses, Opzioni.class, idOpz);
				oia.setOpzione(opzione);
				oia.setInclusa(false);
				for (OpzioniListini ol:inclOpzSet) {
					if (oia.getOpzione().equals(ol.getOpzione())) {
						oia.setInclusa(true);
					}
				}
				oiaDao.save(ses, oia);
				newOiaSet.add(oia);
			}
		}
		//Rimuove opzioni non selezionate
		Set<OpzioniIstanzeAbbonamenti> cycleSet = new HashSet<OpzioniIstanzeAbbonamenti>();
		cycleSet.addAll(ia.getOpzioniIstanzeAbbonamentiSet());
		for (OpzioniIstanzeAbbonamenti iaOia:cycleSet) {
			boolean found = false;
			for (Integer idOpz:idOpzFinalSet) {
				if (idOpz.equals(iaOia.getId())) found = true;
			}
			if (!found) {
				new OpzioniIstanzeAbbonamentiDao().delete(ses, iaOia);
			}
		}
		//Salva in ia
		ia.getOpzioniIstanzeAbbonamentiSet().clear();
		ia.getOpzioniIstanzeAbbonamentiSet().addAll(newOiaSet);
		iaDao.update(ses, ia);
	}
	
	
	
	public static Double getMissingAmount(Session ses, Integer idIa) {
		IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIa);
		Set<Integer> idOpzSet = new HashSet<Integer>();
		if (ia.getOpzioniIstanzeAbbonamentiSet() != null) {
			for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
				idOpzSet.add(oia.getOpzione().getId());
			}
		}
		return getMissingAmount(ses, idIa, idOpzSet);
	}
	public static Double getMissingAmount(Session ses, Integer idIa,
			Set<Integer> idOpzSet) {
		IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIa);
		//Calcolo nuovo costo (unitario)
		double costo = 0d;
		if (ia.getIdFattura() == null) costo += ia.getListino().getPrezzo();
		//Aggiunta costo nuovi supplementi
		for (Integer idOpz:idOpzSet) {
			boolean mandatory = false;
			for (OpzioniListini ol:ia.getListino().getOpzioniListiniSet()) {
				if (ol.getOpzione().getId() == idOpz) mandatory = true;
			}
			boolean include = true;
			for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
				if (oia.getOpzione().getId() == idOpz && oia.getIdFattura() != null) {
					include = false;
				}
			}
			if (include && !mandatory) {
				Opzioni opz = GenericDao.findById(ses, Opzioni.class, idOpz);
				costo += opz.getPrezzo();
			}
		}
		//Aggiunta costo altri supplementi non saldati
		for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
			boolean obbligatoria = false;
			for (OpzioniListini ol:ia.getListino().getOpzioniListiniSet()) {
				if (ol.getOpzione().getId() == oia.getOpzione().getId()) obbligatoria = true;
			}
			if (!obbligatoria) {
				boolean found = false;
				for (Integer idOpz:idOpzSet) found = found || oia.getOpzione().getId().equals(idOpz);
				if (!found && (oia.getIdFattura() == null)) {
					costo += oia.getOpzione().getPrezzo();//non nuovo e non pagato
				}
			}
		}
		//Costo * copie
		costo = costo * ia.getCopie();
		return costo;
	}
	
	public static Double getIstanzaTotalPrice(Session ses, Integer idIa) {
		IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIa);
		return getIstanzaTotalPrice(ia);
	}
	public static Double getIstanzaTotalPrice(IstanzeAbbonamenti ia) {
		//Calcolo nuovo costo (unitario)
		double costo = 0d;
		costo += ia.getListino().getPrezzo();
		for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
			boolean mandatory = false;
			for (OpzioniListini ol:ia.getListino().getOpzioniListiniSet()) {
				if (ol.getOpzione().getId() == oia.getOpzione().getId()) mandatory = true;
			}
			if (!mandatory) {
				costo += oia.getOpzione().getPrezzo();
			}
		}
		//Costo * copie
		costo = costo * ia.getCopie();
		return costo;
	}
	
	public static Double getTotalAmount(Session ses, 
			Set<Integer> idPagSet, Set<Integer> idCredSet) {
		Set<Pagamenti> pagSet = new HashSet<Pagamenti>();
		Set<PagamentiCrediti> credSet = new HashSet<PagamentiCrediti>();
		if (idPagSet != null) {
			for (Integer id:idPagSet) {
				Pagamenti pag = GenericDao.findById(ses, Pagamenti.class, id);
				pagSet.add(pag);
			}
		}
		if (idCredSet != null) {
			for (Integer id:idCredSet) {
				PagamentiCrediti cred = GenericDao.findById(ses, PagamentiCrediti.class, id);
				credSet.add(cred);
			}
		}
		return getTotalAmount(pagSet, credSet);
	}
	
	public static Double getTotalAmount(Set<Pagamenti> pagSet,
			Set<PagamentiCrediti> credSet) {
		//Calcolo credito
		double totCrediti = 0d;
		if (pagSet != null) {
			for (Pagamenti pag:pagSet) {
				totCrediti += pag.getImporto();
			}
		}
		if (credSet != null) {
			for (PagamentiCrediti cred:credSet) {
				totCrediti += cred.getImporto();
			}
		}
		return totCrediti;
	}
	
	public static void fixIstanza(Session ses, IstanzeAbbonamenti ia) {
		boolean paid = IstanzeStatusUtil.isInRegola(ia);
		Date now = DateUtil.now();
		//Reattach missing fascicoli
		if (paid) {
			ia.setPagato(true);
			ia.setDataSaldo(now);
			new EvasioniFascicoliDao().enqueueMissingArretratiByStatus(ses, ia, ia.getIdUtente());
		} else {
			ia.setPagato(false);
			ia.setDataSaldo(null);
		}
	}

	
	/* il pagamento è fatturato totalmente come anticipo */
	public static Fatture processDepositPayment(Session ses, 
			Date dataPagamento, Date dataAccredito, Integer idPagamento, 
			Integer idPagante, String idSocieta, boolean fatturaFittizia, String idUtente) 
			throws HibernateException, BusinessException {
		if (idPagamento == null) throw new BusinessException("Nessun pagamento o credito da abbinare");
		
		Pagamenti pag = GenericDao.findById(ses, Pagamenti.class, idPagamento);
		if (pag.getIdFattura() != null) throw new BusinessException("Pagamento "+idPagamento+
				" is already bound to invoice "+pag.getIdFattura());
		pag.setIdUtente(idUtente);
		new PagamentiDao().update(ses, pag);
		
		//Crea fattura (non ancora le righe)
		Anagrafiche pagante = GenericDao.findById(ses, Anagrafiche.class, idPagante);
		Fatture fatt = FattureBusiness.setupEmptyFattura(ses, pagante,
				idSocieta, dataPagamento, fatturaFittizia, idUtente);
		//fatt.setIdIstanza(idIa);
		//fatt.setIdPeriodico(ia.getAbbonamento().getPeriodico().getId());
				
		//boolean ivaScorporata = FattureBusiness.hasIvaScorporata(pagante);
		Set<Pagamenti> pagSet = new HashSet<Pagamenti>();
		pagSet.add(pag);
		List<FattureArticoli> faList = FattureBusiness
				.bindFattureArticoliResto(ses, fatt, pag.getImporto());//, true);
		FattureBusiness.sumIntoFattura(fatt, faList);
		FattureBusiness.bindPagamentiCrediti(ses, fatt, null, pagSet, null);
		new FattureDao().update(ses, fatt);
		
		//Credito (da stornare o rimborsare in futuro)
		createCredito(ses, fatt, pag.getImporto(), idSocieta, 
				pagante.getId(), false, idUtente);
		return fatt;
	}
	
	/* il pagamento è compatibile con il prezzo da pagare > saldo */
	public static Fatture processFinalPayment(Session ses, Date dataPagamento, Date dataAccredito,
			Set<Integer> idPagSet, Set<Integer> idCredSet,
			Integer idIa, Set<Integer> idOpzSet, String idUtente)
					 throws HibernateException, BusinessException {
		Fatture fatt = null;
		Date now = DateUtil.now();
		if (idOpzSet == null) idOpzSet = new HashSet<Integer>();
		if (idCredSet == null) idCredSet = new HashSet<Integer>();
		if (idPagSet == null) idPagSet = new HashSet<Integer>();
		FattureDao fattDao = new FattureDao();
		IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();

		//Importi
		Set<Pagamenti> pagSet = new HashSet<Pagamenti>();
		for (Integer idPag:idPagSet) {
			Pagamenti p = GenericDao.findById(ses, Pagamenti.class, idPag);
			pagSet.add(p);
		}
		Set<PagamentiCrediti> credSet = new HashSet<PagamentiCrediti>();
		for (Integer idCred:idCredSet) {
			PagamentiCrediti cred = GenericDao.findById(ses, PagamentiCrediti.class, idCred);
			credSet.add(cred);
		}

		//Abbonamento
		IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIa);
		Set<OpzioniIstanzeAbbonamenti> oiaSet = new HashSet<OpzioniIstanzeAbbonamenti>();
		for (Integer idOpz:idOpzSet) {
			OpzioniIstanzeAbbonamenti oia = null;
			for (OpzioniIstanzeAbbonamenti x:ia.getOpzioniIstanzeAbbonamentiSet()) {
				if (x.getOpzione().getId().equals(idOpz)) oia = x;
			}
			if (oia == null) {
				throw new BusinessException("Opzione "+idOpz+" is not included in instance "+idIa);
			} else {
				oiaSet.add(oia);
			}
		}
		
		double dovuto = PagamentiMatchBusiness.getMissingAmount(ses, idIa, idOpzSet);
		double pagato = PagamentiMatchBusiness.getTotalAmount(pagSet, credSet);
		if (pagato < AppConstants.SOGLIA) throw new BusinessException("Nessun pagamento o credito da abbinare");
		Double resto = 0D;
		if (pagato > dovuto) resto = pagato - dovuto;

		//Il pagamento è comunque forzato
		ia.setPagato(true);
		ia.setDataSaldo(now);
		ia.setDataModifica(now);
		ia.setIdUtente(idUtente);
		iaDao.update(ses, ia);
	
		//Crea fattura (non ancora le righe)
		Anagrafiche pagante = ia.getAbbonato();
		if (ia.getPagante() != null) pagante = ia.getPagante();
		fatt = FattureBusiness.setupEmptyFattura(ses, pagante,
				ia.getAbbonamento().getPeriodico().getIdSocieta(), dataPagamento, 
				ia.getListino().getFatturaInibita(), idUtente);
		fatt.setIdIstanzaAbbonamento(idIa);
		fatt.setIdPeriodico(ia.getAbbonamento().getPeriodico().getId());
		if (resto >= AppConstants.SOGLIA)
			fatt.setImportoResto(resto);
			
		//Bind:
		List<FattureArticoli> faList = FattureBusiness.bindFattureArticoli(ses,
				fatt, pagato, resto, pagante, ia, idOpzSet);
		FattureBusiness.sumIntoFattura(fatt, faList);
		FattureBusiness.bindIstanzeOpzioni(ses, fatt, ia, oiaSet);
		FattureBusiness.bindPagamentiCrediti(ses, fatt, ia, pagSet, credSet);
		fattDao.update(ses, fatt);
		
		//Credito (da stornare o rimborsare in futuro)
		if (resto >= AppConstants.SOGLIA) {
			createCredito(ses, fatt, resto,
					ia.getAbbonamento().getPeriodico().getIdSocieta(), 
					pagante.getId(), false, idUtente);
		}
		fixIstanza(ses, ia);
		iaDao.updateUnlogged(ses, ia);
		return fatt;
	}
	
	
	public static Integer createCredito(Session ses, Fatture fatturaOrigine, Double importo,
			String idSocieta, Integer idAnagrafica, boolean stornatoDaOrigine, String idUtente) {
		PagamentiCrediti cred = new PagamentiCrediti();
		cred.setDataCreazione(DateUtil.now());
		cred.setFatturaOrigine(fatturaOrigine);
		cred.setIdAnagrafica(idAnagrafica);
		cred.setIdSocieta(idSocieta);
		cred.setImporto(importo);
		cred.setIdUtente(idUtente);
		cred.setStornatoDaOrigine(stornatoDaOrigine);
		Serializable id = new PagamentiCreditiDao().save(ses, cred);
		return (Integer)id;
	}
	
	public static Boolean verifyPagatoAndUpdate(Session ses, Integer idIstanzaAbbonamento) 
			throws BusinessException {
		Boolean result = true;
		IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIstanzaAbbonamento);
		if (ia == null) throw new BusinessException("Istanza non trovata");
		if (ia.getFatturaDifferita() || ia.getListino().getFatturaDifferita())
			return null;
		//tutte le componenti non obbligatorie devono avere fattura
		if (ia.getIdFattura() == null) result = false;
		for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
			boolean included = false;
			for (OpzioniListini ol:ia.getListino().getOpzioniListiniSet()) {
				if (ol.getOpzione().getId().equals(oia.getOpzione().getId())) included=true;
			}
			if (oia.getIdFattura() == null && !included) result = false;
		}
		//update
		ia.setPagato(result);
		new IstanzeAbbonamentiDao().update(ses, ia);
		return result;
	}
}
