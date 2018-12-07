
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
 * <p>Java class for mnNewsletter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnNewsletter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="creationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="idCampaign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idEditor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idNewsletter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sent" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="tags" type="{http://webservices.magnews/}fieldValue" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="firstSent" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="defaultTarget" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="expectedDeliveryTs" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="emailChannelEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="smsChannelEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="faxChannelEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnNewsletter", propOrder = {
    "creationDate",
    "idCampaign",
    "idEditor",
    "idNewsletter",
    "name",
    "sent",
    "tags",
    "firstSent",
    "defaultTarget",
    "expectedDeliveryTs",
    "emailChannelEnabled",
    "smsChannelEnabled",
    "faxChannelEnabled"
})
public class MnNewsletter {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creationDate;
    protected String idCampaign;
    protected String idEditor;
    protected String idNewsletter;
    protected String name;
    protected boolean sent;
    @XmlElement(nillable = true)
    protected List<FieldValue> tags;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar firstSent;
    @XmlElement(nillable = true)
    protected List<String> defaultTarget;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar expectedDeliveryTs;
    protected boolean emailChannelEnabled;
    protected boolean smsChannelEnabled;
    protected boolean faxChannelEnabled;

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
     * Gets the value of the idCampaign property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdCampaign() {
        return idCampaign;
    }

    /**
     * Sets the value of the idCampaign property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdCampaign(String value) {
        this.idCampaign = value;
    }

    /**
     * Gets the value of the idEditor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdEditor() {
        return idEditor;
    }

    /**
     * Sets the value of the idEditor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdEditor(String value) {
        this.idEditor = value;
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
     * Gets the value of the tags property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tags property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTags().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FieldValue }
     * 
     * 
     */
    public List<FieldValue> getTags() {
        if (tags == null) {
            tags = new ArrayList<FieldValue>();
        }
        return this.tags;
    }

    /**
     * Gets the value of the firstSent property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFirstSent() {
        return firstSent;
    }

    /**
     * Sets the value of the firstSent property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFirstSent(XMLGregorianCalendar value) {
        this.firstSent = value;
    }

    /**
     * Gets the value of the defaultTarget property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the defaultTarget property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDefaultTarget().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getDefaultTarget() {
        if (defaultTarget == null) {
            defaultTarget = new ArrayList<String>();
        }
        return this.defaultTarget;
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
     * Gets the value of the emailChannelEnabled property.
     * 
     */
    public boolean isEmailChannelEnabled() {
        return emailChannelEnabled;
    }

    /**
     * Sets the value of the emailChannelEnabled property.
     * 
     */
    public void setEmailChannelEnabled(boolean value) {
        this.emailChannelEnabled = value;
    }

    /**
     * Gets the value of the smsChannelEnabled property.
     * 
     */
    public boolean isSmsChannelEnabled() {
        return smsChannelEnabled;
    }

    /**
     * Sets the value of the smsChannelEnabled property.
     * 
     */
    public void setSmsChannelEnabled(boolean value) {
        this.smsChannelEnabled = value;
    }

    /**
     * Gets the value of the faxChannelEnabled property.
     * 
     */
    public boolean isFaxChannelEnabled() {
        return faxChannelEnabled;
    }

    /**
     * Sets the value of the faxChannelEnabled property.
     * 
     */
    public void setFaxChannelEnabled(boolean value) {
        this.faxChannelEnabled = value;
    }

}
