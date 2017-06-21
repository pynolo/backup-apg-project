package it.giunti.apg.ws.soap;

import it.giunti.apg.core.business.WsLogBusiness;
import it.giunti.apg.core.persistence.FascicoliDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.IstanzeStatusUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Opzioni;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.ws.WsConstants;
import it.giunti.apg.ws.business.CommonBusiness;
import it.giunti.apgws.wsbeans.giuntiscuolainfo.Anagrafica;
import it.giunti.apgws.wsbeans.giuntiscuolainfo.Giuntiscuolainfo;
import it.giunti.apgws.wsbeans.giuntiscuolainfo.SubscriberData;
import it.giunti.apgws.wsbeans.giuntiscuolainfo.SubscriberinfoParams;
import it.giunti.apgws.wsbeans.giuntiscuolainfo.SubscriberinfoResult;
import it.giunti.apgws.wsbeans.giuntiscuolainfo.Supplemento;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService(serviceName = "giuntiscuolainfo", portName = "giuntiscuolainfoSOAP",
		endpointInterface = "it.giunti.apgws.wsbeans.giuntiscuolainfo.Giuntiscuolainfo",
		targetNamespace = "http://applicazioni.giunti.it/apgws/giuntiscuolainfo",
		wsdlLocation = "WEB-INF/wsdl/giuntiscuolainfo.wsdl")
public class GiuntiscuolainfoImpl implements Giuntiscuolainfo {

	private static final Logger LOG = LoggerFactory.getLogger(GiuntiscuolainfoImpl.class);

	@Override
	@WebMethod(action = "http://applicazioni.giunti.it/apgws/subscriberinfo")
	@WebResult(name = "SubscriberinfoResult", targetNamespace = "http://applicazioni.giunti.it/apgws/giuntiscuolainfo", partName = "parameters")
	public SubscriberinfoResult subscriberinfo(
			@WebParam(name = "SubscriberinfoParams", targetNamespace = "http://applicazioni.giunti.it/apgws/giuntiscuolainfo", partName = "parameters") SubscriberinfoParams parameters) {
		String codice = parameters.getCodice();
		SubscriberinfoResult result = new SubscriberinfoResult();
		
		if (codice == null) codice = "";
		if (codice.length() == 0) {
			result.setErrorCode(WsConstants.WS_ERR_PARAMETER);
			result.setErrorDesc(WsConstants.WS_ERR_PARAMETER_DESC);
			return result;
		}
		//Go search
		Session ses = SessionFactory.getSession();
		IstanzeAbbonamenti abboResult = null;
		IstanzeAbbonamenti abboPrevious = null;
		SubscriberData sd = null;
		try {
			List<IstanzeAbbonamenti> iaList = new IstanzeAbbonamentiDao().findIstanzeByCodice(ses, codice, 0, 2);
			if (iaList != null) {
				if (iaList.size() > 0) {
					abboResult = iaList.get(0);
					if (iaList.size() > 1) abboPrevious = iaList.get(1);
				}
			}
			if (abboResult != null) {
				if (!abboResult.getInvioBloccato()) {
					//Risponde se esiste e non è bloccato
					sd = convertSubscriberData(ses, abboResult, abboPrevious);
				} else {
					//Errore: dati rimossi
					LOG.warn(codice+" "+WsConstants.WS_ERR_REMOVED_DESC);
					result.setErrorCode(WsConstants.WS_ERR_REMOVED);
					result.setErrorDesc(WsConstants.WS_ERR_REMOVED_DESC);
				}
			} else {
				//Errore: dati non esistenti
				LOG.warn(codice+" "+WsConstants.WS_ERR_NOT_FOUND_DESC);
				result.setErrorCode(WsConstants.WS_ERR_NOT_FOUND);
				result.setErrorDesc(WsConstants.WS_ERR_NOT_FOUND_DESC);
			}
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			result.setErrorCode(WsConstants.WS_ERR_SYSTEM);
			result.setErrorDesc(WsConstants.WS_ERR_SYSTEM_DESC);
			return result;
		} finally {
			ses.close();
		}
		//Validation
		if (result.getErrorCode() != null) {
			return result;
		}
		//Format result
		result.setSubscriberData(sd);
		
		try {
			String params = "codice="+codice+WsConstants.SERVICE_SEPARATOR;
			WsLogBusiness.writeWsLog(WsConstants.SERVICE_GIUNTISCUOLAINFO,
					"subscriberinfo", params, WsConstants.SERVICE_OK);
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
		}
		return result;
	}
	
	private SubscriberData convertSubscriberData(Session ses, IstanzeAbbonamenti abboResult, IstanzeAbbonamenti abboPrevious) {
		SubscriberData sd = new SubscriberData();
		sd.setCodice(abboResult.getAbbonamento().getCodiceAbbonamento());
		sd.setNomePeriodico(abboResult.getAbbonamento().getPeriodico().getNome());
		//Anagrafiche
		Anagrafica anag = convertAnagrafica(abboResult.getAbbonato());
		sd.setAbbonato(anag);
		if (abboResult.getPagante() != null) {
			Anagrafica pagn = convertAnagrafica(abboResult.getPagante());
			sd.setPagante(pagn);
		}
		//Stato abbonamento
		boolean inRegola = IstanzeStatusUtil.isInRegola(abboResult);
		sd.setInRegola(inRegola);
		sd.setFascicoloInizio(abboResult.getFascicoloInizio().getTitoloNumero());
		sd.setFascicoloFine(abboResult.getFascicoloFine().getTitoloNumero());
		//Date
		sd.setSubscriptionExpiryDate(CommonBusiness.dateToXmlDate(
				abboResult.getFascicoloFine().getDataFine()));
		Fascicoli fasGracing = null;
		if (inRegola) {
			fasGracing = new FascicoliDao().findFascicoliAfterFascicolo(ses,
					abboResult.getFascicoloFine(),
					abboResult.getListino().getGracingFinale());
		} else {
			fasGracing = new FascicoliDao().findFascicoliAfterFascicolo(ses,
					abboResult.getFascicoloInizio(),
					abboResult.getListino().getGracingIniziale());
		}
		sd.setGracingExpiryDate(CommonBusiness.dateToXmlDate(
				fasGracing.getDataInizio()));
		//Supplementi
		List<Supplemento> supList = new ArrayList<Supplemento>();
		Set<OpzioniIstanzeAbbonamenti> set1 = abboResult.getOpzioniIstanzeAbbonamentiSet();
		if (set1 != null) {
			for (OpzioniIstanzeAbbonamenti opz:set1) {
				supList.add(convertOpzione(opz.getOpzione(), abboResult));
			}
		}
		//Non è più necessario recuperare i supplementi dall'anno scorso
		//if ((abboPrevious != null) && (supList.size() == 0)) {
		//	Set<OpzioniIstanzeAbbonamenti> set2 = abboPrevious.getOpzioniIstanzeAbbonamentiSet();
		//	if (set2 != null) {
		//		for (OpzioniIstanzeAbbonamenti opz:set2) {
		//			supList.add(convertOpzione(opz.getOpzione(), abboPrevious));
		//		}
		//	}
		//}
		sd.getSupplemento().addAll(supList);
		return sd;
	}

	private Anagrafica convertAnagrafica(Anagrafiche anag) {
		Anagrafica result = new Anagrafica();
		String nome = anag.getIndirizzoPrincipale().getCognomeRagioneSociale();
		if (anag.getIndirizzoPrincipale().getNome() != null) {
			if (anag.getIndirizzoPrincipale().getNome().length() > 0) {
				nome = anag.getIndirizzoPrincipale().getNome()+" "+nome;
			}
		}
		result.setNome(nome);
		if (anag.getIndirizzoPrincipale().getCap() != null) {
			if (anag.getIndirizzoPrincipale().getCap().length() > 0) {
				result.setCap(anag.getIndirizzoPrincipale().getCap());
			}
		}
		if (anag.getIndirizzoPrincipale().getIndirizzo() != null) {
			if (anag.getIndirizzoPrincipale().getIndirizzo().length() > 0) {
				result.setIndirizzo(anag.getIndirizzoPrincipale().getIndirizzo());
			}
		}
		if (anag.getIndirizzoPrincipale().getLocalita() != null) {
			if (anag.getIndirizzoPrincipale().getLocalita().length() > 0) {
				result.setLocalita(anag.getIndirizzoPrincipale().getLocalita());
			}
		}
		if (anag.getIndirizzoPrincipale().getPresso() != null) {
			if (anag.getIndirizzoPrincipale().getPresso().length() > 0) {
				result.setPresso(anag.getIndirizzoPrincipale().getPresso());
			}
		}
		if (anag.getIndirizzoPrincipale().getProvincia() != null) {
			result.setProvincia(anag.getIndirizzoPrincipale().getProvincia());
		} else {
			result.setProvincia(WsConstants.PROVINCIA_ESTERO_AUTH);
		}
		result.setSesso(anag.getSesso());
		if (anag.getEmailPrimaria() != null) {
			if (anag.getEmailPrimaria().length() > 0) {
				String[] emArray = anag.getEmailPrimaria().split(AppConstants.STRING_SEPARATOR);
				for (String em:emArray) {
					result.getEmail().add(em.trim());
				}
			}
		}
		return result;
	}
	
	private Supplemento convertOpzione(Opzioni opz, IstanzeAbbonamenti ia) {
		Supplemento result = new Supplemento();
		result.setCodiceSupplemento(opz.getUid().toString());
		result.setSubscriptionExpiryDate(CommonBusiness.dateToXmlDate(
				ia.getFascicoloFine().getDataInizio()));
		result.setNomeSupplemento(opz.getNome());
		if (opz.getTag() != null) {
			if (opz.getTag().length() > 0) result.setTags(opz.getTag());
		}
		return result;
	}

}

