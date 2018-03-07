/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.giunti.apg.shared.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author paolo
 */
@Entity
@Table(name = "cache_crm")
public class CacheCrm extends BaseEntity {
	private static final long serialVersionUID = -4785224870260440239L;
	@Id
	@Basic(optional = false)
	@Column(name = "id_customer", length = 16, nullable = false)
	private String idCustomer;
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
	@Column(name = "phone_mobile", length = 16)
	private String phoneMobile;
	@Column(name = "phone_landline", length = 16)
	private String phoneLandline;
	@Column(name = "email_primary", length = 256)
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
	@Column(name = "customer_type", length = 4)
	private String customerType;
	@Basic(optional = false)
    @Column(name = "consent_tos", nullable = false)
    private boolean consentTos;
    @Basic(optional = false)
    @Column(name = "consent_marketing", nullable = false)
    private boolean consentMarketing;
    @Basic(optional = false)
    @Column(name = "consent_profiling", nullable = false)
    private boolean consentProfiling;
	@Column(name = "consent_update_date")
	@Temporal(TemporalType.DATE)
	private Date consentUpdateDate;
	@Column(name = "creation_date")
	@Temporal(TemporalType.DATE)
	private Date creationDate;
	@Column(name = "modified_date")
	@Temporal(TemporalType.DATE)
	private Date modifiedDate;

	@Column(name = "own_subscription_identifier_1", length = 16)
	private String ownSubscriptionIdentifier1;
	@Basic(optional = false)
	@Column(name = "own_subscription_blocked_1", nullable = false)
	private boolean ownSubscriptionBlocked1;
	@Column(name = "own_subscription_begin_1")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionBegin1;
	@Column(name = "own_subscription_end_1")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionEnd1;
	@Column(name = "gift_subscription_end_1")
	@Temporal(TemporalType.DATE)
	private Date giftSubscriptionEnd1;
	@Column(name = "subscription_creation_date_1")
	@Temporal(TemporalType.DATE)
	private Date subscriptionCreationDate1;
    
	@Column(name = "own_subscription_identifier_2", length = 16)
	private String ownSubscriptionIdentifier2;
	@Basic(optional = false)
	@Column(name = "own_subscription_blocked_2", nullable = false)
	private boolean ownSubscriptionBlocked2;
	@Column(name = "own_subscription_begin_2")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionBegin2;
	@Column(name = "own_subscription_end_2")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionEnd2;
	@Column(name = "gift_subscription_end_2")
	@Temporal(TemporalType.DATE)
	private Date giftSubscriptionEnd2;
	@Column(name = "subscription_creation_date_2")
	@Temporal(TemporalType.DATE)
	private Date subscriptionCreationDate2;
	
	@Column(name = "own_subscription_identifier_3", length = 16)
	private String ownSubscriptionIdentifier3;
	@Basic(optional = false)
	@Column(name = "own_subscription_blocked_3", nullable = false)
	private boolean ownSubscriptionBlocked3;
	@Column(name = "own_subscription_begin_3")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionBegin3;
	@Column(name = "own_subscription_end_3")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionEnd3;
	@Column(name = "gift_subscription_end_3")
	@Temporal(TemporalType.DATE)
	private Date giftSubscriptionEnd3;
	@Column(name = "subscription_creation_date_3")
	@Temporal(TemporalType.DATE)
	private Date subscriptionCreationDate3;
	
	@Column(name = "own_subscription_identifier_4", length = 16)
	private String ownSubscriptionIdentifier4;
	@Basic(optional = false)
	@Column(name = "own_subscription_blocked_4", nullable = false)
	private boolean ownSubscriptionBlocked4;
	@Column(name = "own_subscription_begin_4")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionBegin4;
	@Column(name = "own_subscription_end_4")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionEnd4;
	@Column(name = "gift_subscription_end_4")
	@Temporal(TemporalType.DATE)
	private Date giftSubscriptionEnd4;
	@Column(name = "subscription_creation_date_4")
	@Temporal(TemporalType.DATE)
	private Date subscriptionCreationDate4;
	
	@Column(name = "own_subscription_identifier_5", length = 16)
	private String ownSubscriptionIdentifier5;
	@Basic(optional = false)
	@Column(name = "own_subscription_blocked_5", nullable = false)
	private boolean ownSubscriptionBlocked5;
	@Column(name = "own_subscription_begin_5")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionBegin5;
	@Column(name = "own_subscription_end_5")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionEnd5;
	@Column(name = "gift_subscription_end_5")
	@Temporal(TemporalType.DATE)
	private Date giftSubscriptionEnd5;
	@Column(name = "subscription_creation_date_5")
	@Temporal(TemporalType.DATE)
	private Date subscriptionCreationDate5;
	
	@Column(name = "own_subscription_identifier_6", length = 16)
	private String ownSubscriptionIdentifier6;
	@Basic(optional = false)
	@Column(name = "own_subscription_blocked_6", nullable = false)
	private boolean ownSubscriptionBlocked6;
	@Column(name = "own_subscription_begin_6")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionBegin6;
	@Column(name = "own_subscription_end_6")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionEnd6;
	@Column(name = "gift_subscription_end_6")
	@Temporal(TemporalType.DATE)
	private Date giftSubscriptionEnd6;
	@Column(name = "subscription_creation_date_6")
	@Temporal(TemporalType.DATE)
	private Date subscriptionCreationDate6;
	
	@Column(name = "own_subscription_identifier_7", length = 16)
	private String ownSubscriptionIdentifier7;
	@Basic(optional = false)
	@Column(name = "own_subscription_blocked_7", nullable = false)
	private boolean ownSubscriptionBlocked7;
	@Column(name = "own_subscription_begin_7")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionBegin7;
	@Column(name = "own_subscription_end_7")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionEnd7;
	@Column(name = "gift_subscription_end_7")
	@Temporal(TemporalType.DATE)
	private Date giftSubscriptionEnd7;
	@Column(name = "subscription_creation_date_7")
	@Temporal(TemporalType.DATE)
	private Date subscriptionCreationDate7;
	
	@Column(name = "own_subscription_identifier_8", length = 16)
	private String ownSubscriptionIdentifier8;
	@Basic(optional = false)
	@Column(name = "own_subscription_blocked_8", nullable = false)
	private boolean ownSubscriptionBlocked8;
	@Column(name = "own_subscription_begin_8")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionBegin8;
	@Column(name = "own_subscription_end_8")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionEnd8;
	@Column(name = "gift_subscription_end_8")
	@Temporal(TemporalType.DATE)
	private Date giftSubscriptionEnd8;
	@Column(name = "subscription_creation_date_8")
	@Temporal(TemporalType.DATE)
	private Date subscriptionCreationDate8;
	
	
    
    public CacheCrm() {
    }

    public String getIdCustomer() {
		return idCustomer;
	}

	public void setIdCustomer(String idCustomer) {
		this.idCustomer = idCustomer;
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

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getOwnSubscriptionIdentifier1() {
		return ownSubscriptionIdentifier1;
	}

	public void setOwnSubscriptionIdentifier1(String ownSubscriptionIdentifier1) {
		this.ownSubscriptionIdentifier1 = ownSubscriptionIdentifier1;
	}

	public boolean isOwnSubscriptionBlocked1() {
		return ownSubscriptionBlocked1;
	}

	public void setOwnSubscriptionBlocked1(boolean ownSubscriptionBlocked1) {
		this.ownSubscriptionBlocked1 = ownSubscriptionBlocked1;
	}

	public Date getOwnSubscriptionBegin1() {
		return ownSubscriptionBegin1;
	}

	public void setOwnSubscriptionBegin1(Date ownSubscriptionBegin1) {
		this.ownSubscriptionBegin1 = ownSubscriptionBegin1;
	}

	public Date getOwnSubscriptionEnd1() {
		return ownSubscriptionEnd1;
	}

	public void setOwnSubscriptionEnd1(Date ownSubscriptionEnd1) {
		this.ownSubscriptionEnd1 = ownSubscriptionEnd1;
	}

	public Date getGiftSubscriptionEnd1() {
		return giftSubscriptionEnd1;
	}

	public void setGiftSubscriptionEnd1(Date giftSubscriptionEnd1) {
		this.giftSubscriptionEnd1 = giftSubscriptionEnd1;
	}

	public Date getSubscriptionCreationDate1() {
		return subscriptionCreationDate1;
	}

	public void setSubscriptionCreationDate1(Date subscriptionCreationDate1) {
		this.subscriptionCreationDate1 = subscriptionCreationDate1;
	}

	public String getOwnSubscriptionIdentifier2() {
		return ownSubscriptionIdentifier2;
	}

	public void setOwnSubscriptionIdentifier2(String ownSubscriptionIdentifier2) {
		this.ownSubscriptionIdentifier2 = ownSubscriptionIdentifier2;
	}

	public boolean isOwnSubscriptionBlocked2() {
		return ownSubscriptionBlocked2;
	}

	public void setOwnSubscriptionBlocked2(boolean ownSubscriptionBlocked2) {
		this.ownSubscriptionBlocked2 = ownSubscriptionBlocked2;
	}

	public Date getOwnSubscriptionBegin2() {
		return ownSubscriptionBegin2;
	}

	public void setOwnSubscriptionBegin2(Date ownSubscriptionBegin2) {
		this.ownSubscriptionBegin2 = ownSubscriptionBegin2;
	}

	public Date getOwnSubscriptionEnd2() {
		return ownSubscriptionEnd2;
	}

	public void setOwnSubscriptionEnd2(Date ownSubscriptionEnd2) {
		this.ownSubscriptionEnd2 = ownSubscriptionEnd2;
	}

	public Date getGiftSubscriptionEnd2() {
		return giftSubscriptionEnd2;
	}

	public void setGiftSubscriptionEnd2(Date giftSubscriptionEnd2) {
		this.giftSubscriptionEnd2 = giftSubscriptionEnd2;
	}

	public Date getSubscriptionCreationDate2() {
		return subscriptionCreationDate2;
	}

	public void setSubscriptionCreationDate2(Date subscriptionCreationDate2) {
		this.subscriptionCreationDate2 = subscriptionCreationDate2;
	}

	public String getOwnSubscriptionIdentifier3() {
		return ownSubscriptionIdentifier3;
	}

	public void setOwnSubscriptionIdentifier3(String ownSubscriptionIdentifier3) {
		this.ownSubscriptionIdentifier3 = ownSubscriptionIdentifier3;
	}

	public boolean isOwnSubscriptionBlocked3() {
		return ownSubscriptionBlocked3;
	}

	public void setOwnSubscriptionBlocked3(boolean ownSubscriptionBlocked3) {
		this.ownSubscriptionBlocked3 = ownSubscriptionBlocked3;
	}

	public Date getOwnSubscriptionBegin3() {
		return ownSubscriptionBegin3;
	}

	public void setOwnSubscriptionBegin3(Date ownSubscriptionBegin3) {
		this.ownSubscriptionBegin3 = ownSubscriptionBegin3;
	}

	public Date getOwnSubscriptionEnd3() {
		return ownSubscriptionEnd3;
	}

	public void setOwnSubscriptionEnd3(Date ownSubscriptionEnd3) {
		this.ownSubscriptionEnd3 = ownSubscriptionEnd3;
	}

	public Date getGiftSubscriptionEnd3() {
		return giftSubscriptionEnd3;
	}

	public void setGiftSubscriptionEnd3(Date giftSubscriptionEnd3) {
		this.giftSubscriptionEnd3 = giftSubscriptionEnd3;
	}

	public Date getSubscriptionCreationDate3() {
		return subscriptionCreationDate3;
	}

	public void setSubscriptionCreationDate3(Date subscriptionCreationDate3) {
		this.subscriptionCreationDate3 = subscriptionCreationDate3;
	}

	public String getOwnSubscriptionIdentifier4() {
		return ownSubscriptionIdentifier4;
	}

	public void setOwnSubscriptionIdentifier4(String ownSubscriptionIdentifier4) {
		this.ownSubscriptionIdentifier4 = ownSubscriptionIdentifier4;
	}

	public boolean isOwnSubscriptionBlocked4() {
		return ownSubscriptionBlocked4;
	}

	public void setOwnSubscriptionBlocked4(boolean ownSubscriptionBlocked4) {
		this.ownSubscriptionBlocked4 = ownSubscriptionBlocked4;
	}

	public Date getOwnSubscriptionBegin4() {
		return ownSubscriptionBegin4;
	}

	public void setOwnSubscriptionBegin4(Date ownSubscriptionBegin4) {
		this.ownSubscriptionBegin4 = ownSubscriptionBegin4;
	}

	public Date getOwnSubscriptionEnd4() {
		return ownSubscriptionEnd4;
	}

	public void setOwnSubscriptionEnd4(Date ownSubscriptionEnd4) {
		this.ownSubscriptionEnd4 = ownSubscriptionEnd4;
	}

	public Date getGiftSubscriptionEnd4() {
		return giftSubscriptionEnd4;
	}

	public void setGiftSubscriptionEnd4(Date giftSubscriptionEnd4) {
		this.giftSubscriptionEnd4 = giftSubscriptionEnd4;
	}

	public Date getSubscriptionCreationDate4() {
		return subscriptionCreationDate4;
	}

	public void setSubscriptionCreationDate4(Date subscriptionCreationDate4) {
		this.subscriptionCreationDate4 = subscriptionCreationDate4;
	}

	public String getOwnSubscriptionIdentifier5() {
		return ownSubscriptionIdentifier5;
	}

	public void setOwnSubscriptionIdentifier5(String ownSubscriptionIdentifier5) {
		this.ownSubscriptionIdentifier5 = ownSubscriptionIdentifier5;
	}

	public boolean isOwnSubscriptionBlocked5() {
		return ownSubscriptionBlocked5;
	}

	public void setOwnSubscriptionBlocked5(boolean ownSubscriptionBlocked5) {
		this.ownSubscriptionBlocked5 = ownSubscriptionBlocked5;
	}

	public Date getOwnSubscriptionBegin5() {
		return ownSubscriptionBegin5;
	}

	public void setOwnSubscriptionBegin5(Date ownSubscriptionBegin5) {
		this.ownSubscriptionBegin5 = ownSubscriptionBegin5;
	}

	public Date getOwnSubscriptionEnd5() {
		return ownSubscriptionEnd5;
	}

	public void setOwnSubscriptionEnd5(Date ownSubscriptionEnd5) {
		this.ownSubscriptionEnd5 = ownSubscriptionEnd5;
	}

	public Date getGiftSubscriptionEnd5() {
		return giftSubscriptionEnd5;
	}

	public void setGiftSubscriptionEnd5(Date giftSubscriptionEnd5) {
		this.giftSubscriptionEnd5 = giftSubscriptionEnd5;
	}

	public Date getSubscriptionCreationDate5() {
		return subscriptionCreationDate5;
	}

	public void setSubscriptionCreationDate5(Date subscriptionCreationDate5) {
		this.subscriptionCreationDate5 = subscriptionCreationDate5;
	}

	public String getOwnSubscriptionIdentifier6() {
		return ownSubscriptionIdentifier6;
	}

	public void setOwnSubscriptionIdentifier6(String ownSubscriptionIdentifier6) {
		this.ownSubscriptionIdentifier6 = ownSubscriptionIdentifier6;
	}

	public boolean isOwnSubscriptionBlocked6() {
		return ownSubscriptionBlocked6;
	}

	public void setOwnSubscriptionBlocked6(boolean ownSubscriptionBlocked6) {
		this.ownSubscriptionBlocked6 = ownSubscriptionBlocked6;
	}

	public Date getOwnSubscriptionBegin6() {
		return ownSubscriptionBegin6;
	}

	public void setOwnSubscriptionBegin6(Date ownSubscriptionBegin6) {
		this.ownSubscriptionBegin6 = ownSubscriptionBegin6;
	}

	public Date getOwnSubscriptionEnd6() {
		return ownSubscriptionEnd6;
	}

	public void setOwnSubscriptionEnd6(Date ownSubscriptionEnd6) {
		this.ownSubscriptionEnd6 = ownSubscriptionEnd6;
	}

	public Date getGiftSubscriptionEnd6() {
		return giftSubscriptionEnd6;
	}

	public void setGiftSubscriptionEnd6(Date giftSubscriptionEnd6) {
		this.giftSubscriptionEnd6 = giftSubscriptionEnd6;
	}

	public Date getSubscriptionCreationDate6() {
		return subscriptionCreationDate6;
	}

	public void setSubscriptionCreationDate6(Date subscriptionCreationDate6) {
		this.subscriptionCreationDate6 = subscriptionCreationDate6;
	}

	public String getOwnSubscriptionIdentifier7() {
		return ownSubscriptionIdentifier7;
	}

	public void setOwnSubscriptionIdentifier7(String ownSubscriptionIdentifier7) {
		this.ownSubscriptionIdentifier7 = ownSubscriptionIdentifier7;
	}

	public boolean isOwnSubscriptionBlocked7() {
		return ownSubscriptionBlocked7;
	}

	public void setOwnSubscriptionBlocked7(boolean ownSubscriptionBlocked7) {
		this.ownSubscriptionBlocked7 = ownSubscriptionBlocked7;
	}

	public Date getOwnSubscriptionBegin7() {
		return ownSubscriptionBegin7;
	}

	public void setOwnSubscriptionBegin7(Date ownSubscriptionBegin7) {
		this.ownSubscriptionBegin7 = ownSubscriptionBegin7;
	}

	public Date getOwnSubscriptionEnd7() {
		return ownSubscriptionEnd7;
	}

	public void setOwnSubscriptionEnd7(Date ownSubscriptionEnd7) {
		this.ownSubscriptionEnd7 = ownSubscriptionEnd7;
	}

	public Date getGiftSubscriptionEnd7() {
		return giftSubscriptionEnd7;
	}

	public void setGiftSubscriptionEnd7(Date giftSubscriptionEnd7) {
		this.giftSubscriptionEnd7 = giftSubscriptionEnd7;
	}

	public Date getSubscriptionCreationDate7() {
		return subscriptionCreationDate7;
	}

	public void setSubscriptionCreationDate7(Date subscriptionCreationDate7) {
		this.subscriptionCreationDate7 = subscriptionCreationDate7;
	}

	public String getOwnSubscriptionIdentifier8() {
		return ownSubscriptionIdentifier8;
	}

	public void setOwnSubscriptionIdentifier8(String ownSubscriptionIdentifier8) {
		this.ownSubscriptionIdentifier8 = ownSubscriptionIdentifier8;
	}

	public boolean isOwnSubscriptionBlocked8() {
		return ownSubscriptionBlocked8;
	}

	public void setOwnSubscriptionBlocked8(boolean ownSubscriptionBlocked8) {
		this.ownSubscriptionBlocked8 = ownSubscriptionBlocked8;
	}

	public Date getOwnSubscriptionBegin8() {
		return ownSubscriptionBegin8;
	}

	public void setOwnSubscriptionBegin8(Date ownSubscriptionBegin8) {
		this.ownSubscriptionBegin8 = ownSubscriptionBegin8;
	}

	public Date getOwnSubscriptionEnd8() {
		return ownSubscriptionEnd8;
	}

	public void setOwnSubscriptionEnd8(Date ownSubscriptionEnd8) {
		this.ownSubscriptionEnd8 = ownSubscriptionEnd8;
	}

	public Date getGiftSubscriptionEnd8() {
		return giftSubscriptionEnd8;
	}

	public void setGiftSubscriptionEnd8(Date giftSubscriptionEnd8) {
		this.giftSubscriptionEnd8 = giftSubscriptionEnd8;
	}

	public Date getSubscriptionCreationDate8() {
		return subscriptionCreationDate8;
	}

	public void setSubscriptionCreationDate8(Date subscriptionCreationDate8) {
		this.subscriptionCreationDate8 = subscriptionCreationDate8;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (idCustomer != null ? idCustomer.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CacheCrm)) {
            return false;
        }
        CacheCrm other = (CacheCrm) object;
        if ((this.idCustomer == null && other.idCustomer != null) || (this.idCustomer != null && !this.idCustomer.equals(other.idCustomer))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String s = "CacheCrm[uid=" + idCustomer + "] ";
        return s;
    }

}
