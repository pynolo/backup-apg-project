
package it.giunti.apg.soap.magnews;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for mnSendOptions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnSendOptions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="allowCellDuplicated" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="allowEmailDuplicated" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="allowFaxDuplicated" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="enableEmail" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="enableFax" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="enableSms" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="ondemand" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="requestedStartDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="trial" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnSendOptions", propOrder = {
    "allowCellDuplicated",
    "allowEmailDuplicated",
    "allowFaxDuplicated",
    "enableEmail",
    "enableFax",
    "enableSms",
    "ondemand",
    "requestedStartDate",
    "trial"
})
public class MnSendOptions {

    protected boolean allowCellDuplicated;
    protected boolean allowEmailDuplicated;
    protected boolean allowFaxDuplicated;
    protected boolean enableEmail;
    protected boolean enableFax;
    protected boolean enableSms;
    protected boolean ondemand;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar requestedStartDate;
    protected boolean trial;

    /**
     * Gets the value of the allowCellDuplicated property.
     * 
     */
    public boolean isAllowCellDuplicated() {
        return allowCellDuplicated;
    }

    /**
     * Sets the value of the allowCellDuplicated property.
     * 
     */
    public void setAllowCellDuplicated(boolean value) {
        this.allowCellDuplicated = value;
    }

    /**
     * Gets the value of the allowEmailDuplicated property.
     * 
     */
    public boolean isAllowEmailDuplicated() {
        return allowEmailDuplicated;
    }

    /**
     * Sets the value of the allowEmailDuplicated property.
     * 
     */
    public void setAllowEmailDuplicated(boolean value) {
        this.allowEmailDuplicated = value;
    }

    /**
     * Gets the value of the allowFaxDuplicated property.
     * 
     */
    public boolean isAllowFaxDuplicated() {
        return allowFaxDuplicated;
    }

    /**
     * Sets the value of the allowFaxDuplicated property.
     * 
     */
    public void setAllowFaxDuplicated(boolean value) {
        this.allowFaxDuplicated = value;
    }

    /**
     * Gets the value of the enableEmail property.
     * 
     */
    public boolean isEnableEmail() {
        return enableEmail;
    }

    /**
     * Sets the value of the enableEmail property.
     * 
     */
    public void setEnableEmail(boolean value) {
        this.enableEmail = value;
    }

    /**
     * Gets the value of the enableFax property.
     * 
     */
    public boolean isEnableFax() {
        return enableFax;
    }

    /**
     * Sets the value of the enableFax property.
     * 
     */
    public void setEnableFax(boolean value) {
        this.enableFax = value;
    }

    /**
     * Gets the value of the enableSms property.
     * 
     */
    public boolean isEnableSms() {
        return enableSms;
    }

    /**
     * Sets the value of the enableSms property.
     * 
     */
    public void setEnableSms(boolean value) {
        this.enableSms = value;
    }

    /**
     * Gets the value of the ondemand property.
     * 
     */
    public boolean isOndemand() {
        return ondemand;
    }

    /**
     * Sets the value of the ondemand property.
     * 
     */
    public void setOndemand(boolean value) {
        this.ondemand = value;
    }

    /**
     * Gets the value of the requestedStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRequestedStartDate() {
        return requestedStartDate;
    }

    /**
     * Sets the value of the requestedStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRequestedStartDate(XMLGregorianCalendar value) {
        this.requestedStartDate = value;
    }

    /**
     * Gets the value of the trial property.
     * 
     */
    public boolean isTrial() {
        return trial;
    }

    /**
     * Sets the value of the trial property.
     * 
     */
    public void setTrial(boolean value) {
        this.trial = value;
    }

}
