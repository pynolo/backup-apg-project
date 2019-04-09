package it.giunti.apg.core.persistence;

import it.giunti.apg.shared.model.Adesioni;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class AdesioniDao implements BaseDao<Adesioni> {


	@Override
	public void update(Session ses, Adesioni instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, Adesioni transientInstance)
			throws HibernateException {
		Serializable key = ses.save(transientInstance);
		return key;
	}
	
	@Override
	public void delete(Session ses, Adesioni instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public List<Adesioni> findByPrefix(Session ses, String filterPrefix,
			int offset, int pageSize) throws HibernateException {
		//ricerca dell'ultima istanza
		String hql = "from Adesioni as a " +
				"order by a.codice asc";
		if (filterPrefix != null) {
			if (filterPrefix.length() > 0) {
				hql = "from Adesioni as a where " +
						"a.codice like :s1 " +
						"order by a.codice asc";
			}
		}
		Query q = ses.createQuery(hql);
		if (filterPrefix != null) {
			if (filterPrefix.length() > 0) {
				q.setString("s1", filterPrefix+"%");
			}
		}
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<Adesioni> nList = (List<Adesioni>) q.list();
		return nList;
	}
	
	@SuppressWarnings("unchecked")
	public Adesioni findByCodice(Session ses, String codice)
			throws HibernateException {
		String qs = "from Adesioni as ade where " +
				"ade.codice like :s1 " +
				"order by ade.id ";
		Query q = ses.createQuery(qs);
		q.setString("s1", codice);
		List<Adesioni> dList= (List<Adesioni>) q.list();
		if (dList != null) {
			if (dList.size() == 1) {
				return dList.get(0);
			} else {
				throw new HibernateException(dList.size()+" Adesioni with codice="+codice);
			}
		} else {
			throw new HibernateException("No Adesioni with codice="+codice);
		}
	}
	
}
