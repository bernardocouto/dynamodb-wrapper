package br.com.bernardocouto.dynamodbwrapper.implementations;

import br.com.bernardocouto.dynamodbwrapper.Entity;
import br.com.bernardocouto.dynamodbwrapper.EntityMapper;
import br.com.bernardocouto.dynamodbwrapper.Operation;
import br.com.bernardocouto.dynamodbwrapper.Request;
import br.com.bernardocouto.dynamodbwrapper.converters.Converter;
import br.com.bernardocouto.dynamodbwrapper.exceptions.OperationException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

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

    @Override
    public <T> long count(CriteriaImplementation criteriaImplementation, Class<T> entity) {
        return executeCount(criteriaImplementation, entity);
    }

}
