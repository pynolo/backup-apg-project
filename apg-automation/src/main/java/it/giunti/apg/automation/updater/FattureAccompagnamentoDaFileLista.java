package it.giunti.apg.automation.updater;

import it.giunti.apg.automation.business.FattureTxtBusiness;
import it.giunti.apg.core.persistence.FattureDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.Societa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FattureAccompagnamentoDaFileLista {
	
	private static Logger LOG = LoggerFactory.getLogger(FattureAccompagnamentoDaFileLista.class);
	private static FattureDao fatDao = new FattureDao();
	
	public static void execute(String[] args) {
		//param: fileFattureRegistrate
		String filePath = args[0];
		File fatRegFile= new File(filePath);
		if (!fatRegFile.exists()) LOG.error("File not found: "+filePath);
		
		// Extract fatture
		Session ses = SessionFactory.getSession();
  		try {
  			/*Crea elenco stimato fatture*/
  			List<Fatture> fList = new ArrayList<Fatture>();
  			/* Esamina il file */
  			LOG.info("Ricerca fatture presenti nel file");
  			FileReader reader = new FileReader(fatRegFile);
  			BufferedReader br = new BufferedReader(reader);
  			String line = null;
  			do {
  				line = br.readLine();
  				if (line != null) {
  					String numFatt = line.trim();
  					List<Fatture> tmpList = fatDao.findByNumeroFattura(ses, numFatt);
  					if (tmpList == null) LOG.warn("non trovata "+numFatt);
  					if (tmpList.size() == 0) LOG.warn("non trovata "+numFatt);
  					fList.addAll(tmpList);
  				}
  			} while (line != null);
  			br.close();
  			reader.close();
  			LOG.info("Acquisite "+fList.size()+" fatture");
  			
			/* ** CREAZIONE FILE ACCOMPAGNAMENTO ** */
			
			if (fList != null) {
				if (fList.size() > 0) {
					Societa societa = GenericDao.findById(ses, Societa.class, fList.get(0).getIdSocieta());
					File corFile = FattureTxtBusiness.createAccompagnamentoPdfFile(ses, fList, societa);
					Path accPath = moveFile(corFile, "accompagnamento.frd");
					LOG.info("File accompagnamento Pdf: "+accPath.toAbsolutePath());
				}
			}
	  	} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			ses.close();
		}
  		LOG.info("Fine");
	}

	private static Path moveFile(File f, String destFileName) throws IOException {
		String tempDir = System.getProperty("java.io.tmpdir");
		Path path = Files.move(f.toPath(),
				f.toPath().resolve(tempDir+"/"+destFileName),
				StandardCopyOption.REPLACE_EXISTING);
		return path;
	}
	
}
