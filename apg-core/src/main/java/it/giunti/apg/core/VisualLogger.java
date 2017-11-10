package it.giunti.apg.core;

import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.RapportiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Rapporti;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisualLogger {
	
	private static final Logger LOG = LoggerFactory.getLogger(VisualLogger.class);
	private static final SimpleDateFormat SDF_LOG = new SimpleDateFormat("HH:mm:ss");
	
	private static VisualLogger instance = null;
	private final Map<Integer, LogBundle> logBundleMap = new HashMap<Integer, LogBundle>(); // <idRapporto, LogBundle>
	
	private VisualLogger() {}
	
	public static VisualLogger get() {
		if (instance == null) {
			instance = new VisualLogger();
		}
		return instance;
	}
	
	public Integer createRapporto(String titolo, String idUtente) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		Integer idRapporto = null;
		RapportiDao rapDao = new RapportiDao();
		try {
			Rapporti rapportoT = new Rapporti();
			rapportoT.setDataModifica(DateUtil.now());
			rapportoT.setTerminato(false);
			rapportoT.setErrore(false);
			rapportoT.setTesto("");
			rapportoT.setTitolo(titolo);
			rapportoT.setIdUtente(idUtente);
			idRapporto = (Integer) rapDao.save(ses, rapportoT);
			Rapporti rapporto = GenericDao.findById(ses, Rapporti.class, idRapporto);
			//Mette il relativo logBundle nella mappa, per avere un accesso veloce ai dati di log in lettura/scrittura
			LogBundle bundle = new LogBundle();
			bundle.setLogList(new ArrayList<String>());
			bundle.setRapporto(rapporto);
			bundle.setErrore(false);
			logBundleMap.put(idRapporto, bundle);
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return idRapporto;
	}
	
	public void addHtmlInfoLine(Integer idRapporto, String htmlLogLine) {
		//Aggiunge riga nella List e nell'oggetto Rapporto
		htmlLogLine = SDF_LOG.format(DateUtil.now()) + " - " + htmlLogLine;
		if (idRapporto != null) {
			LogBundle bundle = logBundleMap.get(idRapporto);
			bundle.getLogList().add(htmlLogLine);
		}
		LOG.info(htmlLogLine);
	}
	
	public void addHtmlErrorLine(Integer idRapporto, String htmlLogLine) {
		addHtmlErrorLine(idRapporto, htmlLogLine, null);
	}
	
	public void addHtmlErrorLine(Integer idRapporto, String htmlLogLine, Exception e) {
		//Aggiunge riga nella List e nell'oggetto Rapporto
		htmlLogLine = SDF_LOG.format(DateUtil.now()) + " - " + htmlLogLine;
		if (idRapporto != null) {
			LogBundle bundle = logBundleMap.get(idRapporto);
			bundle.getLogList().add(htmlLogLine);
			bundle.setErrore(true);
		}
		if (e != null) {
			LOG.error(htmlLogLine, e);
		} else {
			LOG.error(htmlLogLine);
		}
	}
	
	public List<String> getLogFromLine(Integer idRapporto, int fromLine) {
		List<String> result = new ArrayList<String>();
		List<String> logList = logBundleMap.get(idRapporto).getLogList();
		if (logList.size() > fromLine) {
			for (int i=fromLine; i<logList.size(); i++) {
				result.add(logList.get(i));
			}
		}
		return result;
	}
	
	public void setLogTitle(Integer idRapporto, String title) {
		LogBundle bundle = logBundleMap.get(idRapporto);
		bundle.getRapporto().setTitolo(title);
	}
	
	public String getLogTitle(Integer idRapporto) {
		LogBundle bundle = logBundleMap.get(idRapporto);
		return bundle.getRapporto().getTitolo();
	}
	
	public void closeAndSaveRapporto(Integer idRapporto) throws BusinessException {
		addHtmlInfoLine(idRapporto, AppConstants.MSG_LOG_END);
		//Aggiunge riga nella List e nell'oggetto Rapporto
		Rapporti rapporto = logBundleMap.get(idRapporto).getRapporto();
		List<String> logList = logBundleMap.get(idRapporto).getLogList();
		boolean errore = logBundleMap.get(idRapporto).getErrore();
		String body = "";
		for (String line:logList) {
			body += line + "<br />\r\n";
		}
		rapporto.setTesto(body);
		rapporto.setTerminato(true);
		rapporto.setErrore(errore);
		//l'oggetto Rapporto viene aggiornato su DB
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		RapportiDao rapDao = new RapportiDao();
		try {
			rapDao.update(ses, rapporto);
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
		

	
	//Inner classes
	
	
	
	protected class LogBundle {
		
		private Rapporti rapporto = new Rapporti();
		private List<String> logList = new ArrayList<String>();
		private boolean errore = false;
		
		public Rapporti getRapporto() {
			return rapporto;
		}
		public void setRapporto(Rapporti rapporto) {
			this.rapporto = rapporto;
		}
		public List<String> getLogList() {
			return logList;
		}
		public void setLogList(List<String> logList) {
			this.logList = logList;
		}
		public boolean getErrore() {
			return errore;
		}
		public void setErrore(boolean errore) {
			this.errore = errore;
		}
	}
}
