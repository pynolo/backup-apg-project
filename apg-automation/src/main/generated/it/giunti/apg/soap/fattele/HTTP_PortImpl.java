
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package it.giunti.apg.soap.fattele;

import java.util.logging.Logger;

/**
 * This class was generated by Apache CXF 3.3.7
 * 2020-09-23T08:35:30.879+02:00
 * Generated source version: 3.3.7
 *
 */

@javax.jws.WebService(
                      serviceName = "INVOICE_E_OUTService",
                      portName = "HTTP_Port",
                      targetNamespace = "urn:giuntieditore.com:INTERNAL:0032_SALES_INVOICE.E_ABB",
                      wsdlLocation = "/wsdl/IF_017_002_fattele.wsdl",
                      endpointInterface = "it.giunti.apg.soap.fattele.INVOICEEOUT")

public class HTTP_PortImpl implements INVOICEEOUT {

    private static final Logger LOG = Logger.getLogger(HTTP_PortImpl.class.getName());

    /* (non-Javadoc)
     * @see it.giunti.apg.soap.fattele.INVOICEEOUT#invoiceEOUT(it.giunti.apg.soap.fattele.INVOICEEREQ invoiceEREQ)*
     */
    public it.giunti.apg.soap.fattele.INVOICEERSP invoiceEOUT(INVOICEEREQ invoiceEREQ) {
        LOG.info("Executing operation invoiceEOUT");
        System.out.println(invoiceEREQ);
        try {
            it.giunti.apg.soap.fattele.INVOICEERSP _return = new it.giunti.apg.soap.fattele.INVOICEERSP();
            java.util.List<it.giunti.apg.soap.fattele.INVOICEERSP.ZFATTELERRS> _returnZFATTELERRS = new java.util.ArrayList<it.giunti.apg.soap.fattele.INVOICEERSP.ZFATTELERRS>();
            it.giunti.apg.soap.fattele.INVOICEERSP.ZFATTELERRS _returnZFATTELERRSVal1 = new it.giunti.apg.soap.fattele.INVOICEERSP.ZFATTELERRS();
            _returnZFATTELERRSVal1.setLINE("LINE707856941");
            _returnZFATTELERRSVal1.setTABNAME("TABNAME-1275843999");
            _returnZFATTELERRSVal1.setFIELDNAME("FIELDNAME-1755051311");
            _returnZFATTELERRSVal1.setMESSAGE("MESSAGE1399802118");
            _returnZFATTELERRS.add(_returnZFATTELERRSVal1);
            _return.getZFATTELERRS().addAll(_returnZFATTELERRS);
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}
