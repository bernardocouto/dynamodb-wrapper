package br.com.bernardocouto.dynamodbwrapper.converters;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;

public interface Converter {

    <T> List<Map<String, AttributeValue>> mapToList(T type);

    <T> Map<String, AttributeValue> mapToType(T type);

    <T> T mapToType(Map<String, AttributeValue> map, Class<T> type);

    String translateFieldName(String fieldName);

}
