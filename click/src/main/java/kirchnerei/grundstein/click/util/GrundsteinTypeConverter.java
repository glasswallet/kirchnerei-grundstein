package kirchnerei.grundstein.click.util;

import kirchnerei.grundstein.composite.CompositeBuilder;
import kirchnerei.grundstein.composite.CompositeInit;
import org.apache.click.util.RequestTypeConverter;

/**
 *
 * <p>
 *     InitParameter for the GrundsteinClickServlet:
 * </p>
 *
 *
 * <pre><code>
 *     &lt;init-param&gt;
 *         &lt;param-name&gt;type-converter-class&lt;/param-name&gt;
 *         &lt;param-value&gt;kirchnerei.grundstein.click.util.GrundsteinTypeConverter&lt;/param-value&gt;
 *     &lt;/init-param&gt;
 * </code></pre>
 *
 */
public class GrundsteinTypeConverter extends RequestTypeConverter implements CompositeInit {

	private RequestConverterFactory converterFactory;

	@Override
	protected Object convertValue(Object value, Class<?> toType) {
		if (converterFactory.contains(toType)) {
			return converterFactory.convert(value, toType);
		}
		return super.convertValue(value, toType);
	}

	@Override
	public void init(CompositeBuilder builder) {
		converterFactory = builder.getSingleton(RequestConverterFactory.class);
	}
}
