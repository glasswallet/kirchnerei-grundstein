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
package kirchnerei.grundstein.composite;

import kirchnerei.grundstein.ClassUtils;
import kirchnerei.grundstein.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class <code>CompositeBuilder</code> contains a pool of singleton objects. If a reference
 * in another Component required, the builder can provide class.
 * 
 * <pre><code>
 * CompositeBuilder cb = new CompositeBuilder();
 * cb.setup(Setup.class);
 * Bean b1 = cb.getSingleton(Bean.class);
 * Bean b2 = cb.getSingleton(Bean.class);
 * Assert.assertEqual(b1, b2);
 * </code></pre>
 * @author Kirchnerei
 */
public class CompositeBuilder {

	private static final Log log = LogFactory.getLog(CompositeBuilder.class);

	private static final Object locking = new Object();
	
	private final Map<Class<?>, Object> class2object;
	private final Set<Class<?>> setupClasses;

	public CompositeBuilder() {
		this.class2object = new HashMap<Class<?>, Object>();
		this.setupClasses = new HashSet<Class<?>>();
	}

	/**
	 * The setup class is declared the special classes to be configured, for example special.
	 * 
	 * <pre><code>
	 * 
	 * public class Bean {
	 *    private String name;
	 *    
	 *    public String getName() { return name; }
	 *    
	 *    public void setName(String name) { this.name = name; }
	 * }
	 * 
	 * public class Setup {
	 *    public static class ABean extends Bean {
	 *       public ABean() {
	 *           super();
	 *           setName("dashboard");
	 *       }
	 *    }
	 * }
	 * 
	 * CompositeBuilder cb = new CompositeBuilder();
	 * cb.setup(Setup.class);
	 * Bean b1 = cb.getSingleton(Bean.class);
	 * Assert.assertTrue(b1.getClass() == Setup.ABean.class);
	 * Assert.assertEquals("dashboard", b1.getName());
	 * </code></pre>
	 */
	public void setup(Class<?> setupClazz) {
		Class<?> tempClazz = setupClazz;
		while (tempClazz != null && tempClazz != Object.class) {
			Class<?>[] classes = tempClazz.getClasses();
			for (Class<?> clazz : classes) {
				if (clazz.isInterface() || clazz.isEnum() || clazz.isArray()) {
					continue;
				}
				setupClasses.add(clazz);
				LogUtils.debug(log, "add setup type '%s'", clazz.getSimpleName());
			}
			tempClazz = tempClazz.getSuperclass();
		}
	}

	/**
	 * Returns an instance of the specified class back. If the class is a derived class
	 * declares setup, so this type is used.
	 * 
	 * @throws CompositeException
	 */
	public synchronized <T> T getSingleton(Class<T> type) {
		return getInternalSingleton(type);
	}

	/**
	 * @see #getSingleton(Class)
	 */
	public synchronized Object getSingleton(String className) {
		Class<?> clazz = ClassUtils.forName(className);
		return getSingleton(clazz);
	}

	/**
	 * @see #getSingleton(Class)
	 */
	public synchronized <T> T getSingleton(String className, Class<T> type) {
		Object o = getSingleton(className);
		return ClassUtils.cast(o, type);
	}

	public synchronized <T> T getSingleton(Class<?> type, Class<T> superType) {
		if (!superType.isAssignableFrom(type)) {
			throw new CompositeException("Casting is unable of the classes <%s> to <%s>",
					type.getSimpleName(), superType.getSimpleName());
		}
		Object o = getInternalSingleton(type);
		return ClassUtils.cast(o, superType);
	}
	
	/**
	 * Adds an instance that corresponds to the specified type and performs the initialization,
	 * if the interface {@link CompositeInit} has been implemented in the class.
	 * 
	 * <p>
	 * <b>Note</b>: an existing entry in the pool will be overwritten.
	 * </p>
	 * 
	 * @throws CompositeException
	 * @throws NullPointerException if parameter <value> is null
	 */
	public synchronized void addSingleton(Object value, Class<?> type) {
		if (value == null) {
			throw new NullPointerException("parameter <value> can't be null");
		}
		if (!type.isAssignableFrom(value.getClass())) {
			throw new CompositeException("Casting is unable of the classes <%s> to <%s>",
					value.getClass().getSimpleName(), type.getSimpleName());
		}
		class2object.put(type, callInitialize(value));
	}
	
	public synchronized <T> T init(T value) {
		return callInitialize(value);
	}

	public synchronized <T> T newInstance(Class<T> type) {
		return newInternalInstance(type);
	}

	public synchronized Object newInstance(String className) {
		Class<?> type = ClassUtils.forName(className);
		return newInstance(type);
	}

	public synchronized <T> T newInstance(String className, Class<T> type) {
		Object o = newInstance(className);
		return ClassUtils.cast(o, type);
	}

	/**
	 * Erstellt einen neue Instance der gegebenen Klasse <code>clazz</code> und
	 * konvertiert diese in den Type <code>superClazz</code>.
	 * 
	 * <p>
	 * Es muss darauf geachtet werden, dass die Classheriachie uebereinstimmt.
	 * </p>
	 */
	public synchronized <T> T newInstance(Class<?> type, Class<T> superType) {
		if (!superType.isAssignableFrom(type)) {
			throw new CompositeException("Casting is unable of the classes <%s> to <%s>",
					type.getSimpleName(), superType.getSimpleName());
		}
		Object o = newInstance(type);
		return ClassUtils.cast(o, superType);
	}

	private <T> T getInternalSingleton(Class<T> clazz) {
		if (!class2object.containsKey(clazz)) {
			synchronized (locking) {
				if (!class2object.containsKey(clazz)) {
					Class<?> tempClazz = prepareClassInfo(clazz);
					Object o = ClassUtils.createInstance(tempClazz);
					LogUtils.debug(log, "create singleton instance from '%s' (%s)",
						clazz.getSimpleName(), tempClazz.getSimpleName());
					class2object.put(clazz, o);
					return callInitialize(ClassUtils.cast(o, clazz));
				}
			}
		}
		Object o = class2object.get(clazz);
		return ClassUtils.cast(o, clazz);
	}

	private <T> T newInternalInstance(Class<T> clazz) {
		synchronized (locking) {
			Class<?> tempClazz = prepareClassInfo(clazz);
			Object o = ClassUtils.createInstance(tempClazz);
			return callInitialize(ClassUtils.cast(o, clazz));
		}
	}

	private <T> T callInitialize(T value) {
		if (value instanceof CompositeInit) {
			((CompositeInit) value).init(this);
			LogUtils.debug(log, "%s: call initialize", value.getClass().getSimpleName());
		}
		return value;
	}

	private Class<?> prepareClassInfo(Class<?> superClazz) {
		for (Class<?> clazz : setupClasses) {
			if (superClazz.isAssignableFrom(clazz)) {
				LogUtils.debug(log, "prepare info: find extends type from '%s' => '%s'",
					superClazz.getSimpleName(), clazz.getSimpleName());
				return clazz;
			}
		}
		LogUtils.debug(log, "prepare info: use the type '%s'", superClazz.getSimpleName());
		return superClazz;
	}

	/**
	 * This method <code>destroy</code> frees all instances
	 */
	public synchronized void destroy() {
		for (Class<?> cls: class2object.keySet()) {
			Object obj = class2object.get(cls);
			free(obj);
		}
		class2object.clear();
	}
	
	public synchronized void free(Object o) {
		if (o instanceof CompositeFree) {
			((CompositeFree) o).free(this);
		}
	}

}
