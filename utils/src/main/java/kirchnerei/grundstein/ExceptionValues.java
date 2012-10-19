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
package kirchnerei.grundstein;

public class ExceptionValues {

	private final Object[] values;

	public ExceptionValues() {
		this.values = null;
	}

	public ExceptionValues(Object... values) {
		this.values = values;
	}

	public boolean hasValues() {
		return values != null && values.length > 0;
	}

	public String getMessage(String message) {
		if (hasValues()) {
			try {
				return String.format(message, values);
			} catch (Exception e) {
				return "??" + message + "??";
			}
		}
		return message;
	}
}
