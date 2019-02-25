
package it.giunti.apg.soap.magnews;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mnContentReportDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnContentReportDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idcontent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="href" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reportLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="totalClicks" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="totalConversions" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="usersClicked" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="usersConverted" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnContentReportDetail", propOrder = {
    "idcontent",
    "href",
    "reportLabel",
    "totalClicks",
    "totalConversions",
    "usersClicked",
    "usersConverted"
})
public class MnContentReportDetail {

    protected String idcontent;
    protected String href;
    protected String reportLabel;
    protected int totalClicks;
    protected int totalConversions;
    protected int usersClicked;
    protected int usersConverted;

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
     * Gets the value of the href property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * Gets the value of the reportLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReportLabel() {
        return reportLabel;
    }

    /**
     * Sets the value of the reportLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReportLabel(String value) {
        this.reportLabel = value;
    }

    /**
     * Gets the value of the totalClicks property.
     * 
     */
    public int getTotalClicks() {
        return totalClicks;
    }

    /**
     * Sets the value of the totalClicks property.
     * 
     */
    public void setTotalClicks(int value) {
        this.totalClicks = value;
    }

    /**
     * Gets the value of the totalConversions property.
     * 
     */
    public int getTotalConversions() {
        return totalConversions;
    }

    /**
     * Sets the value of the totalConversions property.
     * 
     */
    public void setTotalConversions(int value) {
        this.totalConversions = value;
    }

    /**
     * Gets the value of the usersClicked property.
     * 
     */
    public int getUsersClicked() {
        return usersClicked;
    }

    /**
     * Sets the value of the usersClicked property.
     * 
     */
    public void setUsersClicked(int value) {
        this.usersClicked = value;
    }

    /**
     * Gets the value of the usersConverted property.
     * 
     */
    public int getUsersConverted() {
        return usersConverted;
    }

    /**
     * Sets the value of the usersConverted property.
     * 
     */
    public void setUsersConverted(int value) {
        this.usersConverted = value;
    }

}
