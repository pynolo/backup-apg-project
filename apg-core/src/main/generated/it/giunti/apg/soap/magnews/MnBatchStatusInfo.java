
package it.giunti.apg.soap.magnews;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mnBatchStatusInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnBatchStatusInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="batchId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="info" type="{http://webservices.magnews/}fieldValue" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reportRowsSetId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="totalRecordCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="userRequestedStop" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnBatchStatusInfo", propOrder = {
    "batchId",
    "info",
    "name",
    "reportRowsSetId",
    "status",
    "totalRecordCount",
    "userRequestedStop"
})
public class MnBatchStatusInfo {

    protected String batchId;
    @XmlElement(nillable = true)
    protected List<FieldValue> info;
    protected String name;
    protected String reportRowsSetId;
    protected String status;
    protected int totalRecordCount;
    protected boolean userRequestedStop;

    /**
     * Gets the value of the batchId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBatchId() {
        return batchId;
    }

    /**
     * Sets the value of the batchId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatchId(String value) {
        this.batchId = value;
    }

    /**
     * Gets the value of the info property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the info property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FieldValue }
     * 
     * 
     */
    public List<FieldValue> getInfo() {
        if (info == null) {
            info = new ArrayList<FieldValue>();
        }
        return this.info;
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
     * Gets the value of the reportRowsSetId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReportRowsSetId() {
        return reportRowsSetId;
    }

    /**
     * Sets the value of the reportRowsSetId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReportRowsSetId(String value) {
        this.reportRowsSetId = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the totalRecordCount property.
     * 
     */
    public int getTotalRecordCount() {
        return totalRecordCount;
    }

    /**
     * Sets the value of the totalRecordCount property.
     * 
     */
    public void setTotalRecordCount(int value) {
        this.totalRecordCount = value;
    }

    /**
     * Gets the value of the userRequestedStop property.
     * 
     */
    public boolean isUserRequestedStop() {
        return userRequestedStop;
    }

    /**
     * Sets the value of the userRequestedStop property.
     * 
     */
    public void setUserRequestedStop(boolean value) {
        this.userRequestedStop = value;
    }

}
