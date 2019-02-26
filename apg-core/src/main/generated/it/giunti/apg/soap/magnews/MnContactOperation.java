
package it.giunti.apg.soap.magnews;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mnContactOperation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnContactOperation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="actionResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="debug" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="errors" type="{http://webservices.magnews/}mnError" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="idContact" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ok" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="primaryKeyValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestedAction" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sendEmailResult" type="{http://webservices.magnews/}mnSendEmailResult" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnContactOperation", propOrder = {
    "actionResult",
    "customId",
    "debug",
    "errors",
    "idContact",
    "ok",
    "primaryKeyValue",
    "requestedAction",
    "sendEmailResult"
})
public class MnContactOperation {

    protected String actionResult;
    protected String customId;
    protected String debug;
    @XmlElement(nillable = true)
    protected List<MnError> errors;
    protected String idContact;
    protected boolean ok;
    protected String primaryKeyValue;
    protected String requestedAction;
    protected MnSendEmailResult sendEmailResult;

    /**
     * Gets the value of the actionResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActionResult() {
        return actionResult;
    }

    /**
     * Sets the value of the actionResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActionResult(String value) {
        this.actionResult = value;
    }

    /**
     * Gets the value of the customId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomId() {
        return customId;
    }

    /**
     * Sets the value of the customId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomId(String value) {
        this.customId = value;
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

    /**
     * Gets the value of the errors property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the errors property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getErrors().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MnError }
     * 
     * 
     */
    public List<MnError> getErrors() {
        if (errors == null) {
            errors = new ArrayList<MnError>();
        }
        return this.errors;
    }

    /**
     * Gets the value of the idContact property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdContact() {
        return idContact;
    }

    /**
     * Sets the value of the idContact property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdContact(String value) {
        this.idContact = value;
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
     * Gets the value of the primaryKeyValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrimaryKeyValue() {
        return primaryKeyValue;
    }

    /**
     * Sets the value of the primaryKeyValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrimaryKeyValue(String value) {
        this.primaryKeyValue = value;
    }

    /**
     * Gets the value of the requestedAction property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestedAction() {
        return requestedAction;
    }

    /**
     * Sets the value of the requestedAction property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestedAction(String value) {
        this.requestedAction = value;
    }

    /**
     * Gets the value of the sendEmailResult property.
     * 
     * @return
     *     possible object is
     *     {@link MnSendEmailResult }
     *     
     */
    public MnSendEmailResult getSendEmailResult() {
        return sendEmailResult;
    }

    /**
     * Sets the value of the sendEmailResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link MnSendEmailResult }
     *     
     */
    public void setSendEmailResult(MnSendEmailResult value) {
        this.sendEmailResult = value;
    }

}
