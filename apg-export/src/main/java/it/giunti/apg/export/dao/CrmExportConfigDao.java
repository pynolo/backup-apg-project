package it.giunti.apg.export.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import it.giunti.apg.export.model.CrmExportConfig;

@Repository("crmExportConfigDao")
public class CrmExportConfigDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	@SuppressWarnings("unchecked")
	public CrmExportConfig selectById(String id) {
		Query query = entityManager.createQuery(
				"from CrmExportConfig as cec where "+
				"cec.id = :s1")
				.setParameter("s1", id);
		List<CrmExportConfig> list = (List<CrmExportConfig>) query.getResultList();
		if (list != null) {
			if (list.size() > 0) return list.get(0);
		}
		return null;
	}
	
	public CrmExportConfig insert(CrmExportConfig task) {
		entityManager.persist(task);
		return task;
	}
	
	public CrmExportConfig update(CrmExportConfig cec) {
		CrmExportConfig cecToUpdate = selectById(cec.getId());
		cecToUpdate.setUpdateTimestamp(cec.getUpdateTimestamp());
		entityManager.merge(cecToUpdate);
		entityManager.flush();
		return cec;
	}
	
	public void delete(String id) {
		CrmExportConfig cec = selectById(id);
		entityManager.merge(cec);
		entityManager.remove(cec);
		entityManager.flush();
	}
}
