package kirchnerei.grundstein.proxy;

import static org.junit.Assert.*;

import kirchnerei.grundstein.proxy.MaxLengthProxyFilter;
import kirchnerei.grundstein.proxy.ProxyHandler;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ProxyHandlerTextTest {


	@Test
	public void test() {
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
