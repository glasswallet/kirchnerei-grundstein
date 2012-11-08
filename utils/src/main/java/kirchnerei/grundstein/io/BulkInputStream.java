package kirchnerei.grundstein.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO add documentation
 */
public class BulkInputStream extends InputStream {

	private Deque<InputStream> streams;

	public BulkInputStream(List<InputStream> streams) {
		super();
		this.streams = new LinkedList<>(streams);
	}

	public BulkInputStream(InputStream... streams) {
		super();
		this.streams = new LinkedList<>(Arrays.asList(streams));
	}

	@Override
	public int read() throws IOException {
		int result = -1;
		while (!streams.isEmpty() && (result = streams.getFirst().read()) == -1) {
			nextStream();
		}
		return result;
	}

	@Override
	public int read(byte b[], int off, int len) throws IOException {
		int result = -1;
		while (!streams.isEmpty() && (result = streams.getFirst().read(b, off, len)) == -1) {
			nextStream();
		}
		return result;
	}

	@Override
	public long skip(long n) throws IOException {
		long skipped = 0L;
		while (skipped < n && !streams.isEmpty()) {
			long thisSkip = streams.getFirst().skip(n - skipped);
			if (thisSkip > 0)
				skipped += thisSkip;
			else
				nextStream();
		}
		return skipped;
	}


	@Override
	public int available() throws IOException {
		return streams.isEmpty() ? 0 : streams.getFirst().available();
	}

	@Override
	public void close() throws IOException {
		while (!streams.isEmpty()) {
			nextStream();
		}
	}


	private void nextStream() throws IOException {
		streams.removeFirst().close();
	}
}
