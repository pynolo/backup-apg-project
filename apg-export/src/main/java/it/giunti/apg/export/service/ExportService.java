package it.giunti.apg.export.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.giunti.apg.export.ApgExportApplication;
import it.giunti.apg.export.CrmExportMediaEnum;
import it.giunti.apg.export.CrmExportStatusEnum;
import it.giunti.apg.export.dao.AnagraficheDao;
import it.giunti.apg.export.dao.CrmExportConfigDao;
import it.giunti.apg.export.dao.CrmExportDao;
import it.giunti.apg.export.dao.IstanzeAbbonamentiDao;
import it.giunti.apg.export.model.Anagrafiche;
import it.giunti.apg.export.model.CrmExport;
import it.giunti.apg.export.model.CrmExportConfig;
import it.giunti.apg.export.model.IstanzeAbbonamenti;
import it.giunti.apg.export.model.Listini;

@Service("exportService")
public class ExportService {
	private static Logger LOG = LoggerFactory.getLogger(ApgExportApplication.class);
	
	@Value("${apg.export.order}")
	private String apgExportOrder;
	private String[] orderArray;
	
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	CrmExportConfigDao crmExportConfigDao;
	@Autowired
	CrmExportDao crmExportDao;
	@Autowired
	AnagraficheDao anagraficheDao;
	@Autowired
	IstanzeAbbonamentiDao istanzeAbbonamentiDao;
	
	private Date beginTimestamp = null;
	private Date endTimestamp = null;
	
	@Transactional
	public void runExport(boolean fullExport) {
		orderArray = apgExportOrder.split(",");
		loadBeginTimestamp();
		loadEndTimestamp();
		LOG.info("STEP 1: finding changes and status variations");
		Set<Integer> anagraficheIds = findAnagraficheIdsToUpdate(fullExport);
		LOG.info("STEP 2: acquiring full data for changed items");
		Set<ExportItem> itemSet = fillExportItems(anagraficheIds);
		LOG.info("STEP 3: updating crm_export rows");
		updateCrmExportData(itemSet);
		saveNextTimestamp();
		LOG.info("FINISHED: updated "+itemSet.size()+" crm_export rows");
	}
	
	private void loadBeginTimestamp() {
		//Find updateTimestamp of last run
		CrmExportConfig config = crmExportConfigDao.selectById(ApgExportApplication.LAST_EXPORT_TIMESTAMP);
		if (config == null) {
			beginTimestamp = new Date(0L);
		} else {
			beginTimestamp = config.getUpdateTimestamp();
		}
	}
	
	private void loadEndTimestamp() {
		endTimestamp = anagraficheDao.findLastUpdateTimestamp();
		Date endTimestampIa = istanzeAbbonamentiDao.findLastUpdateTimestamp();
		if (endTimestampIa.after(endTimestamp)) endTimestamp = endTimestampIa;
	}
	
	//STEP 1: finding changes and status variations
	private Set<Integer> findAnagraficheIdsToUpdate(boolean fullExport) {
		if (fullExport) beginTimestamp = new Date(0L);
		Set<Integer> changedIds = new HashSet<Integer>();
		LOG.info("1.1 - Finding 'id' in changed anagrafiche");
		int count = 0;
		int size = 0;
		do {
			List<Integer> list = 
					anagraficheDao.findIdByTimestamp(beginTimestamp, endTimestamp, count, ApgExportApplication.PAGING);
			changedIds.addAll(list);
			entityManager.flush();
			entityManager.clear();
			size = list.size();
			count += size;
			LOG.info("  Found:"+count);
		} while (size > 0);
		LOG.info("1.1 - Changed anagrafiche: "+count+" total: "+changedIds.size());
		
		if (!fullExport) {
			LOG.info("1.2 - Finding 'id_abbonato' in changed istanze_abbonamenti");
			count = 0;
			do {
				List<Integer> list = 
						istanzeAbbonamentiDao.findIdAbbonatoByTimestamp(beginTimestamp, endTimestamp, count, ApgExportApplication.PAGING);
				changedIds.addAll(list);
				entityManager.flush();
				entityManager.clear();
				size = list.size();
				count += size;
				LOG.info("  Found:"+count);
			} while (size > 0);
			LOG.info("1.2 - Changed istanze_abbonamenti(own): "+count+" total: "+changedIds.size());
			
			LOG.info("1.3 - Finding 'id_pagante' in changed istanze_abbonamenti");
			count = 0;
			do {
				List<Integer> list = 
						istanzeAbbonamentiDao.findIdPaganteByTimestamp(beginTimestamp, endTimestamp, count, ApgExportApplication.PAGING);
				changedIds.addAll(list);
				entityManager.flush();
				entityManager.clear();
				size = list.size();
				count += size;
				LOG.info("  Found:"+count);
			} while (size > 0);
			LOG.info("3) Changed istanze_abbonamenti(payer): "+count+" total: "+changedIds.size());
		}
		return changedIds;
	}
	
	//STEP 2: Fill export object 'ExportItem' and define next update timestamp
	private Set<ExportItem> fillExportItems(Set<Integer> ids) {
		Set<ExportItem> itemSet = new HashSet<ExportItem>();
		int count = 0;
		LOG.info("2.1 - Filling ExportItems with anagrafiche and last istanze_abbonamenti");
		for (Integer id:ids) {
			ExportItem item = new ExportItem();
			//Anagrafiche
			Anagrafiche anag = anagraficheDao.selectById(id);
			item.setAnagrafica(anag);
			//ownSubscriptions
			List<IstanzeAbbonamenti> ownList = istanzeAbbonamentiDao.selectLastByIdAbbonato(id);
			for (IstanzeAbbonamenti ia:ownList) {
				if (ia.getAbbonamento().getPeriodico().getUid().equals(orderArray[0])) item.setOwnSubscription0(ia);
				if (ia.getAbbonamento().getPeriodico().getUid().equals(orderArray[1])) item.setOwnSubscription1(ia);
				if (ia.getAbbonamento().getPeriodico().getUid().equals(orderArray[2])) item.setOwnSubscription2(ia);
				if (ia.getAbbonamento().getPeriodico().getUid().equals(orderArray[3])) item.setOwnSubscription3(ia);
				if (ia.getAbbonamento().getPeriodico().getUid().equals(orderArray[4])) item.setOwnSubscription4(ia);
				if (ia.getAbbonamento().getPeriodico().getUid().equals(orderArray[5])) item.setOwnSubscription5(ia);
				if (ia.getAbbonamento().getPeriodico().getUid().equals(orderArray[6])) item.setOwnSubscription6(ia);
			}
			//Gift subscriptions
			List<IstanzeAbbonamenti> giftList = istanzeAbbonamentiDao.selectLastByIdPagante(id);
			for (IstanzeAbbonamenti ia:giftList) {
				if (ia.getAbbonamento().getPeriodico().getUid().equals(orderArray[0])) item.setGiftSubscription0(ia);
				if (ia.getAbbonamento().getPeriodico().getUid().equals(orderArray[1])) item.setGiftSubscription1(ia);
				if (ia.getAbbonamento().getPeriodico().getUid().equals(orderArray[2])) item.setGiftSubscription2(ia);
				if (ia.getAbbonamento().getPeriodico().getUid().equals(orderArray[3])) item.setGiftSubscription3(ia);
				if (ia.getAbbonamento().getPeriodico().getUid().equals(orderArray[4])) item.setGiftSubscription4(ia);
				if (ia.getAbbonamento().getPeriodico().getUid().equals(orderArray[5])) item.setGiftSubscription5(ia);
				if (ia.getAbbonamento().getPeriodico().getUid().equals(orderArray[6])) item.setGiftSubscription6(ia);
			}
			itemSet.add(item);
			count++;
			//flush and detaches objects every 'paging' cycles
			if (count%ApgExportApplication.PAGING == 0) {
				entityManager.flush();
				entityManager.clear();
				LOG.info("  Filled:"+count);
			}
		}
		LOG.info("2.1 - Total ExportItems:"+count);
		return itemSet;
	}
	
	//STEP 3: persist changes to crm_export table
	private void updateCrmExportData(Set<ExportItem> itemSet) {
		Set<CrmExport> crmExportSet = new HashSet<CrmExport>();
		int count = 0;
		LOG.info("3.1 - persisting ExportItems into crm_export");
		for (ExportItem item:itemSet) {
			boolean isInsert = false;
			CrmExport ce = crmExportDao.selectByUid(item.getAnagrafica().getUid());
			if (ce == null) {
				isInsert = true;
				ce = new CrmExport();
				ce.setUid(item.getAnagrafica().getUid());
			}
			ce.setIdentityUid(item.getAnagrafica().getIdentityUid());
			ce.setDeleted(item.getAnagrafica().isDeleted());
			ce.setMergedIntoUid(item.getAnagrafica().getMergedIntoUid());
			ce.setAddressTitle(item.getAnagrafica().getIndirizzoPrincipale().getTitolo());
			ce.setAddressFirstName(item.getAnagrafica().getIndirizzoPrincipale().getNome());
			ce.setAddressLastNameCompany(item.getAnagrafica().getIndirizzoPrincipale().getCognomeRagioneSociale());
			ce.setAddressCo(item.getAnagrafica().getIndirizzoPrincipale().getPresso());
			ce.setAddressAddress(item.getAnagrafica().getIndirizzoPrincipale().getIndirizzo());
			ce.setAddressLocality(item.getAnagrafica().getIndirizzoPrincipale().getLocalita());
			ce.setAddressProvince(item.getAnagrafica().getIndirizzoPrincipale().getProvincia());
			ce.setAddressZip(item.getAnagrafica().getIndirizzoPrincipale().getCap());
			ce.setAddressCountryCode(item.getAnagrafica().getIndirizzoPrincipale().getNazione().getSiglaNazione());
			ce.setSex(item.getAnagrafica().getSesso());
			ce.setCodFisc(item.getAnagrafica().getCodiceFiscale());
			ce.setPiva(item.getAnagrafica().getPartitaIva());
			ce.setPhoneMobile(item.getAnagrafica().getTelMobile());
			ce.setPhoneLandline(item.getAnagrafica().getTelCasa());
			ce.setEmailPrimary(item.getAnagrafica().getEmailPrimaria());
			ce.setIdJob(item.getAnagrafica().getIdProfessione());
			ce.setIdQualification(item.getAnagrafica().getIdTitoloStudio());
			ce.setIdTipoAnagrafica(item.getAnagrafica().getIdTipoAnagrafica());
			ce.setBirthDate(item.getAnagrafica().getDataNascita());
			ce.setConsentTos(item.getAnagrafica().getConsensoTos());
			ce.setConsentMarketing(item.getAnagrafica().getConsensoMarketing());
			ce.setConsentProfiling(item.getAnagrafica().getConsensoProfilazione());
			ce.setConsentUpdateDate(item.getAnagrafica().getDataAggiornamentoConsenso());
			
			ce.setOwnSubscriptionIdentifier0(item.getOwnSubscription0().getAbbonamento().getCodiceAbbonamento());
			ce.setOwnSubscriptionMedia0(encodeMedia(item.getOwnSubscription0().getListino()));
			ce.setOwnSubscriptionStatus0(encodeStatus(item.getOwnSubscription0(), item.getAnagrafica()));
			ce.setOwnSubscriptionCreationDate0(item.getOwnSubscription0().getAbbonamento().getDataCreazione());
			ce.setOwnSubscriptionEndDate0(item.getOwnSubscription0().getFascicoloFine().getDataFine());
			ce.setGiftSubscriptionEndDate0(item.getGiftSubscription0().getFascicoloFine().getDataFine());
			
			ce.setOwnSubscriptionIdentifier1(item.getOwnSubscription1().getAbbonamento().getCodiceAbbonamento());
			ce.setOwnSubscriptionMedia1(encodeMedia(item.getOwnSubscription1().getListino()));
			ce.setOwnSubscriptionStatus1(encodeStatus(item.getOwnSubscription1(), item.getAnagrafica()));
			ce.setOwnSubscriptionCreationDate1(item.getOwnSubscription1().getAbbonamento().getDataCreazione());
			ce.setOwnSubscriptionEndDate1(item.getOwnSubscription1().getFascicoloFine().getDataFine());
			ce.setGiftSubscriptionEndDate1(item.getGiftSubscription1().getFascicoloFine().getDataFine());
			
			ce.setOwnSubscriptionIdentifier2(item.getOwnSubscription2().getAbbonamento().getCodiceAbbonamento());
			ce.setOwnSubscriptionMedia2(encodeMedia(item.getOwnSubscription2().getListino()));
			ce.setOwnSubscriptionStatus2(encodeStatus(item.getOwnSubscription2(), item.getAnagrafica()));
			ce.setOwnSubscriptionCreationDate2(item.getOwnSubscription2().getAbbonamento().getDataCreazione());
			ce.setOwnSubscriptionEndDate2(item.getOwnSubscription2().getFascicoloFine().getDataFine());
			ce.setGiftSubscriptionEndDate2(item.getGiftSubscription2().getFascicoloFine().getDataFine());
			
			ce.setOwnSubscriptionIdentifier3(item.getOwnSubscription3().getAbbonamento().getCodiceAbbonamento());
			ce.setOwnSubscriptionMedia3(encodeMedia(item.getOwnSubscription3().getListino()));
			ce.setOwnSubscriptionStatus3(encodeStatus(item.getOwnSubscription3(), item.getAnagrafica()));
			ce.setOwnSubscriptionCreationDate3(item.getOwnSubscription3().getAbbonamento().getDataCreazione());
			ce.setOwnSubscriptionEndDate3(item.getOwnSubscription3().getFascicoloFine().getDataFine());
			ce.setGiftSubscriptionEndDate3(item.getGiftSubscription3().getFascicoloFine().getDataFine());
			
			ce.setOwnSubscriptionIdentifier4(item.getOwnSubscription4().getAbbonamento().getCodiceAbbonamento());
			ce.setOwnSubscriptionMedia4(encodeMedia(item.getOwnSubscription4().getListino()));
			ce.setOwnSubscriptionStatus4(encodeStatus(item.getOwnSubscription4(), item.getAnagrafica()));
			ce.setOwnSubscriptionCreationDate4(item.getOwnSubscription4().getAbbonamento().getDataCreazione());
			ce.setOwnSubscriptionEndDate4(item.getOwnSubscription4().getFascicoloFine().getDataFine());
			ce.setGiftSubscriptionEndDate4(item.getGiftSubscription4().getFascicoloFine().getDataFine());
			
			ce.setOwnSubscriptionIdentifier5(item.getOwnSubscription5().getAbbonamento().getCodiceAbbonamento());
			ce.setOwnSubscriptionMedia5(encodeMedia(item.getOwnSubscription5().getListino()));
			ce.setOwnSubscriptionStatus5(encodeStatus(item.getOwnSubscription5(), item.getAnagrafica()));
			ce.setOwnSubscriptionCreationDate5(item.getOwnSubscription5().getAbbonamento().getDataCreazione());
			ce.setOwnSubscriptionEndDate5(item.getOwnSubscription5().getFascicoloFine().getDataFine());
			ce.setGiftSubscriptionEndDate5(item.getGiftSubscription5().getFascicoloFine().getDataFine());
			
			ce.setOwnSubscriptionIdentifier6(item.getOwnSubscription6().getAbbonamento().getCodiceAbbonamento());
			ce.setOwnSubscriptionMedia6(encodeMedia(item.getOwnSubscription6().getListino()));
			ce.setOwnSubscriptionStatus6(encodeStatus(item.getOwnSubscription6(), item.getAnagrafica()));
			ce.setOwnSubscriptionCreationDate6(item.getOwnSubscription6().getAbbonamento().getDataCreazione());
			ce.setOwnSubscriptionEndDate6(item.getOwnSubscription6().getFascicoloFine().getDataFine());
			ce.setGiftSubscriptionEndDate6(item.getGiftSubscription6().getFascicoloFine().getDataFine());
			
			if (isInsert) {
				crmExportDao.insert(ce);
			} else {
				crmExportDao.update(ce);
			}
			crmExportSet.add(ce);
			count++;
			//flush and detaches objects every 250 cycles
			if (count%ApgExportApplication.PAGING == 0) {
				entityManager.flush();
				entityManager.clear();
				LOG.info("  Persisted:"+count);
			}
			LOG.info("3.1 - persisted "+count+" crm_export rows");
		}
	}
	
	private void saveNextTimestamp() {
		CrmExportConfig config = crmExportConfigDao.selectById(ApgExportApplication.LAST_EXPORT_TIMESTAMP);
		if (config == null) {
			config = new CrmExportConfig();
			config.setId(ApgExportApplication.LAST_EXPORT_TIMESTAMP);
			config.setUpdateTimestamp(endTimestamp);
			crmExportConfigDao.insert(config);
		} else {
			config.setUpdateTimestamp(endTimestamp);
			crmExportConfigDao.update(config);
		}
	}
	
	private String encodeMedia(Listini lst) {
		String media;
		if (lst.getCartaceo() && lst.getCartaceo()) {
			media = CrmExportMediaEnum.BOTH.getMedia();
		} else {
			if (lst.getCartaceo()) {
				media = CrmExportMediaEnum.PAPER.getMedia();
			} else {
				media = CrmExportMediaEnum.DIGITAL.getMedia();
			}
		}
		return media;
	}
	
	private String encodeStatus(IstanzeAbbonamenti ia, Anagrafiche anag) {
		String status;
		if (ia.getPagato() || ia.getFatturaDifferita()) {
			status = CrmExportStatusEnum.PAGATO.getStatus();
		} else {
			status = CrmExportStatusEnum.ATTESA_SALDO.getStatus();
			if (ia.getPropostaAcquisto())
				status = CrmExportStatusEnum.PROSPECT.getStatus();
		}
		if (ia.getIdAbbonato().equals(anag.getId()) && ia.getIdPagante() != null)
			status = CrmExportStatusEnum.BENEFICIARIO.getStatus();
		if (ia.getListino().getPrezzo() <= 0.1 && !ia.getListino().getFatturaDifferita())
			status = CrmExportStatusEnum.OMAGGIO.getStatus();
		if (ia.getDataDisdetta() != null)
			status = CrmExportStatusEnum.DISDETTA.getStatus();
		if (ia.getInvioBloccato())
			status = CrmExportStatusEnum.BLOCCATO.getStatus();
		return status;
	}
}
