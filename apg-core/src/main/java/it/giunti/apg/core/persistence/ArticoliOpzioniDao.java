package it.giunti.apg.core.persistence;

import it.giunti.apg.shared.model.ArticoliOpzioni;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;

public class ArticoliOpzioniDao implements BaseDao<ArticoliOpzioni> {

	@Override
	public void update(Session ses, ArticoliOpzioni instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, ArticoliOpzioni transientInstance)
			throws HibernateException {
		Integer id = (Integer)GenericDao.saveGeneric(ses, transientInstance);
		return id;
	}

	@Override
	public void delete(Session ses, ArticoliOpzioni instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}

	@SuppressWarnings("unchecked")
	public List<ArticoliOpzioni> findByOpzione(Session ses, Integer idOpzione)
			throws HibernateException {
		String qs = "from ArticoliOpzioni as ao where " +
				"ao.opzione.id like :id1 " +
				"order by ao.id ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idOpzione, IntegerType.INSTANCE);
		List<ArticoliOpzioni> aoList= (List<ArticoliOpzioni>) q.list();
		return aoList;
	}
	
	@SuppressWarnings("unchecked")
	public List<ArticoliOpzioni> findByPeriodicoDate(Session ses, Integer idPeriodico, Date date)
			throws HibernateException {
		String qs = "from ArticoliOpzioni as ao where " +
				"ao.opzione.periodico.id = :id1 and " +
				"ao.articolo.dataInizio <= :dt1 and " +
				"(ao.articolo.dataFine >= :dt2 or ao.articolo.dataFine is null)" +
				"order by ao.id ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		q.setParameter("dt1", date, DateType.INSTANCE);
		q.setParameter("dt2", date, DateType.INSTANCE);
		List<ArticoliOpzioni> aoList= (List<ArticoliOpzioni>) q.list();
		return aoList;
	}
	
	@SuppressWarnings("unchecked")
	public Map<ArticoliOpzioni, Integer> findPendingArticoliOpzioniCount(Session ses) {
		String hql = "select ao, sum(ea.copie) "+
				"from MaterialiSpedizione ms, ArticoliOpzioni ao, Abbonamenti abb, IstanzeAbbonamenti ia where " +
				 "ms.idArticoloOpzione = ao.id and "+//join
				 "ms.idAbbonamento = abb.id and " +//join
				 "ia.idAbbonamento = abb.id and " +//join
				"ms.dataInvio is null and "+
				"ms.dataOrdine is null and "+
				"ms.dataAnnullamento is null and "+//false
				"ms.idArticoloOpzione is not null and "+
				"ms.prenotazioneIstanzaFutura = :b2 and "+//false
				"ms.materiale.inAttesa = :b3 and " + //false
				"ao.dataEstrazione is null and " +
					"(ia.pagato = :b41 or "+ //true
					"ia.fatturaDifferita = :b42 or "+ //true
					"ia.listino.invioSenzaPagamento = :b43) "+ //true
				"group by ms.idArticoloOpzione "+
				"order by ms.idArticoloOpzione";
		Query q = ses.createQuery(hql);
		q.setParameter("b2", Boolean.FALSE, BooleanType.INSTANCE);
		q.setParameter("b3", Boolean.FALSE, BooleanType.INSTANCE);
		q.setParameter("b41", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b42", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b43", Boolean.TRUE, BooleanType.INSTANCE);
		List<Object[]> list = q.list();
		Map<ArticoliOpzioni, Integer> result = new HashMap<ArticoliOpzioni, Integer>();
		for (Object[] obj:list) {
			Long count = (Long)obj[1];
			result.put((ArticoliOpzioni)obj[0], count.intValue());
		}
		return result;
	}
}
