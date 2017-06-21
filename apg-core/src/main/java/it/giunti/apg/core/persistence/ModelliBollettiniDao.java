package it.giunti.apg.core.persistence;

import it.giunti.apg.shared.model.ModelliBollettini;
import it.giunti.apg.shared.model.Periodici;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;

public class ModelliBollettiniDao implements BaseDao<ModelliBollettini> {

	@Override
	public void update(Session ses, ModelliBollettini instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, ModelliBollettini transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, ModelliBollettini instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public List<ModelliBollettini> findModelliBollettini(
			Session ses,
			Integer offset, Integer size) throws HibernateException {
		String qs = "from ModelliBollettini bm " +
				"order by bm.descr asc ";
		Query q = ses.createQuery(qs);
		q.setFirstResult(offset);
	    q.setMaxResults(size);
		List<ModelliBollettini> bmList = (List<ModelliBollettini>) q.list();
		return bmList;
	}
	
	@SuppressWarnings("unchecked")
	public List<ModelliBollettini> findModelliBollettiniByPeriodico(Session ses, 
			Integer idPeriodico, Integer offset, Integer size) throws HibernateException {
		String qs = "from ModelliBollettini bm where " +
				"bm.periodico.id = :p1 "+
				"order by bm.predefinitoPeriodico desc, bm.descr asc ";
		Query q = ses.createQuery(qs);
		q.setInteger("p1", idPeriodico);
		q.setFirstResult(offset);
	    q.setMaxResults(size);
		List<ModelliBollettini> bmList = (List<ModelliBollettini>) q.list();
		return bmList;
	}
	
	@SuppressWarnings("unchecked")
	public ModelliBollettini findModelliBollettiniPredefinitoByPeriodico(Session ses, 
			Integer idPeriodico) throws HibernateException {
		String qs = "from ModelliBollettini mb where " +
				"mb.periodico.id = :p1 and "+
				"mb.predefinitoPeriodico = :b1";
		Query q = ses.createQuery(qs);
		q.setParameter("p1", idPeriodico, IntegerType.INSTANCE);
		q.setParameter("b1", Boolean.TRUE);
		List<ModelliBollettini> mbList = (List<ModelliBollettini>) q.list();
		ModelliBollettini mb = null;
		if (mbList != null) {
			if (mbList.size() > 0) mb = mbList.get(0); 
		}
		return mb;
	}
	
	public ModelliBollettini createModelliBollettini(Session ses, Integer idPeriodico, String testoBandella) {
		ModelliBollettini result = new ModelliBollettini();
		Periodici periodico = GenericDao.findById(ses, Periodici.class, idPeriodico);
		ModelliBollettini modelloPeriodico = new ModelliBollettiniDao().findModelliBollettiniPredefinitoByPeriodico(ses, idPeriodico);
		result.setPeriodico(periodico);
		result.setAutorizzazione(modelloPeriodico.getAutorizzazione());
		result.setCodiceModello(modelloPeriodico.getCodiceModello());
		result.setLogoVerticalPath(modelloPeriodico.getLogoVerticalPath());
		result.setLogoSmallPath(modelloPeriodico.getLogoSmallPath());
		result.setReportFilePath(modelloPeriodico.getReportFilePath());
		result.setTestoBandella(testoBandella);
		result.setPredefinitoPeriodico(false);
		return result;
	}
}
