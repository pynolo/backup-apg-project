
package it.giunti.apg.soap.magnews;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for mnDatabaseReport complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnDatabaseReport">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="contacts" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="creationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="idDatabase" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subscribedContacts" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="suspendedContacts" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="unsubscribedContacts" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="waitingForSubscriptionContacts" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnDatabaseReport", propOrder = {
    "contacts",
    "creationDate",
    "idDatabase",
    "name",
    "subscribedContacts",
    "suspendedContacts",
    "unsubscribedContacts",
    "waitingForSubscriptionContacts"
})
public class MnDatabaseReport {

    protected int contacts;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creationDate;
    protected String idDatabase;
    protected String name;
    protected int subscribedContacts;
    protected int suspendedContacts;
    protected int unsubscribedContacts;
    protected int waitingForSubscriptionContacts;

    /**
     * Gets the value of the contacts property.
     * 
     */
    public int getContacts() {
        return contacts;
    }

    /**
     * Sets the value of the contacts property.
     * 
     */
    public void setContacts(int value) {
        this.contacts = value;
    }

    /**
     * Gets the value of the creationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the value of the creationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreationDate(XMLGregorianCalendar value) {
        this.creationDate = value;
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
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the subscribedContacts property.
     * 
     */
    public int getSubscribedContacts() {
        return subscribedContacts;
    }

    /**
     * Sets the value of the subscribedContacts property.
     * 
     */
    public void setSubscribedContacts(int value) {
        this.subscribedContacts = value;
    }

    /**
     * Gets the value of the suspendedContacts property.
     * 
     */
    public int getSuspendedContacts() {
        return suspendedContacts;
    }

    /**
     * Sets the value of the suspendedContacts property.
     * 
     */
    public void setSuspendedContacts(int value) {
        this.suspendedContacts = value;
    }

    /**
     * Gets the value of the unsubscribedContacts property.
     * 
     */
    public int getUnsubscribedContacts() {
        return unsubscribedContacts;
    }

    /**
     * Sets the value of the unsubscribedContacts property.
     * 
     */
    public void setUnsubscribedContacts(int value) {
        this.unsubscribedContacts = value;
    }

    /**
     * Gets the value of the waitingForSubscriptionContacts property.
     * 
     */
    public int getWaitingForSubscriptionContacts() {
        return waitingForSubscriptionContacts;
    }

    /**
     * Sets the value of the waitingForSubscriptionContacts property.
     * 
     */
    public void setWaitingForSubscriptionContacts(int value) {
        this.waitingForSubscriptionContacts = value;
    }

}
