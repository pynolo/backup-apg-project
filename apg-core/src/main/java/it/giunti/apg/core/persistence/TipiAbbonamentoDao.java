package it.giunti.apg.core.persistence;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.TipiAbbonamento;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;

public class TipiAbbonamentoDao implements BaseDao<TipiAbbonamento> {

	@Override
	public void update(Session ses, TipiAbbonamento instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
		LogEditingDao.writeEditingLog(ses, TipiAbbonamento.class, instance.getId(),
				instance.getId()+"", instance.getIdUtente());
	}

	@Override
	public Serializable save(Session ses, TipiAbbonamento transientInstance)
			throws HibernateException {
		Integer id = (Integer)GenericDao.saveGeneric(ses, transientInstance);
		LogEditingDao.writeEditingLog(ses, TipiAbbonamento.class, id, 
				id+"", transientInstance.getIdUtente());
		return id;
	}

	@Override
	public void delete(Session ses, TipiAbbonamento instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	public TipiAbbonamento findDefaultTipiAbbByPeriodicoDate(Session ses,
			Integer idPeriodico, String defaultCodiceTipoAbb, Date date) throws HibernateException {
		Listini resultLst = new ListiniDao()
			.findDefaultListinoByPeriodicoDate(ses, idPeriodico, defaultCodiceTipoAbb, date);
		return resultLst.getTipoAbbonamento();
	}
	
	@SuppressWarnings("unchecked")
	public List<TipiAbbonamento> findByPeriodicoDate(Session ses,
			Integer idPeriodico, Integer selectedId, Date beginDate) throws HibernateException {
		if (beginDate == null) beginDate = ServerConstants.DATE_FAR_PAST;
		String hql = "select distinct lst.tipoAbbonamento "+
				"from Listini lst where " +
				"lst.tipoAbbonamento.periodico.id = :id1 and " +
				"( "+
					"("+
						"(lst.dataInizio <= :dt1 or lst.dataInizio is null) and "+
						"(lst.dataFine >= :dt2 or lst.dataFine is null) "+
					") ";
		if (selectedId != null) hql += "or (lst.tipoAbbonamento.id = :id2)";
		hql +=	") "+
				"order by lst.tipoAbbonamento.codice asc, lst.tipoAbbonamento.nome asc";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		q.setParameter("dt1", beginDate, DateType.INSTANCE);
		q.setParameter("dt2", beginDate, DateType.INSTANCE);
		if (selectedId != null) q.setParameter("id2", selectedId, IntegerType.INSTANCE);
		List<TipiAbbonamento> lstList = q.list();
		return lstList;
	}
	
	@SuppressWarnings("unchecked")
	public List<TipiAbbonamento> findByPeriodico(Session ses,
			Integer idPeriodico, Integer selectedId) throws HibernateException {
		String hql = "select distinct lst.tipoAbbonamento "+
				"from Listini lst where " +
				"lst.tipoAbbonamento.periodico.id = :id1 ";
		if (selectedId != null) hql += "or (lst.tipoAbbonamento.id = :id2)";
		hql +=	"order by lst.tipoAbbonamento.codice asc, lst.tipoAbbonamento.nome asc";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		if (selectedId != null) q.setParameter("id2", selectedId, IntegerType.INSTANCE);
		List<TipiAbbonamento> lstList = q.list();
		return lstList;
	}
}
