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
import java.util.HashMap;
import java.util.Map;

public class ProxyBase implements InvocationHandler {


	private Object value;

	private final Map<String, ProxyFilter> prop2Filter;

	protected ProxyBase(Object value, Map<String, ProxyFilter> prop2Filter) {
		this.value = value;
		this.prop2Filter = (prop2Filter != null) ? prop2Filter : new HashMap<String, ProxyFilter>();
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return invokeSource(getValue(), proxy, method, args);
	}

	Object getValue() {
		return value;
	}

	void setValue(Object value) {
		this.value = value;
	}

	protected final Object invokeSource(Object source, Object proxy, Method method, Object[] args)
		throws Throwable
	{
		String methodName = method.getName();
		if (StringUtils.isEmpty(methodName)) {
			// strange thing (method without name??)
			return method.invoke(source, args);
		}
		InvokeDirection dir = calculateInvokeDirection(methodName);
		if (dir == null) {
			// not getter or setter method
			return method.invoke(source, args);
		}
		String property = calculatePropertyName(methodName);
		if (dir == InvokeDirection.READ) {
			Object result = method.invoke(source, args);
			Class<?> type = method.getReturnType();
			return invokeRead(type, property, result);
		}
		if (dir == InvokeDirection.WRITE &&
			method.getParameterTypes() != null && method.getParameterTypes().length > 0)
		{
			Class<?> type = method.getParameterTypes()[0];
			args[0] = invokeWrite(type, property, args[0]);
			return method.invoke(source, args);
		}
		return method.invoke(source, args);

	}

	protected Object invokeRead(Class<?> type, String property, Object value) {
		if (prop2Filter.containsKey(property)) {
			ProxyFilter filter = prop2Filter.get(property);
			return filter.invoke(InvokeDirection.READ, type, value);
		}
		return value;
	}

	protected Object invokeWrite(Class<?> type, String property, Object value) {
		if (prop2Filter.containsKey(property)) {
			ProxyFilter filter = prop2Filter.get(property);
			return filter.invoke(InvokeDirection.WRITE, type, value);
		}
		return value;
	}

	static String calculatePropertyName(String methodName) {
		if (methodName.length() < 4) {
			return null;
		}
		int index = 3;
		if (methodName.startsWith("is")) {
			index = 2;
		}
		return Character.toLowerCase(methodName.charAt(index)) + methodName.substring(index + 1);
	}

	static InvokeDirection calculateInvokeDirection(String methodName) {
		if (methodName.startsWith("get") || methodName.startsWith("is")) {
			return InvokeDirection.READ;
		} else if (methodName.startsWith("set")) {
			return InvokeDirection.WRITE;
		}
		return null;
	}
}
