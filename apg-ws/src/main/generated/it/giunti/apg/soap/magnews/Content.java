
package it.giunti.apg.soap.magnews;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for content complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="content">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="baseUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="binaryPayload" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="binaryPayloadEncoding" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contentType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="designerType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idcampaign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idnewsletter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idparent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idwebsite" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="magnewsType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tags" type="{http://webservices.magnews/}fieldValue" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="values" type="{http://webservices.magnews/}fieldValue" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "content", propOrder = {
    "baseUrl",
    "binaryPayload",
    "binaryPayloadEncoding",
    "contentType",
    "designerType",
    "idcampaign",
    "idnewsletter",
    "idparent",
    "idwebsite",
    "magnewsType",
    "name",
    "tags",
    "text",
    "values"
})
public class Content {

    protected String baseUrl;
    protected byte[] binaryPayload;
    protected String binaryPayloadEncoding;
    protected String contentType;
    protected String designerType;
    protected String idcampaign;
    protected String idnewsletter;
    protected String idparent;
    protected String idwebsite;
    protected String magnewsType;
    protected String name;
    @XmlElement(nillable = true)
    protected List<FieldValue> tags;
    protected String text;
    @XmlElement(nillable = true)
    protected List<FieldValue> values;

    /**
     * Gets the value of the baseUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Sets the value of the baseUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseUrl(String value) {
        this.baseUrl = value;
    }

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
     * Gets the value of the binaryPayloadEncoding property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBinaryPayloadEncoding() {
        return binaryPayloadEncoding;
    }

    /**
     * Sets the value of the binaryPayloadEncoding property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBinaryPayloadEncoding(String value) {
        this.binaryPayloadEncoding = value;
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
     * Gets the value of the designerType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDesignerType() {
        return designerType;
    }

    /**
     * Sets the value of the designerType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDesignerType(String value) {
        this.designerType = value;
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
     * Gets the value of the idparent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdparent() {
        return idparent;
    }

    /**
     * Sets the value of the idparent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdparent(String value) {
        this.idparent = value;
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
     * Gets the value of the magnewsType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMagnewsType() {
        return magnewsType;
    }

    /**
     * Sets the value of the magnewsType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMagnewsType(String value) {
        this.magnewsType = value;
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
     * Gets the value of the tags property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tags property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTags().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FieldValue }
     * 
     * 
     */
    public List<FieldValue> getTags() {
        if (tags == null) {
            tags = new ArrayList<FieldValue>();
        }
        return this.tags;
    }

    /**
     * Gets the value of the text property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setText(String value) {
        this.text = value;
    }

    /**
     * Gets the value of the values property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the values property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FieldValue }
     * 
     * 
     */
    public List<FieldValue> getValues() {
        if (values == null) {
            values = new ArrayList<FieldValue>();
        }
        return this.values;
    }

}
