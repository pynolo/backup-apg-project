package it.giunti.apg.export.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.stereotype.Repository;

import it.giunti.apg.export.model.CrmExport;

@Repository("crmExportDao")
public class CrmExportDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	@SuppressWarnings("unchecked")
	public CrmExport selectByUid(String uid) {
		Query query = entityManager.createQuery(
				"from CrmExport as ce where "+
				"ce.uid = :s1")
				.setParameter("s1", uid);
		List<CrmExport> list = (List<CrmExport>) query.getResultList();
		if (list != null) {
			if (list.size() > 0) return list.get(0);
		}
		return null;
	}
	
	public CrmExport insert(CrmExport task) {
		entityManager.persist(task);
		return task;
	}
	
	public CrmExport update(CrmExport ce) {
		CrmExport upd = selectByUid(ce.getUid());
		upd.setAddressAddress(ce.getAddressAddress());
		upd.setAddressCo(ce.getAddressCo());
		upd.setAddressCountryCode(ce.getAddressCountryCode());
		upd.setAddressFirstName(ce.getAddressFirstName());
		upd.setAddressLastNameCompany(ce.getAddressLastNameCompany());
		upd.setAddressLocality(ce.getAddressLocality());
		upd.setAddressProvince(ce.getAddressProvince());
		upd.setAddressTitle(ce.getAddressTitle());
		upd.setAddressZip(ce.getAddressZip());
		upd.setBirthDate(ce.getBirthDate());
		upd.setCodFisc(ce.getCodFisc());
		upd.setConsentMarketing(ce.isConsentMarketing());
		upd.setConsentProfiling(ce.isConsentProfiling());
		upd.setConsentTos(ce.isConsentTos());
		upd.setConsentUpdateDate(ce.getConsentUpdateDate());
		upd.setDeleted(ce.isDeleted());
		//TODO
		entityManager.merge(upd);
		entityManager.flush();
		return ce;
	}
	
	public void delete(String uid) {
		CrmExport ce = selectByUid(uid);
		entityManager.merge(ce);
		entityManager.remove(ce);
		entityManager.flush();
	}
	
	@SuppressWarnings("unchecked")
	public List<CrmExport> findByStartTimestamp(Date startTimestamp, int firstResult, int maxResult) {
		Query query = entityManager.createQuery(
				"from CrmExport as ce where "+
				"ce.updateTimestamp >= :s1")
				.setParameter("s1", startTimestamp, TemporalType.TIMESTAMP);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		List<CrmExport> list = (List<CrmExport>) query.getResultList();
		return list;
	}
}
