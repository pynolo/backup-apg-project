
package it.giunti.apg.soap.magnews;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mnSimpleMessageTypeReport complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnSimpleMessageTypeReport">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idMessageType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sent" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="received" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="opened" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="clicked" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="complainted" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="converted" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="unsubscribed" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="hardBounced" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="softBounced" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="totalOpens" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="totalClicks" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="totalConversions" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnSimpleMessageTypeReport", propOrder = {
    "idMessageType",
    "sent",
    "received",
    "opened",
    "clicked",
    "complainted",
    "converted",
    "unsubscribed",
    "hardBounced",
    "softBounced",
    "totalOpens",
    "totalClicks",
    "totalConversions"
})
public class MnSimpleMessageTypeReport {

    protected String idMessageType;
    protected long sent;
    protected long received;
    protected long opened;
    protected long clicked;
    protected long complainted;
    protected long converted;
    protected long unsubscribed;
    protected long hardBounced;
    protected long softBounced;
    protected long totalOpens;
    protected long totalClicks;
    protected long totalConversions;

    /**
     * Gets the value of the idMessageType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdMessageType() {
        return idMessageType;
    }

    /**
     * Sets the value of the idMessageType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdMessageType(String value) {
        this.idMessageType = value;
    }

    /**
     * Gets the value of the sent property.
     * 
     */
    public long getSent() {
        return sent;
    }

    /**
     * Sets the value of the sent property.
     * 
     */
    public void setSent(long value) {
        this.sent = value;
    }

    /**
     * Gets the value of the received property.
     * 
     */
    public long getReceived() {
        return received;
    }

    /**
     * Sets the value of the received property.
     * 
     */
    public void setReceived(long value) {
        this.received = value;
    }

    /**
     * Gets the value of the opened property.
     * 
     */
    public long getOpened() {
        return opened;
    }

    /**
     * Sets the value of the opened property.
     * 
     */
    public void setOpened(long value) {
        this.opened = value;
    }

    /**
     * Gets the value of the clicked property.
     * 
     */
    public long getClicked() {
        return clicked;
    }

    /**
     * Sets the value of the clicked property.
     * 
     */
    public void setClicked(long value) {
        this.clicked = value;
    }

    /**
     * Gets the value of the complainted property.
     * 
     */
    public long getComplainted() {
        return complainted;
    }

    /**
     * Sets the value of the complainted property.
     * 
     */
    public void setComplainted(long value) {
        this.complainted = value;
    }

    /**
     * Gets the value of the converted property.
     * 
     */
    public long getConverted() {
        return converted;
    }

    /**
     * Sets the value of the converted property.
     * 
     */
    public void setConverted(long value) {
        this.converted = value;
    }

    /**
     * Gets the value of the unsubscribed property.
     * 
     */
    public long getUnsubscribed() {
        return unsubscribed;
    }

    /**
     * Sets the value of the unsubscribed property.
     * 
     */
    public void setUnsubscribed(long value) {
        this.unsubscribed = value;
    }

    /**
     * Gets the value of the hardBounced property.
     * 
     */
    public long getHardBounced() {
        return hardBounced;
    }

    /**
     * Sets the value of the hardBounced property.
     * 
     */
    public void setHardBounced(long value) {
        this.hardBounced = value;
    }

    /**
     * Gets the value of the softBounced property.
     * 
     */
    public long getSoftBounced() {
        return softBounced;
    }

    /**
     * Sets the value of the softBounced property.
     * 
     */
    public void setSoftBounced(long value) {
        this.softBounced = value;
    }

    /**
     * Gets the value of the totalOpens property.
     * 
     */
    public long getTotalOpens() {
        return totalOpens;
    }

    /**
     * Sets the value of the totalOpens property.
     * 
     */
    public void setTotalOpens(long value) {
        this.totalOpens = value;
    }

    /**
     * Gets the value of the totalClicks property.
     * 
     */
    public long getTotalClicks() {
        return totalClicks;
    }

    /**
     * Sets the value of the totalClicks property.
     * 
     */
    public void setTotalClicks(long value) {
        this.totalClicks = value;
    }

    /**
     * Gets the value of the totalConversions property.
     * 
     */
    public long getTotalConversions() {
        return totalConversions;
    }

    /**
     * Sets the value of the totalConversions property.
     * 
     */
    public void setTotalConversions(long value) {
        this.totalConversions = value;
    }

}
