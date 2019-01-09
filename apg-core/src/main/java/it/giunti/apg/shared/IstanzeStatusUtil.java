package it.giunti.apg.shared;

import it.giunti.apg.shared.model.IstanzeAbbonamenti;

public class IstanzeStatusUtil {
	
	/** "Spedibile" e' ogni abbonamento che ha diritto a ricevere i fascicoli in uscita (ed eventuale
	 * gracing finale) per tutta la sua durata. Tiene in conto anche che i periodici scolastici sono
	 * spediti anche senza pagamento.
	 * @param ia
	 * @return
	 */
	public static boolean isSpedibile(IstanzeAbbonamenti ia) {
		boolean spedibile = ia.getPagato() ||
				(ia.getListino().getPrezzo() < AppConstants.SOGLIA) ||
				ia.getFatturaDifferita() || ia.getListino().getFatturaDifferita() ||
				ia.getListino().getInvioSenzaPagamento();//questa è la condizione scolastica
		return spedibile;
	}
	
	/** "InRegola" è un abbonamento per cui non deve essere richiesto un pagamento. O perché già pagato,
	 * oppure perché è fatturato/omaggio
	 * @param ia
	 * @return
	 */
	public static boolean isInRegola(IstanzeAbbonamenti ia) {
		boolean spedibile = ia.getPagato() || isFatturatoOppureOmaggio(ia);
		return spedibile;
	}
	
	public static boolean isFatturatoOppureOmaggio(IstanzeAbbonamenti ia) {
		boolean fatturaOmaggio = (ia.getListino().getPrezzo() < AppConstants.SOGLIA) ||
				isFatturato(ia);
		return fatturaOmaggio;
	}
	
	public static boolean isFatturato(IstanzeAbbonamenti ia) {
		boolean fatturato = ia.getFatturaDifferita() || ia.getListino().getFatturaDifferita();
		return fatturato;
	}
	
	public static boolean isOmaggio(IstanzeAbbonamenti ia) {
		boolean omaggio = (ia.getListino().getPrezzo() < AppConstants.SOGLIA) && !isFatturato(ia);
		return omaggio;
	}
	
	public static boolean isTipoRegalo(IstanzeAbbonamenti ia) {
		boolean regalo = ia.getListino().getTipoAbbonamento().getPermettiPagante() && !isFatturato(ia);
		return regalo;
	}

}
