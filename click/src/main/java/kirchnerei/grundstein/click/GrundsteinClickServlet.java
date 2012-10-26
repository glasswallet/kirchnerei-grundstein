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

package kirchnerei.grundstein.click;

import kirchnerei.grundstein.LogUtils;
import kirchnerei.grundstein.composite.CompositeBuilder;
import kirchnerei.grundstein.webapp.HttpBuilder;
import org.apache.click.ClickServlet;
import org.apache.click.Page;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;

/**
 * The servlet has the composite builder and every page will be initialized with the builder.
 *
 * <p>
 *     Pages with the interface CompositeInit will be called and they can connect able other
 *     business classes.
 * </p>
 */
public class GrundsteinClickServlet extends ClickServlet {

	private static final Log log = LogFactory.getLog(GrundsteinClickServlet.class);

	private CompositeBuilder builder;

	@Override
	public void init() throws ServletException {
		super.init();

		builder = HttpBuilder.getCompositeBuilder(getServletContext());
		LogUtils.trace(log, "initialize the click servlet and load the composite builder");
	}

	@Override
	protected void activatePageInstance(Page page) {
		builder.init(page);
		LogUtils.trace(log, "connect the page '%s' with the composite builder",
			page.getClass().getSimpleName());
	}
}
