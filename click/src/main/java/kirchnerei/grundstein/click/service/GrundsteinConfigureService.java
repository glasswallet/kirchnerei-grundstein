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

package kirchnerei.grundstein.click.service;

import org.apache.click.service.XmlConfigService;

import javax.servlet.ServletContext;

/**
 *
 * <pre><code>
 *     &lt;context-param&gt;
 *         &lt;param-name&gt;config-service-class&lt;/param-name&gt;
 *         &lt;param-value&gt;kirchnerei.grundstein.click.GrundsteinConfigureService&lt;/param-value&gt;
 *     &lt;/context-param&gt;
 * </code></pre>
 */
public class GrundsteinConfigureService extends XmlConfigService {

	@Override
	public boolean isTemplate(String path) {
		boolean isTemplate = super.isTemplate(path);
		if (!isTemplate) {
			isTemplate = path.endsWith("html");
		}
		return isTemplate;
	}

	@Override
	public void onInit(ServletContext servletContext) throws Exception {
		super.onInit(servletContext);
	}
}
