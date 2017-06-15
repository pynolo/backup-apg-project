package it.giunti.apg.server.persistence;

import it.giunti.apg.shared.model.OrdiniLogistica;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;

public class OrdiniLogisticaDao implements BaseDao<OrdiniLogistica> {

	@Override
	public void update(Session ses, OrdiniLogistica instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, OrdiniLogistica transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, OrdiniLogistica instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}

	public OrdiniLogistica createPersistent(Session ses, Integer idAnagrafica, Date dataInserimento)
			throws HibernateException {
		OrdiniLogistica ordine = new OrdiniLogistica();
		String codice = new ContatoriDao().createCodiceOrdine(ses);
		ordine.setNumeroOrdine(codice);
		ordine.setIdAnagrafica(idAnagrafica);
		ordine.setDataInserimento(dataInserimento);
		Integer id = (Integer) save(ses, ordine);
		OrdiniLogistica persistent = GenericDao.findById(ses, OrdiniLogistica.class, id);
		return persistent;
	}
	
	@SuppressWarnings("unchecked")
	public List<OrdiniLogistica> findNuoviByDataInserimento(Session ses, Date startDate, int offset, int pageSize) {
		String hql="from OrdiniLogistica ol where " +
				"ol.dataInserimento >= :dt1 and " +
				"ol.dataRifiuto is null and " +
				"ol.dataChiusura is null " +
				"order by ol.id";
		Query q = ses.createQuery(hql);
		q.setParameter("dt1", startDate, TimestampType.INSTANCE);
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<OrdiniLogistica> olList = (List<OrdiniLogistica>) q.list();
		return olList;
	}
	
	@SuppressWarnings("unchecked")
	public List<OrdiniLogistica> findOrdini(Session ses, boolean showAnnullati,
			int offset, int pageSize) {
		String hql="from OrdiniLogistica ol ";
		if (!showAnnullati) hql += "where ol.dataRifiuto is null ";
		hql += "order by ol.id desc";
		Query q = ses.createQuery(hql);
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<OrdiniLogistica> olList = (List<OrdiniLogistica>) q.list();
		return olList;
	}
	
	@SuppressWarnings("unchecked")
	public OrdiniLogistica findOrdineByNumeroOrdine(Session ses, String numeroOrdine) {
		String hql="from OrdiniLogistica ol where " +
				"ol.numeroOrdine = :s1 ";
		Query q = ses.createQuery(hql);
		q.setParameter("s1", numeroOrdine, StringType.INSTANCE);
		List<OrdiniLogistica> olList = (List<OrdiniLogistica>) q.list();
		OrdiniLogistica ol = null;
		if (olList != null) {
			if (olList.size() > 0) {
				ol = olList.get(0);
			}
		}
		return ol;
	}
	
	@SuppressWarnings("unchecked")
	public List<OrdiniLogistica> findOrdiniByAnagrafica(Session ses, boolean excludeRifiutati,
			Integer idAnagrafica, int offset, int pageSize) {
		String hql="from OrdiniLogistica ol where ";
		if (excludeRifiutati) hql += "ol.dataRifiuto is null and ";
		hql += "ol.idAnagrafica = :id1 order by ol.id ";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idAnagrafica, IntegerType.INSTANCE);
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<OrdiniLogistica> olList = (List<OrdiniLogistica>) q.list();
		return olList;
	}
}
