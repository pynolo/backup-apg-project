package it.giunti.apg.automation.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import it.giunti.apg.core.ConfigUtil;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.FascicoliGroupBean;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.MaterialiSpedizioneDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.MaterialiSpedizione;

public class FascicoliBusiness {

	//private static final Logger LOG = LoggerFactory.getLogger(FascicoliBusiness.class);
	
	public static List<MaterialiSpedizione> extractArretratiDaSpedire_(Integer idOpzione, int idRapporto)
			throws BusinessException, EmptyResultException {
		List<MaterialiSpedizione> msList = null;
		Session ses = SessionFactory.getSession();
		try {
			msList = new MaterialiSpedizioneDao().findPendingByOpzione(ses, idOpzione);
		} catch (HibernateException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (msList == null) throw new EmptyResultException("Nessun arretrato da estrarre");
		if (msList.size() == 0) throw new EmptyResultException("Nessun arretrato da estrarre");
		return msList;
	}

	public static void updateMaterialiSpedizione(List<MaterialiSpedizione> msList, Date dataInvio, int idRapporto, String idUtente) 
		throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		MaterialiSpedizioneDao msDao = new MaterialiSpedizioneDao();
		try {
			for (MaterialiSpedizione ms:msList) {
				ms.setDataInvio(dataInvio);
				msDao.update(ses, ms);
			}
			trn.commit();
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Fine scrittura su DB dell'invio arretrati");
		} catch (HibernateException e) {
			trn.rollback();
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	/** Raggruppa per anagrafica destinatario e mette in un oggetto OrdineBean*/
	public static List<OrderBean> createOrdiniLogistica(Session ses,
			List<MaterialiSpedizione> msList,
			Date dataInserimento, int idRapporto)
			throws BusinessException {
		//Scorre le evasioni, le raggruppa per anagrafica e committente e CREA SU DB gli ordini
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Raggruppamento materiali per anagrafica in corso...");
		Map<String, OrderBean> anagOrderMap = new HashMap<String, OrderBean>();
		for (MaterialiSpedizione ms:msList) {
			String committenteSap = ms.getMateriale().getIdTipoAnagraficaSap();
			Anagrafiche anag = null;
			if (ms.getIdAnagrafica() != null) {
					anag = GenericDao.findById(ses, Anagrafiche.class, ms.getIdAnagrafica());
			}
			if (anag != null) {
				String key = ms.getIdAnagrafica()+"-"+committenteSap;
				OrderBean ob = anagOrderMap.get(key);
				if (ob == null) {
					//nella mappa non c'è ancora un OrdineBean abbinato a questa istanza quindi lo crea
					String orderPrefix = ConfigUtil.getOrderPrefix(ses);
					ob = new OrderBean(ses, anag, committenteSap, dataInserimento, orderPrefix);
					anagOrderMap.put(key, ob);
				}
				OrderRowBean orb = new OrderRowBean(ms, committenteSap);
				ob.getRowList().add(orb);
			} else {
				VisualLogger.get().addHtmlInfoLine(idRapporto, "ERRORE: Il MaterialeSpedizione "+ms.getId()+" non e' abbinato ad alcuna anagrafica");
			}
		}
		VisualLogger.get().addHtmlInfoLine(idRapporto, msList.size()+" MaterialiSpedizioni raggruppati in "+
				anagOrderMap.keySet().size()+" ordini");
		
		//Da mappa crea la lista ordini e
		//SCRIVE SU DB le modifiche alle evasioni (cioè il loro abbinamento all'ordine)
		List<OrderBean> ordList = new ArrayList<OrderBean>();
		for (String key:anagOrderMap.keySet()) {
			OrderBean bean = anagOrderMap.get(key);
			ordList.add(bean);
			int count = 0;
			for (OrderRowBean orb:bean.getRowList()) {
				count += orb.getSpedizione().getCopie();
				orb.getSpedizione().setDataOrdine(bean.getOrdineLogistica().getDataInserimento());
				orb.getSpedizione().setOrdineLogistica(bean.getOrdineLogistica());
				GenericDao.updateGeneric(ses, orb.getSpedizione().getId(), orb.getSpedizione());
			}
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Ordine:<b>"+bean.getOrdineLogistica().getNumeroOrdine()+"</b>"+
					" destinatario:"+bean.getAnagrafica().getUid()+
					" materiali:"+bean.getRowList().size()+
					" totale:"+count);
		}

		return ordList;
	}
	
	
	public static List<FascicoliGroupBean> groupFascicoliByAbbonamento(List<MaterialiSpedizione> msList, int idRapporto)
			throws BusinessException {
		Map<Integer, FascicoliGroupBean> fgMap = new HashMap<Integer, FascicoliGroupBean>();
		Session ses = SessionFactory.getSession();
		try {
			for (MaterialiSpedizione ms:msList) {
				if (ms.getIdAbbonamento() != null) {
					FascicoliGroupBean fg = fgMap.get(ms.getIdAbbonamento());
					if (fg == null) {
						//nella mappa non c'è ancora un fg abbinato a questa istanza quindi crea fg
						Abbonamenti abb = GenericDao.findById(ses, Abbonamenti.class, ms.getIdAbbonamento());
						Anagrafiche ana = GenericDao.findById(ses, Anagrafiche.class, ms.getIdAnagrafica());
						fg = new FascicoliGroupBean(abb, ana);
						fgMap.put(ms.getIdAbbonamento(), fg);
					}
					fg.getMaterialiSpedizioneList().add(ms);
				} else {
					VisualLogger.get().addHtmlInfoLine(idRapporto, "ERRORE: L'arretrato "+
							ms.getMateriale().getCodiceMeccanografico()+
							" non e' abbinato a un abbonamento");
				}
			}
		} catch (HibernateException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		//Da mappa a lista
		List<FascicoliGroupBean> fgList = new ArrayList<FascicoliGroupBean>();
		for (Integer id:fgMap.keySet()) {
			List<FascicoliGroupBean> fgSplit = splitIfEtichetteSeparate(fgMap.get(id));
			fgList.addAll(fgSplit);
		}
		return fgList;
	}

	private static List<FascicoliGroupBean> splitIfEtichetteSeparate(FascicoliGroupBean fg) {
		List<FascicoliGroupBean> fgList = new ArrayList<FascicoliGroupBean>();
		List<MaterialiSpedizione> msList = new ArrayList<MaterialiSpedizione>();
		msList.addAll(fg.getMaterialiSpedizioneList());
		for (MaterialiSpedizione ms:msList) {
			if (ms.getMateriale().getInAttesa()) {
				//Etichetta separata > crea nuova EvasioniGroupBean
				FascicoliGroupBean fgb = new FascicoliGroupBean(fg.getAbbonamento(), fg.getAnagrafica());
				List<MaterialiSpedizione> singleMs = new ArrayList<MaterialiSpedizione>();
				singleMs.add(ms);
				fgb.setMaterialiSpedizioneList(singleMs);
				fgList.add(fgb);
				//Rimuove il fascicolo dal gruppo iniziale
				fg.getMaterialiSpedizioneList().remove(ms);
			}
		}
		//Se restano fascicoli nel fg iniziale allora lo aggiunge alla lista
		if (fg.getMaterialiSpedizioneList().size() > 0) {
			fgList.add(fg);
		}
		return fgList;
	}

}
