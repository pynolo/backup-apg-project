
package it.giunti.apgws.wsbeans.giuntiscuolainfo2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="codiceSupplemento" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo2}String16Type"/>
 *         &lt;element name="nomeSupplemento" type="{http://applicazioni.giunti.it/apgws/giuntiscuolainfo2}String64Type"/>
 *         &lt;element name="incluso" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "incluso"
})
public class Supplemento {

    @XmlElement(required = true)
    protected String codiceSupplemento;
    @XmlElement(required = true)
    protected String nomeSupplemento;
    protected boolean incluso;

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
     * Gets the value of the incluso property.
     * 
     */
    public boolean isIncluso() {
        return incluso;
    }

    /**
     * Sets the value of the incluso property.
     * 
     */
    public void setIncluso(boolean value) {
        this.incluso = value;
    }

}
