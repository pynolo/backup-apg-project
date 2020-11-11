
package it.giunti.apg.soap.fattele;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * IF_017_002 - Electronic Invoicing
 * 
 * <p>Java class for INVOICE_E_RSP complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="INVOICE_E_RSP"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ZFATT_EL_ERR_S" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="LINE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                   &lt;element name="TABNAME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                   &lt;element name="FIELDNAME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                   &lt;element name="MESSAGE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "INVOICE_E_RSP", propOrder = {
    "zfattelerrs"
})
public class INVOICEERSP {

    @XmlElement(name = "ZFATT_EL_ERR_S")
    protected List<INVOICEERSP.ZFATTELERRS> zfattelerrs;

    /**
     * Gets the value of the zfattelerrs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the zfattelerrs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getZFATTELERRS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link INVOICEERSP.ZFATTELERRS }
     * 
     * 
     */
    public List<INVOICEERSP.ZFATTELERRS> getZFATTELERRS() {
        if (zfattelerrs == null) {
            zfattelerrs = new ArrayList<INVOICEERSP.ZFATTELERRS>();
        }
        return this.zfattelerrs;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="LINE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *         &lt;element name="TABNAME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *         &lt;element name="FIELDNAME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *         &lt;element name="MESSAGE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "line",
        "tabname",
        "fieldname",
        "message"
    })
    public static class ZFATTELERRS {

        @XmlElement(name = "LINE")
        protected String line;
        @XmlElement(name = "TABNAME")
        protected String tabname;
        @XmlElement(name = "FIELDNAME")
        protected String fieldname;
        @XmlElement(name = "MESSAGE")
        protected String message;

        /**
         * Gets the value of the line property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLINE() {
            return line;
        }

        /**
         * Sets the value of the line property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLINE(String value) {
            this.line = value;
        }

        /**
         * Gets the value of the tabname property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTABNAME() {
            return tabname;
        }

        /**
         * Sets the value of the tabname property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTABNAME(String value) {
            this.tabname = value;
        }

        /**
         * Gets the value of the fieldname property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFIELDNAME() {
            return fieldname;
        }

        /**
         * Sets the value of the fieldname property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFIELDNAME(String value) {
            this.fieldname = value;
        }

        /**
         * Gets the value of the message property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMESSAGE() {
            return message;
        }

        /**
         * Sets the value of the message property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMESSAGE(String value) {
            this.message = value;
        }

    }

}
