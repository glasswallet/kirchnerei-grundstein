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

package kirchnerei.grundstein.click.service;

import kirchnerei.grundstein.ClassUtils;
import kirchnerei.grundstein.bean.BeanCopy;
import kirchnerei.grundstein.composite.CompositeBuilder;
import kirchnerei.grundstein.composite.CompositeInit;
import org.apache.click.Context;
import org.apache.commons.lang.math.NumberUtils;

import java.util.List;

public class RequestBeanService implements CompositeInit {

	private BeanCopy beanCopy;

	@Override
	public void init(CompositeBuilder builder) {
		beanCopy = builder.getSingleton(BeanCopy.class);
	}

	public <T> T read(Context ctx, Class<T> type, List<String> properties) {
		T value = ClassUtils.createInstance(type);
		return read(ctx, value, properties);
	}

	public <T> T read(Context ctx, T target, List<String> properties) {
		for (String name : properties) {
			String paramValue = ctx.getRequestParameter(name);
			Class<?> propType = beanCopy.getWritePropertyClass(target, name);
			Object value = convert(propType, paramValue);
			beanCopy.copyProperty(target, name, value);
		}
		return target;
	}


	Object convert(Class<?> propType, String value) {
		if (propType == int.class || propType == Integer.class) {
			return NumberUtils.toInt(value, 0);
		}
		if (propType == long.class || propType == Long.class) {
			return NumberUtils.toLong(value, 0L);
		}
		if (propType == double.class || propType == Double.class) {
			return NumberUtils.toDouble(value, 0.0);
		}
		if (propType == float.class || propType == Float.class) {
			return NumberUtils.toFloat(value, 0.0f);
		}
		return value;
	}
}
