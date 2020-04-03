package br.com.bernardocouto.dynamodbwrapper.implementations;

import br.com.bernardocouto.dynamodbwrapper.*;
import br.com.bernardocouto.dynamodbwrapper.converters.Converter;
import br.com.bernardocouto.dynamodbwrapper.exceptions.OperationException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class OperationImplementation implements Operation {

    private final Converter converter;

    private final DynamoDbClient dynamoDbClient;

    private final EntityMapper entityMapper;

    private final Request request;

    public OperationImplementation(Converter converter, DynamoDbClient dynamoDbClient, EntityMapper entityMapper, Request request) {
        this.converter = converter;
        this.dynamoDbClient = dynamoDbClient;
        this.entityMapper = entityMapper;
        this.request = request;
    }

    private <T> ScanResponse executeBaseScan(CriteriaImplementation criteriaImplementation, Class<T> entity) {
        Entity entityBaseScan = entityMapper.getEntity(entity);
        ScanRequest scanRequest = request.scanRequest(entityBaseScan, criteriaImplementation);
        try {
            return dynamoDbClient.scan(scanRequest);
        } catch (Exception e) {
            throw new OperationException("Error to scan with criteria", e);
        }
    }

    private <T> long executeCount(CriteriaImplementation criteriaImplementation, Class<T> entity) {
        ScanResponse scanResponse = executeBaseScan(criteriaImplementation, entity);
        return scanResponse.count();
    }

    private <T> T executeFindOne(Key key, Class<T> entity) {
        Entity entityFindOne = entityMapper.getEntity(entity);
        GetItemRequest getItemRequest = request.getItemRequest(entityFindOne, key);
        try {
            GetItemResponse getItemResponse = dynamoDbClient.getItem(getItemRequest);
            return converter.mapToType(getItemResponse.item(), entity);
        } catch (Exception e) {
            throw new OperationException("Error to find one entity", e);
        }
    }

    private <T> List<T> executeQuery(CriteriaImplementation criteriaImplementation, Class<T> entity) {
        Entity entityQuery = entityMapper.getEntity(entity);
        QueryRequest queryRequest = request.queryRequest(entityQuery, criteriaImplementation);
        try {
            QueryResponse queryResponse = dynamoDbClient.query(queryRequest);
            return queryResponse
                    .items()
                    .stream()
                    .map(item -> converter.mapToType(item, entity))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new OperationException("Error to query with criteria", e);
        }
    }

    private <T> void executeRemove(T entity) {
        Entity entityRemove = entityMapper.getEntity(entity.getClass());
        DeleteItemRequest deleteItemRequest = request.deleteItemRequest(entityRemove, converter.mapToType(entityRemove));
        try {
            dynamoDbClient.deleteItem(deleteItemRequest);
        } catch (Exception e) {
            throw new OperationException("Error to remove entity", e);
        }
    }

    private <T> void executeRemove(Key key, Class<T> entity) {
        Entity entityRemove = entityMapper.getEntity(entity);
        DeleteItemRequest deleteItemRequest = request.deleteItemRequest(entityRemove, entityRemove.getKeyMap(key));
        try {
            dynamoDbClient.deleteItem(deleteItemRequest);
        } catch (Exception e) {
            throw new OperationException("Error to remove entity", e);
        }
    }

    private <T> T executeSave(CriteriaImplementation criteriaImplementation, T entity) {
        Entity entitySave = entityMapper.getEntity(entity.getClass());
        PutItemRequest putItemRequest = request.putItemRequest(entitySave, converter.mapToType(entity), criteriaImplementation);
        try {
            dynamoDbClient.putItem(putItemRequest);
        } catch (Exception e) {
            throw new OperationException("Error to save entity", e);
        }
        return entity;
    }

    private <T> List<T> executeScan(CriteriaImplementation criteriaImplementation, Class<T> entity) {
        ScanResponse scanResponse = executeBaseScan(criteriaImplementation, entity);
        return scanResponse
                .items()
                .stream()
                .map(item -> converter.mapToType(item, entity))
                .collect(Collectors.toList());
    }

    private <T> T save(CriteriaImplementation criteriaImplementation, T entity) {
        return executeSave(criteriaImplementation, entity);
    }

    @Override
    public <T> long count(CriteriaImplementation criteriaImplementation, Class<T> entity) {
        return executeCount(criteriaImplementation, entity);
    }

    @Override
    public <T> List<T> findAll(Class<T> entity) {
        return executeScan(null, entity);
    }

    @Override
    public <T> List<T> findAll(CriteriaImplementation criteriaImplementation, Class<T> entity) {
        return executeQuery(criteriaImplementation, entity);
    }

    @Override
    public <T> T findOne(CriteriaImplementation criteriaImplementation, Class<T> entity) {
        List<T> entities = executeQuery(criteriaImplementation, entity);
        return entities.isEmpty() ? null : entities.get(0);
    }

    @Override
    public <T> T findOne(Key key, Class<T> entity) {
        return executeFindOne(key, entity);
    }

    @Override
    public <T> void remove(T entity) {
        executeRemove(entity);
    }

    @Override
    public <T> void remove(Key key, Class<T> entity) {
        executeRemove(key, entity);
    }

    @Override
    public <T> T save(T entity) {
        return executeSave(null, entity);
    }

    @Override
    public <T> List<T> scan(CriteriaImplementation criteriaImplementation, Class<T> entity) {
        return executeScan(criteriaImplementation, entity);
    }

}
