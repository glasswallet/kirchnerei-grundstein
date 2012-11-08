package kirchnerei.grundstein.io;

import java.io.IOException;
import java.io.InputStream;

public class ClassOpenerInputStream implements InputStreamFactory.OpenerInputStream {

	@Override
	public InputStream open(String resourceName) throws IOException {
		if (resourceName.startsWith("class://")) {
			resourceName = resourceName.substring("class://".length());
		}
		InputStream input = getClass().getResourceAsStream(resourceName);
		if (input == null) {
			input = getClass().getClassLoader().getResourceAsStream(resourceName);
		}
		return input;
	}
}
