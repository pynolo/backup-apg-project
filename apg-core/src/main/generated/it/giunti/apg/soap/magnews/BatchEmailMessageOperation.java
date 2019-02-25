
package it.giunti.apg.soap.magnews;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for batchEmailMessageOperation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="batchEmailMessageOperation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idmessage" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="customdata" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ok" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="debug" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "batchEmailMessageOperation", propOrder = {
    "idmessage",
    "customdata",
    "ok",
    "debug"
})
public class BatchEmailMessageOperation {

    protected int idmessage;
    protected String customdata;
    protected boolean ok;
    protected String debug;

    /**
     * Gets the value of the idmessage property.
     * 
     */
    public int getIdmessage() {
        return idmessage;
    }

    /**
     * Sets the value of the idmessage property.
     * 
     */
    public void setIdmessage(int value) {
        this.idmessage = value;
    }

    /**
     * Gets the value of the customdata property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomdata() {
        return customdata;
    }

    /**
     * Sets the value of the customdata property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomdata(String value) {
        this.customdata = value;
    }

    /**
     * Gets the value of the ok property.
     * 
     */
    public boolean isOk() {
        return ok;
    }

    /**
     * Sets the value of the ok property.
     * 
     */
    public void setOk(boolean value) {
        this.ok = value;
    }

    /**
     * Gets the value of the debug property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDebug() {
        return debug;
    }

    /**
     * Sets the value of the debug property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDebug(String value) {
        this.debug = value;
    }

}
