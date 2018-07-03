package it.giunti.apg.updater;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.DateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.business.SearchBusiness;
import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Anagrafiche;

public class UpdateAnagraficaCreazione {

	private static final Logger LOG = LoggerFactory.getLogger(UpdateAnagraficaCreazione.class);
	
	private static int PAGE_SIZE = 100;
	private static AnagraficheDao anagDao = new AnagraficheDao();
	private static DecimalFormat df = new DecimalFormat("0.00");
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static String DATE_2018_STRING = "15/01/2018";
	private static Date DATE_2018 = null; 
	
	@SuppressWarnings("unchecked")
	public static void update() 
			throws BusinessException, IOException {
		try {
			DATE_2018 = ServerConstants.FORMAT_DAY.parse(DATE_2018_STRING);
		} catch (ParseException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		List<Anagrafiche> aList = new ArrayList<Anagrafiche>();
		Integer offset = 0;
		try {
			String hql = "select count(a.id) from Anagrafiche a where a.dataCreazione >= :dt1 ";
			Query q = ses.createQuery(hql);
			q.setParameter("dt1", DATE_2018, DateType.INSTANCE);
			Object result = q.uniqueResult();
			Long totalAnag = (Long) result;
			System.out.println("Totale anagrafiche: "+totalAnag);
			Date dtStart = new Date();
			ReportWriter rw = new ReportWriter("anagraficaUpdate");
			//Update Anagrafiche
			hql = "from Anagrafiche a where a.dataCreazione >= :dt1 order by a.id ";
			do {
				q = ses.createQuery(hql);
				q.setParameter("dt1", DATE_2018, DateType.INSTANCE);
				q.setFirstResult(offset);
				q.setMaxResults(PAGE_SIZE);
				aList = (List<Anagrafiche>) q.list();
				for (Anagrafiche a:aList) {
					Date oldCreation = a.getDataCreazione();
					changeDataCreazione(ses, a);
					a.setSearchString(SearchBusiness.buildAnagraficheSearchString(a));
					if (oldCreation.after(a.getDataCreazione())) a.setDataModifica(DateUtil.now());
					anagDao.updateUnlogged(ses, a);
					String logLine = a.getUid()+";"+a.getIndirizzoPrincipale().getCognomeRagioneSociale()+";"+
							ServerConstants.FORMAT_DAY.format(oldCreation)+";"+
							ServerConstants.FORMAT_DAY.format(a.getDataCreazione());
					LOG.info(logLine);
					rw.println(logLine);
				}
				offset += aList.size();
				Double perc = 100*(offset.doubleValue()/totalAnag.doubleValue());
				System.out.println("Aggiornate "+offset+" anagrafiche ("+df.format(perc)+"%) "+
						"fine stimata "+stimaFine(dtStart, offset, totalAnag));
				ses.flush();
				ses.clear();
				trn.commit();
				trn = ses.beginTransaction();
			} while (aList.size() == PAGE_SIZE);
			rw.close();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	
	private static String stimaFine(Date dtInizio, Integer offset, Long total) {
		Date now = new Date();
		Long elapsed = now.getTime()-dtInizio.getTime();
		Double forecastDouble = elapsed.doubleValue()*total.doubleValue()/offset.doubleValue();
		Long forecastTime = forecastDouble.longValue() + dtInizio.getTime();
		Date forecastDt = new Date(forecastTime);
		return sdf.format(forecastDt);
	}
		
	@SuppressWarnings("unchecked")
	private static void changeDataCreazione(Session ses, Anagrafiche a) {
		String sql = "select MIN(ia.data_creazione) "+
				"from istanze_abbonamenti ia where ia.id_abbonato = :id1 ";
		Query q = ses.createSQLQuery(sql);
		q.setParameter("id1", a.getId());
		List<Object> list = (List<Object>) q.list();
		if (list.size() > 0) {
			Object obj = list.get(0);
			if (obj != null) {
				Date dataCreazione = (Date) obj;
				a.setDataCreazione(dataCreazione);
				if (a.getDataAggiornamentoConsenso().before(DATE_2018))
					a.setDataAggiornamentoConsenso(dataCreazione);
			} else {
				changeDataCreazionePagante(ses, a);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void changeDataCreazionePagante(Session ses, Anagrafiche a) {
		String sql = "select MIN(ia.data_creazione) "+
				"from istanze_abbonamenti ia where ia.id_pagante = :id1 ";
		Query q = ses.createSQLQuery(sql);
		q.setParameter("id1", a.getId());
		List<Object> list = (List<Object>) q.list();
		if (list.size() > 0) {
			Object obj = list.get(0);
			if (obj != null) {
				Date dataCreazione = (Date) obj;
				a.setDataCreazione(dataCreazione);
				if (a.getDataAggiornamentoConsenso().before(DATE_2018))
					a.setDataAggiornamentoConsenso(dataCreazione);
			} else {
				changeDataCreazionePromotore(ses, a);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void changeDataCreazionePromotore(Session ses, Anagrafiche a) {
		String sql = "select MIN(ia.data_creazione) "+
				"from istanze_abbonamenti ia where ia.id_promotore = :id1 ";
		Query q = ses.createSQLQuery(sql);
		q.setParameter("id1", a.getId());
		List<Object> list = (List<Object>) q.list();
		if (list.size() > 0) {
			Object obj = list.get(0);
			if (obj != null) {
				Date dataCreazione = (Date) obj;
				a.setDataCreazione(dataCreazione);
				if (a.getDataAggiornamentoConsenso().before(DATE_2018))
					a.setDataAggiornamentoConsenso(dataCreazione);
			} else {
				a.setDataCreazione(a.getDataModifica());
				if (a.getDataAggiornamentoConsenso().before(DATE_2018))
					a.setDataAggiornamentoConsenso(a.getDataModifica());
			}
		}
	}
	
	
	// inner classes
	
	
	private static class ReportWriter {
		private FileWriter writer = null;
		
		public ReportWriter(String fileName) throws IOException {
			File report = File.createTempFile(fileName, ".csv");
			LOG.info("Report su "+report.getAbsolutePath());
			writer = new FileWriter(report);
		}
		
		public void println(String report) 
				throws IOException {
			String line = report +"\r\n";
			writer.write(line);
		}
		
		public void close() throws IOException {
			writer.close();
		}
	}
}
