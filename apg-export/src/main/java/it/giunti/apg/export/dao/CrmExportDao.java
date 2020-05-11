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
	
	public void flushClear() {
		entityManager.flush();
		entityManager.clear();
	}
	
	public CrmExport selectById(String uid) {
		CrmExport ce = entityManager.find(CrmExport.class, uid);
		return ce;
	}
	
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
	
	public CrmExport insert(CrmExport ce) {
		entityManager.persist(ce);
		return ce;
	}
	
	public CrmExport update(CrmExport ce) {
		CrmExport upd = selectByUid(ce.getUid());
		upd.setDeleted(ce.isDeleted());
		upd.setMergedIntoUid(ce.getMergedIntoUid());
		upd.setAddressTitle(ce.getAddressTitle());
		upd.setAddressFirstName(ce.getAddressFirstName());
		upd.setAddressLastNameCompany(ce.getAddressLastNameCompany());
		upd.setAddressCo(ce.getAddressCo());
		upd.setAddressAddress(ce.getAddressAddress());
		upd.setAddressLocality(ce.getAddressLocality());
		upd.setAddressProvince(ce.getAddressProvince());
		upd.setAddressZip(ce.getAddressZip());
		upd.setAddressCountryCode(ce.getAddressCountryCode());
		upd.setSex(ce.getSex());
		upd.setCodFisc(ce.getCodFisc());
		upd.setPiva(ce.getPiva());
		upd.setPhoneMobile(ce.getPhoneMobile());
		upd.setPhoneLandline(ce.getPhoneLandline());
		upd.setEmailPrimary(ce.getEmailPrimary());
		upd.setIdJob(ce.getIdJob());
		upd.setIdQualification(ce.getIdQualification());
		upd.setIdTipoAnagrafica(ce.getIdTipoAnagrafica());
		upd.setBirthDate(ce.getBirthDate());
		upd.setConsentTos(ce.isConsentTos());
		upd.setConsentMarketing(ce.isConsentMarketing());
		upd.setConsentProfiling(ce.isConsentProfiling());
		upd.setConsentUpdateDate(ce.getConsentUpdateDate());

		upd.setOwnSubscriptionIdentifier0(ce.getOwnSubscriptionIdentifier0());
		upd.setOwnSubscriptionMedia0(ce.getOwnSubscriptionMedia0());
		upd.setOwnSubscriptionStatus0(ce.getOwnSubscriptionStatus0());
		upd.setOwnSubscriptionCreationDate0(ce.getOwnSubscriptionCreationDate0());
		upd.setOwnSubscriptionEndDate0(ce.getOwnSubscriptionEndDate0());
		upd.setGiftSubscriptionEndDate0(ce.getGiftSubscriptionEndDate0());

		upd.setOwnSubscriptionIdentifier1(ce.getOwnSubscriptionIdentifier1());
		upd.setOwnSubscriptionMedia1(ce.getOwnSubscriptionMedia1());
		upd.setOwnSubscriptionStatus1(ce.getOwnSubscriptionStatus1());
		upd.setOwnSubscriptionCreationDate1(ce.getOwnSubscriptionCreationDate1());
		upd.setOwnSubscriptionEndDate1(ce.getOwnSubscriptionEndDate1());
		upd.setGiftSubscriptionEndDate1(ce.getGiftSubscriptionEndDate1());
		
		upd.setOwnSubscriptionIdentifier2(ce.getOwnSubscriptionIdentifier2());
		upd.setOwnSubscriptionMedia2(ce.getOwnSubscriptionMedia2());
		upd.setOwnSubscriptionStatus2(ce.getOwnSubscriptionStatus2());
		upd.setOwnSubscriptionCreationDate2(ce.getOwnSubscriptionCreationDate2());
		upd.setOwnSubscriptionEndDate2(ce.getOwnSubscriptionEndDate2());
		upd.setGiftSubscriptionEndDate2(ce.getGiftSubscriptionEndDate2());

		upd.setOwnSubscriptionIdentifier3(ce.getOwnSubscriptionIdentifier3());
		upd.setOwnSubscriptionMedia3(ce.getOwnSubscriptionMedia3());
		upd.setOwnSubscriptionStatus3(ce.getOwnSubscriptionStatus3());
		upd.setOwnSubscriptionCreationDate3(ce.getOwnSubscriptionCreationDate3());
		upd.setOwnSubscriptionEndDate3(ce.getOwnSubscriptionEndDate3());
		upd.setGiftSubscriptionEndDate3(ce.getGiftSubscriptionEndDate3());

		upd.setOwnSubscriptionIdentifier4(ce.getOwnSubscriptionIdentifier4());
		upd.setOwnSubscriptionMedia4(ce.getOwnSubscriptionMedia4());
		upd.setOwnSubscriptionStatus4(ce.getOwnSubscriptionStatus4());
		upd.setOwnSubscriptionCreationDate4(ce.getOwnSubscriptionCreationDate4());
		upd.setOwnSubscriptionEndDate4(ce.getOwnSubscriptionEndDate4());
		upd.setGiftSubscriptionEndDate4(ce.getGiftSubscriptionEndDate4());

		upd.setOwnSubscriptionIdentifier5(ce.getOwnSubscriptionIdentifier5());
		upd.setOwnSubscriptionMedia5(ce.getOwnSubscriptionMedia5());
		upd.setOwnSubscriptionStatus5(ce.getOwnSubscriptionStatus5());
		upd.setOwnSubscriptionCreationDate5(ce.getOwnSubscriptionCreationDate5());
		upd.setOwnSubscriptionEndDate5(ce.getOwnSubscriptionEndDate5());
		upd.setGiftSubscriptionEndDate5(ce.getGiftSubscriptionEndDate5());

		upd.setOwnSubscriptionIdentifier6(ce.getOwnSubscriptionIdentifier6());
		upd.setOwnSubscriptionMedia6(ce.getOwnSubscriptionMedia6());
		upd.setOwnSubscriptionStatus6(ce.getOwnSubscriptionStatus6());
		upd.setOwnSubscriptionCreationDate6(ce.getOwnSubscriptionCreationDate6());
		upd.setOwnSubscriptionEndDate6(ce.getOwnSubscriptionEndDate6());
		upd.setGiftSubscriptionEndDate6(ce.getGiftSubscriptionEndDate6());

		entityManager.merge(upd);
		entityManager.flush();
		return upd;
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
