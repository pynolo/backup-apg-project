
package it.giunti.apg.soap.magnews;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for mnDeliveryStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnDeliveryStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="active" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="idDeliveryStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idNewsletter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestedEmailMessages" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="requestedFaxMessages" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="requestedSmsMessages" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="target" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="idPlannedDelivery" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="additionTargetFilterPresent" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnDeliveryStatus", propOrder = {
    "active",
    "idDeliveryStatus",
    "idNewsletter",
    "requestedEmailMessages",
    "requestedFaxMessages",
    "requestedSmsMessages",
    "startDate",
    "status",
    "target",
    "idPlannedDelivery",
    "additionTargetFilterPresent"
})
public class MnDeliveryStatus {

    protected boolean active;
    protected String idDeliveryStatus;
    protected String idNewsletter;
    protected int requestedEmailMessages;
    protected int requestedFaxMessages;
    protected int requestedSmsMessages;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar startDate;
    protected String status;
    @XmlElement(nillable = true)
    protected List<String> target;
    protected String idPlannedDelivery;
    protected boolean additionTargetFilterPresent;

    /**
     * Gets the value of the active property.
     * 
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the value of the active property.
     * 
     */
    public void setActive(boolean value) {
        this.active = value;
    }

    /**
     * Gets the value of the idDeliveryStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdDeliveryStatus() {
        return idDeliveryStatus;
    }

    /**
     * Sets the value of the idDeliveryStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdDeliveryStatus(String value) {
        this.idDeliveryStatus = value;
    }

    /**
     * Gets the value of the idNewsletter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdNewsletter() {
        return idNewsletter;
    }

    /**
     * Sets the value of the idNewsletter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdNewsletter(String value) {
        this.idNewsletter = value;
    }

    /**
     * Gets the value of the requestedEmailMessages property.
     * 
     */
    public int getRequestedEmailMessages() {
        return requestedEmailMessages;
    }

    /**
     * Sets the value of the requestedEmailMessages property.
     * 
     */
    public void setRequestedEmailMessages(int value) {
        this.requestedEmailMessages = value;
    }

    /**
     * Gets the value of the requestedFaxMessages property.
     * 
     */
    public int getRequestedFaxMessages() {
        return requestedFaxMessages;
    }

    /**
     * Sets the value of the requestedFaxMessages property.
     * 
     */
    public void setRequestedFaxMessages(int value) {
        this.requestedFaxMessages = value;
    }

    /**
     * Gets the value of the requestedSmsMessages property.
     * 
     */
    public int getRequestedSmsMessages() {
        return requestedSmsMessages;
    }

    /**
     * Sets the value of the requestedSmsMessages property.
     * 
     */
    public void setRequestedSmsMessages(int value) {
        this.requestedSmsMessages = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStartDate(XMLGregorianCalendar value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the target property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the target property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTarget().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTarget() {
        if (target == null) {
            target = new ArrayList<String>();
        }
        return this.target;
    }

    /**
     * Gets the value of the idPlannedDelivery property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdPlannedDelivery() {
        return idPlannedDelivery;
    }

    /**
     * Sets the value of the idPlannedDelivery property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdPlannedDelivery(String value) {
        this.idPlannedDelivery = value;
    }

    /**
     * Gets the value of the additionTargetFilterPresent property.
     * 
     */
    public boolean isAdditionTargetFilterPresent() {
        return additionTargetFilterPresent;
    }

    /**
     * Sets the value of the additionTargetFilterPresent property.
     * 
     */
    public void setAdditionTargetFilterPresent(boolean value) {
        this.additionTargetFilterPresent = value;
    }

}
