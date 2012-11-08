package kirchnerei.grundstein.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileOpenerInputStream implements InputStreamFactory.OpenerInputStream {

	@Override
	public InputStream open(String resourceName) throws IOException {
		if (resourceName.startsWith("file://")) {
			resourceName = resourceName.substring("file://".length());
		}
		return new FileInputStream(resourceName);
	}
}
