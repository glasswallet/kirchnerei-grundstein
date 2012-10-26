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
package kirchnerei.grundstein.persistence;

import kirchnerei.grundstein.LogUtils;
import kirchnerei.grundstein.composite.CompositeBuilder;
import kirchnerei.grundstein.composite.CompositeException;
import kirchnerei.grundstein.composite.CompositeFree;
import kirchnerei.grundstein.composite.CompositeInit;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * The class <code>EntityService</code>
 *
 * <pre><code>
 *     EntityManager em = null;
 *     EntityTransaction tx = null;
 *     try {
 *         em = entityService.get();
 *         tx = em.getTransaction();
 *         ...
 *         EntityService.commit(tx);
 *     } catch (Exception e) {
 *         EntityService.rollback(tx);
 *     } finally {
 *         EntityService.close(em);
 *     }
 * </code></pre>
 */
public class EntityService implements CompositeInit, CompositeFree {

	private static final Log log = LogFactory.getLog(EntityService.class);

	private EntityManagerFactory factory = null;

	private String name;

	@Override
	public void init(CompositeBuilder builder) {
		if (StringUtils.isEmpty(getName())) {
			throw new CompositeException("persistence unit is empty");
		}
		try {
			factory = Persistence.createEntityManagerFactory(getName());
			LogUtils.debug(log, "create entity manager '%s' with %s",
				getName(), factory == null ? "null" : "success");
		} catch (Exception e) {
			LogUtils.warn(log, e, "persistent unit '%s' is failed'", getName());
			throw new CompositeException(e, "persistent unit '%s' is failed", getName());
		}
	}

	public EntityManager get() {
		return factory.createEntityManager();
	}

	public static void close(EntityManager em) {
		if (em != null) {
			em.close();
		}
	}

	public static void rollback(EntityTransaction tx) {
		try {
			if (tx != null) {
				tx.rollback();
			}
		} catch (Exception e) {
			LogUtils.warn(log, "entity rollback is failed (message: %s)", e.getMessage());
		}
	}

	public static void commit(EntityTransaction tx) {
		if (tx != null) {
			tx.commit();
		}
	}

	@Override
	public void free(CompositeBuilder builder) {
		factory.close();
		LogUtils.debug(log, "entity manager factory is closed");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
