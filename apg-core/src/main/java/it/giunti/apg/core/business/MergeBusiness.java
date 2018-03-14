package it.giunti.apg.core.business;

import it.giunti.apg.core.persistence.EvasioniArticoliDao;
import it.giunti.apg.core.persistence.EvasioniFascicoliDao;
import it.giunti.apg.core.persistence.FattureDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.OrdiniLogisticaDao;
import it.giunti.apg.core.persistence.PagamentiCreditiDao;
import it.giunti.apg.core.persistence.PagamentiDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.OrdiniLogistica;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.PagamentiCrediti;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

public class MergeBusiness {

	/**
	 * @param primary - its code is preserved
	 * @param secondary - its data is preserved
	 * @return merged data
	 */
	public static Anagrafiche mergeTransient(Anagrafiche primary, Anagrafiche secondary) {
		// PRIMARY must be BEFORE SECONDARY
		Anagrafiche anagLastModified = secondary;
		if (primary.getDataModifica().after(secondary.getDataModifica()))
			anagLastModified = primary;
		Date now = DateUtil.now();
		Anagrafiche result = new Anagrafiche();
		//result.setCentroDiCosto(primary.getCentroDiCosto());
		result.setUid(primary.getUid());
		result.setCodiceFiscale(mergeValue(primary.getCodiceFiscale(), secondary.getCodiceFiscale()));
		result.setCodiceSap(mergeValue(primary.getCodiceSap(), secondary.getCodiceSap()));
		result.setUidMergeList(mergeCodiciCliente(primary,secondary));
		if (primary.getDataAggiornamentoConsenso().after(secondary.getDataAggiornamentoConsenso())) {
			result.setDataAggiornamentoConsenso(primary.getDataAggiornamentoConsenso());
			result.setConsensoTos(primary.getConsensoTos());
			result.setConsensoMarketing(primary.getConsensoMarketing());
			result.setConsensoProfilazione(primary.getConsensoProfilazione());
		} else {
			result.setDataAggiornamentoConsenso(secondary.getDataAggiornamentoConsenso());
			result.setConsensoTos(secondary.getConsensoTos());
			result.setConsensoMarketing(secondary.getConsensoMarketing());
			result.setConsensoProfilazione(secondary.getConsensoProfilazione());
		}
		result.setDataModifica(now);
		result.setEmailPrimaria(mergeValue(primary.getEmailPrimaria(), secondary.getEmailPrimaria()));
		result.setEmailSecondaria(mergeValue(primary.getEmailSecondaria(), secondary.getEmailSecondaria()));
		result.setGiuntiCard(mergeValue(primary.getGiuntiCard(), secondary.getGiuntiCard()));
		result.setId(primary.getId());
		result.setIdAnagraficaDaAggiornare(null);
		result.setIdTipoAnagrafica(mergeValue(primary.getIdTipoAnagrafica(), secondary.getIdTipoAnagrafica()));
		result.setNecessitaVerifica(false);
		result.setNote(concat(primary.getNote(),secondary.getNote()));
		result.setPartitaIva(mergeValue(primary.getPartitaIva(), secondary.getPartitaIva()));
		result.setProfessione(mergeValue(primary.getProfessione(), secondary.getProfessione()));
		//result.setRichiedeFattura(primary.getRichiedeFattura() && secondary.getRichiedeFattura());
		result.setSesso(mergeValue(primary.getSesso(), secondary.getSesso()));
		result.setTelCasa(mergeValue(primary.getTelCasa(), secondary.getTelCasa()));
		result.setTelMobile(mergeValue(primary.getTelMobile(), secondary.getTelMobile()));
		result.setTitoloStudio(mergeValue(primary.getTitoloStudio(), secondary.getTitoloStudio()));
		result.setIdUtente(anagLastModified.getIdUtente());
		result.setDataCreazione(primary.getDataCreazione());
		if (secondary.getDataCreazione() != null) {
			if (secondary.getDataCreazione().before(primary.getDataCreazione()))
				result.setDataCreazione(secondary.getDataCreazione());
		}
		//Indirizzi
		Indirizzi indP = new Indirizzi();
		Indirizzi indF = new Indirizzi();
		indP.setCognomeRagioneSociale(mergeValue(primary.getIndirizzoPrincipale().getCognomeRagioneSociale(), secondary.getIndirizzoPrincipale().getCognomeRagioneSociale()));
		indP.setNome(mergeValue(primary.getIndirizzoPrincipale().getNome(), secondary.getIndirizzoPrincipale().getNome()));
		indP.setTitolo(mergeValue(primary.getIndirizzoPrincipale().getTitolo(), secondary.getIndirizzoPrincipale().getTitolo()));
		indP.setCap(mergeValue(primary.getIndirizzoPrincipale().getCap(), secondary.getIndirizzoPrincipale().getCap()));
		indP.setDataModifica(now);
		indP.setId(primary.getIndirizzoPrincipale().getId());
		indP.setIndirizzo(mergeValue(primary.getIndirizzoPrincipale().getIndirizzo(), secondary.getIndirizzoPrincipale().getIndirizzo()));
		indP.setLocalita(mergeValue(primary.getIndirizzoPrincipale().getLocalita(), secondary.getIndirizzoPrincipale().getLocalita()));
		indP.setNazione(mergeValue(primary.getIndirizzoPrincipale().getNazione(), secondary.getIndirizzoPrincipale().getNazione()));
		indP.setPresso(mergeValue(primary.getIndirizzoPrincipale().getPresso(), secondary.getIndirizzoPrincipale().getPresso()));
		indP.setProvincia(mergeValue(primary.getIndirizzoPrincipale().getProvincia(), secondary.getIndirizzoPrincipale().getProvincia()));
		indP.setIdUtente(anagLastModified.getIndirizzoPrincipale().getIdUtente());
		indF.setCognomeRagioneSociale(mergeValue(primary.getIndirizzoFatturazione().getCognomeRagioneSociale(), secondary.getIndirizzoFatturazione().getCognomeRagioneSociale()));
		indF.setNome(mergeValue(primary.getIndirizzoFatturazione().getNome(), secondary.getIndirizzoFatturazione().getNome()));
		indF.setTitolo(mergeValue(primary.getIndirizzoFatturazione().getTitolo(), secondary.getIndirizzoFatturazione().getTitolo()));
		indF.setCap(mergeValue(primary.getIndirizzoFatturazione().getCap(), secondary.getIndirizzoFatturazione().getCap()));
		indF.setDataModifica(now);
		indF.setId(primary.getIndirizzoFatturazione().getId());
		indF.setIndirizzo(mergeValue(primary.getIndirizzoFatturazione().getIndirizzo(), secondary.getIndirizzoFatturazione().getIndirizzo()));
		indF.setLocalita(mergeValue(primary.getIndirizzoFatturazione().getLocalita(), secondary.getIndirizzoFatturazione().getLocalita()));
		indF.setNazione(mergeValue(primary.getIndirizzoFatturazione().getNazione(), secondary.getIndirizzoFatturazione().getNazione()));
		indF.setPresso(mergeValue(primary.getIndirizzoFatturazione().getPresso(), secondary.getIndirizzoFatturazione().getPresso()));
		indF.setProvincia(mergeValue(primary.getIndirizzoFatturazione().getProvincia(), secondary.getIndirizzoFatturazione().getProvincia()));
		indF.setIdUtente(anagLastModified.getIndirizzoFatturazione().getIdUtente());
		result.setIndirizzoPrincipale(indP);
		result.setIndirizzoFatturazione(indF);
		return result;
	}
	
	private static <T> T mergeValue(T primary, T secondary) {
		T result = secondary;
		if (secondary == null) {
			result = primary;
		} else {
			if (secondary instanceof String) {
				String ps = (String) secondary;
				if (ps.length() == 0) result = primary;
			}
		}
		return result;
	}
	
	private static String concat(String s1, String s2) {
		String result = "";
		if (s1 == null && s2 != null) result = s2;
		if (s1 != null && s2 == null) result = s1;
		if (s1 != null && s2 != null) result = s1+" "+s2;
		return result;
	}
	
	private static String mergeCodiciCliente(Anagrafiche primary, Anagrafiche secondary) {
		Set<String> codiciSet = new HashSet<String>();
		codiciSet.add(secondary.getUid());
		if (primary.getUidMergeList() != null) {
			String[] mergedArray = primary.getUidMergeList().split(AppConstants.STRING_SEPARATOR);
			for (String codice:mergedArray) codiciSet.add(codice);
		}
		if (secondary.getUidMergeList() != null) {
			String[] mergedArray = secondary.getUidMergeList().split(AppConstants.STRING_SEPARATOR);
			for (String codice:mergedArray) codiciSet.add(codice);
		}
		String result = "";
		for (String codici:codiciSet) {
			if (result.length() > 0) result += AppConstants.STRING_SEPARATOR;
			result += codici;
		}
		return result;
	}
	
	public static void moveAbbonamenti(Session ses, Integer idPrimary, Integer idToRemove) {
		IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
		Anagrafiche primary = GenericDao.findById(ses, Anagrafiche.class, idPrimary);
		List<IstanzeAbbonamenti> propriList = new IstanzeAbbonamentiDao().findIstanzeProprieByAnagrafica(ses, idToRemove, false, 0, Integer.MAX_VALUE);
		List<IstanzeAbbonamenti> regaliList = new IstanzeAbbonamentiDao().findIstanzeRegalateByAnagrafica(ses, idToRemove, false, 0, Integer.MAX_VALUE);
		List<IstanzeAbbonamenti> promossiList = new IstanzeAbbonamentiDao().findIstanzePromosseByAnagrafica(ses, idToRemove, false, 0, Integer.MAX_VALUE);
		for (IstanzeAbbonamenti ia:propriList) {
			ia.setAbbonato(primary);
			iaDao.updateUnlogged(ses, ia);
		}
		for (IstanzeAbbonamenti ia:regaliList) {
			ia.setPagante(primary);
			iaDao.updateUnlogged(ses, ia);
		}
		for (IstanzeAbbonamenti ia:promossiList) {
			ia.setPromotore(primary);
			iaDao.updateUnlogged(ses, ia);
		}
	}
	
	public static void moveEvasioniFisiche(Session ses, Integer idPrimary, Integer idToRemove) {
		EvasioniFascicoliDao efDao = new EvasioniFascicoliDao();
		EvasioniArticoliDao eaDao = new EvasioniArticoliDao();
		List<EvasioniFascicoli> efList = efDao.findByAnagrafica(ses, idToRemove);
		for (EvasioniFascicoli ef:efList) {
			ef.setIdAnagrafica(idPrimary);;
			efDao.update(ses, ef);
		}
		List<EvasioniArticoli> eaList = eaDao.findByAnagrafica(ses, idToRemove);
		for (EvasioniArticoli ea:eaList) {
			ea.setIdAnagrafica(idPrimary);;
			eaDao.update(ses, ea);
		}
	}
	
	public static void moveFatture(Session ses, Integer idPrimary, Integer idToRemove) {
		FattureDao fDao = new FattureDao();
		List<Fatture> fList = fDao.findByAnagrafica(ses, idToRemove, true);
		for (Fatture f:fList) {
			f.setIdAnagrafica(idPrimary);
			fDao.update(ses, f);
		}
	}
	
	public static void movePagamenti(Session ses, Integer idPrimary, Integer idToRemove) {
		PagamentiDao pDao = new PagamentiDao();
		Anagrafiche primary = GenericDao.findById(ses, Anagrafiche.class, idPrimary);
		List<Pagamenti> pList = pDao.findByAnagrafica(ses, idToRemove, null, null);
		for (Pagamenti p:pList) {
			p.setAnagrafica(primary);
			pDao.update(ses, p);
		}
	}
	
	public static void moveCrediti(Session ses, Integer idPrimary, Integer idToRemove) {
		PagamentiCreditiDao credDao = new PagamentiCreditiDao();
		List<PagamentiCrediti> credList = credDao.findByAnagrafica(ses, idToRemove, null);
		for (PagamentiCrediti cred:credList) {
			cred.setIdAnagrafica(idPrimary);
			credDao.update(ses, cred);
		}
	}
	
	public static void moveOrdiniLogistica(Session ses, Integer idPrimary, Integer idToRemove) {
		OrdiniLogisticaDao olDao = new OrdiniLogisticaDao();
		List<OrdiniLogistica> olList = olDao.findOrdiniByAnagrafica(ses, false, idToRemove, 0, Integer.MAX_VALUE);
		for (OrdiniLogistica ol:olList) {
			ol.setIdAnagrafica(idPrimary);
			olDao.update(ses, ol);
		}
	}
	
}
