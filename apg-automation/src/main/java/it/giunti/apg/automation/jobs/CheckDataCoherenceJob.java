package it.giunti.apg.automation.jobs;

import it.giunti.apg.automation.AutomationConstants;
import it.giunti.apg.automation.business.CountEvasioniFascicoli;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.AvvisiBusiness;
import it.giunti.apg.core.persistence.EvasioniFascicoliDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IndirizziDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.LocalitaDao;
import it.giunti.apg.core.persistence.OpzioniIstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.PeriodiciDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Localita;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.shared.model.OpzioniListini;
import it.giunti.apg.shared.model.Periodici;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckDataCoherenceJob implements Job {
	
	private static Logger LOG = LoggerFactory.getLogger(CheckDataCoherenceJob.class);
	private static final String EOL = "<br/>\r\n";
	private static String jobName = null;
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		jobName = jobCtx.getJobDetail().getKey().getName();
		//param: letterePeriodici
		String letterePeriodici = (String) jobCtx.getMergedJobDataMap().get("letterePeriodici");
		if (letterePeriodici == null) throw new JobExecutionException("letterePeriodici non definito");
		if (letterePeriodici.equals("")) throw new JobExecutionException("letterePeriodici non definito");
		String[] lettereArray = letterePeriodici.split(AppConstants.STRING_SEPARATOR);
		
		LOG.info("Started job '"+jobName+"'");
		try {
			//Controllo coerenza dei cap
			fixLocalitaCap(ServerConstants.DEFAULT_SYSTEM_USER);
			//Controllo che le istanze abbiano tutte le opzioni obbligatorie
			fixOpzioniMancanti(lettereArray, ServerConstants.DEFAULT_SYSTEM_USER);
			//Controllo abbonamenti senza fascicolo iniziale spedito
			checkFascicoliInizio(ServerConstants.DEFAULT_SYSTEM_USER);
			//Controllo della somma dei fascicoli inviati per ciascuna istanza
			randomCheckFascicoliInviati(lettereArray, ServerConstants.DEFAULT_SYSTEM_USER);
			//Controllo istanze sovrapposte temporalmente (oggi e tra 4 mesi)
			checkAbbonamentiDoppi(ServerConstants.DEFAULT_SYSTEM_USER);
			//Controllo che le istanze scadute abbiano ricevuto tutti i fascicoli
			checkFascicoliMancanti(lettereArray, ServerConstants.DEFAULT_SYSTEM_USER);
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		}
		//try {
		//	VisualLogger.get().closeAndSaveRapporto(idRapporto);
		//} catch (PagamentiException e) {
		//	throw new JobExecutionException(e.getMessage(), e);
		//}
		LOG.info("Ended job '"+jobName+"'");
	}
	
	public void fixLocalitaCap(String idUtente) throws BusinessException {
		String rapporto = "";
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		IndirizziDao indDao = new IndirizziDao();
		int indCount = 0;
		try {
			//ricerca cappario modificato
			List<Localita> locList = findModifiedLocalita(ses);
			int locCount = 0;
			for (Localita loc:locList) {
				locCount++;
				LOG.debug(locCount+"/"+locList.size()+" "+loc.getNome());
				List<Anagrafiche> anaList = findAnagraficheByLocalita(ses, loc);
				//Correzione anagrafiche per ciscuna località
				for (Anagrafiche ana:anaList) {
					boolean correct = false;
					if (ana.getIndirizzoPrincipale().getCap() != null) {
						if (ana.getIndirizzoPrincipale().getCap().trim().equals(loc.getCap())) {
							correct = true;
						}
					}
					if (!correct) {
						indCount++;
						String msg = indCount+") UID["+ana.getUid()+"] localita' "+
								ana.getIndirizzoPrincipale().getLocalita()+" "+
								ana.getIndirizzoPrincipale().getCap()+" -> "+
								loc.getCap();
						LOG.debug(msg);
						rapporto += msg+EOL;
						ana.getIndirizzoPrincipale().setCap(loc.getCap());
						indDao.update(ses, ana.getIndirizzoPrincipale());
					}
				}
				loc.setModificaPropagata(true);
				new LocalitaDao().update(ses, loc);
			}
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (rapporto.length() > 1) {
			String reportName = "Controllo corrispondenza localita/cap: corrette "+
					indCount+" anomalie";
			writeReport(reportName, rapporto, idUtente, false);
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Localita> findModifiedLocalita(Session ses) {
		String hql = "from Localita loc1 where " +
				"loc1.modificaPropagata = :b1 and "+
				//Solo le località con un solo CAP
				"1 = (select count(*) from Localita loc2 where "+
						"loc2.nome = loc1.nome and "+
						"loc2.idProvincia = loc1.idProvincia) "+
				"order by loc1.nome";
		Query q = ses.createQuery(hql);
		q.setParameter("b1", Boolean.FALSE);
		List<Localita> locList = (List<Localita>) q.list();
		return locList;
	}
	@SuppressWarnings("unchecked")
	private List<Anagrafiche> findAnagraficheByLocalita(Session ses, Localita loc) {
		String locName = loc.getNome();
		if (locName.length() > 25) locName = locName.substring(0, 25)+"%";
		String hql = "from Anagrafiche a where " +
				"a.indirizzoPrincipale.localita like :s1 and "+
				"a.indirizzoPrincipale.provincia like :s2 "+
				"order by a.id";
		Query q = ses.createQuery(hql);
		q.setParameter("s1", locName, StringType.INSTANCE);
		q.setParameter("s2", loc.getIdProvincia(), StringType.INSTANCE);
		List<Anagrafiche> anaList = (List<Anagrafiche>) q.list();
		return anaList;
	}
	
	public void randomCheckFascicoliInviati(String[] uidPeriodiciArray, String idUtente) throws IOException, BusinessException {
		//Controllo 'fascicoli inviati'
		Double randomLettera = Math.floor(Math.random()*(uidPeriodiciArray.length));
		String letteraPeriodico = uidPeriodiciArray[randomLettera.intValue()];
		Periodici chosenPeriodico = null;
		Session ses = SessionFactory.getSession();
		try {
			chosenPeriodico = new PeriodiciDao().findByUid(ses, letteraPeriodico);
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		StringBuilder message = new StringBuilder();
		int diffCount = CountEvasioniFascicoli.countEvasioni(chosenPeriodico, message, EOL);
		if (diffCount > 0) {
			String reportName = "Controllo fascicoli inviati per "+chosenPeriodico.getNome()+": "+
					AutomationConstants.ICON_AMBULANCE+" <b>"+diffCount+" anomalie</b>";
			writeReport(reportName, EOL+message.toString(), idUtente, false);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void checkAbbonamentiDoppi(String idUtente) throws IOException, BusinessException{
		Calendar cal = new GregorianCalendar();
		Date now = cal.getTime();
		cal.add(Calendar.MONTH, AppConstants.MESE_INIZIO_MONTHS_FORWARD);
		Date future = cal.getTime();
		
		Session ses = SessionFactory.getSession();
		int errorCount = 0;
		String message = "";
		try {
			String hql = "select count(*), ia.abbonamento.codiceAbbonamento " +
					"from IstanzeAbbonamenti ia where " +
					"ia.fascicoloInizio.dataInizio < :dt1 and " +
					"ia.fascicoloFine.dataFine > :dt2 and " +
					"ia.invioBloccato = :b1 " + // is false
					"group by ia.abbonamento.codiceAbbonamento " +
					"having count(*) > :i1"; // > 1
			Query q = ses.createQuery(hql);
			q.setParameter("dt1", now, DateType.INSTANCE);
			q.setParameter("dt2", now, DateType.INSTANCE);
			q.setParameter("b1", Boolean.FALSE);
			q.setParameter("i1", 1, IntegerType.INSTANCE);
			List<Object[]> istanzeNow = (List<Object[]>) q.list();
			q.setParameter("dt1", future, DateType.INSTANCE);
			q.setParameter("dt2", future, DateType.INSTANCE);
			q.setParameter("b1", Boolean.FALSE);
			q.setParameter("i1", 1, IntegerType.INSTANCE);
			List<Object[]> istanzeFuture = (List<Object[]>) q.list();
			
			Set<Object[]> istanzeSet = new HashSet<Object[]>();
			istanzeSet.addAll(istanzeNow);
			istanzeSet.addAll(istanzeFuture);
			
			errorCount = istanzeSet.size();
			if (errorCount > 0) {
				message += "Abbonamenti con istanze attive sovrapposte:"+EOL;
				for (Object[] item:istanzeSet) {
					String codice = (String) item[1];
					List<IstanzeAbbonamenti> iaList = new IstanzeAbbonamentiDao()
						.findIstanzeByCodice(ses, codice, 0, Integer.MAX_VALUE);
					if (iaList.size() > 0) {
						message += "Abbonamento <b>"+
								iaList.get(0).getAbbonamento().getCodiceAbbonamento()+"</b>: "+EOL;
						for (IstanzeAbbonamenti ia:iaList) {
							message += " UID["+ia.getId()+"] "+
									ServerConstants.FORMAT_DAY.format(ia.getFascicoloInizio().getDataInizio())+
									"-"+ServerConstants.FORMAT_DAY.format(ia.getFascicoloFine().getDataFine())+" ";
							if (ia.getPagato()) {
								message += "<i>pagato</i> ";
							}
							if (ia.getFatturaDifferita() || ia.getListino().getFatturaDifferita()) {
								message += "<i>in fatturazione</i> ";
							}
							if (ia.getListino().getPrezzo() < AppConstants.SOGLIA) {
								message += "<i>omaggio</i> ";
							}
							message += EOL;
						}
					}
				}
			}
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (errorCount > 0) {
			String reportName = "Controllo abbonamenti doppi: "+
					AutomationConstants.ICON_AMBULANCE+" <b>"+errorCount+" anomalie</b>";
			writeReport(reportName, EOL+message, idUtente, true);
		}
	}
	
	public void checkFascicoliMancanti(String[] uidPeriodiciArray, String idUtente)
			throws IOException, BusinessException{
		StringBuilder message = new StringBuilder();
		int errorCount = 0;
		Session ses = SessionFactory.getSession();
		try {
			for (String lettera:uidPeriodiciArray) {
				Periodici periodico = new PeriodiciDao().findByUid(ses, lettera);
				errorCount += checkFascicoliMancantiByPeriodico(ses, periodico, message);
			}
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (errorCount > 0) {
			String reportName = "Controllo fascicoli mancanti: "+
					AutomationConstants.ICON_AMBULANCE+" <b>"+errorCount+" anomalie</b>";
			writeReport(reportName, EOL+message.toString(), idUtente, true);
		}
	}
	
	@SuppressWarnings("unchecked")
	private int checkFascicoliMancantiByPeriodico(Session ses, Periodici periodico,
			StringBuilder message) throws IOException, BusinessException {
		Date now = DateUtil.now();
		String hql = "from IstanzeAbbonamenti ia where " +
				"ia.abbonamento.periodico.id = :id1 and " +
				"ia.fascicoloFine.dataInizio < :dt1 and " +
				"ia.fascicoloFine.dataEstrazione is not null and " +
				"ia.invioBloccato = :b1 and " + // is false
				"ia.fascicoliSpediti < ia.fascicoliTotali and " +
				"ia.listino.cartaceo = :b2 and " + //is cartaceo
					"(ia.pagato = :b3 or " +
					"ia.fatturaDifferita = :b4 or " +
					"ia.listino.fatturaDifferita = :b5 or " +
					"ia.listino.prezzo <= :d1) " +
				"order by ia.abbonamento.codiceAbbonamento";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", periodico.getId(), IntegerType.INSTANCE);
		q.setParameter("dt1", now, DateType.INSTANCE);
		q.setParameter("b1", Boolean.FALSE);
		q.setParameter("b2", Boolean.TRUE);
		q.setParameter("b3", Boolean.TRUE);
		q.setParameter("b4", Boolean.TRUE);
		q.setParameter("b5", Boolean.TRUE);
		q.setParameter("d1", AppConstants.SOGLIA);
		List<IstanzeAbbonamenti> iaList = (List<IstanzeAbbonamenti>) q.list();
		if (iaList.size() > 0) {
			message.append(periodico.getNome()+EOL+
					"Istanze scadute con meno fascicoli del dovuto:"+EOL);
			for (IstanzeAbbonamenti ia:iaList) {
					message.append("<b>"+ia.getAbbonamento().getCodiceAbbonamento()+"</b> UID["+ia.getId()+"] "+
							ServerConstants.FORMAT_DAY.format(ia.getFascicoloInizio().getDataInizio())+"-"+
							ServerConstants.FORMAT_DAY.format(ia.getFascicoloFine().getDataFine())+" "+
							"fasc."+ia.getFascicoliSpediti()+"/"+ia.getFascicoliTotali()+EOL);
			}
			message.append(EOL);
		}
		return iaList.size();
	}
	
	@SuppressWarnings("unchecked")
	private int checkFascicoliInizio(String idUtente) throws BusinessException {
		int errorCount = 0;
		String message = "Nelle seguenti istanze non c'&egrave; corrispondenza tra " +
				"l'intervallo di inizio/fine e i fascicoli inviati: "+EOL;
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		EvasioniFascicoliDao efDao = new EvasioniFascicoliDao();
		IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
		try {
			LOG.info("Estrazione abbonamenti da verificare");
			String hql = "from IstanzeAbbonamenti ia where " +
					"ia.invioBloccato = :b1 and " +//FALSE
					"ia.listino.cartaceo = :b2 and " +//TRUE
					"ia.fascicoloInizio.dataEstrazione is not null and " +
					"ia.fascicoloInizio.id not in (" +
						"select ef.fascicolo.id from EvasioniFascicoli ef where " +
						"ef.idIstanzaAbbonamento=ia.id and " +
						"ef.fascicolo.id=ia.fascicoloInizio.id " +
						") " +
					"order by ia.abbonamento.codiceAbbonamento asc ";
			Query q = ses.createQuery(hql);
			q.setParameter("b1", Boolean.FALSE);
			q.setParameter("b2", Boolean.TRUE);
			List<IstanzeAbbonamenti> iaList = q.list();
			LOG.info("Totale abbonamenti da verificare: "+iaList.size());
			for (IstanzeAbbonamenti ia:iaList) {
				//Riattacca i fascicoli di vecchio gracing
				efDao.reattachEvasioniFascicoliToIstanza(ses, ia);
				//Aggiorna totale fascicoli inviati
				int newSpediti = efDao.countFascicoliSpediti(ses, ia.getId());
				if (ia.getFascicoliSpediti() != newSpediti) {
					ia.setFascicoliSpediti(newSpediti);
					iaDao.update(ses, ia);
				}
				errorCount++;
				message += errorCount+") <b>"+ia.getAbbonamento().getCodiceAbbonamento()+"</b> "+
						"UID["+ia.getId()+"] del "+
						ServerConstants.FORMAT_DAY.format(ia.getFascicoloInizio().getDataInizio())+EOL;
				if (errorCount%100 == 0) LOG.info("Verificati "+errorCount+"/"+iaList.size());
			}
			trn.commit();
			LOG.info("Termine verifica: "+errorCount+"/"+iaList.size());
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (errorCount > 0) {
			String reportName = "Controllo corrispondenza dei fascicoli inviati: "+
					AutomationConstants.ICON_AMBULANCE+" <b>"+errorCount+" anomalie</b>";
			writeReport(reportName, message.toString(), idUtente, true);
		}
		return errorCount;
	}
	
	public void fixOpzioniMancanti(String[] uidPeriodiciArray, String idUtente)
			throws IOException, BusinessException{
		StringBuilder message = new StringBuilder();
		int iaCount = 0;
		int oiaCount = 0;
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			for (String lettera:uidPeriodiciArray) {
				Periodici periodico = new PeriodiciDao().findByUid(ses, lettera);
				List<IstanzeAbbonamenti> iaList = findOpzioniMancantiByPeriodico(ses, periodico, message);
				iaCount += iaList.size();
				oiaCount += fixOpzioniMancantiByPeriodico(ses, iaList);
			}
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (iaCount > 0) {
			String reportName = "Controllo opzioni obbligatorie: "+
					AutomationConstants.ICON_AMBULANCE+" <b>"+iaCount+" istanze errate</b> "+
					"aggiunte "+oiaCount+" opzioni mancanti.";
			writeReport(reportName, EOL+message.toString(), idUtente, true);
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<IstanzeAbbonamenti> findOpzioniMancantiByPeriodico(Session ses, Periodici periodico,
			StringBuilder message) throws IOException, BusinessException {
		Date now = DateUtil.now();
		//Lista di tutti i listini con opzioni obbligatorie
		String opzioniHql = "from OpzioniListini ol where "+
				"ol.listino.tipoAbbonamento.periodico.id = :id1";
		Query opzioniQ = ses.createQuery(opzioniHql);
		opzioniQ.setParameter("id1", periodico.getId(), IntegerType.INSTANCE);
		List<OpzioniListini> olList = (List<OpzioniListini>) opzioniQ.list();
		List<IstanzeAbbonamenti> iaList = new ArrayList<IstanzeAbbonamenti>();
		for (OpzioniListini ol:olList) {
			//Elenco istanze con listino dato che non hanno l'opzione obbligatoria
			String sql = "select ia.id from istanze_abbonamenti as ia "+
					"left outer join opzioni_istanze_abbonamenti as oia on oia.id_istanza_abbonamento=ia.id "+
					"join fascicoli ff on ia.id_fascicolo_fine = ff.id where "+
					"ff.data_inizio >= :dt1 and "+
					"ia.id_listino = :id1 and "+
					"ia.data_disdetta is null and "+
					"ia.invio_bloccato = :b1 "+
					"group by ia.id having count(oia.id) = 0 "+
					"order by ia.id";
			Query q = ses.createSQLQuery(sql);
			q.setParameter("id1", ol.getListino().getId(), IntegerType.INSTANCE);
			q.setParameter("dt1", now, DateType.INSTANCE);
			q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
			List<Integer> list = (List<Integer>) q.list();
			//Aggiunge le istanze mancanti
			if (list.size() > 0) {
				for (Integer id:list) {
					IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, id);
					if (!iaList.contains(ia)) iaList.add(ia);
				}
			}
		}
		if (iaList.size() > 0) {
			message.append(periodico.getNome()+EOL+
					"Istanze senza opzioni obbligatorie:"+EOL);
			for (IstanzeAbbonamenti ia:iaList) {
					String opzioni = "";
					for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
						opzioni += oia.getOpzione().getUid()+";";
					}
					message.append("<b>"+ia.getAbbonamento().getCodiceAbbonamento()+"</b> UID["+ia.getId()+"] "+
							"scad."+ServerConstants.FORMAT_DAY.format(ia.getFascicoloFine().getDataFine())+" "+
							"<b>"+ia.getListino().getTipoAbbonamento().getCodice()+"</b> "+
							"opz: "+opzioni+EOL);
			}
			message.append(EOL);
		}
		return iaList;
	}
	
	private int fixOpzioniMancantiByPeriodico(Session ses, List<IstanzeAbbonamenti> iaList) {
		OpzioniIstanzeAbbonamentiDao oiaDao = new OpzioniIstanzeAbbonamentiDao();
		int count = 0;
		//ciclo istanze
		for (IstanzeAbbonamenti ia:iaList) {
			//ciclo opzioni obbligatorie
			for (OpzioniListini ol:ia.getListino().getOpzioniListiniSet()) {
				//ciclo opzioni assegnate
				boolean found = false;
				for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
					if (oia.getOpzione().getId() == ol.getOpzione().getId()) found = true;
				}
				if (!found) {
					OpzioniIstanzeAbbonamenti newOia = new OpzioniIstanzeAbbonamenti();
					newOia.setIstanza(ia);
					newOia.setOpzione(ol.getOpzione());
					oiaDao.save(ses, newOia);
					count ++;
				}
			}
		}
		return count;
	}
	
	private void writeReport(String titoloRapporto, String message, String idUtente, boolean createAvviso) 
			throws BusinessException {
		Integer idRapporto = VisualLogger.get().createRapporto(titoloRapporto, idUtente);
		VisualLogger.get().addHtmlInfoLine(idRapporto, message);
		VisualLogger.get().closeAndSaveRapporto(idRapporto);
		if (createAvviso) {
			//UriParameters params = new UriParameters();
			//params.add(AppConstants.PARAM_ID, idRapporto);
			//String uri = params.getUri(UriManager.RAPPORTO);
			//String link = "<a href='#"+uri+"'>"+AutomationConstants.ICON_MAGNIFIER+" "+titoloRapporto+"</a>";
			AvvisiBusiness.writeAvviso(titoloRapporto, false, ServerConstants.DEFAULT_SYSTEM_USER);
		}
	}
}
