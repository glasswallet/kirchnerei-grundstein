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

import static org.junit.Assert.*;

import kirchnerei.grundstein.proxy.filter.StringNotEmptyProxyFilter;
import org.junit.Test;

import java.util.*;

public class ProxyListHandlerTest {


	@Test
	public void testStandard() {
		List<BeanData> items = new ArrayList<BeanData>(
			Arrays.asList(new BeanView(1, "Test 1"), new BeanView(2, null)));
		Map<String, ProxyFilter> filters = new HashMap<String, ProxyFilter>();
		filters.put("text", new StringNotEmptyProxyFilter());
		filters.put("id", new IdMultiplicationProxyFilter());

		List<BeanData> list = ProxyListHandler.createProxy(items, BeanData.class, filters);
		assertNotNull(list);
		for (BeanData data : list) {
			assertNotNull(data);
			assertTrue(data.getId() > 1000);
			assertNotNull(data.getText());
		}
		assertNotNull(list.get(0));
		assertEquals(1001, list.get(0).getId());
		assertEquals("Test 1", list.get(0).getText());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParameterWithNull() {
		ProxyListHandler.createProxy(null, null, null);
	}

	@Test
	public void testWithoutFilter() {
		BeanView b = new BeanView(1, "Test 1");
		List<BeanData> items = new ArrayList<BeanData>(
			Arrays.asList(b, new BeanView(2, null)));
		List<BeanData> list = ProxyListHandler.createProxy(items, BeanData.class, null);
		assertNotNull(list);
		assertEquals(2, list.size());
		assertFalse(list.isEmpty());
		assertTrue(list.contains(b));
		assertTrue(list.indexOf(b) >= 0);
		assertTrue(list.lastIndexOf(b) >= 0);

		for (BeanData data : list) {
			assertNotNull(data);
			assertTrue(data.getId() > 0);
		}
		assertNotNull(list.get(0));
		assertEquals(1, list.get(0).getId());
		assertEquals("Test 1", list.get(0).getText());
		assertNotNull(list.get(1));
		assertEquals(2, list.get(1).getId());
		assertNull(list.get(1).getText());
	}

	public static class IdMultiplicationProxyFilter extends ProxyFilter {

		@Override
		protected Object convert(InvokeDirection dir, Class<?> type, Object value) {
			if (type == long.class || type == Long.class) {
				long id = Long.valueOf((Long) value);
				return (Long) id + 1000l;
			}
			return value;
		}
	}

	public static interface BeanData {

		long getId();

		String getText();

		void setText(String text);
	}

	public static class BeanView implements  BeanData {

		private long id;

		private String text;

		public BeanView(long id, String text) {
			this.id = id;
			this.text = text;
		}

		@Override
		public long getId() {
			return id;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
	}
}
