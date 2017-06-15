
package it.giunti.apgws.wsbeans.hbsauth;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for AuthData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AuthData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="authorized" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="subscribed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="subscriptionStartDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="subscriptionExpiryDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="message" type="{http://applicazioni.giunti.it/apgws/hbsauth}String256Type" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthData", propOrder = {
    "authorized",
    "subscribed",
    "subscriptionStartDate",
    "subscriptionExpiryDate",
    "message"
})
public class AuthData {

    protected boolean authorized;
    protected Boolean subscribed;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar subscriptionStartDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar subscriptionExpiryDate;
    protected String message;

    /**
     * Gets the value of the authorized property.
     * 
     */
    public boolean isAuthorized() {
        return authorized;
    }

    /**
     * Sets the value of the authorized property.
     * 
     */
    public void setAuthorized(boolean value) {
        this.authorized = value;
    }

    /**
     * Gets the value of the subscribed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSubscribed() {
        return subscribed;
    }

    /**
     * Sets the value of the subscribed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSubscribed(Boolean value) {
        this.subscribed = value;
    }

    /**
     * Gets the value of the subscriptionStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSubscriptionStartDate() {
        return subscriptionStartDate;
    }

    /**
     * Sets the value of the subscriptionStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSubscriptionStartDate(XMLGregorianCalendar value) {
        this.subscriptionStartDate = value;
    }

    /**
     * Gets the value of the subscriptionExpiryDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSubscriptionExpiryDate() {
        return subscriptionExpiryDate;
    }

    /**
     * Sets the value of the subscriptionExpiryDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSubscriptionExpiryDate(XMLGregorianCalendar value) {
        this.subscriptionExpiryDate = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

}
