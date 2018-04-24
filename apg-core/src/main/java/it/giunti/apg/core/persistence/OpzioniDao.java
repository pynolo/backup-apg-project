package it.giunti.apg.core.persistence;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Opzioni;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.shared.model.OpzioniListini;
import it.giunti.apg.shared.model.Periodici;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

public class OpzioniDao implements BaseDao<Opzioni> {

	@Override
	public void update(Session ses, Opzioni instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, Opzioni transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, Opzioni instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	public List<Opzioni> findByDate(Session ses, Date extractionDt)
			throws HibernateException {
		long extrDt = extractionDt.getTime();
		return findByDate(ses, extrDt, extrDt);
	}
	
	@SuppressWarnings("unchecked")
	public Opzioni findByUid(Session ses, String uid)
			throws HibernateException {
		String qs = "from Opzioni as o where " +
				"o.uid = :s1 " +
				"order by o.id desc ";
		Query q = ses.createQuery(qs);
		q.setParameter("s1", uid, StringType.INSTANCE);
		List<Opzioni> result = (List<Opzioni>) q.list();
		if (result != null) {
			if (result.size() > 0) return result.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public String createNewUid(Session ses, Integer idPeriodico)
			throws HibernateException {
		String qs = "from Opzioni as o where " +
				"o.periodico.id = :id1 " +
				"order by o.uid desc ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		List<Opzioni> list = (List<Opzioni>) q.list();
		Periodici per = GenericDao.findById(ses, Periodici.class, idPeriodico);
		String lastUid = per.getUid()+"000";
		if (list.size() > 0) {
			Opzioni lastOpz = list.get(0);
			lastUid = lastOpz.getUid();
		}
		String counterHex = lastUid.substring(1);
		Integer newCounter = Integer.parseInt(counterHex, 16);
		newCounter++;
		String newCounterHex = "000"+Integer.toHexString(newCounter);
		newCounterHex = newCounterHex
				.substring(newCounterHex.length()-3, newCounterHex.length());
		String result = per.getUid()+newCounterHex.toUpperCase();
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Opzioni> findByDate(Session ses, long startDt, long finishDt)
			throws HibernateException {
		String qs = "from Opzioni as s where " +
				"((s.dataInizio is null) or (s.dataInizio <= :d2)) and " +
				"((s.dataFine is null) or (s.dataFine >= :d1)) " +
				"order by s.periodico ";
		Query q = ses.createQuery(qs);
		q.setDate("d1", new Date(startDt));
		q.setDate("d2", new Date(finishDt));
		List<Opzioni> sList = (List<Opzioni>) q.list();
		return sList;
	}

	@SuppressWarnings("unchecked")
	public List<Opzioni> findByPeriodicoDate(Session ses, Integer idPeriodico,
			Date extractionDt, boolean soloCartacei) throws HibernateException {
		String qs = "from Opzioni as s where " +
				"s.periodico.id = :p1 and " +
				"((s.dataInizio is null) or (s.dataInizio <= :d1)) and " +
				"((s.dataFine is null) or (s.dataFine >= :d2)) ";
		if (soloCartacei) qs += "and s.cartaceo = :b1 ";
		qs += "order by s.dataInizio desc ";
		Query q = ses.createQuery(qs);
		q.setInteger("p1", idPeriodico);
		q.setParameter("d1", extractionDt, DateType.INSTANCE);
		q.setParameter("d2", extractionDt, DateType.INSTANCE);
		if (soloCartacei) q.setBoolean("b1", Boolean.TRUE);
		List<Opzioni> sList = (List<Opzioni>) q.list();
		return sList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Opzioni> findByPeriodicoDate(Session ses, Integer idPeriodico,
			Date startDt, Date finishDt, boolean soloCartacei) throws HibernateException {
		if (startDt == null) startDt = ServerConstants.DATE_FAR_PAST;
		if (finishDt == null) finishDt = ServerConstants.DATE_FAR_FUTURE;
		String qs = "from Opzioni as s where " +
				"s.periodico.id = :p1 and " +
				"((s.dataInizio is null) or (s.dataInizio <= :d1)) and " +
				"((s.dataFine is null) or (s.dataFine >= :d2)) ";
		if (soloCartacei) qs += "and s.cartaceo = :b1 ";
		qs += "order by s.dataInizio desc ";
		Query q = ses.createQuery(qs);
		q.setInteger("p1", idPeriodico);
		q.setDate("d1", finishDt);
		q.setDate("d2", startDt);
		if (soloCartacei) q.setBoolean("b1", Boolean.TRUE);
		List<Opzioni> sList = (List<Opzioni>) q.list();
		return sList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Opzioni> findOpzioniByListino(Session ses,
			Integer idListino) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "select ol.opzione from OpzioniListini ol");
		qf.addWhere("ol.listino.id = :id1");
		qf.addParam("id1", idListino);
		qf.addOrder("ol.id asc");
		Query q = qf.getQuery();
		List<Opzioni> oList = (List<Opzioni>) q.list();
		return oList;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean hasOpzione(Session ses, IstanzeAbbonamenti ia, Opzioni opz) {
		String hql = "from OpzioniIstanzeAbbonamenti oia where "
				+ "oia.istanza.id = :id1 and oia.opzione.id = :id2";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", ia.getId(), IntegerType.INSTANCE);
		q.setParameter("id2", opz.getId(), IntegerType.INSTANCE);
		List<OpzioniIstanzeAbbonamenti> result = q.list();
		if (result != null) {
			if (result.size() > 0) return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean hasOpzione(Session ses, Listini lsn, Opzioni opz) {
		String hql = "from OpzioniListini ol where "
				+ "ol.listino.id = :id1 and ol.opzione.id = :id2";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", lsn.getId(), IntegerType.INSTANCE);
		q.setParameter("id2", opz.getId(), IntegerType.INSTANCE);
		List<OpzioniListini> result = q.list();
		if (result != null) {
			if (result.size() > 0) return true;
		}
		return false;
	}
}
