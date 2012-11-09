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
import kirchnerei.grundstein.composite.CompositeBuilder;

import javax.servlet.ServletContext;

public final class HttpCompositeBuilder {

	static final String ATTR_BUILDER_KEY = CompositeBuilder.class.getName();

	static CompositeBuilder initialize(ServletContext ctx, Class<?> setupType) {
		CompositeBuilder builder = new CompositeBuilder();
		builder.setup(setupType);
		ctx.setAttribute(ATTR_BUILDER_KEY, builder);
		return builder;
	}

	public static CompositeBuilder getCompositeBuilder(ServletContext ctx) {
		Object value = ctx.getAttribute(ATTR_BUILDER_KEY);
		return ClassUtils.cast(value, CompositeBuilder.class);
	}

	static void destroyBuilder(ServletContext ctx) {
		CompositeBuilder builder = getCompositeBuilder(ctx);
		ctx.removeAttribute(ATTR_BUILDER_KEY);
		builder.destroy();
	}
}
