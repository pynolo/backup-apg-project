
package it.giunti.apgws.wsbeans.giuntiscuolainfo2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Subscription complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Subscription">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codice" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo2}String8Type"/>
 *         &lt;element name="nomePeriodico" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo2}String64Type"/>
 *         &lt;element name="abbonato" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo2}Anagrafica"/>
 *         &lt;element name="pagante" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo2}Anagrafica" minOccurs="0"/>
 *         &lt;element name="copie" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo2}String4Type"/>
 *         &lt;element name="fascicoloInizio" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo2}String256Type"/>
 *         &lt;element name="fascicoloFine" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo2}String256Type"/>
 *         &lt;element name="inRegola" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="subscriptionStartDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="subscriptionExpiryDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="codiceTipoAbbonamento" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo2}String4Type"/>
 *         &lt;element name="descrizioneTipoAbbonamento" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo2}String256Type"/>
 *         &lt;element name="totaleFascicoliTipoAbbonamento" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo2}String4Type"/>
 *         &lt;element name="totaleFascicoliAbbonamento" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo2}String4Type"/>
 *         &lt;element name="cartaceo" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="digitale" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="tagList" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo2}String256Type" maxOccurs="256" minOccurs="0"/>
 *         &lt;element name="supplementiList" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo2}Supplemento" maxOccurs="256" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Subscription", propOrder = {
    "codice",
    "nomePeriodico",
    "abbonato",
    "pagante",
    "copie",
    "fascicoloInizio",
    "fascicoloFine",
    "inRegola",
    "subscriptionStartDate",
    "subscriptionExpiryDate",
    "codiceTipoAbbonamento",
    "descrizioneTipoAbbonamento",
    "totaleFascicoliTipoAbbonamento",
    "totaleFascicoliAbbonamento",
    "cartaceo",
    "digitale",
    "tagList",
    "supplementiList"
})
public class Subscription {

    @XmlElement(required = true)
    protected String codice;
    @XmlElement(required = true)
    protected String nomePeriodico;
    @XmlElement(required = true)
    protected Anagrafica abbonato;
    protected Anagrafica pagante;
    @XmlElement(required = true)
    protected String copie;
    @XmlElement(required = true)
    protected String fascicoloInizio;
    @XmlElement(required = true)
    protected String fascicoloFine;
    protected boolean inRegola;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar subscriptionStartDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar subscriptionExpiryDate;
    @XmlElement(required = true)
    protected String codiceTipoAbbonamento;
    @XmlElement(required = true)
    protected String descrizioneTipoAbbonamento;
    @XmlElement(required = true)
    protected String totaleFascicoliTipoAbbonamento;
    @XmlElement(required = true)
    protected String totaleFascicoliAbbonamento;
    protected boolean cartaceo;
    protected boolean digitale;
    protected List<String> tagList;
    protected List<Supplemento> supplementiList;

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
     * Gets the value of the copie property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCopie() {
        return copie;
    }

    /**
     * Sets the value of the copie property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCopie(String value) {
        this.copie = value;
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
     * Gets the value of the subscriptionStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSubscriptionStartDate() {
        return subscriptionStartDate;
    }

    /**
     * Sets the value of the subscriptionStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSubscriptionStartDate(XMLGregorianCalendar value) {
        this.subscriptionStartDate = value;
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
     * Gets the value of the codiceTipoAbbonamento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodiceTipoAbbonamento() {
        return codiceTipoAbbonamento;
    }

    /**
     * Sets the value of the codiceTipoAbbonamento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodiceTipoAbbonamento(String value) {
        this.codiceTipoAbbonamento = value;
    }

    /**
     * Gets the value of the descrizioneTipoAbbonamento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescrizioneTipoAbbonamento() {
        return descrizioneTipoAbbonamento;
    }

    /**
     * Sets the value of the descrizioneTipoAbbonamento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescrizioneTipoAbbonamento(String value) {
        this.descrizioneTipoAbbonamento = value;
    }

    /**
     * Gets the value of the totaleFascicoliTipoAbbonamento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotaleFascicoliTipoAbbonamento() {
        return totaleFascicoliTipoAbbonamento;
    }

    /**
     * Sets the value of the totaleFascicoliTipoAbbonamento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotaleFascicoliTipoAbbonamento(String value) {
        this.totaleFascicoliTipoAbbonamento = value;
    }

    /**
     * Gets the value of the totaleFascicoliAbbonamento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotaleFascicoliAbbonamento() {
        return totaleFascicoliAbbonamento;
    }

    /**
     * Sets the value of the totaleFascicoliAbbonamento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotaleFascicoliAbbonamento(String value) {
        this.totaleFascicoliAbbonamento = value;
    }

    /**
     * Gets the value of the cartaceo property.
     * 
     */
    public boolean isCartaceo() {
        return cartaceo;
    }

    /**
     * Sets the value of the cartaceo property.
     * 
     */
    public void setCartaceo(boolean value) {
        this.cartaceo = value;
    }

    /**
     * Gets the value of the digitale property.
     * 
     */
    public boolean isDigitale() {
        return digitale;
    }

    /**
     * Sets the value of the digitale property.
     * 
     */
    public void setDigitale(boolean value) {
        this.digitale = value;
    }

    /**
     * Gets the value of the tagList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tagList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTagList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTagList() {
        if (tagList == null) {
            tagList = new ArrayList<String>();
        }
        return this.tagList;
    }

    /**
     * Gets the value of the supplementiList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supplementiList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupplementiList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Supplemento }
     * 
     * 
     */
    public List<Supplemento> getSupplementiList() {
        if (supplementiList == null) {
            supplementiList = new ArrayList<Supplemento>();
        }
        return this.supplementiList;
    }

}
