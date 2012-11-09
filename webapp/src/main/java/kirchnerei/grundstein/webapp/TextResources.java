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
package kirchnerei.grundstein.webapp;

import kirchnerei.grundstein.composite.CompositeBuilder;
import kirchnerei.grundstein.composite.CompositeInit;
import kirchnerei.grundstein.io.BulkInputStream;
import kirchnerei.grundstein.io.InputStreamFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TextResources implements CompositeInit {

	private final List<String> resources = new ArrayList<>();

	private InputStreamFactory factory;

	private int bufferSize = 1024 * 10; // 10Kb

	private String contentType;

	private String encoding;

	@Override
	public void init(CompositeBuilder builder) {
		factory = builder.getSingleton(InputStreamFactory.class);
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void addResource(String name) {
		resources.add(name);
	}

	public void writeTo(OutputStream output) throws IOException {
		Writer writer = new OutputStreamWriter(output, getEncoding());
		writeTo(writer);
	}

	public void writeTo(Writer writer) throws IOException {
		Reader reader = new InputStreamReader(openStream(), getEncoding());
		char[] buffer = new char[getBufferSize()];
		int length = 0;
		while ((length = reader.read(buffer)) != -1) {
			writer.write(buffer, 0, length);
		}
		reader.close();
	}

	private InputStream openStream() {
		BulkInputStream input = new BulkInputStream();
		for (String resourceName : resources) {
			input.add(factory.open(resourceName));
		}
		return input;
	}
}
