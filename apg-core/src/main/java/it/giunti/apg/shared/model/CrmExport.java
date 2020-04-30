/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.giunti.apg.shared.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "crm_export")
public class CrmExport extends BaseEntity {
	private static final long serialVersionUID = -3182726566584169782L;
	
	@Column(name = "uid", length = 16)
	private String uid;
	@Column(name = "identity_uid", length = 32)
	private String identityUid;
	@Basic(optional = false)
	@Column(name = "deleted", nullable = false)
	private boolean deleted;
	@Column(name = "merged_into_uid", length = 16)
	private String mergedIntoUid;
	@Column(name = "update_timestamp")
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

	@Column(name = "own_subscription_identifier_0", length = 8)
	private String ownSubscriptionIdentifier0;
	@Column(name = "own_subscription_media_0", length = 2)
	private String ownSubscriptionMedia0;
	@Column(name = "own_subscription_status_0", length = 8)
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
	
	@Column(name = "own_subscription_identifier_1", length = 8)
	private String ownSubscriptionIdentifier1;
	@Column(name = "own_subscription_media_1", length = 2)
	private String ownSubscriptionMedia1;
	@Column(name = "own_subscription_status_1", length = 8)
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

	@Column(name = "own_subscription_identifier_2", length = 8)
	private String ownSubscriptionIdentifier2;
	@Column(name = "own_subscription_media_2", length = 2)
	private String ownSubscriptionMedia2;
	@Column(name = "own_subscription_status_2", length = 8)
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
	
	@Column(name = "own_subscription_identifier_3", length = 8)
	private String ownSubscriptionIdentifier3;
	@Column(name = "own_subscription_media_3", length = 2)
	private String ownSubscriptionMedia3;
	@Column(name = "own_subscription_status_3", length = 8)
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
	
	@Column(name = "own_subscription_identifier_4", length = 8)
	private String ownSubscriptionIdentifier4;
	@Column(name = "own_subscription_media_4", length = 2)
	private String ownSubscriptionMedia4;
	@Column(name = "own_subscription_status_4", length = 8)
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
	
	@Column(name = "own_subscription_identifier_5", length = 8)
	private String ownSubscriptionIdentifier5;
	@Column(name = "own_subscription_media_5", length = 2)
	private String ownSubscriptionMedia5;
	@Column(name = "own_subscription_status_5", length = 8)
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
	
	@Column(name = "own_subscription_identifier_6", length = 8)
	private String ownSubscriptionIdentifier6;
	@Column(name = "own_subscription_media_6", length = 2)
	private String ownSubscriptionMedia6;
	@Column(name = "own_subscription_status_6", length = 8)
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
