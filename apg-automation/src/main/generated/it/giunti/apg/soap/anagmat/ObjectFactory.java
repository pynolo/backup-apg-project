
package it.giunti.apg.soap.anagmat;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.giunti.apg.soap.anagmat package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _STOCKREPORTRSP_QNAME = new QName("urn:giuntieditore.com:3RD_PARTY:0090_STOCK_REPORT_ABB", "STOCK_REPORT_RSP");
    private final static QName _STOCKREPORTREQ_QNAME = new QName("urn:giuntieditore.com:3RD_PARTY:0090_STOCK_REPORT_ABB", "STOCK_REPORT_REQ");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.giunti.apg.soap.anagmat
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link STOCKREPORTRSP }
     * 
     */
    public STOCKREPORTRSP createSTOCKREPORTRSP() {
        return new STOCKREPORTRSP();
    }

    /**
     * Create an instance of {@link STOCKREPORTREQ }
     * 
     */
    public STOCKREPORTREQ createSTOCKREPORTREQ() {
        return new STOCKREPORTREQ();
    }

    /**
     * Create an instance of {@link STOCKREPORTRSP.RECORDSET }
     * 
     */
    public STOCKREPORTRSP.RECORDSET createSTOCKREPORTRSPRECORDSET() {
        return new STOCKREPORTRSP.RECORDSET();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link STOCKREPORTRSP }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link STOCKREPORTRSP }{@code >}
     */
    @XmlElementDecl(namespace = "urn:giuntieditore.com:3RD_PARTY:0090_STOCK_REPORT_ABB", name = "STOCK_REPORT_RSP")
    public JAXBElement<STOCKREPORTRSP> createSTOCKREPORTRSP(STOCKREPORTRSP value) {
        return new JAXBElement<STOCKREPORTRSP>(_STOCKREPORTRSP_QNAME, STOCKREPORTRSP.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link STOCKREPORTREQ }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link STOCKREPORTREQ }{@code >}
     */
    @XmlElementDecl(namespace = "urn:giuntieditore.com:3RD_PARTY:0090_STOCK_REPORT_ABB", name = "STOCK_REPORT_REQ")
    public JAXBElement<STOCKREPORTREQ> createSTOCKREPORTREQ(STOCKREPORTREQ value) {
        return new JAXBElement<STOCKREPORTREQ>(_STOCKREPORTREQ_QNAME, STOCKREPORTREQ.class, null, value);
    }

}
