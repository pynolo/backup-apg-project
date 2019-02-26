
package it.giunti.apg.soap.magnews;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mnWebSite complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mnWebSite">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="homePageLink" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hostnameAliases" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="idWebsite" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="primaryHostName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mnWebSite", propOrder = {
    "homePageLink",
    "hostnameAliases",
    "idWebsite",
    "name",
    "primaryHostName"
})
public class MnWebSite {

    protected String homePageLink;
    @XmlElement(nillable = true)
    protected List<String> hostnameAliases;
    protected String idWebsite;
    protected String name;
    protected String primaryHostName;

    /**
     * Gets the value of the homePageLink property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHomePageLink() {
        return homePageLink;
    }

    /**
     * Sets the value of the homePageLink property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHomePageLink(String value) {
        this.homePageLink = value;
    }

    /**
     * Gets the value of the hostnameAliases property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hostnameAliases property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHostnameAliases().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getHostnameAliases() {
        if (hostnameAliases == null) {
            hostnameAliases = new ArrayList<String>();
        }
        return this.hostnameAliases;
    }

    /**
     * Gets the value of the idWebsite property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdWebsite() {
        return idWebsite;
    }

    /**
     * Sets the value of the idWebsite property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdWebsite(String value) {
        this.idWebsite = value;
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
     * Gets the value of the primaryHostName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrimaryHostName() {
        return primaryHostName;
    }

    /**
     * Sets the value of the primaryHostName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrimaryHostName(String value) {
        this.primaryHostName = value;
    }

}
