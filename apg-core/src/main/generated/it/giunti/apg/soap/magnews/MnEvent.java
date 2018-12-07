
package it.giunti.apg.soap.magnews;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mnEvent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnEvent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bounceCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="bounceType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="eventTimestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="eventType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idContact" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="idContent" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="idDelivery" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="idWebPage" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ipAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="position" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="referer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subIdDelivery" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="userAgent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnEvent", propOrder = {
    "bounceCode",
    "bounceType",
    "eventTimestamp",
    "eventType",
    "idContact",
    "idContent",
    "idDelivery",
    "idWebPage",
    "ipAddress",
    "position",
    "referer",
    "subIdDelivery",
    "userAgent",
    "value"
})
public class MnEvent {

    protected int bounceCode;
    protected String bounceType;
    protected long eventTimestamp;
    protected String eventType;
    protected int idContact;
    protected int idContent;
    protected int idDelivery;
    protected int idWebPage;
    protected String ipAddress;
    protected String position;
    protected String referer;
    protected int subIdDelivery;
    protected String userAgent;
    protected int value;

    /**
     * Gets the value of the bounceCode property.
     * 
     */
    public int getBounceCode() {
        return bounceCode;
    }

    /**
     * Sets the value of the bounceCode property.
     * 
     */
    public void setBounceCode(int value) {
        this.bounceCode = value;
    }

    /**
     * Gets the value of the bounceType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBounceType() {
        return bounceType;
    }

    /**
     * Sets the value of the bounceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBounceType(String value) {
        this.bounceType = value;
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
     * Gets the value of the idContent property.
     * 
     */
    public int getIdContent() {
        return idContent;
    }

    /**
     * Sets the value of the idContent property.
     * 
     */
    public void setIdContent(int value) {
        this.idContent = value;
    }

    /**
     * Gets the value of the idDelivery property.
     * 
     */
    public int getIdDelivery() {
        return idDelivery;
    }

    /**
     * Sets the value of the idDelivery property.
     * 
     */
    public void setIdDelivery(int value) {
        this.idDelivery = value;
    }

    /**
     * Gets the value of the idWebPage property.
     * 
     */
    public int getIdWebPage() {
        return idWebPage;
    }

    /**
     * Sets the value of the idWebPage property.
     * 
     */
    public void setIdWebPage(int value) {
        this.idWebPage = value;
    }

    /**
     * Gets the value of the ipAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the value of the ipAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpAddress(String value) {
        this.ipAddress = value;
    }

    /**
     * Gets the value of the position property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPosition() {
        return position;
    }

    /**
     * Sets the value of the position property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPosition(String value) {
        this.position = value;
    }

    /**
     * Gets the value of the referer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferer() {
        return referer;
    }

    /**
     * Sets the value of the referer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferer(String value) {
        this.referer = value;
    }

    /**
     * Gets the value of the subIdDelivery property.
     * 
     */
    public int getSubIdDelivery() {
        return subIdDelivery;
    }

    /**
     * Sets the value of the subIdDelivery property.
     * 
     */
    public void setSubIdDelivery(int value) {
        this.subIdDelivery = value;
    }

    /**
     * Gets the value of the userAgent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Sets the value of the userAgent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserAgent(String value) {
        this.userAgent = value;
    }

    /**
     * Gets the value of the value property.
     * 
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     */
    public void setValue(int value) {
        this.value = value;
    }

}
