package br.com.bernardocouto.dynamodbwrapper;

import br.com.bernardocouto.dynamodbwrapper.implementations.CriteriaImplementation;

import java.util.Collection;
import java.util.List;

public interface Operation {

    <T> long count(CriteriaImplementation criteriaImplementation, Class<T> entity);

    <T> List<T> findAll(Class<T> entity);

    <T> List<T> findAll(CriteriaImplementation criteriaImplementation, Class<T> entity);

    <T> T findOne(CriteriaImplementation criteriaImplementation, Class<T> entity);

    <T> T findOne(Key key, Class<T> entity);

    <T> void remove(T entity);

    <T> void remove(Key key, Class<T> entity);

    <T> T save(T entity);

    <T> List<T> scan(CriteriaImplementation criteriaImplementation, Class<T> entity);

}
