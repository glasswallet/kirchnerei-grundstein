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
