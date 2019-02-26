
package it.giunti.apg.soap.magnews;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for smsMessage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="smsMessage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="from" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="to" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smsbody" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idmessagetype" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tempvar" type="{http://webservices.magnews/}typedValue" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "smsMessage", propOrder = {
    "from",
    "to",
    "smsbody",
    "idmessagetype",
    "tempvar",
    "externalId"
})
public class SmsMessage {

    protected String from;
    protected String to;
    protected String smsbody;
    protected String idmessagetype;
    protected List<TypedValue> tempvar;
    protected String externalId;

    /**
     * Gets the value of the from property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets the value of the from property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrom(String value) {
        this.from = value;
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
     * Gets the value of the smsbody property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmsbody() {
        return smsbody;
    }

    /**
     * Sets the value of the smsbody property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmsbody(String value) {
        this.smsbody = value;
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

}
