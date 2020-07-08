package it.giunti.apg.ws.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Session;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.IstanzeStatusUtil;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

public class AbbonamentiDateBusiness {

	public static Date getBeginPubblicazioneOfFirstInstance(List<IstanzeAbbonamenti> iaList) {
		Date dataInizio = iaList.get(0).getDataInizio();
		//Assegna le date estreme di tutta la serie di istanze dell'abbonamento
		for (IstanzeAbbonamenti ia:iaList) {
			if (!ia.getInvioBloccato()) {
				if (dataInizio.after(ia.getDataInizio())) {
					dataInizio=ia.getDataInizio();
				}
			}
		}
		return dataInizio;
	}
	
	public static Date getEndOfLastInstance(Session ses, List<IstanzeAbbonamenti> iaList) {
		IstanzeAbbonamenti last = iaList.get(0);
		Date dataInizio = last.getDataInizio();
		Date dataFine = last.getDataFine();
		//Assegna le date estreme di tutta la serie di istanze dell'abbonamento
		for (IstanzeAbbonamenti ia:iaList) {
			if (!ia.getInvioBloccato()) {
				if (dataInizio.after(ia.getDataInizio())) {
					dataInizio=ia.getDataInizio();
				}
				if (dataFine.before(ia.getDataFine())) {
					dataFine=ia.getDataFine();
					last=ia;//l'istanza più recente
				}
			}
		}
		if (!IstanzeStatusUtil.isSpedibile(last)) {
			//Se NON PAGATO il fasFine resta fasFine per le
			//scolastiche ma (fasInizio+gracingIniziale) per i periodici varia
			if (last.getAbbonamento().getPeriodico().getIdTipoPeriodico().equals(AppConstants.PERIODICO_VARIA)) {
				if (last.getListino().getGracingInizialeMesi() > 0) {
					Calendar cal = new GregorianCalendar();
					cal.setTime(last.getDataInizio());
					cal.add(Calendar.MONTH, last.getListino().getGracingInizialeMesi());
					dataFine = cal.getTime();
				}
			}
		}
		return dataFine;
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
