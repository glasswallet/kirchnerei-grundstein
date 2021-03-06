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
package kirchnerei.grundstein.io;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class InputStreamFactoryTest {

	private InputStreamFactory factory = new InputStreamFactory();

	public void setUp() {
		assertNotNull(factory);
	}

	/**
	 * resource is in the same package as the factory class
	 * @throws IOException
	 */
	@Test
	public void testInputStreamClassPath() throws IOException {
		InputStream input = factory.open("class://BulkText1.txt");
		readAndVerify(input);
	}

	/**
	 * resource with the complete package names with the leading slash
	 * @throws IOException
	 */
	@Test
	public void testInputStreamClassCompletePath1() throws IOException {
		InputStream input = factory.open("class:///kirchnerei/grundstein/io/BulkText1.txt");
		readAndVerify(input);
	}

	/**
	 * resource with the complete package names without the leading slash
	 * @throws IOException
	 */
	@Test
	public void testInputStreamClassCompletePath2() throws IOException {
		InputStream input = factory.open("class://kirchnerei/grundstein/io/BulkText1.txt");
		readAndVerify(input);
	}

	/**
	 * resource as file
	 * @throws IOException
	 */
	@Test
	public void testInputStreamFilePath() throws IOException {
		InputStream input = factory.open("file://utils/src/test/resources/kirchnerei/grundstein/io/BulkText1.txt");
		readAndVerify(input);
	}

	@Test(expected = NullPointerException.class)
	public void testInputNullName() {
		factory.open(null);
	}

	@Test(expected = NullPointerException.class)
	public void testInputEmptyName() {
		factory.open("");
	}

	@Test(expected = IOException.class)
	public void testInputUnknownProtocolAndName() throws Exception {
		InputStream input = factory.open("test://blabla.txt");
		input.read();
	}

	@Test(expected = IOException.class)
	public void testInputUnknownName() throws Exception {
		InputStream input = factory.open("class://blabla.txt");
		input.read();
	}

	@Test
	public void testInputHttpFile() throws Exception {
		InputStream input = factory.open("http://www.google.de", false);
		assertNotNull(input);
		String content = IOUtils.toString(input);
		input.close();
		assertNotNull(content);
		assertTrue(content.contains("Google"));
	}

	private void readAndVerify(InputStream input) throws IOException {
		assertNotNull(input);
		Reader reader = new InputStreamReader(input);
		BufferedReader in = new BufferedReader(reader);
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = in.readLine()) != null) {
			sb.append(line).append("\n");
		}
		in.close();
		assertEquals("1\n2\n3\n4\n", sb.toString());
	}
}
