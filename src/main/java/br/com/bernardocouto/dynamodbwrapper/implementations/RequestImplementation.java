package br.com.bernardocouto.dynamodbwrapper.implementations;

import br.com.bernardocouto.dynamodbwrapper.*;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class RequestImplementation implements Request {

    private final FilterExpressionValidator filterExpressionValidator;

    public RequestImplementation(FilterExpressionValidator filterExpressionValidator) {
        this.filterExpressionValidator = filterExpressionValidator;
    }

    @Override
    public DeleteItemRequest deleteItemRequest(Entity entity, Map<String, AttributeValue> attributeValueMap) {
        return DeleteItemRequest
                .builder()
                .tableName(entity.getTableName())
                .key(attributeValueMap)
                .build();
    }

    @Override
    public GetItemRequest getItemRequest(Entity entity, Key key) {
        return GetItemRequest
                .builder()
                .tableName(entity.getTableName())
                .key(entity.getKeyMap(key))
                .build();
    }

    @Override
    public PutItemRequest putItemRequest(Entity entity, Map<String, AttributeValue> attributeValueMap, CriteriaImplementation criteriaImplementation) {
        PutItemRequest.Builder builder = PutItemRequest
                .builder()
                .tableName(entity.getTableName())
                .item(attributeValueMap);
        if (Objects.nonNull(criteriaImplementation)) {
            FilterExpression filterExpression = criteriaImplementation.getFilterExpression();
            builder.conditionExpression(filterExpression.getExpression());
            Map<String, String> attributeValueName = filterExpression.getAttributeValueMap(entity);
            if (Objects.nonNull(attributeValueName)) {
                builder.expressionAttributeNames(attributeValueName);
            }
        }
        return builder.build();
    }

    @Override
    public QueryRequest queryRequest(Entity entity, CriteriaImplementation criteriaImplementation) {
        FilterExpression filterExpression = criteriaImplementation.getFilterExpression();
        filterExpressionValidator.validate(entity, filterExpression);
        return QueryRequest
                .builder()
                .tableName(entity.getTableName())
                .keyConditionExpression(filterExpression.getExpression())
                .expressionAttributeNames(filterExpression.getAttributeValueMap(entity))
                .expressionAttributeValues(filterExpression.getExpressionAttributeValueMap())
                .build();
    }

    @Override
    public ScanRequest scanRequest(Entity entity, CriteriaImplementation criteriaImplementation) {
        FilterExpression filterExpression = Optional
                .ofNullable(criteriaImplementation)
                .map(CriteriaImplementation::getFilterExpression)
                .orElse(null);
        ScanRequest.Builder builder = ScanRequest
                .builder()
                .tableName(entity.getTableName());
        if (Objects.nonNull(filterExpression)) {
            builder
                    .filterExpression(filterExpression.getExpression())
                    .expressionAttributeValues(filterExpression.getExpressionAttributeValueMap());
        }
        return builder.build();
    }

}
