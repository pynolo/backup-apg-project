package it.giunti.apg.core.business;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;

import java.util.List;

public class QueryResultFormatBusiness {
	private static final String EOL = "\r\n";
	private static final String SEP = ";";
	
	public static String format(List<IstanzeAbbonamenti> iaList) {
		String result = createHeader()+EOL;
		for (IstanzeAbbonamenti ia:iaList) {
			result += formatLine(ia)+EOL;
		}
		return result;
	}
	
	private static String createHeader() {
		String header = "BEN_codice"+SEP+"BEN_ragSoc"+SEP+"BEN_nome"+SEP+"BEN_presso"+SEP+
				"BEN_indirizzo"+SEP+"BEN_cap"+SEP+"BEN_loc"+SEP+"BEN_prov"+SEP+"BEN_naz"+SEP+"BEN_email"+SEP;
		header += "PAG_codice"+SEP+"PAG_ragSoc"+SEP+"PAG_nome"+SEP+"PAG_presso"+SEP;
		header += "PRM_codice"+SEP+"PRM_ragSoc"+SEP+"PRM_nome"+SEP+"PRM_presso"+SEP;
		header += "abbonamento"+SEP+"tipo"+SEP+"copie"+SEP+"inizio"+SEP+"fine"+SEP+
				"adesione"+SEP+"pagato"+SEP+"fatt"+SEP+"disd"+SEP+"bloc"+SEP+"opz";
		return header;
	}
	
	private static String formatLine(IstanzeAbbonamenti ia) {
		String line = "";
		//Anagrafiche
		Anagrafiche ben = ia.getAbbonato();
		line += "\""+ben.getUid()+"\""+SEP;
		line += clean(ben.getIndirizzoPrincipale().getCognomeRagioneSociale())+SEP;
		line += clean(ben.getIndirizzoPrincipale().getNome())+SEP;
		line += clean(ben.getIndirizzoPrincipale().getPresso())+SEP;
		line += clean(ben.getIndirizzoPrincipale().getIndirizzo())+SEP;
		line += "\""+clean(ben.getIndirizzoPrincipale().getCap())+"\""+SEP;
		line += clean(ben.getIndirizzoPrincipale().getLocalita())+SEP;
		line += (ben.getIndirizzoPrincipale().getProvincia() != null) ? ben.getIndirizzoPrincipale().getProvincia()+SEP : SEP;
		line += ben.getIndirizzoPrincipale().getNazione().getNomeNazione()+SEP;
		line += clean(ben.getEmailPrimaria()).toLowerCase()+SEP;
		Anagrafiche pag = ia.getPagante();
		if (pag == null) {
			pag = new Anagrafiche();
			pag.setIndirizzoPrincipale(new Indirizzi());
		}
		line += "\""+clean(pag.getUid())+"\""+SEP;
		line += clean(pag.getIndirizzoPrincipale().getCognomeRagioneSociale())+SEP;
		line += clean(pag.getIndirizzoPrincipale().getNome())+SEP;
		line += clean(pag.getIndirizzoPrincipale().getPresso())+SEP;
		Anagrafiche prm = ia.getPromotore();
		if (prm == null) {
			prm = new Anagrafiche();
			prm.setIndirizzoPrincipale(new Indirizzi());
		}
		line += "\""+clean(prm.getUid())+"\""+SEP;
		line += clean(prm.getIndirizzoPrincipale().getCognomeRagioneSociale())+SEP;
		line += clean(prm.getIndirizzoPrincipale().getNome())+SEP;
		line += clean(prm.getIndirizzoPrincipale().getPresso())+SEP;
		
		//Istanza
		line += "\""+ia.getAbbonamento().getCodiceAbbonamento()+"\""+SEP;
		line += "\""+ia.getListino().getTipoAbbonamento().getCodice()+"\""+SEP;
		line += ia.getCopie()+SEP;
		line += ServerConstants.FORMAT_DAY.format(ia.getFascicoloInizio().getDataInizio())+SEP;
		line += ServerConstants.FORMAT_DAY.format(ia.getFascicoloFine().getDataFine())+SEP;
		line += (ia.getAdesione() != null) ? ia.getAdesione().getCodice()+SEP : SEP;
		line += (ia.getPagato()) ? "1"+SEP : "0"+SEP;
		line += (ia.getFatturaDifferita()) ? "1"+SEP : "0"+SEP;
		line += (ia.getDataDisdetta() != null) ? ServerConstants.FORMAT_DAY.format(ia.getDataDisdetta())+SEP : SEP;
		line += (ia.getInvioBloccato()) ? "1"+SEP : "0"+SEP;
		line += getOpzioniCodeList(ia);
		return line;
	}
	
	private static String clean(String s) { //Escape Separator
		if (s == null) s = "";
		s=s.replaceAll(SEP, ":");
		s=s.trim();
		return s;
	}
	
	private static String getOpzioniCodeList(IstanzeAbbonamenti ia) {
		String result = "";
		if (ia.getOpzioniIstanzeAbbonamentiSet() != null) {
			for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
				result += oia.getOpzione().getUid()+" ";
			}
		}
		return result;
	}
}
