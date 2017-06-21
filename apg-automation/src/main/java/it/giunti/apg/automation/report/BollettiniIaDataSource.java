package it.giunti.apg.automation.report;

import it.giunti.apg.core.business.PagamentiMatchBusiness;
import it.giunti.apg.core.persistence.ModelliBollettiniDao;
import it.giunti.apg.core.persistence.PagamentiCreditiDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.ModelliBollettini;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

public class BollettiniIaDataSource {

	private static List<IstanzeAbbonamenti> _iaList = null;
	
	public static void initDataSource(List<IstanzeAbbonamenti> iaList) {
		_iaList = iaList;
	}
	
	public static List<Bollettino> createBeanCollection(Session ses) throws BusinessException {
  		List<Bollettino> list = new ArrayList<Bollettino>();
  		ModelliBollettiniDao mbDao = new ModelliBollettiniDao();
  		PagamentiCreditiDao credDao = new PagamentiCreditiDao();
  		if (_iaList != null) {
			for (IstanzeAbbonamenti ia:_iaList) {
				if (ia.getInFatturazione() || ia.getListino().getFatturaDifferita() || ia.getPagato())
					throw new BusinessException(ia.getAbbonamento().getCodiceAbbonamento()+" is paid");
				//Controlla se stampare o meno
				ModelliBollettini modello = mbDao.findModelliBollettiniPredefinitoByPeriodico(ses, ia.getAbbonamento().getPeriodico().getId());
				if (modello == null) throw new BusinessException("No default bollettino for "+ia.getAbbonamento().getPeriodico().getNome());
				
				Double dovuto = PagamentiMatchBusiness.getMissingAmount(ses, ia.getId());
				Double pagato = credDao.getCreditoByAnagraficaSocieta(ses, ia.getId(),
						ia.getAbbonamento().getPeriodico().getIdSocieta(), null, false);
				Double importo = dovuto-pagato;
				if (importo < AppConstants.SOGLIA)
					throw new BusinessException(ia.getAbbonamento().getCodiceAbbonamento()+" is paid");
				Bollettino bean = new Bollettino(ia, importo, modello);
				list.add(bean);
			}
  		}
  		return list;
	}
	
}
