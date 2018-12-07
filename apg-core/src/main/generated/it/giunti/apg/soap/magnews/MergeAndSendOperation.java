
package it.giunti.apg.soap.magnews;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mergeAndSendOperation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mergeAndSendOperation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="customData" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="errorOccurred" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="mergeResult" type="{http://webservices.magnews/}mnContactOperation" minOccurs="0"/>
 *         &lt;element name="message" type="{http://webservices.magnews/}onDemandMessage" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mergeAndSendOperation", propOrder = {
    "customData",
    "errorOccurred",
    "mergeResult",
    "message"
})
public class MergeAndSendOperation {

    protected Object customData;
    protected boolean errorOccurred;
    protected MnContactOperation mergeResult;
    protected OnDemandMessage message;

    /**
     * Gets the value of the customData property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getCustomData() {
        return customData;
    }

    /**
     * Sets the value of the customData property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setCustomData(Object value) {
        this.customData = value;
    }

    /**
     * Gets the value of the errorOccurred property.
     * 
     */
    public boolean isErrorOccurred() {
        return errorOccurred;
    }

    /**
     * Sets the value of the errorOccurred property.
     * 
     */
    public void setErrorOccurred(boolean value) {
        this.errorOccurred = value;
    }

    /**
     * Gets the value of the mergeResult property.
     * 
     * @return
     *     possible object is
     *     {@link MnContactOperation }
     *     
     */
    public MnContactOperation getMergeResult() {
        return mergeResult;
    }

    /**
     * Sets the value of the mergeResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link MnContactOperation }
     *     
     */
    public void setMergeResult(MnContactOperation value) {
        this.mergeResult = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link OnDemandMessage }
     *     
     */
    public OnDemandMessage getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link OnDemandMessage }
     *     
     */
    public void setMessage(OnDemandMessage value) {
        this.message = value;
    }

}
