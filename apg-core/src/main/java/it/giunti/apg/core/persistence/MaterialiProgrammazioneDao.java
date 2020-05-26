package it.giunti.apg.core.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

import it.giunti.apg.shared.model.MaterialiProgrammazione;

public class MaterialiProgrammazioneDao implements BaseDao<MaterialiProgrammazione> {

	@Override
	public void update(Session ses, MaterialiProgrammazione instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, MaterialiProgrammazione transientInstance)
			throws HibernateException {
		Integer id = (Integer) GenericDao.saveGeneric(ses, transientInstance);
		return id;
	}

	@Override
	public void delete(Session ses, MaterialiProgrammazione instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
		
	@SuppressWarnings("unchecked")
	public MaterialiProgrammazione findByPeriodicoDataInizio(Session ses, Integer idPeriodico,
			Date date) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from MaterialiProgrammazione f");
		qf.addWhere("f.periodico.id = :p1");
		qf.addParam("p1", idPeriodico);
		qf.addWhere("f.dataNominale <= :p2");
		qf.addParam("p2", date);
		qf.addWhere("f.opzione is null");
		qf.addOrder("f.dataNominale desc");
		qf.setPaging(0, 2);
		Query q = qf.getQuery();
		List<MaterialiProgrammazione> cList = (List<MaterialiProgrammazione>) q.list();
		if (cList != null) {
			if (cList.size() > 0) {
				return cList.get(0);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public MaterialiProgrammazione findPrimoNonSpedito(Session ses, Integer idPeriodico,
			Date date) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from MaterialiProgrammazione f");
		qf.addWhere("f.periodico.id = :p1");
		qf.addParam("p1", idPeriodico);
		qf.addWhere("f.dataEstrazione is null");
		qf.addWhere("f.dataNominale >= :p2");
		qf.addParam("p2", date);
		qf.addWhere("f.opzione is null");
		qf.addOrder("f.dataNominale asc");
		qf.setPaging(0, 2);
		Query q = qf.getQuery();
		List<MaterialiProgrammazione> cList = (List<MaterialiProgrammazione>) q.list();
		if (cList != null) {
			if (cList.size() > 0) {
				return cList.get(0);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<MaterialiProgrammazione> findByPeriodico(Session ses, Integer idPeriodico, Integer selectedId,
			long startDt, long finishDt, boolean includeOpzioni,
			boolean orderAsc, int offset, int pageSize) throws HibernateException {
		Date startDate = new Date(startDt);
		Date finishDate = new Date(finishDt);
		
		String hql = "from MaterialiProgrammazione f where "+
			"f.periodico.id = :id1 and "+
			"( "+
				"( ";
		if (!includeOpzioni) hql += "f.opzione is null and ";
		hql +=		"f.dataNominale >= :dt1 and "+
					"f.dataNominale <= :dt2 "+
				") ";
		if (selectedId != null) hql += "or (f.id = :id2) ";
		hql += ") ";
		if (orderAsc) {
			hql += "order by f.dataNominale asc ";
		} else {
			hql += "order by f.dataNominale desc ";
		}
		
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		q.setParameter("dt1", startDate, DateType.INSTANCE);
		q.setParameter("dt2", finishDate, DateType.INSTANCE);
		if (selectedId != null) q.setParameter("id2", selectedId, IntegerType.INSTANCE);
		q.setMaxResults(pageSize);
		q.setFirstResult(offset);
		
		List<MaterialiProgrammazione> cList = (List<MaterialiProgrammazione>) q.list();
		return cList;
	}
	
	@SuppressWarnings("unchecked")
	public List<MaterialiProgrammazione> findByOpzione(Session ses, Integer idOpzione,
			boolean orderAsc, int offset, int pageSize) throws HibernateException {
		String hql = "from MaterialiProgrammazione f where "+
			"f.opzione.id = :id1 ";
		if (orderAsc) {
			hql += "order by f.dataNominale asc ";
		} else {
			hql += "order by f.dataNominale desc ";
		}
		
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idOpzione, IntegerType.INSTANCE);
		q.setMaxResults(pageSize);
		q.setFirstResult(offset);
		
		List<MaterialiProgrammazione> cList = (List<MaterialiProgrammazione>) q.list();
		return cList;
	}
	
//	/**
//	 * fascicoliCount = 1 significa il fascicolo successivo
//	 * fascicoliCount = 2..n sono i fascicoli nel futuro rispetto a quello di partenza
//	 * @param ses
//	 * @param idFascicolo
//	 * @param fascicoliCount
//	 * @return
//	 * @throws HibernateException
//	 */
//	public Fascicoli findFascicoliAfterFascicolo(Session ses, Integer idFascicolo,
//			int fascicoliCount) throws HibernateException {
//		Fascicoli oldFas = (Fascicoli) ses.get(Fascicoli.class, idFascicolo);
//		if (oldFas == null) throw new HibernateException("Non esiste un fascicolo con id="+idFascicolo);
//		return findFascicoliAfterFascicolo(ses, oldFas, fascicoliCount);
//	}
//	@SuppressWarnings("unchecked")
//	public Fascicoli findFascicoliAfterFascicolo(Session ses, Fascicoli oldFas,
//			int fascicoliCount) throws HibernateException {
//		if (fascicoliCount == 0) return oldFas;
//		QueryFactory qf = new QueryFactory(ses, "from Fascicoli f");
//		qf.addWhere("f.periodico.id = :p1");
//		qf.addParam("p1", oldFas.getPeriodico().getId());
//		qf.addWhere("f.dataInizio >= :p2");
//		qf.addParam("p2", oldFas.getDataInizio());
//		qf.addWhere("f.fascicoliAccorpati > :p3");
//		qf.addParam("p3", 0);
//		qf.addWhere("f.opzione is null");
//		qf.addOrder("f.dataInizio asc");
//		qf.setPaging(0, fascicoliCount+2);
//		Query q = qf.getQuery();
//		List<Fascicoli> cList = (List<Fascicoli>) q.list();
//		Fascicoli fas = null;
//		int i = 0;
//		int count = 0;
//		while (count < fascicoliCount+1) {
//			try {
//				fas = cList.get(i);
//			} catch (Exception e) {
//				throw new HibernateException("Non sono disponibili "+fascicoliCount+" fascicoli dopo " +
//						oldFas.getTitoloNumero() + " " + oldFas.getPeriodico().getNome());
//			}
//			count += fas.getFascicoliAccorpati();
//			i++;
//		}
//		return fas;
//	}
//	
//	
//	public Fascicoli findFascicoliBeforeFascicolo(Session ses, Integer idFascicolo,
//			int fascicoliCount) throws HibernateException {
//		Fascicoli oldFas = (Fascicoli) ses.get(Fascicoli.class, idFascicolo);
//		if (oldFas == null) throw new HibernateException("Non esiste un fascicolo con id="+idFascicolo);
//		return findFascicoliBeforeFascicolo(ses, oldFas, fascicoliCount);
//	}
//	@SuppressWarnings("unchecked")
//	public Fascicoli findFascicoliBeforeFascicolo(Session ses, Fascicoli oldFas,
//			int fascicoliCount) throws HibernateException {
//		if (fascicoliCount == 0) return oldFas;
//		QueryFactory qf = new QueryFactory(ses, "from Fascicoli f");
//		qf.addWhere("f.periodico.id = :p1");
//		qf.addParam("p1", oldFas.getPeriodico().getId());
//		qf.addWhere("f.dataInizio <= :p2");
//		qf.addParam("p2", oldFas.getDataInizio());
//		qf.addWhere("f.fascicoliAccorpati > :p3");
//		qf.addParam("p3", 0);
//		qf.addWhere("f.opzione is null");
//		qf.addOrder("f.dataInizio desc");
//		qf.setPaging(0, fascicoliCount+1);
//		Query q = qf.getQuery();
//		List<Fascicoli> cList = (List<Fascicoli>) q.list();
//		Fascicoli fas = null;
//		try {
//			if (cList != null) {
//				int i = 0;
//				int count = 0;
//				while (count < fascicoliCount) {
//					i++;
//					fas = cList.get(i);
//					count += fas.getFascicoliAccorpati();
//				}
//			}
//		} catch (IndexOutOfBoundsException e) {
//			//Ritorna null
//		}
//		return fas;
//	}
	
	@SuppressWarnings("unchecked")
	public List<MaterialiProgrammazione> findBetweenDates(Session ses, Integer idPeriodico,
			Date dataInizio, Date dataFine) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from MaterialiProgrammazione f");
		qf.addWhere("f.periodico.id = :p1");
		qf.addParam("p1", idPeriodico);
		qf.addWhere("f.dataNominale >= :p2");
		qf.addParam("p2", dataInizio);
		qf.addWhere("f.dataNominale <= :p3");
		qf.addParam("p3", dataFine);
		qf.addWhere("f.opzione is null");
		qf.addOrder("f.dataNominale desc");
		qf.addOrder("f.fascicoliAccorpati desc");
		Query q = qf.getQuery();
		List<MaterialiProgrammazione> cList = (List<MaterialiProgrammazione>) q.list();
		return cList;
	}
	
	/**
	 * Restituisce i fascicoli per cui non sono ancora state spedite le comunicazioni
	 */
	@SuppressWarnings("unchecked")
	public List<MaterialiProgrammazione> findByComunicazioniMancanti(Session ses) throws HibernateException {
		String qs = "from MaterialiProgrammazione f where " +
				"f.dataEstrazione is not null and " +
				"f.comunicazioniInviate = :b1 and " +
				"f.opzione is null ";//non deve essere un opzione
		qs += "order by f.id asc ";
		Query q = ses.createQuery(qs);
		q.setParameter("b1", Boolean.FALSE);
		List<MaterialiProgrammazione> fasList = q.list();
		return fasList;
	}
	
	/**
	 * Restituisce i MaterialiProgrammazione per cui esistono delle
	 * EvasioniComunicazioni accodate ma non ancora spedite, per il tipoMedia specificato.
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, Integer> findByEnqueuedComunicazioniMedia(Session ses, String idTipoMedia) throws HibernateException {
		String qs = "select ec.idMaterialiProgrammazione, count(ec.id) from EvasioniComunicazioni ec where " +
				"ec.dataEstrazione is null and " +
				"ec.eliminato = :b1 and " +
				"ec.comunicazione is not null and " +
				"ec.comunicazione.idTipoMedia = :s1 and " +
				"ec.idMaterialiProgrammazione is not null " +//non deve essere un opzione
				"group by ec.idMaterialiProgrammazione "+
				"order by ec.id asc ";
		Query q = ses.createQuery(qs);
		q.setParameter("b1", Boolean.FALSE);
		q.setParameter("s1", idTipoMedia, StringType.INSTANCE);
		List<Object[]> coupleList = q.list();
		Map<Integer, Integer> fasMap = new HashMap<Integer, Integer>();
		for (Object[] couple:coupleList) {
			Integer qty = ((Long)couple[1]).intValue();
			fasMap.put((Integer)couple[0], qty);
		}
		return fasMap;
	}
	
	/**
	 * Restituisce i Fascicoli per cui esistono delle
	 * EvasioniComunicazioni accodate ma non ancora spedite, per il tipoMedia specificato.
	 */
	@SuppressWarnings("unchecked")
	public List<MaterialiProgrammazione> findByEnqueuedComunicazioniPeriodicoMedia(Session ses, Integer idPeriodico, String idTipoMedia) throws HibernateException {
		String qs = "select distinct mp from EvasioniComunicazioni ec, MaterialiProgrammazione mp where " +
				"ec.idMaterialeProgrammazione = mp.id and "+
				"ec.dataEstrazione is null and " +
				"ec.eliminato = :b1 and " +
				"ec.comunicazione is not null and " +
				"ec.comunicazione.idTipoMedia = :s1 and " +
				"ec.idMaterialiProgrammazione is not null and " +//non deve essere un'opzione
				"mp.periodico.id = :id1 " +
				"order by mp.id asc ";
		Query q = ses.createQuery(qs);
		q.setParameter("b1", Boolean.FALSE);
		q.setParameter("s1", idTipoMedia, StringType.INSTANCE);
		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		List<MaterialiProgrammazione> fasList = q.list();
		return fasList;
	}
	
//	public Fascicoli changeFascicoloToMatchStartingMonth(Session ses,
//			Listini lst /*, Fascicoli tentativeFascicoloInizio*/) {
//		Calendar cal = new GregorianCalendar();
//		Date dataNominale = cal.getTime();
//		int count = MonthBusiness.getMonthsToSpecificMonth(dataNominale, lst.getMeseInizio()-1);
//		if (count <= AppConstants.MESE_INIZIO_MONTHS_FORWARD) {
//			dataNominale = MonthBusiness.getFirstDayOfSpecificMonth(dataNominale, lst.getMeseInizio()-1);
//		} else {
//			dataNominale = MonthBusiness.getFirstDayOfPastMonth(dataNominale, lst.getMeseInizio()-1);
//		}
//		Fascicoli fascicoloInizio = new MaterialiProgrammazioneDao()
//				.findFascicoloByPeriodicoDataInizio(ses,
//						lst.getTipoAbbonamento().getPeriodico().getId(), dataNominale);
//		if (fascicoloInizio == null) {
//			cal.add(Calendar.YEAR, -1);
//			fascicoloInizio = findFascicoloByPeriodicoDataInizio(ses,
//					lst.getTipoAbbonamento().getPeriodico().getId(),
//					cal.getTime());
//		}
//		return fascicoloInizio;
//	}
}
