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
package kirchnerei.grundstein.composite;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CompositeBuilderTest {

	private CompositeBuilder builder = null;

	@Before
	public void setUp() {
		assertNull(builder);
		builder = new CompositeBuilder();
		builder.setup(Setup.class);
	}

	@After
	public void tearDown() {
		builder.destroy();
		builder = null;
	}

	@Test
	public void testSingletonAndEquals() {
		Bean b1 = builder.getSingleton(Bean.class);
		Bean b2 = builder.getSingleton(Bean.class);
		assertNotNull(b1);
		assertNotNull(b2);
		assertEquals(b1, b2);
	}

	@Test
	public void testSingletonAndSameClass() {
		Bean b1 = builder.getSingleton(Bean.class);
		assertNotNull(b1);
		assertTrue(b1.getClass() == Setup.Bean200.class);
	}

	@Test
	public void testSingletonAndContains() {
		TestBean tb = builder.getSingleton(TestBean.class);
		assertNotNull(tb);
		assertNotNull(tb.getBean());
		assertEquals("Bean200", tb.getBean().getName());

		Bean b1 = builder.getSingleton(Bean.class);
		assertNotNull(b1);
		assertEquals(b1, tb.getBean());
	}

	@Test
	public void testAddSingletonToPool() {
		Bean b = new Bean() {
			{
				setName("Bean30");
				setAmount(30);
			}
		};
		builder.addSingleton(b, Bean.class);

		TestBean tb = builder.getSingleton(TestBean.class);
		assertNotNull(tb);
		assertNotNull(tb.getBean());
		assertEquals("Bean30", tb.getBean().getName());
		assertEquals(b, tb.getBean());
	}

	@Test(expected=NullPointerException.class)
	public void testAddSingletonWithNull() {
		builder.addSingleton(null, Bean.class);
	}

	@Test(expected=CompositeException.class)
	public void testAddSingletonWithWrongType() {
		Bean b = new Bean() {
			{
				setName("Bean99");
				setAmount(99);
			}
		};
		builder.addSingleton(b, TestBean.class);
	}

	@Test
	public void testNewInstance() {
		TestBean tb1 = builder.newInstance(TestBean.class);
		assertNotNull(tb1);
		assertNotNull(tb1.getBean());
		TestBean tb2 = builder.newInstance(TestBean.class);
		assertNotNull(tb2);
		assertNotNull(tb2.getBean());

		assertNotSame(tb1, tb2);
		assertEquals(tb1.getBean(), tb2.getBean());
	}

	@Test
	public void testSingletonSuperType() {
		Bean b1 = builder.getSingleton(Setup.Bean200.class, Bean.class);
		assertNotNull(b1);
		assertEquals("Bean200", b1.getName());
	}

	/**
	 * The Setup class of this test case
	 */
	public static class Setup {

		public static class Bean200 extends Bean implements CompositeCreate {
			public Bean200() {
				super();
				setAmount(200);
			}

			@Override
			public void create(CompositeBuilder builder) {
				setName("Bean200");
			}
		}
	}

	public static class Bean {

		private String name;

		private int amount;

		public String getName() {
			return name;
		}

		public int getAmount() {
			return amount;
		}

		protected void setName(String name) {
			this.name = name;
		}

		protected void setAmount(int amount) {
			this.amount = amount;
		}
	}

	public static class TestBean implements CompositeInit {

		private Bean bean = null;

		@Override
		public void init(CompositeBuilder builder) {
			bean = builder.getSingleton(Bean.class);
		}

		/**
		 * @return the bean
		 */
		public Bean getBean() {
			return bean;
		}
	}
}
