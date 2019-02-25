
package it.giunti.apg.soap.magnews;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for media complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="media">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="binaryPayload" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="contentType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="height" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="idcampaign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idcontent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idmedia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idnewsletter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idwebsite" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="width" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "media", propOrder = {
    "binaryPayload",
    "contentType",
    "description",
    "height",
    "idcampaign",
    "idcontent",
    "idmedia",
    "idnewsletter",
    "idwebsite",
    "name",
    "title",
    "width"
})
public class Media {

    protected byte[] binaryPayload;
    protected String contentType;
    protected String description;
    protected int height;
    protected String idcampaign;
    protected String idcontent;
    protected String idmedia;
    protected String idnewsletter;
    protected String idwebsite;
    protected String name;
    protected String title;
    protected int width;

    /**
     * Gets the value of the binaryPayload property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getBinaryPayload() {
        return binaryPayload;
    }

    /**
     * Sets the value of the binaryPayload property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setBinaryPayload(byte[] value) {
        this.binaryPayload = value;
    }

    /**
     * Gets the value of the contentType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the value of the contentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContentType(String value) {
        this.contentType = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the height property.
     * 
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     */
    public void setHeight(int value) {
        this.height = value;
    }

    /**
     * Gets the value of the idcampaign property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdcampaign() {
        return idcampaign;
    }

    /**
     * Sets the value of the idcampaign property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdcampaign(String value) {
        this.idcampaign = value;
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
     * Gets the value of the idmedia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdmedia() {
        return idmedia;
    }

    /**
     * Sets the value of the idmedia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdmedia(String value) {
        this.idmedia = value;
    }

    /**
     * Gets the value of the idnewsletter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdnewsletter() {
        return idnewsletter;
    }

    /**
     * Sets the value of the idnewsletter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdnewsletter(String value) {
        this.idnewsletter = value;
    }

    /**
     * Gets the value of the idwebsite property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdwebsite() {
        return idwebsite;
    }

    /**
     * Sets the value of the idwebsite property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdwebsite(String value) {
        this.idwebsite = value;
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
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the width property.
     * 
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     */
    public void setWidth(int value) {
        this.width = value;
    }

}
