
package it.giunti.apg.soap.magnews;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for mnGroupInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnGroupInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idGroup" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idList" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idParent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="expireDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="staticgroup" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnGroupInfo", propOrder = {
    "idGroup",
    "idList",
    "idParent",
    "name",
    "expireDate",
    "staticgroup"
})
public class MnGroupInfo {

    protected String idGroup;
    protected String idList;
    protected String idParent;
    protected String name;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar expireDate;
    protected boolean staticgroup;

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
     * Gets the value of the idList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdList() {
        return idList;
    }

    /**
     * Sets the value of the idList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdList(String value) {
        this.idList = value;
    }

    /**
     * Gets the value of the idParent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdParent() {
        return idParent;
    }

    /**
     * Sets the value of the idParent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdParent(String value) {
        this.idParent = value;
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

    /**
     * Gets the value of the expireDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpireDate() {
        return expireDate;
    }

    /**
     * Sets the value of the expireDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpireDate(XMLGregorianCalendar value) {
        this.expireDate = value;
    }

    /**
     * Gets the value of the staticgroup property.
     * 
     */
    public boolean isStaticgroup() {
        return staticgroup;
    }

    /**
     * Sets the value of the staticgroup property.
     * 
     */
    public void setStaticgroup(boolean value) {
        this.staticgroup = value;
    }

}
