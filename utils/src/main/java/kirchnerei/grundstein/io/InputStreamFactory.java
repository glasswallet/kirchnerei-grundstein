package kirchnerei.grundstein.io;

import org.apache.commons.lang.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class InputStreamFactory {

	public static final String SEPARATOR = "://";

	private static final int SEP_LENGTH = SEPARATOR.length();

	private final Map<String, OpenerInputStream> openers = new HashMap<>();

	public InputStreamFactory() {
		addOpener("class", new ClassOpenerInputStream());
		addOpener("file", new ClassOpenerInputStream());
		addOpener("http", new HttpOpenerInputStream());
	}

	public void addOpener(String name, OpenerInputStream opener) {
		openers.put(name, opener);
	}

	public final InputStream open(String resourceName) {
		if (StringUtils.isEmpty(resourceName)) {
			throw new NullPointerException("missing parameter 'resourceName'");
		}
		return new DelayedInputStream(resourceName, this);
	}

	protected InputStream openInternal(String resourceName) throws IOException {
		int pos = resourceName.indexOf(SEPARATOR);
		InputStream input = null;
		if (pos > 0) {
			String name = resourceName.substring(0, pos);
			String resName = resourceName.substring(pos + SEP_LENGTH);
			input = openInternal(name, resName);
		}
		if (pos <= 0 && input == null) {
			input = defaultOpen(resourceName);
		}
		if (input != null) {
			return input;
		}
		throw new IOException("unknown resource '" + resourceName + "'");
	}

	protected InputStream openInternal(String name, String resourceName) throws IOException {
		if (openers.containsKey(name)) {
			OpenerInputStream ois = openers.get(name);
			return ois.open(resourceName);
		}
		return defaultOpen(resourceName);
	}

	private InputStream defaultOpen(String resourceName) throws IOException {
		InputStream input = getClass().getResourceAsStream(resourceName);
		if (input == null) {
			input = getClass().getClassLoader().getResourceAsStream(resourceName);
		}
		if (input == null) {
			input = new FileInputStream(resourceName);
		}
		return input;
	}


	public static interface OpenerInputStream {

		InputStream open(String resourceName) throws IOException;
	}

	private static class DelayedInputStream extends InputStream {

		private String resourceName;
		private InputStreamFactory factory;
		private InputStream input = null;

		DelayedInputStream(String resourceName, InputStreamFactory factory) {
			this.resourceName = resourceName;
			this.factory = factory;
		}

		@Override
		public int read() throws IOException {
			checkInput();
			return input.read();
		}

		@Override
		public int read(byte[] b) throws IOException {
			checkInput();
			return input.read(b);
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			checkInput();
			return input.read(b, off, len);
		}

		@Override
		public long skip(long n) throws IOException {
			checkInput();
			return input.skip(n);
		}

		@Override
		public int available() throws IOException {
			checkInput();
			return input.available();
		}

		@Override
		public void close() throws IOException {
			if (isOpenInput()) {
				input.close();
			}
		}

		@Override
		public synchronized void mark(int readLimit) {
			try {
				checkInput();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			input.mark(readLimit);
		}

		@Override
		public synchronized void reset() throws IOException {
			checkInput();
			input.reset();
		}

		@Override
		public boolean markSupported() {
			try {
				checkInput();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return input.markSupported();
		}

		private void checkInput() throws IOException {
			if (!isOpenInput()) {
				input = factory.openInternal(resourceName);
			}
		}

		private boolean isOpenInput() {
			return input != null;
		}
	}
}
