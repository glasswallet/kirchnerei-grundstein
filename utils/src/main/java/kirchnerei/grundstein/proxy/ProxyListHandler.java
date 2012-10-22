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

import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProxyListHandler<T> extends ProxyBase implements Iterable<T> {

	public static <T> ProxyListHandler<T> createProxy(List<T> items, Class<T> type,
	                                      Map<String, ProxyFilter> prop2Filter)
	{
		if (type == null || items == null) {
			throw new IllegalArgumentException("parameters are null");
		}
		if (!type.isInterface()) {
			throw new IllegalArgumentException("proxy type is not a interface");
		}
		ClassLoader cl = type.getClassLoader();
		ProxyListHandler<T> handler = new ProxyListHandler<T>(items, prop2Filter);
		handler.proxy = Proxy.newProxyInstance(cl, new Class<?>[]{type}, handler);
		return handler;

	}

	private final List<T> items;

	private Object proxy;

	protected ProxyListHandler(List<T> items, Map<String, ProxyFilter> prop2Filter) {
		super(null, prop2Filter);
		this.items = items;
		this.proxy = proxy;
	}

	@Override
	public Iterator<T> iterator() {
		return new ProxyIterator<T>(items, this);
	}

	private class ProxyIterator<T> implements Iterator<T> {

		private final List<T> items;

		private final ProxyListHandler handler;

		private int index;

		ProxyIterator(List<T> items, ProxyListHandler handler) {
			this.items = items;
			this.handler = handler;
			this.index = (items != null && !items.isEmpty()) ? 0 : -1;
		}

		@Override
		public boolean hasNext() {
			return (index >= 0 && index < items.size());
		}

		@Override
		@SuppressWarnings("unchecked")
		public T next() {
			handler.setValue(items.get(index++));
			return (T) handler.proxy;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
