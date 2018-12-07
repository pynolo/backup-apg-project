
package it.giunti.apg.soap.magnews;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for simpleMessageEvent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="simpleMessageEvent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idSimpleMessage" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="idContact" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="eventType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="open" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="link" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="eventTimestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="otherValues" type="{http://webservices.magnews/}fieldValue" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="idChannel" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "simpleMessageEvent", propOrder = {
    "idSimpleMessage",
    "idContact",
    "eventType",
    "open",
    "link",
    "eventTimestamp",
    "otherValues",
    "idChannel"
})
public class SimpleMessageEvent {

    protected int idSimpleMessage;
    protected int idContact;
    protected String eventType;
    protected boolean open;
    protected String link;
    protected long eventTimestamp;
    @XmlElement(nillable = true)
    protected List<FieldValue> otherValues;
    protected int idChannel;

    /**
     * Gets the value of the idSimpleMessage property.
     * 
     */
    public int getIdSimpleMessage() {
        return idSimpleMessage;
    }

    /**
     * Sets the value of the idSimpleMessage property.
     * 
     */
    public void setIdSimpleMessage(int value) {
        this.idSimpleMessage = value;
    }

    /**
     * Gets the value of the idContact property.
     * 
     */
    public int getIdContact() {
        return idContact;
    }

    /**
     * Sets the value of the idContact property.
     * 
     */
    public void setIdContact(int value) {
        this.idContact = value;
    }

    /**
     * Gets the value of the eventType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Sets the value of the eventType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventType(String value) {
        this.eventType = value;
    }

    /**
     * Gets the value of the open property.
     * 
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Sets the value of the open property.
     * 
     */
    public void setOpen(boolean value) {
        this.open = value;
    }

    /**
     * Gets the value of the link property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets the value of the link property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLink(String value) {
        this.link = value;
    }

    /**
     * Gets the value of the eventTimestamp property.
     * 
     */
    public long getEventTimestamp() {
        return eventTimestamp;
    }

    /**
     * Sets the value of the eventTimestamp property.
     * 
     */
    public void setEventTimestamp(long value) {
        this.eventTimestamp = value;
    }

    /**
     * Gets the value of the otherValues property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the otherValues property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOtherValues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FieldValue }
     * 
     * 
     */
    public List<FieldValue> getOtherValues() {
        if (otherValues == null) {
            otherValues = new ArrayList<FieldValue>();
        }
        return this.otherValues;
    }

    /**
     * Gets the value of the idChannel property.
     * 
     */
    public int getIdChannel() {
        return idChannel;
    }

    /**
     * Sets the value of the idChannel property.
     * 
     */
    public void setIdChannel(int value) {
        this.idChannel = value;
    }

}
