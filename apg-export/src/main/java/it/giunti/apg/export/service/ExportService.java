package it.giunti.apg.export.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.giunti.apg.export.ApgExportApplication;
import it.giunti.apg.export.dao.AnagraficheDao;
import it.giunti.apg.export.dao.CrmExportConfigDao;
import it.giunti.apg.export.dao.IstanzeAbbonamentiDao;
import it.giunti.apg.export.model.CrmExportConfig;

@Service("exportService")
public class ExportService {
	private static Logger LOG = LoggerFactory.getLogger(ApgExportApplication.class);
	
	@Autowired
	CrmExportConfigDao crmExportConfigDao;
	@Autowired
	AnagraficheDao anagraficheDao;
	@Autowired
	IstanzeAbbonamentiDao istanzeAbbonamentiDao;
	
	@Transactional
	public void runExport() {
		Set<Integer> anagraficheIds = findAnagraficheIdsToUpdate();
		Set<ExportItem> exportItems = fillExportItems(anagraficheIds);
		//TODO
		
	}
		
	private Set<Integer> findAnagraficheIdsToUpdate() {
		//Find updateTimestamp of last run
		CrmExportConfig config = crmExportConfigDao.selectById(ApgExportApplication.LAST_EXPORT_TIMESTAMP);
		if (config == null) {
			config = new CrmExportConfig();
			Date updateTimestamp = new Date(0L);
			config.setUpdateTimestamp(updateTimestamp);
		}
		Date startTimestamp = config.getUpdateTimestamp();
		
		Set<Integer> changedIds = new HashSet<Integer>();
		//1) Find changed anagrafiche and take their id's
		int count = 0;
		int size = 0;
		do {
			List<Integer> list = 
					anagraficheDao.findIdByUpdateTimestamp(startTimestamp, count, ApgExportApplication.PAGING);
			changedIds.addAll(list);
			size = list.size();
			count += size;
		} while (size > 0);
		LOG.debug("1) Changed Anagrafiche: "+count+" total: "+changedIds.size());
		
		//2) Find changed istanze_abbonamenti
		count = 0;
		do {
			List<Integer> list = 
					istanzeAbbonamentiDao.findIdAbbonatoByUpdateTimestamp(startTimestamp, count, ApgExportApplication.PAGING);
			changedIds.addAll(list);
			size = list.size();
			count += size;
		} while (size > 0);
		LOG.debug("2) Changed Istanze(own): "+count+" total: "+changedIds.size());
		//3) Find changed istanze_abbonamenti with payer
		count = 0;
		do {
			List<Integer> list = 
					istanzeAbbonamentiDao.findIdPaganteByUpdateTimestamp(startTimestamp, count, ApgExportApplication.PAGING);
			changedIds.addAll(list);
			size = list.size();
			count += size;
		} while (size > 0);
		LOG.debug("3) Changed Istanze(payer): "+count+" total: "+changedIds.size());
		
		//4) Find expired istanze_abbonamenti
		count = 0;
		do {
			List<Integer> list = 
					istanzeAbbonamentiDao.findExpiringSinceTimestamp(startTimestamp, count, ApgExportApplication.PAGING);
			changedIds.addAll(list);
			size = list.size();
			count += size;
		} while (size > 0);
		LOG.debug("4) Expired Istanze: "+count+" total: "+changedIds.size());
		return changedIds;
	}
	
	private Set<ExportItem> fillExportItems(Set<Integer> ids) {
		Set<ExportItem> itemSet = new HashSet<ExportItem>();
		for (Integer id:ids) {
			ExportItem item = new ExportItem();
			item.setAnagrafica(anagraficheDao.selectById(id));
			item.setOwnSubscription0(istanzeAbbonamentiDao.selectLastByIdAbbonato(id));
			item.setGiftSubscription0(istanzeAbbonamentiDao.selectLastByIdPagante(id));
			//TODO
			itemSet.add(item);
		}
		return itemSet;
	}
}
