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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FilterContainer implements ProxyFilter {

	private final List<ProxyFilter> filters = new ArrayList<ProxyFilter>();

	public FilterContainer(Collection<ProxyFilter> filters) {
		this.filters.addAll(filters);
	}

	public FilterContainer(ProxyFilter... filters) {
		this(Arrays.asList(filters));
	}

	@Override
	public Object invoke(InvokeDirection dir, Class<?> type, Object value) {
		Object temp = value;
		for (ProxyFilter f : filters) {
			temp = f.invoke(dir, type, temp);
		}
		return temp;
	}
}
