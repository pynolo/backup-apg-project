package it.giunti.apg.core.persistence;

import it.giunti.apg.shared.model.ArticoliListini;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;

public class ArticoliListiniDao implements BaseDao<ArticoliListini> {

	@Override
	public void update(Session ses, ArticoliListini instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, ArticoliListini transientInstance)
			throws HibernateException {
		Integer id = (Integer)GenericDao.saveGeneric(ses, transientInstance);
		return id;
	}

	@Override
	public void delete(Session ses, ArticoliListini instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}

	public Date buildDataLimite(ArticoliListini al, Date instanceStartDt) {
		Date result = null;
		if (al != null) {
			if ((al.getGiornoLimitePagamento() != null) && (al.getMeseLimitePagamento() != null)) {
				Calendar cal = new GregorianCalendar();
				cal.setTime(instanceStartDt);//Data inizio abbonamento o data attuale
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.DAY_OF_MONTH, al.getGiornoLimitePagamento());
				cal.set(Calendar.MONTH, al.getMeseLimitePagamento()-1);//I mesi sono 0-11
				result = cal.getTime();
				if (result.before(instanceStartDt)) {
					cal.add(Calendar.YEAR, 1);
					result = cal.getTime();
				}
			}
		}
		return result;
	}
		
	@SuppressWarnings("unchecked")
	public List<ArticoliListini> findByListino(Session ses, Integer idListino)
			throws HibernateException {
		String qs = "from ArticoliListini as al where " +
				"al.listino.id = :id1 " +
				"order by al.id ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idListino, IntegerType.INSTANCE);
		List<ArticoliListini> alList= (List<ArticoliListini>) q.list();
		return alList;
	}
	
	@SuppressWarnings("unchecked")
	public List<ArticoliListini> findByPeriodicoDate(Session ses, Integer idPeriodico, Date date)
			throws HibernateException {
		String qs = "from ArticoliListini as al where " +
				"al.listino.tipoAbbonamento.periodico.id = :id1 and " +
				"al.articolo.dataInizio <= :dt1 and " +
				"(al.articolo.dataFine >= :dt2 or al.articolo.dataFine is null)" +
				"order by al.id ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		q.setParameter("dt1", date, DateType.INSTANCE);
		q.setParameter("dt2", date, DateType.INSTANCE);
		List<ArticoliListini> alList= (List<ArticoliListini>) q.list();
		return alList;
	}

	@SuppressWarnings("unchecked")
	public Map<ArticoliListini, Integer> findPendingArticoliListiniCount(Session ses) {
		String hql = "select al, sum(ea.copie) "+
				"from EvasioniArticoli ea, ArticoliListini al, IstanzeAbbonamenti ia where "+
				 "ea.idArticoloListino = al.id and "+//join
				 "ea.idIstanzaAbbonamento = ia.id and "+//join
				"ea.dataInvio is null and "+
				"ea.dataOrdine is null and "+
				"ea.dataAnnullamento is null and "+//false
				"ea.idArticoloListino is not null and "+
				"ea.prenotazioneIstanzaFutura = :b2 and "+//false
				"al.dataEstrazione is null and "+
				"(ia.pagato = :b31 or ia.fatturaDifferita = :b32) and "+//true, true
				"ea.articolo.inAttesa = :b4 " + //false
				"group by ea.idArticoloListino "+
				"order by ea.idArticoloListino ";
		Query q = ses.createQuery(hql);
		q.setParameter("b2", Boolean.FALSE, BooleanType.INSTANCE);
		q.setParameter("b31", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b32", Boolean.TRUE, BooleanType.INSTANCE);
		q.setParameter("b4", Boolean.FALSE, BooleanType.INSTANCE);
		List<Object[]> list = q.list();
		Map<ArticoliListini, Integer> result = new HashMap<ArticoliListini, Integer>();
		for (Object[] obj:list) {
			Long count = (Long)obj[1];
			result.put((ArticoliListini)obj[0], count.intValue());
		}
		return result;
	}
	
}
