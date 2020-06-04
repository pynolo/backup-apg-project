package it.giunti.apg.core.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

import it.giunti.apg.core.SerializationUtil;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.ImportiBusiness;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Comunicazioni;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.MaterialiProgrammazione;
import it.giunti.apg.shared.model.TipiAbbonamento;

public class EvasioniComunicazioniDao {

	private static final int PAGE_SIZE = 250;
	
	//@SuppressWarnings("unchecked")
	public void update(Session ses, EvasioniComunicazioni instance) throws HibernateException {
		if (instance == null) throw new HibernateException("Updating a null instance");
		GenericDao.updateGeneric(ses, instance.getId(), instance);
		//Integer id = instance.getId();
		//String hql = "from EvasioniComunicazioni ec where " +
		//		"ec.id = :id1";
		//Query q = ses.createQuery(hql);
		//q.setParameter("id1", id);
		//List<EvasioniComunicazioni> ecList = q.list();
		//if (ecList == null) throw new HibernateException("No EvasioniComunicazioni with id="+id);
		//if (ecList.size() == 0) throw new HibernateException("No EvasioniComunicazioni with id="+id);
		//EvasioniComunicazioni persEc = null;
		//if (ecList.size() > 0) {
		//	persEc = ecList.get(0);
		//	try {
		//		PropertyUtils.copyProperties(persEc, instance);
		//	} catch (Exception e) {
		//		throw new HibernateException(e);
		//	}
		//	ses.update(persEc);
		//}
	}

	public Serializable save(Session ses, EvasioniComunicazioni transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	public void delete(Session ses, Integer idEvasioniComunicazioni)
			throws HibernateException {
		String qs = "delete from EvasioniComunicazioni where id = :id1";
		Query query = ses.createQuery(qs);
		query.setParameter("id1", idEvasioniComunicazioni, IntegerType.INSTANCE);
		query.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniComunicazioni> findByIstanza(Session ses, Integer idIstanza)
			throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from EvasioniComunicazioni ec");
		qf.addWhere("ec.istanzaAbbonamento.id = :p1");
		qf.addParam("p1", idIstanza);
		Query q = qf.getQuery();
		List<EvasioniComunicazioni> cList = (List<EvasioniComunicazioni>) q.list();
		//Toglie i testi dalle bandelle per alleggerire la comunicazione
		for (EvasioniComunicazioni ec:cList) {
			SerializationUtil.makeSerializable(ec);
		}
		return cList;
	}
	
	public List<EvasioniComunicazioni> findOrPersistEvasioniComunicazioniProgrammate(Session ses, Date date,
			Integer idPeriodico, String idTipoMedia, String idTipoAttivazione, Integer idFasc,
			int idRapporto, String idUtente) 
					throws HibernateException, BusinessException {
		List<EvasioniComunicazioni> result = new ArrayList<EvasioniComunicazioni>();
		//Per status (vengono create al volo)
		if (idTipoAttivazione.equals(AppConstants.COMUN_ATTIVAZ_PER_STATUS)) {
			List<EvasioniComunicazioni> ecEventList = persistComunicazioniByStatus(ses,
					date, idTipoMedia, idPeriodico, idRapporto, idUtente);
			result.addAll(ecEventList);
		} else {
		//Per attivazione sincrona al fascicolo o per evento
			List<EvasioniComunicazioni> ecEventList = findEnqueuedComunicazioniByMediaAttivazione(ses,
					idPeriodico, idTipoMedia, idTipoAttivazione, idRapporto);
			result.addAll(ecEventList);
		}
		return result;
	}
	
	private void removeEliminati(List<EvasioniComunicazioni> ecList) {
		List<EvasioniComunicazioni> result = new ArrayList<EvasioniComunicazioni>();
		for (EvasioniComunicazioni ec:ecList) {
			if (!ec.getEliminato()) result.add(ec);
		}
		ecList.clear();
		ecList.addAll(result);
	}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniComunicazioni> findEvasioniComunicazioniManuali(Session ses,
			Integer idPeriodico, String idTipoMedia, 
			int idRapporto) throws HibernateException, BusinessException {
		String hql = "from EvasioniComunicazioni ec where " +
				"ec.istanzaAbbonamento.abbonamento.periodico.id = :p1 and " +
				"ec.eliminato = :b2 and " + //eliminato deve essere FALSE
				"ec.comunicazione is null and " +//manuale!!
				"ec.dataEstrazione is null and " +
				"ec.idTipoMedia = :s1 ";
		Query q = ses.createQuery(hql);
		q.setInteger("p1", idPeriodico);
		q.setBoolean("b2", Boolean.FALSE);
		q.setString("s1", idTipoMedia);
		List<EvasioniComunicazioni> pendingList = (List<EvasioniComunicazioni>) q.list();
		ImportiBusiness.persistImportiAndCausali(ses, pendingList);
		removeEliminati(pendingList);
		return pendingList;
	}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniComunicazioni> findEnqueuedComunicazioniByMediaAttivazione(Session ses,
			Integer idPeriodico, String idTipoMedia, String idTipoAttivazione,
			int idRapporto) throws HibernateException, BusinessException {
		String hql = "from EvasioniComunicazioni ec where " +
				"ec.istanzaAbbonamento.abbonamento.periodico.id = :p1 and " +
				"ec.eliminato = :b2 and " + //eliminato deve essere FALSE
				"ec.comunicazione.idTipoAttivazione = :s0 and " +
				"ec.dataEstrazione is null and " +
				"ec.idTipoMedia = :s1 ";
		Query q = ses.createQuery(hql);
		q.setInteger("p1", idPeriodico);
		q.setBoolean("b2", Boolean.FALSE);
		q.setString("s0", idTipoAttivazione);
		q.setString("s1", idTipoMedia);
		List<EvasioniComunicazioni> pendingList = (List<EvasioniComunicazioni>) q.list();
		ImportiBusiness.persistImportiAndCausali(ses, pendingList);
		removeEliminati(pendingList);
		return pendingList;
	}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniComunicazioni> findEnqueuedComunicazioniByMedia(Session ses,
			Integer idPeriodico, String idTipoMedia,
			int idRapporto) throws HibernateException, BusinessException {
		String hql = "from EvasioniComunicazioni ec where " +
				"ec.istanzaAbbonamento.abbonamento.periodico.id = :p1 and " +
				"ec.eliminato = :b2 and " + //eliminato deve essere FALSE
				"ec.dataEstrazione is null and " +
				"ec.idTipoMedia = :s1 ";
		Query q = ses.createQuery(hql);
		q.setInteger("p1", idPeriodico);
		q.setBoolean("b2", Boolean.FALSE);
		q.setString("s1", idTipoMedia);
		List<EvasioniComunicazioni> pendingList = (List<EvasioniComunicazioni>) q.list();
		ImportiBusiness.persistImportiAndCausali(ses, pendingList);
		removeEliminati(pendingList);
		return pendingList;
	}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniComunicazioni> findEnqueuedComunicazioniByMaterialeProgrammazione(Session ses,
			Integer idMaterialeProgrammazione, String idTipoMedia, int offset, int pageSize,
			int idRapporto) throws HibernateException, BusinessException {
		String hql = "from EvasioniComunicazioni ec where " +
				"ec.eliminato = :b1 and " + //eliminato deve essere FALSE
				"ec.dataEstrazione is null and " +
				"ec.comunicazione.idTipoMedia = :s1 and " +
				"ec.materialeProgrammazione.id = :id1 " +
				"order by ec.id ";
		Query q = ses.createQuery(hql);
		q.setParameter("b1", Boolean.FALSE);
		q.setParameter("s1", idTipoMedia, StringType.INSTANCE);
		q.setParameter("id1", idMaterialeProgrammazione, IntegerType.INSTANCE);
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<EvasioniComunicazioni> pendingList = (List<EvasioniComunicazioni>) q.list();
		ImportiBusiness.persistImportiAndCausali(ses, pendingList);
		removeEliminati(pendingList);
		return pendingList;
	}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniComunicazioni> findEnqueuedComunicazioniByComunicazione(Session ses,
			Integer idComunicazione, int offset, int pageSize,
			int idRapporto) throws HibernateException, BusinessException {
		String hql = "from EvasioniComunicazioni ec where " +
				"ec.eliminato = :b1 and " + //eliminato deve essere FALSE
				"ec.dataEstrazione is null and " +
				"ec.comunicazione.id = :id1 " +
				"order by ec.id ";
		Query q = ses.createQuery(hql);
		q.setParameter("b1", Boolean.FALSE);
		q.setParameter("id1", idComunicazione, IntegerType.INSTANCE);
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<EvasioniComunicazioni> pendingList = (List<EvasioniComunicazioni>) q.list();
		ImportiBusiness.persistImportiAndCausali(ses, pendingList);
		removeEliminati(pendingList);
		return pendingList;
	}
	
	@SuppressWarnings("unchecked")
	private List<EvasioniComunicazioni> persistComunicazioniByStatus(Session ses,
			Date date, String idTipoMedia,
			Integer idPeriodico, int idRapporto, String idUtente)
					throws HibernateException, BusinessException {
		List<EvasioniComunicazioni> result = new ArrayList<EvasioniComunicazioni>();
		//Map<Integer, Integer> iaMap = new HashMap<Integer, Integer>();//contiene gli id delle istanze per fare la deduplica
		String comQs = "from Comunicazioni c where " +
				"c.periodico.id = :p1 and " +
				"c.idTipoAttivazione = :ta1 and " +
				"c.idTipoMedia = :tm1 and " +
				"(c.dataInizio is null or c.dataInizio <= :d1) and " +
				"(c.dataFine is null or c.dataFine >= :d2)";
		Query comQ = ses.createQuery(comQs);
		comQ.setInteger("p1", idPeriodico);
		comQ.setString("ta1", AppConstants.COMUN_ATTIVAZ_PER_STATUS);
		comQ.setString("tm1", idTipoMedia);
		comQ.setDate("d1", date);
		comQ.setDate("d2", date);
		List<Comunicazioni> cList = (List<Comunicazioni>) comQ.list();
		//Cicla tutte le comunicazioni di una rivista
		//ed effettua una query per ogni tipo abbonamento legato alla comunicazione
		for (Comunicazioni com:cList) {
			String[] idsArray = com.getTipiAbbonamentoList().split(AppConstants.COMUN_TIPI_ABB_SEPARATOR);
			for (String ids:idsArray) {
				if (ids.length() > 0) {
					Integer idTa = Integer.parseInt(ids);
					TipiAbbonamento ta = (TipiAbbonamento) ses.get(TipiAbbonamento.class, idTa);
					QueryFactory qf = new QueryFactory(ses, "from IstanzeAbbonamenti ia");
					//Condizione di intervallo temporale istanza
					qf.addWhere("ia.fascicoloInizio.dataInizio <= :dt1");
					qf.addParam("dt1", date);
					qf.addWhere("ia.fascicoloFine.dataFine >= :dt2");
					qf.addParam("dt2", date);
					//condizioni ottenute dalla comunicazione
					qf.addWhere("ia.listino.tipoAbbonamento.id = :c1");
					qf.addParam("c1", idTa);
					if (com.getSoloNonPagati()) {
						qf.addWhere("ia.pagato = :c2 and ia.fatturaDifferita = :c3 and " +//pagato o fatturato
								"ia.listino.fatturaDifferita = :c4 ");//fatturato
						qf.addParam("c2", Boolean.FALSE);
						qf.addParam("c3", Boolean.FALSE);
						qf.addParam("c4", Boolean.FALSE);
					}
					if (com.getSoloPiuCopie() && !com.getSoloUnaCopia()) {
						qf.addWhere("ia.copie > :c5");
						qf.addParam("c5", 1);
					}
					if (com.getSoloUnaCopia() && !com.getSoloPiuCopie()) {
						qf.addWhere("ia.copie = :c6");
						qf.addParam("c6", 1);
					}
					if (com.getSoloConPagante()) {
						qf.addWhere("ia.pagante is not null");
					}
					if (com.getSoloSenzaPagante()) {
						qf.addWhere("ia.pagante is null");
					}
					if (com.getSoloUnaIstanza()) {
						qf.addWhere("(select count(ia2.id) from IstanzeAbbonamenti ia2 where ia2.abbonamento.id = ia.abbonamento.id) = :n1");
						qf.addParam("n1", new Integer(1));
					}
					if (com.getSoloMolteIstanze()) {
						qf.addWhere("(select count(ia3.id) from IstanzeAbbonamenti ia3 where ia3.abbonamento.id = ia.abbonamento.id) > :n2");
						qf.addParam("n2", new Integer(1));
					}
					//condizioni ovvie
					qf.addWhere("ia.invioBloccato = :b1");
					qf.addParam("b1", Boolean.FALSE);
					qf.addWhere("ia.ultimaDellaSerie = :b2");
					qf.addParam("b2", Boolean.TRUE);
					qf.addOrder("ia.id asc");
					
					VisualLogger.get().addHtmlInfoLine(idRapporto,
							"Estrazione '"+com.getTitolo()+"' per "+ta.getNome());
					Query q = qf.getQuery();
					//Ripete la query paginata con PAGE_SIZE
					List<IstanzeAbbonamenti> iaList = splitQueryInPages(ses, q, idRapporto);
					List<EvasioniComunicazioni> ecList = createEvasioniComFromIstanze(ses, iaList, com, null, date, idUtente);
					result.addAll(ecList);
					if (iaList.size() == 0) VisualLogger.get().addHtmlInfoLine(idRapporto,
							"Estratte "+iaList.size()+" istanze");
				}
			}
		}
		ImportiBusiness.persistImportiAndCausali(ses, result);
		removeEliminati(result);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private List<IstanzeAbbonamenti> splitQueryInPages(Session ses, Query q, int idRapporto) {
		//Ripete la query paginata con PAGE_SIZE
		List<IstanzeAbbonamenti> iaList = new ArrayList<IstanzeAbbonamenti>();
		int offset = 0;
		int resultSize = 0;
		do {
			if (offset != 0) VisualLogger.get().addHtmlInfoLine(idRapporto,
					"Estrazione in corso: "+offset+" abbonati");
			q.setMaxResults(PAGE_SIZE);
			q.setFirstResult(offset);
			List<IstanzeAbbonamenti> pagedList = (List<IstanzeAbbonamenti>) q.list();
			resultSize = pagedList.size();
			offset += resultSize;
			iaList.addAll(pagedList);
			ses.flush();
			ses.clear();
		} while (resultSize > 0);
		return iaList;
	}
	
	private List<EvasioniComunicazioni> createEvasioniComFromIstanze(
			Session ses, List<IstanzeAbbonamenti> iaList, Comunicazioni com, MaterialiProgrammazione matProg,
			Date date, String idUtente) throws HibernateException {
		List<EvasioniComunicazioni> ecList = new ArrayList<EvasioniComunicazioni>();
		for (IstanzeAbbonamenti ia:iaList) {
			EvasioniComunicazioni ec = new EvasioniComunicazioni();
			ec.setComunicazione(com);
			ec.setMaterialeProgrammazione(matProg);
			ec.setDataCreazione(date);
			ec.setDataModifica(date);
			ec.setDataEstrazione(date);
			ec.setEliminato(false);
			ec.setIstanzaAbbonamento(ia);
			ec.setIdTipoMedia(com.getIdTipoMedia());
			ec.setIdTipoDestinatario(com.getIdTipoDestinatario());
			ec.setRichiestaRinnovo(com.getRichiestaRinnovo());
			ec.setIdUtente(idUtente);
			ecList.add(ec);
		}
		return ecList;
	}
	
	
	@SuppressWarnings("unchecked")
	public EvasioniComunicazioni findEvasioniComunicazioniByAnagraficaProgrammazione(Session ses,
			IstanzeAbbonamenti ia, Comunicazioni com, MaterialiProgrammazione matProg) throws HibernateException {
		String qs = "from EvasioniComunicazioni as ec where " +
				"ec.istanzaAbbonamento.id = :id1 and " +
				"ec.comunicazione.id = :id2 and " +
				"ec.materiale.id = :id3";
		Query q = ses.createQuery(qs);
		q.setEntity("e1", ia.getId());
		q.setEntity("e2", com.getId());
		q.setEntity("e3", matProg.getId());
		List<EvasioniComunicazioni> ecList = (List<EvasioniComunicazioni>) q.list();
		if (ecList == null) return null;
		if (ecList.size() < 1) {
			return null;
		} else {
			return ecList.get(0);
		}
	}
	

	//metodi con SQL
	
	
	public void insert(Session ses, EvasioniComunicazioni ec) throws HibernateException {
		if (ec.getComunicazione() != null) {
			ec.setIdTipoDestinatario(ec.getComunicazione().getIdTipoDestinatario());
			ec.setIdTipoMedia(ec.getComunicazione().getIdTipoMedia());
		}
		save(ses, ec);
	}
	
	//public void sqlInsert(Session ses, EvasioniComunicazioni ec) throws HibernateException {
	//	String sql = "insert into evasioni_comunicazioni(" +
	//			"data_estrazione, data_creazione, data_modifica, id_utente, " +
	//			"id_istanza_abbonamento, id_comunicazione, id_fascicolo, " +
	//			"eliminato, messaggio, " +
	//			"id_tipo_destinatario, id_tipo_media, richiesta_rinnovo, note, " +
	//			"importo_stampato, importo_alternativo_stampato " +
	//		") values(" +
	//			":dt1, :dt2, :dt3, :s1, " +
	//			":id1, :id2, :id3, " +
	//			":b1, :s2, " +
	//			":s4, :s5, :b3, :s6, " +
	//			":d1, :d2 " +
	//		")";
	//	Query q = ses.createSQLQuery(sql);
	//	q.setParameter("dt1", ec.getDataEstrazione(), DateType.INSTANCE);
	//	q.setParameter("dt2", ec.getDataCreazione(), DateType.INSTANCE);
	//	q.setParameter("dt3", ec.getDataModifica(), DateType.INSTANCE);
	//	q.setParameter("s1", ec.getIdUtente(), StringType.INSTANCE);
	//	q.setParameter("id1", ec.getIstanzaAbbonamento().getId(), IntegerType.INSTANCE);
	//	if(ec.getFascicolo() != null) {
	//		q.setParameter("id3", ec.getFascicolo().getId(), IntegerType.INSTANCE);
	//	} else {
	//		q.setParameter("id3", null, IntegerType.INSTANCE);
	//	}
	//	q.setParameter("b1", ec.getEliminato(), BooleanType.INSTANCE);
	//	q.setParameter("s2", ec.getMessaggio(), StringType.INSTANCE);
	//	q.setParameter("b3", ec.getRichiestaRinnovo(), BooleanType.INSTANCE);
	//	q.setParameter("s6", ec.getNote(), StringType.INSTANCE);
	//	q.setParameter("d1", ec.getImportoStampato(), DoubleType.INSTANCE);
	//	q.setParameter("d2", ec.getImportoAlternativoStampato(), DoubleType.INSTANCE);
	//	if (ec.getComunicazione() != null) {
	//		q.setParameter("id2", ec.getComunicazione().getId(), IntegerType.INSTANCE);
	//		q.setParameter("s4", ec.getComunicazione().getIdTipoDestinatario(), StringType.INSTANCE);
	//		q.setParameter("s5", ec.getComunicazione().getIdTipoMedia(), StringType.INSTANCE);
	//	} else {
	//		q.setParameter("id2", null, IntegerType.INSTANCE);
	//		q.setParameter("s4", ec.getIdTipoDestinatario(), StringType.INSTANCE);
	//		q.setParameter("s5", ec.getIdTipoMedia(), StringType.INSTANCE);
	//	}
	//	q.executeUpdate(); //NON SUPPORTA INSERT
	//}
	
	public void sqlUpdate(Session ses, EvasioniComunicazioni ec) throws HibernateException {
		String sql = "update evasioni_comunicazioni as ec set " +
				"ec.data_estrazione=:dt1, " +
				"ec.data_creazione=:dt2, " +
				"ec.data_modifica=:dt3, " +
				"ec.id_utente=:s1, " +
				"ec.id_istanza_abbonamento=:id1, " +
				"ec.id_comunicazione=:id2, " +
				"ec.eliminato=:b1, " +
				"ec.messaggio=:s2, " +
				"ec.id_tipo_destinatario=:s4, " +
				"ec.id_tipo_media=:s5, " +
				"richiesta_rinnovo=:b3, " +
				"note=:s6, " +
				"ec.importo_stampato=:d1, " +
				"ec.importo_alternativo_stampato=:d2 ";
		if (ec.getMaterialeProgrammazione() != null) sql += ", ec.id_materiale_programmazione=:id3 ";
		if (ec.getProgressivo() != null) sql += ", ec.progressivo=:i1 ";
		sql += "where " +
				"ec.id=:id4 ";
		Query q = ses.createSQLQuery(sql);
		q.setDate("dt1", ec.getDataEstrazione());
		q.setDate("dt2", ec.getDataCreazione());
		q.setDate("dt3", ec.getDataModifica());
		q.setString("s1", ec.getIdUtente());
		q.setInteger("id1", ec.getIstanzaAbbonamento().getId());
		q.setInteger("id4", ec.getId());
		q.setBoolean("b1", ec.getEliminato());
		if (ec.getMaterialeProgrammazione() != null) q.setInteger("id3", ec.getMaterialeProgrammazione().getId());
		if (ec.getProgressivo() != null) q.setInteger("i1", ec.getProgressivo());
		q.setParameter("s2", ec.getMessaggio(), StringType.INSTANCE);
		q.setParameter("b3", ec.getRichiestaRinnovo(), BooleanType.INSTANCE);
		q.setParameter("s6", ec.getNote(), StringType.INSTANCE);
		q.setParameter("d1", ec.getImportoStampato(), DoubleType.INSTANCE);
		q.setParameter("d2", ec.getImportoAlternativoStampato(), DoubleType.INSTANCE);
		if (ec.getComunicazione() != null) {
			q.setParameter("id2", ec.getComunicazione().getId(), IntegerType.INSTANCE);
			q.setParameter("s4", ec.getComunicazione().getIdTipoDestinatario(), StringType.INSTANCE);
			q.setParameter("s5", ec.getComunicazione().getIdTipoMedia(), StringType.INSTANCE);
		} else {
			q.setParameter("id2", null, IntegerType.INSTANCE);
			q.setParameter("s4", ec.getIdTipoDestinatario(), StringType.INSTANCE);
			q.setParameter("s5", ec.getIdTipoMedia(), StringType.INSTANCE);
		}
		q.executeUpdate();
	}
	
	//private void assegnaProgressivoNdd(Session ses, List<EvasioniComunicazioni> ecList) {
		//Assegna un progressivo nel caso si tratti di stampe di NDD
			//int numProgressivo = -1;
			//for (EvasioniComunicazioni ec:ecList) {
			//	if (ec.getIdTipoMedia().equals(AppConstants.COMUN_MEDIA_NDD)) {
			//		if (numProgressivo < 0) {
			//			numProgressivo = new ContatoriDao().loadProgressivo(ses, ServerConstants.CONTATORE_NDD);
			//		}
			//		numProgressivo++;
			//		ec.setProgressivo(numProgressivo);
			//	}
			//}
	//}
	
	//private Date getGiugnoScorso() {
	//	Calendar cal = new GregorianCalendar();
	//	if (cal.get(Calendar.MONTH) < Calendar.JUNE) {
	//		cal.add(Calendar.YEAR, -1);
	//	}
	//	cal.set(Calendar.MONTH, Calendar.JUNE);
	//	cal.set(Calendar.DAY_OF_MONTH, 1);//cal contiene il 1° giugno scorso
	//	return cal.getTime();
	//}
}
