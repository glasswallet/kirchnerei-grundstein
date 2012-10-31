/*
 * Copyright 2012 Kirchnerei
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kirchnerei.grundstein.bean;

import kirchnerei.grundstein.ClassUtils;
import kirchnerei.grundstein.LogUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanCopy {

	private static final Log log = LogFactory.getLog(BeanCopy.class);

	private final Map<Class<?>, StringConverter> class2Converter = new HashMap<>();

	private final BeanUtilsBean bub = new BeanUtilsBean();
	private final PropertyUtilsBean pub = bub.getPropertyUtils();

	public BeanCopy() {
		LongStringConverter lsc = new LongStringConverter();
		IntegerStringConverter isc = new IntegerStringConverter();
		DoubleStringConverter dsc = new DoubleStringConverter();
		BooleanStringConverter bsc = new BooleanStringConverter();
		addConverter(long.class, lsc);
		addConverter(Long.class, lsc);
		addConverter(int.class, isc);
		addConverter(Integer.class, isc);
		addConverter(double.class, dsc);
		addConverter(Double.class, dsc);
		addConverter(boolean.class, bsc);
		addConverter(Boolean.class, bsc);
	}

	public <T> T copy(Object source, Class<T> type, List<String> properties) {
		T target = ClassUtils.createInstance(type);
		return copy(source, target, properties);
	}

	public <T> T copy(Object source, T target, List<String> properties) {
		LogUtils.trace(log, "copy bean (%s -> %s)",
			source.getClass().getSimpleName(), target.getClass().getSimpleName());
		for (String name : properties) {
			LogUtils.trace(log, "copy property '%s'", name);
			Object value = readProperty(source, name);
			copyProperty(target, name, value);
		}
		return target;
	}


	public <T> T read(ParameterDelivery delivery, List<String> properties, Class<T> type) {
		T target = ClassUtils.createInstance(type);
		return read(delivery, properties, target);
	}

	public <T> T read(ParameterDelivery delivery, List<String> properties, T target) {
		for (String name : properties) {
			String value = delivery.getParameter(name);
			copyProperty(target, name, value);
		}
		return target;
	}

	public Object readProperty(Object bean, String name) {
		try {
			return pub.getProperty(bean, name);
		} catch (Exception e) {
			throw new BeanCopyException(e, "read property '%s' is failed", name);
		}
	}

	public void copyProperty(Object bean, String name, Object value) {
		Class<?> toType = getWritePropertyClass(bean, name);
		copyProperty(bean, name, toType, value);
	}

	private void copyProperty(Object bean, String name, Class<?> toType, Object value) {
		try {
			bub.copyProperty(bean, name, convertValue(toType, value));
		} catch (Exception e) {
			throw new BeanCopyException(e, "copy property '%s' is failed", name);
		}
	}

	public Class<?> getWritePropertyClass(Object bean, String name) {
		try {
			PropertyDescriptor pd = pub.getPropertyDescriptor(bean, name);
			return pd.getWriteMethod().getParameterTypes()[0];
		} catch (Exception e) {
			throw new BeanCopyException(e, "write property class from '%s' is failed", name);
		}
	}

	public Class<?> getReadPropertyClass(Object bean, String name) {
		try {
			PropertyDescriptor pd = pub.getPropertyDescriptor(bean, name);
			return pd.getReadMethod().getReturnType();
		} catch (Exception e) {
			throw new BeanCopyException(e, "read property class from '%s' is failed", name);
		}
	}

	public void addConverter(Class<?> toType, StringConverter converter) {
		if (!class2Converter.containsKey(toType)) {
			class2Converter.put(toType, converter);
		}
	}

	public void addConverter(Class<?> toType, Class<? extends StringConverter> converterType) {
		if (!class2Converter.containsKey(toType)) {
			StringConverter converter = ClassUtils.createInstance(converterType);
			class2Converter.put(toType, converter);
		}
	}

	private Object convertValue(Class<?> toType, Object value) {
		if (class2Converter.containsKey(toType) && value instanceof String) {
			StringConverter converter = class2Converter.get(toType);
			return converter.convert((String) value);
		}
		return value;
	}


	private static class LongStringConverter implements StringConverter {

		@Override
		public Object convert(String text) {
			return NumberUtils.toLong(text);
		}
	}

	private static class IntegerStringConverter implements StringConverter {

		@Override
		public Object convert(String text) {
			return NumberUtils.toInt(text);
		}
	}

	private static class DoubleStringConverter implements StringConverter {

		@Override
		public Object convert(String text) {
			return NumberUtils.toDouble(text);
		}
	}

	private static class BooleanStringConverter implements StringConverter {

		@Override
		public Object convert(String text) {
			if (StringUtils.isEmpty(text)) {
				return Boolean.FALSE;
			}
			switch (text.toLowerCase()) {
				case "true":
				case "t":
					return Boolean.TRUE;
				case "false":
				case "f":
				case "0":
					return Boolean.FALSE;
			}
			int value = NumberUtils.toInt(text);
			return (value > 0) ? Boolean.TRUE : Boolean.FALSE;
		}
	}
}
