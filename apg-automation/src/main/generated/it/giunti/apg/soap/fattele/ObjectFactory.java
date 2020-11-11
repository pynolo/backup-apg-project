
package it.giunti.apg.soap.fattele;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.giunti.apg.soap.fattele package. 
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

    private final static QName _INVOICEERSP_QNAME = new QName("urn:giuntieditore.com:INTERNAL:0032_SALES_INVOICE.E_ABB", "INVOICE_E_RSP");
    private final static QName _INVOICEEREQ_QNAME = new QName("urn:giuntieditore.com:INTERNAL:0032_SALES_INVOICE.E_ABB", "INVOICE_E_REQ");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.giunti.apg.soap.fattele
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link INVOICEEREQ }
     * 
     */
    public INVOICEEREQ createINVOICEEREQ() {
        return new INVOICEEREQ();
    }

    /**
     * Create an instance of {@link INVOICEEREQ.FATTELE }
     * 
     */
    public INVOICEEREQ.FATTELE createINVOICEEREQFATTELE() {
        return new INVOICEEREQ.FATTELE();
    }

    /**
     * Create an instance of {@link INVOICEERSP }
     * 
     */
    public INVOICEERSP createINVOICEERSP() {
        return new INVOICEERSP();
    }

    /**
     * Create an instance of {@link INVOICEEREQ.FATTELE.ZFATTELHEAD }
     * 
     */
    public INVOICEEREQ.FATTELE.ZFATTELHEAD createINVOICEEREQFATTELEZFATTELHEAD() {
        return new INVOICEEREQ.FATTELE.ZFATTELHEAD();
    }

    /**
     * Create an instance of {@link INVOICEEREQ.FATTELE.ZFATTELITEM }
     * 
     */
    public INVOICEEREQ.FATTELE.ZFATTELITEM createINVOICEEREQFATTELEZFATTELITEM() {
        return new INVOICEEREQ.FATTELE.ZFATTELITEM();
    }

    /**
     * Create an instance of {@link INVOICEERSP.ZFATTELERRS }
     * 
     */
    public INVOICEERSP.ZFATTELERRS createINVOICEERSPZFATTELERRS() {
        return new INVOICEERSP.ZFATTELERRS();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link INVOICEERSP }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link INVOICEERSP }{@code >}
     */
    @XmlElementDecl(namespace = "urn:giuntieditore.com:INTERNAL:0032_SALES_INVOICE.E_ABB", name = "INVOICE_E_RSP")
    public JAXBElement<INVOICEERSP> createINVOICEERSP(INVOICEERSP value) {
        return new JAXBElement<INVOICEERSP>(_INVOICEERSP_QNAME, INVOICEERSP.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link INVOICEEREQ }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link INVOICEEREQ }{@code >}
     */
    @XmlElementDecl(namespace = "urn:giuntieditore.com:INTERNAL:0032_SALES_INVOICE.E_ABB", name = "INVOICE_E_REQ")
    public JAXBElement<INVOICEEREQ> createINVOICEEREQ(INVOICEEREQ value) {
        return new JAXBElement<INVOICEEREQ>(_INVOICEEREQ_QNAME, INVOICEEREQ.class, null, value);
    }

}
