package br.com.bernardocouto.dynamodbwrapper;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

public class FilterExpression {

    private final String expression;

    private final Map<String, AttributeValue> expressionAttributeValueMap;

    private Map<String, String> attributeValueMap;

    public FilterExpression(String expression, Map<String, AttributeValue> expressionAttributeValueMap) {
        this.expression = expression;
        this.expressionAttributeValueMap = expressionAttributeValueMap;
    }

    public String getExpression() {
        return expression;
    }

    public Map<String, AttributeValue> getExpressionAttributeValueMap() {
        return expressionAttributeValueMap;
    }

    public Map<String, String> getAttributeValueMap(Entity entity) {
        Map<String, String> map = new HashMap<>();
        if (expression.contains(Criteria.PARTITION_KEY_ALIAS)) {
            map.put(Criteria.PARTITION_KEY_ALIAS, entity.getPartitionKey());
        }
        if (expression.contains(Criteria.SORT_KEY_ALIAS)) {
            map.put(Criteria.SORT_KEY_ALIAS, entity.getSortKey());
        }
        return map.isEmpty() ? null : map;
    }

}
