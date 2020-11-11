package it.giunti.apg.automation.sap;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

import it.giunti.apg.soap.fattele.INVOICEEOUT;

/**
 * This class was generated by Apache CXF 3.3.7
 * 2020-07-13T11:13:15.944+02:00
 * Generated source version: 3.3.7
 *
 */
@WebServiceClient(name = "INVOICE_E_OUTService",
                  wsdlLocation = "/wsdl/IF_017_002_fattele.wsdl",
                  targetNamespace = "urn:giuntieditore.com:INTERNAL:0032_SALES_INVOICE.E_ABB")
public class FatteleSapService extends Service {

	public final static String WSDL_FILE_LOCATION = "/wsdl/IF_017_002_fattele.wsdl";
    public final static URL WSDL_LOCATION = new FatteleSapServiceBusiness().getClass().getResource(WSDL_FILE_LOCATION);

    public final static QName SERVICE = new QName("urn:giuntieditore.com:INTERNAL:0032_SALES_INVOICE.E_ABB", "INVOICE_E_OUTService");
    public final static QName HTTPSPort = new QName("urn:giuntieditore.com:INTERNAL:0032_SALES_INVOICE.E_ABB", "HTTPS_Port");
    public final static QName HTTPPort = new QName("urn:giuntieditore.com:INTERNAL:0032_SALES_INVOICE.E_ABB", "HTTP_Port");

    public FatteleSapService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public FatteleSapService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public FatteleSapService() {
        super(WSDL_LOCATION, SERVICE);
    }

    public FatteleSapService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    public FatteleSapService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public FatteleSapService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }




    /**
     *
     * @return
     *     returns INVOICEEOUT
     */
    @WebEndpoint(name = "HTTPS_Port")
    public INVOICEEOUT getHTTPSPort() {
        return super.getPort(HTTPSPort, INVOICEEOUT.class);
    }

    /**
     *
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns INVOICEEOUT
     */
    @WebEndpoint(name = "HTTPS_Port")
    public INVOICEEOUT getHTTPSPort(WebServiceFeature... features) {
        return super.getPort(HTTPSPort, INVOICEEOUT.class, features);
    }


    /**
     *
     * @return
     *     returns INVOICEEOUT
     */
    @WebEndpoint(name = "HTTP_Port")
    public INVOICEEOUT getHTTPPort() {
        return super.getPort(HTTPPort, INVOICEEOUT.class);
    }

    /**
     *
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns INVOICEEOUT
     */
    @WebEndpoint(name = "HTTP_Port")
    public INVOICEEOUT getHTTPPort(WebServiceFeature... features) {
        return super.getPort(HTTPPort, INVOICEEOUT.class, features);
    }

}
