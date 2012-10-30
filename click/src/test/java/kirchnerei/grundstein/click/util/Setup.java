package kirchnerei.grundstein.click.util;

import org.apache.commons.lang.math.NumberUtils;

/**
 * Setup class for CompositeBuilder instance
 */
public class Setup {

	public static class requestConvertFactory extends RequestConverterFactory {
		{
			RequestConverter bean = new BeanRequestConverter();
			addConverter(BeanView.class, bean);
			addConverter(BeanData.class, bean);
		}

		private static class BeanRequestConverter implements RequestConverter {

			@Override
			public Object convert(Object value) {
				if (value instanceof String) {
					long id = NumberUtils.toLong((String) value, -1);
					if (id >= 0) {
						return create(id);
					}
				}
				if (value instanceof Long) {
					return create((Long) value);
				}
				return null;
			}

			BeanData create(long id) {
				BeanView b = new BeanView();
				b.setId(id);
				return b;
			}
		}
	}
}
