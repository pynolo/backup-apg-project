
package it.giunti.apg.soap.magnews;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fetchSurveySessions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fetchSurveySessions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idRowSet" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fromIndex" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="toIndex" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="credentials" type="{http://webservices.magnews/}credentials" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fetchSurveySessions", propOrder = {
    "idRowSet",
    "fromIndex",
    "toIndex",
    "credentials"
})
public class FetchSurveySessions {

    protected String idRowSet;
    protected int fromIndex;
    protected int toIndex;
    protected Credentials credentials;

    /**
     * Gets the value of the idRowSet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdRowSet() {
        return idRowSet;
    }

    /**
     * Sets the value of the idRowSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdRowSet(String value) {
        this.idRowSet = value;
    }

    /**
     * Gets the value of the fromIndex property.
     * 
     */
    public int getFromIndex() {
        return fromIndex;
    }

    /**
     * Sets the value of the fromIndex property.
     * 
     */
    public void setFromIndex(int value) {
        this.fromIndex = value;
    }

    /**
     * Gets the value of the toIndex property.
     * 
     */
    public int getToIndex() {
        return toIndex;
    }

    /**
     * Sets the value of the toIndex property.
     * 
     */
    public void setToIndex(int value) {
        this.toIndex = value;
    }

    /**
     * Gets the value of the credentials property.
     * 
     * @return
     *     possible object is
     *     {@link Credentials }
     *     
     */
    public Credentials getCredentials() {
        return credentials;
    }

    /**
     * Sets the value of the credentials property.
     * 
     * @param value
     *     allowed object is
     *     {@link Credentials }
     *     
     */
    public void setCredentials(Credentials value) {
        this.credentials = value;
    }

}
