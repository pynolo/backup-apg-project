package it.giunti.apg.ws.business;

import it.giunti.apg.core.persistence.FascicoliDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.IstanzeStatusUtil;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

public class AbbonamentiDateBusiness {

	public static Date getBeginPubblicazioneOfFirstInstance(List<IstanzeAbbonamenti> iaList) {
		Fascicoli fasInizio = iaList.get(0).getFascicoloInizio();
		//Assegna le date estreme di tutta la serie di istanze dell'abbonamento
		for (IstanzeAbbonamenti ia:iaList) {
			if (!ia.getInvioBloccato()) {
				if (fasInizio.getDataPubblicazione().after(ia.getFascicoloInizio().getDataPubblicazione())) {
					fasInizio=ia.getFascicoloInizio();
				}
			}
		}
		return fasInizio.getDataPubblicazione();
	}
	
	public static Date getEndOfLastInstance(Session ses, List<IstanzeAbbonamenti> iaList) {
		IstanzeAbbonamenti istanza = iaList.get(0);
		Fascicoli fasInizio = istanza.getFascicoloInizio();
		Fascicoli fasFine = istanza.getFascicoloFine();
		//Assegna le date estreme di tutta la serie di istanze dell'abbonamento
		for (IstanzeAbbonamenti ia:iaList) {
			if (!ia.getInvioBloccato()) {
				if (fasInizio.getDataInizio().after(ia.getFascicoloInizio().getDataInizio())) {
					fasInizio=ia.getFascicoloInizio();
				}
				if (fasFine.getDataInizio().before(ia.getFascicoloFine().getDataInizio())) {
					fasFine=ia.getFascicoloFine();
					istanza=ia;//l'istanza più recente
				}
			}
		}
		if (!IstanzeStatusUtil.isSpedibile(istanza)) {
			//Se NON PAGATO il fasFine resta fasFine per le
			//scolastiche ma (fasInizio+gracingIniziale) per i periodici varia
			if (fasFine.getPeriodico().getIdTipoPeriodico().equals(AppConstants.PERIODICO_VARIA)) {
				if (istanza.getListino().getGracingIniziale() > 0) {
					fasFine = new FascicoliDao().findFascicoliAfterFascicolo(ses, fasInizio,
							istanza.getListino().getGracingIniziale()-1);
				}
			}
		}
		return fasFine.getDataFine();
	}
	
//	public static Date getEndPubblicazioneGracingOfLastInstance(Session ses, List<IstanzeAbbonamenti> iaList) {
//		IstanzeAbbonamenti istanza = iaList.get(0);
//		Fascicoli fasInizio = istanza.getFascicoloInizio();
//		Fascicoli fasFine = istanza.getFascicoloFine();
//		//Assegna le date estreme di tutta la serie di istanze dell'abbonamento
//		for (IstanzeAbbonamenti ia:iaList) {
//			if (!ia.getInvioBloccato()) {
//				if (fasInizio.getDataPubblicazione().after(ia.getFascicoloInizio().getDataPubblicazione())) {
//					fasInizio=ia.getFascicoloInizio();
//				}
//				if (fasFine.getDataPubblicazione().before(ia.getFascicoloFine().getDataPubblicazione())) {
//					fasFine=ia.getFascicoloFine();
//					istanza=ia;//l'istanza più recente
//				}
//			}
//		}
//		if (IstanzeAbbonamentiBusiness.isSpedibile(istanza)) {
//			//Se PAGATO allora al fasFine somma il gracing finale
//			fasFine = new FascicoliDao().findFascicoliAfterFascicolo(ses, fasFine,
//					istanza.getListino().getGracingFinale());
//		} else {
//			//Se NON PAGATO il fasFine resta fasFine per le
//			//scolastiche ma (fasInizio+gracingFinale) per i periodici varia
//			if (fasFine.getPeriodico().getIdTipoPeriodico().equals(AppConstants.PERIODICO_VARIA)) {
//				fasFine = new FascicoliDao().findFascicoliAfterFascicolo(ses, fasInizio,
//						istanza.getListino().getGracingIniziale());
//			}
//		}
//		return fasFine.getDataPubblicazione();
//	}
}
