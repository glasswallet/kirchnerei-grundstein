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

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class BeanCopyTest {

	private BeanCopy beanCopy = new BeanCopy();

	@Test
	public void testSimpleCopy() {
		Bean bean1 = new Bean(4711L, "Test", true, 2.34);
		Bean bean2 = beanCopy.copy(bean1, Bean.class,
			Arrays.asList("id", "title", "active", "amount"));
		assertNotNull(bean2);
		assertTrue(bean1 != bean2);
		assertEquals(bean1.getId(), bean2.getId());
		assertEquals(bean1.getTitle(), bean2.getTitle());
		assertEquals(bean1.isActive(), bean2.isActive());
		assertEquals(bean1.getAmount(), bean2.getAmount(), 0.01);
	}

	@Test
	public void testReadType() {
		Bean bean = new Bean(4712L, "Test ReadType", true, 2.45);
		Class<?> type = beanCopy.getReadPropertyClass(bean, "amount");
		assertEquals(double.class, type);
	}

	@Test(expected = BeanCopyException.class)
	public void testReadTypeUnknownProperty() {
		Bean bean = new Bean(4712L, "Test ReadType", true, 2.45);
		beanCopy.getReadPropertyClass(bean, "text");
	}

	@Test
	public void testWriteType() {
		Bean bean = new Bean(4712L, "Test ReadType", true, 2.45);
		Class<?> type = beanCopy.getWritePropertyClass(bean, "amount");
		assertEquals(double.class, type);
	}

	@Test(expected = BeanCopyException.class)
	public void testWriteTypeUnknownProperty() {
		Bean bean = new Bean(4712L, "Test ReadType", true, 2.45);
		beanCopy.getWritePropertyClass(bean, "text");
	}

	@Test(expected = BeanCopyException.class)
	public void testReadUnknownProperty() {
		Bean bean = new Bean(4712L, "Test ReadType", true, 2.45);
		beanCopy.readProperty(bean, "text");
	}

	@Test(expected = BeanCopyException.class)
	public void testWriteUnknownProperty() {
		Bean bean = new Bean(4712L, "Test ReadType", true, 2.45);
		beanCopy.copyProperty(bean, "text", "hallo");
	}

	@Test
	public void testReadParameterDelivery() {
		final Map<String, String> params = new HashMap<>();
		params.put("id", "4711");
		params.put("title", "Parameter Delivery");
		params.put("active", "true");
		params.put("amount", "13.99");

		final ParameterDelivery pd = new ParameterDelivery() {
			@Override
			public String getParameter(String name) {
				return params.get(name);
			}
		};

		Bean bean = beanCopy.read(pd, Arrays.asList("id", "title", "active", "amount"), Bean.class);
		assertNotNull(bean);
		assertEquals(4711, bean.getId());
		assertEquals("Parameter Delivery", bean.getTitle());
		assertEquals(13.99, bean.getAmount(), 0.01);
		assertTrue(bean.isActive());
	}


	public static class Bean {

		private long id;

		private String title;

		private boolean active;

		private double amount;

		public Bean() { }

		public Bean(long id, String title, boolean active, double amount) {
			this.id = id;
			this.title = title;
			this.active = active;
			this.amount = amount;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		public double getAmount() {
			return amount;
		}

		public void setAmount(double amount) {
			this.amount = amount;
		}
	}
}
