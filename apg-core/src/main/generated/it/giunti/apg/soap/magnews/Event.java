
package it.giunti.apg.soap.magnews;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for event complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="event">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eventtype" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idcontent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tsevent" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "event", propOrder = {
    "eventtype",
    "idcontent",
    "tsevent"
})
public class Event {

    protected String eventtype;
    protected String idcontent;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar tsevent;

    /**
     * Gets the value of the eventtype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventtype() {
        return eventtype;
    }

    /**
     * Sets the value of the eventtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventtype(String value) {
        this.eventtype = value;
    }

    /**
     * Gets the value of the idcontent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdcontent() {
        return idcontent;
    }

    /**
     * Sets the value of the idcontent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdcontent(String value) {
        this.idcontent = value;
    }

    /**
     * Gets the value of the tsevent property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTsevent() {
        return tsevent;
    }

    /**
     * Sets the value of the tsevent property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTsevent(XMLGregorianCalendar value) {
        this.tsevent = value;
    }

}
