
package it.giunti.apg.soap.magnews;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for emailMessage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="emailMessage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fromemail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fromname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="to" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="replyto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="chartset" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="htmlbody" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="textbody" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idmessagetype" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="header" type="{http://webservices.magnews/}fieldValue" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="attachment" type="{http://webservices.magnews/}attachment" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="tempvar" type="{http://webservices.magnews/}typedValue" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="externalId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="inputparam" type="{http://webservices.magnews/}typedValue" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "emailMessage", propOrder = {
    "fromemail",
    "fromname",
    "to",
    "replyto",
    "subject",
    "chartset",
    "htmlbody",
    "textbody",
    "idmessagetype",
    "header",
    "attachment",
    "tempvar",
    "externalId",
    "inputparam"
})
public class EmailMessage {

    protected String fromemail;
    protected String fromname;
    protected String to;
    protected String replyto;
    protected String subject;
    protected String chartset;
    protected String htmlbody;
    protected String textbody;
    protected String idmessagetype;
    protected List<FieldValue> header;
    protected List<Attachment> attachment;
    protected List<TypedValue> tempvar;
    protected String externalId;
    protected List<TypedValue> inputparam;

    /**
     * Gets the value of the fromemail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromemail() {
        return fromemail;
    }

    /**
     * Sets the value of the fromemail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromemail(String value) {
        this.fromemail = value;
    }

    /**
     * Gets the value of the fromname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromname() {
        return fromname;
    }

    /**
     * Sets the value of the fromname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromname(String value) {
        this.fromname = value;
    }

    /**
     * Gets the value of the to property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets the value of the to property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTo(String value) {
        this.to = value;
    }

    /**
     * Gets the value of the replyto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReplyto() {
        return replyto;
    }

    /**
     * Sets the value of the replyto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReplyto(String value) {
        this.replyto = value;
    }

    /**
     * Gets the value of the subject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the value of the subject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubject(String value) {
        this.subject = value;
    }

    /**
     * Gets the value of the chartset property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChartset() {
        return chartset;
    }

    /**
     * Sets the value of the chartset property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChartset(String value) {
        this.chartset = value;
    }

    /**
     * Gets the value of the htmlbody property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHtmlbody() {
        return htmlbody;
    }

    /**
     * Sets the value of the htmlbody property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHtmlbody(String value) {
        this.htmlbody = value;
    }

    /**
     * Gets the value of the textbody property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTextbody() {
        return textbody;
    }

    /**
     * Sets the value of the textbody property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTextbody(String value) {
        this.textbody = value;
    }

    /**
     * Gets the value of the idmessagetype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdmessagetype() {
        return idmessagetype;
    }

    /**
     * Sets the value of the idmessagetype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdmessagetype(String value) {
        this.idmessagetype = value;
    }

    /**
     * Gets the value of the header property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the header property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHeader().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FieldValue }
     * 
     * 
     */
    public List<FieldValue> getHeader() {
        if (header == null) {
            header = new ArrayList<FieldValue>();
        }
        return this.header;
    }

    /**
     * Gets the value of the attachment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attachment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttachment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Attachment }
     * 
     * 
     */
    public List<Attachment> getAttachment() {
        if (attachment == null) {
            attachment = new ArrayList<Attachment>();
        }
        return this.attachment;
    }

    /**
     * Gets the value of the tempvar property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tempvar property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTempvar().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypedValue }
     * 
     * 
     */
    public List<TypedValue> getTempvar() {
        if (tempvar == null) {
            tempvar = new ArrayList<TypedValue>();
        }
        return this.tempvar;
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

    /**
     * Gets the value of the inputparam property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inputparam property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInputparam().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypedValue }
     * 
     * 
     */
    public List<TypedValue> getInputparam() {
        if (inputparam == null) {
            inputparam = new ArrayList<TypedValue>();
        }
        return this.inputparam;
    }

}
