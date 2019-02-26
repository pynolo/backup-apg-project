
package it.giunti.apg.soap.magnews;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mnBatchStartOptions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnBatchStartOptions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idDatabase" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="addContactsToSimpleStaticGroup" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="idGroup" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="emptySimpleStaticGroup" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnBatchStartOptions", propOrder = {
    "name",
    "idDatabase",
    "addContactsToSimpleStaticGroup",
    "idGroup",
    "emptySimpleStaticGroup"
})
public class MnBatchStartOptions {

    protected String name;
    protected String idDatabase;
    protected boolean addContactsToSimpleStaticGroup;
    protected String idGroup;
    protected Boolean emptySimpleStaticGroup;

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

    /**
     * Gets the value of the idDatabase property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdDatabase() {
        return idDatabase;
    }

    /**
     * Sets the value of the idDatabase property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdDatabase(String value) {
        this.idDatabase = value;
    }

    /**
     * Gets the value of the addContactsToSimpleStaticGroup property.
     * 
     */
    public boolean isAddContactsToSimpleStaticGroup() {
        return addContactsToSimpleStaticGroup;
    }

    /**
     * Sets the value of the addContactsToSimpleStaticGroup property.
     * 
     */
    public void setAddContactsToSimpleStaticGroup(boolean value) {
        this.addContactsToSimpleStaticGroup = value;
    }

    /**
     * Gets the value of the idGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdGroup() {
        return idGroup;
    }

    /**
     * Sets the value of the idGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdGroup(String value) {
        this.idGroup = value;
    }

    /**
     * Gets the value of the emptySimpleStaticGroup property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEmptySimpleStaticGroup() {
        return emptySimpleStaticGroup;
    }

    /**
     * Sets the value of the emptySimpleStaticGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEmptySimpleStaticGroup(Boolean value) {
        this.emptySimpleStaticGroup = value;
    }

}
