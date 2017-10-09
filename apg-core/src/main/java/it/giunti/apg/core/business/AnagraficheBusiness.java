package it.giunti.apg.core.business;

import it.giunti.apg.core.ServerUtil;
import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.ContatoriDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IndirizziDao;
import it.giunti.apg.core.persistence.LocalitaDao;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.Localita;
import it.giunti.apg.shared.model.Nazioni;
import it.giunti.apg.shared.model.Professioni;
import it.giunti.apg.shared.model.TitoliStudio;

import java.io.Serializable;

import org.hibernate.Session;

public class AnagraficheBusiness {
	
	public static Integer saveOrUpdate(Session ses, Anagrafiche item, boolean verificaCap) throws BusinessException, ValidationException {
		ServerUtil.pojoToUppercase(item);
		AnagraficheDao anagDao = new AnagraficheDao();
		IndirizziDao indDao = new IndirizziDao();
		Integer id = null;
		//Controllo o generazione codice cliente
		if (item.getUid() == null) {
			String uid = new ContatoriDao().generateUidCliente(ses);
			item.setUid(uid);
		}
		//Indirizzo Fatturazione
		if (item.getIndirizzoFatturazione().getIdNazioneT() != null) {
			if (!item.getIndirizzoFatturazione().getIdNazioneT().equals("")) {
				Nazioni nazFatt = GenericDao.findById(ses, Nazioni.class, item.getIndirizzoFatturazione().getIdNazioneT());
				item.getIndirizzoFatturazione().setNazione(nazFatt);
			}
		}
		//Indirizzo Principale
		if (item.getIndirizzoPrincipale().getIdNazioneT() != null) {
			if (!item.getIndirizzoPrincipale().getIdNazioneT().equals("")) {
				Nazioni nazPrinc = GenericDao.findById(ses, Nazioni.class, item.getIndirizzoPrincipale().getIdNazioneT());
				item.getIndirizzoPrincipale().setNazione(nazPrinc);
			}
		}
		//Verifica Cap e lancia l'eccezione
		if (verificaCap) {
			verificaCap(ses, item.getIndirizzoPrincipale().getLocalita(), item.getIndirizzoPrincipale().getCap(),
					item.getIndirizzoPrincipale().getProvincia(), item.getIndirizzoPrincipale().getNazione().getSiglaNazione());
			verificaCap(ses, item.getIndirizzoFatturazione().getLocalita(), item.getIndirizzoFatturazione().getCap(),
					item.getIndirizzoFatturazione().getProvincia(), item.getIndirizzoFatturazione().getNazione().getSiglaNazione());
		}
		if (item.getId() != null) {
			//Indirizzi
			indDao.update(ses, item.getIndirizzoFatturazione());
			indDao.update(ses, item.getIndirizzoPrincipale());
			//Anagrafica
			if (item.getIdProfessioneT() != null) {
				if (!item.getIdProfessioneT().equals("")) {
					Integer idProfessione;
					try {
						idProfessione = Integer.valueOf(item.getIdProfessioneT());
					} catch (NumberFormatException e1) {
						idProfessione=0;
					}
					Professioni professione = GenericDao.findById(ses, Professioni.class, idProfessione);
					item.setProfessione(professione);
				}
				if (!item.getIdTitoloStudioT().equals("")) {
					Integer idTitoloStudio;
					try {
						idTitoloStudio = Integer.valueOf(item.getIdTitoloStudioT());
					} catch (NumberFormatException e1) {
						idTitoloStudio=0;
					}
					TitoliStudio ts = GenericDao.findById(ses, TitoliStudio.class, idTitoloStudio);
					item.setTitoloStudio(ts);
				}
			}
			item.setIdTipoAnagrafica(item.getIdTipoAnagrafica());
			item.setSearchString(SearchBusiness.buildAnagraficheSearchString(item));
			if (item.getDataCreazione() == null) item.setDataCreazione(DateUtil.now());
			anagDao.update(ses, item);
			id = item.getId();
		} else {
			//Crea Indirizzo Fatturazione
			Serializable idFatt = indDao.save(ses, item.getIndirizzoFatturazione());
			Indirizzi indFatt = GenericDao.findById(ses, Indirizzi.class, idFatt);
			item.setIndirizzoFatturazione(indFatt);
			//Crea Indirizzo Principale
			Serializable idPrinc = indDao.save(ses, item.getIndirizzoPrincipale());
			Indirizzi indPrinc = GenericDao.findById(ses, Indirizzi.class, idPrinc);
			item.setIndirizzoPrincipale(indPrinc);
			//Anagrafica
			if (item.getIdProfessioneT() != null) {
				if (!item.getIdProfessioneT().equals("")) {
					Professioni professione = null;
					Integer idProfessione = ValueUtil.stoi(item.getIdProfessioneT());
					if (idProfessione != null) {
						professione = GenericDao.findById(ses, Professioni.class, idProfessione);
					}
					item.setProfessione(professione);
				}
			}
			item.setIdTipoAnagrafica(item.getIdTipoAnagrafica());
			item.setSearchString(SearchBusiness.buildAnagraficheSearchString(item));
			if (item.getDataCreazione() == null) item.setDataCreazione(DateUtil.now());
			id = (Integer) anagDao.save(ses, item);
		}
		return id;
	}

	private static void verificaCap(Session ses, String localita, String cap, String provinciaSigla,
			String nazioneSigla) throws BusinessException, ValidationException {
		if (provinciaSigla == null) provinciaSigla = "";
		if (nazioneSigla != null) {
			if (!nazioneSigla.toUpperCase().equals("IT")) {
				return;
			}
		}
		if (localita == null) localita = "";
		if (cap == null) cap = "";
		if ((localita.length() > 0) || (cap.length() > 0)){
			Localita result = new LocalitaDao().findCapByLocalitaCapString(ses, localita, cap);
			if (result == null) {
				throw new BusinessException("Localita' e cap non corrispondono");
			} else {
				if (!result.getIdProvincia().equals(provinciaSigla)) {
					throw new BusinessException("Localita', cap e provincia non corrispondono");
				}
			}
			return;
		}
		//throw new ValidationException("Localita' e cap sono obbligatori");
	}

}
