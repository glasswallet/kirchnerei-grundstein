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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public class ProxyListHandler<T> extends ProxyBase implements Iterable<T>, List<T> {

	public static <T> List<T> createProxy(List<T> items, Class<T> type,
	                                      Map<String, ProxyFilter> prop2Filter)
	{
		if (type == null || items == null) {
			throw new IllegalArgumentException("parameters are null");
		}
		if (!type.isInterface()) {
			throw new IllegalArgumentException("proxy type is not a interface");
		}
		return new ProxyListHandler<T>(items, type, prop2Filter);
	}

	private final List<T> items;

	private final Object proxy;

	private final Class<T> type;

	protected ProxyListHandler(List<T> items, Class<T> type, Map<String, ProxyFilter> prop2Filter) {
		super(null, prop2Filter);
		this.items = Collections.unmodifiableList(items);
		this.type = type;
		ClassLoader cl = type.getClassLoader();
		this.proxy = Proxy.newProxyInstance(cl, new Class<?>[] {type}, this);
	}

	@Override
	public int size() {
		return items.size();
	}

	@Override
	public boolean isEmpty() {
		return items.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return items.contains(o);
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(T t) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return items.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(int index) {
		setValue(items.get(index));
		return (T) proxy;
	}

	@Override
	public T set(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object o) {
		return items.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return items.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return new ProxyIterator<T>(this, type);
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<T> iterator() {
		return new ProxyIterator<T>(this, type);
	}

	private static class ProxyIterator<T> implements ListIterator<T>, InvocationHandler {

		private final ProxyListHandler<T> handler;
		private final Object proxy;
		private T value;

		private int index;

		ProxyIterator(ProxyListHandler<T> handler, Class<T> type) {
			this.handler = handler;
			this.index = (handler.items != null && !handler.items.isEmpty()) ? 0 : -1;
			ClassLoader cl = type.getClassLoader();
			this.proxy = Proxy.newProxyInstance(cl, new Class<?>[] {type}, this);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return handler.invokeSource(value, proxy, method, args);
		}

		@Override
		public boolean hasNext() {
			return (index >= 0 && index < handler.items.size());
		}

		@Override
		@SuppressWarnings("unchecked")
		public T next() {
			value = handler.items.get(index++);
			return (T) proxy;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasPrevious() {
			return index > 0;
		}

		@Override
		@SuppressWarnings("unchecked")
		public T previous() {
			value = handler.items.get(--index);
			return (T) proxy;
		}

		@Override
		public int nextIndex() {
			return index + 1;
		}

		@Override
		public int previousIndex() {
			return index - 1;
		}

		@Override
		public void set(T t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(T t) {
			throw new UnsupportedOperationException();
		}
	}
}
