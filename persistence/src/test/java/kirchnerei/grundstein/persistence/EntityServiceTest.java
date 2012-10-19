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

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class EntityServiceTest {

	private static final Log log = LogFactory.getLog(EntityServiceTest.class);

	private CompositeBuilder builder;

	private EntityService entityService;

	@Before
	public void setUp() {
		assertNull(builder);
		builder = new CompositeBuilder();
		builder.setup(EntityServiceSetup.class);

		assertNull(entityService);
		entityService = builder.getSingleton(EntityService.class);
	}

	@After
	public void tearDown() {
		assertNotNull(entityService);
		entityService = null;

		assertNotNull(builder);
		builder.destroy();
		builder = null;
	}

	@Test
	public void testCrud() {
		EntityManager em = null;
		EntityTransaction tx = null;

		EntityServiceBean bean1 = new EntityServiceBean();
		bean1.setName("EntityServiceBean 1");

		// Create
		LogUtils.debug(log, "begin 'Create'");
		try {
			em = entityService.get();
			tx = em.getTransaction();
			tx.begin();
			em.persist(bean1);
			EntityService.commit(tx);
		} catch (Exception e) {
			EntityService.rollback(tx);
			throw new RuntimeException("Create is failed", e);
		} finally {
			EntityService.close(em);
		}
		long id = bean1.getId();
		assertTrue("bean id must be greater 0", id > 0);
		LogUtils.debug(log, "finish 'Create'");

		// Read
		LogUtils.debug(log, "begin 'Read'");
		EntityServiceBean bean2 = null;
		try {
			em = entityService.get();
			bean2 = em.find(EntityServiceBean.class, id);
		} finally {
			EntityService.close(em);
		}
		assertNotNull("bean must be not null", bean2);
		LogUtils.debug(log, "finish 'Read'");

		// Update
		LogUtils.debug(log, "begin 'Update'");
		EntityServiceBean bean3 = new EntityServiceBean();
		bean3.setId(id);
		bean3.setName("EntityServiceBean 3");
		try {
			em = entityService.get();
			tx = em.getTransaction();
			tx.begin();
			bean3 = em.merge(bean3);
			EntityService.commit(tx);
		} catch (Exception e) {
			EntityService.rollback(tx);
			throw new RuntimeException("Update is failed", e);
		} finally {
			EntityService.close(em);
		}
		assertEquals("EntityServiceBean 3", bean3.getName());
		assertEquals(id, bean3.getId());
		LogUtils.debug(log, "finish 'Update'");

		// Delete
		LogUtils.debug(log, "begin 'Delete'");
		EntityServiceBean bean4 = null;
		try {
			em = entityService.get();
			tx = em.getTransaction();
			tx.begin();
			bean4 = em.find(EntityServiceBean.class, id);
			em.remove(bean4);
			EntityService.commit(tx);
		} catch (Exception e) {
			EntityService.rollback(tx);
			throw new RuntimeException("Delete is failed", e);
		} finally {
			EntityService.close(em);
		}
		assertNotNull(bean4);
		LogUtils.debug(log, "finish 'Delete'");
	}
}
