
package it.giunti.apgws.wsbeans.giuntiscuolainfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Supplemento complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Supplemento">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codiceSupplemento" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo}String16Type"/>
 *         &lt;element name="nomeSupplemento" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo}String64Type"/>
 *         &lt;element name="tags" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo}String256Type" minOccurs="0"/>
 *         &lt;element name="subscriptionExpiryDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Supplemento", propOrder = {
    "codiceSupplemento",
    "nomeSupplemento",
    "tags",
    "subscriptionExpiryDate"
})
public class Supplemento {

    @XmlElement(required = true)
    protected String codiceSupplemento;
    @XmlElement(required = true)
    protected String nomeSupplemento;
    protected String tags;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar subscriptionExpiryDate;

    /**
     * Gets the value of the codiceSupplemento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodiceSupplemento() {
        return codiceSupplemento;
    }

    /**
     * Sets the value of the codiceSupplemento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodiceSupplemento(String value) {
        this.codiceSupplemento = value;
    }

    /**
     * Gets the value of the nomeSupplemento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeSupplemento() {
        return nomeSupplemento;
    }

    /**
     * Sets the value of the nomeSupplemento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeSupplemento(String value) {
        this.nomeSupplemento = value;
    }

    /**
     * Gets the value of the tags property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTags() {
        return tags;
    }

    /**
     * Sets the value of the tags property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTags(String value) {
        this.tags = value;
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

}
