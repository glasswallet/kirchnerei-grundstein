package kirchnerei.grundstein.webapp;

import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 *
 */
public abstract class HttpResourceServlet extends HttpServlet {

	private static final String ETAG_SESSION_KEY =
		HttpResourceServlet.class.getName() + ".ETagSession";

	private static final String REQ_HEADER_MATCH = "If-None-Match";
	private static final String REQ_HEADER_ETAG = "ETag";

	private ETagStrategy etagStrategy;

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
				}
			}
		}
		return etagStrategy;
	}

	public void setEtagStrategy(ETagStrategy etagStrategy) {
		this.etagStrategy = etagStrategy;
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

	protected abstract void addContentType(HttpServletResponse response);

	protected abstract void doProcess(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException;

}
