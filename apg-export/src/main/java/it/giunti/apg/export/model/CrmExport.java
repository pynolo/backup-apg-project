package it.giunti.apg.export.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "crm_export")
public class CrmExport {
	
	@Id
	@Basic(optional = false)
	@Column(name = "uid", length = 16, nullable = false)
	private String uid;
	@Column(name = "identity_uid", length = 32)
	private String identityUid;
	@Basic(optional = false)
	@Column(name = "deleted", nullable = false)
	private boolean deleted;
	@Column(name = "merged_into_uid", length = 16)
	private String mergedIntoUid;
	@Column(name = "update_timestamp", updatable=false, insertable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTimestamp;
	@Column(name = "address_title", length = 32)
	private String addressTitle;
	@Column(name = "address_first_name", length = 32)
	private String addressFirstName;
	@Column(name = "address_last_name_company", length = 64)
	private String addressLastNameCompany;
	@Column(name = "address_co", length = 64)
	private String addressCo;
	@Column(name = "address_address", length = 128)
	private String addressAddress;
	@Column(name = "address_locality", length = 64)
	private String addressLocality;
	@Column(name = "address_province", length = 4)
	private String addressProvince;
	@Column(name = "address_zip", length = 8)
	private String addressZip;
	@Column(name = "address_country_code", length = 2)
	private String addressCountryCode;
	@Column(name = "sex", length = 1)
	private String sex;
	@Column(name = "cod_fisc", length = 16)
	private String codFisc;
	@Column(name = "piva", length = 16)
	private String piva;
	@Column(name = "phone_mobile", length = 32)
	private String phoneMobile;
	@Column(name = "phone_landline", length = 32)
	private String phoneLandline;
	@Column(name = "email_primary", length = 128)
	private String emailPrimary;
	@Column(name = "id_job")
	private Integer idJob;
	@Column(name = "id_qualification")
	private Integer idQualification;
	@Column(name = "id_tipo_anagrafica", length = 8)
	private String idTipoAnagrafica;
	@Column(name = "birth_date")
	@Temporal(TemporalType.DATE)
	private Date birthDate;
	@Column(name = "consent_tos")
	private boolean consentTos;
	@Column(name = "consent_marketing")
	private boolean consentMarketing;
	@Column(name = "consent_profiling")
	private boolean consentProfiling;
	@Column(name = "consent_update_date")
	@Temporal(TemporalType.DATE)
	private Date consentUpdateDate;

	@Column(name = "own_subscription_identifier_0", length = 16)
	private String ownSubscriptionIdentifier0;
	@Column(name = "own_subscription_media_0", length = 2)
	private String ownSubscriptionMedia0;
	@Column(name = "own_subscription_status_0", length = 16)
	private String ownSubscriptionStatus0;
	@Column(name = "own_subscription_creation_date_0")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionCreationDate0;
	@Column(name = "own_subscription_end_date_0")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionEndDate0;
	@Column(name = "gift_subscription_end_date_0")
	@Temporal(TemporalType.DATE)
	private Date giftSubscriptionEndDate0;
	
	@Column(name = "own_subscription_identifier_1", length = 16)
	private String ownSubscriptionIdentifier1;
	@Column(name = "own_subscription_media_1", length = 2)
	private String ownSubscriptionMedia1;
	@Column(name = "own_subscription_status_1", length = 16)
	private String ownSubscriptionStatus1;
	@Column(name = "own_subscription_creation_date_1")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionCreationDate1;
	@Column(name = "own_subscription_end_date_1")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionEndDate1;
	@Column(name = "gift_subscription_end_date_1")
	@Temporal(TemporalType.DATE)
	private Date giftSubscriptionEndDate1;

	@Column(name = "own_subscription_identifier_2", length = 16)
	private String ownSubscriptionIdentifier2;
	@Column(name = "own_subscription_media_2", length = 2)
	private String ownSubscriptionMedia2;
	@Column(name = "own_subscription_status_2", length = 16)
	private String ownSubscriptionStatus2;
	@Column(name = "own_subscription_creation_date_2")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionCreationDate2;
	@Column(name = "own_subscription_end_date_2")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionEndDate2;
	@Column(name = "gift_subscription_end_date_2")
	@Temporal(TemporalType.DATE)
	private Date giftSubscriptionEndDate2;
	
	@Column(name = "own_subscription_identifier_3", length = 16)
	private String ownSubscriptionIdentifier3;
	@Column(name = "own_subscription_media_3", length = 2)
	private String ownSubscriptionMedia3;
	@Column(name = "own_subscription_status_3", length = 16)
	private String ownSubscriptionStatus3;
	@Column(name = "own_subscription_creation_date_3")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionCreationDate3;
	@Column(name = "own_subscription_end_date_3")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionEndDate3;
	@Column(name = "gift_subscription_end_date_3")
	@Temporal(TemporalType.DATE)
	private Date giftSubscriptionEndDate3;
	
	@Column(name = "own_subscription_identifier_4", length = 16)
	private String ownSubscriptionIdentifier4;
	@Column(name = "own_subscription_media_4", length = 2)
	private String ownSubscriptionMedia4;
	@Column(name = "own_subscription_status_4", length = 16)
	private String ownSubscriptionStatus4;
	@Column(name = "own_subscription_creation_date_4")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionCreationDate4;
	@Column(name = "own_subscription_end_date_4")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionEndDate4;
	@Column(name = "gift_subscription_end_date_4")
	@Temporal(TemporalType.DATE)
	private Date giftSubscriptionEndDate4;
	
	@Column(name = "own_subscription_identifier_5", length = 16)
	private String ownSubscriptionIdentifier5;
	@Column(name = "own_subscription_media_5", length = 2)
	private String ownSubscriptionMedia5;
	@Column(name = "own_subscription_status_5", length = 16)
	private String ownSubscriptionStatus5;
	@Column(name = "own_subscription_creation_date_5")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionCreationDate5;
	@Column(name = "own_subscription_end_date_5")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionEndDate5;
	@Column(name = "gift_subscription_end_date_5")
	@Temporal(TemporalType.DATE)
	private Date giftSubscriptionEndDate5;
	
	@Column(name = "own_subscription_identifier_6", length = 16)
	private String ownSubscriptionIdentifier6;
	@Column(name = "own_subscription_media_6", length = 2)
	private String ownSubscriptionMedia6;
	@Column(name = "own_subscription_status_6", length = 16)
	private String ownSubscriptionStatus6;
	@Column(name = "own_subscription_creation_date_6")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionCreationDate6;
	@Column(name = "own_subscription_end_date_6")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionEndDate6;
	@Column(name = "gift_subscription_end_date_6")
	@Temporal(TemporalType.DATE)
	private Date giftSubscriptionEndDate6;
	
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getIdentityUid() {
		return identityUid;
	}

	public void setIdentityUid(String identityUid) {
		this.identityUid = identityUid;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getMergedIntoUid() {
		return mergedIntoUid;
	}

	public void setMergedIntoUid(String mergedIntoUid) {
		this.mergedIntoUid = mergedIntoUid;
	}

	public Date getUpdateTimestamp() {
		return updateTimestamp;
	}

	public void setUpdateTimestamp(Date updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

	public String getAddressTitle() {
		return addressTitle;
	}

	public void setAddressTitle(String addressTitle) {
		this.addressTitle = addressTitle;
	}

	public String getAddressFirstName() {
		return addressFirstName;
	}

	public void setAddressFirstName(String addressFirstName) {
		this.addressFirstName = addressFirstName;
	}

	public String getAddressLastNameCompany() {
		return addressLastNameCompany;
	}

	public void setAddressLastNameCompany(String addressLastNameCompany) {
		this.addressLastNameCompany = addressLastNameCompany;
	}

	public String getAddressCo() {
		return addressCo;
	}

	public void setAddressCo(String addressCo) {
		this.addressCo = addressCo;
	}

	public String getAddressAddress() {
		return addressAddress;
	}

	public void setAddressAddress(String addressAddress) {
		this.addressAddress = addressAddress;
	}

	public String getAddressLocality() {
		return addressLocality;
	}

	public void setAddressLocality(String addressLocality) {
		this.addressLocality = addressLocality;
	}

	public String getAddressProvince() {
		return addressProvince;
	}

	public void setAddressProvince(String addressProvince) {
		this.addressProvince = addressProvince;
	}

	public String getAddressZip() {
		return addressZip;
	}

	public void setAddressZip(String addressZip) {
		this.addressZip = addressZip;
	}

	public String getAddressCountryCode() {
		return addressCountryCode;
	}

	public void setAddressCountryCode(String addressCountryCode) {
		this.addressCountryCode = addressCountryCode;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getCodFisc() {
		return codFisc;
	}

	public void setCodFisc(String codFisc) {
		this.codFisc = codFisc;
	}

	public String getPiva() {
		return piva;
	}

	public void setPiva(String piva) {
		this.piva = piva;
	}

	public String getPhoneMobile() {
		return phoneMobile;
	}

	public void setPhoneMobile(String phoneMobile) {
		this.phoneMobile = phoneMobile;
	}

	public String getPhoneLandline() {
		return phoneLandline;
	}

	public void setPhoneLandline(String phoneLandline) {
		this.phoneLandline = phoneLandline;
	}

	public String getEmailPrimary() {
		return emailPrimary;
	}

	public void setEmailPrimary(String emailPrimary) {
		this.emailPrimary = emailPrimary;
	}

	public Integer getIdJob() {
		return idJob;
	}

	public void setIdJob(Integer idJob) {
		this.idJob = idJob;
	}

	public Integer getIdQualification() {
		return idQualification;
	}

	public void setIdQualification(Integer idQualification) {
		this.idQualification = idQualification;
	}

	public String getIdTipoAnagrafica() {
		return idTipoAnagrafica;
	}

	public void setIdTipoAnagrafica(String idTipoAnagrafica) {
		this.idTipoAnagrafica = idTipoAnagrafica;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public boolean isConsentTos() {
		return consentTos;
	}

	public void setConsentTos(boolean consentTos) {
		this.consentTos = consentTos;
	}

	public boolean isConsentMarketing() {
		return consentMarketing;
	}

	public void setConsentMarketing(boolean consentMarketing) {
		this.consentMarketing = consentMarketing;
	}

	public boolean isConsentProfiling() {
		return consentProfiling;
	}

	public void setConsentProfiling(boolean consentProfiling) {
		this.consentProfiling = consentProfiling;
	}

	public Date getConsentUpdateDate() {
		return consentUpdateDate;
	}

	public void setConsentUpdateDate(Date consentUpdateDate) {
		this.consentUpdateDate = consentUpdateDate;
	}

	public String getOwnSubscriptionIdentifier0() {
		return ownSubscriptionIdentifier0;
	}

	public void setOwnSubscriptionIdentifier0(String ownSubscriptionIdentifier0) {
		this.ownSubscriptionIdentifier0 = ownSubscriptionIdentifier0;
	}

	public String getOwnSubscriptionMedia0() {
		return ownSubscriptionMedia0;
	}

	public void setOwnSubscriptionMedia0(String ownSubscriptionMedia0) {
		this.ownSubscriptionMedia0 = ownSubscriptionMedia0;
	}

	public String getOwnSubscriptionStatus0() {
		return ownSubscriptionStatus0;
	}

	public void setOwnSubscriptionStatus0(String ownSubscriptionStatus0) {
		this.ownSubscriptionStatus0 = ownSubscriptionStatus0;
	}

	public Date getOwnSubscriptionCreationDate0() {
		return ownSubscriptionCreationDate0;
	}

	public void setOwnSubscriptionCreationDate0(Date ownSubscriptionCreationDate0) {
		this.ownSubscriptionCreationDate0 = ownSubscriptionCreationDate0;
	}

	public Date getOwnSubscriptionEndDate0() {
		return ownSubscriptionEndDate0;
	}

	public void setOwnSubscriptionEndDate0(Date ownSubscriptionEndDate0) {
		this.ownSubscriptionEndDate0 = ownSubscriptionEndDate0;
	}

	public Date getGiftSubscriptionEndDate0() {
		return giftSubscriptionEndDate0;
	}

	public void setGiftSubscriptionEndDate0(Date giftSubscriptionEndDate0) {
		this.giftSubscriptionEndDate0 = giftSubscriptionEndDate0;
	}

	public String getOwnSubscriptionIdentifier1() {
		return ownSubscriptionIdentifier1;
	}

	public void setOwnSubscriptionIdentifier1(String ownSubscriptionIdentifier1) {
		this.ownSubscriptionIdentifier1 = ownSubscriptionIdentifier1;
	}

	public String getOwnSubscriptionMedia1() {
		return ownSubscriptionMedia1;
	}

	public void setOwnSubscriptionMedia1(String ownSubscriptionMedia1) {
		this.ownSubscriptionMedia1 = ownSubscriptionMedia1;
	}

	public String getOwnSubscriptionStatus1() {
		return ownSubscriptionStatus1;
	}

	public void setOwnSubscriptionStatus1(String ownSubscriptionStatus1) {
		this.ownSubscriptionStatus1 = ownSubscriptionStatus1;
	}

	public Date getOwnSubscriptionCreationDate1() {
		return ownSubscriptionCreationDate1;
	}

	public void setOwnSubscriptionCreationDate1(Date ownSubscriptionCreationDate1) {
		this.ownSubscriptionCreationDate1 = ownSubscriptionCreationDate1;
	}

	public Date getOwnSubscriptionEndDate1() {
		return ownSubscriptionEndDate1;
	}

	public void setOwnSubscriptionEndDate1(Date ownSubscriptionEndDate1) {
		this.ownSubscriptionEndDate1 = ownSubscriptionEndDate1;
	}

	public Date getGiftSubscriptionEndDate1() {
		return giftSubscriptionEndDate1;
	}

	public void setGiftSubscriptionEndDate1(Date giftSubscriptionEndDate1) {
		this.giftSubscriptionEndDate1 = giftSubscriptionEndDate1;
	}

	public String getOwnSubscriptionIdentifier2() {
		return ownSubscriptionIdentifier2;
	}

	public void setOwnSubscriptionIdentifier2(String ownSubscriptionIdentifier2) {
		this.ownSubscriptionIdentifier2 = ownSubscriptionIdentifier2;
	}

	public String getOwnSubscriptionMedia2() {
		return ownSubscriptionMedia2;
	}

	public void setOwnSubscriptionMedia2(String ownSubscriptionMedia2) {
		this.ownSubscriptionMedia2 = ownSubscriptionMedia2;
	}

	public String getOwnSubscriptionStatus2() {
		return ownSubscriptionStatus2;
	}

	public void setOwnSubscriptionStatus2(String ownSubscriptionStatus2) {
		this.ownSubscriptionStatus2 = ownSubscriptionStatus2;
	}

	public Date getOwnSubscriptionCreationDate2() {
		return ownSubscriptionCreationDate2;
	}

	public void setOwnSubscriptionCreationDate2(Date ownSubscriptionCreationDate2) {
		this.ownSubscriptionCreationDate2 = ownSubscriptionCreationDate2;
	}

	public Date getOwnSubscriptionEndDate2() {
		return ownSubscriptionEndDate2;
	}

	public void setOwnSubscriptionEndDate2(Date ownSubscriptionEndDate2) {
		this.ownSubscriptionEndDate2 = ownSubscriptionEndDate2;
	}

	public Date getGiftSubscriptionEndDate2() {
		return giftSubscriptionEndDate2;
	}

	public void setGiftSubscriptionEndDate2(Date giftSubscriptionEndDate2) {
		this.giftSubscriptionEndDate2 = giftSubscriptionEndDate2;
	}

	public String getOwnSubscriptionIdentifier3() {
		return ownSubscriptionIdentifier3;
	}

	public void setOwnSubscriptionIdentifier3(String ownSubscriptionIdentifier3) {
		this.ownSubscriptionIdentifier3 = ownSubscriptionIdentifier3;
	}

	public String getOwnSubscriptionMedia3() {
		return ownSubscriptionMedia3;
	}

	public void setOwnSubscriptionMedia3(String ownSubscriptionMedia3) {
		this.ownSubscriptionMedia3 = ownSubscriptionMedia3;
	}

	public String getOwnSubscriptionStatus3() {
		return ownSubscriptionStatus3;
	}

	public void setOwnSubscriptionStatus3(String ownSubscriptionStatus3) {
		this.ownSubscriptionStatus3 = ownSubscriptionStatus3;
	}

	public Date getOwnSubscriptionCreationDate3() {
		return ownSubscriptionCreationDate3;
	}

	public void setOwnSubscriptionCreationDate3(Date ownSubscriptionCreationDate3) {
		this.ownSubscriptionCreationDate3 = ownSubscriptionCreationDate3;
	}

	public Date getOwnSubscriptionEndDate3() {
		return ownSubscriptionEndDate3;
	}

	public void setOwnSubscriptionEndDate3(Date ownSubscriptionEndDate3) {
		this.ownSubscriptionEndDate3 = ownSubscriptionEndDate3;
	}

	public Date getGiftSubscriptionEndDate3() {
		return giftSubscriptionEndDate3;
	}

	public void setGiftSubscriptionEndDate3(Date giftSubscriptionEndDate3) {
		this.giftSubscriptionEndDate3 = giftSubscriptionEndDate3;
	}

	public String getOwnSubscriptionIdentifier4() {
		return ownSubscriptionIdentifier4;
	}

	public void setOwnSubscriptionIdentifier4(String ownSubscriptionIdentifier4) {
		this.ownSubscriptionIdentifier4 = ownSubscriptionIdentifier4;
	}

	public String getOwnSubscriptionMedia4() {
		return ownSubscriptionMedia4;
	}

	public void setOwnSubscriptionMedia4(String ownSubscriptionMedia4) {
		this.ownSubscriptionMedia4 = ownSubscriptionMedia4;
	}

	public String getOwnSubscriptionStatus4() {
		return ownSubscriptionStatus4;
	}

	public void setOwnSubscriptionStatus4(String ownSubscriptionStatus4) {
		this.ownSubscriptionStatus4 = ownSubscriptionStatus4;
	}

	public Date getOwnSubscriptionCreationDate4() {
		return ownSubscriptionCreationDate4;
	}

	public void setOwnSubscriptionCreationDate4(Date ownSubscriptionCreationDate4) {
		this.ownSubscriptionCreationDate4 = ownSubscriptionCreationDate4;
	}

	public Date getOwnSubscriptionEndDate4() {
		return ownSubscriptionEndDate4;
	}

	public void setOwnSubscriptionEndDate4(Date ownSubscriptionEndDate4) {
		this.ownSubscriptionEndDate4 = ownSubscriptionEndDate4;
	}

	public Date getGiftSubscriptionEndDate4() {
		return giftSubscriptionEndDate4;
	}

	public void setGiftSubscriptionEndDate4(Date giftSubscriptionEndDate4) {
		this.giftSubscriptionEndDate4 = giftSubscriptionEndDate4;
	}

	public String getOwnSubscriptionIdentifier5() {
		return ownSubscriptionIdentifier5;
	}

	public void setOwnSubscriptionIdentifier5(String ownSubscriptionIdentifier5) {
		this.ownSubscriptionIdentifier5 = ownSubscriptionIdentifier5;
	}

	public String getOwnSubscriptionMedia5() {
		return ownSubscriptionMedia5;
	}

	public void setOwnSubscriptionMedia5(String ownSubscriptionMedia5) {
		this.ownSubscriptionMedia5 = ownSubscriptionMedia5;
	}

	public String getOwnSubscriptionStatus5() {
		return ownSubscriptionStatus5;
	}

	public void setOwnSubscriptionStatus5(String ownSubscriptionStatus5) {
		this.ownSubscriptionStatus5 = ownSubscriptionStatus5;
	}

	public Date getOwnSubscriptionCreationDate5() {
		return ownSubscriptionCreationDate5;
	}

	public void setOwnSubscriptionCreationDate5(Date ownSubscriptionCreationDate5) {
		this.ownSubscriptionCreationDate5 = ownSubscriptionCreationDate5;
	}

	public Date getOwnSubscriptionEndDate5() {
		return ownSubscriptionEndDate5;
	}

	public void setOwnSubscriptionEndDate5(Date ownSubscriptionEndDate5) {
		this.ownSubscriptionEndDate5 = ownSubscriptionEndDate5;
	}

	public Date getGiftSubscriptionEndDate5() {
		return giftSubscriptionEndDate5;
	}

	public void setGiftSubscriptionEndDate5(Date giftSubscriptionEndDate5) {
		this.giftSubscriptionEndDate5 = giftSubscriptionEndDate5;
	}

	public String getOwnSubscriptionIdentifier6() {
		return ownSubscriptionIdentifier6;
	}

	public void setOwnSubscriptionIdentifier6(String ownSubscriptionIdentifier6) {
		this.ownSubscriptionIdentifier6 = ownSubscriptionIdentifier6;
	}

	public String getOwnSubscriptionMedia6() {
		return ownSubscriptionMedia6;
	}

	public void setOwnSubscriptionMedia6(String ownSubscriptionMedia6) {
		this.ownSubscriptionMedia6 = ownSubscriptionMedia6;
	}

	public String getOwnSubscriptionStatus6() {
		return ownSubscriptionStatus6;
	}

	public void setOwnSubscriptionStatus6(String ownSubscriptionStatus6) {
		this.ownSubscriptionStatus6 = ownSubscriptionStatus6;
	}

	public Date getOwnSubscriptionCreationDate6() {
		return ownSubscriptionCreationDate6;
	}

	public void setOwnSubscriptionCreationDate6(Date ownSubscriptionCreationDate6) {
		this.ownSubscriptionCreationDate6 = ownSubscriptionCreationDate6;
	}

	public Date getOwnSubscriptionEndDate6() {
		return ownSubscriptionEndDate6;
	}

	public void setOwnSubscriptionEndDate6(Date ownSubscriptionEndDate6) {
		this.ownSubscriptionEndDate6 = ownSubscriptionEndDate6;
	}

	public Date getGiftSubscriptionEndDate6() {
		return giftSubscriptionEndDate6;
	}

	public void setGiftSubscriptionEndDate6(Date giftSubscriptionEndDate6) {
		this.giftSubscriptionEndDate6 = giftSubscriptionEndDate6;
	}

	
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (uid != null ? uid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CrmExport)) {
            return false;
        }
        CrmExport other = (CrmExport) object;
        if ((this.uid == null && other.uid != null) || (this.uid != null && !this.uid.equals(other.uid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String s = "CrmExport[uid=" + uid + "] ";
        return s;
    }

}
