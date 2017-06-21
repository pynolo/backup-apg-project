package it.giunti.apg.automation.jobs;

import it.giunti.apg.core.Mailer;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.FascicoliDao;
import it.giunti.apg.core.persistence.PagamentiDao;
import it.giunti.apg.core.persistence.PeriodiciDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Periodici;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailRiscontiJob implements Job {
	
	static private Logger LOG = LoggerFactory.getLogger(EmailRiscontiJob.class);
	static private int QUERY_PAGE_SIZE = 250;

	private Map<Fascicoli,Double> fascicoliImportiMap = null;
	private Map<Fascicoli,Integer> fascicoliTiraturaMap = null;
	private List<String> codiciAbbList = new ArrayList<String>();
	
	//private int countOmaggi = 0;
	//Rimosso perché se il calcolo parte dai 6 mesi precedenti allora
	//sono contati negli omaggi anche quelli dell'anno prima
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		LOG.info("Started job '"+jobCtx.getJobDetail().getKey().getName()+"'");
		//param: letterePeriodici
		String letterePeriodici = (String) jobCtx.getMergedJobDataMap().get("letterePeriodici");
		if (letterePeriodici == null) throw new JobExecutionException("letterePeriodici non definito");
		if (letterePeriodici.equals("")) throw new JobExecutionException("letterePeriodici non definito");
		String[] lettereArray = letterePeriodici.split(AppConstants.STRING_SEPARATOR);
		//param: emailRecipients
		String emailRecipients = (String) jobCtx.getMergedJobDataMap().get("emailRecipients");
		if (emailRecipients == null) throw new JobExecutionException("emailRecipients non definito");
		if (emailRecipients.equals("")) throw new JobExecutionException("emailRecipients non definito");
		String[] recipientArray = emailRecipients.split(AppConstants.STRING_SEPARATOR);
		//JOB
		String body = "";
		Session ses = SessionFactory.getSession();
		try {
			File codiciFile = null;
			for (String l:lettereArray) {
				Periodici periodico = new PeriodiciDao().findByUid(ses, l);
				body +="Risconti per: "+periodico.getNome()+"\r\n";
				body += reportRiscontiByPeriodico(ses, periodico);
				//body += "Abbonamenti omaggio 09: "+countOmaggi+"\r\n";
				body += "\r\n";
			}
			try {
				codiciFile = File.createTempFile("codici_risconti", ".csv");
				FileWriter fw = new FileWriter(codiciFile);
				for (String s:codiciAbbList) {
					fw.append(s + "\r\n");
				}
				fw.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
				throw new JobExecutionException(e);
			}

			//Spedisce il report
			sendReport("[APG] Risconti "+ServerConstants.FORMAT_DAY.format(new Date()),
					recipientArray, body, codiciFile);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		} finally {
			ses.close();
		}
		LOG.info("Ended job '"+jobCtx.getJobDetail().getKey().getName()+"'");
	}
	
	
	private void sendReport(String subject, String[] recipients,
			String messageBody, File codiciFile) {
		try {
			//Mailer.postMail(ServerConstants.SMTP_HOST,
			//		ServerConstants.SMTP_USER,
			//		ServerConstants.SMTP_PASSWORD,
			//		ServerConstants.SMTP_FROM,
			//		recipients, subject, messageBody);
			Mailer.postMail(ServerConstants.SMTP_HOST,
					ServerConstants.SMTP_FROM,
					recipients, subject, messageBody, false, codiciFile);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	private String reportRiscontiByPeriodico(Session ses, Periodici periodico) {
		fascicoliImportiMap = new HashMap<Fascicoli, Double>();
		fascicoliTiraturaMap = new HashMap<Fascicoli, Integer>();
		//countOmaggi = 0;
		String result = "";
		buildRiscontiByPeriodico(ses, periodico);
		Set<Fascicoli> fasSet = fascicoliTiraturaMap.keySet();
		List<String> resultRows = new ArrayList<String>();
		for (Fascicoli fas:fasSet) {
			String row = "Fascicolo "+fas.getTitoloNumero()+" ("+
					ServerConstants.FORMAT_DAY.format(fas.getDataInizio())+") "+
					" tiratura: "+fascicoliTiraturaMap.get(fas) +
					" incasso: "+ServerConstants.FORMAT_CURRENCY.format(fascicoliImportiMap.get(fas));
			resultRows.add(row);
		}
		Collections.sort(resultRows);
		for (String row:resultRows) {
			result += row +"\r\n";
		}
		return result;
	}
	
	private void buildRiscontiByPeriodico(Session ses,
			Periodici periodico) throws HibernateException {
		//Intervallo temporale fascicoli
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.MONTH, -30);//30 mesi = 2,5 anni
		Date dataInizioFascicoli = cal.getTime();
		cal.add(Calendar.YEAR, 10);//10 anni
		Date dataFineFascicoli = cal.getTime();
		//Ciclo
		FascicoliDao fasDao = new FascicoliDao();
		//Fascicoli nextFas = fasDao.findPrimoFascicoloNonSpedito(ses, periodico.getId(), new Date());
		Date expiringFromDate = dataInizioFascicoli; //nextFas.getDataInizio();
		List<Fascicoli> fasList = fasDao.findFascicoliBetweenDates(ses, periodico.getId(),
				dataInizioFascicoli, dataFineFascicoli);
		int offset = 0;
		List<IstanzeAbbonamenti> iaList = null;
		do {
			iaList = findIaEndingInTheFuture(ses, periodico,
					expiringFromDate, offset, QUERY_PAGE_SIZE);
			offset += iaList.size();
			distribuisciFascicoliImporti(ses, iaList, fasList, expiringFromDate);
			ses.clear();
			if (iaList.size() > 0) LOG.info("Risconti "+periodico.getNome()+": "+offset);
		} while (iaList.size() > 0);
	}
	
	@SuppressWarnings("unchecked")
	private List<IstanzeAbbonamenti> findIaEndingInTheFuture(Session ses,
			Periodici periodico, Date expiringFromDate, int offset, int pageSize)
			throws HibernateException {
		String qs = "from IstanzeAbbonamenti ia where " +
				"ia.abbonamento.periodico.id = :id1 and " +
				"ia.invioBloccato = :b1 and " +
				"(ia.pagato = :b2 or ia.inFatturazione = :b3 or " +
					"ia.listino.fatturaDifferita = :b4 or " +
					"ia.listino.prezzo <= :d1) and " +//non sospesi
				"ia.fascicoloFine.dataInizio >= :dt1 " +
				"order by ia.id asc ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", periodico.getId(), IntegerType.INSTANCE);
		q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
		q.setParameter("b2", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b3", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b4", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("d1", AppConstants.SOGLIA, DoubleType.INSTANCE);
		q.setParameter("dt1", expiringFromDate, DateType.INSTANCE);
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<IstanzeAbbonamenti> iaList = q.list();
		return iaList;
	}
	
	private void distribuisciFascicoliImporti(Session ses,
			List<IstanzeAbbonamenti> iaList,
			List<Fascicoli> fasList, Date expiringFromDate) throws HibernateException {
		PagamentiDao pDao = new PagamentiDao();
		//Date today = new Date();
		for (IstanzeAbbonamenti ia:iaList) {
			boolean added = false;
			//Conto omaggi
			//if (ia.getTipoAbbonamentoListino().getTipoAbbonamento().getCodiceTipoAbbonamento().equals("09")) {
			//	countOmaggi += ia.getCopie();
			//}
			if (ia.getFascicoliTotali() > 2) {
				Double importoPagato = pDao.sumPagamentiByIstanza(ses, ia.getId());
				if (importoPagato < AppConstants.SOGLIA) importoPagato = ia.getListino().getPrezzo();
				Double importoPerFascicolo = importoPagato / ia.getFascicoliTotali();
				long inizioIstanza = ia.getFascicoloInizio().getDataInizio().getTime();
				long fineIstanza = ia.getFascicoloFine().getDataInizio().getTime();
				int fasCount = 0;
				for (Fascicoli fas:fasList) {
					//Considera solo i fascicoli tra inizio e scadenza
					if ( (fas.getFascicoliAccorpati() > 0) &&
							(fas.getDataInizio().getTime() >= inizioIstanza) && 
							(fas.getDataInizio().getTime() <= fineIstanza)){
						fasCount += fas.getFascicoliAccorpati();
						//Tiratura
						Integer tiratura = fascicoliTiraturaMap.get(fas);
						if (tiratura == null) tiratura = 0;
						tiratura += ia.getCopie();
						fascicoliTiraturaMap.put(fas, tiratura);
						//Incassato
						Double incassato = fascicoliImportiMap.get(fas);
						if (incassato == null) incassato = 0D;
						incassato += importoPerFascicolo;
						fascicoliImportiMap.put(fas, incassato);
						added = true;
					}
				}
				//Verifica di aver incluso tutti i fascicoli:
				//la conta dei fascicoli deve essere il numero previsto dall'abbonamento,
				//se tutta sua durata è inclusa nel calcolo
				if ( (fasCount != ia.getFascicoliTotali()) &&
						(ia.getFascicoloInizio().getDataInizio().getTime() >= expiringFromDate.getTime()) ) {
					LOG.warn(ia.getAbbonamento().getCodiceAbbonamento()+" ("+
						ServerConstants.FORMAT_DAY.format(ia.getFascicoloInizio().getDataInizio())+"-"+
						ServerConstants.FORMAT_DAY.format(ia.getFascicoloFine().getDataFine())+") "+
						"fas. contati:"+fasCount+" previsti:"+ia.getFascicoliTotali());
				}
			}
			if (added) {
				codiciAbbList.add(ia.getAbbonamento().getCodiceAbbonamento()+";"+
						ServerConstants.FORMAT_DAY.format(ia.getFascicoloInizio().getDataInizio()));
			}
		}
	}

}
