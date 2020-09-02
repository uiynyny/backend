package com.ecom.dbagent.daoImpl;

import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.criterion.Projections;
import org.hibernate.query.Query;

import com.ecom.dbagent.dao.DaoInterface;

/*
 * reference used
 * https://examples.javacodegeeks.com/enterprise-java/hibernate/hibernate-jpa-dao-example/
 */
public class DaoImpl implements DaoInterface {

	private Session currentSession;

	private Transaction currentTransaction;
	private static SessionFactory sessionFactory;

	public DaoImpl() {

	}

	public <T> void persist(T entity) {
		getCurrentSession().save(entity);
	}

	public int update(String hql, Map<String, String> paramValues) {
		Query createQuery = getCurrentSession().createQuery(hql);
		for (Map.Entry<String, String> paramValue : paramValues.entrySet()) {
			createQuery.setParameter(paramValue.getKey(), paramValue.getValue());
		}
		int executeUpdate = createQuery.executeUpdate();
		return executeUpdate;
	}

	public <T> List<T> findAll(String hql) {
		@SuppressWarnings("unchecked")
		List<T> entities = (List<T>) getCurrentSession().createQuery(hql).list();
		return entities;
	}

	public <T> T findEntity(String id, Class<T> classType) {
		T entity = (T) getCurrentSession().get(classType, id);
		return entity;
	}

	public <T> List<T> findAllWithCondition(String hql, Map<String, String> paramValues) {
		Query createQuery = getCurrentSession().createQuery(hql);
		for (Map.Entry<String, String> paramValue : paramValues.entrySet()) {
			createQuery.setParameter(paramValue.getKey(), paramValue.getValue());
		}
		List<T> resultEntities = (List<T>) createQuery.list();
		return resultEntities;
	}

	public <T> int findCount(Class<T> entity) {

		Number uniqueResult = (Number) getCurrentSession().createCriteria(entity).setProjection(Projections.rowCount())
				.uniqueResult();
		return uniqueResult.intValue();
	}

	public Session openCurrentSession() {
		currentSession = getSessionFactory().openSession();
		return currentSession;
	}

	public Session openCurrentSessionwithTransaction() {
		currentSession = getSessionFactory().openSession();
		currentTransaction = currentSession.beginTransaction();
		return currentSession;
	}

	public void closeCurrentSessionwithTransaction() {
		currentTransaction.commit();
		currentSession.close();
	}

	public void closeCurrentSession() {
		currentSession.close();
	}

	public void setCurrentSession(Session currentSession) {
		this.currentSession = currentSession;
	}

	public Transaction getCurrentTransaction() {
		return currentTransaction;
	}

	public void setCurrentTransaction(Transaction currentTransaction) {
		this.currentTransaction = currentTransaction;
	}

	public Session getCurrentSession() {
		return currentSession;
	}

	private static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
			MetadataSources sources = new MetadataSources(registry);
			Metadata metadata = sources.getMetadataBuilder().build();
			sessionFactory = metadata.getSessionFactoryBuilder().build();
			return sessionFactory;

		}
		return sessionFactory;
	}

}
