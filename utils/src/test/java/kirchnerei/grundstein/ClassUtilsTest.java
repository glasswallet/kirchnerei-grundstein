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
package kirchnerei.grundstein;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.AbstractList;
import java.util.List;

public class ClassUtilsTest {

	@Test(expected = NullPointerException.class)
	public void testCreateInstanceWithNull() {
		ClassUtils.createInstance(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateInstanceWithInterface() {
		ClassUtils.createInstance(List.class);
	}

	@Test(expected = ClassUtilException.class)
	public void testCreateInstanceWithAbstractClass() {
		ClassUtils.createInstance(AbstractList.class);
	}

	@Test(expected = ClassUtilException.class)
	public void testCreateInstanceAccessException() {
		ClassUtils.createInstance(ModifierPrivate.class);
	}

	@Test(expected = ClassUtilException.class)
	public void testCreateInstanceInstantiationException() {
		ClassUtils.createInstance(BeanWithParameter.class);
	}

	@Test
	public void testCreateInstanceWithStringBuffer() {
		StringBuffer sb = ClassUtils.createInstance(StringBuffer.class);
		assertNotNull(sb);
	}

	@Test(expected = ClassUtilException.class)
	public void testForNameClassNotFound() {
		ClassUtils.forName("doolittle.Dream");
	}

	@Test
	public void testForNameWithStringBuffer() {
		Class<?> type = ClassUtils.forName("java.lang.StringBuffer");
		assertNotNull(type);
	}

	@Test(expected = NullPointerException.class)
	public void testCastNullValue() {
		ClassUtils.cast(null, StringBuffer.class);
	}

	@Test(expected = NullPointerException.class)
	public void testCastNullNull() {
		Object value = new Object();
		ClassUtils.cast(value, null);
	}

	@Test(expected = ClassUtilException.class)
	public void testCastException() {
		Object value = new Object();
		ClassUtils.cast(value, StringBuffer.class);
	}

	@Test
	public void testCreateInstanceFromClassNameWithType() {
		NameView bean = ClassUtils.createInstance("kirchnerei.grundstein.ClassUtilsTest$NameBean",
			NameView.class);
		assertNotNull(bean);
	}

	@Test(expected = ClassUtilException.class)
	public void testCreateInstanceFromWrongClassName() {
		ClassUtils.createInstance("kirchnerei.grundstein.ClassUtilsTest.NameBean",
			StringBuffer.class);
	}

	@Test
	public void testCast() {
		Object value = new StringBuffer("test");
		StringBuffer sb = ClassUtils.cast(value, StringBuffer.class);
		assertNotNull(sb);
		assertEquals("test", sb.toString());
	}


	public static class ModifierPrivate {
		private ModifierPrivate() {}
	}

	public static class BeanWithParameter {
		public BeanWithParameter(int id){
		}
	}

	public static interface NameView {
		String getName();
	}

	public static class NameBean implements NameView {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
