package it.giunti.apg.core.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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
import it.giunti.apg.shared.IstanzeStatusUtil;
import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.ArticoliListini;
import it.giunti.apg.shared.model.ArticoliOpzioni;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.MaterialiProgrammazione;
import it.giunti.apg.shared.model.MaterialiSpedizione;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;

public class MaterialiSpedizioneDao implements BaseDao<MaterialiSpedizione> {

	@Override
	public void update(Session ses, MaterialiSpedizione instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, MaterialiSpedizione transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, MaterialiSpedizione instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public List<MaterialiSpedizione> findByIstanza(Session ses, IstanzeAbbonamenti istanza)
			throws HibernateException {
		String qs = "select ms from MaterialiSpedizione ms, MaterialiProgrammazione mp where " +
				"ms.materiale = mp.materiale and "+
				"ms.idAbbonamento = :id1 and " +
				"mp.dataNominale >= :dt1 " +
				"order by ms.dataCreazione asc";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", istanza.getAbbonamento().getId(), IntegerType.INSTANCE);
		q.setParameter("dt1", istanza.getDataInizio(), DateType.INSTANCE);
		List<MaterialiSpedizione> cList = (List<MaterialiSpedizione>) q.list();
		return cList;
	}
	
	@SuppressWarnings("unchecked")
	public List<MaterialiSpedizione> findByAbbonamento(Session ses, Abbonamenti abb)
			throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from MaterialiSpedizione ms");
		qf.addWhere("ms.idAbbonamento = :p1");
		qf.addParam("p1", abb.getId());
		qf.addOrder("ms.dataCreazione asc");
		Query q = qf.getQuery();
		List<MaterialiSpedizione> cList = (List<MaterialiSpedizione>) q.list();
		return cList;
	}
	
	@SuppressWarnings("unchecked")
	public List<MaterialiSpedizione> findPrenotatiByAbbonamento(Session ses, Integer idAbbonamento)
			throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from MaterialiSpedizione ed");
		qf.addWhere("ed.idAbbonamento = :p1");
		qf.addParam("p1", idAbbonamento);
		qf.addWhere("ed.prenotazioneIstanzaFutura = :b1");
		qf.addParam("b1", true);
		qf.addOrder("ed.dataCreazione asc");
		Query q = qf.getQuery();
		List<MaterialiSpedizione> dList = (List<MaterialiSpedizione>) q.list();
		return dList;
	}
	
	@SuppressWarnings("unchecked")
	public List<MaterialiSpedizione> findByAnagrafica(Session ses, Integer idAnagrafica)
			throws HibernateException {
		String hql = "from MaterialiSpedizione ea where "+
			"ea.idAbbonamento is null and "+
			"ea.idAnagrafica = :id1 "+
			"order by ea.dataCreazione asc";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idAnagrafica, IntegerType.INSTANCE);
		List<MaterialiSpedizione> dList = (List<MaterialiSpedizione>) q.list();
		return dList;
	}

	@SuppressWarnings("unchecked")
	public List<MaterialiSpedizione> findPendingByPeriodico(Session ses, Integer idPeriodico)
			throws HibernateException {
		String hql =  "select ms from MaterialiSpedizione ms, MaterialiProgrammazione mp where "+
				"ms.materiale = mp.materiale and "+
				"mp.periodico.id = :id1 and " +
				"ms.dataInvio is null and ms.dataOrdine is null and " +
				"ms.materiale.inAttesa = :b1 " +//Non deve avere l'invio arretrato sospeso
				"order by ms.copie desc, mp.dataNominale asc";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		q.setParameter("b1", Boolean.FALSE);
		List<MaterialiSpedizione> cList = (List<MaterialiSpedizione>) q.list();
		return cList;
	}
	
	@SuppressWarnings("unchecked")
	public List<MaterialiSpedizione> findPendingByIstanzeManual(Session ses, Date today)
			throws HibernateException {
		String qString = "select ea from MaterialiSpedizione ea, IstanzeAbbonamenti ia where " +
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
		List<MaterialiSpedizione> edList = (List<MaterialiSpedizione>) q.list();
		return edList;
	}
	
	@SuppressWarnings("unchecked")
	public List<MaterialiSpedizione> findPendingByIstanzeListini(Session ses, Date today)
			throws HibernateException {
		String qString = "select ea from MaterialiSpedizione ea, IstanzeAbbonamenti ia, "+
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
		List<MaterialiSpedizione> edList = (List<MaterialiSpedizione>) q.list();
		return edList;
	}
	
	@SuppressWarnings("unchecked")
	public List<MaterialiSpedizione> findPendingByIstanzeOpzioni(Session ses, Date today)
			throws HibernateException {
		String qString = "select ea from MaterialiSpedizione ea, IstanzeAbbonamenti ia, "+
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
		List<MaterialiSpedizione> edList = (List<MaterialiSpedizione>) q.list();
		return edList;
	}
	
	@SuppressWarnings("unchecked")
	public List<MaterialiSpedizione> findPendingByAnagrafiche(Session ses, Date today)
			throws HibernateException {
		String qString = "select ea from MaterialiSpedizione ea where " +
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
		List<MaterialiSpedizione> edList = (List<MaterialiSpedizione>) q.list();
		return edList;
	}
	
	@SuppressWarnings("unchecked")
	public List<MaterialiSpedizione> findPendingByArticoloListino(Session ses,
			Integer idArticoloListino, Date date, int offset, int pageSize)
			throws HibernateException {
		String hql = "select ea from MaterialiSpedizione ea, IstanzeAbbonamenti ia, ArticoliListini al where " +
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
		List<MaterialiSpedizione> eaList = (List<MaterialiSpedizione>) q.list();
		return eaList;
	}
	
	@SuppressWarnings("unchecked")
	public List<MaterialiSpedizione> findPendingByArticoloOpzione(Session ses,
			Integer idArticoloOpzione, int offset, int pageSize)
			throws HibernateException {
		String hql = "select ea from MaterialiSpedizione ea, IstanzeAbbonamenti ia, ArticoliOpzioni ao where " +
				 "ea.idAbbonamento = ia.abbonamento.id and " +//join
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
		List<MaterialiSpedizione> eaList = (List<MaterialiSpedizione>) q.list();
		return eaList;
	}
	
	@SuppressWarnings("unchecked")
	public List<MaterialiSpedizione> findByNumeroOrdine(Session ses, String numeroOrdine)
			throws HibernateException {
		String hql = "from MaterialiSpedizione ea where "+
			"ea.ordineLogistica.numeroOrdine = :s1 " +
			"order by ea.id asc";
		Query q = ses.createQuery(hql);
		q.setParameter("s1", numeroOrdine, StringType.INSTANCE);
		List<MaterialiSpedizione> edList = (List<MaterialiSpedizione>) q.list();
		return edList;
	}
	
	public MaterialiSpedizione createEmptyFromIstanza(Session ses,
			IstanzeAbbonamenti ia, String idTipoDestinatario)
			throws HibernateException {
		MaterialiSpedizione newEa = new MaterialiSpedizione();
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
		newEa.setMateriale(null);//Sarà assegnato dopo
		newEa.setCopie(ia.getCopie());
		newEa.setDataCreazione(DateUtil.now());
		newEa.setDataLimite(null);
		newEa.setDataOrdine(null);
		newEa.setDataAnnullamento(null);
		newEa.setIdAbbonamento(ia.getAbbonamento().getId());
		newEa.setPrenotazioneIstanzaFutura(false);
		newEa.setNote("");
		return newEa;
	}
	
	public MaterialiSpedizione createFromListino(Session ses,
			ArticoliListini al, IstanzeAbbonamenti ia)
			throws HibernateException {
		MaterialiSpedizione newEa = new MaterialiSpedizione();
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
		newEa.setMateriale(al.getMateriale());
		newEa.setCopie(ia.getCopie());
		newEa.setDataCreazione(DateUtil.now());
		Date dataLimite = new ArticoliListiniDao().buildDataLimite(al, ia.getDataInizio());
		newEa.setDataLimite(dataLimite);
		newEa.setDataOrdine(null);
		newEa.setDataAnnullamento(null);
		newEa.setIdAbbonamento(ia.getAbbonamento().getId());
		newEa.setPrenotazioneIstanzaFutura(false);
		newEa.setNote("");
		return newEa;
	}
	
	public MaterialiSpedizione createFromOpzione(Session ses, 
			ArticoliOpzioni ao, IstanzeAbbonamenti ia)
			throws HibernateException {
		MaterialiSpedizione newEa = new MaterialiSpedizione();
		newEa.setIdArticoloListino(null);
		newEa.setIdArticoloOpzione(ao.getId());
		newEa.setMateriale(ao.getMateriale());
		newEa.setCopie(ia.getCopie());
		newEa.setDataCreazione(DateUtil.now());
		newEa.setDataLimite(null);
		newEa.setDataOrdine(null);
		newEa.setDataAnnullamento(null);
		newEa.setIdAbbonamento(ia.getAbbonamento().getId());
		newEa.setPrenotazioneIstanzaFutura(false);
		newEa.setIdAnagrafica(ia.getAbbonato().getId());
		newEa.setNote("");
		return newEa;
	}
	
	public MaterialiSpedizione createFromAnagrafica(Session ses, Integer idAnagrafica,
			Integer copie) throws HibernateException {
		//if (idTipoDestinatario == null) idTipoDestinatario = AppConstants.DEST_BENEFICIARIO;
		MaterialiSpedizione ed = new MaterialiSpedizione();
		ed.setDataCreazione(DateUtil.now());
		ed.setIdAbbonamento(null);
		ed.setCopie(copie);
		ed.setIdAnagrafica(idAnagrafica);
		ed.setNote("");
		ed.setPrenotazioneIstanzaFutura(false);
		return ed;
	}
	
	@SuppressWarnings("unchecked")
	public MaterialiSpedizione checkMaterialeAbbonamento(Session ses, int idMateriale, int idAbbonamento)
			throws HibernateException {
		String qs = "from MaterialiSpedizione ms where " +
				"ms.idAbbonamento = :p1 and " +
				"ms.materiale.id = :p2";
		Query q = ses.createQuery(qs);
		q.setInteger("p1", idAbbonamento);
		q.setInteger("p2", idMateriale);
		List<MaterialiSpedizione> eaList = (List<MaterialiSpedizione>) q.list();
		if (eaList == null) return null;
		if (eaList.size() > 0) {
			//Ritorna un'evasione, se è stata spedita
			MaterialiSpedizione result = null;
			for (MaterialiSpedizione ea:eaList) {
				if (result == null) result = ea;
				if (ea.getDataInvio() != null) return ea;
			}
			return result;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<MaterialiSpedizione> enqueueMissingArretratiByStatus(Session ses, 
			IstanzeAbbonamenti ia) throws HibernateException {
		//Pagato?
		boolean spedibile = IstanzeStatusUtil.isSpedibile(ia);
		//Bloccato?
		boolean bloccato = ia.getInvioBloccato();
		//Cartaceo
		boolean cartaceo = ia.getListino().getCartaceo();
		//Calcolo ultimo fascicolo arretrato a cui ha diritto
		MaterialiProgrammazioneDao mpDao = new MaterialiProgrammazioneDao();
		MaterialiProgrammazione maxFascicolo = mpDao.findLastFascicoloBetweenDates(ses, 
				ia.getAbbonamento().getPeriodico().getId(), ia.getDataInizio(), ia.getDataFine());
		if (ia.getDataDisdetta() == null) {	//Senza disdetta
			maxFascicolo = mpDao.stepForwardFascicoloAfterDate(ses,
					ia.getAbbonamento().getPeriodico().getId(), ia.getListino().getGracingFinale(), ia.getDataFine());
		}
		String hql1 = "from MaterialiProgrammazione mp where "+
				"mp.periodico.id = :id1 and "+
				"mp.dataNominale >= :dt1 and "+
				"mp.dataNominale <= :dt2 and "+
				"mp.dataEstrazione is not null "+
				"order by mp.dataNominale asc ";
		Query q1 = ses.createQuery(hql1);
		q1.setParameter("id1", ia.getAbbonamento().getPeriodico().getId());
		q1.setParameter("dt1", ia.getDataInizio());
		q1.setParameter("dt2", maxFascicolo.getDataNominale());
		List<MaterialiProgrammazione> mpList = (List<MaterialiProgrammazione>) q1.list();
		
		List<MaterialiSpedizione> msList = new ArrayList<MaterialiSpedizione>();
		if (mpList != null) {
			if (mpList.size() > 0) {
				//Arretrati già programmati o spediti presenti in fList
				String hql2 = "from MaterialiSpedizione ms where " +
						"ms.idAbbonamento = :id1 and " +
						"ms.dataCreazione >= :dt1 " +
						"order by ms.dataCreazione asc";
				Query q2 = ses.createQuery(hql2);
				q2.setParameter("id1", ia.getAbbonamento().getId());
				q2.setParameter("dt1", ia.getDataInizio());
				msList = (List<MaterialiSpedizione>) q2.list();
			}
		}
		//Ricerca arretrati e opzioni mancanti e crea una lista di quelli da creare
		List<MaterialiSpedizione> listToSend = new ArrayList<MaterialiSpedizione>();
		for (MaterialiProgrammazione mp:mpList) {
			//Considera il fascicolo se non è un opzione 
			//se è opzione lo considera solo se fa parte dei opzioni dell'istanza
			boolean fascicoloIsOpzione = (mp.getOpzione() != null);
			if (ia.getOpzioniIstanzeAbbonamentiSet() == null) ia.setOpzioniIstanzeAbbonamentiSet(new HashSet<OpzioniIstanzeAbbonamenti>());
			boolean selectedOpzione = false;
			for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
				if (oia.getOpzione().equals(mp.getOpzione())) selectedOpzione = true;
			}
			if ( !fascicoloIsOpzione || (fascicoloIsOpzione && selectedOpzione) ) {
				boolean found = false;
				//Cerca se esiste già un EvasioniFascicolo per questo Fascicolo
				for (MaterialiSpedizione ms:msList) {
					if (mp.getMateriale().getId().intValue() == ms.getMateriale().getId().intValue()) {
						found = true;
						break;
					}
				}
				//Il fascicolo è aggiunto se: 1) non è già nell'elenco 2) l'abbonamento non è bloccato
				// 3) l'istanza è cartacea, altrimenti solo se è un opzione 
				if (!found && !bloccato && (cartaceo || fascicoloIsOpzione)) {
					//ef non c'è e dovrebbe essere creato (alle seguenti condizioni)
					if (spedibile || ia.getListino().getInvioSenzaPagamento()) {
						MaterialiSpedizione newMs = createSpedizioneFromProgrammazione(mp, ia);
						listToSend.add(newMs);
					}
				}
			}
		}
		List<MaterialiSpedizione> result = new ArrayList<MaterialiSpedizione>();
		MaterialiSpedizioneDao msDao = new MaterialiSpedizioneDao();
		for (MaterialiSpedizione trans:listToSend) {
			Integer id = (Integer) msDao.save(ses, trans);
			MaterialiSpedizione persist = (MaterialiSpedizione) ses.get(MaterialiSpedizione.class, id);
			result.add(persist);
		}
		//updateFascicoliSpediti(ses, ia);
		return result;
	}
	private MaterialiSpedizione createSpedizioneFromProgrammazione(
			MaterialiProgrammazione mp, IstanzeAbbonamenti ia) {
		MaterialiSpedizione ef = new MaterialiSpedizione();
		//Evasione di un fascicolo
		ef.setDataCreazione(DateUtil.now());
		ef.setDataInvio(null);
		ef.setDataOrdine(null);
		ef.setMateriale(mp.getMateriale());
		ef.setIdAbbonamento(ia.getAbbonamento().getId());
		ef.setIdAnagrafica(ia.getAbbonato().getId());
		ef.setCopie(ia.getCopie());
		return ef;
	}
	

	public Integer setupAdditionalMateriali(Session ses,
			IstanzeAbbonamenti persistedIa) throws HibernateException {
		//Articoli prenotati su ABBONAMENTO
		List<MaterialiSpedizione> prenotatiList = findPrenotatiByAbbonamento(ses, persistedIa.getAbbonamento().getId());
		//Articoli presenti su ISTANZA
		List<MaterialiSpedizione> esistentiList = findByIstanza(ses, persistedIa);
		
		//Carica eventuali ArticoliListini da includere
		List<ArticoliListini> alList = new ArticoliListiniDao()
				.findByListino(ses, persistedIa.getListino().getId());
		//Carica eventuali ArticoliOpzioni da includere
		List<ArticoliOpzioni> aoList = new ArrayList<ArticoliOpzioni>();
		if (persistedIa.getOpzioniIstanzeAbbonamentiSet() != null) {
			for (OpzioniIstanzeAbbonamenti oia:persistedIa.getOpzioniIstanzeAbbonamentiSet()) {
				List<ArticoliOpzioni> list = new ArticoliOpzioniDao().findByOpzione(ses, oia.getOpzione().getId());
				if (list != null) aoList.addAll(list);
			}
		}
		
		List<MaterialiSpedizione> eaList = new ArrayList<MaterialiSpedizione>();
		//Aggiunta dei prenotati alla lista finale
		for (MaterialiSpedizione prenotato:prenotatiList) {
			prenotato.setIdAbbonamento(persistedIa.getAbbonamento().getId());
			prenotato.setPrenotazioneIstanzaFutura(false);
			update(ses, prenotato);
		}
		//Aggiunta da ArticoliListini (a meno di ESISTENTI)
		for (ArticoliListini al:alList) {
			boolean exists = false;
			for (MaterialiSpedizione ea:esistentiList) {
				if (ea.getMateriale().equals(al.getMateriale())) exists = true;
			}
			if (!exists) {
				MaterialiSpedizione newEa = createFromListino(ses, al, persistedIa);
				eaList.add(newEa);
			}
		}
		//Aggiunta da ArticoliOpzioni (a meno di ESISTENTI)
		for (ArticoliOpzioni ao:aoList) {
			boolean exists = false;
			for (MaterialiSpedizione ea:esistentiList) {
				if (ea.getMateriale().equals(ao.getMateriale())) exists = true;
			}
			if (!exists) {
				MaterialiSpedizione newEa = createFromOpzione(ses, ao, persistedIa);
				eaList.add(newEa);
			}
		}
		
		//Save or update articoli
		for (MaterialiSpedizione ea:eaList) {
			if (ea.getId() != null) {
				update(ses, ea);
			} else {
				save(ses, ea);
			}
		}
		return eaList.size();
	}	
	
	
	
	//metodi con SQL
	
	
	public void sqlInsert(Session ses, MaterialiSpedizione ea) throws HibernateException {
		String sql = "insert into materiali_spedizione(" +
					"id_materiale, copie, data_creazione, " +
					"data_invio, data_ordine, " +
					"data_annullamento, id_abbonamento, id_anagrafica, " +
					"id_abbonamento, note, "+
					"id_ordine_logistica, prenotazione_istanza_futura" +
				") values(" +
					":id1, :i2, :dt3, " +
					":dt4, :dt6, " +
					":dt7, :id8, :id9, " +
					":id10, :s12, " +
					":id13, :b14" +
				")";
		Query q = ses.createSQLQuery(sql);
		q.setParameter("id1", ea.getMateriale().getId(), IntegerType.INSTANCE);
		q.setParameter("i2", ea.getCopie(), IntegerType.INSTANCE);
		q.setParameter("dt3", ea.getDataCreazione(), DateType.INSTANCE);
		
		q.setParameter("dt4", ea.getDataInvio(), DateType.INSTANCE);
		q.setParameter("dt6", ea.getDataOrdine(), DateType.INSTANCE);
		
		q.setParameter("dt7", ea.getDataAnnullamento(), DateType.INSTANCE);
		q.setParameter("id8", ea.getIdAbbonamento(), IntegerType.INSTANCE);
		q.setParameter("id9", ea.getIdAnagrafica(), IntegerType.INSTANCE);
		
		q.setParameter("id10", ea.getIdAbbonamento(), IntegerType.INSTANCE);
		q.setParameter("s12", ea.getNote(), StringType.INSTANCE);
		
		if (ea.getOrdineLogistica() != null) {
			q.setParameter("id13", ea.getOrdineLogistica().getId(), IntegerType.INSTANCE);
		} else {
			q.setParameter("id13", null, IntegerType.INSTANCE);
		}
		q.setParameter("b14", ea.getPrenotazioneIstanzaFutura(), BooleanType.INSTANCE);
		q.executeUpdate();
	}

	public void sqlInsertFascicolo(Session ses, IstanzeAbbonamenti ia, Integer idMateriale,
			Date day) throws HibernateException {
		String sql = "insert into materiali_spedizione(" +
					"data_creazione, data_invio, id_materiale, " +
					"id_abbonamento, id_anagrafica, " +
					"copie" +
				") values(" +
					":d1, :d3, :id1, " +
					":id2, :id4, " +
					":i1 " +
				")";
		Query q = ses.createSQLQuery(sql);
		q.setDate("d1", day);
		q.setDate("d3", day);
		q.setInteger("id1", idMateriale);
		q.setInteger("id2", ia.getAbbonamento().getId());
		q.setInteger("id4", ia.getAbbonato().getId());
		q.setString("id5", AppConstants.EVASIONE_FAS_REGOLARE);
		q.setInteger("i1", ia.getCopie());
		q.executeUpdate();
	}
}
