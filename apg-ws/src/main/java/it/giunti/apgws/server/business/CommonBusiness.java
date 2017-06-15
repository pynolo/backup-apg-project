package it.giunti.apgws.server.business;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class CommonBusiness {
	
	/**
	* Transform a date in a long to a GregorianCalendar
	*
	* @param date
	* @return
	*/
	public static XMLGregorianCalendar long2XmlGregorian(long date) {
		DatatypeFactory dataTypeFactory;
		try {
			dataTypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(date);
		return dataTypeFactory.newXMLGregorianCalendar(gc);
	}

	/**
	* Transform a date in Date to XMLGregorianCalendar
	*/
	public static XMLGregorianCalendar dateToXmlDate(Date date) {
		return long2XmlGregorian(date.getTime());
	}
	
}
