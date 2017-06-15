package it.giunti.apg.server.persistence;

import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.StatInvio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;

public class StatInvioDao implements BaseDao<StatInvio> {

	@Override
	public void update(Session ses, StatInvio instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, StatInvio transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, StatInvio instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public List<StatInvio> findLastStatInvio(Session ses, Integer idPeriodico)
			throws HibernateException {
		//Cerca l'ultimo fascicoli inviato
		String fasHql = "select distinct si.fascicolo from StatInvio si where " +
				"si.fascicolo.periodico.id = :id1 " +
				"order by si.fascicolo.dataInizio desc";
		Query fasQ = ses.createQuery(fasHql);
		fasQ.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		List<Fascicoli> fasList = fasQ.list();
		if (fasList == null) return null;
		if (fasList.size() == 0) return null;
		Fascicoli lastFas = fasList.get(0);
		//ricerca i valori di invio relativi all'ultimo fascicolo
		List<StatInvio> siList = findStatInvioByFascicolo(ses, lastFas.getId());
		//Ordinamento
		Collections.sort(siList, new Comparator<StatInvio>() {
			@Override
			public int compare(StatInvio o1, StatInvio o2) {
				String s1 = o1.getTipoAbbonamento().getCodice()+o1.getTipoAbbonamento().getNome();
				String s2 = o2.getTipoAbbonamento().getCodice()+o2.getTipoAbbonamento().getNome();
				return s1.compareTo(s2);
			}
		});
		return siList;
	}
	
	@SuppressWarnings("unchecked")
	public List<StatInvio> findStatInvioByFascicolo(Session ses, Integer idFas)
			throws HibernateException {
		//ricerca i valori di invio relativi al fascicolo
		String hql = "from StatInvio as si where " +
				"si.fascicolo.id = :id1 ";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idFas, IntegerType.INSTANCE);
		List<StatInvio> siList = (List<StatInvio>) q.list();
		return siList;
	}
	
	@SuppressWarnings("unchecked")
	public List<List<StatInvio>> findOrderedStatInvio(Session ses, Integer idPeriodico)
			throws HibernateException {
		//ricerca i valori all'ultima data
		String hql = "from StatInvio as si where " +
				"si.fascicolo.periodico.id = :id1 " +
				"order by si.fascicolo.dataInizio asc";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		List<StatInvio> siList = (List<StatInvio>) q.list();
		//La lista deve essere trasformata nella lista di liste
		List<List<StatInvio>> result = new ArrayList<List<StatInvio>>();
		Map<Fascicoli, List<StatInvio>> listMap = new HashMap<Fascicoli, List<StatInvio>>();
		//Crea una mappa con le liste delle singole statistiche
		for (StatInvio si:siList) {
			List<StatInvio> fillingList = listMap.get(si.getFascicolo());
			if (fillingList == null) {
				fillingList = new ArrayList<StatInvio>();
				listMap.put(si.getFascicolo(), fillingList);
			}
			fillingList.add(si);
		}
		//le statistiche dei fascicoli NON SONO in ordine temporale => ordino i fascicoli
		List<Fascicoli> fasList = new ArrayList<Fascicoli>(listMap.keySet());
		Collections.sort(fasList, new Comparator<Fascicoli>() {
			public int compare(Fascicoli o1, Fascicoli o2) {
				Date dt1 = o1.getDataInizio();
				Date dt2 = o2.getDataInizio();
				return dt1.compareTo(dt2);
			}
		});
		//La lista delle liste viene popolata secondo l'ordine temporale dei fascicoli
		for(Fascicoli fas:fasList) {
			List<StatInvio> fasStatList = listMap.get(fas);
			result.add(fasStatList);
		}
		return result;
	}
	
//	@SuppressWarnings("unchecked")
//	public List<StatInvio> findByDates(Session ses, Integer idPeriodico,
//			Date dataInizio , Date dataFine) throws HibernateException {
//		//ricerca dell'ultima istanza
//		String hql = "from StatInvio as si where " +
//				"si.fascicolo.periodico.id = :id1 and " +
//				"si.dataCreazione >= :dt1 and " +
//				"si.dataCreazione <= :dt2 " +
//				"order by si.dataCreazione asc";
//		Query q = ses.createQuery(hql);
//		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
//		q.setParameter("dt1", dataInizio, DateType.INSTANCE);
//		q.setParameter("dt2", dataFine, DateType.INSTANCE);
//		List<StatInvio> saList = (List<StatInvio>) q.list();
//		return saList;
//	}
	

}
