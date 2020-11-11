
package it.giunti.apg.soap.fattele;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * IF_017_002 - Electronic Invoicing
 * 
 * <p>Java class for INVOICE_E_REQ complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="INVOICE_E_REQ"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FATTELE" maxOccurs="unbounded"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="ZFATT_EL_HEAD"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="ZFATT_EL_HEAD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="BUKRS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="SEQUENZIALE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="BELNR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="GJAHR" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *                             &lt;element name="VBTYP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="WAERS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="BLDAT" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *                             &lt;element name="COUNTRY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="MOD_PAG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="DEST_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="DEST_PEC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="ABLAD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="KUNRG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="KUNRG_STCEG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="KUNRG_STCD1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="KUNRG_NAME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="KUNRG_STREET" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="KUNRG_HOUSE_NUM1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="KUNRG_POST_CODE1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="KUNRG_CITY1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="KUNRG_REGION" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="KUNRG_COUNTRY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="BBBNR" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *                             &lt;element name="FISKN_STREET" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="FISKN_HOUSE_NUM1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="FISKN_POST_CODE1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="FISKN_CITY1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="FISKN_REGION" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="FISKN_COUNTRY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="FISKN_NAME1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="FISKN_STCEG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="TOTALE_DOC" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *                             &lt;element name="CAUSALE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="TOT_IMP" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *                             &lt;element name="ZFBDT" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *                             &lt;element name="SCONTO_H" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *                             &lt;element name="SCONTO_VAL_H" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *                             &lt;element name="TRASP_H" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *                             &lt;element name="ELABORATO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="ZFATT_EL_ITEM" maxOccurs="unbounded"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="BUKRS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="SEQUENZIALE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="BELNR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="POSNR" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *                             &lt;element name="GJAHR" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *                             &lt;element name="ORD_ID_DOCUME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="ORD_DATA" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *                             &lt;element name="ORD_NUM_ITEM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="ORD_COMMESSA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="ORD_CUP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="ORD_CIG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="CONT_ID_DOCUME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="CONT_DATA" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *                             &lt;element name="CONT_NUM_ITEM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="CONT_COMMESSA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="CONT_CUP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="CONT_CIG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="CONV_ID_DOCUME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="CONV_DATA" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *                             &lt;element name="CONV_NUM_ITEM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="CONV_COMMESSA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="CONV_CUP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="CONV_CIG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="RIC_ID_DOCUME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="RIC_DATA" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *                             &lt;element name="RIC_NUM_ITEM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="RIC_COMMESSA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="RIC_CUP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="RIC_CIG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="DDT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="DDT_ERDAT" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *                             &lt;element name="EAN11" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="ORD_COD_TIPO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="ORD_COD_VALORE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="TESTO_VBBP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="FKIMG" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *                             &lt;element name="ORD_INIZIO_PRES" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *                             &lt;element name="ORD_FINE_PRES" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *                             &lt;element name="KZWI1" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *                             &lt;element name="SCONTO" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *                             &lt;element name="SCONTO_VAL" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *                             &lt;element name="SC_PLUS" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *                             &lt;element name="TRASP" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *                             &lt;element name="COD_IVA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="ALIQIVA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="IMP_IVA" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *                             &lt;element name="IMPOSTA_IVA" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
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
@XmlType(name = "INVOICE_E_REQ", propOrder = {
    "fattele"
})
public class INVOICEEREQ {

    @XmlElement(name = "FATTELE", required = true)
    protected List<INVOICEEREQ.FATTELE> fattele;

    /**
     * Gets the value of the fattele property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fattele property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFATTELE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link INVOICEEREQ.FATTELE }
     * 
     * 
     */
    public List<INVOICEEREQ.FATTELE> getFATTELE() {
        if (fattele == null) {
            fattele = new ArrayList<INVOICEEREQ.FATTELE>();
        }
        return this.fattele;
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
     *         &lt;element name="ZFATT_EL_HEAD"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="ZFATT_EL_HEAD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="BUKRS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="SEQUENZIALE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="BELNR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="GJAHR" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
     *                   &lt;element name="VBTYP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="WAERS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="BLDAT" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
     *                   &lt;element name="COUNTRY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="MOD_PAG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="DEST_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="DEST_PEC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="ABLAD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="KUNRG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="KUNRG_STCEG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="KUNRG_STCD1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="KUNRG_NAME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="KUNRG_STREET" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="KUNRG_HOUSE_NUM1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="KUNRG_POST_CODE1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="KUNRG_CITY1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="KUNRG_REGION" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="KUNRG_COUNTRY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="BBBNR" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
     *                   &lt;element name="FISKN_STREET" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="FISKN_HOUSE_NUM1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="FISKN_POST_CODE1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="FISKN_CITY1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="FISKN_REGION" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="FISKN_COUNTRY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="FISKN_NAME1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="FISKN_STCEG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="TOTALE_DOC" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
     *                   &lt;element name="CAUSALE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="TOT_IMP" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
     *                   &lt;element name="ZFBDT" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
     *                   &lt;element name="SCONTO_H" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
     *                   &lt;element name="SCONTO_VAL_H" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
     *                   &lt;element name="TRASP_H" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
     *                   &lt;element name="ELABORATO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="ZFATT_EL_ITEM" maxOccurs="unbounded"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="BUKRS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="SEQUENZIALE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="BELNR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="POSNR" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
     *                   &lt;element name="GJAHR" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
     *                   &lt;element name="ORD_ID_DOCUME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="ORD_DATA" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
     *                   &lt;element name="ORD_NUM_ITEM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="ORD_COMMESSA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="ORD_CUP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="ORD_CIG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="CONT_ID_DOCUME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="CONT_DATA" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
     *                   &lt;element name="CONT_NUM_ITEM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="CONT_COMMESSA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="CONT_CUP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="CONT_CIG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="CONV_ID_DOCUME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="CONV_DATA" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
     *                   &lt;element name="CONV_NUM_ITEM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="CONV_COMMESSA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="CONV_CUP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="CONV_CIG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="RIC_ID_DOCUME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="RIC_DATA" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
     *                   &lt;element name="RIC_NUM_ITEM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="RIC_COMMESSA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="RIC_CUP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="RIC_CIG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="DDT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="DDT_ERDAT" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
     *                   &lt;element name="EAN11" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="ORD_COD_TIPO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="ORD_COD_VALORE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="TESTO_VBBP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="FKIMG" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
     *                   &lt;element name="ORD_INIZIO_PRES" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
     *                   &lt;element name="ORD_FINE_PRES" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
     *                   &lt;element name="KZWI1" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
     *                   &lt;element name="SCONTO" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
     *                   &lt;element name="SCONTO_VAL" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
     *                   &lt;element name="SC_PLUS" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
     *                   &lt;element name="TRASP" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
     *                   &lt;element name="COD_IVA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="ALIQIVA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="IMP_IVA" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
     *                   &lt;element name="IMPOSTA_IVA" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
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
    @XmlType(name = "", propOrder = {
        "zfattelhead",
        "zfattelitem"
    })
    public static class FATTELE {

        @XmlElement(name = "ZFATT_EL_HEAD", required = true)
        protected INVOICEEREQ.FATTELE.ZFATTELHEAD zfattelhead;
        @XmlElement(name = "ZFATT_EL_ITEM", required = true)
        protected List<INVOICEEREQ.FATTELE.ZFATTELITEM> zfattelitem;

        /**
         * Gets the value of the zfattelhead property.
         * 
         * @return
         *     possible object is
         *     {@link INVOICEEREQ.FATTELE.ZFATTELHEAD }
         *     
         */
        public INVOICEEREQ.FATTELE.ZFATTELHEAD getZFATTELHEAD() {
            return zfattelhead;
        }

        /**
         * Sets the value of the zfattelhead property.
         * 
         * @param value
         *     allowed object is
         *     {@link INVOICEEREQ.FATTELE.ZFATTELHEAD }
         *     
         */
        public void setZFATTELHEAD(INVOICEEREQ.FATTELE.ZFATTELHEAD value) {
            this.zfattelhead = value;
        }

        /**
         * Gets the value of the zfattelitem property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the zfattelitem property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getZFATTELITEM().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link INVOICEEREQ.FATTELE.ZFATTELITEM }
         * 
         * 
         */
        public List<INVOICEEREQ.FATTELE.ZFATTELITEM> getZFATTELITEM() {
            if (zfattelitem == null) {
                zfattelitem = new ArrayList<INVOICEEREQ.FATTELE.ZFATTELITEM>();
            }
            return this.zfattelitem;
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
         *         &lt;element name="ZFATT_EL_HEAD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="BUKRS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="SEQUENZIALE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="BELNR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="GJAHR" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
         *         &lt;element name="VBTYP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="WAERS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="BLDAT" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
         *         &lt;element name="COUNTRY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="MOD_PAG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="DEST_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="DEST_PEC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="ABLAD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="KUNRG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="KUNRG_STCEG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="KUNRG_STCD1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="KUNRG_NAME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="KUNRG_STREET" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="KUNRG_HOUSE_NUM1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="KUNRG_POST_CODE1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="KUNRG_CITY1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="KUNRG_REGION" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="KUNRG_COUNTRY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="BBBNR" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
         *         &lt;element name="FISKN_STREET" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="FISKN_HOUSE_NUM1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="FISKN_POST_CODE1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="FISKN_CITY1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="FISKN_REGION" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="FISKN_COUNTRY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="FISKN_NAME1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="FISKN_STCEG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="TOTALE_DOC" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
         *         &lt;element name="CAUSALE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="TOT_IMP" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
         *         &lt;element name="ZFBDT" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
         *         &lt;element name="SCONTO_H" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
         *         &lt;element name="SCONTO_VAL_H" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
         *         &lt;element name="TRASP_H" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
         *         &lt;element name="ELABORATO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
            "zfattelhead",
            "bukrs",
            "sequenziale",
            "belnr",
            "gjahr",
            "vbtyp",
            "waers",
            "bldat",
            "country",
            "modpag",
            "destcode",
            "destpec",
            "ablad",
            "kunrg",
            "kunrgstceg",
            "kunrgstcd1",
            "kunrgname",
            "kunrgstreet",
            "kunrghousenum1",
            "kunrgpostcode1",
            "kunrgcity1",
            "kunrgregion",
            "kunrgcountry",
            "bbbnr",
            "fisknstreet",
            "fisknhousenum1",
            "fisknpostcode1",
            "fiskncity1",
            "fisknregion",
            "fiskncountry",
            "fisknname1",
            "fisknstceg",
            "totaledoc",
            "causale",
            "totimp",
            "zfbdt",
            "scontoh",
            "scontovalh",
            "trasph",
            "elaborato"
        })
        public static class ZFATTELHEAD {

            @XmlElement(name = "ZFATT_EL_HEAD")
            protected String zfattelhead;
            @XmlElement(name = "BUKRS")
            protected String bukrs;
            @XmlElement(name = "SEQUENZIALE")
            protected String sequenziale;
            @XmlElement(name = "BELNR")
            protected String belnr;
            @XmlElement(name = "GJAHR")
            protected BigInteger gjahr;
            @XmlElement(name = "VBTYP")
            protected String vbtyp;
            @XmlElement(name = "WAERS")
            protected String waers;
            @XmlElement(name = "BLDAT")
            @XmlSchemaType(name = "date")
            protected XMLGregorianCalendar bldat;
            @XmlElement(name = "COUNTRY")
            protected String country;
            @XmlElement(name = "MOD_PAG")
            protected String modpag;
            @XmlElement(name = "DEST_CODE")
            protected String destcode;
            @XmlElement(name = "DEST_PEC")
            protected String destpec;
            @XmlElement(name = "ABLAD")
            protected String ablad;
            @XmlElement(name = "KUNRG")
            protected String kunrg;
            @XmlElement(name = "KUNRG_STCEG")
            protected String kunrgstceg;
            @XmlElement(name = "KUNRG_STCD1")
            protected String kunrgstcd1;
            @XmlElement(name = "KUNRG_NAME")
            protected String kunrgname;
            @XmlElement(name = "KUNRG_STREET")
            protected String kunrgstreet;
            @XmlElement(name = "KUNRG_HOUSE_NUM1")
            protected String kunrghousenum1;
            @XmlElement(name = "KUNRG_POST_CODE1")
            protected String kunrgpostcode1;
            @XmlElement(name = "KUNRG_CITY1")
            protected String kunrgcity1;
            @XmlElement(name = "KUNRG_REGION")
            protected String kunrgregion;
            @XmlElement(name = "KUNRG_COUNTRY")
            protected String kunrgcountry;
            @XmlElement(name = "BBBNR")
            protected BigInteger bbbnr;
            @XmlElement(name = "FISKN_STREET")
            protected String fisknstreet;
            @XmlElement(name = "FISKN_HOUSE_NUM1")
            protected String fisknhousenum1;
            @XmlElement(name = "FISKN_POST_CODE1")
            protected String fisknpostcode1;
            @XmlElement(name = "FISKN_CITY1")
            protected String fiskncity1;
            @XmlElement(name = "FISKN_REGION")
            protected String fisknregion;
            @XmlElement(name = "FISKN_COUNTRY")
            protected String fiskncountry;
            @XmlElement(name = "FISKN_NAME1")
            protected String fisknname1;
            @XmlElement(name = "FISKN_STCEG")
            protected String fisknstceg;
            @XmlElement(name = "TOTALE_DOC")
            protected BigDecimal totaledoc;
            @XmlElement(name = "CAUSALE")
            protected String causale;
            @XmlElement(name = "TOT_IMP")
            protected BigDecimal totimp;
            @XmlElement(name = "ZFBDT")
            @XmlSchemaType(name = "date")
            protected XMLGregorianCalendar zfbdt;
            @XmlElement(name = "SCONTO_H")
            protected BigDecimal scontoh;
            @XmlElement(name = "SCONTO_VAL_H")
            protected BigDecimal scontovalh;
            @XmlElement(name = "TRASP_H")
            protected BigDecimal trasph;
            @XmlElement(name = "ELABORATO")
            protected String elaborato;

            /**
             * Gets the value of the zfattelhead property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getZFATTELHEAD() {
                return zfattelhead;
            }

            /**
             * Sets the value of the zfattelhead property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setZFATTELHEAD(String value) {
                this.zfattelhead = value;
            }

            /**
             * Gets the value of the bukrs property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getBUKRS() {
                return bukrs;
            }

            /**
             * Sets the value of the bukrs property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setBUKRS(String value) {
                this.bukrs = value;
            }

            /**
             * Gets the value of the sequenziale property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSEQUENZIALE() {
                return sequenziale;
            }

            /**
             * Sets the value of the sequenziale property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSEQUENZIALE(String value) {
                this.sequenziale = value;
            }

            /**
             * Gets the value of the belnr property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getBELNR() {
                return belnr;
            }

            /**
             * Sets the value of the belnr property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setBELNR(String value) {
                this.belnr = value;
            }

            /**
             * Gets the value of the gjahr property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getGJAHR() {
                return gjahr;
            }

            /**
             * Sets the value of the gjahr property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setGJAHR(BigInteger value) {
                this.gjahr = value;
            }

            /**
             * Gets the value of the vbtyp property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getVBTYP() {
                return vbtyp;
            }

            /**
             * Sets the value of the vbtyp property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setVBTYP(String value) {
                this.vbtyp = value;
            }

            /**
             * Gets the value of the waers property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getWAERS() {
                return waers;
            }

            /**
             * Sets the value of the waers property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setWAERS(String value) {
                this.waers = value;
            }

            /**
             * Gets the value of the bldat property.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getBLDAT() {
                return bldat;
            }

            /**
             * Sets the value of the bldat property.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setBLDAT(XMLGregorianCalendar value) {
                this.bldat = value;
            }

            /**
             * Gets the value of the country property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCOUNTRY() {
                return country;
            }

            /**
             * Sets the value of the country property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCOUNTRY(String value) {
                this.country = value;
            }

            /**
             * Gets the value of the modpag property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMODPAG() {
                return modpag;
            }

            /**
             * Sets the value of the modpag property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setMODPAG(String value) {
                this.modpag = value;
            }

            /**
             * Gets the value of the destcode property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDESTCODE() {
                return destcode;
            }

            /**
             * Sets the value of the destcode property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDESTCODE(String value) {
                this.destcode = value;
            }

            /**
             * Gets the value of the destpec property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDESTPEC() {
                return destpec;
            }

            /**
             * Sets the value of the destpec property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDESTPEC(String value) {
                this.destpec = value;
            }

            /**
             * Gets the value of the ablad property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getABLAD() {
                return ablad;
            }

            /**
             * Sets the value of the ablad property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setABLAD(String value) {
                this.ablad = value;
            }

            /**
             * Gets the value of the kunrg property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKUNRG() {
                return kunrg;
            }

            /**
             * Sets the value of the kunrg property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKUNRG(String value) {
                this.kunrg = value;
            }

            /**
             * Gets the value of the kunrgstceg property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKUNRGSTCEG() {
                return kunrgstceg;
            }

            /**
             * Sets the value of the kunrgstceg property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKUNRGSTCEG(String value) {
                this.kunrgstceg = value;
            }

            /**
             * Gets the value of the kunrgstcd1 property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKUNRGSTCD1() {
                return kunrgstcd1;
            }

            /**
             * Sets the value of the kunrgstcd1 property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKUNRGSTCD1(String value) {
                this.kunrgstcd1 = value;
            }

            /**
             * Gets the value of the kunrgname property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKUNRGNAME() {
                return kunrgname;
            }

            /**
             * Sets the value of the kunrgname property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKUNRGNAME(String value) {
                this.kunrgname = value;
            }

            /**
             * Gets the value of the kunrgstreet property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKUNRGSTREET() {
                return kunrgstreet;
            }

            /**
             * Sets the value of the kunrgstreet property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKUNRGSTREET(String value) {
                this.kunrgstreet = value;
            }

            /**
             * Gets the value of the kunrghousenum1 property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKUNRGHOUSENUM1() {
                return kunrghousenum1;
            }

            /**
             * Sets the value of the kunrghousenum1 property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKUNRGHOUSENUM1(String value) {
                this.kunrghousenum1 = value;
            }

            /**
             * Gets the value of the kunrgpostcode1 property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKUNRGPOSTCODE1() {
                return kunrgpostcode1;
            }

            /**
             * Sets the value of the kunrgpostcode1 property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKUNRGPOSTCODE1(String value) {
                this.kunrgpostcode1 = value;
            }

            /**
             * Gets the value of the kunrgcity1 property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKUNRGCITY1() {
                return kunrgcity1;
            }

            /**
             * Sets the value of the kunrgcity1 property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKUNRGCITY1(String value) {
                this.kunrgcity1 = value;
            }

            /**
             * Gets the value of the kunrgregion property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKUNRGREGION() {
                return kunrgregion;
            }

            /**
             * Sets the value of the kunrgregion property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKUNRGREGION(String value) {
                this.kunrgregion = value;
            }

            /**
             * Gets the value of the kunrgcountry property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKUNRGCOUNTRY() {
                return kunrgcountry;
            }

            /**
             * Sets the value of the kunrgcountry property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKUNRGCOUNTRY(String value) {
                this.kunrgcountry = value;
            }

            /**
             * Gets the value of the bbbnr property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getBBBNR() {
                return bbbnr;
            }

            /**
             * Sets the value of the bbbnr property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setBBBNR(BigInteger value) {
                this.bbbnr = value;
            }

            /**
             * Gets the value of the fisknstreet property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFISKNSTREET() {
                return fisknstreet;
            }

            /**
             * Sets the value of the fisknstreet property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFISKNSTREET(String value) {
                this.fisknstreet = value;
            }

            /**
             * Gets the value of the fisknhousenum1 property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFISKNHOUSENUM1() {
                return fisknhousenum1;
            }

            /**
             * Sets the value of the fisknhousenum1 property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFISKNHOUSENUM1(String value) {
                this.fisknhousenum1 = value;
            }

            /**
             * Gets the value of the fisknpostcode1 property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFISKNPOSTCODE1() {
                return fisknpostcode1;
            }

            /**
             * Sets the value of the fisknpostcode1 property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFISKNPOSTCODE1(String value) {
                this.fisknpostcode1 = value;
            }

            /**
             * Gets the value of the fiskncity1 property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFISKNCITY1() {
                return fiskncity1;
            }

            /**
             * Sets the value of the fiskncity1 property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFISKNCITY1(String value) {
                this.fiskncity1 = value;
            }

            /**
             * Gets the value of the fisknregion property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFISKNREGION() {
                return fisknregion;
            }

            /**
             * Sets the value of the fisknregion property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFISKNREGION(String value) {
                this.fisknregion = value;
            }

            /**
             * Gets the value of the fiskncountry property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFISKNCOUNTRY() {
                return fiskncountry;
            }

            /**
             * Sets the value of the fiskncountry property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFISKNCOUNTRY(String value) {
                this.fiskncountry = value;
            }

            /**
             * Gets the value of the fisknname1 property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFISKNNAME1() {
                return fisknname1;
            }

            /**
             * Sets the value of the fisknname1 property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFISKNNAME1(String value) {
                this.fisknname1 = value;
            }

            /**
             * Gets the value of the fisknstceg property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFISKNSTCEG() {
                return fisknstceg;
            }

            /**
             * Sets the value of the fisknstceg property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFISKNSTCEG(String value) {
                this.fisknstceg = value;
            }

            /**
             * Gets the value of the totaledoc property.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getTOTALEDOC() {
                return totaledoc;
            }

            /**
             * Sets the value of the totaledoc property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setTOTALEDOC(BigDecimal value) {
                this.totaledoc = value;
            }

            /**
             * Gets the value of the causale property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCAUSALE() {
                return causale;
            }

            /**
             * Sets the value of the causale property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCAUSALE(String value) {
                this.causale = value;
            }

            /**
             * Gets the value of the totimp property.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getTOTIMP() {
                return totimp;
            }

            /**
             * Sets the value of the totimp property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setTOTIMP(BigDecimal value) {
                this.totimp = value;
            }

            /**
             * Gets the value of the zfbdt property.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getZFBDT() {
                return zfbdt;
            }

            /**
             * Sets the value of the zfbdt property.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setZFBDT(XMLGregorianCalendar value) {
                this.zfbdt = value;
            }

            /**
             * Gets the value of the scontoh property.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getSCONTOH() {
                return scontoh;
            }

            /**
             * Sets the value of the scontoh property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setSCONTOH(BigDecimal value) {
                this.scontoh = value;
            }

            /**
             * Gets the value of the scontovalh property.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getSCONTOVALH() {
                return scontovalh;
            }

            /**
             * Sets the value of the scontovalh property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setSCONTOVALH(BigDecimal value) {
                this.scontovalh = value;
            }

            /**
             * Gets the value of the trasph property.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getTRASPH() {
                return trasph;
            }

            /**
             * Sets the value of the trasph property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setTRASPH(BigDecimal value) {
                this.trasph = value;
            }

            /**
             * Gets the value of the elaborato property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getELABORATO() {
                return elaborato;
            }

            /**
             * Sets the value of the elaborato property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setELABORATO(String value) {
                this.elaborato = value;
            }

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
         *         &lt;element name="BUKRS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="SEQUENZIALE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="BELNR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="POSNR" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
         *         &lt;element name="GJAHR" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
         *         &lt;element name="ORD_ID_DOCUME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="ORD_DATA" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
         *         &lt;element name="ORD_NUM_ITEM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="ORD_COMMESSA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="ORD_CUP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="ORD_CIG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="CONT_ID_DOCUME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="CONT_DATA" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
         *         &lt;element name="CONT_NUM_ITEM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="CONT_COMMESSA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="CONT_CUP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="CONT_CIG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="CONV_ID_DOCUME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="CONV_DATA" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
         *         &lt;element name="CONV_NUM_ITEM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="CONV_COMMESSA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="CONV_CUP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="CONV_CIG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="RIC_ID_DOCUME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="RIC_DATA" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
         *         &lt;element name="RIC_NUM_ITEM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="RIC_COMMESSA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="RIC_CUP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="RIC_CIG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="DDT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="DDT_ERDAT" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
         *         &lt;element name="EAN11" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="ORD_COD_TIPO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="ORD_COD_VALORE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="TESTO_VBBP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="FKIMG" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
         *         &lt;element name="ORD_INIZIO_PRES" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
         *         &lt;element name="ORD_FINE_PRES" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
         *         &lt;element name="KZWI1" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
         *         &lt;element name="SCONTO" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
         *         &lt;element name="SCONTO_VAL" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
         *         &lt;element name="SC_PLUS" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
         *         &lt;element name="TRASP" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
         *         &lt;element name="COD_IVA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="ALIQIVA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="IMP_IVA" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
         *         &lt;element name="IMPOSTA_IVA" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
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
            "bukrs",
            "sequenziale",
            "belnr",
            "posnr",
            "gjahr",
            "ordiddocume",
            "orddata",
            "ordnumitem",
            "ordcommessa",
            "ordcup",
            "ordcig",
            "contiddocume",
            "contdata",
            "contnumitem",
            "contcommessa",
            "contcup",
            "contcig",
            "conviddocume",
            "convdata",
            "convnumitem",
            "convcommessa",
            "convcup",
            "convcig",
            "riciddocume",
            "ricdata",
            "ricnumitem",
            "riccommessa",
            "riccup",
            "riccig",
            "ddt",
            "ddterdat",
            "ean11",
            "ordcodtipo",
            "ordcodvalore",
            "testovbbp",
            "fkimg",
            "ordiniziopres",
            "ordfinepres",
            "kzwi1",
            "sconto",
            "scontoval",
            "scplus",
            "trasp",
            "codiva",
            "aliqiva",
            "impiva",
            "impostaiva"
        })
        public static class ZFATTELITEM {

            @XmlElement(name = "BUKRS")
            protected String bukrs;
            @XmlElement(name = "SEQUENZIALE")
            protected String sequenziale;
            @XmlElement(name = "BELNR")
            protected String belnr;
            @XmlElement(name = "POSNR")
            protected BigInteger posnr;
            @XmlElement(name = "GJAHR")
            protected BigInteger gjahr;
            @XmlElement(name = "ORD_ID_DOCUME")
            protected String ordiddocume;
            @XmlElement(name = "ORD_DATA")
            @XmlSchemaType(name = "date")
            protected XMLGregorianCalendar orddata;
            @XmlElement(name = "ORD_NUM_ITEM")
            protected String ordnumitem;
            @XmlElement(name = "ORD_COMMESSA")
            protected String ordcommessa;
            @XmlElement(name = "ORD_CUP")
            protected String ordcup;
            @XmlElement(name = "ORD_CIG")
            protected String ordcig;
            @XmlElement(name = "CONT_ID_DOCUME")
            protected String contiddocume;
            @XmlElement(name = "CONT_DATA")
            @XmlSchemaType(name = "date")
            protected XMLGregorianCalendar contdata;
            @XmlElement(name = "CONT_NUM_ITEM")
            protected String contnumitem;
            @XmlElement(name = "CONT_COMMESSA")
            protected String contcommessa;
            @XmlElement(name = "CONT_CUP")
            protected String contcup;
            @XmlElement(name = "CONT_CIG")
            protected String contcig;
            @XmlElement(name = "CONV_ID_DOCUME")
            protected String conviddocume;
            @XmlElement(name = "CONV_DATA")
            @XmlSchemaType(name = "date")
            protected XMLGregorianCalendar convdata;
            @XmlElement(name = "CONV_NUM_ITEM")
            protected String convnumitem;
            @XmlElement(name = "CONV_COMMESSA")
            protected String convcommessa;
            @XmlElement(name = "CONV_CUP")
            protected String convcup;
            @XmlElement(name = "CONV_CIG")
            protected String convcig;
            @XmlElement(name = "RIC_ID_DOCUME")
            protected String riciddocume;
            @XmlElement(name = "RIC_DATA")
            @XmlSchemaType(name = "date")
            protected XMLGregorianCalendar ricdata;
            @XmlElement(name = "RIC_NUM_ITEM")
            protected String ricnumitem;
            @XmlElement(name = "RIC_COMMESSA")
            protected String riccommessa;
            @XmlElement(name = "RIC_CUP")
            protected String riccup;
            @XmlElement(name = "RIC_CIG")
            protected String riccig;
            @XmlElement(name = "DDT")
            protected String ddt;
            @XmlElement(name = "DDT_ERDAT")
            @XmlSchemaType(name = "date")
            protected XMLGregorianCalendar ddterdat;
            @XmlElement(name = "EAN11")
            protected String ean11;
            @XmlElement(name = "ORD_COD_TIPO")
            protected String ordcodtipo;
            @XmlElement(name = "ORD_COD_VALORE")
            protected String ordcodvalore;
            @XmlElement(name = "TESTO_VBBP")
            protected String testovbbp;
            @XmlElement(name = "FKIMG")
            protected BigDecimal fkimg;
            @XmlElement(name = "ORD_INIZIO_PRES")
            @XmlSchemaType(name = "date")
            protected XMLGregorianCalendar ordiniziopres;
            @XmlElement(name = "ORD_FINE_PRES")
            @XmlSchemaType(name = "date")
            protected XMLGregorianCalendar ordfinepres;
            @XmlElement(name = "KZWI1")
            protected BigDecimal kzwi1;
            @XmlElement(name = "SCONTO")
            protected BigDecimal sconto;
            @XmlElement(name = "SCONTO_VAL")
            protected BigDecimal scontoval;
            @XmlElement(name = "SC_PLUS")
            protected BigDecimal scplus;
            @XmlElement(name = "TRASP")
            protected BigDecimal trasp;
            @XmlElement(name = "COD_IVA")
            protected String codiva;
            @XmlElement(name = "ALIQIVA")
            protected String aliqiva;
            @XmlElement(name = "IMP_IVA")
            protected BigDecimal impiva;
            @XmlElement(name = "IMPOSTA_IVA")
            protected BigDecimal impostaiva;

            /**
             * Gets the value of the bukrs property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getBUKRS() {
                return bukrs;
            }

            /**
             * Sets the value of the bukrs property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setBUKRS(String value) {
                this.bukrs = value;
            }

            /**
             * Gets the value of the sequenziale property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSEQUENZIALE() {
                return sequenziale;
            }

            /**
             * Sets the value of the sequenziale property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSEQUENZIALE(String value) {
                this.sequenziale = value;
            }

            /**
             * Gets the value of the belnr property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getBELNR() {
                return belnr;
            }

            /**
             * Sets the value of the belnr property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setBELNR(String value) {
                this.belnr = value;
            }

            /**
             * Gets the value of the posnr property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getPOSNR() {
                return posnr;
            }

            /**
             * Sets the value of the posnr property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setPOSNR(BigInteger value) {
                this.posnr = value;
            }

            /**
             * Gets the value of the gjahr property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getGJAHR() {
                return gjahr;
            }

            /**
             * Sets the value of the gjahr property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setGJAHR(BigInteger value) {
                this.gjahr = value;
            }

            /**
             * Gets the value of the ordiddocume property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getORDIDDOCUME() {
                return ordiddocume;
            }

            /**
             * Sets the value of the ordiddocume property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setORDIDDOCUME(String value) {
                this.ordiddocume = value;
            }

            /**
             * Gets the value of the orddata property.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getORDDATA() {
                return orddata;
            }

            /**
             * Sets the value of the orddata property.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setORDDATA(XMLGregorianCalendar value) {
                this.orddata = value;
            }

            /**
             * Gets the value of the ordnumitem property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getORDNUMITEM() {
                return ordnumitem;
            }

            /**
             * Sets the value of the ordnumitem property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setORDNUMITEM(String value) {
                this.ordnumitem = value;
            }

            /**
             * Gets the value of the ordcommessa property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getORDCOMMESSA() {
                return ordcommessa;
            }

            /**
             * Sets the value of the ordcommessa property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setORDCOMMESSA(String value) {
                this.ordcommessa = value;
            }

            /**
             * Gets the value of the ordcup property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getORDCUP() {
                return ordcup;
            }

            /**
             * Sets the value of the ordcup property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setORDCUP(String value) {
                this.ordcup = value;
            }

            /**
             * Gets the value of the ordcig property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getORDCIG() {
                return ordcig;
            }

            /**
             * Sets the value of the ordcig property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setORDCIG(String value) {
                this.ordcig = value;
            }

            /**
             * Gets the value of the contiddocume property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCONTIDDOCUME() {
                return contiddocume;
            }

            /**
             * Sets the value of the contiddocume property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCONTIDDOCUME(String value) {
                this.contiddocume = value;
            }

            /**
             * Gets the value of the contdata property.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getCONTDATA() {
                return contdata;
            }

            /**
             * Sets the value of the contdata property.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setCONTDATA(XMLGregorianCalendar value) {
                this.contdata = value;
            }

            /**
             * Gets the value of the contnumitem property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCONTNUMITEM() {
                return contnumitem;
            }

            /**
             * Sets the value of the contnumitem property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCONTNUMITEM(String value) {
                this.contnumitem = value;
            }

            /**
             * Gets the value of the contcommessa property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCONTCOMMESSA() {
                return contcommessa;
            }

            /**
             * Sets the value of the contcommessa property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCONTCOMMESSA(String value) {
                this.contcommessa = value;
            }

            /**
             * Gets the value of the contcup property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCONTCUP() {
                return contcup;
            }

            /**
             * Sets the value of the contcup property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCONTCUP(String value) {
                this.contcup = value;
            }

            /**
             * Gets the value of the contcig property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCONTCIG() {
                return contcig;
            }

            /**
             * Sets the value of the contcig property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCONTCIG(String value) {
                this.contcig = value;
            }

            /**
             * Gets the value of the conviddocume property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCONVIDDOCUME() {
                return conviddocume;
            }

            /**
             * Sets the value of the conviddocume property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCONVIDDOCUME(String value) {
                this.conviddocume = value;
            }

            /**
             * Gets the value of the convdata property.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getCONVDATA() {
                return convdata;
            }

            /**
             * Sets the value of the convdata property.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setCONVDATA(XMLGregorianCalendar value) {
                this.convdata = value;
            }

            /**
             * Gets the value of the convnumitem property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCONVNUMITEM() {
                return convnumitem;
            }

            /**
             * Sets the value of the convnumitem property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCONVNUMITEM(String value) {
                this.convnumitem = value;
            }

            /**
             * Gets the value of the convcommessa property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCONVCOMMESSA() {
                return convcommessa;
            }

            /**
             * Sets the value of the convcommessa property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCONVCOMMESSA(String value) {
                this.convcommessa = value;
            }

            /**
             * Gets the value of the convcup property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCONVCUP() {
                return convcup;
            }

            /**
             * Sets the value of the convcup property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCONVCUP(String value) {
                this.convcup = value;
            }

            /**
             * Gets the value of the convcig property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCONVCIG() {
                return convcig;
            }

            /**
             * Sets the value of the convcig property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCONVCIG(String value) {
                this.convcig = value;
            }

            /**
             * Gets the value of the riciddocume property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getRICIDDOCUME() {
                return riciddocume;
            }

            /**
             * Sets the value of the riciddocume property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setRICIDDOCUME(String value) {
                this.riciddocume = value;
            }

            /**
             * Gets the value of the ricdata property.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getRICDATA() {
                return ricdata;
            }

            /**
             * Sets the value of the ricdata property.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setRICDATA(XMLGregorianCalendar value) {
                this.ricdata = value;
            }

            /**
             * Gets the value of the ricnumitem property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getRICNUMITEM() {
                return ricnumitem;
            }

            /**
             * Sets the value of the ricnumitem property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setRICNUMITEM(String value) {
                this.ricnumitem = value;
            }

            /**
             * Gets the value of the riccommessa property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getRICCOMMESSA() {
                return riccommessa;
            }

            /**
             * Sets the value of the riccommessa property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setRICCOMMESSA(String value) {
                this.riccommessa = value;
            }

            /**
             * Gets the value of the riccup property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getRICCUP() {
                return riccup;
            }

            /**
             * Sets the value of the riccup property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setRICCUP(String value) {
                this.riccup = value;
            }

            /**
             * Gets the value of the riccig property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getRICCIG() {
                return riccig;
            }

            /**
             * Sets the value of the riccig property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setRICCIG(String value) {
                this.riccig = value;
            }

            /**
             * Gets the value of the ddt property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDDT() {
                return ddt;
            }

            /**
             * Sets the value of the ddt property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDDT(String value) {
                this.ddt = value;
            }

            /**
             * Gets the value of the ddterdat property.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getDDTERDAT() {
                return ddterdat;
            }

            /**
             * Sets the value of the ddterdat property.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setDDTERDAT(XMLGregorianCalendar value) {
                this.ddterdat = value;
            }

            /**
             * Gets the value of the ean11 property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getEAN11() {
                return ean11;
            }

            /**
             * Sets the value of the ean11 property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setEAN11(String value) {
                this.ean11 = value;
            }

            /**
             * Gets the value of the ordcodtipo property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getORDCODTIPO() {
                return ordcodtipo;
            }

            /**
             * Sets the value of the ordcodtipo property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setORDCODTIPO(String value) {
                this.ordcodtipo = value;
            }

            /**
             * Gets the value of the ordcodvalore property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getORDCODVALORE() {
                return ordcodvalore;
            }

            /**
             * Sets the value of the ordcodvalore property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setORDCODVALORE(String value) {
                this.ordcodvalore = value;
            }

            /**
             * Gets the value of the testovbbp property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTESTOVBBP() {
                return testovbbp;
            }

            /**
             * Sets the value of the testovbbp property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTESTOVBBP(String value) {
                this.testovbbp = value;
            }

            /**
             * Gets the value of the fkimg property.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getFKIMG() {
                return fkimg;
            }

            /**
             * Sets the value of the fkimg property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setFKIMG(BigDecimal value) {
                this.fkimg = value;
            }

            /**
             * Gets the value of the ordiniziopres property.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getORDINIZIOPRES() {
                return ordiniziopres;
            }

            /**
             * Sets the value of the ordiniziopres property.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setORDINIZIOPRES(XMLGregorianCalendar value) {
                this.ordiniziopres = value;
            }

            /**
             * Gets the value of the ordfinepres property.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getORDFINEPRES() {
                return ordfinepres;
            }

            /**
             * Sets the value of the ordfinepres property.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setORDFINEPRES(XMLGregorianCalendar value) {
                this.ordfinepres = value;
            }

            /**
             * Gets the value of the kzwi1 property.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getKZWI1() {
                return kzwi1;
            }

            /**
             * Sets the value of the kzwi1 property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setKZWI1(BigDecimal value) {
                this.kzwi1 = value;
            }

            /**
             * Gets the value of the sconto property.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getSCONTO() {
                return sconto;
            }

            /**
             * Sets the value of the sconto property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setSCONTO(BigDecimal value) {
                this.sconto = value;
            }

            /**
             * Gets the value of the scontoval property.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getSCONTOVAL() {
                return scontoval;
            }

            /**
             * Sets the value of the scontoval property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setSCONTOVAL(BigDecimal value) {
                this.scontoval = value;
            }

            /**
             * Gets the value of the scplus property.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getSCPLUS() {
                return scplus;
            }

            /**
             * Sets the value of the scplus property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setSCPLUS(BigDecimal value) {
                this.scplus = value;
            }

            /**
             * Gets the value of the trasp property.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getTRASP() {
                return trasp;
            }

            /**
             * Sets the value of the trasp property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setTRASP(BigDecimal value) {
                this.trasp = value;
            }

            /**
             * Gets the value of the codiva property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCODIVA() {
                return codiva;
            }

            /**
             * Sets the value of the codiva property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCODIVA(String value) {
                this.codiva = value;
            }

            /**
             * Gets the value of the aliqiva property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getALIQIVA() {
                return aliqiva;
            }

            /**
             * Sets the value of the aliqiva property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setALIQIVA(String value) {
                this.aliqiva = value;
            }

            /**
             * Gets the value of the impiva property.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getIMPIVA() {
                return impiva;
            }

            /**
             * Sets the value of the impiva property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setIMPIVA(BigDecimal value) {
                this.impiva = value;
            }

            /**
             * Gets the value of the impostaiva property.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getIMPOSTAIVA() {
                return impostaiva;
            }

            /**
             * Sets the value of the impostaiva property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setIMPOSTAIVA(BigDecimal value) {
                this.impostaiva = value;
            }

        }

    }

}
