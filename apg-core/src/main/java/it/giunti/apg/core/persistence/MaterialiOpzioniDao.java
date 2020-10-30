package it.giunti.apg.core.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;

import it.giunti.apg.shared.model.MaterialiOpzioni;

public class MaterialiOpzioniDao implements BaseDao<MaterialiOpzioni> {

	@Override
	public void update(Session ses, MaterialiOpzioni instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, MaterialiOpzioni transientInstance)
			throws HibernateException {
		Integer id = (Integer)GenericDao.saveGeneric(ses, transientInstance);
		return id;
	}

	@Override
	public void delete(Session ses, MaterialiOpzioni instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}

	@SuppressWarnings("unchecked")
	public List<MaterialiOpzioni> findByOpzione(Session ses, Integer idOpzione)
			throws HibernateException {
		String qs = "from MaterialiOpzioni as ao where " +
				"ao.opzione.id like :id1 " +
				"order by ao.id ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idOpzione, IntegerType.INSTANCE);
		List<MaterialiOpzioni> aoList= (List<MaterialiOpzioni>) q.list();
		return aoList;
	}
	
	@SuppressWarnings("unchecked")
	public List<MaterialiOpzioni> findByPeriodicoDate(Session ses, Integer idPeriodico, Date date)
			throws HibernateException {
		String qs = "from MaterialiOpzioni as ao where " +
				"ao.opzione.periodico.id = :id1 and " +
				"ao.opzione.dataInizio <= :dt1 and " +
				"(ao.opzione.dataFine >= :dt2 or ao.opzione.dataFine is null)" +
				"order by ao.id ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		q.setParameter("dt1", date, DateType.INSTANCE);
		q.setParameter("dt2", date, DateType.INSTANCE);
		List<MaterialiOpzioni> aoList= (List<MaterialiOpzioni>) q.list();
		return aoList;
	}

	@SuppressWarnings("unchecked")
	public List<MaterialiOpzioni> findPendingMaterialiOpzioni(Session ses) {
		String hql = "select mo from MaterialiOpzioni mo, MaterialiSpedizione ms where " +
				 "ms.idMaterialeOpzione = mo.id and "+//join
				"ms.dataInvio is null and "+
				"ms.dataOrdine is null and "+
				"ms.dataAnnullamento is null and "+//false
				"ms.idMaterialeOpzione is not null and "+
				"ms.prenotazioneIstanzaFutura = :b2 and "+//false
				"ms.materiale.inAttesa = :b3 and " + //false
				"mo.dataEstrazione is null " +
				"group by ms.idMaterialeOpzione "+
				"order by ms.idMaterialeOpzione";
		Query q = ses.createQuery(hql);
		q.setParameter("b2", Boolean.FALSE, BooleanType.INSTANCE);
		q.setParameter("b3", Boolean.FALSE, BooleanType.INSTANCE);
		List<MaterialiOpzioni> result = (List<MaterialiOpzioni>) q.list();
		return result;
	}
	
//	@SuppressWarnings("unchecked")
//	public Map<MaterialiOpzioni, Integer> findPendingMaterialiOpzioniCount(Session ses) {
//		String hql = "select mo, sum(ms.copie) "+
//				"from MaterialiSpedizione ms, MaterialiOpzioni mo, Abbonamenti abb, IstanzeAbbonamenti ia where " +
//				 "ms.idMaterialeOpzione = mo.id and "+//join
//				 "ms.idAbbonamento = abb.id and " +//join
//				 "ia.abbonamento = abb.id and " +//join
//				"ms.dataInvio is null and "+
//				"ms.dataOrdine is null and "+
//				"ms.dataAnnullamento is null and "+//false
//				"ms.idMaterialeOpzione is not null and "+
//				"ms.prenotazioneIstanzaFutura = :b2 and "+//false
//				"ms.materiale.inAttesa = :b3 and " + //false
//				"mo.dataEstrazione is null and " +
//					"(ia.pagato = :b41 or "+ //true
//					"ia.fatturaDifferita = :b42 or "+ //true
//					"ia.listino.invioSenzaPagamento = :b43) "+ //true
//				"group by ms.idMaterialeOpzione "+
//				"order by ms.idMaterialeOpzione";
//		Query q = ses.createQuery(hql);
//		q.setParameter("b2", Boolean.FALSE, BooleanType.INSTANCE);
//		q.setParameter("b3", Boolean.FALSE, BooleanType.INSTANCE);
//		q.setParameter("b41", Boolean.TRUE, BooleanType.INSTANCE);
//		q.setParameter("b42", Boolean.TRUE, BooleanType.INSTANCE);
//		q.setParameter("b43", Boolean.TRUE, BooleanType.INSTANCE);
//		List<Object[]> list = q.list();
//		Map<MaterialiOpzioni, Integer> result = new HashMap<MaterialiOpzioni, Integer>();
//		for (Object[] obj:list) {
//			Long count = (Long)obj[1];
//			result.put((MaterialiOpzioni)obj[0], count.intValue());
//		}
//		return result;
//	}
}
