package it.giunti.apg.automation.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.JCoDestination;

import it.giunti.apg.automation.business.MaterialiBusiness;
import it.giunti.apg.automation.business.EntityBusiness;
import it.giunti.apg.automation.business.FascicoliBusiness;
import it.giunti.apg.automation.business.OrderBean;
import it.giunti.apg.automation.sap.CustomDestinationDataProvider;
import it.giunti.apg.automation.sap.ZrfcApgCreaOdvBusiness;
import it.giunti.apg.automation.sap.ZrfcApgMaterialeBusiness;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.AvvisiBusiness;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.MaterialiSpedizione;
import it.giunti.apg.shared.model.Periodici;

public class SapOrdiniCreateJob implements Job {
	
	//private static final long serialVersionUID = 4394668127625471725L;
	static private Logger LOG = LoggerFactory.getLogger(SapOrdiniCreateJob.class);
		
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		LOG.info("Started job '"+jobCtx.getJobDetail().getKey().getName()+"'");
		//param: letterePeriodici
		String letterePeriodici = (String) jobCtx.getMergedJobDataMap().get("letterePeriodici");
		if (letterePeriodici == null) throw new JobExecutionException("letterePeriodici non definito");
		if (letterePeriodici.equals("")) throw new JobExecutionException("letterePeriodici non definito");
		String[] lettereArray = letterePeriodici.split(AppConstants.STRING_SEPARATOR);
		//param: JCO_ASHOST
		String ashost = (String) jobCtx.getMergedJobDataMap().get("JCO_ASHOST");
		if (ashost == null) throw new JobExecutionException("JCO_ASHOST non definito");
		if (ashost.equals("")) throw new JobExecutionException("JCO_ASHOST non definito");
		//param: JCO_GWHOST
		String gwhost = (String) jobCtx.getMergedJobDataMap().get("JCO_GWHOST");
		if (gwhost == null) throw new JobExecutionException("JCO_GWHOST non definito");
		if (gwhost.equals("")) throw new JobExecutionException("JCO_GWHOST non definito");
		//param: JCO_SYSNR
		String sysnr = (String) jobCtx.getMergedJobDataMap().get("JCO_SYSNR");
		if (sysnr == null) throw new JobExecutionException("JCO_SYSNR non definito");
		if (sysnr.equals("")) throw new JobExecutionException("JCO_SYSNR non definito");
		//param: JCO_CLIENT
		String client = (String) jobCtx.getMergedJobDataMap().get("JCO_CLIENT");
		if (client == null) throw new JobExecutionException("JCO_CLIENT non definito");
		if (client.equals("")) throw new JobExecutionException("JCO_CLIENT non definito");
		//param: JCO_USER
		String user = (String) jobCtx.getMergedJobDataMap().get("JCO_USER");
		if (user == null) throw new JobExecutionException("JCO_USER non definito");
		if (user.equals("")) throw new JobExecutionException("JCO_USER non definito");
		//param: JCO_PASSWD
		String passwd = (String) jobCtx.getMergedJobDataMap().get("JCO_PASSWD");
		if (passwd == null) throw new JobExecutionException("JCO_PASSWD non definito");
		if (passwd.equals("")) throw new JobExecutionException("JCO_PASSWD non definito");
		//param: JCO_LANG
		String lang = (String) jobCtx.getMergedJobDataMap().get("JCO_LANG");
		if (lang == null) throw new JobExecutionException("JCO_LANG non definito");
		if (lang.equals("")) throw new JobExecutionException("JCO_LANG non definito");

		//JOB
		int idRapporto;
		try {
			idRapporto = VisualLogger.get().createRapporto(
					jobCtx.getJobDetail().getKey().getName(),
					ServerConstants.DEFAULT_SYSTEM_USER);
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		}
		
		try {
			Date today = DateUtil.now();
			StringBuffer avviso = new StringBuffer();
			//Destination SAP
			JCoDestination sapDestination =
					new CustomDestinationDataProvider(ashost, gwhost, sysnr, client, user, passwd, lang)
					.getDestination();
			
			//Creazione e inserimento ordini
			int ordiniCount = atomicInsertToSapAndDb(sapDestination, lettereArray, today, avviso, idRapporto);
			//Avviso
			if ((avviso.length() > 0) && (ordiniCount > 0)) {
				AvvisiBusiness.writeAvviso("Invio ordini via SAP. "+avviso.toString(), false,
						ServerConstants.DEFAULT_SYSTEM_USER);
			}
		} catch (BusinessException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage(), e);
			throw new JobExecutionException(e);
		} catch (Exception e) {//SAP lancia eccezioni non dichiarate
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage(), e);
			throw new JobExecutionException(e);
		} finally {
			String titolo = "Invio ordini via SAP";
			VisualLogger.get().setLogTitle(idRapporto, titolo);
			try {
				VisualLogger.get().closeAndSaveRapporto(idRapporto);
			} catch (BusinessException e) {
				LOG.error(e.getMessage(), e);
				throw new JobExecutionException(e);
			}
		}
		LOG.info("Ended job '"+jobCtx.getJobDetail().getKey().getName()+"'");
	}
	
	private int atomicInsertToSapAndDb(JCoDestination sapDestination, String[] lettereArray,
			Date dataInserimento, StringBuffer avviso, int idRapporto)
			throws BusinessException {
		//Estrazione arretrati e articoli
		List<MaterialiSpedizione> msList = new ArrayList<MaterialiSpedizione>();
		
		// FASCICOLI
		VisualLogger.get().addHtmlInfoLine(idRapporto, "<b>FASE 1/4: Estrazione FASCICOLI da ordinare</b>");
		List<Periodici> periodici = EntityBusiness.periodiciFromUidArray(lettereArray);
		for (Periodici periodico:periodici) {
			//Ciclo su tutti i periodici
			List<MaterialiSpedizione> list = null;
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Elaborazione arretrati <b>"+periodico.getNome()+"</b>");
			try {
				//Estrae i fascicoli da ordinare
				list = FascicoliBusiness.extractArretratiDaSpedire(periodico.getId(), idRapporto);
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Arretrati in attesa: "+list.size());
			} catch (EmptyResultException e) {
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Nessun arretrato da ordinare");
				list = new ArrayList<MaterialiSpedizione>();
			}
			if (list.size() > 0) {
				if (avviso.length() > 0) avviso.append(", ");
				avviso.append("Fascicoli "+periodico.getUid()+": "+list.size());
			}
			msList.addAll(list);
		}
		
		// ARTICOLI
		VisualLogger.get().addHtmlInfoLine(idRapporto, "<b>FASE 2/4: Estrazione ARTICOLI da ordinare</b>");
		try {
			//Estrae gli articoli abbinati a istanze (manuali, da listino o da opzioni)
			List<MaterialiSpedizione> listPeriodici = MaterialiBusiness.findPendingSpedizioniIstanze(dataInserimento, idRapporto);
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Articoli abbinati a istanze: "+listPeriodici.size());
			msList.addAll(listPeriodici);
		} catch (EmptyResultException e) {
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Nessun articolo da ordinare per istanze");
		}
		try {
			//Estrae gli articoli abbinati a anagrafiche
			List<MaterialiSpedizione> listAnagrafiche = MaterialiBusiness.findPendingSpedizioniAnagrafiche(dataInserimento, idRapporto);
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Articoli abbinati ad anagrafiche: "+listAnagrafiche.size());
			msList.addAll(listAnagrafiche);
		} catch (EmptyResultException e) {
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Nessun articolo da ordinare per anagrafiche");
		}
		if (msList.size() > 0) {
			if (avviso.length() > 0) avviso.append(". ");
			avviso.append("Articoli: "+msList.size());
		}
		if (msList.size() == 0) {
			return 0;
		}
		
		
		//Costruzione ordini
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		int ordiniCount = 0;
		try {
			//Raggruppa i fascicoli creando gli ordini e marca i fascicoli come ORDINATI
			VisualLogger.get().addHtmlInfoLine(idRapporto, "<b>FASE 3/4: Creazione ordini per SAP</b>");
			List<OrderBean> ordList =
					FascicoliBusiness.createOrdiniLogistica(ses, msList, dataInserimento, idRapporto);
			ordiniCount = ordList.size();
			//Invio a SAP
			VisualLogger.get().addHtmlInfoLine(idRapporto, "<b>FASE 4/4: Invio ordini via SAP</b>");
			ZrfcApgMaterialeBusiness
				.checkGiacenzaAndModifyOrders(ses, sapDestination, ordList, idRapporto);
			ZrfcApgCreaOdvBusiness
				.sendAndModifyOrders(ses, sapDestination, ordList, idRapporto);
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return ordiniCount;
	}
	

	
}
