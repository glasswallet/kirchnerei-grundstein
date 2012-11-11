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

import kirchnerei.grundstein.LogUtils;
import kirchnerei.grundstein.composite.CompositeBuilder;
import kirchnerei.grundstein.composite.CompositeInit;
import kirchnerei.grundstein.io.InputStreamFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TextResources implements CompositeInit {

	private static final Log log = LogFactory.getLog(TextResources.class);

	private final List<String> resources = new ArrayList<String>();

	private InputStreamFactory factory;

	private String contentType;

	private String encoding;

	@Override
	public void init(CompositeBuilder builder) {
		factory = builder.getSingleton(InputStreamFactory.class);
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
		if (StringUtils.isEmpty(getEncoding())) {
			throw new IOException("missing the property 'encoding'");
		}
		LogUtils.debug(log, "start writing with encoding='%s'", getEncoding());
		for (String resourceName : resources) {
			LogUtils.debug(log, "write resource '%s'", resourceName);
			InputStream input = factory.open(resourceName, false);
			IOUtils.copy(input, writer, getEncoding());
			IOUtils.closeQuietly(input);
			writer.flush();
		}
		LogUtils.debug(log, "write text resource");
	}
}
