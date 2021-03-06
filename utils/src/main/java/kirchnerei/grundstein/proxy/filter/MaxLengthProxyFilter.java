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
package kirchnerei.grundstein.proxy.filter;

import kirchnerei.grundstein.proxy.InvokeDirection;
import kirchnerei.grundstein.proxy.ProxyFilter;

public class MaxLengthProxyFilter implements ProxyFilter {
	private final int maxLength;

	public MaxLengthProxyFilter(int maxLength) {
		super();
		if (maxLength <= 0) {
			throw new IllegalArgumentException(
				"parameter <maxLength> has wrong value (value must be greater zero)");
		}
		this.maxLength = maxLength;
	}

	public int getMaxLength() {
		return maxLength;
	}

	@Override
	public Object invoke(InvokeDirection dir, Class<?> type, Object value) {
		if (dir == InvokeDirection.READ) {
			return value;
		}
		// write
		if (type == String.class) {
			String s = (String) value;
			if (s != null && s.length() > maxLength) {
				return s.substring(0, maxLength);
			}
			return value;
		}
		return value;
	}
}
