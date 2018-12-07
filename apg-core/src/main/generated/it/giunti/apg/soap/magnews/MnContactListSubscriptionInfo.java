
package it.giunti.apg.soap.magnews;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mnContactListSubscriptionInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnContactListSubscriptionInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idContact" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idList" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idSubscriptionCause" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idSuspensionCause" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idUnsubscriptionCause" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnContactListSubscriptionInfo", propOrder = {
    "idContact",
    "idList",
    "idSubscriptionCause",
    "idSuspensionCause",
    "idUnsubscriptionCause",
    "status"
})
public class MnContactListSubscriptionInfo {

    protected String idContact;
    protected String idList;
    protected String idSubscriptionCause;
    protected String idSuspensionCause;
    protected String idUnsubscriptionCause;
    protected String status;

    /**
     * Gets the value of the idContact property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdContact() {
        return idContact;
    }

    /**
     * Sets the value of the idContact property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdContact(String value) {
        this.idContact = value;
    }

    /**
     * Gets the value of the idList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdList() {
        return idList;
    }

    /**
     * Sets the value of the idList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdList(String value) {
        this.idList = value;
    }

    /**
     * Gets the value of the idSubscriptionCause property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdSubscriptionCause() {
        return idSubscriptionCause;
    }

    /**
     * Sets the value of the idSubscriptionCause property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdSubscriptionCause(String value) {
        this.idSubscriptionCause = value;
    }

    /**
     * Gets the value of the idSuspensionCause property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdSuspensionCause() {
        return idSuspensionCause;
    }

    /**
     * Sets the value of the idSuspensionCause property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdSuspensionCause(String value) {
        this.idSuspensionCause = value;
    }

    /**
     * Gets the value of the idUnsubscriptionCause property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdUnsubscriptionCause() {
        return idUnsubscriptionCause;
    }

    /**
     * Sets the value of the idUnsubscriptionCause property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdUnsubscriptionCause(String value) {
        this.idUnsubscriptionCause = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

}
