
package it.giunti.apg.soap.creaodv;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ORDER_RSP complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ORDER_RSP"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RECORDSET" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="BSTKD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                   &lt;element name="ERRORE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                   &lt;element name="TESTO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
@XmlType(name = "ORDER_RSP", propOrder = {
    "recordset"
})
public class ORDERRSP {

    @XmlElement(name = "RECORDSET")
    protected List<ORDERRSP.RECORDSET> recordset;

    /**
     * Gets the value of the recordset property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the recordset property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRECORDSET().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ORDERRSP.RECORDSET }
     * 
     * 
     */
    public List<ORDERRSP.RECORDSET> getRECORDSET() {
        if (recordset == null) {
            recordset = new ArrayList<ORDERRSP.RECORDSET>();
        }
        return this.recordset;
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
     *         &lt;element name="BSTKD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *         &lt;element name="ERRORE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *         &lt;element name="TESTO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
        "bstkd",
        "errore",
        "testo"
    })
    public static class RECORDSET {

        @XmlElement(name = "BSTKD")
        protected String bstkd;
        @XmlElement(name = "ERRORE")
        protected String errore;
        @XmlElement(name = "TESTO")
        protected String testo;

        /**
         * Gets the value of the bstkd property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBSTKD() {
            return bstkd;
        }

        /**
         * Sets the value of the bstkd property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBSTKD(String value) {
            this.bstkd = value;
        }

        /**
         * Gets the value of the errore property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getERRORE() {
            return errore;
        }

        /**
         * Sets the value of the errore property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setERRORE(String value) {
            this.errore = value;
        }

        /**
         * Gets the value of the testo property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTESTO() {
            return testo;
        }

        /**
         * Sets the value of the testo property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTESTO(String value) {
            this.testo = value;
        }

    }

}
