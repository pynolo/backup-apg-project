package it.giunti.apg.core.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.EvasioniArticoli6;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.MaterialiListini;
import it.giunti.apg.shared.model.MaterialiOpzioni;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;

public class EvasioniArticoli6Dao implements BaseDao<EvasioniArticoli6> {

	@Override
	public void update(Session ses, EvasioniArticoli6 instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, EvasioniArticoli6 transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, EvasioniArticoli6 instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniArticoli6> findByIstanza(Session ses, Integer idIstanza)
			throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from EvasioniArticoli6 ed");
		qf.addWhere("ed.idIstanzaAbbonamento = :p1");
		qf.addParam("p1", idIstanza);
		qf.addOrder("ed.dataCreazione asc");
		Query q = qf.getQuery();
		List<EvasioniArticoli6> dList = (List<EvasioniArticoli6>) q.list();
		return dList;
	}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniArticoli6> findPrenotatiByAbbonamento(Session ses, Integer idAbbonamento)
			throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from EvasioniArticoli6 ed");
		qf.addWhere("ed.idAbbonamento = :p1");
		qf.addParam("p1", idAbbonamento);
		qf.addWhere("ed.prenotazioneIstanzaFutura = :b1");
		qf.addParam("b1", true);
		qf.addOrder("ed.dataCreazione asc");
		Query q = qf.getQuery();
		List<EvasioniArticoli6> dList = (List<EvasioniArticoli6>) q.list();
		return dList;
	}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniArticoli6> findByAnagrafica(Session ses, Integer idAnagrafica)
			throws HibernateException {
		String hql = "from EvasioniArticoli6 ea where "+
			"ea.idIstanzaAbbonamento is null and "+
			"ea.idAnagrafica = :id1 "+
			"order by ea.dataCreazione asc";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idAnagrafica, IntegerType.INSTANCE);
		List<EvasioniArticoli6> dList = (List<EvasioniArticoli6>) q.list();
		return dList;
	}
	
	//@SuppressWarnings("unchecked")
	//public List<EvasioniArticoli> findPendingByIstanze(Session ses, Date today)
	//		throws HibernateException {
	//	String qString = "select ea from EvasioniArticoli ea, IstanzeAbbonamenti ia where " +
	//			"(ea.dataLimite is null or ea.dataLimite > :dt1) and " +//Non deve essere stato superato il limite temporale
	//			"ea.idIstanzaAbbonamento = ia.id and " +
	//			"ea.idIstanzaAbbonamento is not null and " + //Solo ordini agganciati ad istanze
	//			"ea.prenotazioneIstanzaFutura = :b1 and " + //false: NON prenotazione
	//			"ea.dataInvio is null and ea.dataOrdine is null and " +//Né ordinato né spedito
	//			"ea.articolo.inAttesa = :b6 and " + //false: NON in attesa
	//			"ea.eliminato = :b7 and " + //false
	//			"(ia.pagato = :b3 or ia.fatturaDifferita = :b4 or ia.listino.fatturaDifferita = :b5) " +//Pagato
	//			"order by ia.copie desc, ea.id asc ";
	//	Query q = ses.createQuery(qString);
	//	q.setParameter("dt1", today, DateType.INSTANCE);
	//	q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
	//	q.setParameter("b3", Boolean.TRUE, BooleanType.INSTANCE);
	//	q.setParameter("b4", Boolean.TRUE, BooleanType.INSTANCE);
	//	q.setParameter("b5", Boolean.TRUE, BooleanType.INSTANCE);
	//	q.setParameter("b6", Boolean.FALSE, BooleanType.INSTANCE);//non in attesa
	//	q.setParameter("b7", Boolean.FALSE, BooleanType.INSTANCE);//non eliminato
	//	List<EvasioniArticoli> edList = (List<EvasioniArticoli>) q.list();
	//	return edList;
	//}

	
	@SuppressWarnings("unchecked")
	public List<EvasioniArticoli6> findPendingByIstanzeManual(Session ses, Date today)
			throws HibernateException {
		String qString = "select ea from EvasioniArticoli6 ea, IstanzeAbbonamenti ia where " +
					"ea.idIstanzaAbbonamento = ia.id and " +
				"ea.idArticoloListino is null and "+
				"ea.idArticoloOpzione is null and "+
				"(ea.dataLimite is null or ea.dataLimite > :dt1) and " +//Non deve essere stato superato il limite temporale
				"ea.idIstanzaAbbonamento is not null and " + //Solo ordini agganciati ad istanze
				"ea.prenotazioneIstanzaFutura = :b1 and " + //false: NON prenotazione
				"ea.dataInvio is null and ea.dataOrdine is null and " +//Né ordinato né spedito
				"ea.articolo.inAttesa = :b6 and " + //false: NON in attesa
				"ea.dataAnnullamento is null and " + //false
				"(ia.pagato = :b3 or ia.fatturaDifferita = :b4 or ia.listino.fatturaDifferita = :b5) " +//Pagato
				"order by ia.copie desc, ea.id asc ";
		Query q = ses.createQuery(qString);
		q.setParameter("dt1", today, DateType.INSTANCE);
		q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
		q.setParameter("b3", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b4", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b5", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b6", Boolean.FALSE, BooleanType.INSTANCE);//non in attesa
		List<EvasioniArticoli6> edList = (List<EvasioniArticoli6>) q.list();
		return edList;
	}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniArticoli6> findPendingByIstanzeListini(Session ses, Date today)
			throws HibernateException {
		String qString = "select ea from EvasioniArticoli6 ea, IstanzeAbbonamenti ia, "+
					"ArticoliListini al where " +
					"ea.idIstanzaAbbonamento = ia.id and " +
					"ea.idArticoloListino = al.id and "+
				"al.dataEstrazione is not null and "+ //L'articoloListino deve essere stato estratto
				"ea.idArticoloListino is not null and "+
				"ea.idArticoloOpzione is null and "+
				//"(ea.dataLimite is null or ea.dataLimite > :dt1) and " +//Non deve essere stato superato il limite temporale
				"ea.idIstanzaAbbonamento is not null and " + //Solo ordini agganciati ad istanze
				"ea.prenotazioneIstanzaFutura = :b1 and " + //false: NON prenotazione
				"ea.dataInvio is null and ea.dataOrdine is null and " +//Né ordinato né spedito
				"ea.articolo.inAttesa = :b6 and " + //false: NON in attesa
				"ea.dataAnnullamento is null and " + //false
				"(ia.pagato = :b3 or ia.fatturaDifferita = :b4 or ia.listino.fatturaDifferita = :b5) " +//Pagato
				"order by ia.copie desc, ea.id asc ";
		Query q = ses.createQuery(qString);
		//q.setParameter("dt1", today, DateType.INSTANCE);
		q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
		q.setParameter("b3", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b4", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b5", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b6", Boolean.FALSE, BooleanType.INSTANCE);//non in attesa
		List<EvasioniArticoli6> edList = (List<EvasioniArticoli6>) q.list();
		return edList;
	}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniArticoli6> findPendingByIstanzeOpzioni(Session ses, Date today)
			throws HibernateException {
		String qString = "select ea from EvasioniArticoli6 ea, IstanzeAbbonamenti ia, "+
					"ArticoliOpzioni ao where " +
					"ea.idIstanzaAbbonamento = ia.id and " +
					"ea.idArticoloOpzione = ao.id and "+
				"ao.dataEstrazione is not null and "+ //L'articoloOpzione deve essere stato estratto
				"ea.idArticoloListino is null and "+
				"ea.idArticoloOpzione is not null and "+
				//"(ea.dataLimite is null or ea.dataLimite > :dt1) and " +//Non deve essere stato superato il limite temporale
				"ea.idIstanzaAbbonamento is not null and " + //Solo ordini agganciati ad istanze
				"ea.prenotazioneIstanzaFutura = :b1 and " + //false: NON prenotazione
				"ea.dataInvio is null and ea.dataOrdine is null and " +//Né ordinato né spedito
				"ea.articolo.inAttesa = :b6 and " + //false: NON in attesa
				"ea.dataAnnullamento is null and " + //false
				"(ia.pagato = :b3 or ia.fatturaDifferita = :b4 or ia.listino.fatturaDifferita = :b5) " +//Pagato
				"order by ia.copie desc, ea.id asc ";
		Query q = ses.createQuery(qString);
		//q.setParameter("dt1", today, DateType.INSTANCE);
		q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
		q.setParameter("b3", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b4", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b5", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b6", Boolean.FALSE, BooleanType.INSTANCE);//non in attesa
		List<EvasioniArticoli6> edList = (List<EvasioniArticoli6>) q.list();
		return edList;
	}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniArticoli6> findPendingByAnagrafiche(Session ses, Date today)
			throws HibernateException {
		String qString = "select ea from EvasioniArticoli6 ea where " +
				//"(ea.dataLimite is null or ea.dataLimite > :dt1) and " +//Non deve essere stato superato il limite temporale
				"ea.idIstanzaAbbonamento is null and " + //Solo ordini NON agganciati ad istanze
				"ea.idAnagrafica is not null and " + //Solo ordini agganciati ad anagrafiche
				"ea.prenotazioneIstanzaFutura = :b1 and " + //false: NON prenotazione
				"ea.dataInvio is null and ea.dataOrdine is null and " +//Né ordinato né spedito
				"ea.articolo.inAttesa = :b6 and " + //false: NON in attesa
				"ea.dataAnnullamento is null " + //false
				"order by ea.id asc ";
		Query q = ses.createQuery(qString);
		//q.setParameter("dt1", today, DateType.INSTANCE);
		q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
		q.setParameter("b6", Boolean.FALSE, BooleanType.INSTANCE);//non in attesa
		List<EvasioniArticoli6> edList = (List<EvasioniArticoli6>) q.list();
		return edList;
	}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniArticoli6> findPendingByArticoloListino(Session ses,
			Integer idArticoloListino, Date date, int offset, int pageSize)
			throws HibernateException {
		String hql = "select ea from EvasioniArticoli6 ea, IstanzeAbbonamenti ia, ArticoliListini al where " +
				 "ea.idIstanzaAbbonamento = ia.id and " + //join
				 "al.id = ea.idArticoloListino and " +
				"ea.idArticoloListino = :id1 and " +
				"ea.dataInvio is null and " +
				"ea.dataOrdine is null and " +
				"ea.dataAnnullamento is null and " + //false
				"ea.prenotazioneIstanzaFutura = :b2 and " + //false
				"al.dataEstrazione is not null and "+
					"(ia.pagato = :b11 or "+ //true
					"ia.fatturaDifferita = :b12) and "+ //true
				//"(ea.dataLimite is null or ea.dataLimite > :dt1) and " +//Non oltre il limite temporale
				"ea.articolo.inAttesa = :b3 " + //false: NON in attesa
				"order by ea.id asc ";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idArticoloListino, IntegerType.INSTANCE);
		q.setParameter("b2", Boolean.FALSE, BooleanType.INSTANCE);
		q.setParameter("b11", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b12", Boolean.TRUE, BooleanType.INSTANCE);
		//q.setParameter("dt1", date, DateType.INSTANCE);
		q.setParameter("b3", Boolean.FALSE, BooleanType.INSTANCE);//non in attesa
		List<EvasioniArticoli6> eaList = (List<EvasioniArticoli6>) q.list();
		return eaList;
	}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniArticoli6> findPendingByArticoloOpzione(Session ses,
			Integer idArticoloOpzione, int offset, int pageSize)
			throws HibernateException {
		String hql = "select ea from EvasioniArticoli6 ea, IstanzeAbbonamenti ia, ArticoliOpzioni ao where " +
				 "ea.idIstanzaAbbonamento = ia.id and " +//join
				 "ao.id = ea.idArticoloOpzione and "+
				"ea.idArticoloOpzione = :id1 and " + 
				"ea.prenotazioneIstanzaFutura = :b2 and " + //false: NON prenotazione
				"ea.dataInvio is null and ea.dataOrdine is null and " +//Né ordinato né spedito
				"ea.articolo.inAttesa = :b3 and " + //false: NON in attesa
				"ea.dataAnnullamento is null and " + //false
				"ao.dataEstrazione is not null and "+
					"(ia.pagato = :b11 or "+ //true
					"ia.fatturaDifferita = :b12 or "+ //true
					"ia.listino.invioSenzaPagamento = :b13) "+ //true
				"order by ea.id asc ";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idArticoloOpzione, IntegerType.INSTANCE);
		q.setParameter("b2", Boolean.FALSE, BooleanType.INSTANCE);
		q.setParameter("b3", Boolean.FALSE, BooleanType.INSTANCE);//non in attesa
		q.setParameter("b11", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b12", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b13", Boolean.TRUE, BooleanType.INSTANCE);
		List<EvasioniArticoli6> eaList = (List<EvasioniArticoli6>) q.list();
		return eaList;
	}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniArticoli6> findByNumeroOrdine(Session ses, String numeroOrdine)
			throws HibernateException {
		String hql = "from EvasioniArticoli6 ea where "+
			"ea.ordiniLogistica.numeroOrdine = :s1 " +
			"order by ea.id asc";
		Query q = ses.createQuery(hql);
		q.setParameter("s1", numeroOrdine, StringType.INSTANCE);
		List<EvasioniArticoli6> edList = (List<EvasioniArticoli6>) q.list();
		return edList;
	}
	
	public EvasioniArticoli6 createEmptyEvasioniArticoliFromIstanza(Session ses,
			IstanzeAbbonamenti ia, String idTipoDestinatario, String idUtente)
			throws HibernateException {
		EvasioniArticoli6 newEa = new EvasioniArticoli6();
		if (AppConstants.DEST_BENEFICIARIO.equals(idTipoDestinatario))
			newEa.setIdAnagrafica(ia.getAbbonato().getId());
		if (AppConstants.DEST_PAGANTE.equals(idTipoDestinatario)) {
			if (ia.getPagante() != null) {
				newEa.setIdAnagrafica(ia.getPagante().getId());
			} else {
				throw new HibernateException("Il destinatario del articolo 'pagante' non e' definito");
			}
		}
		if (AppConstants.DEST_PROMOTORE.equals(idTipoDestinatario)) {
			if (ia.getPromotore() != null) {
				newEa.setIdAnagrafica(ia.getPromotore().getId());
			} else {
				throw new HibernateException("Il destinatario del articolo 'promotore' non e' definito");
			}
		}
		newEa.setArticolo6(null);//Sarà assegnato dopo
		newEa.setCopie(ia.getCopie());
		newEa.setDataCreazione(DateUtil.now());
		newEa.setDataLimite(null);
		newEa.setDataOrdine(null);
		newEa.setDataModifica(DateUtil.now());
		newEa.setDataAnnullamento(null);
		newEa.setIdIstanzaAbbonamento(ia.getId());
		newEa.setIdAbbonamento(ia.getAbbonamento().getId());
		newEa.setPrenotazioneIstanzaFutura(false);
		newEa.setIdTipoDestinatario(idTipoDestinatario);
		newEa.setNote("");
		newEa.setIdUtente(idUtente);
		return newEa;
	}
	
	public EvasioniArticoli6 createEvasioniArticoliFromListino(Session ses,
			MaterialiListini al, IstanzeAbbonamenti ia, String idUtente)
			throws HibernateException {
		EvasioniArticoli6 newEa = new EvasioniArticoli6();
		if (AppConstants.DEST_BENEFICIARIO.equals(al.getIdTipoDestinatario()))
			newEa.setIdAnagrafica(ia.getAbbonato().getId());
		if (AppConstants.DEST_PAGANTE.equals(al.getIdTipoDestinatario())) {
			if (ia.getPagante() != null) {
				newEa.setIdAnagrafica(ia.getPagante().getId());
			} else {
				throw new HibernateException("Il destinatario del articolo 'pagante' non e' definito");
			}
		}
		if (AppConstants.DEST_PROMOTORE.equals(al.getIdTipoDestinatario())) {
			if (ia.getPromotore() != null) {
				newEa.setIdAnagrafica(ia.getPromotore().getId());
			} else {
				throw new HibernateException("Il destinatario del articolo 'promotore' non e' definito");
			}
		}
		newEa.setIdArticoloListino(al.getId());
		newEa.setIdArticoloOpzione(null);
		newEa.setArticolo6(al.getArticolo6());
		newEa.setCopie(ia.getCopie());
		newEa.setDataCreazione(DateUtil.now());
		Date dataLimite = new MaterialiListiniDao().buildDataLimite(al, ia.getFascicoloInizio6().getDataInizio());
		newEa.setDataLimite(dataLimite);
		newEa.setDataOrdine(null);
		newEa.setDataModifica(DateUtil.now());
		newEa.setDataAnnullamento(null);
		newEa.setIdIstanzaAbbonamento(ia.getId());
		newEa.setIdAbbonamento(ia.getAbbonamento().getId());
		newEa.setPrenotazioneIstanzaFutura(false);
		newEa.setIdTipoDestinatario(al.getIdTipoDestinatario());
		newEa.setNote("");
		newEa.setIdUtente(idUtente);
		return newEa;
	}
	
	public EvasioniArticoli6 createEvasioniArticoliFromOpzione(Session ses, 
			MaterialiOpzioni ao, IstanzeAbbonamenti ia, String idUtente)
			throws HibernateException {
		EvasioniArticoli6 newEa = new EvasioniArticoli6();
		newEa.setIdArticoloListino(null);
		newEa.setIdArticoloOpzione(ao.getId());
		newEa.setArticolo6(ao.getArticolo6());
		newEa.setCopie(ia.getCopie());
		newEa.setDataCreazione(DateUtil.now());
		newEa.setDataLimite(null);
		newEa.setDataOrdine(null);
		newEa.setDataModifica(DateUtil.now());
		newEa.setDataAnnullamento(null);
		newEa.setIdIstanzaAbbonamento(ia.getId());
		newEa.setIdAbbonamento(ia.getAbbonamento().getId());
		newEa.setPrenotazioneIstanzaFutura(false);
		newEa.setIdAnagrafica(ia.getAbbonato().getId());
		newEa.setIdTipoDestinatario(AppConstants.DEST_BENEFICIARIO);
		newEa.setNote("");
		newEa.setIdUtente(idUtente);
		return newEa;
	}
	
	public EvasioniArticoli6 createEvasioniArticoliFromAnagrafica(Session ses, Integer idAnagrafica,
			Integer copie, String idTipoDestinatario, String idUtente) throws HibernateException {
		if (idTipoDestinatario == null) idTipoDestinatario = AppConstants.DEST_BENEFICIARIO;
		EvasioniArticoli6 ed = new EvasioniArticoli6();
		ed.setDataCreazione(DateUtil.now());
		ed.setIdAbbonamento(null);
		ed.setIdIstanzaAbbonamento(null);
		ed.setCopie(copie);
		ed.setIdTipoDestinatario(idTipoDestinatario);
		ed.setIdAnagrafica(idAnagrafica);
		ed.setNote("");
		ed.setPrenotazioneIstanzaFutura(false);
		ed.setIdUtente(idUtente);
		return ed;
	}
	
	public Integer reattachEvasioniArticoliToInstanza(Session ses,
			IstanzeAbbonamenti persistedIa, String idUtente) throws HibernateException {
		//Articoli prenotati su ABBONAMENTO
		List<EvasioniArticoli6> prenotatiList = findPrenotatiByAbbonamento(ses, persistedIa.getAbbonamento().getId());
		//Articoli presenti su ISTANZA
		List<EvasioniArticoli6> esistentiList = findByIstanza(ses, persistedIa.getId());
		
		//Carica eventuali ArticoliListini da includere
		List<MaterialiListini> alList = new MaterialiListiniDao()
				.findByListino(ses, persistedIa.getListino().getId());
		//Carica eventuali ArticoliOpzioni da includere
		List<MaterialiOpzioni> aoList = new ArrayList<MaterialiOpzioni>();
		if (persistedIa.getOpzioniIstanzeAbbonamentiSet() != null) {
			for (OpzioniIstanzeAbbonamenti oia:persistedIa.getOpzioniIstanzeAbbonamentiSet()) {
				List<MaterialiOpzioni> list = new MaterialiOpzioniDao().findByOpzione(ses, oia.getOpzione().getId());
				if (list != null) aoList.addAll(list);
			}
		}
		
		List<EvasioniArticoli6> eaList = new ArrayList<EvasioniArticoli6>();
		//Aggiunta dei prenotati alla lista finale
		for (EvasioniArticoli6 prenotato:prenotatiList) {
			prenotato.setIdAbbonamento(persistedIa.getAbbonamento().getId());
			prenotato.setIdIstanzaAbbonamento(persistedIa.getId());
			prenotato.setPrenotazioneIstanzaFutura(false);
			update(ses, prenotato);
		}
		//Aggiunta da ArticoliListini (a meno di ESISTENTI)
		for (MaterialiListini al:alList) {
			boolean exists = false;
			for (EvasioniArticoli6 ea:esistentiList) {
				if (ea.getArticolo6().equals(al.getArticolo6())) exists = true;
			}
			if (!exists) {
				EvasioniArticoli6 newEa = createEvasioniArticoliFromListino(ses, al, persistedIa, idUtente);
				eaList.add(newEa);
			}
		}
		//Aggiunta da ArticoliOpzioni (a meno di ESISTENTI)
		for (MaterialiOpzioni ao:aoList) {
			boolean exists = false;
			for (EvasioniArticoli6 ea:esistentiList) {
				if (ea.getArticolo6().equals(ao.getArticolo6())) exists = true;
			}
			if (!exists) {
				EvasioniArticoli6 newEa = createEvasioniArticoliFromOpzione(ses, ao, persistedIa, idUtente);
				eaList.add(newEa);
			}
		}
		
		//Save or update articoli
		for (EvasioniArticoli6 ea:eaList) {
			if (ea.getId() != null) {
				update(ses, ea);
			} else {
				save(ses, ea);
			}
		}
		return eaList.size();
	}
	
	@SuppressWarnings("unchecked")
	public EvasioniArticoli6 checkArticoloIstanza(Session ses, Integer idIstanza, Integer idArticolo)
			throws HibernateException {
		String qs = "from EvasioniArticoli6 ea where " +
				"ea.idIstanzaAbbonamento = :p1 and " +
				"ea.articolo.id = :p2";
		Query q = ses.createQuery(qs);
		q.setInteger("p1", idIstanza);
		q.setInteger("p2", idArticolo);
		List<EvasioniArticoli6> eaList = (List<EvasioniArticoli6>) q.list();
		if (eaList == null) return null;
		if (eaList.size() > 0) {
			//Ritorna un'evasione, specialmente se è stata spedita
			EvasioniArticoli6 result = null;
			for (EvasioniArticoli6 ea:eaList) {
				if (result == null) result = ea;
				if (ea.getDataInvio() != null) return ea;
			}
			return result;
		} else {
			return null;
		}
	}
	
	
	//metodi con SQL
	
	
	public void sqlInsert(Session ses, EvasioniArticoli6 ea) throws HibernateException {
		String sql = "insert into evasioni_articoli(" +
					"id_articolo, copie, data_creazione, " +
					"data_invio, data_modifica, data_ordine, " +
					"data_annullamento, id_abbonamento, id_anagrafica, " +
					"id_istanza_abbonamento, id_tipo_destinatario, note, "+
					"id_ordine_logistica, prenotazione_istanza_futura, id_utente" +
				") values(" +
					":id1, :i2, :dt3, " +
					":dt4, :dt5, :dt6, " +
					":dt7, :id8, :id9, " +
					":id10, :id11, :s12, " +
					":id13, :b14, :s15" +
				")";
		Query q = ses.createSQLQuery(sql);
		q.setParameter("id1", ea.getArticolo6().getId(), IntegerType.INSTANCE);
		q.setParameter("i2", ea.getCopie(), IntegerType.INSTANCE);
		q.setParameter("dt3", ea.getDataCreazione(), DateType.INSTANCE);
		
		q.setParameter("dt4", ea.getDataInvio(), DateType.INSTANCE);
		q.setParameter("dt5", ea.getDataModifica(), DateType.INSTANCE);
		q.setParameter("dt6", ea.getDataOrdine(), DateType.INSTANCE);
		
		q.setParameter("dt7", ea.getDataAnnullamento(), DateType.INSTANCE);
		q.setParameter("id8", ea.getIdAbbonamento(), IntegerType.INSTANCE);
		q.setParameter("id9", ea.getIdAnagrafica(), IntegerType.INSTANCE);
		
		q.setParameter("id10", ea.getIdIstanzaAbbonamento(), IntegerType.INSTANCE);
		q.setParameter("id11", ea.getIdTipoDestinatario(), StringType.INSTANCE);
		q.setParameter("s12", ea.getNote(), StringType.INSTANCE);
		
		if (ea.getOrdiniLogistica() != null) {
			q.setParameter("id13", ea.getOrdiniLogistica().getId(), IntegerType.INSTANCE);
		} else {
			q.setParameter("id13", null, IntegerType.INSTANCE);
		}
		q.setParameter("b14", ea.getPrenotazioneIstanzaFutura(), BooleanType.INSTANCE);
		q.setParameter("s15", ea.getIdUtente(), StringType.INSTANCE);
		q.executeUpdate();
	}
}
