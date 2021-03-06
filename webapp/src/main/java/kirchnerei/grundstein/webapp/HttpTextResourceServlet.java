/*
 * Copyright 2012 Kirchnerei
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kirchnerei.grundstein.webapp;

import kirchnerei.grundstein.ClassUtilException;
import kirchnerei.grundstein.LogUtils;
import kirchnerei.grundstein.composite.CompositeBuilder;
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
 *
 * <p>
 *     Configuration in the web.xml
 * </p>
 *
 * <servlet>
 *     <servlet-name>ScriptResourceServlet</servlet-name>
 *     <servlet-class>kirchnerei.grundstein.webapp.HttpTextResourceServlet</servlet-class>
 *     <init-parameter>
 *         <name>kirchnerei.grundstein.webapp.HttpETagStrategy</name>
 *         <value>class implements the interface ETagStrategy</value>
 *     </init-parameter>
 *     <init-parameter>
 *         <name>kirchnerei.grundstein.webapp.TextResources</name>
 *         <value>class of TextResources</value>
 *     </init-parameter>
 *     <load-on-startup>1</load-on-startup>
 * </servlet>
 * <servlet-mapping>
 *     <servlet-name>ScriptResourceServlet</servlet-name>
 *     <url>/asserts/jquery.js</url>
 * </servlet-mapping>
 */
public class HttpTextResourceServlet extends HttpServlet {

	private static final long serialVersionUID = -5634908479411913228L;

	public static final String INIT_PARAM_STRATEGY = "kirchnerei.grundstein.webapp.HttpETagStrategy";

	public static final String INIT_PARAM_RESOURCES = "kirchnerei.grundstein.webapp.TextResources";

	private static final Log log = LogFactory.getLog(HttpTextResourceServlet.class);

	private static final String ETAG_SESSION_KEY =
		HttpTextResourceServlet.class.getName() + ".ETagSession";

	private static final String REQ_HEADER_MATCH = "If-None-Match";
	private static final String REQ_HEADER_ETAG = "ETag";

	private HttpETagStrategy etagStrategy;

	private TextResources resources;

	private String encoding = "UTF-8";

	public HttpETagStrategy getEtagStrategy() {
		if (etagStrategy == null) {
			synchronized (this) {
				if (etagStrategy == null) {
					etagStrategy = new HttpETagStrategy() {
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

	public void setEtagStrategy(HttpETagStrategy etagStrategy) {
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
		CompositeBuilder builder = HttpCompositeBuilder.getCompositeBuilder(getServletContext());
		loadETagStrategy(builder);
		loadTextResources(builder);
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
		if (!response.isCommitted() && etag.equals(request.getHeader(REQ_HEADER_MATCH))) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			LogUtils.debug(log, "ETag is okay, no modified (304) '%s'", etag);
			return false;
		}
		response.setHeader(REQ_HEADER_ETAG, etag);
		LogUtils.debug(log, "ETag is not okay, the text resource will be delivery '%s'", etag);
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

	protected void addContentType(HttpServletResponse response) throws IOException {
		if (StringUtils.isEmpty(response.getContentType())) {
			throw new IOException("missing the property 'contentType' in text resource");
		}
		response.setContentType(resources.getContentType());
	}

	protected void doProcess(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		OutputStream output = response.getOutputStream();
		getResources().writeTo(output);
		LogUtils.debug(log, "write the text resource into the output stream");
	}


	private void loadETagStrategy(CompositeBuilder builder) {
		String className = getInitParameter(INIT_PARAM_STRATEGY);
		if (StringUtils.isEmpty(className)) {
			return;
		}
		HttpETagStrategy strategy = null;
		try {
			strategy = builder.getSingleton(className, HttpETagStrategy.class);
		} catch (ClassUtilException e) {
			LogUtils.warn(log, e, "load http etag strategy is failed '%s'", className);
			strategy = null;
		}
		setEtagStrategy(strategy);
	}

	private void loadTextResources(CompositeBuilder builder) {
		TextResources textResources = null;
		String className = getInitParameter(INIT_PARAM_RESOURCES);
		if (!StringUtils.isEmpty(className)) {
			textResources = builder.getSingleton(className, TextResources.class);
		}
		this.setResources(textResources);
	}
}
