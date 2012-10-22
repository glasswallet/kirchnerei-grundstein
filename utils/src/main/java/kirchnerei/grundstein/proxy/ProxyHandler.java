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
package kirchnerei.grundstein.proxy;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;


public class ProxyHandler extends ProxyBase {

	/**
	 * Create a proxy instance of the given Object value
	 */
	@SuppressWarnings("unchecked")
	public static <T> T createProxy(Object value, Class<T> type, Map<String, ProxyFilter> prop2Filter) {
		if (type == null || value == null) {
			throw new IllegalArgumentException("parameters are null");
		}
		if (!type.isInterface()) {
			throw new IllegalArgumentException("proxy type is not a interface");
		}
		if (value.getClass().isAssignableFrom(type)) {
			throw new IllegalArgumentException("value can cast into the interface type");
		}
		ClassLoader cl = type.getClassLoader();
		InvocationHandler handler = new ProxyHandler(value, prop2Filter);
		Object proxy = Proxy.newProxyInstance(cl, new Class<?>[] {type}, handler);
		return (T) proxy;
	}

	protected ProxyHandler(Object value, Map<String, ProxyFilter> prop2Filter) {
		super(value, prop2Filter);
	}
}
