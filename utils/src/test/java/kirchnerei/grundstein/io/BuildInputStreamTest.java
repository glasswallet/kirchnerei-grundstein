package kirchnerei.grundstein.io;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BuildInputStreamTest {

	@Test
	public void testBuildInputStream() throws IOException {
		List<InputStream> streams = new ArrayList<>();
		streams.add(openStream("BulkText1.txt"));
		streams.add(openStream("BulkText2.txt"));
		BulkInputStream input = new BulkInputStream(streams);
		Reader reader = new InputStreamReader(input);
		BufferedReader in = new BufferedReader(reader);
		String line;
		StringBuilder sb = new StringBuilder();
		while ((line = in.readLine()) != null) {
			sb.append(line).append("\n");
		}
		assertEquals("1\n2\n3\n4\n5\n6\n7\n8\n", sb.toString());
		in.close();
	}

	InputStream openStream(String name) {
		InputStream in = getClass().getResourceAsStream(name);
		assertNotNull(in);
		return in;
	}
}
