
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package it.giunti.apg.soap.anagmat;

import java.util.logging.Logger;

/**
 * This class was generated by Apache CXF 3.3.7
 * 2020-08-28T11:52:59.612+02:00
 * Generated source version: 3.3.7
 *
 */

@javax.jws.WebService(
                      serviceName = "STOCK_REPORT_OUTService",
                      portName = "HTTPS_Port",
                      targetNamespace = "urn:giuntieditore.com:3RD_PARTY:0090_STOCK_REPORT_ABB",
                      wsdlLocation = "/wsdl/IF_017_003_anagmat.wsdl",
                      endpointInterface = "it.giunti.apg.soap.anagmat.STOCKREPORTOUT")

public class HTTPS_PortImpl implements STOCKREPORTOUT {

    private static final Logger LOG = Logger.getLogger(HTTPS_PortImpl.class.getName());

    /* (non-Javadoc)
     * @see it.giunti.apg.soap.anagmat.STOCKREPORTOUT#stockREPORTOUT(it.giunti.apg.soap.anagmat.STOCKREPORTREQ stockREPORTREQ)*
     */
    public it.giunti.apg.soap.anagmat.STOCKREPORTRSP stockREPORTOUT(STOCKREPORTREQ stockREPORTREQ) {
        LOG.info("Executing operation stockREPORTOUT");
        System.out.println(stockREPORTREQ);
        try {
            it.giunti.apg.soap.anagmat.STOCKREPORTRSP _return = new it.giunti.apg.soap.anagmat.STOCKREPORTRSP();
            java.util.List<it.giunti.apg.soap.anagmat.STOCKREPORTRSP.RECORDSET> _returnRECORDSET = new java.util.ArrayList<it.giunti.apg.soap.anagmat.STOCKREPORTRSP.RECORDSET>();
            it.giunti.apg.soap.anagmat.STOCKREPORTRSP.RECORDSET _returnRECORDSETVal1 = new it.giunti.apg.soap.anagmat.STOCKREPORTRSP.RECORDSET();
            _returnRECORDSETVal1.setMATNR("MATNR-476781789");
            _returnRECORDSETVal1.setTIPO("TIPO-882393053");
            _returnRECORDSETVal1.setGIACENZA(new java.math.BigDecimal("-2072881084596893396.9192725812304914179"));
            _returnRECORDSETVal1.setBLOCCATO(Boolean.valueOf(true));
            _returnRECORDSET.add(_returnRECORDSETVal1);
            _return.getRECORDSET().addAll(_returnRECORDSET);
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}
