
package it.giunti.apg.soap.magnews;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mnSendEmailResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnSendEmailResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="emailaddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="emailsent" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="idsimplemessage" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnSendEmailResult", propOrder = {
    "emailaddress",
    "emailsent",
    "idsimplemessage"
})
public class MnSendEmailResult {

    protected String emailaddress;
    protected boolean emailsent;
    protected int idsimplemessage;

    /**
     * Gets the value of the emailaddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailaddress() {
        return emailaddress;
    }

    /**
     * Sets the value of the emailaddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailaddress(String value) {
        this.emailaddress = value;
    }

    /**
     * Gets the value of the emailsent property.
     * 
     */
    public boolean isEmailsent() {
        return emailsent;
    }

    /**
     * Sets the value of the emailsent property.
     * 
     */
    public void setEmailsent(boolean value) {
        this.emailsent = value;
    }

    /**
     * Gets the value of the idsimplemessage property.
     * 
     */
    public int getIdsimplemessage() {
        return idsimplemessage;
    }

    /**
     * Sets the value of the idsimplemessage property.
     * 
     */
    public void setIdsimplemessage(int value) {
        this.idsimplemessage = value;
    }

}
