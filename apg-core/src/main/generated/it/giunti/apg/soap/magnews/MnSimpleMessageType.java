
package it.giunti.apg.soap.magnews;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mnSimpleMessageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnSimpleMessageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idMessageType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idMessageCategory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnSimpleMessageType", propOrder = {
    "idMessageType",
    "idMessageCategory",
    "name"
})
public class MnSimpleMessageType {

    protected String idMessageType;
    protected String idMessageCategory;
    protected String name;

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
     * Gets the value of the idMessageCategory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdMessageCategory() {
        return idMessageCategory;
    }

    /**
     * Sets the value of the idMessageCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdMessageCategory(String value) {
        this.idMessageCategory = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
