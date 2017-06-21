package it.giunti.apg.updater;

import it.giunti.apg.core.persistence.AbbonamentiDao;
import it.giunti.apg.core.persistence.EvasioniFascicoliDao;
import it.giunti.apg.core.persistence.FascicoliDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;

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

public class UpdateFascicoliTotali {
	
	private static final Logger LOG = LoggerFactory.getLogger(UpdateFascicoliTotali.class);
	
	public static Date DATA_ARTICOLO = new Date();
	private static final String SEPARATOR_REGEX = "\\;";
	
	private static AbbonamentiDao aDao = new AbbonamentiDao();
	private static IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
	
	public static void parseUpdate(String csvFilePath, boolean adjustBeginEnd, boolean adjustSentCount) 
			throws BusinessException, IOException {
		File csvFile = new File(csvFilePath);
		FileInputStream fstream = new FileInputStream(csvFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		int count = 0;
		
		try {
			//Ciclo su tutte le righe
			LOG.info("Ripristino durate corrette per le istanze nel file "+csvFile.getAbsolutePath());
			String line = br.readLine();
			while (line != null) {
				//Sessione singola istanza
				Session ses = SessionFactory.getSession();
				Transaction trn = ses.beginTransaction();
				try {
					parseUpdateTotFascicoli(ses, line, adjustBeginEnd, adjustSentCount);
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
		LOG.info("Aggiornate "+count+" abbonamenti");

	}
	
	private static void parseUpdateTotFascicoli(Session ses, String line,
			boolean adjustBeginEnd, boolean adjustSentCount) 
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
			//Elenco in ordine cronologico inverso
			List<IstanzeAbbonamenti> iaList = iaDao.findIstanzeByAbbonamento(ses, abb.getId());
			for (int i=0; i<3; i++) {
				if (iaList.size()>i) {
					IstanzeAbbonamenti ia = iaList.get(i);
					Listini lsn = ia.getListino();
					if (adjustBeginEnd) {
						ia.setFascicoliTotali(lsn.getNumFascicoli());
						Fascicoli fasInizio = ia.getFascicoloInizio();
						Fascicoli fasFine = new FascicoliDao()
								.findFascicoliAfterFascicolo(ses, fasInizio, lsn.getNumFascicoli()-1);
						ia.setFascicoloFine(fasFine);
					}
					int count = ia.getFascicoliSpediti();
					if (adjustSentCount) {
						count = new EvasioniFascicoliDao().countFascicoliSpediti(ses, ia.getId());
						ia.setFascicoliSpediti(count);
					}
					LOG.info("Aggiornato "+ia.getAbbonamento().getCodiceAbbonamento()+
							" ["+lsn.getTipoAbbonamento().getCodice()+"] "+
							count+"/"+lsn.getNumFascicoli()+" uscite num."+
							ia.getFascicoloInizio().getTitoloNumero()+"-"+
							ia.getFascicoloFine().getTitoloNumero());
				}
			}
			return;
		}
		LOG.error("Impossible to parse: "+line);
	}
	
}
