
package it.giunti.apg.soap.statoodv;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.giunti.apg.soap.statoodv package. 
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

    private final static QName _ORDERSTATUSREQ_QNAME = new QName("urn:giuntieditore.com:INTERNAL:0031_REPORT_ODV_ABB", "ORDER_STATUS_REQ");
    private final static QName _ORDERSTATUSRSP_QNAME = new QName("urn:giuntieditore.com:INTERNAL:0031_REPORT_ODV_ABB", "ORDER_STATUS_RSP");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.giunti.apg.soap.statoodv
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ORDERSTATUSRSP }
     * 
     */
    public ORDERSTATUSRSP createORDERSTATUSRSP() {
        return new ORDERSTATUSRSP();
    }

    /**
     * Create an instance of {@link ORDERSTATUSREQ }
     * 
     */
    public ORDERSTATUSREQ createORDERSTATUSREQ() {
        return new ORDERSTATUSREQ();
    }

    /**
     * Create an instance of {@link ORDERSTATUSRSP.ORDERDATA }
     * 
     */
    public ORDERSTATUSRSP.ORDERDATA createORDERSTATUSRSPORDERDATA() {
        return new ORDERSTATUSRSP.ORDERDATA();
    }

    /**
     * Create an instance of {@link ORDERSTATUSREQ.RECORDSET }
     * 
     */
    public ORDERSTATUSREQ.RECORDSET createORDERSTATUSREQRECORDSET() {
        return new ORDERSTATUSREQ.RECORDSET();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ORDERSTATUSREQ }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ORDERSTATUSREQ }{@code >}
     */
    @XmlElementDecl(namespace = "urn:giuntieditore.com:INTERNAL:0031_REPORT_ODV_ABB", name = "ORDER_STATUS_REQ")
    public JAXBElement<ORDERSTATUSREQ> createORDERSTATUSREQ(ORDERSTATUSREQ value) {
        return new JAXBElement<ORDERSTATUSREQ>(_ORDERSTATUSREQ_QNAME, ORDERSTATUSREQ.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ORDERSTATUSRSP }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ORDERSTATUSRSP }{@code >}
     */
    @XmlElementDecl(namespace = "urn:giuntieditore.com:INTERNAL:0031_REPORT_ODV_ABB", name = "ORDER_STATUS_RSP")
    public JAXBElement<ORDERSTATUSRSP> createORDERSTATUSRSP(ORDERSTATUSRSP value) {
        return new JAXBElement<ORDERSTATUSRSP>(_ORDERSTATUSRSP_QNAME, ORDERSTATUSRSP.class, null, value);
    }

}
