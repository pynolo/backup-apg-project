package it.giunti.apgws.server.ws;

import it.giunti.apg.server.business.WsLogBusiness;
import it.giunti.apg.server.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.IstanzeStatusUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Opzioni;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.shared.model.OpzioniListini;
import it.giunti.apgws.server.WsConstants;
import it.giunti.apgws.server.business.CommonBusiness;
import it.giunti.apgws.wsbeans.giuntiscuolainfo2.Anagrafica;
import it.giunti.apgws.wsbeans.giuntiscuolainfo2.GetsubscriptiondataParams;
import it.giunti.apgws.wsbeans.giuntiscuolainfo2.GetsubscriptiondataResult;
import it.giunti.apgws.wsbeans.giuntiscuolainfo2.Giuntiscuolainfo2;
import it.giunti.apgws.wsbeans.giuntiscuolainfo2.Subscription;
import it.giunti.apgws.wsbeans.giuntiscuolainfo2.Supplemento;

import java.util.ArrayList;
import java.util.HashSet;
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

@WebService(serviceName = "giuntiscuolainfo2", portName = "giuntiscuolainfo2SOAP",
		endpointInterface = "it.giunti.apgws.wsbeans.giuntiscuolainfo2.Giuntiscuolainfo2",
		targetNamespace = "http://applicazioni.giunti.it/apgws/giuntiscuolainfo2",
		wsdlLocation = "WEB-INF/wsdl/giuntiscuolainfo2.wsdl")
public class Giuntiscuolainfo2Impl implements Giuntiscuolainfo2 {

	private static final Logger LOG = LoggerFactory.getLogger(Giuntiscuolainfo2Impl.class);

	@Override
	@WebMethod(action = "http://applicazioni.giunti.it/apgws/getsubscriptiondata")
	@WebResult(name = "GetsubscriptiondataResult", targetNamespace = "http://applicazioni.giunti.it/apgws/giuntiscuolainfo2", partName = "parameters")
	public GetsubscriptiondataResult getsubscriptiondata(
			@WebParam(name = "GetsubscriptiondataParams", targetNamespace = "http://applicazioni.giunti.it/apgws/giuntiscuolainfo2", partName = "parameters") GetsubscriptiondataParams parameters) {
		String codice = parameters.getCodice();
		GetsubscriptiondataResult result = new GetsubscriptiondataResult();
		
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
		Subscription subs = null;
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
					subs = convertSubscription(ses, abboResult, abboPrevious);
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
		result.setSubscription(subs);
		
		try {
			String params = "codice="+codice+WsConstants.SERVICE_SEPARATOR;
			WsLogBusiness.writeWsLog(WsConstants.SERVICE_GIUNTISCUOLAINFO2,
					"subscriberinfo", params, WsConstants.SERVICE_OK);
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
		}
		return result;
	}
	
	private Subscription convertSubscription(Session ses, IstanzeAbbonamenti abboResult, IstanzeAbbonamenti abboPrevious) {
		Subscription subs = new Subscription();
		subs.setCodice(abboResult.getAbbonamento().getCodiceAbbonamento());
		subs.setNomePeriodico(abboResult.getAbbonamento().getPeriodico().getNome());
		//Anagrafiche
		Anagrafica anag = convertAnagrafica(abboResult.getAbbonato());
		subs.setAbbonato(anag);
		if (abboResult.getPagante() != null) {
			Anagrafica pagn = convertAnagrafica(abboResult.getPagante());
			subs.setPagante(pagn);
		}
		//Stato abbonamento
		boolean inRegola = IstanzeStatusUtil.isInRegola(abboResult);
		subs.setInRegola(inRegola);
		subs.setFascicoloInizio(abboResult.getFascicoloInizio().getTitoloNumero());
		subs.setFascicoloFine(abboResult.getFascicoloFine().getTitoloNumero());
		//subscriptionStartDate
		subs.setSubscriptionStartDate(CommonBusiness.dateToXmlDate(
				abboResult.getFascicoloInizio().getDataInizio()));
		//subscriptionStartDate
		subs.setSubscriptionExpiryDate(CommonBusiness.dateToXmlDate(
				abboResult.getFascicoloFine().getDataFine()));
		////gracingExpiryDate
		//Fascicoli fasGracing = null;
		//if (inRegola) {
		//	fasGracing = new FascicoliDao().findFascicoliAfterFascicolo(ses,
		//			abboResult.getFascicoloFine(),
		//			abboResult.getListino().getGracingFinale());
		//} else {
		//	fasGracing = new FascicoliDao().findFascicoliAfterFascicolo(ses,
		//			abboResult.getFascicoloInizio(),
		//			abboResult.getListino().getGracingIniziale());
		//}
		//subs.setGracingExpiryDate(CommonBusiness.dateToXmlDate(
		//		fasGracing.getDataInizio()));
		//copie
		subs.setCopie(abboResult.getCopie()+"");
		//codiceTipoAbbonamento
		subs.setCodiceTipoAbbonamento(abboResult.getListino().getTipoAbbonamento().getCodice());
		//descrizioneTipoAbbonamento
		subs.setDescrizioneTipoAbbonamento(abboResult.getListino().getTipoAbbonamento().getNome());
		//totaleFascicoliTipoAbbonamento
		subs.setTotaleFascicoliTipoAbbonamento(abboResult.getListino().getNumFascicoli()+"");
		//totaleFascicoliAbbonamento
		subs.setTotaleFascicoliAbbonamento(abboResult.getFascicoliTotali()+"");
		//tagList (da opzioni nuove)
		Set<String> tagList = new HashSet<String>();
		Set<OpzioniIstanzeAbbonamenti> opzioniAttualiSet = abboResult.getOpzioniIstanzeAbbonamentiSet();
		if (opzioniAttualiSet != null) {
			for (OpzioniIstanzeAbbonamenti opz:opzioniAttualiSet) {
				if (opz.getOpzione().getTag() != null) {
					String[] tags = opz.getOpzione().getTag()
							.split(AppConstants.STRING_SEPARATOR);
					for (String tag:tags) {
						addTag(tagList, tag);
					}
				}
			}
		}
		//tagList (da opzioni obbligatorie)
		if (tagList.size() == 0) {
			Set<OpzioniListini> opzioniObbligatorieSet = abboResult.getListino().getOpzioniListiniSet();
			if (opzioniObbligatorieSet != null) {
				for (OpzioniListini opz:opzioniObbligatorieSet) {
					if (opz.getOpzione().getTag() != null) {
						String[] tags = opz.getOpzione().getTag()
								.split(AppConstants.STRING_SEPARATOR);
						for (String tag:tags) {
							addTag(tagList, tag);
						}
					}
				}
			}
		}
		////tagList (da opzioni vecchie)
		//if ((abboPrevious != null) && (tagList.size() == 0)) {
		//	Set<OpzioniIstanzeAbbonamenti> opzioniVecchieSet = abboPrevious.getOpzioniIstanzeAbbonamentiSet();
		//	if (opzioniVecchieSet != null) {
		//		for (OpzioniIstanzeAbbonamenti opz:opzioniVecchieSet) {
		//			if (opz.getOpzione().getTag() != null) {
		//				String[] tags = opz.getOpzione().getTag()
		//						.split(AppConstants.STRING_SEPARATOR);
		//				for (String tag:tags) {
		//					addTag(tagList, tag);
		//				}
		//			}
		//		}
		//	}
		//}
		//tagList (da listino corrente)
		if (abboResult.getListino().getTag() != null) {
			String[] tags = abboResult.getListino().getTag()
					.split(AppConstants.STRING_SEPARATOR);
			for (String tag:tags) {
				addTag(tagList, tag);
			}
		}
		subs.getTagList().addAll(tagList);
		//supplementiList
		List<Supplemento> supList = new ArrayList<Supplemento>();
		if (opzioniAttualiSet != null) {
			for (OpzioniIstanzeAbbonamenti opz:opzioniAttualiSet) {
				supList.add(convertOpzione(opz.getOpzione(), abboResult));
			}
		}
		//supplementi obbligatori, se mancanti
		if (supList.size() == 0) {
			Set<OpzioniListini> opzioniListiniSet = abboResult.getListino().getOpzioniListiniSet();
			if (opzioniListiniSet != null) {
				for (OpzioniListini opz:opzioniListiniSet) {
					supList.add(convertOpzione(opz.getOpzione(), abboResult));
				}
			}
		}
		//Non è più necessario recuperare i supplementi dall'anno scorso
		//if ((abboPrevious != null) && (supList.size() == 0)) {
		//	Set<OpzioniIstanzeAbbonamenti> opzioniVecchieSet = abboPrevious.getOpzioniIstanzeAbbonamentiSet();
		//	if (opzioniVecchieSet != null) {
		//		for (OpzioniIstanzeAbbonamenti opz:opzioniVecchieSet) {
		//			supList.add(convertOpzione(opz.getOpzione(), abboPrevious));
		//		}
		//	}
		//}
		subs.getSupplementiList().addAll(supList);
		//cartaceo
		subs.setCartaceo(abboResult.getListino().getCartaceo());
		//digitale
		boolean digitale = abboResult.getListino().getDigitale();
		if (tagList.contains(AppConstants.TAG_AREAEXTRA)) {//TODO non dovrebbe essere così cablato
			digitale = true;
		}
		subs.setDigitale(digitale);
		return subs;
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
		result.setNomeSupplemento(opz.getNome());
		boolean incluso = false;
		Set<OpzioniListini> inclusiSet = ia.getListino().getOpzioniListiniSet();
		if (inclusiSet.size() > 0) {
			for (OpzioniListini ol:inclusiSet) {
				if (opz.getId() == ol.getOpzione().getId()) incluso = true;
			}
		}
		result.setIncluso(incluso);
		return result;
	}

	private void addTag(Set<String> tagList, String tag) {
		tag = tag.trim();
		if (!tag.equals("")) {
			if (!tagList.contains(tag)) {
				tagList.add(tag);
			}
		}
	}
}

