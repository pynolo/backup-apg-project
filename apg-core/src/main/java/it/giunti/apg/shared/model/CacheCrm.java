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
	private static final long serialVersionUID = 8014582083617674817L;
	
	@Id
	@Basic(optional = false)
	@Column(name = "id_anagrafica", nullable = false)
	private Integer idAnagrafica;
	@Column(name = "modified_date")
	@Temporal(TemporalType.DATE)
	private Date modifiedDate;
	@Column(name = "customer_type", length = 4)
	private String customerType;
	
	@Column(name = "own_subscription_identifier_0", length = 16)
	private String ownSubscriptionIdentifier0;
	@Basic(optional = false)
	@Column(name = "own_subscription_blocked_0", nullable = false)
	private boolean ownSubscriptionBlocked0;
	@Column(name = "own_subscription_begin_0")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionBegin0;
	@Column(name = "own_subscription_end_0")
	@Temporal(TemporalType.DATE)
	private Date ownSubscriptionEnd0;
	@Column(name = "gift_subscription_end_0")
	@Temporal(TemporalType.DATE)
	private Date giftSubscriptionEnd0;
	@Column(name = "subscription_creation_date_0")
	@Temporal(TemporalType.DATE)
	private Date subscriptionCreationDate0;
	
	
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
	

    
    public CacheCrm() {
    }

	public Integer getIdAnagrafica() {
		return idAnagrafica;
	}

	public void setIdAnagrafica(Integer idAnagrafica) {
		this.idAnagrafica = idAnagrafica;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getOwnSubscriptionIdentifier0() {
		return ownSubscriptionIdentifier0;
	}

	public void setOwnSubscriptionIdentifier0(String ownSubscriptionIdentifier0) {
		this.ownSubscriptionIdentifier0 = ownSubscriptionIdentifier0;
	}

	public boolean getOwnSubscriptionBlocked0() {
		return ownSubscriptionBlocked0;
	}

	public void setOwnSubscriptionBlocked0(Boolean ownSubscriptionBlocked) {
		boolean bool = (ownSubscriptionBlocked == null)?false:ownSubscriptionBlocked;
		this.ownSubscriptionBlocked0 = bool;
	}

	public Date getOwnSubscriptionBegin0() {
		return ownSubscriptionBegin0;
	}

	public void setOwnSubscriptionBegin0(Date ownSubscriptionBegin0) {
		this.ownSubscriptionBegin0 = ownSubscriptionBegin0;
	}

	public Date getOwnSubscriptionEnd0() {
		return ownSubscriptionEnd0;
	}

	public void setOwnSubscriptionEnd0(Date ownSubscriptionEnd0) {
		this.ownSubscriptionEnd0 = ownSubscriptionEnd0;
	}

	public Date getGiftSubscriptionEnd0() {
		return giftSubscriptionEnd0;
	}

	public void setGiftSubscriptionEnd0(Date giftSubscriptionEnd0) {
		this.giftSubscriptionEnd0 = giftSubscriptionEnd0;
	}

	public Date getSubscriptionCreationDate0() {
		return subscriptionCreationDate0;
	}

	public void setSubscriptionCreationDate0(Date subscriptionCreationDate0) {
		this.subscriptionCreationDate0 = subscriptionCreationDate0;
	}

	public String getOwnSubscriptionIdentifier1() {
		return ownSubscriptionIdentifier1;
	}

	public void setOwnSubscriptionIdentifier1(String ownSubscriptionIdentifier1) {
		this.ownSubscriptionIdentifier1 = ownSubscriptionIdentifier1;
	}

	public boolean getOwnSubscriptionBlocked1() {
		return ownSubscriptionBlocked1;
	}

	public void setOwnSubscriptionBlocked1(Boolean ownSubscriptionBlocked) {
		boolean bool = (ownSubscriptionBlocked == null)?false:ownSubscriptionBlocked;
		this.ownSubscriptionBlocked1 = bool;
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

	public boolean getOwnSubscriptionBlocked2() {
		return ownSubscriptionBlocked2;
	}

	public void setOwnSubscriptionBlocked2(Boolean ownSubscriptionBlocked) {
		boolean bool = (ownSubscriptionBlocked == null)?false:ownSubscriptionBlocked;
		this.ownSubscriptionBlocked2 = bool;
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

	public boolean getOwnSubscriptionBlocked3() {
		return ownSubscriptionBlocked3;
	}

	public void setOwnSubscriptionBlocked3(Boolean ownSubscriptionBlocked) {
		boolean bool = (ownSubscriptionBlocked == null)?false:ownSubscriptionBlocked;
		this.ownSubscriptionBlocked3 = bool;
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

	public boolean getOwnSubscriptionBlocked4() {
		return ownSubscriptionBlocked4;
	}

	public void setOwnSubscriptionBlocked4(Boolean ownSubscriptionBlocked) {
		boolean bool = (ownSubscriptionBlocked == null)?false:ownSubscriptionBlocked;
		this.ownSubscriptionBlocked4 = bool;
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

	public boolean getOwnSubscriptionBlocked5() {
		return ownSubscriptionBlocked5;
	}

	public void setOwnSubscriptionBlocked5(Boolean ownSubscriptionBlocked) {
		boolean bool = (ownSubscriptionBlocked == null)?false:ownSubscriptionBlocked;
		this.ownSubscriptionBlocked5 = bool;
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

	public boolean getOwnSubscriptionBlocked6() {
		return ownSubscriptionBlocked6;
	}

	public void setOwnSubscriptionBlocked6(Boolean ownSubscriptionBlocked) {
		boolean bool = (ownSubscriptionBlocked == null)?false:ownSubscriptionBlocked;
		this.ownSubscriptionBlocked6 = bool;
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

	public boolean getOwnSubscriptionBlocked7() {
		return ownSubscriptionBlocked7;
	}

	public void setOwnSubscriptionBlocked7(Boolean ownSubscriptionBlocked) {
		boolean bool = (ownSubscriptionBlocked == null)?false:ownSubscriptionBlocked;
		this.ownSubscriptionBlocked7 = bool;
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

	
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (idAnagrafica != null ? idAnagrafica.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CacheCrm)) {
            return false;
        }
        CacheCrm other = (CacheCrm) object;
        if ((this.idAnagrafica == null && other.idAnagrafica != null) || (this.idAnagrafica != null && !this.idAnagrafica.equals(other.idAnagrafica))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String s = "CacheAnagrafica[id=" + idAnagrafica + "] ";
        return s;
    }

}
