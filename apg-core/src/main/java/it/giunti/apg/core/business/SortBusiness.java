package it.giunti.apg.core.business;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Periodici;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortBusiness {

	public void sortEvasioniComunicazioni(List<EvasioniComunicazioni> ecList) {
		Comparator<EvasioniComunicazioni> ecComp = new EvasioniComunicazioniComparator();
		Collections.sort(ecList, ecComp);
	}
	
	//public void sortEvasioniArticoli(List<EvasioniArticoli> edList) {
	//	Comparator<EvasioniArticoli> edComp = new EvasioniArticoliComparator();
	//	Collections.sort(edList, edComp);
	//}
	public void sortAnagrafiche(List<Anagrafiche> anaList) {
		Comparator<Anagrafiche> anaComp = new AnagraficheComparator();
		Collections.sort(anaList, anaComp);
	}
	
	public void sortIstanzeAbbonamenti(List<IstanzeAbbonamenti> iaList) {
		Comparator<IstanzeAbbonamenti> iaComp = new IstanzeAbbonamentiComparator();
		Collections.sort(iaList, iaComp);
	}
	
	public void sortFascicoliGroup(List<FascicoliGroupBean> fgList) {
		Comparator<FascicoliGroupBean> fgComp = new FascicoliGroupComparator();
		Collections.sort(fgList, fgComp);
	}

	public void sortPeriodici(List<Periodici> pList) {
		Comparator<Periodici> pComp = new PeriodiciLetteraComparator();
		Collections.sort(pList, pComp);
	}
	
	//Inner Classes
	
	
	
	public class EvasioniComunicazioniComparator
			implements Comparator<EvasioniComunicazioni> {
		@Override
		public int compare(EvasioniComunicazioni ec1, EvasioniComunicazioni ec2) {
			int result = 0;
			if (ec1.getIstanzaAbbonamento() != null) {
				if (ec2.getIstanzaAbbonamento() != null) {
					//Separa italia da estero
					boolean isItalia1 = ec1.getIstanzaAbbonamento().getAbbonato().getIndirizzoPrincipale()
							.getNazione().getId().equalsIgnoreCase(AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
					boolean isItalia2 = ec2.getIstanzaAbbonamento().getAbbonato().getIndirizzoPrincipale()
							.getNazione().getId().equalsIgnoreCase(AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
					if (isItalia1 != isItalia2) {
						result = -1;
						if (isItalia1) result = 1;//L'italia va dopo
					} else {
						//Ordina i cap
						String cap1 = ec1.getIstanzaAbbonamento().getAbbonato().getIndirizzoPrincipale().getCap();
						if (cap1 == null) cap1 = "";
						String cap2 = ec2.getIstanzaAbbonamento().getAbbonato().getIndirizzoPrincipale().getCap();
						if (cap2 == null) cap2 = "";
						result = cap1.compareTo(cap2);
						if (result == 0) {
							//Ordina le nazioni
							String nazione1 = ec1.getIstanzaAbbonamento().getAbbonato().getIndirizzoPrincipale().getNazione().getNomeNazione();
							String nazione2 = ec2.getIstanzaAbbonamento().getAbbonato().getIndirizzoPrincipale().getNazione().getNomeNazione();
							result = nazione1.compareTo(nazione2);
						}
					}
				}
			}
			return result;
		}	
	}
	
	//public class EvasioniArticoliComparator
	//		implements Comparator<EvasioniArticoli> {
	//	@Override
	//	public int compare(EvasioniArticoli ed1, EvasioniArticoli ed2) {
	//		int result = 0;
	//		//Separa italia da estero
	//		boolean isItalia1 = ed1.getIstanzaAbbonamento().getAbbonato().getIndirizzoPrincipale()
	//				.getNazione().getId().equalsIgnoreCase(AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
	//		boolean isItalia2 = ed2.getIstanzaAbbonamento().getAbbonato().getIndirizzoPrincipale()
	//				.getNazione().getId().equalsIgnoreCase(AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
	//		if (isItalia1 != isItalia2) {
	//			result = -1;
	//			if (isItalia1) result = 1;//L'italia va dopo
	//		} else {
	//			//Ordina per copie (desc)
	//			Integer copie1 = ed1.getIstanzaAbbonamento().getCopie();
	//			Integer copie2 = ed2.getIstanzaAbbonamento().getCopie();
	//			result = -1*copie1.compareTo(copie2);
	//			if (result == 0) {
	//				//Ordina per cap
	//				String cap1 = ed1.getIstanzaAbbonamento().getAbbonato().getIndirizzoPrincipale().getCap();
	//				if (cap1 == null) cap1 = "";
	//				String cap2 = ed2.getIstanzaAbbonamento().getAbbonato().getIndirizzoPrincipale().getCap();
	//				if (cap2 == null) cap2 = "";
	//				result = cap1.compareTo(cap2);
	//				if (result == 0) {
	//					//Ordina per nazione
	//					String nazione1 = ed1.getIstanzaAbbonamento().getAbbonato().getIndirizzoPrincipale().getNazione().getNomeNazione();
	//					String nazione2 = ed2.getIstanzaAbbonamento().getAbbonato().getIndirizzoPrincipale().getNazione().getNomeNazione();
	//					result = nazione1.compareTo(nazione2);
	//				}
	//			}
	//		}
	//		return result;
	//	}	
	//}
	
	public class AnagraficheComparator
		implements Comparator<Anagrafiche> {
		@Override
		public int compare(Anagrafiche an1, Anagrafiche an2) {
			int result = 0;
			//Separa italia da estero
			boolean isItalia1 = an1.getIndirizzoPrincipale()
					.getNazione().getId().equalsIgnoreCase(AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
			boolean isItalia2 = an2.getIndirizzoPrincipale()
					.getNazione().getId().equalsIgnoreCase(AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
			if (isItalia1 != isItalia2) {
				result = -1;
				if (isItalia1) result = 1;//L'italia va dopo
			} else {
				//Ordina per cap
				String cap1 = an1.getIndirizzoPrincipale().getCap();
				if (cap1 == null) cap1 = "";
				String cap2 = an2.getIndirizzoPrincipale().getCap();
				if (cap2 == null) cap2 = "";
				result = cap1.compareTo(cap2);
				if (result == 0) {
					//Ordina per nazione
					String nazione1 = an1.getIndirizzoPrincipale().getNazione().getNomeNazione();
					String nazione2 = an2.getIndirizzoPrincipale().getNazione().getNomeNazione();
					result = nazione1.compareTo(nazione2);
				}
			}
			return result;
		}	
	}
	
	public class IstanzeAbbonamentiComparator
			implements Comparator<IstanzeAbbonamenti> {
		@Override
		public int compare(IstanzeAbbonamenti ia1, IstanzeAbbonamenti ia2) {
			int result = 0;
			//Separa italia da estero
			boolean isItalia1 = ia1.getAbbonato().getIndirizzoPrincipale()
					.getNazione().getId().equalsIgnoreCase(AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
			boolean isItalia2 = ia2.getAbbonato().getIndirizzoPrincipale()
					.getNazione().getId().equalsIgnoreCase(AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
			if (isItalia1 != isItalia2) {
				result = -1;
				if (isItalia1) result = 1;//L'italia va dopo
			} else {
				//Ordina per copie (desc)
				Integer copie1 = ia1.getCopie();
				Integer copie2 = ia2.getCopie();
				result = -1*copie1.compareTo(copie2);
				if (result == 0) {
					//Ordina per cap
					String cap1 = ia1.getAbbonato().getIndirizzoPrincipale().getCap();
					if (cap1 == null) cap1 = "";
					String cap2 = ia2.getAbbonato().getIndirizzoPrincipale().getCap();
					if (cap2 == null) cap2 = "";
					result = cap1.compareTo(cap2);
					if (result == 0) {
						//Ordina per nazione
						String nazione1 = ia1.getAbbonato().getIndirizzoPrincipale().getNazione().getNomeNazione();
						String nazione2 = ia2.getAbbonato().getIndirizzoPrincipale().getNazione().getNomeNazione();
						result = nazione1.compareTo(nazione2);
					}
				}
			}
			return result;
		}	
	}
	
	public class FascicoliGroupComparator implements Comparator<FascicoliGroupBean> {
		@Override
		public int compare(FascicoliGroupBean fg1, FascicoliGroupBean fg2) {
			IstanzeAbbonamenti ia1 = fg1.getIstanzaAbbonamento();
			IstanzeAbbonamenti ia2 = fg2.getIstanzaAbbonamento();
			int result = 0;
			//Separa italia da estero
			boolean isItalia1 = ia1.getAbbonato().getIndirizzoPrincipale()
					.getNazione().getId().equalsIgnoreCase(AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
			boolean isItalia2 = ia2.getAbbonato().getIndirizzoPrincipale()
					.getNazione().getId().equalsIgnoreCase(AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
			if (isItalia1 != isItalia2) {
				result = -1;
				if (isItalia1) result = 1;//L'italia va dopo
			} else {
				//Ordina per copie
				Integer copie1 = ia1.getCopie();
				Integer copie2 = ia2.getCopie();
				result = (-1)*copie1.compareTo(copie2);
				if (result == 0) {
					//Ordina per cap
					String cap1 = ia1.getAbbonato().getIndirizzoPrincipale().getCap();
					if (cap1 == null) cap1 = "";
					String cap2 = ia2.getAbbonato().getIndirizzoPrincipale().getCap();
					if (cap2 == null) cap2 = "";
					result = cap1.compareTo(cap2);
					if (result == 0) {
						//Ordina per nazione
						String nazione1 = ia1.getAbbonato().getIndirizzoPrincipale().getNazione().getNomeNazione();
						String nazione2 = ia2.getAbbonato().getIndirizzoPrincipale().getNazione().getNomeNazione();
						result = nazione1.compareTo(nazione2);
					}
				}
			}
			return result;
		}	
		
	}
	
	public class PeriodiciLetteraComparator implements Comparator<Periodici> {
		@Override
		public int compare(Periodici p1, Periodici p2) {
			String lettera1 = p1.getUid();
			String lettera2 = p2.getUid();
			int result = lettera1.compareTo(lettera2);
			return result;
		}	
		
	}
}
