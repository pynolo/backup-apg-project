package it.giunti.apgautomation.server.business;

import it.giunti.apg.server.ConfigUtil;
import it.giunti.apg.server.VisualLogger;
import it.giunti.apg.server.business.FascicoliGroupBean;
import it.giunti.apg.server.persistence.EvasioniFascicoliDao;
import it.giunti.apg.server.persistence.GenericDao;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Articoli;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IEvasioni;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class FascicoliBusiness {

	//private static final Logger LOG = LoggerFactory.getLogger(FascicoliBusiness.class);
	
	public static List<EvasioniFascicoli> extractArretratiDaSpedire(Integer idPeriodico, int idRapporto)
			throws BusinessException, EmptyResultException {
		List<EvasioniFascicoli> efList = null;
		Session ses = SessionFactory.getSession();
		try {
			efList = new EvasioniFascicoliDao().findPending(ses, idPeriodico);
		} catch (HibernateException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (efList == null) throw new EmptyResultException("Nessun arretrato da estrarre");
		if (efList.size() == 0) throw new EmptyResultException("Nessun arretrato da estrarre");
		return efList;
	}

	public static void updateEvasioniFascicoli(List<EvasioniFascicoli> efList, Date dataInvio, int idRapporto, String idUtente) 
		throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		EvasioniFascicoliDao efDao = new EvasioniFascicoliDao();
		try {
			for (EvasioniFascicoli ef:efList) {
				ef.setDataInvio(dataInvio);
				efDao.update(ses, ef);
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
			List<? extends IEvasioni> evaList,
			Date dataInserimento, int idRapporto)
			throws BusinessException {
		//Scorre le evasioni, le raggruppa per anagrafica e committente e CREA SU DB gli ordini
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Raggruppamento materiali per anagrafica in corso...");
		Map<String, OrderBean> anagOrderMap = new HashMap<String, OrderBean>();
		for (IEvasioni eva:evaList) {
			String committente = extractCommittenteEvasione(eva);
			if (eva.getIdAnagrafica() != null) {
				String key = eva.getIdAnagrafica()+"-"+committente;
				OrderBean ob = anagOrderMap.get(key);
				if (ob == null) {
					//nella mappa non c'è ancora un OrdineBean abbinato a questa istanza quindi lo crea
					Anagrafiche anag = GenericDao.findById(ses, Anagrafiche.class, eva.getIdAnagrafica());
					String orderPrefix = ConfigUtil.getOrderPrefix(ses);
					ob = new OrderBean(ses, anag, committente, dataInserimento, orderPrefix);
					anagOrderMap.put(key, ob);
				}
				OrderRowBean orb = new OrderRowBean(eva, committente);
				ob.getRowList().add(orb);
			} else {
				VisualLogger.get().addHtmlInfoLine(idRapporto, "ERRORE: Il materiale "+eva.getId()+" ("+
						eva.getClass().getSimpleName()+") non e' abbinato ad alcuna anagrafica");
			}
		}
		VisualLogger.get().addHtmlInfoLine(idRapporto, evaList.size()+" materiali raggruppati in "+
				anagOrderMap.keySet().size()+" ordini");
		
		//Da mappa crea la lista ordini e
		//SCRIVE SU DB le modifiche alle evasioni (cioè il loro abbinamento all'ordine)
		List<OrderBean> ordList = new ArrayList<OrderBean>();
		for (String key:anagOrderMap.keySet()) {
			OrderBean bean = anagOrderMap.get(key);
			ordList.add(bean);
			int count = 0;
			for (OrderRowBean orb:bean.getRowList()) {
				count += orb.getEvasione().getCopie();
				orb.getEvasione().setDataOrdine(bean.getOrdineLogistica().getDataInserimento());
				orb.getEvasione().setOrdiniLogistica(bean.getOrdineLogistica());
				GenericDao.updateGeneric(ses, orb.getEvasione().getId(), orb.getEvasione());
			}
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Ordine:<b>"+bean.getOrdineLogistica().getNumeroOrdine()+"</b>"+
					" destinatario:"+bean.getAnagrafica().getUid()+
					" articoli:"+bean.getRowList().size()+
					" totale:"+count);
		}

		return ordList;
	}
	
	
	public static List<FascicoliGroupBean> groupFascicoliByIstanza(List<EvasioniFascicoli> efList, int idRapporto)
			throws BusinessException {
		Map<Integer, FascicoliGroupBean> fgMap = new HashMap<Integer, FascicoliGroupBean>();
		Session ses = SessionFactory.getSession();
		try {
			for (EvasioniFascicoli ef:efList) {
				if (ef.getIdIstanzaAbbonamento() != null) {
					FascicoliGroupBean fg = fgMap.get(ef.getIdIstanzaAbbonamento());
					if (fg == null) {
						//nella mappa non c'è ancora un fg abbinato a questa istanza quindi crea fg
						IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, ef.getIdIstanzaAbbonamento());
						fg = new FascicoliGroupBean(ia);
						fgMap.put(ef.getIdIstanzaAbbonamento(), fg);
					}
					fg.getEvasioniFacicoliList().add(ef);
				} else {
					Abbonamenti abb = GenericDao.findById(ses, Abbonamenti.class, ef.getIdAbbonamento());
					VisualLogger.get().addHtmlInfoLine(idRapporto, "ERRORE: L'arretrato "+ef.getFascicolo().getTitoloNumero()+
							ef.getFascicolo().getPeriodico().getNome()+" e' abbinato a "+
							abb.getCodiceAbbonamento()+" ma non ad una istanza");
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
		List<EvasioniFascicoli> efList = new ArrayList<EvasioniFascicoli>();
		efList.addAll(fg.getEvasioniFacicoliList());
		for (EvasioniFascicoli ef:efList) {
			if (ef.getFascicolo().getInAttesa()) {
				//Etichetta separata > crea nuova EvasioniGroupBean
				IstanzeAbbonamenti ia = fg.getIstanzaAbbonamento();
				FascicoliGroupBean fgb = new FascicoliGroupBean(ia);
				List<EvasioniFascicoli> singleEf = new ArrayList<EvasioniFascicoli>();
				singleEf.add(ef);
				fgb.setEvasioniFascicoliList(singleEf);
				fgList.add(fgb);
				//Rimuove il fascicolo dal gruppo iniziale
				fg.getEvasioniFacicoliList().remove(ef);
			}
		}
		//Se restano fascicoli nel fg iniziale allora lo aggiunge alla lista
		if (fg.getEvasioniFacicoliList().size() > 0) {
			fgList.add(fg);
		}
		return fgList;
	}
	
	//public static Map<String, Integer> extractCmCopieFromOrdini(List<OrderBean> ordList) {
	//	Map<String, Integer> countMap = new HashMap<String, Integer>();
	//	//Somma arretrati per fascicolo
	//	for (OrderBean bean:ordList) {
	//		for (OrderRowBean orb:bean.getRowList()) {
	//			IEvasioni eva = orb.getEvasione();
	//			Integer count = null;
	//			String cm = null;
	//			if (eva instanceof EvasioniFascicoli) {
	//				Fascicoli fas = ((EvasioniFascicoli) eva).getFascicolo();
	//				cm = fas.getCodiceMeccanografico();
	//			}
	//			if (eva instanceof EvasioniArticoli) {
	//				Articoli dono = ((EvasioniArticoli) eva).getDono();
	//				cm = dono.getCodiceMeccanografico();
	//			}
	//			count = countMap.get(cm);
	//			if (count == null) {//non è stato ancora inserito
	//				count = 0;
	//			}
	//			count += eva.getCopie();
	//			countMap.put(cm, count);
	//		}
	//	}
	//	return countMap;
	//}
	
//	public static String extractCommittenteEvasione(IEvasioni eva) {
//		String idTipoArticolo = null;
//		String idSocieta = null;
//		if (eva instanceof EvasioniFascicoli) {
//			Fascicoli fas = ((EvasioniFascicoli) eva).getFascicolo();
//			idTipoArticolo = fas.getIdTipoArticolo();
//			idSocieta = fas.getPeriodico().getIdSocieta();
//		}
//		if (eva instanceof EvasioniArticoli) {
//			Articoli dono = ((EvasioniArticoli) eva).getArticolo();
//			idTipoArticolo = dono.getIdTipoArticolo();
//			idSocieta = dono.getIdSocieta();
//		}
//		String committente = null;
//		if (idTipoArticolo.equals(AppConstants.ARTICOLO_FASCICOLO)) {
//			if (AppConstants.SOCIETA_GIUNTI_EDITORE.equals(idSocieta))
//				committente = SapConstants.TIPI_ANAG_RIVISTE_GIUNTI;
//			if (AppConstants.SOCIETA_GIUNTI_OS.equals(idSocieta))
//				committente = SapConstants.TIPI_ANAG_RIVISTE_GIUNTI;//TODO will be treated separately
//			if (AppConstants.SOCIETA_GIUNTI_SCUOLA.equals(idSocieta))
//				committente = SapConstants.TIPI_ANAG_RIVISTE_SCUOLA;
//		} else {
//			committente = SapConstants.TIPI_ANAG_LIBRI;
//		}
//		return committente;
//	}
	
	public static String extractCommittenteEvasione(IEvasioni eva) {
		String idCommittente = null;
		if (eva instanceof EvasioniFascicoli) {
			Fascicoli fas = ((EvasioniFascicoli) eva).getFascicolo();
			idCommittente = fas.getIdTipoAnagraficaSap();
		}
		if (eva instanceof EvasioniArticoli) {
			Articoli dono = ((EvasioniArticoli) eva).getArticolo();
			idCommittente = dono.getIdTipoAnagraficaSap();
		}
		return idCommittente;
	}
}
