package it.giunti.apg.core.business;

import it.giunti.apg.shared.model.Indirizzi;

public class IndirizziBusiness {

	public static boolean isFilledUp(Indirizzi ind) {
		boolean filledUp = true;
		filledUp = filledUp && (ind.getNazione() != null);
		if (ind.getIndirizzo() != null) {
			filledUp = filledUp && (ind.getIndirizzo().length() > 1);
		} else {
			filledUp = false;
		}
		if (ind.getLocalita() != null) {
			filledUp = filledUp && (ind.getLocalita().length() > 1);
		} else {
			filledUp = false;
		}
		if (ind.getProvincia() != null) {
			filledUp = filledUp && (ind.getProvincia().length() > 1);
		} else {
			filledUp = false;
		}
		if (ind.getCognomeRagioneSociale() != null) {
			filledUp = filledUp && (ind.getCognomeRagioneSociale().length() > 1);
		} else {
			filledUp = false;
		}
		return filledUp;
	}
	
}
