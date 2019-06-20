package it.giunti.apg.core.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

import it.giunti.apg.core.OpzioniUtil;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.business.CacheBusiness;
import it.giunti.apg.core.business.FascicoliBusiness;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Comunicazioni;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Opzioni;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.TipiAbbonamento;

public class IstanzeAbbonamentiDao implements BaseDao<IstanzeAbbonamenti> {

	@Override
	public void update(Session ses, IstanzeAbbonamenti instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
		//Aggiorna cache
		try {
			CacheBusiness.saveOrUpdateCache(ses, instance.getAbbonato(), false);
			if (instance.getPagante() != null)
					CacheBusiness.saveOrUpdateCache(ses, instance.getPagante(), false);
		} catch (BusinessException e) {
			throw new HibernateException(e.getMessage(), e);
		}
		//Editing log
		LogEditingDao.writeEditingLog(ses, IstanzeAbbonamenti.class, instance.getId(), 
				instance.getId()+"", instance.getIdUtente());
	}
	
	public void updateUnlogged(Session ses, IstanzeAbbonamenti instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
		//Aggiorna cache
		try {
			CacheBusiness.saveOrUpdateCache(ses, instance.getAbbonato(), true);
			if (instance.getPagante() != null)
					CacheBusiness.saveOrUpdateCache(ses, instance.getPagante(), true);
		} catch (BusinessException e) {
			throw new HibernateException(e.getMessage(), e);
		}
	}
	
	@Override
	public Integer save(Session ses, IstanzeAbbonamenti item) throws HibernateException {
		return this.save(ses, item, false);//Does not create evasioni fascicoli/articoli
	}

	@Override
	public void delete(Session ses, IstanzeAbbonamenti instance)
			throws HibernateException {
		Anagrafiche abbonato = instance.getAbbonato();
		Anagrafiche pagante = instance.getPagante();
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
		//Aggiorna cache
		try {
			CacheBusiness.removeCache(ses, abbonato.getId(), true);
			if (pagante != null)
					CacheBusiness.removeCache(ses, pagante.getId(), true);
		} catch (BusinessException e) {
			throw new HibernateException(e.getMessage(), e);
		}
		//Deletion log
		LogDeletionDao.writeDeletionLog(ses, IstanzeAbbonamenti.class, instance.getId(),
				instance.getId()+"", instance.getIdUtente());
	}

	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findIstanzeByAbbonamento(Session ses,
			Integer idAbbonamento) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from IstanzeAbbonamenti ia");
		qf.addWhere("ia.abbonamento.id = :p1");
		qf.addParam("p1", idAbbonamento);
		qf.addOrder("ia.dataCreazione desc");
		Query q = qf.getQuery();
		List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
		return abbList;
	}
	
	@SuppressWarnings("unchecked")
	public IstanzeAbbonamenti findUltimaIstanzaByAbbonamento(Session ses,
			Integer idAbbonamento) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from IstanzeAbbonamenti ia");
		qf.addWhere("ia.abbonamento.id = :p1");
		qf.addParam("p1", idAbbonamento);
		qf.addWhere("ia.ultimaDellaSerie = :p2");
		qf.addParam("p2", Boolean.TRUE);
		qf.addOrder("ia.dataCreazione desc");
		Query q = qf.getQuery();
		List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
		if (abbList != null) {
			if (abbList.size() > 0) {
				return abbList.get(0);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public IstanzeAbbonamenti findIstanzaByCodiceData(Session ses,
			String codiceAbb, Date dataControllo) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from IstanzeAbbonamenti ia");
		qf.addWhere("ia.abbonamento.codiceAbbonamento like :s1");
		qf.addParam("s1", codiceAbb);
		if (dataControllo != null) {
			qf.addWhere("ia.fascicoloInizio.dataInizio <= :dt1");
			qf.addParam("dt1", dataControllo);
			qf.addWhere("ia.fascicoloFine.dataFine >= :dt2");
			qf.addParam("dt2", dataControllo);
		} else {
			qf.addWhere("ia.ultimaDellaSerie = :b1");
			qf.addParam("b1", Boolean.TRUE);
		}
		qf.addOrder("ia.dataCreazione desc");
		Query q = qf.getQuery();
		List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
		if (abbList != null) {
			if (abbList.size() > 0) {
				return abbList.get(0);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findUnsettledIstanzeByDataInizio(Session ses,
			Integer idListino, Date startEndDate, Date finishEndDate, Boolean hasDisdetta,
			int offset, int pageSize) throws HibernateException {
		String hql = "from IstanzeAbbonamenti ia where "+
				"ia.listino.id = :id1 and "+
				"ia.fascicoloInizio.dataInizio >= :dt1 and "+
				"ia.fascicoloInizio.dataInizio <= :dt2 and "+
				"ia.ultimaDellaSerie = :b1 and "+
				"ia.invioBloccato = :b2 and ";
		if (hasDisdetta != null) {
			if (hasDisdetta) {
				hql += "ia.dataDisdetta is not null and ";
			} else {
				hql += "ia.dataDisdetta is null and ";
			}
		}
		hql += "(ia.pagato = :pag_b1 and ia.fatturaDifferita = :pag_b2 and "+
					"ia.listino.fatturaDifferita = :pag_b3 and prezzo >= :pag_d1) "+//NON PAGATO
				"order by ia.id";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idListino, IntegerType.INSTANCE);
		q.setParameter("dt1", startEndDate, DateType.INSTANCE);
		q.setParameter("dt2", finishEndDate, DateType.INSTANCE);
		q.setParameter("b1", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b2", Boolean.FALSE, BooleanType.INSTANCE);
		q.setParameter("pag_b1", Boolean.FALSE, BooleanType.INSTANCE);
		q.setParameter("pag_b2", Boolean.FALSE, BooleanType.INSTANCE);
		q.setParameter("pag_b3", Boolean.FALSE, BooleanType.INSTANCE);
		q.setParameter("pag_d1", AppConstants.SOGLIA, DoubleType.INSTANCE);
		
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<IstanzeAbbonamenti> istList = (List<IstanzeAbbonamenti>) q.list();
		return istList;
	}
	
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findActiveIstanzeByDataFine(Session ses,
			Integer idListino, Date startEndDate, Date finishEndDate, Boolean hasDisdetta,
			int offset, int pageSize) throws HibernateException {
		String hql = "from IstanzeAbbonamenti ia where "+
				"ia.listino.id = :id1 and "+
				"ia.fascicoloFine.dataFine >= :dt1 and "+
				"ia.fascicoloFine.dataFine <= :dt2 and "+
				"ia.ultimaDellaSerie = :b1 and "+
				"ia.invioBloccato = :b2 and ";
		if (hasDisdetta != null) {
			if (hasDisdetta) {
				hql += "ia.dataDisdetta is not null and ";
			} else {
				hql += "ia.dataDisdetta is null and ";
			}
		}
		hql += "(ia.pagato = :pag_b1 or ia.fatturaDifferita = :pag_b2 or "+
					"ia.listino.fatturaDifferita = :pag_b3 or prezzo < :pag_d1) "+//PAGATO
				"order by ia.id";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idListino, IntegerType.INSTANCE);
		q.setParameter("dt1", startEndDate, DateType.INSTANCE);
		q.setParameter("dt2", finishEndDate, DateType.INSTANCE);
		q.setParameter("b1", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b2", Boolean.FALSE, BooleanType.INSTANCE);
		q.setParameter("pag_b1", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("pag_b2", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("pag_b3", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("pag_d1", AppConstants.SOGLIA, DoubleType.INSTANCE);
		
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<IstanzeAbbonamenti> istList = (List<IstanzeAbbonamenti>) q.list();
		return istList;
	}
	
	public List<IstanzeAbbonamenti> findLastIstanzeByAnagrafica(Session ses, 
			Integer idAnagrafica, boolean soloNonPagate, boolean soloScadute) throws HibernateException {
		return findLastIstanzeByAnagraficaSocieta(ses, idAnagrafica, null, soloNonPagate, soloScadute);
	}
	
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findLastIstanzeByAnagraficaSocieta(Session ses, 
			Integer idAnagrafica, String idSocieta, boolean soloNonPagate, boolean soloScadute) throws HibernateException {
		String hql = "from IstanzeAbbonamenti ia "+
				"where ia.ultimaDellaSerie = :b1 "+
				"and (ia.abbonato.id = :id1 or ia.pagante.id = :id2) ";
		if (idSocieta != null) hql += "and ia.fascicoloInizio.periodico.idSocieta = :s1 ";
		if (soloNonPagate) hql += "and ia.pagato = :b2 and ia.fatturaDifferita = :b3 and ia.listino.fatturaDifferita = :b4 and ia.listino.prezzo >= :d1 ";
		if (soloScadute) hql += "and ia.fascicoloFine.dataInizio < :dt1 ";
		hql += "order by ia.id asc";
		Query q = ses.createQuery(hql);
		q.setParameter("b1", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("id1", idAnagrafica, IntegerType.INSTANCE);
		q.setParameter("id2", idAnagrafica, IntegerType.INSTANCE);
		if (idSocieta != null) q.setParameter("s1", idSocieta, StringType.INSTANCE);
		if (soloNonPagate) {
			q.setParameter("b2", Boolean.FALSE, BooleanType.INSTANCE);
			q.setParameter("b3", Boolean.FALSE, BooleanType.INSTANCE);
			q.setParameter("b4", Boolean.FALSE, BooleanType.INSTANCE);
			q.setParameter("d1", AppConstants.SOGLIA, DoubleType.INSTANCE);
		}
		if (soloScadute) q.setParameter("dt1", DateUtil.now(), DateType.INSTANCE);
		List<IstanzeAbbonamenti> istList = (List<IstanzeAbbonamenti>) q.list();
		return istList;
	}

	
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> quickSearchIstanzeAbbonamenti(Session ses,
			String searchString, Integer offset, Integer size) throws HibernateException {
		Integer uid = null;
		try {
			uid = Integer.parseInt(searchString);
		} catch (NumberFormatException e) { }
		QueryFactory qf = new QueryFactory(ses, "from IstanzeAbbonamenti ia");
		String s = searchString.replace('*', '%');
		if (uid == null) {
			qf.addWhere("ia.abbonamento.codiceAbbonamento like :s1 ");
			qf.addParam("s1", s);
		} else {
			qf.addWhere("ia.id = :id1 ");
			qf.addParam("id1", uid);
		}
		qf.addWhere("ia.ultimaDellaSerie = :p1");
		qf.addParam("p1", Boolean.TRUE);
		qf.addOrder("ia.dataModifica desc");
		qf.setPaging(offset, size);
		Query q = qf.getQuery();
		List<IstanzeAbbonamenti> istList = (List<IstanzeAbbonamenti>) q.list();
		return istList;
	}
	
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findIstanzeByCodice(Session ses, String codice,
			Integer offset, Integer size) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from IstanzeAbbonamenti ia");
		qf.addWhere("ia.abbonamento.codiceAbbonamento like :p1");
		qf.addParam("p1", codice);
		qf.addOrder("ia.fascicoloInizio.dataInizio desc, ia.dataCreazione desc");
		qf.setPaging(offset, size);
		Query q = qf.getQuery();
		List<IstanzeAbbonamenti> istList = (List<IstanzeAbbonamenti>) q.list();
		return istList;
	}
	
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findIstanzeByAdesione(Session ses, Integer idAdesione) throws HibernateException {
		String hql = "from IstanzeAbbonamenti ia where "+
			"ia.adesione.id = :id1 "+
			"order by ia.dataCreazione desc";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idAdesione, IntegerType.INSTANCE);
		List<IstanzeAbbonamenti> iaList = (List<IstanzeAbbonamenti>) q.list();
		return iaList;
	}
	
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findIstanzeByIdFattura(Session ses, Integer idFattura) throws HibernateException {
		String qs = "from IstanzeAbbonamenti ia where " +
				"ia.idFattura = :id1 "+
				"order by ia.dataCreazione desc";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idFattura, IntegerType.INSTANCE);
		List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
		return abbList;
	}
	
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findIstanzeConCreditoBySocieta(Session ses,
			String idSocieta, int monthsExpired, boolean regalo, int offset, int pageSize) throws HibernateException {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.MONTH, (-1)*monthsExpired);
		Date expiration = cal.getTime();
		String qs = "select ia from IstanzeAbbonamenti ia, PagamentiCrediti pc where ";
				//"((ia.pagante is not null and ia.pagante.id = pc.idAnagrafica) or (ia.pagante is null and ia.abbonato.id = pc.idAnagrafica)) and "+
		if (regalo) {
			qs += "ia.pagante is not null and ia.pagante.id = pc.idAnagrafica and ";
		} else {
			qs += "ia.pagante is null and ia.abbonato.id = pc.idAnagrafica and ";
		}
		qs += "ia.abbonamento.periodico.idSocieta = :id1 and " + 
				"ia.fascicoloFine.dataFine >= :dt1 and "+
				"ia.invioBloccato = :b0 and "+
				"(ia.pagato = :b1 and ia.fatturaDifferita = :b2 and ia.listino.fatturaDifferita = :b3 and ia.listino.prezzo >= :d1) and " +
				"pc.fatturaImpiego is null and " +
				"pc.idSocieta = :id2 "+
				"order by ia.dataCreazione desc ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idSocieta, StringType.INSTANCE);
		q.setParameter("dt1", expiration, DateType.INSTANCE);
		q.setParameter("b0", false, BooleanType.INSTANCE);
		q.setParameter("b1", false, BooleanType.INSTANCE);
		q.setParameter("b2", false, BooleanType.INSTANCE);
		q.setParameter("b3", false, BooleanType.INSTANCE);
		q.setParameter("d1", AppConstants.SOGLIA, DoubleType.INSTANCE);
		q.setParameter("id2", idSocieta, StringType.INSTANCE);
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
		return abbList;
	}
		
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findIstanzeProprieByAnagrafica(Session ses,
			Integer idAbbonato, boolean onlyLatest, int offset, int pageSize) throws HibernateException {
		String qs = "from IstanzeAbbonamenti ia where " +
				"ia.abbonato.id = :id1 ";
		if (onlyLatest) {
			qs += "and (ia.ultimaDellaSerie = :b1 or ia.fascicoloInizio.dataInizio >= :dt1) ";
		}
		qs += "order by ia.dataCreazione desc ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idAbbonato, IntegerType.INSTANCE);
		if (onlyLatest) {
			q.setParameter("b1", Boolean.TRUE, BooleanType.INSTANCE);
			q.setParameter("dt1", DateUtil.now(), DateType.INSTANCE);
		}
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
		return abbList;
	}
	
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findIstanzeProprieByAnagraficaDate(Session ses,
			Integer idAnanagrafica, Date date, int offset, int pageSize) throws HibernateException {
		String qs = "from IstanzeAbbonamenti ia where " +
				"ia.abbonato.id = :id1 and " + 
				"ia.fascicoloInizio.dataInizio <= :dt1 and " +
				"ia.fascicoloFine.dataFine >= :dt2 " +
				"order by ia.dataCreazione desc ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idAnanagrafica, IntegerType.INSTANCE);
		q.setParameter("dt1", date, DateType.INSTANCE);
		q.setParameter("dt2", date, DateType.INSTANCE);
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
		return abbList;
	}
	
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findIstanzeRegalateByAnagrafica(Session ses,
			Integer idAnanagrafica, boolean onlyLatest,  int offset, int pageSize) throws HibernateException {
		String qs = "from IstanzeAbbonamenti ia where " +
				"ia.pagante.id = :id1 ";
		if (onlyLatest) {
			qs += "and (ia.ultimaDellaSerie = :b1 or ia.fascicoloInizio.dataInizio >= :dt1) ";
		}
		qs += "order by ia.dataCreazione desc ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idAnanagrafica, IntegerType.INSTANCE);
		if (onlyLatest) {
			q.setParameter("b1", Boolean.TRUE, BooleanType.INSTANCE);
			q.setParameter("dt1", DateUtil.now(), DateType.INSTANCE);
		}
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
		return abbList;
	}
	
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findIstanzeRegalateByAnagraficaDate(Session ses,
			Integer idAnanagrafica, Date date, int offset, int pageSize) throws HibernateException {
		String qs = "from IstanzeAbbonamenti ia where " +
				"ia.pagante.id = :id1 and " + 
				"ia.fascicoloInizio.dataInizio <= :dt1 and " +
				"ia.fascicoloFine.dataFine >= :dt2 " +
				"order by ia.dataCreazione desc ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idAnanagrafica, IntegerType.INSTANCE);
		q.setParameter("dt1", date, DateType.INSTANCE);
		q.setParameter("dt2", date, DateType.INSTANCE);
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
		return abbList;
	}
	
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findIstanzePromosseByAnagrafica(Session ses,
			Integer idAbbonato, boolean onlyLatest, int offset, int pageSize) throws HibernateException {
		String qs = "from IstanzeAbbonamenti ia where " +
				"ia.promotore.id = :id1 ";
		if (onlyLatest) qs += "and ia.ultimaDellaSerie = :b1 ";
		qs += "order by ia.dataCreazione desc ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idAbbonato, IntegerType.INSTANCE);
		if (onlyLatest) q.setParameter("b1", Boolean.TRUE, BooleanType.INSTANCE);
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
		return abbList;
	}
	
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findIstanzePromosseByAnagraficaDate(Session ses,
			Integer idAbbonato, Date date, int offset, int pageSize) throws HibernateException {
		String qs = "from IstanzeAbbonamenti ia where " +
				"ia.promotore.id = :id1 and " + 
				"ia.fascicoloInizio.dataInizio <= :dt1 and " +
				"ia.fascicoloFine.dataFine >= :dt2 " +
				"order by ia.dataCreazione desc ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idAbbonato, IntegerType.INSTANCE);
		q.setParameter("dt1", date, DateType.INSTANCE);
		q.setParameter("dt2", date, DateType.INSTANCE);
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
		return abbList;
	}
	
	///** Controlla se la comunicazione ha una restrizione su un particolare tag di opzione.
	// * Se no, allora estrae le istanze in attesa della particolare comunicazione.
	// * Se sì, trova i Opzioni corrispondenti al tag e estrae le istanze in attesa della
	// * comunicazione restringendo a quelle abbinate al dato opzione.
	// * @param ses
	// * @param idTipoAbb
	// * @param com
	// * @param sogliaDt
	// * @return
	// * @throws HibernateException
	// */
	//@SuppressWarnings("unchecked")
	//public List<IstanzeAbbonamenti> findIstanzeByMissingEvasioneOnCreation(Session ses, 
	//		Integer idTipoAbb, Comunicazioni com, Date sogliaDt) throws HibernateException {
	//	if (com.getTagOpzione() == null) com.setTagOpzione("");
	//	if (com.getTagOpzione().length() == 0) {
	//		return findIstanzeByMissingEvasioneOnCreation(ses, idTipoAbb, com, null, sogliaDt);
	//	} else {
	//		String supHql = "from Opzioni s where s.tag = :tag";
	//		Query supQ = ses.createQuery(supHql);
	//		supQ.setParameter("tag", com.getTagOpzione(), StringType.INSTANCE);
	//		List<Opzioni> supList = (List<Opzioni>) supQ.list();
	//		if (supList == null) supList = new ArrayList<Opzioni>();
	//		if (supList.size() == 0) {
	//			return findIstanzeByMissingEvasioneOnCreation(ses, idTipoAbb, com, null, sogliaDt);
	//		} else {
	//			List<IstanzeAbbonamenti> iaList = new ArrayList<IstanzeAbbonamenti>();
	//			for (Opzioni sup:supList) {
	//				iaList.addAll(
	//						findIstanzeByMissingEvasioneOnCreation(ses, idTipoAbb, com, sup, sogliaDt));
	//			}
	//			return iaList;
	//		}
	//	}
	//}
	
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findIstanzeByMissingEvasioneOnCreationOrCambioTipo(Session ses, 
			Integer idTipoAbb, Comunicazioni com, String tagOpzione, Date sogliaDt) throws HibernateException {
		Integer idFascicoloInizio = com.getIdFascicoloInizio();
		String hql = "select ia from IstanzeAbbonamenti ia ";
		if (tagOpzione != null) hql += "join ia.opzioniIstanzeAbbonamentiSet sl "; 
		hql += "where " +
				"(ia.dataCreazione >= :d11 or ia.dataCambioTipo >= :d12) and " +
				"ia.listino.tipoAbbonamento.id = :i1 and " + 
				"ia.id not in (" +//insieme delle istanze che nello stesso periodo hanno ricevuto la comunicazione com
					"select ec.istanzaAbbonamento.id from EvasioniComunicazioni ec where " +
					/*"ec.istanzaAbbonamento.dataModifica >= :d2 and " +*/
					"ec.comunicazione.id = :i2 and " +
					"ec.istanzaAbbonamento.listino.tipoAbbonamento.id = :i3" +
				") ";
		if (idFascicoloInizio != null) {
			hql += "and ia.fascicoloInizio.id = :id1 ";
		}
		if (tagOpzione != null) {
			hql += "and sl.opzione.tag = :tag ";//il opzione deve avere il tag
		}
		if (com.getSoloConPagante()) {
			hql += "and ia.pagante is not null ";
		}
		if (com.getSoloSenzaPagante()) {
			hql += "and ia.pagante is null ";
		}
		Query q = ses.createQuery(hql);
		q.setTimestamp("d11", sogliaDt);
		q.setTimestamp("d12", sogliaDt);
		q.setInteger("i1", idTipoAbb);
		/*q.setTimestamp("d2", sogliaDt);*/
		q.setInteger("i2", com.getId());
		q.setInteger("i3", idTipoAbb);
		if (idFascicoloInizio != null) q.setParameter("id1", idFascicoloInizio);
		if (tagOpzione != null) q.setParameter("tag", tagOpzione, StringType.INSTANCE);
		List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
		return abbList;
	}
		
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findIstanzeByMissingEvasioneOnPayment(Session ses,
			Integer idTipoAbb, Comunicazioni com, String tagOpzione, Date sogliaDt) throws HibernateException {
		Integer idFascicoloInizio = com.getIdFascicoloInizio();
		String hql = "select ia from IstanzeAbbonamenti ia ";
		if (tagOpzione != null) hql += "join ia.opzioniIstanzeAbbonamentiSet sl "; 
		hql += "where "+
				"ia.dataSaldo >= :d1 and " +
				"(ia.pagato = :b1 or ia.fatturaDifferita = :b2) and " +
				"ia.listino.tipoAbbonamento.id = :i1 and " +
				"ia.id not in (" +//insieme delle istanze che nello stesso periodo hanno ricevuto la comunicazione com
					"select ec.istanzaAbbonamento.id from EvasioniComunicazioni ec where " +
					/*"ec.istanzaAbbonamento.dataModifica >= :d2 and " +*/
					"ec.comunicazione.id = :i2 and " +
					"ec.istanzaAbbonamento.listino.tipoAbbonamento.id = :i3" +
				") ";
		if (idFascicoloInizio != null) {
			hql += "and ia.fascicoloInizio.id = :id1 ";
		}
		if (tagOpzione != null) {
			hql += "and sl.opzione.tag like :tag ";//il opzione deve avere il tag
		}
		if (com.getSoloConPagante()) {
			hql += "and ia.pagante is not null ";
		}
		if (com.getSoloSenzaPagante()) {
			hql += "and ia.pagante is null ";
		}
		Query q = ses.createQuery(hql);
		q.setDate("d1", sogliaDt);
		q.setBoolean("b1", Boolean.TRUE);
		q.setBoolean("b2", Boolean.TRUE);
		q.setInteger("i1", idTipoAbb);
		/*q.setTimestamp("d2", sogliaDt);*/
		q.setInteger("i2", com.getId());
		q.setInteger("i3", idTipoAbb);
		if (idFascicoloInizio != null) q.setParameter("id1", idFascicoloInizio);
		if (tagOpzione != null) q.setParameter("tag", tagOpzione, StringType.INSTANCE);
		List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
		return abbList;
	}

	@SuppressWarnings("unchecked")
	public IstanzeAbbonamenti findUltimaIstanzaByCodice(Session ses,
			String codiceAbbonamento) throws HibernateException {
		//ricerca dell'ultima istanza
		String hql2 = "from IstanzeAbbonamenti as ia " +
				"where ia.abbonamento.codiceAbbonamento = :p1 " +
				"order by ia.fascicoloFine.dataInizio desc";
		Query q2 = ses.createQuery(hql2);
		q2.setString("p1", codiceAbbonamento);
		List<IstanzeAbbonamenti> iaList = (List<IstanzeAbbonamenti>) q2.list();
		if (iaList != null) {
			if (iaList.size() > 0) {
				return iaList.get(0);
			}
		}
		return null;
	}
	
	public IstanzeAbbonamenti createAbbonamentoAndIstanzaByCodiceTipoAbb(Session ses, Integer idAbbonato,
			Integer idPagante, Integer idAgente, Integer idPeriodico, String codiceTipoAbb) throws HibernateException {
		Date today = DateUtil.now();
		Fascicoli fascicoloInizio = new FascicoliDao().findFascicoloByPeriodicoDataInizio(ses,
				idPeriodico, today);
		Listini lst = new ListiniDao().findDefaultListinoByFascicoloInizio(ses, idPeriodico,
				AppConstants.DEFAULT_TIPO_ABBO,
				fascicoloInizio.getId());
		return this.createAbbonamentoAndIstanza(ses,
				idAbbonato, idPagante, idAgente, idPeriodico, lst);	
	}
	
	public IstanzeAbbonamenti createAbbonamentoAndIstanzaByUidListino(Session ses, Integer idAbbonato,
			Integer idPagante, Integer idAgente, Integer idPeriodico, String uidListino) throws HibernateException {
		Listini lst = new ListiniDao().findByUid(ses, uidListino);
		return this.createAbbonamentoAndIstanza(ses,
				idAbbonato, idPagante, idAgente, idPeriodico, lst);	
	}
	
	private IstanzeAbbonamenti createAbbonamentoAndIstanza(Session ses, Integer idAbbonato,
			Integer idPagante, Integer idAgente, Integer idPeriodico, Listini lst)
			throws HibernateException {
		Date today = DateUtil.now();
		//boolean fattura = false;
		Anagrafiche ana = null;
		if (idAbbonato != null) {
			ana = (Anagrafiche)ses.get(Anagrafiche.class, idAbbonato);
			//fattura = ana.getRichiedeFattura();
		}
		Anagrafiche pagante=null;
		if (idPagante != null) {
			pagante = (Anagrafiche)ses.get(Anagrafiche.class, idPagante);
			//fattura = fattura || pagante.getRichiedeFattura();
		}
		Anagrafiche agente = null;
		if (idAgente !=null ) {
			agente = (Anagrafiche)ses.get(Anagrafiche.class, idAgente);
		}

		Periodici periodico = (Periodici) ses.get(Periodici.class, idPeriodico);
		if (periodico == null) {
			List<Periodici> periodiciList = new PeriodiciDao().findByDate(ses, DateUtil.now());
			periodico = periodiciList.get(0);
			idPeriodico = periodico.getId();
		}
		Fascicoli fascicoloInizio = new FascicoliDao().findFascicoloByPeriodicoDataInizio(ses,
				idPeriodico, today);
		
		Abbonamenti abb = new Abbonamenti();
		abb.setDataCreazione(today);
		abb.setDataModifica(today);
		abb.setCodiceAbbonamento("");
		abb.setPeriodico(periodico);
		abb.setIdTipoSpedizione(AppConstants.SPEDIZIONE_POSTA_ORDINARIA);
		
		IstanzeAbbonamenti ia = new IstanzeAbbonamenti();
		ia.setAbbonamento(abb);
		ia.setFascicoloInizio(fascicoloInizio);
		ia.setListino(lst);
		FascicoliBusiness.changePeriodico(ses, ia, idPeriodico, lst.getTipoAbbonamento().getCodice());
		FascicoliBusiness.setupFascicoloFine(ses, ia);
		ia.setCopie(1);
		ia.setFascicoliTotali(lst.getNumFascicoli());
		ia.setDataCreazione(today);
		ia.setDataModifica(today);
		ia.setDataSyncMailing(ServerConstants.DATE_FAR_PAST);
		ia.setDataCambioTipo(today);
		ia.setPagato(false);
		ia.setPropostaAcquisto(false);
		ia.setFatturaDifferita(lst.getFatturaDifferita());
		if (lst.getFatturaDifferita()) ia.setDataSaldo(today);
		ia.setAbbonato(ana);
		ia.setPagante(pagante);
		ia.setPromotore(agente);
		ia.setUltimaDellaSerie(true);
		return ia;
	}
	
	@SuppressWarnings("unchecked")
	public void markUltimaDellaSerie(Session ses, Abbonamenti abb) throws HibernateException {
		//ricerca dell'ultima istanza
		String hql2 = "from IstanzeAbbonamenti as ia where " +
				"ia.abbonamento = :abb " +
				"order by ia.fascicoloFine.dataInizio desc";
		Query q2 = ses.createQuery(hql2);
		q2.setEntity("abb", abb);
		List<IstanzeAbbonamenti> iaList = (List<IstanzeAbbonamenti>) q2.list();
		IstanzeAbbonamenti last = null;
		if (iaList != null) {
			if (iaList.size() > 0) {
				last = iaList.get(0);
			}
		}
		if (last != null) {
			//se esiste una ultima istanza => viene impostata come "più recente"
			last.setUltimaDellaSerie(Boolean.TRUE);
			updateUnlogged(ses, last);
			//tutte le istanze sono inattivate
			String hql1 = "update IstanzeAbbonamenti set ultimaDellaSerie = :ia " +
					"where abbonamento = :abb and id <> :lastId";
			Query q1 = ses.createQuery(hql1);
			q1.setBoolean("ia", Boolean.FALSE);
			q1.setEntity("abb", abb);
			q1.setInteger("lastId", last.getId());
			q1.executeUpdate();
		}
	}
	
	
	//public Abbonamenti checkPagatoCreditoResiduo(Session ses, IstanzeAbbonamenti ia, Utenti utente) throws ValidationException {
	//	if (!ia.getIstanzaPiuRecente()) {
	//		throw new ValidationException("IstanzeAbbonamenti [id="+ia.getId()+"] non e' l'istanza piu' recente");
	//	}
	//	List<Pagamenti> pList = (List<Pagamenti>) findByProperty(ses, Pagamenti.class, "istanzaAbbonamento", ia);
	//	ia.setUtente(utente);
	//	double pagato = 0D;
	//	for (Pagamenti p:pList) {
	//		pagato += p.getImporto();
	//	}
	//	if (pagato == ia.getTipoAbbonamentoListino().getPrezzo().doubleValue()) {
	//		ia.setPagato(true);
	//		ia.getAbbonamento().setCreditoResiduo(0D);
	//	}
	//	if (pagato > ia.getTipoAbbonamentoListino().getPrezzo().doubleValue()) {
	//		ia.setPagato(true);
	//		ia.getAbbonamento().setCreditoResiduo(pagato-ia.getTipoAbbonamentoListino().getPrezzo().doubleValue());
	//	}
	//	if (pagato < ia.getTipoAbbonamentoListino().getPrezzo().doubleValue()) {
	//		ia.setPagato(false);
	//		ia.getAbbonamento().setCreditoResiduo(0D);
	//	}
	//	update(ses, ia.getId(), ia);
	//	update(ses, ia.getAbbonamento().getId(), ia.getAbbonamento());
	//	return ia.getAbbonamento();
	//}
	
	@SuppressWarnings("unchecked")
	public Integer countIstanzeByCodice(Session ses, String codice) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "select count(ia.id) from IstanzeAbbonamenti ia");
		qf.addWhere("ia.abbonamento.codiceAbbonamento = :p1");
		qf.addParam("p1", codice);
		Query q = qf.getQuery();
		Long count = null;
		List<Object> list = (List<Object>) q.list();
		if (list != null) {
			if (list instanceof List) {
				if (list.size() > 0) {
					count = (Long) list.get(0);
				}
			}
		}
		if (count == null) throw new HibernateException("It's impossible to count IstanzeAbbonamenti");
		return count.intValue();
	}
	
	@SuppressWarnings("unchecked")
	public Integer countIstanzeByTipoAbbonamento(Session ses, Integer idTipoAbbonamento, Date date) throws HibernateException {
		String hql = "select count(ia.id) from IstanzeAbbonamenti ia where "+
				"ia.listino.tipoAbbonamento.id = :id1 and "+
				"ia.fascicoloInizio.dataInizio <= :dt1 and "+
				"ia.fascicoloFine.dataFine >= :dt2 and "+
				"ia.invioBloccato = :b1 and "+//FALSE
				"(ia.pagato = :b2 or "+
					"ia.fatturaDifferita = :b3 or "+
					"ia.listino.fatturaDifferita = :b4 or "+
					"ia.listino.prezzo <= :d1)";//TRUE TRUE TRUE SOGLIA
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idTipoAbbonamento, IntegerType.INSTANCE);
		q.setParameter("dt1", date, DateType.INSTANCE);
		q.setParameter("dt2", date, DateType.INSTANCE);
		q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
		q.setParameter("b2", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b3", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b4", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("d1", AppConstants.SOGLIA, DoubleType.INSTANCE);
		Long count = null;
		List<Object> list = (List<Object>) q.list();
		if (list != null) {
			if (list instanceof List) {
				if (list.size() > 0) {
					count = (Long) list.get(0);
				}
			}
		}
		if (count == null) throw new HibernateException("It's impossible to count IstanzeAbbonamenti");
		return count.intValue();
	}
	
	/** Cerca le istanze che hanno ricevuto il fascicoloSpedito,
	 *  e che NON HANNO RICEVUTO la Comunicazione com
	 */
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findIstanzeByFascicoloInizioMissingComunicazione(Session ses,
			Integer idFascicoloSpedito, TipiAbbonamento ta,
			Comunicazioni com, String tagOpzione)
			throws HibernateException {
		Integer delta = com.getNumeriDaInizioOFine();
		//Cerca il fascicolo iniziale delle istanze che dovrebbero avere
		//una comunicazione in corrispondenza di fas
		Fascicoli fasInizio = new FascicoliDao().findFascicoliBeforeFascicolo(ses, idFascicoloSpedito, delta);
		String qs = "from IstanzeAbbonamenti ia ";
		if (tagOpzione != null) qs += "join ia.opzioniIstanzeAbbonamentiSet sl "; 
		qs += "where ";
		if (com.getRichiestaRinnovo()) qs += "(ia.pagato = :b21 or ia.fatturaDifferita = :b22) and ";
		if (com.getSoloNonPagati()) qs += "ia.pagato = :b31 and ia.fatturaDifferita = :b32 and ";
		if (com.getSoloPiuCopie()) qs += "ia.copie > :i2 and ";
		if (com.getSoloConPagante()) qs += "ia.pagante is not null and ";
		if (com.getSoloSenzaPagante()) qs += "ia.pagante is null and ";
		qs += "ia.fascicoloInizio.id = :id2 and " +//condizione fascicolo iniziale
				"ia.invioBloccato = :b1 and " +//false, condizione abbonamento non bloccato
				"ia.dataDisdetta is null and " +
				"ia.listino.tipoAbbonamento.id = :id3 and " +
				"(select count(ec.id) from EvasioniComunicazioni ec where " +
					"ec.istanzaAbbonamento.id = ia.id and " +
					"ec.comunicazione.id = :id4" +
				") = :i1 ";//condizione evasione com. mancante
		if (tagOpzione != null) {
			qs += "and sl.tag = :tag ";//il opzione deve avere il tag
		}
		qs += "order by ia.id asc";
		Query q = ses.createQuery(qs);
		if (com.getRichiestaRinnovo()) {
			q.setParameter("b21", Boolean.TRUE);
			q.setParameter("b22", Boolean.TRUE);
		}
		if (com.getSoloNonPagati()) {
			q.setParameter("b31", Boolean.FALSE);
			q.setParameter("b32", Boolean.FALSE);
		}
		if (com.getSoloPiuCopie()) q.setParameter("i2", 1, IntegerType.INSTANCE);
		q.setParameter("id2", fasInizio.getId(), IntegerType.INSTANCE);
		q.setParameter("b1", Boolean.FALSE);
		q.setParameter("id3", ta.getId(), IntegerType.INSTANCE);
		q.setParameter("id4", com.getId(), IntegerType.INSTANCE);
		q.setParameter("i1", 0, IntegerType.INSTANCE);
		if (tagOpzione != null) q.setParameter("tag", tagOpzione, StringType.INSTANCE);
		//q.setFirstResult(offset);
		//q.setMaxResults(pageSize);
		List<IstanzeAbbonamenti> iaList = (List<IstanzeAbbonamenti>) q.list();
		return iaList;
	}
	
	
	///** Cerca le istanze che hanno ricevuto il fascicoloEvaso (regolare),
	// *  che iniziano con il fascicoloInizio e che NON HANNO RICEVUTO la
	// *  Comunicazione com
	// */
	//@SuppressWarnings("unchecked")
	//public List<IstanzeAbbonamenti> findIstanzeByFasEvasoFasInizioMissingComunicazione(Session ses,
	//		Integer idFascicoloEvaso, Integer idFascicoloInizio, TipiAbbonamento ta,
	//		Comunicazioni com, String tagOpzione/*, int offset, int pageSize*/)
	//		throws HibernateException {
	//	String qs = "select ia from EvasioniFascicoli ef, IstanzeAbbonamenti ia ";
	//	if (tagOpzione != null) qs += "join ia.opzioniIstanzeAbbonamentiSet sl "; 
	//	qs += "where " +
	//			"ef.idIstanzaAbbonamento = ia.id and " +//join condition
	//			"ef.fascicolo.id = :id1 and " +//condizione fascicolo ricevuto
	//			"ef.idTipoEvasione = :s1 and " +//condizione fascicolo regolare
	//			"ia.fascicoloInizio.id = :id2 and " +//condizione fascicolo iniziale
	//			"ia.invioBloccato = :b1 and " +//false, condizione abbonamento non bloccato
	//			"ia.listino.tipoAbbonamento.id = :id3 and " +
	//			"(select count(ec.id) from EvasioniComunicazioni ec where " +
	//				"ec.istanzaAbbonamento.id = ia.id and " +
	//				"ec.comunicazione.id = :id4" +
	//			") = :i1 ";//condizione evasione com. mancante
	//	if (tagOpzione != null) {
	//		qs += "and sl.tag = :tag ";//il opzione deve avere il tag
	//	}
	//	qs += "order by ia.id asc";
	//	Query q = ses.createQuery(qs);
	//	q.setParameter("id1", idFascicoloEvaso, IntegerType.INSTANCE);
	//	q.setParameter("id2", idFascicoloInizio, IntegerType.INSTANCE);
	//	q.setParameter("b1", Boolean.FALSE);
	//	q.setParameter("id3", ta.getId(), IntegerType.INSTANCE);
	//	q.setParameter("id4", com.getId(), IntegerType.INSTANCE);
	//	q.setParameter("s1", AppConstants.EVASIONE_FAS_REGOLARE);
	//	q.setParameter("i1", 0, IntegerType.INSTANCE);
	//	if (tagOpzione != null) q.setParameter("tag", tagOpzione, StringType.INSTANCE);
	//	//q.setFirstResult(offset);
	//	//q.setMaxResults(pageSize);
	//	List<IstanzeAbbonamenti> iaList = (List<IstanzeAbbonamenti>) q.list();
	//	return iaList;
	//}

	/** Cerca le istanze che hanno ricevuto il fascicoloSpedito,
	 *  e che NON HANNO RICEVUTO la Comunicazione com
	 */
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findIstanzeByFascicoloFineMissingComunicazione(Session ses,
			Integer idFascicoloSpedito, TipiAbbonamento ta,
			Comunicazioni com, String tagOpzione)
			throws HibernateException {
		Integer delta = com.getNumeriDaInizioOFine();
		//Cerca il fascicolo finale delle istanze che dovrebbero avere
		//una comunicazione in corrispondenza di fas
		Fascicoli fasFine = new FascicoliDao().findFascicoliBeforeFascicolo(ses, idFascicoloSpedito, delta);
		String qs = "from IstanzeAbbonamenti ia ";
		if (tagOpzione != null) qs += "join ia.opzioniIstanzeAbbonamentiSet sl "; 
		qs += "where ";
		if (com.getRichiestaRinnovo()) qs += "(ia.pagato = :b21 or ia.fatturaDifferita = :b22) and ";
		if (com.getSoloNonPagati()) qs += "ia.pagato = :b31 and ia.fatturaDifferita = :b32 and ";
		if (com.getSoloPiuCopie()) qs += "ia.copie > :i2 and ";
		if (com.getSoloConPagante()) qs += "ia.pagante is not null and ";
		if (com.getSoloSenzaPagante()) qs += "ia.pagante is null and ";
		qs += "ia.fascicoloFine.id = :id2 and " +//condizione fascicolo finale
				"ia.invioBloccato = :b1 and " +//false, condizione abbonamento non bloccato
				"ia.dataDisdetta is null and " +
				"ia.ultimaDellaSerie = :b4 and "+
				"ia.listino.tipoAbbonamento.id = :id3 and " +
				"(select count(ec.id) from EvasioniComunicazioni ec where " +
					"ec.istanzaAbbonamento.id = ia.id and " +
					"ec.comunicazione.id = :id4" +
				") = :i1 ";//condizione evasione com. mancante
		if (tagOpzione != null) {
			qs += "and sl.tag = :tag ";//il opzione deve avere il tag
		}
		qs += "order by ia.id asc";
		Query q = ses.createQuery(qs);
		if (com.getRichiestaRinnovo()) {
			q.setParameter("b21", Boolean.TRUE);
			q.setParameter("b22", Boolean.TRUE);
		}
		if (com.getSoloNonPagati()) {
			q.setParameter("b31", Boolean.FALSE);
			q.setParameter("b32", Boolean.FALSE);
		}
		if (com.getSoloPiuCopie()) q.setParameter("i2", 1, IntegerType.INSTANCE);
		q.setParameter("id2", fasFine.getId(), IntegerType.INSTANCE);
		q.setParameter("b1", Boolean.FALSE);
		q.setParameter("b4", Boolean.TRUE);
		q.setParameter("id3", ta.getId(), IntegerType.INSTANCE);
		q.setParameter("id4", com.getId(), IntegerType.INSTANCE);
		q.setParameter("i1", 0, IntegerType.INSTANCE);
		if (tagOpzione != null) q.setParameter("tag", tagOpzione, StringType.INSTANCE);
		//q.setFirstResult(offset);
		//q.setMaxResults(pageSize);
		List<IstanzeAbbonamenti> iaList = (List<IstanzeAbbonamenti>) q.list();
		return iaList;
	}
	
	///** Cerca le istanze che hanno ricevuto il fascicoloEvaso,
	// *  che finiscono con il fascicoloFine e che NON HANNO RICEVUTO la
	// *  Comunicazione com
	// */
	//@SuppressWarnings("unchecked")
	//public List<IstanzeAbbonamenti> findIstanzeByFasEvasoFasFineMissingComunicazione(Session ses,
	//		Integer idFascicoloEvaso, Integer idFascicoloFine, TipiAbbonamento ta,
	//		Comunicazioni com, String tagOpzione/*, int offset, int pageSize*/)
	//		throws HibernateException {
	//	String qs = "select ia from EvasioniFascicoli ef, IstanzeAbbonamenti ia ";
	//	if (tagOpzione != null) qs += "join ia.opzioniIstanzeAbbonamentiSet sl "; 
	//	qs += "where " +
	//			"ef.idIstanzaAbbonamento = ia.id and " +//join condition
	//			"ef.fascicolo.id = :id1 and " +//condizione fascicolo ricevuto
	//			"ef.idTipoEvasione = :s1 and " +//condizione fascicolo regolare
	//			"ia.fascicoloFine.id = :id2 and " +//condizione fascicolo finale
	//			"ia.invioBloccato = :b1 and " +//false, condizione abbonamento non bloccato
	//			"ia.listino.tipoAbbonamento.id = :id3 and " +
	//			"(select count(ec.id) from EvasioniComunicazioni ec where " +
	//				"ec.istanzaAbbonamento.id = ia.id and " +
	//				"ec.comunicazione.id = :id4" +
	//			") = :i1 ";//condizione evasione com. mancante
	//	if (tagOpzione != null) {
	//		qs += "and sl.tag = :tag ";//il opzione deve avere il tag
	//	}
	//	qs += "order by ia.id asc";
	//	Query q = ses.createQuery(qs);
	//	q.setParameter("id1", idFascicoloEvaso, IntegerType.INSTANCE);
	//	q.setParameter("id2", idFascicoloFine, IntegerType.INSTANCE);
	//	q.setParameter("b1", Boolean.FALSE);
	//	q.setParameter("id3", ta.getId(), IntegerType.INSTANCE);
	//	q.setParameter("id4", com.getId(), IntegerType.INSTANCE);
	//	q.setParameter("s1", AppConstants.EVASIONE_FAS_REGOLARE);
	//	q.setParameter("i1", 0, IntegerType.INSTANCE);
	//	if (tagOpzione != null) q.setParameter("tag", tagOpzione, StringType.INSTANCE);
	//	//q.setFirstResult(offset);
	//	//q.setMaxResults(pageSize);
	//	List<IstanzeAbbonamenti> iaList = (List<IstanzeAbbonamenti>) q.list();
	//	return iaList;
	//}
	
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findModifiedSinceDate(Session ses,
			Date startDate,  int offset, int pageSize)
			throws HibernateException {
		String qs = "from IstanzeAbbonamenti ia where " +
				"ia.dataModifica >= :dt1 " +
				"order by ia.dataModifica desc";
		Query q = ses.createQuery(qs);
		q.setParameter("dt1", startDate, DateType.INSTANCE);
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
		if (abbList != null) {
			if (abbList.size() > 0) {
				return abbList;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findOrderByLastModified(Session ses,
			Integer idPeriodico,  int offset, int pageSize)
			throws HibernateException {
		String qs = "from IstanzeAbbonamenti ia where " +
				"ia.abbonamento.periodico.id = :id1 " +
				"order by ia.dataModifica desc";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
		if (abbList != null) {
			if (abbList.size() > 0) {
				return abbList;
			}
		}
		return null;
	}

	public Integer save(Session ses, IstanzeAbbonamenti item, boolean handleEvasioniAndArretrati)
				throws HibernateException {
		Integer idIa = null;
		Date now = DateUtil.now();
		IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
		EvasioniFascicoliDao efDao = new EvasioniFascicoliDao();
		Integer idAbb = null;
		try {
			//Se chiamato da UI i valori ottenuti dai idT devono andare in sostituzione dei valori esistenti:
			boolean replaceWithEmpty = false;
			//Associa Tipo Abbonamento
			if (item.getIdListinoT() != null) {
				replaceWithEmpty = true;//perché questo metodo è chiamato dalla UI
				Listini listinoNew = GenericDao.findById(ses, Listini.class,
						ValueUtil.stoi(item.getIdListinoT()));
				item.setListino(listinoNew);
				item.setDataCambioTipo(now);
			}
			//Associa abbonato
			Integer idAbbonato = ValueUtil.stoi(item.getIdAbbonatoT());
			Anagrafiche abbonato = item.getAbbonato();
			if (idAbbonato != null) {
				abbonato = GenericDao.findById(ses, Anagrafiche.class, idAbbonato);
				item.setAbbonato(abbonato);
			} else {
				//Verifica anagrafiche
				if (replaceWithEmpty) throw new BusinessException("L'abbonato deve essere definito");
			}
			//Associa promotore
			Integer idPromotore = ValueUtil.stoi(item.getIdPromotoreT());
			if (replaceWithEmpty && (idPromotore == null)) {
				item.setPromotore(null);
			}
			if (idPromotore != null) {
				Anagrafiche promotore = GenericDao.findById(ses, Anagrafiche.class, idPromotore);
				item.setPromotore(promotore);
			}
			//Associa pagante
			Integer idPagante = ValueUtil.stoi(item.getIdPaganteT());
			if (replaceWithEmpty && (idPagante == null)) {
				item.setPagante(null);
			}
			if (idPagante != null) {
				Anagrafiche pagante = GenericDao.findById(ses, Anagrafiche.class, idPagante);
				item.setPagante(pagante);
			}
			//Recupera e aggiorna il periodico
			Abbonamenti abb = item.getAbbonamento();
			Integer idPeriodico = ValueUtil.stoi(abb.getIdPeriodicoT());
			if (idPeriodico != null) {
				Periodici periodico = GenericDao.findById(ses, Periodici.class, idPeriodico);
				abb.setPeriodico(periodico);
			} else {
				idPeriodico = abb.getPeriodico().getId();
			}
			//Assegna un CodiceAbbonamento
			if (item.getAbbonamento().getCodiceAbbonamento() == null) 
				item.getAbbonamento().setCodiceAbbonamento("");
			if (item.getAbbonamento().getCodiceAbbonamento().length() == 0){
				String codiceAbbonamento = new ContatoriDao().createCodiceAbbonamento(ses, idPeriodico);
				item.getAbbonamento().setCodiceAbbonamento(codiceAbbonamento);
			}
			
			// *** SAVE ABBONAMENTO ***
			if (abb.getDataCreazione() == null) abb.setDataCreazione(now);
			idAbb = (Integer) new AbbonamentiDao().save(ses, abb);

			Abbonamenti abbPersist = GenericDao.findById(ses, Abbonamenti.class, idAbb);
			//Altre proprietà
			Fascicoli fasInizio = item.getFascicoloInizio();
			Fascicoli fasFine = item.getFascicoloFine();
			if ((item.getIdFascicoloFineT() != null) && (item.getIdFascicoloInizioT() != null)) {
				fasInizio = (Fascicoli) ses.get(Fascicoli.class, ValueUtil.stoi(item.getIdFascicoloInizioT()));
				fasFine = (Fascicoli) ses.get(Fascicoli.class, ValueUtil.stoi(item.getIdFascicoloFineT()));
				item.setFascicoloInizio(fasInizio);
				item.setFascicoloFine(fasFine);
				if (fasInizio.getDataInizio().getTime() >= fasFine.getDataInizio().getTime()) {
					throw new BusinessException("Errore nei fascicoli di inizio/fine");
				}
			} else {
				if (replaceWithEmpty) throw new BusinessException("I fascicoli inizio/fine devono essere definiti");
			}
			//Conta il totale fascicoli
			List<Fascicoli> fList = new FascicoliDao().findFascicoliBetweenDates(ses, item.getAbbonamento().getPeriodico().getId(),
					fasInizio.getDataInizio(), fasFine.getDataInizio());
			int totFascicoli = 0;
			for (Fascicoli fas:fList) {
				totFascicoli += fas.getFascicoliAccorpati();
			}
			item.setFascicoliTotali(totFascicoli);
			
			// *** SAVE ISTANZA ***
			IstanzeAbbonamenti persistedIa = null;
			//CREA
			//abbina l'abbonamento
			item.setAbbonamento(abbPersist);
			if ((item.getDataSaldo() == null) && item.getFatturaDifferita()) {
				item.setDataSaldo(now);
			}
			//salva
			idIa = (Integer) GenericDao.saveGeneric(ses, item);
			LogEditingDao.writeEditingLog(ses, IstanzeAbbonamenti.class, idIa, 
					idIa+"", item.getIdUtente());
			//Forza i opzioni obbligatori, se ci sono
			persistedIa = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIa);
			ses.refresh(persistedIa);
			//Se NON sono stati abbinate delle opzioni, abbina quelle obbligatorie
			if (item.getIdOpzioniIstanzeAbbonamentiSetT() == null) item.setIdOpzioniIstanzeAbbonamentiSetT(new HashSet<Integer>());
			if (item.getIdOpzioniIstanzeAbbonamentiSetT().size() == 0) {
				OpzioniUtil.addOpzioniObbligatorie(ses, persistedIa, false);
			}
			//Associa Opzioni
			Set<Opzioni> opzSet = new HashSet<Opzioni>();
			if (item.getIdOpzioniIstanzeAbbonamentiSetT() != null) {
				for (Integer idOpz:item.getIdOpzioniIstanzeAbbonamentiSetT()) {
					Opzioni opz = GenericDao.findById(ses, Opzioni.class, idOpz);
					opzSet.add(opz);
				}
			}
			OpzioniUtil.replaceOpzioni(ses, persistedIa, opzSet, false);
			//Imposta come recente solo l'ultima istanza della serie di abbonamenti
			iaDao.markUltimaDellaSerie(ses, abbPersist);
			if (handleEvasioniAndArretrati) {
				//Aggancia a questa istanza tutti i fascicoli già spediti tra inizio e fine
				efDao.reattachEvasioniFascicoliToIstanza(ses, persistedIa);
				//Forza evantuali articoli obbligatori
				new EvasioniArticoliDao().reattachEvasioniArticoliToInstanza(ses,
						persistedIa, persistedIa.getIdUtente());
				//Aggiunge fascicoli mancanti (Attenzione non gestisce il pagamento o meno)
				efDao.enqueueMissingArretratiByStatus(ses, persistedIa, persistedIa.getIdUtente());
			}
			//Aggiorna con ultime modifiche
			iaDao.updateUnlogged(ses, persistedIa);
			//Aggiorna cache
			try {
				CacheBusiness.saveOrUpdateCache(ses, persistedIa.getAbbonato(), true);
				if (persistedIa.getPagante() != null)
						CacheBusiness.saveOrUpdateCache(ses, persistedIa.getPagante(), true);
			} catch (BusinessException e) {
				throw new HibernateException(e.getMessage(), e);
			}
		} catch (BusinessException e) {
			throw new HibernateException(e.getMessage(), e);
		}
		return idIa;
	}
	
	public Integer update(Session ses, IstanzeAbbonamenti item, boolean calledByUi)
					throws BusinessException {
		Date now = DateUtil.now();
		IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
		EvasioniFascicoliDao efDao = new EvasioniFascicoliDao();
		Integer idAbb = null;
		Integer idIa = null;
		//Associa abbonato
		Integer idAbbonato = ValueUtil.stoi(item.getIdAbbonatoT());
		Anagrafiche abbonato = item.getAbbonato();
		if (idAbbonato != null) {
			abbonato = GenericDao.findById(ses, Anagrafiche.class, idAbbonato);
			item.setAbbonato(abbonato);
		} else {
			//Verifica anagrafiche
			if (calledByUi) throw new BusinessException("L'abbonato deve essere definito");
		}
		//Associa promotore
		Integer idPromotore = ValueUtil.stoi(item.getIdPromotoreT());
		if (calledByUi && (idPromotore == null)) {
			item.setPromotore(null);
		}
		if (idPromotore != null) {
			Anagrafiche promotore = GenericDao.findById(ses, Anagrafiche.class, idPromotore);
			item.setPromotore(promotore);
		}
		//Associa pagante
		Integer idPagante = ValueUtil.stoi(item.getIdPaganteT());
		if (calledByUi && (idPagante == null)) {
			item.setPagante(null);
		}
		if (idPagante != null) {
			Anagrafiche pagante = GenericDao.findById(ses, Anagrafiche.class, idPagante);
			item.setPagante(pagante);
		}
		//Recupera e aggiorna il periodico
		//Abbonamenti abb = item.getAbbonamento();
		//Integer idPeriodico = ValueUtil.stoi(abb.getIdPeriodicoT());
		//if (idPeriodico != null) {
		//	Periodici periodico = GenericDao.findById(ses, Periodici.class, idPeriodico);
		//	abb.setPeriodico(periodico);
		//}
		
		// *** UPDATE  ABBONAMENTO ***
		new AbbonamentiDao().update(ses, item.getAbbonamento());
		idAbb = item.getAbbonamento().getId();
		Abbonamenti abbPersist = GenericDao.findById(ses, Abbonamenti.class, idAbb);
		//Fascicoli 
		Fascicoli fasInizio = item.getFascicoloInizio();
		Fascicoli fasFine = item.getFascicoloFine();
		if ((item.getIdFascicoloFineT() != null) && (item.getIdFascicoloInizioT() != null)) {
			fasInizio = (Fascicoli) ses.get(Fascicoli.class, ValueUtil.stoi(item.getIdFascicoloInizioT()));
			fasFine = (Fascicoli) ses.get(Fascicoli.class, ValueUtil.stoi(item.getIdFascicoloFineT()));
			item.setFascicoloInizio(fasInizio);
			item.setFascicoloFine(fasFine);
			if (fasInizio.getDataInizio().getTime() >= fasFine.getDataInizio().getTime()) {
				throw new BusinessException("Errore nei fascicoli di inizio/fine");
			}
		} else {
			if (calledByUi) throw new BusinessException("I fascicoli inizio/fine devono essere definiti");
		}
		//Conta il totale fascicoli
		List<Fascicoli> fList = new FascicoliDao().findFascicoliBetweenDates(ses, item.getAbbonamento().getPeriodico().getId(),
				fasInizio.getDataInizio(), fasFine.getDataInizio());
		int totFascicoli = 0;
		for (Fascicoli fas:fList) {
			totFascicoli += fas.getFascicoliAccorpati();
		}
		item.setFascicoliTotali(totFascicoli);
		
		// *** UPDATE ISTANZA ***
		IstanzeAbbonamenti persistedIa = null;
		if ((item.getDataSaldo() == null) &&
				(item.getFatturaDifferita() || item.getListino().getFatturaDifferita())) {
			item.setDataSaldo(now);
		}
		//AGGIORNA
		iaDao.updateUnlogged(ses, item);
		idIa = item.getId();
		persistedIa = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIa);

		//Associa Opzioni
		if (calledByUi) {
			Set<Opzioni> opzSet = new HashSet<Opzioni>();
			if (item.getIdOpzioniIstanzeAbbonamentiSetT() != null) {
				for (Integer idOpz:item.getIdOpzioniIstanzeAbbonamentiSetT()) {
					Opzioni opz = GenericDao.findById(ses, Opzioni.class, idOpz);
					opzSet.add(opz);
				}
			}
			OpzioniUtil.replaceOpzioni(ses, persistedIa, opzSet, false);
		}
		//Imposta come recente solo l'ultima istanza della serie di abbonamenti
		iaDao.markUltimaDellaSerie(ses, abbPersist);
		//Aggancia a questa istanza tutti i fascicoli tra inizio e fine
		efDao.reattachEvasioniFascicoliToIstanza(ses, 
				persistedIa);
		//Forza evantuali articoli obbligatori
		new EvasioniArticoliDao().reattachEvasioniArticoliToInstanza(ses,
				persistedIa, persistedIa.getIdUtente());
		new EvasioniFascicoliDao().enqueueMissingArretratiByStatus(ses, 
				persistedIa, persistedIa.getIdUtente());
		//Aggiorna con ultime modifiche
		iaDao.update(ses, persistedIa);
		//Aggiorna cache
		try {
			CacheBusiness.saveOrUpdateCache(ses, persistedIa.getAbbonato(), true);
			if (persistedIa.getPagante() != null)
					CacheBusiness.saveOrUpdateCache(ses, persistedIa.getPagante(), true);
		} catch (BusinessException e) {
			throw new HibernateException(e.getMessage(), e);
		}
		return idIa;
	}
	
	
	
	
	
	//metodi con SQL
	
	public void sqlUpdateFascicoliSpediti(Session ses, IstanzeAbbonamenti ia,
			Integer numFas) throws HibernateException {
		String sql = "update istanze_abbonamenti as ia set " +
				"ia.fascicoli_spediti=:i1 " +
				"where " +
				"ia.id=:id1 ";
		Query q = ses.createSQLQuery(sql);
		q.setInteger("i1", numFas);
		q.setInteger("id1", ia.getId());
		q.executeUpdate();
	}
	
		
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findIstanzeByMissingCodFiscOnCreation(Session ses, 
			Date sogliaDt) throws HibernateException {
		String hql = "select ia "+
				"from IstanzeAbbonamenti ia left outer join ia.pagante pgnt where " +
				"ia.ultimaDellaSerie = :b1 and " +//TRUE
				"ia.invioBloccato = :b2 and " +//FALSE
				"ia.dataCreazione >= :dt1 and " +
				"ia.listino.prezzo > :d1 and " +
				"( "+
					"("+// se esiste pagante => deve avere email e codFisc
						"pgnt is not null and "+
						//non deve già esistere un feedback
						"pgnt.id not in (select fa.idAnagrafica from FeedbackAnagrafiche fa where fa.idAnagrafica = pgnt.id) and "+
						"(pgnt.emailPrimaria is not null and pgnt.emailPrimaria not like :s11) and "+// Email presente
						"(pgnt.codiceFiscale is null or pgnt.codiceFiscale like :s12) "+// Cod Fisc NON presente
					") or ("+ // se non ha pagante => deve avere email e codFisc nell'anagrafica
						"ia.pagante is null and "+
						//non deve già esistere un feedback
						"ia.abbonato.id not in (select fa.idAnagrafica from FeedbackAnagrafiche fa where fa.idAnagrafica = ia.abbonato.id) and "+
						"(ia.abbonato.emailPrimaria is not null and ia.abbonato.emailPrimaria not like :s21) and "+// Email presente
						"(ia.abbonato.codiceFiscale is null or ia.abbonato.codiceFiscale like :s22) "+// Cod Fisc NON presente
					") "+
				") "+
				"group by ia.id order by ia.id";
		Query q = ses.createQuery(hql);
		q.setBoolean("b1", Boolean.TRUE);
		q.setBoolean("b2", Boolean.FALSE);
		q.setTimestamp("dt1", sogliaDt);
		q.setParameter("d1", AppConstants.SOGLIA, DoubleType.INSTANCE);
		q.setParameter("s11", "", StringType.INSTANCE);
		q.setParameter("s12", "", StringType.INSTANCE);
		q.setParameter("s21", "", StringType.INSTANCE);
		q.setParameter("s22", "", StringType.INSTANCE);
		List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
		return abbList;
	}
	
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> findIstanzeByMissingCodFiscOnPayment(Session ses,
			Date sogliaDt) throws HibernateException {
		String hql = "select ia "+
				"from IstanzeAbbonamenti ia left outer join ia.pagante pgnt where " +
				"ia.ultimaDellaSerie = :b1 and " +//TRUE
				"ia.invioBloccato = :b2 and " +//FALSE
				"ia.dataSaldo >= :dt1 and " +
				"ia.pagato = :b3 and " +//TRUE
				"ia.listino.prezzo > :d1 and "+
				"( "+
					"( "+// se esiste pagante => deve avere email e codFisc
						"pgnt is not null and "+
						//non deve già esistere un feedback
						"pgnt.id not in (select fa.idAnagrafica from FeedbackAnagrafiche fa where fa.idAnagrafica = pgnt.id) and "+
						"(pgnt.emailPrimaria is not null and pgnt.emailPrimaria not like :s11) and "+// Email presente
						"(pgnt.codiceFiscale is null or pgnt.codiceFiscale like :s12) "+// Cod Fisc NON presente
					") or ("+ // se non ha pagante => deve avere email e codFisc nell'anagrafica
						"ia.pagante is null and "+
						//non deve già esistere un feedback
						"ia.abbonato.id not in (select fa.idAnagrafica from FeedbackAnagrafiche fa where fa.idAnagrafica = ia.abbonato.id) and "+
						"(ia.abbonato.emailPrimaria is not null and ia.abbonato.emailPrimaria not like :s21) and "+// Email presente
						"(ia.abbonato.codiceFiscale is null or ia.abbonato.codiceFiscale like :s22) "+// Cod Fisc NON presente
					") "+
				") "+
				"group by ia.id order by ia.id";
		Query q = ses.createQuery(hql);
		q.setBoolean("b1", Boolean.TRUE);
		q.setBoolean("b2", Boolean.FALSE);
		q.setDate("dt1", sogliaDt);
		q.setBoolean("b3", Boolean.TRUE);
		q.setParameter("d1", AppConstants.SOGLIA, DoubleType.INSTANCE);
		q.setParameter("s11", "", StringType.INSTANCE);
		q.setParameter("s12", "", StringType.INSTANCE);
		q.setParameter("s21", "", StringType.INSTANCE);
		q.setParameter("s22", "", StringType.INSTANCE);
		List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
		return abbList;
	}
	

	
}
