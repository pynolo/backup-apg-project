
package it.giunti.apg.soap.magnews;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-b01-
 * Generated source version: 2.1
 * 
 */
@WebFault(name = "AuthenticationFailedException", targetNamespace = "http://webservices.magnews/")
public class AuthenticationFailedException_Exception
    extends Exception
{

	private static final long serialVersionUID = -8409778705061176823L;
	/**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private AuthenticationFailedException faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public AuthenticationFailedException_Exception(String message, AuthenticationFailedException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param cause
     * @param message
     */
    public AuthenticationFailedException_Exception(String message, AuthenticationFailedException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: it.giunti.apg.soap.magnews.AuthenticationFailedException
     */
    public AuthenticationFailedException getFaultInfo() {
        return faultInfo;
    }

}
