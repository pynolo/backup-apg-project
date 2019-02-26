
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
 * <p>Java class for mnNewsletterDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnNewsletterDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="creationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="idCampaign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idEditor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idNewsletter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sent" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="values" type="{http://webservices.magnews/}fieldValue" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnNewsletterDetails", propOrder = {
    "creationDate",
    "idCampaign",
    "idEditor",
    "idNewsletter",
    "name",
    "sent",
    "values"
})
public class MnNewsletterDetails {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creationDate;
    protected String idCampaign;
    protected String idEditor;
    protected String idNewsletter;
    protected String name;
    protected boolean sent;
    @XmlElement(nillable = true)
    protected List<FieldValue> values;

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
     * Gets the value of the values property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the values property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FieldValue }
     * 
     * 
     */
    public List<FieldValue> getValues() {
        if (values == null) {
            values = new ArrayList<FieldValue>();
        }
        return this.values;
    }

}
