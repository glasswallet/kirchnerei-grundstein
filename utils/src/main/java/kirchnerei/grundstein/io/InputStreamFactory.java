package kirchnerei.grundstein.io;

import kirchnerei.grundstein.LogUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class InputStreamFactory {

	private static final Log log = LogFactory.getLog(InputStreamFactory.class);

	public static final String SEPARATOR = "://";

	private final Map<String, OpenerInputStream> openers = new HashMap<String, OpenerInputStream>();

	public InputStreamFactory() {
		addOpener("class", new ClassOpenerInputStream());
		addOpener("file", new FileOpenerInputStream());
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

	public final InputStream open(String resourceName, boolean withoutDelayed) {
		if (withoutDelayed) {
			try {
				return openInternal(resourceName);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return open(resourceName);
	}

	protected InputStream openInternal(String resourceName) throws IOException {
		int pos = resourceName.indexOf(SEPARATOR);
		InputStream input = null;
		if (pos > 0) {
			String name = resourceName.substring(0, pos);
			input = openInternal(name, resourceName);
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
				LogUtils.debug(log, "open stream from '%s' (message=%s)",
					resourceName, input != null ? "okay" : "failed");
			}
		}

		private boolean isOpenInput() {
			return input != null;
		}

		@Override
		public String toString() {
			return String.format("Delayed [name=%s, type=%s]",
				resourceName, (input != null) ? input : "null");
		}
	}
}
