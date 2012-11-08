package kirchnerei.grundstein.webapp;

import kirchnerei.grundstein.ClassUtilException;
import kirchnerei.grundstein.ClassUtils;
import kirchnerei.grundstein.LogUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 */
public class HttpResourceServlet extends HttpServlet {

	private static final long serialVersionUID = -5634908479411913228L;

	public static final String INIT_PARAM_STRATEGY = "kirchnerei.grundstein.webapp.etagStrategy";

	public static final String INIT_PARAM_RESOURCES =
		"kirchnerei.grundstein.webapp.Resources";

	public static final String INIT_PARAM_ENCODING = "kirchnerei.grundstein.webapp.encoding";

	private static final Log log = LogFactory.getLog(HttpResourceServlet.class);

	private static final String ETAG_SESSION_KEY =
		HttpResourceServlet.class.getName() + ".ETagSession";

	private static final String REQ_HEADER_MATCH = "If-None-Match";
	private static final String REQ_HEADER_ETAG = "ETag";

	private ETagStrategy etagStrategy;

	private TextResources resources;

	private String encoding = "UTF-8";

	public ETagStrategy getEtagStrategy() {
		if (etagStrategy == null) {
			synchronized (this) {
				if (etagStrategy == null) {
					etagStrategy = new ETagStrategy() {
						@Override
						public String build(HttpServletRequest request) {
							HttpSession session = request.getSession(true);
							return DigestUtils.sha256Hex(session.getId());
						}
					};
					LogUtils.info(log, "etag strategy use the default implementation");
				}
			}
		}
		return etagStrategy;
	}

	public void setEtagStrategy(ETagStrategy etagStrategy) {
		if (etagStrategy != null) {
			this.etagStrategy = etagStrategy;
			LogUtils.info(log, "etag strategy is implemented from '%s'",
				etagStrategy.getClass().getSimpleName());
		}
	}

	public TextResources getResources() {
		return resources;
	}

	public void setResources(TextResources resources) {
		if (resources == null) {
			throw new RuntimeException("parameter <resources> must be not null");
		}
		this.resources = resources;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public void init() throws ServletException {
		super.init();
		loadETagStrategy();
		loadTextResources();
		loadEncoding();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		addContentType(response);
		if (!checkEtagFromRequest(request, response)) {
			return;
		}
		doProcess(request, response);
	}

	private boolean checkEtagFromRequest(HttpServletRequest request, HttpServletResponse response) {
		String etag = getETagFromSession(request);
		if (!response.isCommitted() && REQ_HEADER_MATCH.equals(etag)) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return false;
		}
		response.setHeader(REQ_HEADER_ETAG, etag);
		return true;
	}

	private String getETagFromSession(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		Object o = session.getAttribute(ETAG_SESSION_KEY);
		if (o != null) {
			return o.toString();
		}
		String etag = "\"" + getEtagStrategy().build(request) + "\"";
		session.setAttribute(ETAG_SESSION_KEY, etag);
		return etag;
	}

	protected void addContentType(HttpServletResponse response) {
		response.setContentType(resources.getContentType());
	}

	protected void doProcess(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		OutputStream output = response.getOutputStream();
		getResources().writeTo(output, getEncoding());
		output.flush();
	}


	private void loadETagStrategy() {
		ETagStrategy strategy = null;
		try {
			String className = getInitParameter(INIT_PARAM_STRATEGY);
			if (StringUtils.isEmpty(className)) {
				return;
			}
			strategy = ClassUtils.createInstance(className, ETagStrategy.class);
		} catch (ClassUtilException e) {
			strategy = null;
		}
		setEtagStrategy(strategy);
	}

	private void loadTextResources() {
		TextResources textResources = null;
		String className = getInitParameter(INIT_PARAM_RESOURCES);
		if (!StringUtils.isEmpty(className)) {
			textResources = ClassUtils.createInstance(className, TextResources.class);
		}
		this.setResources(textResources);
	}

	private void loadEncoding() {
		String temp = getInitParameter(INIT_PARAM_ENCODING);
		if (!StringUtils.isEmpty(temp)) {
			setEncoding(temp);
		}
	}
}
