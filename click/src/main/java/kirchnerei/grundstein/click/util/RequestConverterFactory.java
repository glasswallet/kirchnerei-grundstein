package kirchnerei.grundstein.click.util;

import kirchnerei.grundstein.composite.CompositeBuilder;
import kirchnerei.grundstein.composite.CompositeException;
import kirchnerei.grundstein.composite.CompositeInit;

import java.util.HashMap;
import java.util.Map;

public class RequestConverterFactory implements CompositeInit {


	private final Map<Class<?>, RequestConverter> class2Converter =
		new HashMap<Class<?>, RequestConverter>();

	public Object convert(Object value, Class<?> toClass) {
		if (contains(toClass)) {
			RequestConverter c = class2Converter.get(toClass);
			return c.convert(value);
		}
		return null;
	}

	public boolean contains(Class<?> toClass) {
		return class2Converter.containsKey(toClass);
	}

	/**
	 * Add a request converter to the map. <b>Note</b>: call before the method #init()
	 * is called (e.x: in the constructor).
	 *
	 * @param toType
	 * @param reqConverter
	 */
	protected void addConverter(Class<?> toType, RequestConverter reqConverter) {
		if (contains(toType)) {
			throw new CompositeException(
				"request converter for '%s' is setting already", toType.getSimpleName());
		}
		class2Converter.put(toType, reqConverter);
	}

	@Override
	public void init(CompositeBuilder builder) {
		for (Map.Entry<Class<?>, RequestConverter> entry : class2Converter.entrySet()) {
			builder.init(entry.getValue());
		}
	}
}
