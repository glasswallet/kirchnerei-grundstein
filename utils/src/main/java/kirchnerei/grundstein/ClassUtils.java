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

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Modifier;

public final class ClassUtils {

	/**
	 * Converts an object into an instance of the specified types to.
	 * 
	 * @throws kirchnerei.grundstein.ClassUtilException
	 * 		if a parameter is <code>null</code> or if an {@link ClassCastException} occurs.
	 * @throws NullPointerException
	 */
	public static <T> T cast(Object o, Class<T> type) {
		if (o == null) {
			throw new NullPointerException("missing parameter <o>");
		}
		try {
			return type.cast(o);
		} catch (ClassCastException e) {
			throw new ClassUtilException(e, "instance <%s> couldn't cast into <%s>",
				o, type.getSimpleName());
		}
	}
	

	/**
	 * Looking out the class name, the appropriate {@link ClassCastException} Instanze
	 * @param className The fully qualified class name.
	 * @return the Class instance
	 * @throws kirchnerei.grundstein.ClassUtilException
	 * 		if a {@link ClassNotFoundException} occurs.
	 * @throws IllegalArgumentException
	 * 		if the parameter is null or empty
	 */
	public static Class<?> forName(String className) {
		if (StringUtils.isEmpty(className)) {
			throw new IllegalArgumentException(
					"parameter <className> is null or empty");
		}
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new ClassUtilException(e, "could not find the class <%s>", className);
		}
	}

	/**
	 * create an instance of the given type.
	 * 
	 * @throws NullPointerException if the parameter 'type' is null.
	 * @throws IllegalArgumentException if the type is an interface or enum.
	 * @throws kirchnerei.grundstein.ClassUtilException if can not create the instance
	 */
	public static <T> T createInstance(Class<T> type) {
		if (type.isInterface() || type.isEnum()) {
			throw new IllegalArgumentException(
					"parameter <type> is an interface");
		}
		if (Modifier.isAbstract(type.getModifiers())) {
			throw new ClassUtilException(
					"class <%s> is non instantiable, because it is an abstract class",
					type.getSimpleName());
		}
		try {
			return type.newInstance();
		} catch (IllegalAccessException e) {
			throw new ClassUtilException(e, "there isn't a access to the class <%s>",
					type.getSimpleName());
		} catch (InstantiationException e) {
			throw new ClassUtilException(e, "class <%s> is non instantiable",
					type.getSimpleName());
		}
	}
	
	private ClassUtils() { }
}
