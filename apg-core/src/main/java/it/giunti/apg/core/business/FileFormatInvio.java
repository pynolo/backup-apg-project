package it.giunti.apg.core.business;

import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.FileException;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.HibernateException;
import org.hibernate.Session;

public class FileFormatInvio {
    
	//private static final Logger LOG = LoggerFactory.getLogger(FileFormatInvio.class);
	
	
	public static void formatInviiRegolari(File file, List<IstanzeAbbonamenti> iaList, Fascicoli fas, Integer idRapporto) 
			throws BusinessException, FileException {
		Locale.setDefault(Locale.ITALIAN);
		if (file != null) {
			Session ses = SessionFactory.getSession();
			try {
				FileOutputStream fos = new FileOutputStream(file);
				OutputStreamWriter fileWriter = new OutputStreamWriter(fos, AppConstants.CHARSET);
				createIndirizzarioFileContent(ses,
						iaList,
						fas,
						DateUtil.now(),
						fileWriter,
						idRapporto);
				fileWriter.close();
			} catch(HibernateException e)	{
				VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
				throw new BusinessException(e.getMessage(), e);
			} catch(IOException e)	{
				VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
				throw new FileException(e.getMessage(), e);
			} finally {
				ses.close();
			}
		} else {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "Errore nell'apertura del file");
		}
	}

	
	//Formattazione indirizzario
	
	
	public static final void createIndirizzarioFileContent(Session ses, List<IstanzeAbbonamenti> iaList, 
			Fascicoli fas, Date dataInvio, OutputStreamWriter writer, Integer idRapporto)
			throws BusinessException, IOException {
		try {
			int progressivo = 1;
			String header = FileFormatCommon.createInvioHeader();
			writer.append(header);
			for (IstanzeAbbonamenti ia:iaList) {
				//crea la linea
				Date dataScadenza = ia.getFascicoloFine().getDataFine();
				String line = FileFormatCommon.createInvioLine(progressivo, ia, fas.getCodiceMeccanografico(),
						dataScadenza, dataInvio);
				progressivo++;
				writer.append(line);
				if (progressivo%2000 == 0) {
					if (idRapporto != null) VisualLogger.get().addHtmlInfoLine(idRapporto, "Formattate "+progressivo+" righe su "+iaList.size());
					writer.flush();
				}
			}
			if (idRapporto != null) VisualLogger.get().addHtmlInfoLine(idRapporto, "Formattate "+iaList.size()+" righe");
		} catch (HibernateException e) {
			if (idRapporto != null) VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	}
	
}
