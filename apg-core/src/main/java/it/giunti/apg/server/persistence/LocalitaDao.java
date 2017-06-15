package it.giunti.apg.server.persistence;

import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.Localita;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class LocalitaDao implements BaseDao<Localita> {

	@Override
	public void update(Session ses, Localita instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, Localita transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, Localita instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public Localita findCapByCapNumber(Session ses, String capString) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from Localita c");
		qf.addWhere("c.cap = :p1");
		qf.addParam("p1", capString);
		Query q = qf.getQuery();
		List<Localita> list = (List<Localita>) q.list();
		if (list != null) {
			if (list.size() > 0) {
				return list.get(0);
			}
		}
		return null;
	}
	
	/** Restituisce il cap della località se univoco.
	 * Se cap di città con stradario restituisce le prime 3 cifre.
	 * Altrimenti null
	 * @param ses
	 * @param localita
	 * @return
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public Localita findCapByLocalitaProv(Session ses, String localita, String optionalProv) throws HibernateException, EmptyResultException {
		QueryFactory qf = new QueryFactory(ses, "from Localita c");
		qf.addWhere("c.nome like :p1");
		qf.addParam("p1", localita);
		Query q = qf.getQuery();
		List<Localita> list = (List<Localita>) q.list();
		if (list != null) {
			if (list.size() == 0) {
				throw new EmptyResultException("Il nome '"+localita+"' non corrisponde ad una localita' nota");
			}
			if (list.size() == 1) {
				return list.get(0);
			} else {
				//Se ci sono più province con la località allora filtra in base a optionalProv
				//Split by provincia
				Map<String, List<Localita>> capListMap = new HashMap<String, List<Localita>>();
				for (Localita c:list) {
					List<Localita> capList = capListMap.get(c.getIdProvincia());
					if (capList == null) {
						//nuovo raggruppamento di cap
						capList = new ArrayList<Localita>();
						capListMap.put(c.getIdProvincia(), capList);
					}
					capList.add(c);
				}
				//adesso capListMap è una mappa con le liste dei cap per provincia
				List<Localita> provCapList = capListMap.get(optionalProv);
				if (provCapList != null) {
					list = provCapList;
				} else {
					//Se l'elenco corrispondente alla provincia è vuoto
					//allora ritorna quello di una provincia qualsiasi
					String[] provArray = capListMap.keySet().toArray(new String[0]);
					if (provArray.length > 0) list = capListMap.get(provArray[0]);
				}
				if (list.size() > 1) {
					//Altrimenti propone il cap scorciato per le cifre dello stradario
					Localita result = list.get(0);
					result.setCap(result.getCap().substring(0, 3));
					return result;
				} else {
					return list.get(0);
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Localita findCapByLocalitaCapString(Session ses, String localita, String cap) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from Localita c");
		qf.addWhere("c.nome like :p1");
		qf.addParam("p1", localita);
		qf.addWhere("c.cap = :p2");
		qf.addParam("p2", cap);
		Query q = qf.getQuery();
		List<Localita> list = (List<Localita>) q.list();
		if (list != null) {
			if (list.size() > 0) {
				return list.get(0);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Localita> findLocalitaCapSuggestions(Session ses, String localitaPrefix, String provinciaPrefix, String capPrefix) throws HibernateException {
		String localita = localitaPrefix+"%";
		if (provinciaPrefix != null) {
			if (provinciaPrefix.length() > 0) {
				localita = localitaPrefix;
			}
		}
		String prov = "%";
		if (provinciaPrefix != null) {
			prov = provinciaPrefix+"%";
			if (provinciaPrefix.length() > 1) {
				prov = provinciaPrefix;
			}
		}
		String cap = "%";
		if (capPrefix != null) {
			cap = capPrefix+"%";
			if (capPrefix.length() >= 5) {
				cap = capPrefix;
			}
		}
		QueryFactory qf = new QueryFactory(ses, "from Localita c");
		qf.addWhere("c.nome like :p1");
		qf.addParam("p1", localita);
		qf.addWhere("c.idProvincia like :p2");
		qf.addParam("p2", prov);
		qf.addWhere("c.cap like :p3");
		qf.addParam("p3", cap);
		Query q = qf.getQuery();
		List<Localita> list = (List<Localita>) q.list();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Localita> findLocalitaSuggestions(Session ses, String localitaPrefix) throws HibernateException {
		String localita = localitaPrefix+"%";
		QueryFactory qf = new QueryFactory(ses, "from Localita c");
		qf.addWhere("c.nome like :p1");
		qf.addParam("p1", localita);
		Query q = qf.getQuery();
		List<Localita> list = (List<Localita>) q.list();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public Boolean verifyLocalitaItalia(Session ses, String localitaName, String localitaProv, String localitaCap) throws HibernateException {
		if ((localitaCap == null) || (localitaProv == null)) return false;
		if (localitaCap.length() < 5) return false;
		QueryFactory qf = new QueryFactory(ses, "from Localita c");
		qf.addWhere("c.nome like :p1");
		qf.addParam("p1", localitaName);
		qf.addWhere("c.idProvincia like :p2");
		qf.addParam("p2", localitaProv);
		qf.addWhere("c.cap like :p3");
		qf.addParam("p3", localitaCap.substring(0,3)+"%");
		Query q = qf.getQuery();
		List<Localita> list = (List<Localita>) q.list();
		if (list != null) {
			if (list.size() > 0) {
				return true;
			}
		}
		return false;
	}
	
}
