package it.giunti.apg.export.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional(propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
public class ExportService {
	private static Logger LOG = LoggerFactory.getLogger(ExportService.class);
	private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	
	@Value("${apg.export.order}")
	private String apgExportOrder;
	private String[] orderArray;

	@Autowired
	CrmExportConfigDao crmExportConfigDao;
	@Autowired
	CrmExportDao crmExportDao;
	@Autowired
	AnagraficheDao anagraficheDao;
	@Autowired
	IstanzeAbbonamentiDao istanzeAbbonamentiDao;
	
	
	public int exportCluster(boolean fullExport, Date beginTimestamp, Date endTimestamp) {
		
		LOG.info("STEP 1: finding changes and status variations");
		Map<Integer, Date> idMap = findClusterIdsToUpdate(fullExport, beginTimestamp, endTimestamp);
		Date clusterEndTimestamp = new Date();
		for (Integer key:idMap.keySet()) {
			Date ts = idMap.get(key);
			if (ts.after(clusterEndTimestamp)) clusterEndTimestamp = ts;
		}
		
		LOG.info("STEP 2: acquiring full data for changed items");
		Set<ExportBean> itemSet = fillExportItems(idMap.keySet());
		
		LOG.info("STEP 3: updating crm_export rows");
		updateCrmExportData(itemSet);
		
		saveNextTimestamp(clusterEndTimestamp);
		int clusterRows = idMap.size();
		return clusterRows;
	}

	
	// Functions for running job checks
	
	
	public boolean checkExportRunning() {
		boolean isRunning = true;
		CrmExportConfig config = crmExportConfigDao.selectById(ApgExportApplication.CONFIG_EXPORT_RUNNING_TIMESTAMP);
		if (config == null) {
			isRunning = false;
		}
		return isRunning;
	}

	public void markExportStarted() {
		CrmExportConfig config = crmExportConfigDao.selectById(ApgExportApplication.CONFIG_EXPORT_RUNNING_TIMESTAMP);
		if (config == null) {
			CrmExportConfig cec = new CrmExportConfig();
			cec.setId(ApgExportApplication.CONFIG_EXPORT_RUNNING_TIMESTAMP);
			cec.setVal(SDF.format(new Date()));
			crmExportConfigDao.insert(cec);
		} else {
			//throw new ConcurrencyFailureException("Tried to mark as started an already running job");
			config.setVal(SDF.format(new Date()));
			crmExportConfigDao.update(config);
		}
	}

	public void markExportFinished() {
		CrmExportConfig config = crmExportConfigDao.selectById(ApgExportApplication.CONFIG_EXPORT_RUNNING_TIMESTAMP);
		if (config != null) {
			crmExportConfigDao.delete(ApgExportApplication.CONFIG_EXPORT_RUNNING_TIMESTAMP);
		}
	}
	
	public void updateLastRunEnd() throws ConcurrencyFailureException {
		CrmExportConfig config = crmExportConfigDao.selectById(ApgExportApplication.CONFIG_EXPORT_RUNNING_TIMESTAMP);
		if (config == null) {
			CrmExportConfig cec = new CrmExportConfig();
			cec.setId(ApgExportApplication.CONFIG_EXPORT_RUNNING_TIMESTAMP);
			cec.setVal(new Long(new Date().getTime()).toString());
			crmExportConfigDao.insert(cec);
		} else {
			config.setVal(new Long(new Date().getTime()).toString());
			crmExportConfigDao.update(config);
		}
	}
	
	
	// Functions for begin/end timestamps

	
	public Date loadEndTimestamp() {
		Date endTimestamp = anagraficheDao.findLastUpdateTimestamp();
		Date endTimestampIa = istanzeAbbonamentiDao.findLastUpdateTimestamp();
		if (endTimestampIa.after(endTimestamp)) endTimestamp = endTimestampIa;
		return endTimestamp;
	}

	public Date loadBeginTimestamp() {
		//Find updateTimestamp of last run
		Date beginTimestamp = new Date(0L);
		CrmExportConfig config = crmExportConfigDao.selectById(ApgExportApplication.CONFIG_LAST_EXPORT_TIMESTAMP);
		if (config != null) {
			Long ts = new Long(config.getVal());
			beginTimestamp = new Date(ts);
		}
		return beginTimestamp;
	}
	
	
	//--------------------

	
	protected void saveNextTimestamp(Date nextTimestamp) {
		CrmExportConfig config = crmExportConfigDao.selectById(ApgExportApplication.CONFIG_LAST_EXPORT_TIMESTAMP);
		if (config == null) {
			config = new CrmExportConfig();
			config.setId(ApgExportApplication.CONFIG_LAST_EXPORT_TIMESTAMP);
			config.setVal(new Long(nextTimestamp.getTime()).toString());
			crmExportConfigDao.insert(config);
		} else {
			config.setVal(new Long(nextTimestamp.getTime()).toString());
			crmExportConfigDao.update(config);
		}
	}
	
	//STEP 1: finding changes and status variations
	//clusterEndTimestamp:
	//se fullExport è definito dall'ultimo timestamp della query sulle anagrafiche modificate (1.1f)
	//altrimenti è definito dall'ultimo timestamp della query su abbonamenti personali (1.1)
	protected Map<Integer, Date> findClusterIdsToUpdate(boolean fullExport, Date beginTimestamp, Date endTimestamp) {
		Map<Integer, Date> idMap = new HashMap<Integer, Date>();
		
		if (fullExport) {
			// FULL EXPORT
			LOG.info("1.1f - Finding 'id' in changed anagrafiche");
			List<Object[]> list = 
					anagraficheDao.findIdTimestampByTimestamp(new Date(0L), endTimestamp, 0, ApgExportApplication.CLUSTER_SIZE);
			for (Object[] obj:list) {
				Date objTs = (Date) obj[1];
				idMap.put((Integer) obj[0], objTs);
			}
			//entityManager.flush();
			//entityManager.clear();
			LOG.info("1.1f - Changed anagrafiche: "+list.size()+" total: "+idMap.size());
		} else {
			Date clusterEndTimestamp = new Date(0L);
			
			// NORMAL EXPORT
			LOG.info("1.1 - Finding 'id_abbonato' in changed istanze_abbonamenti");
			List<Object[]> list = istanzeAbbonamentiDao.findIdAbbonatoTimestampByTimestamp(beginTimestamp, endTimestamp, 
					0, ApgExportApplication.CLUSTER_SIZE);
			for (Object[] obj:list) {
				Date objTs = (Date) obj[1];
				idMap.put((Integer) obj[0], objTs);
				if (objTs.after(clusterEndTimestamp)) clusterEndTimestamp = objTs;
			}
			//entityManager.flush();
			//entityManager.clear();
			LOG.info("1.1 - Changed istanze_abbonamenti(own): "+list.size()+" total: "+idMap.size());
			
			if (clusterEndTimestamp != null) {
				
				LOG.info("1.2 - Finding 'id_pagante' in changed istanze_abbonamenti");
				list = istanzeAbbonamentiDao.findIdPaganteTimestampByTimestamp(beginTimestamp, clusterEndTimestamp, 
						0, ApgExportApplication.CLUSTER_SIZE-idMap.size());
				for (Object[] obj:list) {
					Date objTs1 = (Date) obj[1];
					Date objTs2 = idMap.get((Integer) obj[0]);
					if (objTs2 != null) {
						if (objTs1.after(objTs2)) idMap.put((Integer) obj[0], objTs1);
					} else {
						idMap.put((Integer) obj[0], objTs1);
					}
					if (objTs1.after(clusterEndTimestamp)) clusterEndTimestamp = objTs1;
				}
				//entityManager.flush();
				//entityManager.clear();
				LOG.info("1.2 - Changed istanze_abbonamenti(payer): "+list.size()+" total: "+idMap.size());
				
				LOG.info("1.3 - Finding 'id' in changed anagrafiche");
				list = anagraficheDao.findIdTimestampByTimestamp(beginTimestamp, clusterEndTimestamp, 
						0, ApgExportApplication.CLUSTER_SIZE-idMap.size());
				for (Object[] obj:list) {
					Date objTs1 = (Date) obj[1];
					Date objTs2 = idMap.get((Integer) obj[0]);
					if (objTs2 != null) {
						if (objTs1.after(objTs2)) idMap.put((Integer) obj[0], objTs1);
					} else {
						idMap.put((Integer) obj[0], objTs1);
					}
					if (objTs1.after(clusterEndTimestamp)) clusterEndTimestamp = objTs1;
				}
				//entityManager.flush();
				//entityManager.clear();
				LOG.info("1.3 - Changed anagrafiche: "+list.size()+" total: "+idMap.size());
			}
		}
		return idMap;
	}
	
	//STEP 2: Fill export object 'ExportItem' and define next update timestamp
	protected Set<ExportBean> fillExportItems(Set<Integer> ids) {
		Date startTime = new Date();
		orderArray = apgExportOrder.split(",");
		Set<ExportBean> itemSet = new HashSet<ExportBean>();
		int count = 0;
		LOG.info("2.1 - Filling ExportItems with anagrafiche and last istanze_abbonamenti");
		for (Integer id:ids) {
			ExportBean item = new ExportBean();
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
			if (count%ApgExportApplication.PAGING_SIZE == 0) {
				//entityManager.flush();
				//entityManager.clear();
				Date now = new Date();
				Double averageMillisec = (double) (now.getTime()-startTime.getTime()) / (double) count;
				long esteemLong = startTime.getTime() + averageMillisec.longValue()*ids.size();
				Date esteemDate = new Date(esteemLong);
				LOG.info("  Filled:"+count+" finishing "+SDF.format(esteemDate));
			}
		}
		LOG.info("2.1 - Total ExportItems:"+count);
		return itemSet;
	}
	
	//STEP 3: persist changes to crm_export table
	protected void updateCrmExportData(Set<ExportBean> itemSet) {
		Set<CrmExport> crmExportSet = new HashSet<CrmExport>();
		int count = 0;
		LOG.info("3.1 - persisting ExportItems into crm_export");
		for (ExportBean item:itemSet) {
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
			
			if (item.getOwnSubscription0() != null) {
				ce.setOwnSubscriptionIdentifier0(item.getOwnSubscription0().getAbbonamento().getCodiceAbbonamento());
				ce.setOwnSubscriptionMedia0(encodeMedia(item.getOwnSubscription0().getListino()));
				ce.setOwnSubscriptionStatus0(encodeStatus(item.getOwnSubscription0(), item.getAnagrafica()));
				ce.setOwnSubscriptionCreationDate0(item.getOwnSubscription0().getAbbonamento().getDataCreazione());
				ce.setOwnSubscriptionEndDate0(item.getOwnSubscription0().getFascicoloFine().getDataFine());
			}
			if (item.getGiftSubscription0() != null)
				ce.setGiftSubscriptionEndDate0(item.getGiftSubscription0().getFascicoloFine().getDataFine());
			
			if (item.getOwnSubscription1() != null) {
				ce.setOwnSubscriptionIdentifier1(item.getOwnSubscription1().getAbbonamento().getCodiceAbbonamento());
				ce.setOwnSubscriptionMedia1(encodeMedia(item.getOwnSubscription1().getListino()));
				ce.setOwnSubscriptionStatus1(encodeStatus(item.getOwnSubscription1(), item.getAnagrafica()));
				ce.setOwnSubscriptionCreationDate1(item.getOwnSubscription1().getAbbonamento().getDataCreazione());
				ce.setOwnSubscriptionEndDate1(item.getOwnSubscription1().getFascicoloFine().getDataFine());
			}
			if (item.getGiftSubscription1() != null)
				ce.setGiftSubscriptionEndDate1(item.getGiftSubscription1().getFascicoloFine().getDataFine());
			
			if (item.getOwnSubscription2() != null) {
				ce.setOwnSubscriptionIdentifier2(item.getOwnSubscription2().getAbbonamento().getCodiceAbbonamento());
				ce.setOwnSubscriptionMedia2(encodeMedia(item.getOwnSubscription2().getListino()));
				ce.setOwnSubscriptionStatus2(encodeStatus(item.getOwnSubscription2(), item.getAnagrafica()));
				ce.setOwnSubscriptionCreationDate2(item.getOwnSubscription2().getAbbonamento().getDataCreazione());
				ce.setOwnSubscriptionEndDate2(item.getOwnSubscription2().getFascicoloFine().getDataFine());
			}
			if (item.getGiftSubscription2() != null)
				ce.setGiftSubscriptionEndDate2(item.getGiftSubscription2().getFascicoloFine().getDataFine());
			
			if (item.getOwnSubscription3() != null) {
				ce.setOwnSubscriptionIdentifier3(item.getOwnSubscription3().getAbbonamento().getCodiceAbbonamento());
				ce.setOwnSubscriptionMedia3(encodeMedia(item.getOwnSubscription3().getListino()));
				ce.setOwnSubscriptionStatus3(encodeStatus(item.getOwnSubscription3(), item.getAnagrafica()));
				ce.setOwnSubscriptionCreationDate3(item.getOwnSubscription3().getAbbonamento().getDataCreazione());
				ce.setOwnSubscriptionEndDate3(item.getOwnSubscription3().getFascicoloFine().getDataFine());
			}
			if (item.getGiftSubscription3() != null)
				ce.setGiftSubscriptionEndDate3(item.getGiftSubscription3().getFascicoloFine().getDataFine());
			
			if (item.getOwnSubscription4() != null) {
				ce.setOwnSubscriptionIdentifier4(item.getOwnSubscription4().getAbbonamento().getCodiceAbbonamento());
				ce.setOwnSubscriptionMedia4(encodeMedia(item.getOwnSubscription4().getListino()));
				ce.setOwnSubscriptionStatus4(encodeStatus(item.getOwnSubscription4(), item.getAnagrafica()));
				ce.setOwnSubscriptionCreationDate4(item.getOwnSubscription4().getAbbonamento().getDataCreazione());
				ce.setOwnSubscriptionEndDate4(item.getOwnSubscription4().getFascicoloFine().getDataFine());
			}
			if (item.getGiftSubscription4() != null)
				ce.setGiftSubscriptionEndDate4(item.getGiftSubscription4().getFascicoloFine().getDataFine());
			
			if (item.getOwnSubscription5() != null) {
				ce.setOwnSubscriptionIdentifier5(item.getOwnSubscription5().getAbbonamento().getCodiceAbbonamento());
				ce.setOwnSubscriptionMedia5(encodeMedia(item.getOwnSubscription5().getListino()));
				ce.setOwnSubscriptionStatus5(encodeStatus(item.getOwnSubscription5(), item.getAnagrafica()));
				ce.setOwnSubscriptionCreationDate5(item.getOwnSubscription5().getAbbonamento().getDataCreazione());
				ce.setOwnSubscriptionEndDate5(item.getOwnSubscription5().getFascicoloFine().getDataFine());
			}
			if (item.getGiftSubscription5() != null)
				ce.setGiftSubscriptionEndDate5(item.getGiftSubscription5().getFascicoloFine().getDataFine());
			
			if (item.getOwnSubscription6() != null) {
				ce.setOwnSubscriptionIdentifier6(item.getOwnSubscription6().getAbbonamento().getCodiceAbbonamento());
				ce.setOwnSubscriptionMedia6(encodeMedia(item.getOwnSubscription6().getListino()));
				ce.setOwnSubscriptionStatus6(encodeStatus(item.getOwnSubscription6(), item.getAnagrafica()));
				ce.setOwnSubscriptionCreationDate6(item.getOwnSubscription6().getAbbonamento().getDataCreazione());
				ce.setOwnSubscriptionEndDate6(item.getOwnSubscription6().getFascicoloFine().getDataFine());
			}
			if (item.getGiftSubscription6() != null)
				ce.setGiftSubscriptionEndDate6(item.getGiftSubscription6().getFascicoloFine().getDataFine());
			
			if (isInsert) {
				crmExportDao.insert(ce);
			} else {
				crmExportDao.update(ce);
			}
			crmExportSet.add(ce);
			count++;
			//flush and detaches objects every 250 cycles
			if (count%ApgExportApplication.PAGING_SIZE == 0) {
				crmExportDao.flushClear();
				LOG.info("  Persisted:"+count);
			}
		}
		LOG.info("3.1 - persisted "+count+" crm_export rows");
	}
	
	protected String encodeMedia(Listini lst) {
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
	
	protected String encodeStatus(IstanzeAbbonamenti ia, Anagrafiche anag) {
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
