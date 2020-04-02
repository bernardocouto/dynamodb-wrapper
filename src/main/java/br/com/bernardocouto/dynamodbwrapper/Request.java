package br.com.bernardocouto.dynamodbwrapper;

import br.com.bernardocouto.dynamodbwrapper.implementations.CriteriaImplementation;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Map;

public interface Request {

    DeleteItemRequest deleteItemRequest(Entity entity, Map<String, AttributeValue> attributeValueMap);

    GetItemRequest getItemRequest(Entity entity, Key key);

    PutItemRequest putItemRequest(Entity entity, Map<String, AttributeValue> attributeValueMap, CriteriaImplementation criteriaImplementation);

    QueryRequest queryRequest(Entity entity, CriteriaImplementation criteriaImplementation);

    ScanRequest scanRequest(Entity entity, CriteriaImplementation criteriaImplementation);

}
