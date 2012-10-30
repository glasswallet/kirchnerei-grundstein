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
package kirchnerei.grundstein.click.util;

import kirchnerei.grundstein.ClassUtils;
import kirchnerei.grundstein.composite.CompositeBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class RequestConverterFactoryTest {

	private CompositeBuilder builder;

	private RequestConverterFactory factory;

	@Before
	public void setUp() {
		builder = new CompositeBuilder();
		builder.setup(Setup.class);
		factory = builder.getSingleton(RequestConverterFactory.class);
	}

	public void tearDown() {
		factory = null;
		builder.destroy();
		builder = null;
	}

	@Test
	public void testCheckContainsClassesOrNot() {
		assertTrue(factory.contains(BeanView.class));
		assertFalse(factory.contains(StringBuffer.class));
	}

	@Test
	public void testConvertClassBean() {
		Object bean = factory.convert(Long.valueOf("12"), BeanView.class);
		assertNotNull(bean);
		assertEquals(bean.getClass(), BeanView.class);
		BeanView b = ClassUtils.cast(bean, BeanView.class);
		assertNotNull(b);
		assertEquals(12, b.getId());
	}

	@Test
	public void testConvertInterfaceBean() {
		Object bean = factory.convert(Long.valueOf("12"), BeanData.class);
		assertNotNull(bean);
		assertEquals(bean.getClass(), BeanView.class);
		BeanData d = ClassUtils.cast(bean, BeanData.class);
		assertNotNull(d);
		assertEquals(12, d.getId());
	}

	@Test
	public void testWrongConvertClass() {
		assertNull(factory.convert(Long.valueOf("12"), Date.class));
	}

	@Test
	public void testConvertStringTypeInfoInterface() {
		Object bean = factory.convert("12", BeanData.class);
		assertNotNull(bean);
		assertEquals(bean.getClass(), BeanView.class);
		BeanData d = ClassUtils.cast(bean, BeanData.class);
		assertNotNull(d);
		assertEquals(12, d.getId());
	}
}
