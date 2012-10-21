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

import static org.junit.Assert.*;

import org.junit.Test;

public class NumberUtilsTest {

	@Test
	public void testLongIsNull() {
		Long n = null;
		assertNotNull(NumberUtils.isNull(n));
	}

	@Test
	public void testDoubleIsNull() {
		Double n = null;
		assertNotNull(NumberUtils.isNull(n));
	}


	@Test
	public void testFloatIsNull() {
		Float n = null;
		assertNotNull(NumberUtils.isNull(n));
	}

	@Test
	public void testLongIsEmpty1() {
		Long n = 0L;
		assertTrue(NumberUtils.isEmpty(n));
	}

	@Test
	public void testLongIsEmpty2() {
		Long n = -1L;
		assertTrue(NumberUtils.isEmpty(n));
	}

	@Test
	public void testDoubleIsEmpty1() {
		Double n = 0D;
		assertTrue(NumberUtils.isEmpty(n));
	}

	@Test
	public void testDoubleIsEmpty2() {
		Double n = -1.0D;
		assertTrue(NumberUtils.isEmpty(n));
	}

	@Test
	public void testFloatIsEmpty1() {
		Float n = 0.0f;
		assertTrue(NumberUtils.isEmpty(n));
	}

	@Test
	public void testFloatIsEmpty2() {
		Float n = -0.2f;
		assertTrue(NumberUtils.isEmpty(n));
	}

	@Test
	public void testCompareLongNull1() {
		Long n1 = null;
		Long n2 = null;
		assertTrue(NumberUtils.compare(n1, n2));
	}

	@Test
	public void testCompareLongNull2() {
		Long n1 = null;
		Long n2 = 1L;
		assertFalse(NumberUtils.compare(n1, n2));
	}

	@Test
	public void testCompareLongNull3() {
		Long n1 = -1L;
		Long n2 = null;
		assertFalse(NumberUtils.compare(n1, n2));
	}

	@Test
	public void testCompareLongEqual() {
		Long n1 = Long.valueOf(3L);
		Long n2 = 3L;
		assertTrue(NumberUtils.compare(n1, n2));
	}
}
