package kirchnerei.grundstein.proxy;

import static org.junit.Assert.*;

import kirchnerei.grundstein.proxy.filter.MaxLengthProxyFilter;
import kirchnerei.grundstein.proxy.filter.StringNotEmptyProxyFilter;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ProxyHandlerTextTest {


	@Test
	public void testMaxLength() {
		Map<String, ProxyFilter> filters = new HashMap<String, ProxyFilter>();
		filters.put("text", new MaxLengthProxyFilter(10));

		BeanData data = new BeanView("Das ist");
		BeanData proxy = ProxyHandler.createProxy(data, BeanData.class, filters);
		assertNotNull(proxy);
		assertNotNull(proxy.getText());
		assertEquals("Das ist", proxy.getText());
		assertEquals(data.getText(), proxy.getText());

		proxy.setText("Hausbaumart");
		assertNotNull(proxy.getText());
		assertEquals("Hausbaumar", proxy.getText());
		assertEquals(data.getText(), proxy.getText());
	}

	@Test
	public void testNotEmpty() {
		Map<String, ProxyFilter> filters = new HashMap<String, ProxyFilter>();
		filters.put("text", new StringNotEmptyProxyFilter("abc"));

		BeanData data = new BeanView(null);
		BeanData proxy = ProxyHandler.createProxy(data, BeanData.class, filters);
		assertNotNull(proxy);
		assertNotNull(proxy.getText());
		assertEquals("abc", proxy.getText());
		assertNotSame(data.getText(), proxy.getText());

		proxy.setText("Hurra");
		assertNotNull(proxy.getText());
		assertEquals("Hurra", proxy.getText());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWithNullParameter() {
		ProxyHandler.createProxy(null, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWithoutInterface() {
		BeanView view = new BeanView("Test");
		BeanView proxy = ProxyHandler.createProxy(view, BeanView.class, null);
	}


	@Test
	public void testWithoutFilter() {
		BeanData view = new BeanView("Test");
		BeanData proxy = ProxyHandler.createProxy(view, BeanData.class, null);
		assertNotNull(proxy);
		assertNotNull(proxy.getText());
		assertEquals("Test", proxy.getText());
	}


	public static interface BeanData {

		String getText();

		void setText(String text);
	}

	public static class BeanView implements  BeanData {

		private String text;

		public BeanView(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
	}
}
