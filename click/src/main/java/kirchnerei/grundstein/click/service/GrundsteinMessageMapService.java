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
package kirchnerei.grundstein.click.service;

import kirchnerei.grundstein.LogUtils;
import kirchnerei.grundstein.text.CharIterator;
import org.apache.click.service.DefaultMessagesMapService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Configure in the click.xml
 *
 * <pre><code>
 *     &lt;click-app charset="UTF-8"&gt;
 *         &lt;messages-map-service classname="kirchnerei.grundstein.click.service.GrundsteinMessageMapService"/&gt;
 *     &lt;/click-app&gt;
 * </code></pre>
 */
public class GrundsteinMessageMapService extends DefaultMessagesMapService {

	private static final Log log = LogFactory.getLog(GrundsteinMessageMapService.class);

	private static final Map<String, String> nameMap = new HashMap<String, String>();

	@Override
	public Map<String, String> createMessagesMap(Class<?> baseClass, String globalResource, Locale locale) {
		Map<String, String> messageMap = super.createMessagesMap(baseClass, globalResource, locale);
		return new NamedMap(messageMap);
	}

	private static class NamedMap implements Map<String, String> {

		private final Map<String, String> parentMap;

		NamedMap(Map<String, String> parentMap) {
			this.parentMap = parentMap;
		}


		@Override
		public int size() {
			return parentMap.size();
		}

		@Override
		public boolean isEmpty() {
			return parentMap.isEmpty();
		}

		@Override
		public boolean containsKey(Object key) {
			return key != null && parentMap.containsKey(getNamedKey(key));
		}

		@Override
		public boolean containsValue(Object value) {
			return parentMap.containsValue(value);
		}

		@Override
		public String get(Object key) {
			if (key == null || StringUtils.isEmpty(key.toString())) {
				return "";
			}
			if ("-".equals(key.toString())) {
				return "-";
			}
			return parentMap.get(getNamedKey(key));
		}

		@Override
		public String put(String key, String value) {
			return parentMap.put(key, value);
		}

		@Override
		public String remove(Object key) {
			if (key == null) {
				return "";
			}
			return parentMap.remove(getNamedKey(key));
		}

		@Override
		public void putAll(Map<? extends String, ? extends String> m) {
			parentMap.putAll(m);
		}

		@Override
		public void clear() {
			parentMap.clear();
		}

		@Override
		public Set<String> keySet() {
			return parentMap.keySet();
		}

		@Override
		public Collection<String> values() {
			return parentMap.values();
		}

		@Override
		public Set<Entry<String, String>> entrySet() {
			return parentMap.entrySet();
		}

		@Override
		public boolean equals(Object o) {
			return parentMap.equals(o);
		}

		@Override
		public int hashCode() {
			return parentMap.hashCode();
		}
	}

	public static String getNamedKey(String key) {
		if (!nameMap.containsKey(key)) {
			String mappedKey = buildNamedKey(key);
			nameMap.put(key, mappedKey);
			LogUtils.trace(log, "mapped key '%s' => '%s'", key, mappedKey);
		}
		return nameMap.get(key);
	}

	static String getNamedKey(Object key) {
		if (key == null) {
			return "";
		}
		return getNamedKey(key.toString());
	}

	static String buildNamedKey(String name) {
		StringBuilder sb = new StringBuilder(name.length() + 4);
		CharIterator it = new CharIterator(name);
		while (it.hasNext()) {
			char ch = it.next();
			if (Character.isUpperCase(ch)) {
				sb.append('.');
				ch = Character.toLowerCase(ch);
			}
			sb.append(ch);
		}
		return sb.toString();
	}
}
