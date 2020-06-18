package it.giunti.apg.core.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.FileException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.MaterialiSpedizione;

public class FileFormatArticoli {
    
	//private static final Logger LOG = LoggerFactory.getLogger(FileFormatArticoli.class);
	//private static int CM_FIELD_LENGTH = 100;
	
	public static void formatArticoliDaSpedire(File file,
			List<MaterialiSpedizione> msList, int idRapporto) 
			throws BusinessException, FileException {
		Locale.setDefault(Locale.ITALIAN);
		//la lista è in ordine di abbonamento e fascicolo
		String data;
		if (file != null) {
			try {
				//Odinamento
				//Recupero anagrafiche e creazione mappa
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Recupero anagrafiche");
				Map<Anagrafiche, List<MaterialiSpedizione>> invioMap = OutputMaterialiBusiness
						.buildMapFromSpedizioni(msList);
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Ordinamento per cap e nazione");
				List<Anagrafiche> anaList = new ArrayList<Anagrafiche>();
				anaList.addAll(invioMap.keySet());
				new SortBusiness().sortAnagrafiche(anaList);
				//Preparazione file
				FileOutputStream fos = new FileOutputStream(file);
				OutputStreamWriter fileWriter = new OutputStreamWriter(fos, AppConstants.CHARSET_UTF8);
				Session ses = SessionFactory.getSession();
				try {
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Formattazione dati");
					String header = FileFormatCommon.createInvioHeader();
					fileWriter.append(header);
					for (Anagrafiche ana:anaList) {
						List<MaterialiSpedizione> msGroup = invioMap.get(ana);
						int progressivo = 1;
						for(MaterialiSpedizione ms:msGroup) {
							data = FileFormatCommon.MaterialiSpedizioneToBuffer(ses, 
									progressivo, ms, idRapporto);
							//data += FileFormatCommon.formatString(CM_FIELD_LENGTH, ea.getArticolo().getCodiceMeccanografico());
							fileWriter.append(data+ServerConstants.INVIO_EOL);
							progressivo++;
						}
					}
				} catch (HibernateException e) {
					VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
					throw new BusinessException(e.getMessage(), e);
				} finally {
					fileWriter.close();
					ses.close();
				}
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Scrittura file completa");
			} catch(IOException e)	{
				VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
				throw new FileException(e.getMessage(), e);
			}
		} else {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "Errore nell'apertura del file");
		}
	}
	
	
//	/**
//	 * Returns a formatted string with this data:
//	 * codInterno8
//	 * codMecc8;
//	 * @param regalo
//	 * @return
//	 */
//	private static String regaloToBuffer(Articoli regalo) {
//		String codInterno8 = FileFormatCommon.formatString(8, regalo.getCodiceInterno());
//		String codMecc8 = FileFormatCommon.formatString(8, regalo.getCodiceMeccanografico());
//		return codInterno8 +
//				codMecc8;
//	}
}
