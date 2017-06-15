package it.giunti.apg.server.business;

import it.giunti.apg.server.persistence.GenericDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Comunicazioni;
import it.giunti.apg.shared.model.TipiAbbonamento;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;

public class ComunicazioniBusiness {

	public static String getTipiAbbStringFromComunicazione(Session ses, Integer idCom)
			throws HibernateException {
		String result = "";
		Comunicazioni com = GenericDao.findById(ses, Comunicazioni.class, idCom);
		String[] idArray = com.getTipiAbbonamentoList().split(AppConstants.COMUN_TIPI_ABB_SEPARATOR);
		for (String idString:idArray) {
			Integer id = Integer.parseInt(idString);
			TipiAbbonamento ta = GenericDao.findById(ses, TipiAbbonamento.class, id);
			if (ta != null) {
				result += ta.getCodice()+" ";
			} else {
				throw new HibernateException("La comunicazione '"+com.getTitolo()+"' è abbinata ad un tipo abb. non esistente");
			}
		}
		return result;
	}
	
	public static List<TipiAbbonamento> getTipiAbbListFromComunicazione(Session ses, Integer idCom)
			throws HibernateException {
		List<TipiAbbonamento> result = new ArrayList<TipiAbbonamento>();
		Comunicazioni com = GenericDao.findById(ses, Comunicazioni.class, idCom);
		String[] idArray = com.getTipiAbbonamentoList().split(AppConstants.COMUN_TIPI_ABB_SEPARATOR);
		for (String idString:idArray) {
			Integer id = Integer.parseInt(idString);
			TipiAbbonamento ta = GenericDao.findById(ses, TipiAbbonamento.class, id);
			result.add(ta);
		}
		return result;
	}
	
}
