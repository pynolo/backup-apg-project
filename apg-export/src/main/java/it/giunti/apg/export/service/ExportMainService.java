package it.giunti.apg.export.service;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import it.giunti.apg.export.ApgExportApplication;
import it.giunti.apg.export.dao.AnagraficheDao;
import it.giunti.apg.export.dao.CrmExportConfigDao;
import it.giunti.apg.export.dao.IstanzeAbbonamentiDao;
import it.giunti.apg.export.model.CrmExportConfig;

@Service("exportMainService")
@Transactional(propagation=Propagation.REQUIRED)
public class ExportMainService {
	private static Logger LOG = LoggerFactory.getLogger(ExportMainService.class);
	
	@Autowired
	private ExportService exportService;

	@Autowired
	CrmExportConfigDao crmExportConfigDao;
	@Autowired
	AnagraficheDao anagraficheDao;
	@Autowired
	IstanzeAbbonamentiDao istanzeAbbonamentiDao;

	
	@Transactional
	protected int exportCluster(boolean fullExport, Date beginTimestamp, Date endTimestamp) {
		
		LOG.info("STEP 1: finding changes and status variations");
		Map<Integer, Date> idMap = exportService.findClusterIdsToUpdate(fullExport, beginTimestamp, endTimestamp);
		Date clusterEndTimestamp = new Date();
		for (Integer key:idMap.keySet()) {
			Date ts = idMap.get(key);
			if (ts.after(clusterEndTimestamp)) clusterEndTimestamp = ts;
		}
		
		LOG.info("STEP 2: acquiring full data for changed items");
		Set<ExportBean> itemSet = exportService.fillExportItems(idMap.keySet());
		
		LOG.info("STEP 3: updating crm_export rows");
		exportService.updateCrmExportData(itemSet);
		
		exportService.saveNextTimestamp(clusterEndTimestamp);
		int clusterRows = idMap.size();
		return clusterRows;
	}

	
	// Functions for running job checks
	
	
	protected boolean checkExportRunning() {
		boolean isRunning = true;
		CrmExportConfig config = crmExportConfigDao.selectById(ApgExportApplication.CONFIG_EXPORT_RUNNING_TIMESTAMP);
		if (config == null) {
			isRunning = false;
		}
		return isRunning;
	}
	
	protected void markExportStarted() throws ConcurrencyFailureException {
		//Find updateTimestamp of last run
		CrmExportConfig config = crmExportConfigDao.selectById(ApgExportApplication.CONFIG_EXPORT_RUNNING_TIMESTAMP);
		if (config == null) {
			CrmExportConfig cec = new CrmExportConfig();
			cec.setId(ApgExportApplication.CONFIG_EXPORT_RUNNING_TIMESTAMP);
			cec.setVal(new Long(new Date().getTime()).toString());
			crmExportConfigDao.insert(cec);
		} else {
			throw new ConcurrencyFailureException("Tried to mark as started an already running job");
		}
	}

	protected void markExportFinished() throws ConcurrencyFailureException {
		//Find updateTimestamp of last run
		CrmExportConfig config = crmExportConfigDao.selectById(ApgExportApplication.CONFIG_EXPORT_RUNNING_TIMESTAMP);
		if (config == null) {
			throw new ConcurrencyFailureException("Tried to mark as finished an already finished job");
		} else {
			crmExportConfigDao.delete(ApgExportApplication.CONFIG_EXPORT_RUNNING_TIMESTAMP);
		}
	}
	
	// Functions for begin/end timestamps

	
	protected Date loadEndTimestamp() {
		Date endTimestamp = anagraficheDao.findLastUpdateTimestamp();
		Date endTimestampIa = istanzeAbbonamentiDao.findLastUpdateTimestamp();
		if (endTimestampIa.after(endTimestamp)) endTimestamp = endTimestampIa;
		return endTimestamp;
	}

	protected Date loadBeginTimestamp() {
		//Find updateTimestamp of last run
		Date beginTimestamp = new Date(0L);
		CrmExportConfig config = crmExportConfigDao.selectById(ApgExportApplication.CONFIG_LAST_EXPORT_TIMESTAMP);
		if (config != null) {
			Long ts = new Long(config.getVal());
			beginTimestamp = new Date(ts);
		}
		return beginTimestamp;
	}

}
