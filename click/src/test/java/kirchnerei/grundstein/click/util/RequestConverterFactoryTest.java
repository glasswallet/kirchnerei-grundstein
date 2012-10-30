package kirchnerei.grundstein.click.util;

import kirchnerei.grundstein.ClassUtils;
import kirchnerei.grundstein.composite.CompositeBuilder;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class RequestConverterFactoryTest {

	private CompositeBuilder builder;

	@Before
	public void setUp() {
		builder = new CompositeBuilder();
		builder.setup(Setup.class);
	}

	public void tearDown() {
		builder.destroy();
		builder = null;
	}

	@Test
	public void testConvertType() {
		RequestConverterFactory factory = builder.getSingleton(RequestConverterFactory.class);

		assertTrue(factory.contains(BeanView.class));
		assertFalse(factory.contains(StringBuffer.class));

		Object bean = factory.convert(Long.valueOf("12"), BeanView.class);
		assertNotNull(bean);
		assertEquals(bean.getClass(), BeanView.class);
		BeanView b = ClassUtils.cast(bean, BeanView.class);
		assertNotNull(b);
		assertEquals(12, b.getId());

		assertNull(factory.convert(Long.valueOf("12"), Date.class));

		bean = factory.convert(Long.valueOf("12"), BeanData.class);
		assertNotNull(bean);
		assertEquals(bean.getClass(), BeanView.class);
		BeanData d = ClassUtils.cast(bean, BeanData.class);
		assertNotNull(b);
		assertEquals(12, b.getId());

		assertNotNull(factory.convert("12", BeanData.class));
	}
}
