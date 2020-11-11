
package it.giunti.apg.soap.creaodv;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.giunti.apg.soap.creaodv package. 
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

    private final static QName _ORDERRSP_QNAME = new QName("urn:giuntieditore.com:INTERNAL:0031_REPORT_ODV_ABB", "ORDER_RSP");
    private final static QName _ORDERREQ_QNAME = new QName("urn:giuntieditore.com:INTERNAL:0031_REPORT_ODV_ABB", "ORDER_REQ");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.giunti.apg.soap.creaodv
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ORDERREQ }
     * 
     */
    public ORDERREQ createORDERREQ() {
        return new ORDERREQ();
    }

    /**
     * Create an instance of {@link ORDERRSP }
     * 
     */
    public ORDERRSP createORDERRSP() {
        return new ORDERRSP();
    }

    /**
     * Create an instance of {@link ORDERREQ.RECORDSET }
     * 
     */
    public ORDERREQ.RECORDSET createORDERREQRECORDSET() {
        return new ORDERREQ.RECORDSET();
    }

    /**
     * Create an instance of {@link ORDERRSP.RECORDSET }
     * 
     */
    public ORDERRSP.RECORDSET createORDERRSPRECORDSET() {
        return new ORDERRSP.RECORDSET();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ORDERRSP }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ORDERRSP }{@code >}
     */
    @XmlElementDecl(namespace = "urn:giuntieditore.com:INTERNAL:0031_REPORT_ODV_ABB", name = "ORDER_RSP")
    public JAXBElement<ORDERRSP> createORDERRSP(ORDERRSP value) {
        return new JAXBElement<ORDERRSP>(_ORDERRSP_QNAME, ORDERRSP.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ORDERREQ }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ORDERREQ }{@code >}
     */
    @XmlElementDecl(namespace = "urn:giuntieditore.com:INTERNAL:0031_REPORT_ODV_ABB", name = "ORDER_REQ")
    public JAXBElement<ORDERREQ> createORDERREQ(ORDERREQ value) {
        return new JAXBElement<ORDERREQ>(_ORDERREQ_QNAME, ORDERREQ.class, null, value);
    }

}
