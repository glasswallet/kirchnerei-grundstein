package kirchnerei.grundstein.webapp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileResources implements Iterable<String> {

	private final List<String> fileNames = new ArrayList<>();

	public void add(String name) {
		fileNames.add(name);
	}

	@Override
	public Iterator<String> iterator() {
		return fileNames.iterator();
	}

	public static InputStream openStream(String name) {
		InputStream in = FileResources.class.getResourceAsStream(name);
		if (in == null) {
			in = FileResources.class.getClassLoader().getResourceAsStream(name);
		}
		if (in == null) {
			try {
				in = new FileInputStream(name);
			} catch (IOException e) {

			}
		}
		return in;
	}
}
