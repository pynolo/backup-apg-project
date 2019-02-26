
package it.giunti.apg.soap.magnews;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mnFormSubmissionResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnFormSubmissionResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="errors" type="{http://webservices.magnews/}mnError" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="idContact" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idPage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idSurveySession" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ok" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnFormSubmissionResult", propOrder = {
    "errors",
    "idContact",
    "idPage",
    "idSurveySession",
    "ok"
})
public class MnFormSubmissionResult {

    @XmlElement(nillable = true)
    protected List<MnError> errors;
    protected String idContact;
    protected String idPage;
    protected String idSurveySession;
    protected boolean ok;

    /**
     * Gets the value of the errors property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the errors property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getErrors().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MnError }
     * 
     * 
     */
    public List<MnError> getErrors() {
        if (errors == null) {
            errors = new ArrayList<MnError>();
        }
        return this.errors;
    }

    /**
     * Gets the value of the idContact property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdContact() {
        return idContact;
    }

    /**
     * Sets the value of the idContact property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdContact(String value) {
        this.idContact = value;
    }

    /**
     * Gets the value of the idPage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdPage() {
        return idPage;
    }

    /**
     * Sets the value of the idPage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdPage(String value) {
        this.idPage = value;
    }

    /**
     * Gets the value of the idSurveySession property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdSurveySession() {
        return idSurveySession;
    }

    /**
     * Sets the value of the idSurveySession property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdSurveySession(String value) {
        this.idSurveySession = value;
    }

    /**
     * Gets the value of the ok property.
     * 
     */
    public boolean isOk() {
        return ok;
    }

    /**
     * Sets the value of the ok property.
     * 
     */
    public void setOk(boolean value) {
        this.ok = value;
    }

}
