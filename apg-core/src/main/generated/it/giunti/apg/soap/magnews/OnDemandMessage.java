
package it.giunti.apg.soap.magnews;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for onDemandMessage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="onDemandMessage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contactPrimaryKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customData" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="debugMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idContact" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idLaunch" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idNewsletter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idmessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rescheduled" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
@XmlType(name = "onDemandMessage", propOrder = {
    "address",
    "contactPrimaryKey",
    "customData",
    "debugMessage",
    "idContact",
    "idLaunch",
    "idNewsletter",
    "idmessage",
    "rescheduled",
    "status"
})
public class OnDemandMessage {

    protected String address;
    protected String contactPrimaryKey;
    protected String customData;
    protected String debugMessage;
    protected String idContact;
    protected String idLaunch;
    protected String idNewsletter;
    protected String idmessage;
    protected boolean rescheduled;
    protected String status;

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress(String value) {
        this.address = value;
    }

    /**
     * Gets the value of the contactPrimaryKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactPrimaryKey() {
        return contactPrimaryKey;
    }

    /**
     * Sets the value of the contactPrimaryKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactPrimaryKey(String value) {
        this.contactPrimaryKey = value;
    }

    /**
     * Gets the value of the customData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomData() {
        return customData;
    }

    /**
     * Sets the value of the customData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomData(String value) {
        this.customData = value;
    }

    /**
     * Gets the value of the debugMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDebugMessage() {
        return debugMessage;
    }

    /**
     * Sets the value of the debugMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDebugMessage(String value) {
        this.debugMessage = value;
    }

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
     * Gets the value of the idLaunch property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdLaunch() {
        return idLaunch;
    }

    /**
     * Sets the value of the idLaunch property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdLaunch(String value) {
        this.idLaunch = value;
    }

    /**
     * Gets the value of the idNewsletter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdNewsletter() {
        return idNewsletter;
    }

    /**
     * Sets the value of the idNewsletter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdNewsletter(String value) {
        this.idNewsletter = value;
    }

    /**
     * Gets the value of the idmessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdmessage() {
        return idmessage;
    }

    /**
     * Sets the value of the idmessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdmessage(String value) {
        this.idmessage = value;
    }

    /**
     * Gets the value of the rescheduled property.
     * 
     */
    public boolean isRescheduled() {
        return rescheduled;
    }

    /**
     * Sets the value of the rescheduled property.
     * 
     */
    public void setRescheduled(boolean value) {
        this.rescheduled = value;
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
