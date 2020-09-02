package com.ecom.dbagent.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;

public interface DaoInterface {

	public <T> void persist(T entity);

	public int update(String hql, Map<String, String> paramValues);

	public <T> T findEntity(String id, Class<T> classType);

	public <T> List<T> findAll(String hql);

	public <T> List<T> findAllWithCondition(String hql, Map<String, String> paramValues);

	public <T> int findCount(Class<T> entity);

	public Session openCurrentSession();

	public Session openCurrentSessionwithTransaction();

	public void closeCurrentSessionwithTransaction();

	public void closeCurrentSession();

}
