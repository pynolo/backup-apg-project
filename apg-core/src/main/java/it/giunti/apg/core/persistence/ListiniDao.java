package it.giunti.apg.core.persistence;

import it.giunti.apg.core.OpzioniUtil;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Opzioni;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

public class ListiniDao implements BaseDao<Listini> {

	@Override
	public void update(Session ses, Listini instance) throws HibernateException {
		if (instance.getUid() == null) {
			instance.setUid(createUidListino(instance));
		}
		GenericDao.updateGeneric(ses, instance.getId(), instance);
		LogEditingDao.writeEditingLog(ses, Listini.class, instance.getId(), 
				instance.getUid(), instance.getIdUtente());
	}

	@Override
	public Serializable save(Session ses, Listini transientInstance)
			throws HibernateException {
		Integer id = (Integer)GenericDao.saveGeneric(ses, transientInstance);
		update(ses, transientInstance);//Per assegnare il codice
		//EditLogDao.writeEditLog(ses, Listini.class, id, transientInstance.getUtente());
		return id;
	}

	@Override
	public void delete(Session ses, Listini instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	public String createUidListino(Listini lst) {
		if (lst.getId() == null || lst.getTipoAbbonamento() == null) return null;
		String result = lst.getTipoAbbonamento().getCodice().substring(0, 2);
		String hexStr = "0000"+Integer.toString(lst.getId(),16).toUpperCase();
		result += hexStr.substring(hexStr.length()-4);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Listini findByUid(Session ses, String codice) throws HibernateException {
		String qs = "from Listini lst where " +
				"lst.uid = :s1 "+
				"order by lst.id desc";
		Query q = ses.createQuery(qs);
		q.setParameter("s1", codice, StringType.INSTANCE);
		List<Listini> list = q.list();
		if (list != null) {
			if (list.size()>0) {
				return list.get(0);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Listini> findListiniByPeriodicoDate(Session ses,
			Integer idPeriodico, Date date, Integer selectedId, int offset, int pageSize) throws HibernateException {
		if (idPeriodico == null) return null;
		String hql = "from Listini lst where "+
			"( "+
				"( "+
					"lst.dataInizio <= :dt1 and "+
					"((lst.dataFine >= :dt2) or (lst.dataFine is null)) "+
				") ";
		if (selectedId != null) hql += "or (lst.id = :id2) ";
		hql += ") and "+
			"lst.tipoAbbonamento.periodico.id = :id1 "+
			"order by lst.tipoAbbonamento.codice asc, lst.tipoAbbonamento.nome asc ";
		
		Query q = ses.createQuery(hql);
		q.setParameter("dt1", date, DateType.INSTANCE);
		q.setParameter("dt2", date, DateType.INSTANCE);
		if (selectedId != null) q.setParameter("id2", selectedId, IntegerType.INSTANCE);
		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		q.setMaxResults(pageSize);
		q.setFirstResult(offset);
		List<Listini> lstList = (List<Listini>) q.list();
		return lstList;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Listini> findListiniByFascicoloInizio(Session ses,
			Integer idPeriodico, Integer idFascicolo, Integer selectedId, int offset, int pageSize) throws HibernateException {
		if (idPeriodico == null) return null;
		String hql = "select lst from Listini lst, Fascicoli fi where "+
			"fi.id = :id3 and "+
			"( "+
				"( "+
					"lst.dataInizio <= fi.dataInizio and "+
					"((lst.dataFine >= fi.dataInizio) or (lst.dataFine is null))"+
				") ";
		if (selectedId != null) hql += "or (lst.id = :id2) ";
		hql += ") and "+
			"lst.tipoAbbonamento.periodico.id = :id1 "+
			"order by lst.tipoAbbonamento.codice asc, lst.tipoAbbonamento.nome asc ";
		
		Query q = ses.createQuery(hql);
		q.setParameter("id3", idFascicolo, IntegerType.INSTANCE);
		if (selectedId != null) q.setParameter("id2", selectedId, IntegerType.INSTANCE);
		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		q.setMaxResults(pageSize);
		q.setFirstResult(offset);
		List<Listini> lstList = (List<Listini>) q.list();
		return lstList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Listini> findListiniByTipoAbb(Session ses,
			Integer idTipoAbb, int offset, int size) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from Listini lst");
		qf.addWhere("lst.tipoAbbonamento.id = :p1");
		qf.addParam("p1", idTipoAbb);
		qf.addOrder("lst.dataInizio desc");
		qf.addOrder("lst.tipoAbbonamento.codice asc");
		qf.setPaging(offset, size);
		Query q = qf.getQuery();
		List<Listini> lstList = (List<Listini>) q.list();
		return lstList;
	}
	
	@SuppressWarnings("unchecked")
	public Listini findListinoByTipoAbbDate(Session ses,
			Integer idTipoAbb, Date date) throws HibernateException {
		String qs = "from Listini lst where " +
				"lst.tipoAbbonamento.id = :p1 and " +
				"lst.dataInizio <= :d1 and " +
				"(lst.dataFine >= :d2 or lst.dataFine is null) " +
				"order by lst.id desc";
		Query q = ses.createQuery(qs);
		q.setInteger("p1", idTipoAbb);
		q.setDate("d1", date);
		q.setDate("d2", date);
		List<Listini> list = q.list();
		if (list != null) {
			if (list.size()>0) {
				return list.get(0);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Integer countProssimaTiraturaByListino(Session ses, Date today,
			Listini lst)
			throws BusinessException {
		Integer result = 0;
		FascicoliDao fDao = new FascicoliDao();
		Fascicoli fasNext = fDao.findPrimoFascicoloNonSpedito(ses,
				lst.getTipoAbbonamento().getPeriodico().getId(), today, false);
		
		String baseSelect = "select ia.copie from IstanzeAbbonamenti as ia ";
		QueryFactory qf = new QueryFactory(ses, baseSelect);
		qf.addWhere("ia.listino.id = :p0");
		qf.addParam("p0", lst.getId());
		qf.addWhere("ia.fascicoloInizio.dataInizio <= :d1");//data inizio <= data prox fascicolo
		qf.addParam("d1", fasNext.getDataInizio());
		qf.addWhere("(" +//regolare e pagato: spediti-totali<=gracing [es. 7-6<=1 ok]
					"((ia.fascicoliSpediti-ia.fascicoliTotali) < :p1 and " +
					"((ia.pagato = :b11 or ia.inFatturazione = :b12 or ia.listino.invioSenzaPagamento = :b13 or ia.listino.fatturaDifferita = :b14 or (ia.listino.prezzo < :d15)) and ia.dataDisdetta is null and ia.ultimaDellaSerie = :b16)) " +
				"or " +//pagato ma con disdetta o non "ultima della serie":
					"((ia.fascicoliSpediti < ia.fascicoliTotali) and " +
					"((ia.pagato = :b21 or ia.inFatturazione = :b22 or ia.listino.invioSenzaPagamento = :b23 or ia.listino.fatturaDifferita = :b24 or (ia.listino.prezzo < :d25)) and (ia.dataDisdetta is not null or ia.ultimaDellaSerie = :b26))) " +
				"or " +//gracing iniziale:
					"(ia.fascicoliSpediti < :p2) " +
				")");
		qf.addParam("b11", true);
		qf.addParam("b12", true);
		qf.addParam("b13", true);
		qf.addParam("b14", true);
		qf.addParam("d15", AppConstants.SOGLIA);
		qf.addParam("b16", Boolean.TRUE);
		qf.addParam("b21", true);
		qf.addParam("b22", true);
		qf.addParam("b23", true);
		qf.addParam("b24", true);
		qf.addParam("d25", AppConstants.SOGLIA);
		qf.addParam("b26", Boolean.FALSE);
		qf.addParam("p1", lst.getGracingFinale());
		qf.addParam("p2", lst.getGracingIniziale());
		qf.addWhere("ia.invioBloccato = :b4");
		qf.addParam("b4", false);
					
		Query iaQ = qf.getQuery();
		List<Integer> copieList = (List<Integer>) iaQ.list();

		for (Integer i:copieList) {
			result += i;
		}
		return result;
	}
	
	public Listini findDefaultListinoByPeriodicoDate(Session ses,
			Integer idPeriodico, String defaultCodiceTipoAbb, Date date) throws HibernateException {
		List<Listini> lstList = new ListiniDao()
				.findListiniByPeriodicoDate(ses, idPeriodico, date, null, 0, Integer.MAX_VALUE);
		Listini result = null;
		for (Listini lst:lstList) {
			if (result == null) result = lst;
			if (lst.getTipoAbbonamento().getCodice().equalsIgnoreCase(defaultCodiceTipoAbb)) {
				if (result.getTipoAbbonamento().getCodice().equalsIgnoreCase(defaultCodiceTipoAbb)) {
					if (lst.getNumFascicoli() < result.getNumFascicoli()) {
						result = lst;
					}
				} else {
					result = lst;
				}
			}
		}
		return result;
	}
	
	public Listini findDefaultListinoByFascicoloInizio(Session ses,
			Integer idPeriodico, String defaultCodiceTipoAbb, Integer idFascicolo) throws HibernateException {
		List<Listini> lstList = new ListiniDao()
				.findListiniByFascicoloInizio(ses, idPeriodico, idFascicolo, null, 0, Integer.MAX_VALUE);
		Listini result = null;
		for (Listini lst:lstList) {
			if (result == null) result = lst;
			if (lst.getTipoAbbonamento().getCodice().equalsIgnoreCase(defaultCodiceTipoAbb)) {
				if (result.getTipoAbbonamento().getCodice().equalsIgnoreCase(defaultCodiceTipoAbb)) {
					if (lst.getNumFascicoli() < result.getNumFascicoli()) {
						result = lst;
					}
				} else {
					result = lst;
				}
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Listini> findActiveListiniByTimeFrame(Session ses, Integer idPeriodico,
			Date dtBegin, Date dtEnd) throws HibernateException {
		String hql = "select distinct ia.listino from IstanzeAbbonamenti ia where "+
				"ia.fascicoloInizio.periodico.id = :id1 and "+
				"ia.fascicoloInizio.dataInizio <= :dt2 and "+ //data inizio <= dtEnd
				"ia.fascicoloFine.dataFine >= :dt1 and "+ //data fine >= dtBegin
				"ia.ultimaDellaSerie = :b1 and "+ //TRUE
				"ia.invioBloccato = :b2 "+ //FALSE
				"order by ia.listino.id";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		q.setParameter("dt1", dtBegin, DateType.INSTANCE);
		q.setParameter("dt2", dtEnd, DateType.INSTANCE);
		q.setParameter("b1", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b2", Boolean.FALSE, BooleanType.INSTANCE);
		List<Listini> lList = q.list();
		return lList;
	}
	
	public Integer saveOrUpdate(Session ses, Listini lsn) throws HibernateException, BusinessException,
			ValidationException {
		Integer lsnId = null;
		//Salva il listino
		if (lsn.getId() == null) {
			//Nuovo
			lsnId = (Integer) save(ses, lsn);
		} else {
			//Esiste, update
			lsnId = lsn.getId();
			update(ses, lsn);
		}
		//Associa Opzioni
		Set<Opzioni> opzSet = new HashSet<Opzioni>();
		if (lsn.getIdOpzioniListiniSetT() != null) {
			for (Integer idOpz:lsn.getIdOpzioniListiniSetT()) {
				Opzioni opz = GenericDao.findById(ses, Opzioni.class, idOpz);
				opzSet.add(opz);
			}
		}
		lsn = GenericDao.findById(ses, Listini.class, lsnId);
		OpzioniUtil.replaceOpzioni(ses, lsn, opzSet, false);
		return lsnId;
	}
}
