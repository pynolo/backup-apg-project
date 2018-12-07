
package it.giunti.apg.soap.magnews;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mnPage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnPage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idPage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idSurvey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idWebsite" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnPage", propOrder = {
    "idPage",
    "idSurvey",
    "idWebsite",
    "name",
    "type"
})
public class MnPage {

    protected String idPage;
    protected String idSurvey;
    protected String idWebsite;
    protected String name;
    protected String type;

    /**
     * Gets the value of the idPage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdPage() {
        return idPage;
    }

    /**
     * Sets the value of the idPage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdPage(String value) {
        this.idPage = value;
    }

    /**
     * Gets the value of the idSurvey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdSurvey() {
        return idSurvey;
    }

    /**
     * Sets the value of the idSurvey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdSurvey(String value) {
        this.idSurvey = value;
    }

    /**
     * Gets the value of the idWebsite property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdWebsite() {
        return idWebsite;
    }

    /**
     * Sets the value of the idWebsite property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdWebsite(String value) {
        this.idWebsite = value;
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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

}
