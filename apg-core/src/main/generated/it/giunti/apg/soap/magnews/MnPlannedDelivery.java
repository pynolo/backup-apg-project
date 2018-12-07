
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
 * <p>Java class for mnPlannedDelivery complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnPlannedDelivery">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idPlannedDelivery" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idNewsletter" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="targetSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="idGroup" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="referenceDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="targetActions" type="{http://webservices.magnews/}plannedDeliveryTargetAction" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="details" type="{http://webservices.magnews/}fieldValue" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnPlannedDelivery", propOrder = {
    "idPlannedDelivery",
    "name",
    "idNewsletter",
    "targetSize",
    "idGroup",
    "referenceDate",
    "targetActions",
    "details"
})
public class MnPlannedDelivery {

    protected int idPlannedDelivery;
    protected String name;
    protected int idNewsletter;
    protected int targetSize;
    protected int idGroup;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar referenceDate;
    @XmlElement(nillable = true)
    protected List<PlannedDeliveryTargetAction> targetActions;
    @XmlElement(nillable = true)
    protected List<FieldValue> details;

    /**
     * Gets the value of the idPlannedDelivery property.
     * 
     */
    public int getIdPlannedDelivery() {
        return idPlannedDelivery;
    }

    /**
     * Sets the value of the idPlannedDelivery property.
     * 
     */
    public void setIdPlannedDelivery(int value) {
        this.idPlannedDelivery = value;
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
     * Gets the value of the idNewsletter property.
     * 
     */
    public int getIdNewsletter() {
        return idNewsletter;
    }

    /**
     * Sets the value of the idNewsletter property.
     * 
     */
    public void setIdNewsletter(int value) {
        this.idNewsletter = value;
    }

    /**
     * Gets the value of the targetSize property.
     * 
     */
    public int getTargetSize() {
        return targetSize;
    }

    /**
     * Sets the value of the targetSize property.
     * 
     */
    public void setTargetSize(int value) {
        this.targetSize = value;
    }

    /**
     * Gets the value of the idGroup property.
     * 
     */
    public int getIdGroup() {
        return idGroup;
    }

    /**
     * Sets the value of the idGroup property.
     * 
     */
    public void setIdGroup(int value) {
        this.idGroup = value;
    }

    /**
     * Gets the value of the referenceDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReferenceDate() {
        return referenceDate;
    }

    /**
     * Sets the value of the referenceDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReferenceDate(XMLGregorianCalendar value) {
        this.referenceDate = value;
    }

    /**
     * Gets the value of the targetActions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the targetActions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTargetActions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PlannedDeliveryTargetAction }
     * 
     * 
     */
    public List<PlannedDeliveryTargetAction> getTargetActions() {
        if (targetActions == null) {
            targetActions = new ArrayList<PlannedDeliveryTargetAction>();
        }
        return this.targetActions;
    }

    /**
     * Gets the value of the details property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the details property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDetails().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FieldValue }
     * 
     * 
     */
    public List<FieldValue> getDetails() {
        if (details == null) {
            details = new ArrayList<FieldValue>();
        }
        return this.details;
    }

}
