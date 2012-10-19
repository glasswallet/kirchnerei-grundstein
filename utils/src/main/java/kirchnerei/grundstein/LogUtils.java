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
package kirchnerei.grundstein;

import org.apache.commons.logging.Log;

/**
 * helper class <code>LogUtils</code>
 */
public final class LogUtils {

	public static void warn(Log log, String message, Object... args) {
		if (log.isWarnEnabled()) {
			if (args != null && args.length > 0) {
				message = String.format(message, args);
			}
			log.warn(message);
		}
	}
	
	public static void warn(Log log, Throwable t, String message, Object... args) {
		if (log.isWarnEnabled()) {
			if (args != null && args.length > 0) {
				message = String.format(message, args);
			}
			log.warn(message, t);
		}
	}

	public static void info(Log log, String message, Object... args) {
		if (log.isInfoEnabled()) {
			if (args != null && args.length > 0) {
				message = String.format(message, args);
			}
			log.info(message);
		}
	}
	
	public static void info(Log log, Throwable t, String message, Object... args) {
		if (log.isInfoEnabled()) {
			if (args != null && args.length > 0) {
				message = String.format(message, args);
			}
			log.info(message, t);
		}
	}
	
	public static void debug(Log log, String message, Object... args) {
		if (log.isDebugEnabled()) {
			if (args != null && args.length > 0) {
				message = String.format(message, args);
			}
			log.debug(message);
		}
	}
	
	public static void debug(Log log, Throwable t, String message, Object... args) {
		if (log.isDebugEnabled()) {
			if (args != null && args.length > 0) {
				message = String.format(message, args);
			}
			log.debug(message, t);
		}
	}
	
	public static void trace(Log log, String message, Object... args) {
		if (log.isTraceEnabled()) {
			if (args != null && args.length > 0) {
				message = String.format(message, args);
			}
			log.trace(message);
		}
	}
	
	public static void trace(Log log, Throwable t, String message, Object... args) {
		if (log.isTraceEnabled()) {
			if (args != null && args.length > 0) {
				message = String.format(message, args);
			}
			log.trace(message, t);
		}
	}

}
