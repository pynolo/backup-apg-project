package it.giunti.apg.core.persistence;

import it.giunti.apg.core.DateUtil;
import it.giunti.apg.core.business.MonthBusiness;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.Listini;

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
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

public class FascicoliDao implements BaseDao<Fascicoli> {

	@Override
	public void update(Session ses, Fascicoli instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
		updateDataFineOnFascicoli(ses, instance);
	}

	@Override
	public Serializable save(Session ses, Fascicoli transientInstance)
			throws HibernateException {
		if (transientInstance.getDataFine() == null) {
			transientInstance.setDataFine(DateUtil.now());//Tanto verrà aggiornata dopo il save
		}
		Integer id = (Integer) GenericDao.saveGeneric(ses, transientInstance);
		Fascicoli fas = GenericDao.findById(ses, Fascicoli.class, id);
		updateDataFineOnFascicoli(ses, fas);
		return id;
	}

	@Override
	public void delete(Session ses, Fascicoli instance)
			throws HibernateException {
		Fascicoli fasPrima = findFascicoliBeforeFascicolo(ses, instance, 1);
		Fascicoli fasDopo = findFascicoliAfterFascicolo(ses, instance, 1);
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
		//Ricalcola le date di fine dei fascicoli prima e dopo
		updateDataFineOnFascicoli(ses, fasPrima);
		updateDataFineOnFascicoli(ses, fasDopo);
	}
	
	@SuppressWarnings("unchecked")
	public Fascicoli findByCodiceMeccanografico(Session ses, String cm)
			throws HibernateException {
		String qs = "from Fascicoli f where " +
				"f.codiceMeccanografico = :s1 " +
				"order by f.id desc ";
		Query q = ses.createQuery(qs);
		q.setParameter("s1", cm, StringType.INSTANCE);
		List<Fascicoli> cList = (List<Fascicoli>) q.list();
		if (cList != null) {
			if (cList.size()==1) {
				return cList.get(0);
			}
		}
		return null;
	}
	
	/** Imposta la data nominale fine per il fascicolo corrente e per quello precedente nel tempo.
	 * La data fine del fascicolo in esame sarà il giorno prima rispetto alla data nominale del
	 * fascicolo successivo, se esiste. Altrimenti sarà 1 mese dopo la sua stessa data nominale.
	 * @param ses
	 * @param fas
	 */
	public void updateDataFineOnFascicoli(Session ses, Fascicoli fas) {
		if (fas == null) return;
		boolean chooseDefaultDate = true;
		if (fas.getOpzione() == null) { //=> Non e' un opzione
			//Cerca i fascicoli prima e dopo
			Fascicoli fasPrima = null;
			try {
				fasPrima = findFascicoliBeforeFascicolo(ses, fas, 1);
			} catch (HibernateException e) {
				// non c'è un fascicolo prima, tutto ok.
			}
			Fascicoli fasDopo = null;
			try {
				fasDopo = findFascicoliAfterFascicolo(ses, fas, 1);
			} catch (HibernateException e) {
				// non c'è un fascicolo dopo, tutto ok.
			}
			if (fasPrima != null) {
				//Imposta la data nominale fine del fascicolo precedente
				Calendar cal = new GregorianCalendar();
				cal.setTime(fas.getDataInizio());
				cal.add(Calendar.DAY_OF_MONTH, -1);
				Date dataNominaleFine = cal.getTime();
				fasPrima.setDataFine(dataNominaleFine);
				GenericDao.updateGeneric(ses, fasPrima.getId(), fasPrima);
			}
			if (fasDopo != null) {
				//Imposta la data nominale fine del fascicolo attuale in base a quello futuro
				Calendar cal = new GregorianCalendar();
				cal.setTime(fasDopo.getDataInizio());
				cal.add(Calendar.DAY_OF_MONTH, -1);
				Date dataNominaleFine = cal.getTime();
				fas.setDataFine(dataNominaleFine);
				chooseDefaultDate=false;
			}
		}
		if (chooseDefaultDate) {
			//Sceglie la durata del fascicolo = 1 mese.
			Calendar cal = new GregorianCalendar();
			cal.setTime(fas.getDataInizio());
			cal.add(Calendar.MONTH, 1);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			Date dataNominaleFine = cal.getTime();
			fas.setDataFine(dataNominaleFine);
		}
		GenericDao.updateGeneric(ses, fas.getId(), fas);
	}
		
	@SuppressWarnings("unchecked")
	public Fascicoli findFascicoloByPeriodicoDataInizio(Session ses, Integer idPeriodico,
			Date date) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from Fascicoli f");
		qf.addWhere("f.periodico.id = :p1");
		qf.addParam("p1", idPeriodico);
		qf.addWhere("f.dataInizio <= :p2");
		qf.addParam("p2", date);
		qf.addWhere("f.fascicoliAccorpati > :i1");
		qf.addParam("i1", 0);
		qf.addWhere("f.opzione is null");
		qf.addOrder("f.dataInizio desc");
		qf.setPaging(0, 2);
		Query q = qf.getQuery();
		List<Fascicoli> cList = (List<Fascicoli>) q.list();
		if (cList != null) {
			if (cList.size() > 0) {
				return cList.get(0);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Fascicoli findPrimoFascicoloNonSpedito(Session ses, Integer idPeriodico,
			Date date, boolean includeAllegati) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from Fascicoli f");
		qf.addWhere("f.periodico.id = :p1");
		qf.addParam("p1", idPeriodico);
		qf.addWhere("f.dataEstrazione is null");
		if (!includeAllegati) {
			qf.addWhere("f.fascicoliAccorpati > :i1");
			qf.addParam("i1", 0);
		}
		qf.addWhere("f.dataInizio >= :p2");
		qf.addParam("p2", date);
		qf.addWhere("f.opzione is null");
		qf.addOrder("f.dataInizio asc");
		qf.setPaging(0, 2);
		Query q = qf.getQuery();
		List<Fascicoli> cList = (List<Fascicoli>) q.list();
		if (cList != null) {
			if (cList.size() > 0) {
				return cList.get(0);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Fascicoli> findFascicoliByPeriodico(Session ses, Integer idPeriodico, Integer selectedId,
			long startDt, long finishDt, boolean includeOpzioni,
			boolean orderAsc, int offset, int pageSize) throws HibernateException {
		Date startDate = new Date(startDt);
		Date finishDate = new Date(finishDt);
		
		String hql = "from Fascicoli f where "+
			"f.periodico.id = :id1 and "+
			"( "+
				"( ";
		if (!includeOpzioni) hql += "f.opzione is null and ";
		hql +=		"f.dataInizio >= :dt1 and "+
					"f.dataInizio <= :dt2 "+
				") ";
		if (selectedId != null) hql += "or (f.id = :id2) ";
		hql += ") ";
		if (orderAsc) {
			hql += "order by f.dataInizio asc ";
		} else {
			hql += "order by f.dataInizio desc ";
		}
		
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		q.setParameter("dt1", startDate, DateType.INSTANCE);
		q.setParameter("dt2", finishDate, DateType.INSTANCE);
		if (selectedId != null) q.setParameter("id2", selectedId, IntegerType.INSTANCE);
		q.setMaxResults(pageSize);
		q.setFirstResult(offset);
		
		List<Fascicoli> cList = (List<Fascicoli>) q.list();
		return cList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Fascicoli> findFascicoliByOpzione(Session ses, Integer idOpzione,
			boolean orderAsc, int offset, int pageSize) throws HibernateException {
		String hql = "from Fascicoli f where "+
			"f.opzione.id = :id1 ";
		if (orderAsc) {
			hql += "order by f.dataInizio asc ";
		} else {
			hql += "order by f.dataInizio desc ";
		}
		
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idOpzione, IntegerType.INSTANCE);
		q.setMaxResults(pageSize);
		q.setFirstResult(offset);
		
		List<Fascicoli> cList = (List<Fascicoli>) q.list();
		return cList;
	}
	
	/**
	 * fascicoliCount = 1 significa il fascicolo successivo
	 * fascicoliCount = 2..n sono i fascicoli nel futuro rispetto a quello di partenza
	 * @param ses
	 * @param idFascicolo
	 * @param fascicoliCount
	 * @return
	 * @throws HibernateException
	 */
	public Fascicoli findFascicoliAfterFascicolo(Session ses, Integer idFascicolo,
			int fascicoliCount) throws HibernateException {
		Fascicoli oldFas = (Fascicoli) ses.get(Fascicoli.class, idFascicolo);
		if (oldFas == null) throw new HibernateException("Non esiste un fascicolo con id="+idFascicolo);
		return findFascicoliAfterFascicolo(ses, oldFas, fascicoliCount);
	}
	@SuppressWarnings("unchecked")
	public Fascicoli findFascicoliAfterFascicolo(Session ses, Fascicoli oldFas,
			int fascicoliCount) throws HibernateException {
		if (fascicoliCount == 0) return oldFas;
		QueryFactory qf = new QueryFactory(ses, "from Fascicoli f");
		qf.addWhere("f.periodico.id = :p1");
		qf.addParam("p1", oldFas.getPeriodico().getId());
		qf.addWhere("f.dataInizio >= :p2");
		qf.addParam("p2", oldFas.getDataInizio());
		qf.addWhere("f.fascicoliAccorpati > :p3");
		qf.addParam("p3", 0);
		qf.addWhere("f.opzione is null");
		qf.addOrder("f.dataInizio asc");
		qf.setPaging(0, fascicoliCount+2);
		Query q = qf.getQuery();
		List<Fascicoli> cList = (List<Fascicoli>) q.list();
		Fascicoli fas = null;
		int i = 0;
		int count = 0;
		while (count < fascicoliCount+1) {
			try {
				fas = cList.get(i);
			} catch (Exception e) {
				throw new HibernateException("Non sono disponibili "+fascicoliCount+" fascicoli dopo " +
						oldFas.getTitoloNumero() + " " + oldFas.getPeriodico().getNome());
			}
			count += fas.getFascicoliAccorpati();
			i++;
		}
		return fas;
	}
	
	
	public Fascicoli findFascicoliBeforeFascicolo(Session ses, Integer idFascicolo,
			int fascicoliCount) throws HibernateException {
		Fascicoli oldFas = (Fascicoli) ses.get(Fascicoli.class, idFascicolo);
		if (oldFas == null) throw new HibernateException("Non esiste un fascicolo con id="+idFascicolo);
		return findFascicoliBeforeFascicolo(ses, oldFas, fascicoliCount);
	}
	@SuppressWarnings("unchecked")
	public Fascicoli findFascicoliBeforeFascicolo(Session ses, Fascicoli oldFas,
			int fascicoliCount) throws HibernateException {
		if (fascicoliCount == 0) return oldFas;
		QueryFactory qf = new QueryFactory(ses, "from Fascicoli f");
		qf.addWhere("f.periodico.id = :p1");
		qf.addParam("p1", oldFas.getPeriodico().getId());
		qf.addWhere("f.dataInizio <= :p2");
		qf.addParam("p2", oldFas.getDataInizio());
		qf.addWhere("f.fascicoliAccorpati > :p3");
		qf.addParam("p3", 0);
		qf.addWhere("f.opzione is null");
		qf.addOrder("f.dataInizio desc");
		qf.setPaging(0, fascicoliCount+1);
		Query q = qf.getQuery();
		List<Fascicoli> cList = (List<Fascicoli>) q.list();
		Fascicoli fas = null;
		try {
			if (cList != null) {
				int i = 0;
				int count = 0;
				while (count < fascicoliCount) {
					i++;
					fas = cList.get(i);
					count += fas.getFascicoliAccorpati();
				}
			}
		} catch (IndexOutOfBoundsException e) {
			//Ritorna null
		}
		return fas;
	}
	
	@SuppressWarnings("unchecked")
	public List<Fascicoli> findFascicoliBetweenDates(Session ses, Integer idPeriodico,
			Date dataInizio, Date dataFine) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from Fascicoli f");
		qf.addWhere("f.periodico.id = :p1");
		qf.addParam("p1", idPeriodico);
		qf.addWhere("f.dataInizio >= :p2");
		qf.addParam("p2", dataInizio);
		qf.addWhere("f.dataInizio <= :p3");
		qf.addParam("p3", dataFine);
		qf.addWhere("f.opzione is null");
		qf.addOrder("f.dataInizio desc");
		qf.addOrder("f.fascicoliAccorpati desc");
		Query q = qf.getQuery();
		List<Fascicoli> cList = (List<Fascicoli>) q.list();
		return cList;
	}
	
	///** Ritorna l'ultima data prima dell'uscita del fascicolo successivo.
	// * In pratica serve ad ottenere la data di scadenza di un abbonamento di cui invece
	// * conosciamo solo l'ultimo numero a cui ha diritto.
	// * @param ses
	// * @param fas
	// * @return
	// */
	//public Date findScadenzaFascicolo(Session ses, Fascicoli fas) throws HibernateException {
	//	Fascicoli fascicoloAfter = findFascicoliAfterFascicolo(ses, fas, 1);
	//	Calendar cal = new GregorianCalendar();
	//	cal.setTime(fascicoloAfter.getDataNominale());
	//	cal.add(Calendar.DAY_OF_MONTH, -1);
	//	return cal.getTime();
	//}
	
	/**
	 * Ritorna i fascicoli-opzione abbinati al fascicolo fas
	 * @param ses
	 * @param fas
	 * @return
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public List<Fascicoli> findFascicoliOpzioniAbbinati(Session ses, Fascicoli fas) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from Fascicoli f");
		qf.addWhere("f.fascicoloAbbinato.id = :p1");
		qf.addParam("p1", fas.getId());
		Query q = qf.getQuery();
		List<Fascicoli> supList = (List<Fascicoli>) q.list();
		return supList;
	}
	
	/**
	 * Restituisce i fascicoli per cui non sono ancora state spedite le comunicazioni
	 */
	@SuppressWarnings("unchecked")
	public List<Fascicoli> findByComunicazioniMancanti(Session ses, boolean includeAllegati) throws HibernateException {
		String qs = "from Fascicoli f where " +
				"f.dataEstrazione is not null and " +
				"f.comunicazioniInviate = :b1 and " +
				"f.opzione is null ";//non deve essere un opzione
		if (!includeAllegati) qs += "and f.fascicoliAccorpati > :i1 ";//solo i fascicoli con peso > 0
		qs += "order by f.id asc ";
		Query q = ses.createQuery(qs);
		q.setParameter("b1", Boolean.FALSE);
		if (!includeAllegati) q.setParameter("i1", 0, IntegerType.INSTANCE);
		List<Fascicoli> fasList = q.list();
		return fasList;
	}
	
	/**
	 * Restituisce i Fascicoli per cui esistono delle
	 * EvasioniComunicazioni accodate ma non ancora spedite, per il tipoMedia specificato.
	 */
	@SuppressWarnings("unchecked")
	public Map<Fascicoli, Integer> findByEnqueuedComunicazioniMedia(Session ses, String idTipoMedia) throws HibernateException {
		String qs = "select ec.fascicolo, count(ec.id) from EvasioniComunicazioni ec where " +
				"ec.dataEstrazione is null and " +
				"ec.eliminato = :b1 and " +
				"ec.comunicazione is not null and " +
				"ec.comunicazione.idTipoMedia = :s1 and " +
				"ec.fascicolo is not null " +//non deve essere un opzione
				"group by ec.fascicolo "+
				"order by ec.id asc ";
		Query q = ses.createQuery(qs);
		q.setParameter("b1", Boolean.FALSE);
		q.setParameter("s1", idTipoMedia, StringType.INSTANCE);
		List<Object[]> coupleList = q.list();
		Map<Fascicoli, Integer> fasMap = new HashMap<Fascicoli, Integer>();
		for (Object[] couple:coupleList) {
			Integer qty = ((Long)couple[1]).intValue();
			fasMap.put((Fascicoli)couple[0], qty);
		}
		return fasMap;
	}
	
	/**
	 * Restituisce i Fascicoli per cui esistono delle
	 * EvasioniComunicazioni accodate ma non ancora spedite, per il tipoMedia specificato.
	 */
	@SuppressWarnings("unchecked")
	public List<Fascicoli> findByEnqueuedComunicazioniPeriodicoMedia(Session ses, Integer idPeriodico, String idTipoMedia) throws HibernateException {
		String qs = "select distinct ec.fascicolo from EvasioniComunicazioni ec where " +
				"ec.dataEstrazione is null and " +
				"ec.eliminato = :b1 and " +
				"ec.comunicazione is not null and " +
				"ec.comunicazione.idTipoMedia = :s1 and " +
				"ec.fascicolo is not null and " +//non deve essere un opzione
				"ec.fascicolo.periodico.id = :id1 " +
				"order by ec.id asc ";
		Query q = ses.createQuery(qs);
		q.setParameter("b1", Boolean.FALSE);
		q.setParameter("s1", idTipoMedia, StringType.INSTANCE);
		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		List<Fascicoli> fasList = q.list();
		return fasList;
	}
	
//	public Fascicoli changeFascicoloToMatchStartingMonth(Session ses,
//			Listini lst /*, Fascicoli tentativeFascicoloInizio*/) {
//		Calendar cal = new GregorianCalendar();
//		//cal.setTime(tentativeFascicoloInizio.getDataNominale());//Calendario con l'attuale data del fascicolo iniziale
//		Date dataNominale = cal.getTime();
//		Integer jMeseCorrente = cal.get(Calendar.MONTH);//=jMeseCorrente
//		if (!lst.getMeseInizio().equals(jMeseCorrente+1)) {
//			if (lst.getMeseInizio() <= 3) {//Gennaio
//				cal.add(Calendar.MONTH, AppConstants.MESE_INIZIO_MONTHS_FORWARD);
//				cal.set(Calendar.MONTH, lst.getMeseInizio()-1);
//			} else {
//				//l'anno è dato da: (jMeseCorrente)-(jMeseListino)+(monthsForward)
//				//infatti se monthsForward è 0 il cambio anno corrisponde a meseListino
//				cal.add(Calendar.MONTH, (-1)*(lst.getMeseInizio()-1));// (meseInizio-1)=jMeseListino
//				cal.add(Calendar.MONTH, AppConstants.MESE_INIZIO_MONTHS_FORWARD);
//				cal.set(Calendar.MONTH, lst.getMeseInizio()-1);
//			}
//			dataNominale = cal.getTime();
//		}
//		Fascicoli fascicoloInizio = new FascicoliDao()
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
	
	public Fascicoli changeFascicoloToMatchStartingMonth(Session ses,
			Listini lst /*, Fascicoli tentativeFascicoloInizio*/) {
		Calendar cal = new GregorianCalendar();
		Date dataNominale = cal.getTime();
		int count = MonthBusiness.getMonthsToSpecificMonth(dataNominale, lst.getMeseInizio()-1);
		if (count <= AppConstants.MESE_INIZIO_MONTHS_FORWARD) {
			dataNominale = MonthBusiness.getFirstDayOfSpecificMonth(dataNominale, lst.getMeseInizio()-1);
		} else {
			dataNominale = MonthBusiness.getFirstDayOfPastMonth(dataNominale, lst.getMeseInizio()-1);
		}
		Fascicoli fascicoloInizio = new FascicoliDao()
				.findFascicoloByPeriodicoDataInizio(ses,
						lst.getTipoAbbonamento().getPeriodico().getId(), dataNominale);
		if (fascicoloInizio == null) {
			cal.add(Calendar.YEAR, -1);
			fascicoloInizio = findFascicoloByPeriodicoDataInizio(ses,
					lst.getTipoAbbonamento().getPeriodico().getId(),
					cal.getTime());
		}
		return fascicoloInizio;
	}
}
