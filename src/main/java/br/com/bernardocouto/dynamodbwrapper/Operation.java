package br.com.bernardocouto.dynamodbwrapper;

import br.com.bernardocouto.dynamodbwrapper.implementations.CriteriaImplementation;

public interface Operation {

    <T> long count(CriteriaImplementation criteriaImplementation, Class<T> entity);

}
