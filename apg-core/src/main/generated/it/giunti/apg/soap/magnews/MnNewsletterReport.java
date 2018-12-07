
package it.giunti.apg.soap.magnews;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mnNewsletterReport complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnNewsletterReport">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="countBounced" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="countClicked" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="countConverted" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="countOpened" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="countReceived" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="countSent" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="countSmsBounced" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="countSmsReceived" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="countSmsSent" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="idCampaign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idNewsletter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="totalClicks" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="totalConversions" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="totalHardBounces" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="totalOpens" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="totalSoftBounces" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="uniqueClicks" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="usersClicked" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="usersConverted" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="usersOpens" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="usersReceived" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="usersSent" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="contents" type="{http://webservices.magnews/}mnContentReportDetail" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="usersUnsubscribed" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="usersComplainted" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="usersForwarded" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnNewsletterReport", propOrder = {
    "countBounced",
    "countClicked",
    "countConverted",
    "countOpened",
    "countReceived",
    "countSent",
    "countSmsBounced",
    "countSmsReceived",
    "countSmsSent",
    "idCampaign",
    "idNewsletter",
    "name",
    "subject",
    "totalClicks",
    "totalConversions",
    "totalHardBounces",
    "totalOpens",
    "totalSoftBounces",
    "uniqueClicks",
    "usersClicked",
    "usersConverted",
    "usersOpens",
    "usersReceived",
    "usersSent",
    "contents",
    "usersUnsubscribed",
    "usersComplainted",
    "usersForwarded"
})
public class MnNewsletterReport {

    protected int countBounced;
    protected int countClicked;
    protected int countConverted;
    protected int countOpened;
    protected int countReceived;
    protected int countSent;
    protected int countSmsBounced;
    protected int countSmsReceived;
    protected int countSmsSent;
    protected String idCampaign;
    protected String idNewsletter;
    protected String name;
    protected String subject;
    protected int totalClicks;
    protected int totalConversions;
    protected int totalHardBounces;
    protected int totalOpens;
    protected int totalSoftBounces;
    protected int uniqueClicks;
    protected int usersClicked;
    protected int usersConverted;
    protected int usersOpens;
    protected int usersReceived;
    protected int usersSent;
    @XmlElement(nillable = true)
    protected List<MnContentReportDetail> contents;
    protected int usersUnsubscribed;
    protected int usersComplainted;
    protected int usersForwarded;

    /**
     * Gets the value of the countBounced property.
     * 
     */
    public int getCountBounced() {
        return countBounced;
    }

    /**
     * Sets the value of the countBounced property.
     * 
     */
    public void setCountBounced(int value) {
        this.countBounced = value;
    }

    /**
     * Gets the value of the countClicked property.
     * 
     */
    public int getCountClicked() {
        return countClicked;
    }

    /**
     * Sets the value of the countClicked property.
     * 
     */
    public void setCountClicked(int value) {
        this.countClicked = value;
    }

    /**
     * Gets the value of the countConverted property.
     * 
     */
    public int getCountConverted() {
        return countConverted;
    }

    /**
     * Sets the value of the countConverted property.
     * 
     */
    public void setCountConverted(int value) {
        this.countConverted = value;
    }

    /**
     * Gets the value of the countOpened property.
     * 
     */
    public int getCountOpened() {
        return countOpened;
    }

    /**
     * Sets the value of the countOpened property.
     * 
     */
    public void setCountOpened(int value) {
        this.countOpened = value;
    }

    /**
     * Gets the value of the countReceived property.
     * 
     */
    public int getCountReceived() {
        return countReceived;
    }

    /**
     * Sets the value of the countReceived property.
     * 
     */
    public void setCountReceived(int value) {
        this.countReceived = value;
    }

    /**
     * Gets the value of the countSent property.
     * 
     */
    public int getCountSent() {
        return countSent;
    }

    /**
     * Sets the value of the countSent property.
     * 
     */
    public void setCountSent(int value) {
        this.countSent = value;
    }

    /**
     * Gets the value of the countSmsBounced property.
     * 
     */
    public int getCountSmsBounced() {
        return countSmsBounced;
    }

    /**
     * Sets the value of the countSmsBounced property.
     * 
     */
    public void setCountSmsBounced(int value) {
        this.countSmsBounced = value;
    }

    /**
     * Gets the value of the countSmsReceived property.
     * 
     */
    public int getCountSmsReceived() {
        return countSmsReceived;
    }

    /**
     * Sets the value of the countSmsReceived property.
     * 
     */
    public void setCountSmsReceived(int value) {
        this.countSmsReceived = value;
    }

    /**
     * Gets the value of the countSmsSent property.
     * 
     */
    public int getCountSmsSent() {
        return countSmsSent;
    }

    /**
     * Sets the value of the countSmsSent property.
     * 
     */
    public void setCountSmsSent(int value) {
        this.countSmsSent = value;
    }

    /**
     * Gets the value of the idCampaign property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdCampaign() {
        return idCampaign;
    }

    /**
     * Sets the value of the idCampaign property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdCampaign(String value) {
        this.idCampaign = value;
    }

    /**
     * Gets the value of the idNewsletter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdNewsletter() {
        return idNewsletter;
    }

    /**
     * Sets the value of the idNewsletter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdNewsletter(String value) {
        this.idNewsletter = value;
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
     * Gets the value of the subject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the value of the subject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubject(String value) {
        this.subject = value;
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
     * Gets the value of the totalHardBounces property.
     * 
     */
    public int getTotalHardBounces() {
        return totalHardBounces;
    }

    /**
     * Sets the value of the totalHardBounces property.
     * 
     */
    public void setTotalHardBounces(int value) {
        this.totalHardBounces = value;
    }

    /**
     * Gets the value of the totalOpens property.
     * 
     */
    public int getTotalOpens() {
        return totalOpens;
    }

    /**
     * Sets the value of the totalOpens property.
     * 
     */
    public void setTotalOpens(int value) {
        this.totalOpens = value;
    }

    /**
     * Gets the value of the totalSoftBounces property.
     * 
     */
    public int getTotalSoftBounces() {
        return totalSoftBounces;
    }

    /**
     * Sets the value of the totalSoftBounces property.
     * 
     */
    public void setTotalSoftBounces(int value) {
        this.totalSoftBounces = value;
    }

    /**
     * Gets the value of the uniqueClicks property.
     * 
     */
    public int getUniqueClicks() {
        return uniqueClicks;
    }

    /**
     * Sets the value of the uniqueClicks property.
     * 
     */
    public void setUniqueClicks(int value) {
        this.uniqueClicks = value;
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

    /**
     * Gets the value of the usersOpens property.
     * 
     */
    public int getUsersOpens() {
        return usersOpens;
    }

    /**
     * Sets the value of the usersOpens property.
     * 
     */
    public void setUsersOpens(int value) {
        this.usersOpens = value;
    }

    /**
     * Gets the value of the usersReceived property.
     * 
     */
    public int getUsersReceived() {
        return usersReceived;
    }

    /**
     * Sets the value of the usersReceived property.
     * 
     */
    public void setUsersReceived(int value) {
        this.usersReceived = value;
    }

    /**
     * Gets the value of the usersSent property.
     * 
     */
    public int getUsersSent() {
        return usersSent;
    }

    /**
     * Sets the value of the usersSent property.
     * 
     */
    public void setUsersSent(int value) {
        this.usersSent = value;
    }

    /**
     * Gets the value of the contents property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contents property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContents().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MnContentReportDetail }
     * 
     * 
     */
    public List<MnContentReportDetail> getContents() {
        if (contents == null) {
            contents = new ArrayList<MnContentReportDetail>();
        }
        return this.contents;
    }

    /**
     * Gets the value of the usersUnsubscribed property.
     * 
     */
    public int getUsersUnsubscribed() {
        return usersUnsubscribed;
    }

    /**
     * Sets the value of the usersUnsubscribed property.
     * 
     */
    public void setUsersUnsubscribed(int value) {
        this.usersUnsubscribed = value;
    }

    /**
     * Gets the value of the usersComplainted property.
     * 
     */
    public int getUsersComplainted() {
        return usersComplainted;
    }

    /**
     * Sets the value of the usersComplainted property.
     * 
     */
    public void setUsersComplainted(int value) {
        this.usersComplainted = value;
    }

    /**
     * Gets the value of the usersForwarded property.
     * 
     */
    public int getUsersForwarded() {
        return usersForwarded;
    }

    /**
     * Sets the value of the usersForwarded property.
     * 
     */
    public void setUsersForwarded(int value) {
        this.usersForwarded = value;
    }

}
