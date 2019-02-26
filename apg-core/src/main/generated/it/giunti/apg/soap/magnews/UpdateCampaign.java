
package it.giunti.apg.soap.magnews;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for updateCampaign complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updateCampaign">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idCampaign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="field" type="{http://webservices.magnews/}fieldValue" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="options" type="{http://webservices.magnews/}option" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "updateCampaign", propOrder = {
    "idCampaign",
    "field",
    "options",
    "credentials"
})
public class UpdateCampaign {

    protected String idCampaign;
    protected List<FieldValue> field;
    protected List<Option> options;
    protected Credentials credentials;

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
     * Gets the value of the field property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the field property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getField().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FieldValue }
     * 
     * 
     */
    public List<FieldValue> getField() {
        if (field == null) {
            field = new ArrayList<FieldValue>();
        }
        return this.field;
    }

    /**
     * Gets the value of the options property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the options property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOptions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Option }
     * 
     * 
     */
    public List<Option> getOptions() {
        if (options == null) {
            options = new ArrayList<Option>();
        }
        return this.options;
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
