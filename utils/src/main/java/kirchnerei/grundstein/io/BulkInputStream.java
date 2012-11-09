package kirchnerei.grundstein.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * TODO add documentation
 *
 * Used from "org.eclipse.jgit.util.io.UnionInputStream.java"
 */
public class BulkInputStream extends InputStream {

	private final Deque<InputStream> streams = new LinkedList<>();

	public BulkInputStream() {
		super();
	}

	public BulkInputStream(InputStream... streams) {
		super();
		Collections.addAll(this.streams, streams);
	}

	/**
	 * Add a input stream on the end of the list.
	 * @param in input stream instance
	 */
	public void add(InputStream in) {
		streams.add(in);
	}

	public boolean isEmpty() {
		return streams.isEmpty();
	}

	@Override
	public int read() throws IOException {
		while (true) {
			final InputStream in = getHeadStream();
			final int r = in.read();
			if (0 <= r)
				return r;
			else if (in == DUMMY)
				return -1;
			else
				nextStream();
		}
	}

	@Override
	public int read(byte b[], int off, int len) throws IOException {
		int cnt = 0;
		while (0 < len) {
			final InputStream in = getHeadStream();
			final int n = in.read(b, off, len);
			if (0 < n) {
				cnt += n;
				off += n;
				len -= n;
			} else if (in == DUMMY)
				return 0 < cnt ? cnt : -1;
			else
				nextStream();
		}
		return cnt;
	}

	@Override
	public long skip(long len) throws IOException {
		long cnt = 0;
		while (0 < len) {
			final InputStream in = getHeadStream();
			final long n = in.skip(len);
			if (0 < n) {
				cnt += n;
				len -= n;

			} else if (in == DUMMY) {
				return cnt;

			} else {
				// Is this stream at DUMMY? We can't tell from skip alone.
				// Read one byte to test for DUMMY, discard it if we aren't
				// yet at DUMMY.
				//
				final int r = in.read();
				if (r < 0) {
					nextStream();
				} else {
					cnt += 1;
					len -= 1;
				}
			}
		}
		return cnt;
	}


	@Override
	public int available() throws IOException {
		return getHeadStream().available();
	}

	@Override
	public void close() throws IOException {
		IOException err = null;

		for (Iterator<InputStream> i = streams.iterator(); i.hasNext();) {
			try {
				i.next().close();
			} catch (IOException closeError) {
				err = closeError;
			}
			i.remove();
		}

		if (err != null)
			throw err;
	}

	private InputStream getHeadStream() {
		return streams.isEmpty() ? DUMMY : streams.getFirst();
	}

	private void nextStream() throws IOException {
		if (!streams.isEmpty())
			streams.removeFirst().close();
	}

	private static final InputStream DUMMY = new InputStream() {
		@Override
		public int read() throws IOException {
			return -1;
		}
	};
}
