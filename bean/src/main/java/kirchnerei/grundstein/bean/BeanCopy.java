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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyDescriptor;
import java.util.List;

public class BeanCopy {

	private static final Log log = LogFactory.getLog(BeanCopy.class);

	private final BeanUtilsBean bub = new BeanUtilsBean();
	private final PropertyUtilsBean pub = bub.getPropertyUtils();

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


	public Object readProperty(Object bean, String name) {
		try {
			return pub.getProperty(bean, name);
		} catch (Exception e) {
			throw new BeanCopyException(e, "read property '%s' is failed", name);
		}
	}

	public void copyProperty(Object bean, String name, Object value) {
		try {
			PropertyDescriptor pd = pub.getPropertyDescriptor(bean, name);
			Class<?> type = pd.getWriteMethod().getParameterTypes()[0];
			bub.copyProperty(bean, name, convert(type, value));
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

	private Object convert(Class<?> type, Object value) {
		// TODO
		return value;
	}
}
