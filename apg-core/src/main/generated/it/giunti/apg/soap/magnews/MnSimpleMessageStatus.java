
package it.giunti.apg.soap.magnews;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for mnSimpleMessageStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnSimpleMessageStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="actualStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="clicked" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="lastClickedTs" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lastOpenedTs" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="messageId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="opened" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="sentDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="idbouncecategory" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="complainted" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="notSentCause" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="converted" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="lastConversionTs" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="expectedDeliveryTs" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="messageRetention" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="externalId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnSimpleMessageStatus", propOrder = {
    "actualStatus",
    "address",
    "clicked",
    "lastClickedTs",
    "lastOpenedTs",
    "messageId",
    "opened",
    "sentDate",
    "idbouncecategory",
    "complainted",
    "notSentCause",
    "converted",
    "lastConversionTs",
    "expectedDeliveryTs",
    "messageRetention",
    "externalId"
})
public class MnSimpleMessageStatus {

    protected String actualStatus;
    protected String address;
    protected boolean clicked;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastClickedTs;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastOpenedTs;
    protected String messageId;
    protected boolean opened;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar sentDate;
    protected Integer idbouncecategory;
    protected boolean complainted;
    protected String notSentCause;
    protected boolean converted;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastConversionTs;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar expectedDeliveryTs;
    protected String messageRetention;
    protected String externalId;

    /**
     * Gets the value of the actualStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActualStatus() {
        return actualStatus;
    }

    /**
     * Sets the value of the actualStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActualStatus(String value) {
        this.actualStatus = value;
    }

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
     * Gets the value of the messageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Sets the value of the messageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageId(String value) {
        this.messageId = value;
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
     * Gets the value of the sentDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSentDate() {
        return sentDate;
    }

    /**
     * Sets the value of the sentDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSentDate(XMLGregorianCalendar value) {
        this.sentDate = value;
    }

    /**
     * Gets the value of the idbouncecategory property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIdbouncecategory() {
        return idbouncecategory;
    }

    /**
     * Sets the value of the idbouncecategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIdbouncecategory(Integer value) {
        this.idbouncecategory = value;
    }

    /**
     * Gets the value of the complainted property.
     * 
     */
    public boolean isComplainted() {
        return complainted;
    }

    /**
     * Sets the value of the complainted property.
     * 
     */
    public void setComplainted(boolean value) {
        this.complainted = value;
    }

    /**
     * Gets the value of the notSentCause property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotSentCause() {
        return notSentCause;
    }

    /**
     * Sets the value of the notSentCause property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotSentCause(String value) {
        this.notSentCause = value;
    }

    /**
     * Gets the value of the converted property.
     * 
     */
    public boolean isConverted() {
        return converted;
    }

    /**
     * Sets the value of the converted property.
     * 
     */
    public void setConverted(boolean value) {
        this.converted = value;
    }

    /**
     * Gets the value of the lastConversionTs property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastConversionTs() {
        return lastConversionTs;
    }

    /**
     * Sets the value of the lastConversionTs property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastConversionTs(XMLGregorianCalendar value) {
        this.lastConversionTs = value;
    }

    /**
     * Gets the value of the expectedDeliveryTs property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpectedDeliveryTs() {
        return expectedDeliveryTs;
    }

    /**
     * Sets the value of the expectedDeliveryTs property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpectedDeliveryTs(XMLGregorianCalendar value) {
        this.expectedDeliveryTs = value;
    }

    /**
     * Gets the value of the messageRetention property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageRetention() {
        return messageRetention;
    }

    /**
     * Sets the value of the messageRetention property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageRetention(String value) {
        this.messageRetention = value;
    }

    /**
     * Gets the value of the externalId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * Sets the value of the externalId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExternalId(String value) {
        this.externalId = value;
    }

}
