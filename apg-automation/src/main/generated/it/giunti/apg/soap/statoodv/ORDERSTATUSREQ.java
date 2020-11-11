
package it.giunti.apg.soap.statoodv;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * IF_002_005 – Sales Order Status
 * 
 * <p>Java class for ORDER_STATUS_REQ complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ORDER_STATUS_REQ"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RECORDSET"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="BSTKD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
@XmlType(name = "ORDER_STATUS_REQ", propOrder = {
    "recordset"
})
public class ORDERSTATUSREQ {

    @XmlElement(name = "RECORDSET", required = true)
    protected ORDERSTATUSREQ.RECORDSET recordset;

    /**
     * Gets the value of the recordset property.
     * 
     * @return
     *     possible object is
     *     {@link ORDERSTATUSREQ.RECORDSET }
     *     
     */
    public ORDERSTATUSREQ.RECORDSET getRECORDSET() {
        return recordset;
    }

    /**
     * Sets the value of the recordset property.
     * 
     * @param value
     *     allowed object is
     *     {@link ORDERSTATUSREQ.RECORDSET }
     *     
     */
    public void setRECORDSET(ORDERSTATUSREQ.RECORDSET value) {
        this.recordset = value;
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
        "bstkd"
    })
    public static class RECORDSET {

        @XmlElement(name = "BSTKD")
        protected String bstkd;

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

    }

}
