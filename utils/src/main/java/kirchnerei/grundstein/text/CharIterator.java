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

package kirchnerei.grundstein.text;

public class CharIterator {

	private final CharSequence text;

	private int index = 0;

	public CharIterator(CharSequence text) {
		super();
		this.text = text;
	}

	public boolean hasNext() {
		return text != null && index < text.length();
	}

	public char next() {
		return text.charAt(index++);
	}

	public char peek() {
		return text.charAt(index);
	}

	public boolean isUpperNext() {
		return Character.isUpperCase(peek());
	}

	public boolean isLowerNext() {
		return Character.isLowerCase(peek());
	}

	public boolean isDigitNext() {
		return Character.isDigit(peek());
	}}
