package kirchnerei.grundstein.webapp;

import kirchnerei.grundstein.LogUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class HttpFileResourceServlet extends HttpResourceServlet {

	private static final Log log = LogFactory.getLog(HttpFileResourceServlet.class);

	private static final String INIT_PARAM_CONTENT_TYPE = "kirchnerei.grundstein.webapp.ContentType";

	/**
	 * The List of files (in the classpath) with comma separated.
	 * <pre><code>
	 *     /kirchnerei/files/scripts/jquery.js,/kirchnerei/files/scripts/bootstrap.js
	 * </code></pre>
	 */
	private static final String INIT_PARAM_CONTENT_NAMES = "kirchnerei.grundstein.webapp.ContentNames";

	private String contentType;

	private List<String> names;
	private final StringBuilder content = new StringBuilder();

	@Override
	public void init() throws ServletException {
		super.init();

		contentType = StringUtils.defaultIfEmpty(getInitParameter(INIT_PARAM_CONTENT_TYPE), "text/*");
		LogUtils.debug(log, "%s=%s", INIT_PARAM_CONTENT_TYPE, contentType);

		String contentNames = getInitParameter(INIT_PARAM_CONTENT_NAMES);
		LogUtils.debug(log, "%s=%s", INIT_PARAM_CONTENT_NAMES,
			(contentNames != null) ? contentNames.replaceAll(",", " | ") : "null");
		if (!StringUtils.isEmpty(contentNames)) {
			assert contentNames != null;
			String[] temps = contentNames.split(",");
			names = Arrays.asList(temps);
		}
	}

	@Override
	protected void addContentType(HttpServletResponse response) {
		response.setContentType(contentType);
	}

	@Override
	protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");

	}

	private StringBuilder getContent() {
		if (content.length() == 0) {
			synchronized (this) {
				if (content.length() == 0) {


				}
			}
		}
		return content;
	}
}
