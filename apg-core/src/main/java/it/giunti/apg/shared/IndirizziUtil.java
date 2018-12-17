package it.giunti.apg.shared;

import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.Indirizzi;

public class IndirizziUtil {

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
	
	public static boolean isFilledUp(Fatture fatt) {
		boolean filledUp = true;
		filledUp = filledUp && (fatt.getNazione() != null);
		if (fatt.getIndirizzo() != null) {
			filledUp = filledUp && (fatt.getIndirizzo().length() > 1);
		} else {
			filledUp = false;
		}
		if (fatt.getLocalita() != null) {
			filledUp = filledUp && (fatt.getLocalita().length() > 1);
		} else {
			filledUp = false;
		}
		if (fatt.getIdProvincia() != null) {
			filledUp = filledUp && (fatt.getIdProvincia().length() > 1);
		} else {
			filledUp = false;
		}
		if (fatt.getCognomeRagioneSociale() != null) {
			filledUp = filledUp && (fatt.getCognomeRagioneSociale().length() > 1);
		} else {
			filledUp = false;
		}
		return filledUp;
	}
	
}
