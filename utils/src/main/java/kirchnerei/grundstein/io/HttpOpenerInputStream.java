package kirchnerei.grundstein.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class HttpOpenerInputStream implements InputStreamFactory.OpenerInputStream {

	@Override
	public InputStream open(String resourceName) throws IOException {
		URL url = new URL(resourceName);
		return url.openStream();
	}
}
