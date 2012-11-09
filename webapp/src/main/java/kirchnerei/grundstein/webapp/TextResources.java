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

	public void addResource(String name) {
		resources.add(name);
	}

	public void writeTo(OutputStream output, String encoding) throws IOException {
		Writer writer = new OutputStreamWriter(output, encoding);
		writeTo(writer, encoding);
	}

	public void writeTo(Writer writer, String encoding) throws IOException {
		Reader reader = new InputStreamReader(openStream(), encoding);
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
