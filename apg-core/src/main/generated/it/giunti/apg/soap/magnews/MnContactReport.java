
package it.giunti.apg.soap.magnews;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for mnContactReport complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnContactReport">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="clicked" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="firstClickedTs" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="firstOpenedTs" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="firstSentTs" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="idContact" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastClickedTs" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lastOpenTs" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lastOpenedTs" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lastSentTs" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="opened" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="otherValues" type="{http://webservices.magnews/}fieldValue" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="received" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="sent" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnContactReport", propOrder = {
    "clicked",
    "firstClickedTs",
    "firstOpenedTs",
    "firstSentTs",
    "idContact",
    "lastClickedTs",
    "lastOpenTs",
    "lastOpenedTs",
    "lastSentTs",
    "opened",
    "otherValues",
    "received",
    "sent"
})
public class MnContactReport {

    protected boolean clicked;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar firstClickedTs;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar firstOpenedTs;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar firstSentTs;
    protected String idContact;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastClickedTs;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastOpenTs;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastOpenedTs;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastSentTs;
    protected boolean opened;
    @XmlElement(nillable = true)
    protected List<FieldValue> otherValues;
    protected boolean received;
    protected boolean sent;

    /**
     * Gets the value of the clicked property.
     * 
     */
    public boolean isClicked() {
        return clicked;
    }

    /**
     * Sets the value of the clicked property.
     * 
     */
    public void setClicked(boolean value) {
        this.clicked = value;
    }

    /**
     * Gets the value of the firstClickedTs property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFirstClickedTs() {
        return firstClickedTs;
    }

    /**
     * Sets the value of the firstClickedTs property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFirstClickedTs(XMLGregorianCalendar value) {
        this.firstClickedTs = value;
    }

    /**
     * Gets the value of the firstOpenedTs property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFirstOpenedTs() {
        return firstOpenedTs;
    }

    /**
     * Sets the value of the firstOpenedTs property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFirstOpenedTs(XMLGregorianCalendar value) {
        this.firstOpenedTs = value;
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
     * Gets the value of the lastClickedTs property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastClickedTs() {
        return lastClickedTs;
    }

    /**
     * Sets the value of the lastClickedTs property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastClickedTs(XMLGregorianCalendar value) {
        this.lastClickedTs = value;
    }

    /**
     * Gets the value of the lastOpenTs property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastOpenTs() {
        return lastOpenTs;
    }

    /**
     * Sets the value of the lastOpenTs property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastOpenTs(XMLGregorianCalendar value) {
        this.lastOpenTs = value;
    }

    /**
     * Gets the value of the lastOpenedTs property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastOpenedTs() {
        return lastOpenedTs;
    }

    /**
     * Sets the value of the lastOpenedTs property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastOpenedTs(XMLGregorianCalendar value) {
        this.lastOpenedTs = value;
    }

    /**
     * Gets the value of the lastSentTs property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastSentTs() {
        return lastSentTs;
    }

    /**
     * Sets the value of the lastSentTs property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastSentTs(XMLGregorianCalendar value) {
        this.lastSentTs = value;
    }

    /**
     * Gets the value of the opened property.
     * 
     */
    public boolean isOpened() {
        return opened;
    }

    /**
     * Sets the value of the opened property.
     * 
     */
    public void setOpened(boolean value) {
        this.opened = value;
    }

    /**
     * Gets the value of the otherValues property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the otherValues property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOtherValues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FieldValue }
     * 
     * 
     */
    public List<FieldValue> getOtherValues() {
        if (otherValues == null) {
            otherValues = new ArrayList<FieldValue>();
        }
        return this.otherValues;
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

}
