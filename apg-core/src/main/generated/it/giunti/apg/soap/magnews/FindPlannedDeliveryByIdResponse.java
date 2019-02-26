
package it.giunti.apg.soap.magnews;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for findPlannedDeliveryByIdResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findPlannedDeliveryByIdResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://webservices.magnews/}mnPlannedDelivery" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findPlannedDeliveryByIdResponse", propOrder = {
    "_return"
})
public class FindPlannedDeliveryByIdResponse {

    @XmlElement(name = "return")
    protected MnPlannedDelivery _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link MnPlannedDelivery }
     *     
     */
    public MnPlannedDelivery getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link MnPlannedDelivery }
     *     
     */
    public void setReturn(MnPlannedDelivery value) {
        this._return = value;
    }

}
