package br.com.bernardocouto.dynamodbwrapper;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Entity {

    private final String tableName;

    private final String partitionKey;

    private String sortKey;

    public Entity(String tableName, String partitionKey, String sortKey) {
        this.tableName = tableName;
        this.partitionKey = partitionKey;
        this.sortKey = sortKey;
    }

    public String getTableName() {
        return tableName;
    }

    public String getPartitionKey() {
        return partitionKey;
    }

    public String getSortKey() {
        return sortKey;
    }

    public Map<String, AttributeValue> getKeyMap(Key key) {
        Map<String, AttributeValue> map = new HashMap<>();
        map.put(this.partitionKey, key.getPartitionKey());
        if (Objects.nonNull(key.getSortKey())) {
            map.put(this.sortKey, key.getSortKey());
        }
        return map;
    }

}
