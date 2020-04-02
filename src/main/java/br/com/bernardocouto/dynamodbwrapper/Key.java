package br.com.bernardocouto.dynamodbwrapper;

import br.com.bernardocouto.dynamodbwrapper.utils.AttributeValueUtil;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Objects;

public final class Key {

    private final AttributeValue partitionKey;

    private AttributeValue sortKey;

    public  Key(Object partitionKey) {
        this(partitionKey, null);
    }

    public Key(Object partitionKey, Object sortKey) {
        this.partitionKey = AttributeValueUtil.fromObject(partitionKey);
        this.sortKey = Objects.isNull(sortKey) ? null : AttributeValueUtil.fromObject(sortKey);
    }

    public AttributeValue getPartitionKey() {
        return partitionKey;
    }

    public AttributeValue getSortKey() {
        return sortKey;
    }

}
