
package it.giunti.apg.soap.magnews;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for singleSendOptions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="singleSendOptions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="customData" type="{http://webservices.magnews/}fieldValue" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="customFrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customSubject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="emailBodyEncoding" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="emailFormat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="emailSubjectEncoding" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="forceReaderId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idDatabase" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idMessageType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lookupReader" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="replyToAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "singleSendOptions", propOrder = {
    "customData",
    "customFrom",
    "customSubject",
    "emailBodyEncoding",
    "emailFormat",
    "emailSubjectEncoding",
    "forceReaderId",
    "idDatabase",
    "idMessageType",
    "lookupReader",
    "replyToAddress"
})
public class SingleSendOptions {

    @XmlElement(nillable = true)
    protected List<FieldValue> customData;
    protected String customFrom;
    protected String customSubject;
    protected String emailBodyEncoding;
    protected String emailFormat;
    protected String emailSubjectEncoding;
    protected String forceReaderId;
    protected String idDatabase;
    protected String idMessageType;
    protected boolean lookupReader;
    protected String replyToAddress;

    /**
     * Gets the value of the customData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the customData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCustomData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FieldValue }
     * 
     * 
     */
    public List<FieldValue> getCustomData() {
        if (customData == null) {
            customData = new ArrayList<FieldValue>();
        }
        return this.customData;
    }

    /**
     * Gets the value of the customFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomFrom() {
        return customFrom;
    }

    /**
     * Sets the value of the customFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomFrom(String value) {
        this.customFrom = value;
    }

    /**
     * Gets the value of the customSubject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomSubject() {
        return customSubject;
    }

    /**
     * Sets the value of the customSubject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomSubject(String value) {
        this.customSubject = value;
    }

    /**
     * Gets the value of the emailBodyEncoding property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailBodyEncoding() {
        return emailBodyEncoding;
    }

    /**
     * Sets the value of the emailBodyEncoding property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailBodyEncoding(String value) {
        this.emailBodyEncoding = value;
    }

    /**
     * Gets the value of the emailFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailFormat() {
        return emailFormat;
    }

    /**
     * Sets the value of the emailFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailFormat(String value) {
        this.emailFormat = value;
    }

    /**
     * Gets the value of the emailSubjectEncoding property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailSubjectEncoding() {
        return emailSubjectEncoding;
    }

    /**
     * Sets the value of the emailSubjectEncoding property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailSubjectEncoding(String value) {
        this.emailSubjectEncoding = value;
    }

    /**
     * Gets the value of the forceReaderId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForceReaderId() {
        return forceReaderId;
    }

    /**
     * Sets the value of the forceReaderId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForceReaderId(String value) {
        this.forceReaderId = value;
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
     * Gets the value of the idMessageType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdMessageType() {
        return idMessageType;
    }

    /**
     * Sets the value of the idMessageType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdMessageType(String value) {
        this.idMessageType = value;
    }

    /**
     * Gets the value of the lookupReader property.
     * 
     */
    public boolean isLookupReader() {
        return lookupReader;
    }

    /**
     * Sets the value of the lookupReader property.
     * 
     */
    public void setLookupReader(boolean value) {
        this.lookupReader = value;
    }

    /**
     * Gets the value of the replyToAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReplyToAddress() {
        return replyToAddress;
    }

    /**
     * Sets the value of the replyToAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReplyToAddress(String value) {
        this.replyToAddress = value;
    }

}
