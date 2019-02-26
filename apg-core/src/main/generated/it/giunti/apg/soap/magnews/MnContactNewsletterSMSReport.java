
package it.giunti.apg.soap.magnews;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for mnContactNewsletterSMSReport complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnContactNewsletterSMSReport">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idContact" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idNewsletter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sent" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="received" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="firstSentTs" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="othervalue" type="{http://webservices.magnews/}fieldValue" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnContactNewsletterSMSReport", propOrder = {
    "idContact",
    "idNewsletter",
    "sent",
    "received",
    "firstSentTs",
    "othervalue"
})
public class MnContactNewsletterSMSReport {

    protected String idContact;
    protected String idNewsletter;
    protected boolean sent;
    protected boolean received;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar firstSentTs;
    protected List<FieldValue> othervalue;

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
     * Gets the value of the sent property.
     * 
     */
    public boolean isSent() {
        return sent;
    }

    /**
     * Sets the value of the sent property.
     * 
     */
    public void setSent(boolean value) {
        this.sent = value;
    }

    /**
     * Gets the value of the received property.
     * 
     */
    public boolean isReceived() {
        return received;
    }

    /**
     * Sets the value of the received property.
     * 
     */
    public void setReceived(boolean value) {
        this.received = value;
    }

    /**
     * Gets the value of the firstSentTs property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFirstSentTs() {
        return firstSentTs;
    }

    /**
     * Sets the value of the firstSentTs property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFirstSentTs(XMLGregorianCalendar value) {
        this.firstSentTs = value;
    }

    /**
     * Gets the value of the othervalue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the othervalue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOthervalue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FieldValue }
     * 
     * 
     */
    public List<FieldValue> getOthervalue() {
        if (othervalue == null) {
            othervalue = new ArrayList<FieldValue>();
        }
        return this.othervalue;
    }

}
