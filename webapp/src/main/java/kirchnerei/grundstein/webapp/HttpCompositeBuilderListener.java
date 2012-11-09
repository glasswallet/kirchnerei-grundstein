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

import kirchnerei.grundstein.ClassUtils;
import kirchnerei.grundstein.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * <p>Context Parameter</p>
 * <pre><code>
 * &lt;context-param&gt;
 *    &lt;param-name&gt;kirchnerei.grundstein.composite.Setup&lt;/param-name&gt;
 *    &lt;param-value&gt;kirchnerei.webapp.Setup&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </code></pre>
 * <p>Listener</p>
 * <pre><code>
 * &lt;listener&gt;
 *    &lt;listener-class&gt;kirchnerei.grundstein.webapp.HttpCompositeBuilderListener&lt;/listener-class&gt;
 * &lt;/listener&gt;
 * </code></pre>
 */
public class HttpCompositeBuilderListener implements ServletContextListener {

	private static final Log log = LogFactory.getLog(HttpCompositeBuilderListener.class);

	static final String PARAM_BUILDER_SETUP = "kirchnerei.grundstein.composite.Setup";

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		try {
			String className = ctx.getInitParameter(PARAM_BUILDER_SETUP);
			Class<?> setupType = ClassUtils.forName(className);
			HttpCompositeBuilder.initialize(ctx, setupType);
			LogUtils.debug(log, "initialize the composite builder with '%s'", className);
		} catch (Exception e) {
			LogUtils.warn(log, e, "initializing of composite builder is failed");
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		HttpCompositeBuilder.destroyBuilder(ctx);
		LogUtils.debug(log, "composite builder is destroyed");
	}
}
