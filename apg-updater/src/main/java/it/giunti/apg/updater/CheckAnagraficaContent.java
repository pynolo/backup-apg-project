package it.giunti.apg.updater;

import it.giunti.apg.core.business.CharsetUtil;
import it.giunti.apg.core.business.SearchBusiness;
import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.updater.archive.ChangeNazione;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckAnagraficaContent {

	private static final Logger LOG = LoggerFactory.getLogger(ChangeNazione.class);
	
	private static int PAGE_SIZE = 1000;
	private static AnagraficheDao anagDao = new AnagraficheDao();
	private static DecimalFormat df = new DecimalFormat("0.00");
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
	private static Map<String, String> getDescrTipoMap() {
		Map<String, String> descrTipoMap = new HashMap<String, String>();
		// MUST BE LOWERCASE!!!
		descrTipoMap.put("agenzia", AppConstants.ANAG_AZIENDA);
		descrTipoMap.put("associazion", AppConstants.ANAG_AZIENDA);
		descrTipoMap.put("az.usl", AppConstants.ANAG_AZIENDA);
		descrTipoMap.put("az. usl", AppConstants.ANAG_AZIENDA);
		descrTipoMap.put("coop", AppConstants.ANAG_AZIENDA);
		descrTipoMap.put("cooper", AppConstants.ANAG_AZIENDA);
		descrTipoMap.put("ufficio", AppConstants.ANAG_AZIENDA);
		descrTipoMap.put("uff.", AppConstants.ANAG_AZIENDA);
		descrTipoMap.put("fondazion", AppConstants.ANAG_AZIENDA);
		descrTipoMap.put("fond.", AppConstants.ANAG_AZIENDA);
		descrTipoMap.put("archeo", AppConstants.ANAG_AZIENDA);
		descrTipoMap.put("studio", AppConstants.ANAG_AZIENDA);
		descrTipoMap.put("stud.", AppConstants.ANAG_AZIENDA);
		descrTipoMap.put("comune", AppConstants.ANAG_AZIENDA);
		
		descrTipoMap.put("libreria", AppConstants.ANAG_LIBRERIA);
		descrTipoMap.put("lib.", AppConstants.ANAG_LIBRERIA);
		descrTipoMap.put("cartoleria", AppConstants.ANAG_LIBRERIA);

		descrTipoMap.put("istitut", AppConstants.ANAG_SCUOLA);
		descrTipoMap.put("didattic", AppConstants.ANAG_SCUOLA);
		descrTipoMap.put("scuola ", AppConstants.ANAG_SCUOLA);
		descrTipoMap.put("scuole ", AppConstants.ANAG_SCUOLA);
		descrTipoMap.put("asilo", AppConstants.ANAG_SCUOLA);
		descrTipoMap.put("universita", AppConstants.ANAG_SCUOLA);
		descrTipoMap.put("nido ", AppConstants.ANAG_SCUOLA);
		descrTipoMap.put("nidi ", AppConstants.ANAG_SCUOLA);
		descrTipoMap.put("dir.did.", AppConstants.ANAG_SCUOLA);
		descrTipoMap.put("dir. did.", AppConstants.ANAG_SCUOLA);
		descrTipoMap.put("ist.", AppConstants.ANAG_SCUOLA);
		descrTipoMap.put("i.t.c", AppConstants.ANAG_SCUOLA);
		descrTipoMap.put("itc.", AppConstants.ANAG_SCUOLA);
		descrTipoMap.put("ics.", AppConstants.ANAG_SCUOLA);
		descrTipoMap.put("univ.", AppConstants.ANAG_SCUOLA);
		descrTipoMap.put("accademia", AppConstants.ANAG_SCUOLA);
		descrTipoMap.put("liceo", AppConstants.ANAG_SCUOLA);
		descrTipoMap.put("scolastico", AppConstants.ANAG_SCUOLA);

		descrTipoMap.put("bib.", AppConstants.ANAG_BIBLIOTECA);
		descrTipoMap.put("biblioteca", AppConstants.ANAG_BIBLIOTECA);
		descrTipoMap.put("bibl.", AppConstants.ANAG_BIBLIOTECA);
		return descrTipoMap;
	}
	
	@SuppressWarnings("unchecked")
	public static void update() 
			throws BusinessException, IOException {
		
		Map<String, String> descrTipoMap = getDescrTipoMap();
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		List<Anagrafiche> aList = new ArrayList<Anagrafiche>();
		Integer offset = 0;
		try {
			String hql = "select count(id) from Anagrafiche";
			Object result = ses.createQuery(hql).uniqueResult();
			Long totalAnag = (Long) result;
			System.out.println("Totale anagrafiche: "+totalAnag);
			Date dtStart = new Date();
			ReportWriter rw = new ReportWriter("anagraficaUpdate");
			//Update Anagrafiche
			hql = "from Anagrafiche a order by a.id";
			do {
				Query q = ses.createQuery(hql);
				q.setFirstResult(offset);
				q.setMaxResults(PAGE_SIZE);
				aList = (List<Anagrafiche>) q.list();
				for (Anagrafiche a:aList) {
					a.setSearchString(SearchBusiness.buildAnagraficheSearchString(a));
					changeTipoAnagrafica(a, descrTipoMap, rw);
					anagDao.updateUnlogged(ses, a);
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
	
	private static void changeTipoAnagrafica(Anagrafiche a, Map<String, String> descrTipoMap, ReportWriter rw) 
			throws IOException {
		if (a.getIdTipoAnagrafica().equals(AppConstants.ANAG_PRIVATO)) {
			String receiver = a.getIndirizzoPrincipale().getCognomeRagioneSociale().toLowerCase()+" ";
			if (a.getIndirizzoPrincipale().getNome() != null)
					receiver += a.getIndirizzoPrincipale().getNome().toLowerCase()+" ";
			//if (a.getIndirizzoPrincipale().getPresso() != null)
			//		receiver += a.getIndirizzoPrincipale().getPresso().toLowerCase()+" ";
			receiver = CharsetUtil.normalize(receiver);
			Set<String> descrSet = descrTipoMap.keySet();
			for (String descr:descrSet) {
				if (receiver.contains(descr) && a.getIdTipoAnagrafica().equals(AppConstants.ANAG_PRIVATO)) {
					a.setIdTipoAnagrafica(descrTipoMap.get(descr));
					LOG.info(receiver+"> "+descrTipoMap.get(descr));
					rw.print(receiver+"> "+descrTipoMap.get(descr));
				}
			}
		}
		//if (a.getIdTipoAnagrafica().equals(AppConstants.ANAG_PRIVATO)) {
		//	if (a.getPartitaIva() != null) {
		//		if (a.getPartitaIva().length() > 6) {
		//			a.setPartitaIva(AppConstants.ANAG_AZIENDA);
		//		}
		//	}
		//}
	}
	
	private static class ReportWriter {
		private FileWriter writer = null;
		
		public ReportWriter(String fileName) throws IOException {
			File report = File.createTempFile(fileName, ".csv");
			LOG.info("Report su "+report.getAbsolutePath());
			writer = new FileWriter(report);
		}
		
		public void print(String report) 
				throws IOException {
			String line = report +"\r\n";
			writer.write(line);
		}
		
		public void close() throws IOException {
			writer.close();
		}
	}
}
