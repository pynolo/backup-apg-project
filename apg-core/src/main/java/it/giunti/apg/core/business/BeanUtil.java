package it.giunti.apg.core.business;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanUtil {

	static private Logger LOG = LoggerFactory.getLogger(BeanUtil.class);

	public static boolean compareBeans(Object bean1, Object bean2) {
		// First thing to do is to check if it is the same object and if they are not
		// null:
		if (bean1 == bean2) {
			return true;
		}
		if (bean1 == null || bean2 == null) {
			return false;
		}
		
		// Once not null we can check if both beans belong to the same class :
		if (bean1.getClass() != bean2.getClass()) {
			return false;
		}
		
		// For base types (primitive equivalent class + String) we can use the the
		// equals function and no introspection for performance reasons.
		Class<? extends Object> clazz = bean1.getClass();
		if (clazz == String.class || clazz == Integer.class || clazz == Double.class || clazz == Float.class
				|| clazz == Date.class || clazz == java.sql.Date.class) {
			return bean1.equals(bean2);
		}

		// Next only introspection is left, so let's first gather the information of the
		// bean class :
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			LOG.error(e.getMessage(), e);
			return false;
		}

		// Next we loop over all the properties, get the read method and invoke it on
		// both beans. Both values are compared by recursively calling the current
		// compareBeans function :
		for (PropertyDescriptor prop : beanInfo.getPropertyDescriptors()) {
			Method getter = prop.getReadMethod();
			if (getter != null) {
				Object value1 = null;
				Object value2 = null;
				try {
					value1 = getter.invoke(bean1);
					value2 = getter.invoke(bean2);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					/*LOG.error(e.getMessage(), e);
					return false;*/
				}
				// compare the values as beans
				if (!compareBeans(value1, value2)) {
					LOG.debug(getter.getName()+": "+value1+" != "+value2);
					return false;
				}
			}
		}
		return true;
	}
}
