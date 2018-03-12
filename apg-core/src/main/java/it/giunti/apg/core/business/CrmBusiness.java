package it.giunti.apg.core.business;

import it.giunti.apg.core.persistence.CacheCrmDao;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.CacheCrm;

import org.hibernate.Session;

public class CrmBusiness {

	public static void saveOrUpdate(Session ses, Anagrafiche a) {
		CacheCrm cc = null;
		cc = new CacheCrmDao().findByAnagraficheUid(ses, a.getUid());
		if (cc == null) cc = new CacheCrm();
		//CC Anagrafica
		cc.setIdCustomer(a.getUid());
		cc.setAddressTitle(a.getIndirizzoPrincipale().getTitolo());
		cc.setAddressFirstName(a.getIndirizzoPrincipale().getNome());
		cc.setAddressLastNameCompany(a.getIndirizzoPrincipale().getCognomeRagioneSociale());
		cc.setAddressCo(a.getIndirizzoPrincipale().getPresso());
		cc.setAddressAddress(a.getIndirizzoPrincipale().getIndirizzo());
		cc.setAddressLocality(a.getIndirizzoPrincipale().getLocalita());
		cc.setAddressProvince(a.getIndirizzoPrincipale().getProvincia());
		cc.setAddressZip(a.getIndirizzoPrincipale().getCap());
		cc.setAddressCountryCode(a.getIndirizzoPrincipale().getNazione().getSiglaNazione());
		cc.setSex(a.getSesso());
		cc.setCodFisc(a.getCodiceFiscale());
		cc.setPiva(a.getPartitaIva());
		cc.setPhoneMobile(a.getTelMobile());
		cc.setPhoneLandline(a.getTelCasa());
		cc.setEmailPrimary(a.getEmailPrimaria());
		if (a.getProfessione() != null) {
			cc.setIdJob(a.getProfessione().getId());
		} else { cc.setIdJob(null); }
		if (a.getTitoloStudio() != null) {
			cc.setIdQualification(a.getTitoloStudio().getId());
		} else { cc.setIdQualification(null); }
		cc.setIdTipoAnagrafica(a.getIdTipoAnagrafica());
		cc.setBirthDate(a.getDataNascita());
		cc.setConsentTos(a.getConsensoTos());
		cc.setConsentMarketing(a.getConsensoMarketing());
		cc.setConsentProfiling(a.getConsensoProfilazione());
		***
		
		//CC recap
		cc.setCustomerType(customerType);
	}
}
