package it.giunti.apg.automation.report;

import it.giunti.apg.automation.AutomationConstants;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.persistence.FattureArticoliDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.PagamentiCreditiDao;
import it.giunti.apg.core.persistence.PagamentiDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureArticoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.PagamentiCrediti;
import it.giunti.apg.shared.model.Societa;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;


public class FattureDataSource {

	private static List<Fatture> _fattList;
	private static Integer _idRapporto;
	
	/**
	 * Questo data source parte dall'elenco degli ultimi pagamenti, da questi risale agli abbonamenti e
	 * crea le fatture solo per quelli che sono stati effettivamente saldati con i pagamenti stessi.
	 * 
	 * 
	 * @param pagList
	 */
	public static void initDataSource(Integer idRapporto, List<Fatture> fattList)
			throws HibernateException {
		_idRapporto = idRapporto;
		_fattList = fattList;
	}
	
	public static List<FatturaBean> createBeanCollection(Session ses)
			throws HibernateException, BusinessException {
		PagamentiDao pDao = new PagamentiDao();
		FattureArticoliDao faDao = new FattureArticoliDao();
  		List<FatturaBean> list = new ArrayList<FatturaBean>();
  		int i = 0;
		//Dopo l'inizializzazione, crea effettivamente le fatture
		for (Fatture fattura:_fattList) {
			String logoFileName = AutomationConstants.REPORT_RESOURCES_PATH+
					AutomationConstants.IMG_ICON_GIUNTI;
			Anagrafiche pagante = GenericDao.findById(ses, Anagrafiche.class, fattura.getIdAnagrafica());
			String idTipoPagamento = null;

			IstanzeAbbonamenti ia = null;
			List<Pagamenti> pList = pDao.findPagamentiByIdFattura(ses, fattura.getId());
			if (pList.size() > 0) {
				ia = pList.get(0).getIstanzaAbbonamento();
				idTipoPagamento = pList.get(0).getIdTipoPagamento();
			} else {
				List<PagamentiCrediti> pcList = new PagamentiCreditiDao().findByFatturaOrigine(ses, fattura.getId());
				if (pcList.size() > 0) {
					Integer idIa = pcList.get(0).getIdIstanzaAbbonamento();
					if (idIa != null) ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIa);
					idTipoPagamento = AppConstants.PAGAMENTO_RESTO;
				}
			}
			Societa societa = GenericDao.findById(ses, Societa.class, fattura.getIdSocieta());
			List<FattureArticoli> faList = faDao.findByFattura(ses, fattura.getId());
			FatturaBean bean;
			if (ia != null) {
				bean = new FatturaBean(ses, logoFileName, fattura, faList, 
						idTipoPagamento, pagante, societa, ia);
			} else {
				bean = new FatturaBean(ses, logoFileName, fattura, faList, 
						idTipoPagamento, pagante, societa);
			}
			list.add(bean);
			i++;
			if (i % ServerConstants.FATTURE_PAGE_SIZE == 0) {
				ses.flush();
				if (_idRapporto != null) 
					VisualLogger.get().addHtmlInfoLine(_idRapporto, "Elaborazione report collection "+i+"/"+_fattList.size());
			}
		}
		if (_idRapporto != null) 
			VisualLogger.get().addHtmlInfoLine(_idRapporto, "Elaborazione report collection "+i+"/"+_fattList.size());
  		return list;
	}
	
}
