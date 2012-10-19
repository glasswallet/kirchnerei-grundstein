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
package kirchnerei.grundstein.composite;

import kirchnerei.grundstein.ExceptionValues;

public class CompositeException extends RuntimeException {

	private static final long serialVersionUID = -7419616900713483329L;

	private final ExceptionValues values;

	public CompositeException() {
		super();
		this.values = new ExceptionValues();
	}

	public CompositeException(String message, Object... args) {
		super(message);
		this.values = new ExceptionValues(args);
	}

	public CompositeException(Throwable cause, String message, Object... args) {
		super(message, cause);
		this.values = new ExceptionValues(args);
	}

	public CompositeException(Throwable cause) {
		super(cause);
		this.values = new ExceptionValues();
	}

	@Override
	public String getMessage() {
		return values.getMessage(super.getMessage());
	}
}
