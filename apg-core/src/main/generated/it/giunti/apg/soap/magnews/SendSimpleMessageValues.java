
package it.giunti.apg.soap.magnews;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for sendSimpleMessageValues complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sendSimpleMessageValues">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contactPrimaryKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customData" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idDatabase" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="messageTemplate" type="{http://webservices.magnews/}messageTemplate" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sendSimpleMessageValues", propOrder = {
    "address",
    "contactPrimaryKey",
    "customData",
    "idDatabase",
    "messageTemplate"
})
public class SendSimpleMessageValues {

    protected String address;
    protected String contactPrimaryKey;
    protected String customData;
    protected String idDatabase;
    protected MessageTemplate messageTemplate;

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
     * Gets the value of the idDatabase property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdDatabase() {
        return idDatabase;
    }

    /**
     * Sets the value of the idDatabase property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdDatabase(String value) {
        this.idDatabase = value;
    }

    /**
     * Gets the value of the messageTemplate property.
     * 
     * @return
     *     possible object is
     *     {@link MessageTemplate }
     *     
     */
    public MessageTemplate getMessageTemplate() {
        return messageTemplate;
    }

    /**
     * Sets the value of the messageTemplate property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageTemplate }
     *     
     */
    public void setMessageTemplate(MessageTemplate value) {
        this.messageTemplate = value;
    }

}
