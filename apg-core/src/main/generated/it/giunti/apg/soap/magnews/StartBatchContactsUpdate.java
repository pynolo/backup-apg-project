
package it.giunti.apg.soap.magnews;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for startBatchContactsUpdate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="startBatchContactsUpdate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idRowSet" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="options" type="{http://webservices.magnews/}mnBatchStartOptions" minOccurs="0"/>
 *         &lt;element name="advancedOptions" type="{http://webservices.magnews/}option" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "startBatchContactsUpdate", propOrder = {
    "idRowSet",
    "options",
    "advancedOptions",
    "credentials"
})
public class StartBatchContactsUpdate {

    protected String idRowSet;
    protected MnBatchStartOptions options;
    protected List<Option> advancedOptions;
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
     * Gets the value of the options property.
     * 
     * @return
     *     possible object is
     *     {@link MnBatchStartOptions }
     *     
     */
    public MnBatchStartOptions getOptions() {
        return options;
    }

    /**
     * Sets the value of the options property.
     * 
     * @param value
     *     allowed object is
     *     {@link MnBatchStartOptions }
     *     
     */
    public void setOptions(MnBatchStartOptions value) {
        this.options = value;
    }

    /**
     * Gets the value of the advancedOptions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the advancedOptions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdvancedOptions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Option }
     * 
     * 
     */
    public List<Option> getAdvancedOptions() {
        if (advancedOptions == null) {
            advancedOptions = new ArrayList<Option>();
        }
        return this.advancedOptions;
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
