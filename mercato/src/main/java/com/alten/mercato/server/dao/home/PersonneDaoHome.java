package com.alten.mercato.server.dao.home;
// Generated 2009-4-10 15:33:20 by Hibernate Tools 3.2.4.CR1

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.alten.mercato.server.model.Personne;

/**
 * Home object for domain model class Personne.
 * @see .Personne
 * @author Hibernate Tools
 */
public class PersonneDaoHome extends HibernateDaoSupport{

	private static final Log log = LogFactory.getLog(PersonneDaoHome.class);


	public void persist(Personne transientInstance) {
		log.debug("persisting Personne instance");
		try {
			getHibernateTemplate().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Personne instance) {
		log.debug("attaching dirty Personne instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Personne instance) {
		log.debug("attaching clean Personne instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Personne persistentInstance) {
		log.debug("deleting Personne instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Personne merge(Personne detachedInstance) {
		log.debug("merging Personne instance");
		try {
			Personne result = (Personne) getHibernateTemplate().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Personne findById(long id) {
		log.debug("getting Personne instance with id: " + id);
		try {
			Personne instance = (Personne) getHibernateTemplate().get(Personne.class, id);
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Personne> findByExample(Personne instance) {
		log.debug("finding Personne instance by example");
		try {
			List<Personne> results = (List<Personne>) getHibernateTemplate().findByExample(instance);
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
