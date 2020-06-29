package it.giunti.apg.export.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import it.giunti.apg.export.model.CrmExportConfig;

@Repository("crmExportConfigDao")
public class CrmExportConfigDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	public CrmExportConfig selectById(String id) {
		CrmExportConfig cec = entityManager.find(CrmExportConfig.class, id);
		return cec;
	}
	
	public CrmExportConfig insert(CrmExportConfig cec) {
		entityManager.persist(cec);
		return cec;
	}
	
	public CrmExportConfig update(CrmExportConfig cec) {
		CrmExportConfig cecToUpdate = selectById(cec.getId());
		cecToUpdate.setVal(cec.getVal());
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
