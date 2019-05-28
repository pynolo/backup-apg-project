package it.giunti.apg.core.persistence;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import it.giunti.apg.shared.model.Professioni;

public class ProfessioniDao {

	@SuppressWarnings("unchecked")
	public Professioni findByName(Session ses, String name) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from Professioni p");
		qf.addWhere("p.nome like :p1");
		qf.addParam("p1", name);
		Query q = qf.getQuery();
		List<Professioni> list = (List<Professioni>) q.list();
		if (list != null) {
			if (list.size() > 0) {
				return list.get(0);
			}
		}
		return null;
	}
	
}
