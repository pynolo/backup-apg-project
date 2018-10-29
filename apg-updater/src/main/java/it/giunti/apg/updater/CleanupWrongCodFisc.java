package it.giunti.apg.updater;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

public class CleanupWrongCodFisc {

	private static final Logger LOG = LoggerFactory.getLogger(CleanupWrongCodFisc.class);
	private static final String SEP = ";";
	private static final int PAGE_SIZE = 1000;
	private static final int LIST_SIZE = 32;
	
	private static AnagraficheDao anagDao = new AnagraficheDao();
	private static DecimalFormat df = new DecimalFormat("0.00");
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Date today = DateUtil.now();
	
	public static void run() throws IOException {
		ReportWriter reportWriter = new ReportWriter("wrongCodFiscList_");
		parseData(reportWriter);
		reportWriter.close();
	}
	
	@SuppressWarnings("unchecked")
	public static void parseData(ReportWriter writer) 
			throws IOException {		
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		List<Anagrafiche> aList = new ArrayList<Anagrafiche>();
		int errorCount = 0;
		int emptyCount = 0;
		Integer offset = 0;
		try {
			String hql = "select count(id) from Anagrafiche";
			Object result = ses.createQuery(hql).uniqueResult();
			Long totalAnag = (Long) result;
			LOG.info("Totale anagrafiche: "+totalAnag);
			Date dtStart = new Date();
			//Update Anagrafiche
			hql = "from Anagrafiche a order by a.id";
			do {
				Query q = ses.createQuery(hql);
				q.setFirstResult(offset);
				q.setMaxResults(PAGE_SIZE);
				aList = (List<Anagrafiche>) q.list();
				for (Anagrafiche a:aList) {
					boolean isVerified = verifyAndUpdate(ses, a);
					boolean isEmpty = isEmpty(a);
					if (!isVerified || isEmpty) {
						String desc = (isVerified?"VUOTO":"ERRORE");
						List<IstanzeAbbonamenti> iaBenList = findIstanzeProprieByAnagraficaFineFutura(ses, a.getId(), today, 0, Integer.MAX_VALUE);
						List<IstanzeAbbonamenti> iaPagList = findIstanzeRegalateByAnagraficaFineFutura(ses, a.getId(), today, 0, Integer.MAX_VALUE);
						writer.print(a, desc, iaBenList, iaPagList);
						if (!isVerified) errorCount++;
						if (isEmpty) emptyCount++;
					}
				}
				offset += aList.size();
				Double perc = 100*(offset.doubleValue()/totalAnag.doubleValue());
				LOG.info("Verifiche:"+offset+"("+df.format(perc)+"%) "+
						"Stima fine:"+stimaFine(dtStart, offset, totalAnag)+
						" Errori:"+errorCount+" Vuoti:"+emptyCount);
				ses.flush();
				ses.clear();
			} while (aList.size() == PAGE_SIZE);
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			e.printStackTrace();
			//throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	private static boolean verifyAndUpdate(Session ses, Anagrafiche a) {
		boolean isCodFisVerified = true;
		if (a.getCodiceFiscale() != null) {
			if (a.getCodiceFiscale().length() > 0) {
				isCodFisVerified = ValueUtil.isValidCodFisc(a.getCodiceFiscale(), 
						a.getIndirizzoPrincipale().getNazione().getId());
			}
		}
		if (!isCodFisVerified) {
			a.setNote(a.getNote()+" C.F. rimosso:"+a.getCodiceFiscale());
			a.setCodiceFiscale("");
			a.setDataModifica(DateUtil.now());
			anagDao.updateUnlogged(ses, a);
		}
		boolean isPIvaVerified = true;
		if (a.getPartitaIva() != null) {
			if (a.getPartitaIva().length() > 0) {
				isPIvaVerified = ValueUtil.isValidPartitaIva(a.getPartitaIva(), 
						a.getIndirizzoPrincipale().getNazione().getId());
			}
		}
		if (!isPIvaVerified) {
			a.setNote(a.getNote()+" P.iva rimossa:"+a.getPartitaIva());
			a.setPartitaIva("");
			a.setDataModifica(DateUtil.now());
			anagDao.updateUnlogged(ses, a);
		}
		return isCodFisVerified && isPIvaVerified;
	}
	
	private static boolean isEmpty(Anagrafiche a) {
		boolean isCodFisEmpty = false;
		if (a.getCodiceFiscale() != null) {
			isCodFisEmpty = (a.getCodiceFiscale().length() == 0);
		} else isCodFisEmpty = true;
		
		boolean isPIvaEmpty = false;
		if (a.getPartitaIva() != null) {
			isPIvaEmpty = (a.getPartitaIva().length() == 0);
		} else isPIvaEmpty = true;
		
		return isCodFisEmpty && isPIvaEmpty;
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
	public static List<IstanzeAbbonamenti> findIstanzeProprieByAnagraficaFineFutura(Session ses,
			Integer idAnanagrafica, Date date, int offset, int pageSize) throws HibernateException {
		String qs = "from IstanzeAbbonamenti ia where " +
				"ia.abbonato.id = :id1 and " + 
				"ia.fascicoloFine.dataFine >= :dt2 " +
				"order by ia.dataCreazione desc ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idAnanagrafica, IntegerType.INSTANCE);
		q.setParameter("dt2", date, DateType.INSTANCE);
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
		return abbList;
	}
	
	@SuppressWarnings("unchecked")
	public static List<IstanzeAbbonamenti> findIstanzeRegalateByAnagraficaFineFutura(Session ses,
			Integer idAnanagrafica, Date date, int offset, int pageSize) throws HibernateException {
		String qs = "from IstanzeAbbonamenti ia where " +
				"ia.pagante.id = :id1 and " + 
				"ia.fascicoloFine.dataFine >= :dt2 " +
				"order by ia.dataCreazione desc ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idAnanagrafica, IntegerType.INSTANCE);
		q.setParameter("dt2", date, DateType.INSTANCE);
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
		return abbList;
	}
	
	
	// Inner classes
	
	
	public static class ReportWriter {
		private FileWriter writer = null;
		
		public ReportWriter(String fileNamePrefix) throws IOException {
			File report = File.createTempFile(fileNamePrefix, ".csv");
			LOG.info("Report su "+report.getAbsolutePath());
			writer = new FileWriter(report);
		}
		
		public void print(Anagrafiche ana, String desc, List<IstanzeAbbonamenti> iaBenList, List<IstanzeAbbonamenti> iaPagList) throws IOException {
			Indirizzi ind = ana.getIndirizzoPrincipale();
			String line = desc+SEP+ 
					ana.getUid()+SEP+
					ind.getCognomeRagioneSociale()+SEP+
					(ind.getNome()==null ? "" : ind.getNome())+SEP+
					(ind.getPresso()==null ? "" : ind.getPresso())+SEP+
					ind.getIndirizzo()+SEP+
					(ind.getCap()==null ? "" : ind.getCap())+SEP+
					ind.getLocalita()+SEP+
					(ind.getProvincia()==null ? "" : ind.getProvincia())+SEP+
					ind.getNazione().getNomeNazione()+SEP+
					(ana.getTelCasa()==null ? "" : ana.getTelCasa())+SEP+
					(ana.getTelMobile()==null ? "" : ana.getTelMobile())+SEP+
					(ana.getEmailPrimaria()==null ? "" : ana.getEmailPrimaria())+SEP;
			String iaBen = "";
			for (IstanzeAbbonamenti ia:iaBenList) iaBen += ia.getAbbonamento().getCodiceAbbonamento()+" ";
			if (iaBen.length() > LIST_SIZE) iaBen = iaBen.substring(0, LIST_SIZE)+"…";
			String iaPag = "";
			for (IstanzeAbbonamenti ia:iaPagList) iaPag += ia.getAbbonamento().getCodiceAbbonamento()+" ";
			if (iaPag.length() > LIST_SIZE) iaPag = iaPag.substring(0, LIST_SIZE)+"…";
			line += iaBen+SEP+iaPag;
			line = line.replaceAll("\"", "");
			LOG.debug(line);
			writer.write(line+"\r\n");
		}
		
		public void close() throws IOException {
			writer.close();
		}
		
	}
}
