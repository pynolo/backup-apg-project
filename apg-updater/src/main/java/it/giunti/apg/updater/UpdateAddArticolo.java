package it.giunti.apg.updater;

import it.giunti.apg.server.ServerConstants;
import it.giunti.apg.server.persistence.AbbonamentiDao;
import it.giunti.apg.server.persistence.ArticoliDao;
import it.giunti.apg.server.persistence.EvasioniArticoliDao;
import it.giunti.apg.server.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.Articoli;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateAddArticolo {
	
	private static final Logger LOG = LoggerFactory.getLogger(UpdateAddArticolo.class);
	
	public static Date DATA_ARTICOLO = new Date();
	private static final String SEPARATOR_REGEX = "\\;";
	
	private static AbbonamentiDao aDao = new AbbonamentiDao();
	private static IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
	private static EvasioniArticoliDao eaDao = new EvasioniArticoliDao();
	
	public static void parseFileAddArticolo(String csvFilePath, String cmArticolo) 
			throws BusinessException, IOException {
		File csvFile = new File(csvFilePath);
		FileInputStream fstream = new FileInputStream(csvFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		int count = 0;
		
		try {
			Session ses = SessionFactory.getSession();
			Articoli art = null;
			try {
				art = new ArticoliDao().findByCm(ses, cmArticolo);
			} catch (HibernateException e) {
				throw new BusinessException(e.getMessage(), e);
			} finally {
				ses.close();
			}
			
			//Ciclo su tutte le righe
			LOG.info("Aggiunta articolo '"+art.getTitoloNumero()+"' "+art.getCodiceMeccanografico()+
					" ad abb. nel file "+csvFile.getAbsolutePath()+ " in data "+
					ServerConstants.FORMAT_DAY.format(DATA_ARTICOLO));
			String line = br.readLine();
			while (line != null) {
				//Sessione singola istanza
				ses = SessionFactory.getSession();
				Transaction trn = ses.beginTransaction();
				try {
					parseAddArticolo(ses, line, art);
					count++;
					if (count%100 == 0) {
						ses.flush();
						LOG.info(count+" righe");
					}
					line = br.readLine();
					trn.commit();
				} catch (HibernateException e) {
					trn.rollback();
					throw new BusinessException(e.getMessage(), e);
				} finally {
					ses.close();
				}
			}
		} catch (IOException e) {
			throw new IOException(e.getMessage(), e);
		} finally {
			br.close();
			fstream.close();
		}
		LOG.info("Aggiornate "+count+" anagrafiche con l'articolo "+cmArticolo);

	}
	
	private static void parseAddArticolo(Session ses, String line, Articoli art) 
			throws HibernateException, IOException {
		String[] values = line.split(SEPARATOR_REGEX);
		String codAbb;
		try {
			codAbb = values[0];
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
		Abbonamenti abb = aDao.findAbbonamentiByCodice(ses, codAbb);
		if (abb != null) {
			IstanzeAbbonamenti ia = iaDao.findUltimaIstanzaByAbbonamento(ses, abb.getId());
			if (ia != null) {
				EvasioniArticoli ea = checkEvasioneArticolo(ses, ia, art);
				if (ea == null) {
					addEvasioneArticolo(ses, ia, art);
					LOG.info(ia.getAbbonamento().getCodiceAbbonamento()+" added "+art.getCodiceMeccanografico());
				} else {
					updateEvasioneArticolo(ses, ea);
					LOG.info(ia.getAbbonamento().getCodiceAbbonamento()+" updated "+art.getCodiceMeccanografico());
				}
				return;
			}
		}
		LOG.error("Impossible to parse: "+line);
	}
	
	private static EvasioniArticoli checkEvasioneArticolo(Session ses, IstanzeAbbonamenti ia, Articoli art) 
			throws HibernateException {
		List<EvasioniArticoli> evaList = eaDao.findByIstanza(ses, ia.getId());
		for (EvasioniArticoli ea:evaList) {
			if (ea.getArticolo().getId().equals(art.getId())) {
				return ea;
			}
		}
		return null;
	}
	
	private static void addEvasioneArticolo(Session ses, IstanzeAbbonamenti ia, Articoli art) 
		throws HibernateException {
		EvasioniArticoli ea = eaDao.createEmptyEvasioniArticoliFromIstanza(ses,
				ia, AppConstants.DEST_BENEFICIARIO, ServerConstants.DEFAULT_SYSTEM_USER);
		ea.setArticolo(art);
		ea.setDataCreazione(DATA_ARTICOLO);
		ea.setDataInvio(DATA_ARTICOLO);
		eaDao.save(ses, ea);
	}
	
	private static void updateEvasioneArticolo(Session ses, EvasioniArticoli ea) 
			throws HibernateException {
		ea.setDataInvio(DATA_ARTICOLO);
		eaDao.update(ses, ea);
	}
}
