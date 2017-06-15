
package it.giunti.apgws.wsbeans.giuntiscuolainfo;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for SubscriberData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SubscriberData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codice" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo}String8Type"/>
 *         &lt;element name="nomePeriodico" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo}String64Type"/>
 *         &lt;element name="abbonato" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo}Anagrafica"/>
 *         &lt;element name="pagante" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo}Anagrafica" minOccurs="0"/>
 *         &lt;element name="fascicoloInizio" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo}String256Type"/>
 *         &lt;element name="fascicoloFine" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo}String256Type"/>
 *         &lt;element name="inRegola" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="subscriptionExpiryDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="gracingExpiryDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="supplemento" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo}Supplemento" maxOccurs="256" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubscriberData", propOrder = {
    "codice",
    "nomePeriodico",
    "abbonato",
    "pagante",
    "fascicoloInizio",
    "fascicoloFine",
    "inRegola",
    "subscriptionExpiryDate",
    "gracingExpiryDate",
    "supplemento"
})
public class SubscriberData {

    @XmlElement(required = true)
    protected String codice;
    @XmlElement(required = true)
    protected String nomePeriodico;
    @XmlElement(required = true)
    protected Anagrafica abbonato;
    protected Anagrafica pagante;
    @XmlElement(required = true)
    protected String fascicoloInizio;
    @XmlElement(required = true)
    protected String fascicoloFine;
    protected boolean inRegola;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar subscriptionExpiryDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar gracingExpiryDate;
    protected List<Supplemento> supplemento;

    /**
     * Gets the value of the codice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodice() {
        return codice;
    }

    /**
     * Sets the value of the codice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodice(String value) {
        this.codice = value;
    }

    /**
     * Gets the value of the nomePeriodico property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomePeriodico() {
        return nomePeriodico;
    }

    /**
     * Sets the value of the nomePeriodico property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomePeriodico(String value) {
        this.nomePeriodico = value;
    }

    /**
     * Gets the value of the abbonato property.
     * 
     * @return
     *     possible object is
     *     {@link Anagrafica }
     *     
     */
    public Anagrafica getAbbonato() {
        return abbonato;
    }

    /**
     * Sets the value of the abbonato property.
     * 
     * @param value
     *     allowed object is
     *     {@link Anagrafica }
     *     
     */
    public void setAbbonato(Anagrafica value) {
        this.abbonato = value;
    }

    /**
     * Gets the value of the pagante property.
     * 
     * @return
     *     possible object is
     *     {@link Anagrafica }
     *     
     */
    public Anagrafica getPagante() {
        return pagante;
    }

    /**
     * Sets the value of the pagante property.
     * 
     * @param value
     *     allowed object is
     *     {@link Anagrafica }
     *     
     */
    public void setPagante(Anagrafica value) {
        this.pagante = value;
    }

    /**
     * Gets the value of the fascicoloInizio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFascicoloInizio() {
        return fascicoloInizio;
    }

    /**
     * Sets the value of the fascicoloInizio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFascicoloInizio(String value) {
        this.fascicoloInizio = value;
    }

    /**
     * Gets the value of the fascicoloFine property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFascicoloFine() {
        return fascicoloFine;
    }

    /**
     * Sets the value of the fascicoloFine property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFascicoloFine(String value) {
        this.fascicoloFine = value;
    }

    /**
     * Gets the value of the inRegola property.
     * 
     */
    public boolean isInRegola() {
        return inRegola;
    }

    /**
     * Sets the value of the inRegola property.
     * 
     */
    public void setInRegola(boolean value) {
        this.inRegola = value;
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

    /**
     * Gets the value of the gracingExpiryDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getGracingExpiryDate() {
        return gracingExpiryDate;
    }

    /**
     * Sets the value of the gracingExpiryDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setGracingExpiryDate(XMLGregorianCalendar value) {
        this.gracingExpiryDate = value;
    }

    /**
     * Gets the value of the supplemento property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supplemento property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupplemento().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Supplemento }
     * 
     * 
     */
    public List<Supplemento> getSupplemento() {
        if (supplemento == null) {
            supplemento = new ArrayList<Supplemento>();
        }
        return this.supplemento;
    }

}
