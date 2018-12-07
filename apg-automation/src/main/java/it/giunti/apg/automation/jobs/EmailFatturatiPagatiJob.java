package it.giunti.apg.automation.jobs;

import it.giunti.apg.core.LocalMailer;
import it.giunti.apg.core.PropertyReader;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.PeriodiciDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.IndirizziUtil;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.Periodici;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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

public class EmailFatturatiPagatiJob implements Job {
	
	static private Logger LOG = LoggerFactory.getLogger(EmailFatturatiPagatiJob.class);
	static private final String SEP = ";";
	static private final String EOL = "\r\n";
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		LOG.info("Started job '"+jobCtx.getJobDetail().getKey().getName()+"'");
		//param: letterePeriodici
		String letterePeriodici = (String) jobCtx.getMergedJobDataMap().get("letterePeriodici");
		if (letterePeriodici == null) throw new JobExecutionException("letterePeriodici non definito");
		if (letterePeriodici.equals("")) throw new JobExecutionException("letterePeriodici non definito");
		String[] lettereArray = letterePeriodici.split(AppConstants.STRING_SEPARATOR);
		//param: tipiEsclusi
		String tipiEsclusi = (String) jobCtx.getMergedJobDataMap().get("tipiEsclusi");
		if (tipiEsclusi == null) throw new JobExecutionException("tipiEsclusi non definito");
		if (tipiEsclusi.equals("")) throw new JobExecutionException("tipiEsclusi non definito");
		String[] tipiEsclusiArray = tipiEsclusi.split(AppConstants.STRING_SEPARATOR);
		//param: backwardDays
		String backwardDaysString = (String) jobCtx.getMergedJobDataMap().get("backwardDays");
		Integer backwardDays = ValueUtil.stoi(backwardDaysString);
		if (backwardDays == null) throw new JobExecutionException("Non sono definiti i giorni di aggiornamento");
		//param: emailRecipients
		String emailRecipients = (String) jobCtx.getMergedJobDataMap().get("emailRecipients");
		if (emailRecipients == null) throw new JobExecutionException("emailRecipients non definito");
		if (emailRecipients.equals("")) throw new JobExecutionException("emailRecipients non definito");
		String[] recipientArray = emailRecipients.split(AppConstants.STRING_SEPARATOR);
		//param: testEmailRecipient
		String testEmailRecipient = (String) jobCtx.getMergedJobDataMap().get("testEmailRecipient");
		//test or dev environment?
		//param: test
		boolean test = true;
		String testParam = (String) jobCtx.getMergedJobDataMap().get("test");
		if (testParam != null) {
			if (testParam.equals("false")) test = false;
		}
		String suffix = PropertyReader.getApgStatus();
		if (AppConstants.APG_PROD.equalsIgnoreCase(suffix)) test = false;
				
		//JOB
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, (-1)*backwardDays);
		Date dateLimit = cal.getTime();
		String body = "";
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			File csvFile = null;
			body +="Abbonamenti in fatturazione e pagati"+EOL+EOL;
			List<Pagamenti> pagList = new ArrayList<Pagamenti>();
			List<String> pagRows = new ArrayList<String>();
			for (String l:lettereArray) {
				Periodici periodico = new PeriodiciDao().findByUid(ses, l);
				List<Pagamenti> periodicoPagList = findPagamentiList(ses, periodico, tipiEsclusiArray, dateLimit);
				List<String> periodicoRows = reportPagRows(ses, periodicoPagList);
				body += "Periodico '"+periodico.getNome()+"': "+periodicoRows.size()+EOL;
				pagList.addAll(periodicoPagList);
				pagRows.addAll(periodicoRows);
				LOG.info("Periodico '"+periodico.getNome()+"': "+periodicoRows.size());
			}
			if (pagRows.size() > 0) {
				//Crea l'allegato
				try {
					csvFile = File.createTempFile("fatturati_pagati", ".csv");
					FileWriter fw = new FileWriter(csvFile);
					fw.append(formatHeader()+EOL);
					for (String s:pagRows) {
						fw.append(s + EOL);
					}
					fw.close();
					LOG.info("File fatturati pagati temporaneo su "+csvFile.getAbsolutePath());
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
					throw new JobExecutionException(e);
				}
				//Spedisce il report
				String[] recipients =  recipientArray;
				String[] testRecipients =  { testEmailRecipient };
				if (test) recipients = testRecipients;
				sendReport("[APG] Abbonamenti fatturati e pagati "+ServerConstants.FORMAT_DAY.format(DateUtil.now()),
						recipients, body, csvFile);
				//Marca le istanze notificate
				if (!test) {
					for (Pagamenti pag:pagList) {
						IstanzeAbbonamenti ia = pag.getIstanzaAbbonamento();
						ia.setFatturaPagata(true);
						ses.update(ia);
					}
					LOG.info(pagList.size()+" marcati come pagamenti segnalati");
				}
			}
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		} finally {
			ses.close();
		}
		LOG.info("Ended job '"+jobCtx.getJobDetail().getKey().getName()+"'");
	}
	
	
	private void sendReport(String subject, String[] recipients,
			String messageBody, File csvFile) {
		try {
			LocalMailer.postMail(ServerConstants.SMTP_HOST,
					ServerConstants.SMTP_FROM,
					recipients, subject, messageBody, false, csvFile);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	private List<String> reportPagRows(Session ses, List<Pagamenti> pagList) {
		List<String> pagRows = new ArrayList<String>();
		for (Pagamenti pag:pagList) {
			pagRows.add(formatPagamento(pag));
		}
		return pagRows;
	}
	
	@SuppressWarnings("unchecked")
	private List<Pagamenti> findPagamentiList(Session ses,
			Periodici periodico, String[] tipiEsclusiArray, 
			Date dateLimit) throws HibernateException {
		String qs = "from Pagamenti pag where " +
				"pag.istanzaAbbonamento.abbonamento.periodico.id = :id1 and " +
				"pag.istanzaAbbonamento.invioBloccato = :b1 and " +//false
				"pag.istanzaAbbonamento.pagato = :b2 and "+//true
				"pag.istanzaAbbonamento.inFatturazione = :b3 and " +//true
				"pag.istanzaAbbonamento.listino.tipoAbbonamento.codice not in (:list) and "+
				"pag.istanzaAbbonamento.fatturaPagata = :b4 and " +//false
				"pag.dataAccredito > :dt1 "+
				"order by pag.id asc ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", periodico.getId(), IntegerType.INSTANCE);
		q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
		q.setParameter("b2", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b3", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameterList("list", tipiEsclusiArray, StringType.INSTANCE);
		q.setParameter("b4", Boolean.FALSE, BooleanType.INSTANCE);
		q.setParameter("dt1", dateLimit, DateType.INSTANCE);
		//q.setFirstResult(offset);
		//q.setMaxResults(pageSize);
		List<Pagamenti> pagList = q.list();
		return pagList;
	}
	
	private String formatHeader() {
		String row = "";
		row += "Abbonamento"+SEP;
		row += "Ragione soc."+SEP;
		row += "Presso"+SEP;
		row += "CAP"+SEP;
		row += "Localita'"+SEP;
		row += "Importo"+SEP;
		row += "Pagamento"+SEP;
		row += "Registrazione"+SEP;
		row += "Tipo"+SEP;
		row += "Cod.SAP"+SEP;
		row += "Num.Fattura"+SEP;
		return row;
	}
	private String formatPagamento(Pagamenti pag) {
		String row = "";
		IstanzeAbbonamenti ia = pag.getIstanzaAbbonamento();
		row += ia.getAbbonamento().getCodiceAbbonamento()+SEP;
		Anagrafiche pagante = ia.getPagante();
		if (pagante == null) pagante = ia.getAbbonato();
		Indirizzi indFatt = pagante.getIndirizzoPrincipale();
		if (IndirizziUtil.isFilledUp(pagante.getIndirizzoFatturazione()))
			indFatt = pagante.getIndirizzoFatturazione();
		if (indFatt.getNome() == null) {
			row += indFatt.getCognomeRagioneSociale()+SEP;
		} else {
			row += indFatt.getCognomeRagioneSociale()+" "+indFatt.getNome()+SEP;
		}
		row += indFatt.getPresso()+SEP;
		row += indFatt.getCap()+SEP;
		row += indFatt.getLocalita()+SEP;
		row += pag.getImporto()+SEP;
		if (pag.getDataPagamento() != null) row += ServerConstants.FORMAT_DAY.format(pag.getDataPagamento());
		row += SEP;
		if (pag.getDataAccredito() != null) row += ServerConstants.FORMAT_DAY.format(pag.getDataAccredito());
		row += SEP;
		row += AppConstants.PAGAMENTO_DESC.get(pag.getIdTipoPagamento())+SEP;
		if (pagante.getCodiceSap() != null) row += pagante.getCodiceSap();
		row += SEP;
		row += ia.getFatturaNumero()+SEP;
		return row;
	}

}
