package br.com.bernardocouto.dynamodbwrapper.converters.implementations;

import br.com.bernardocouto.dynamodbwrapper.converters.Converter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;

public class ConverterImplementation implements Converter {

    private final ObjectMapper objectMapper;

    public ConverterImplementation(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> List<Map<String, AttributeValue>> mapToList(T type) {
        return objectMapper.convertValue(type, new TypeReference<List<Map<String, AttributeValue>>>() {});
    }

    @Override
    public <T> Map<String, AttributeValue> mapToType(T type) {
        return objectMapper.convertValue(type, new TypeReference<Map<String, AttributeValue>>() {});
    }

    @Override
    public <T> T mapToTypeList(Map<String, AttributeValue> map, Class<T> type) {
        return objectMapper.convertValue(map, type);
    }

    @Override
    public String translateFieldName(String fieldName) {
        return ((PropertyNamingStrategy.PropertyNamingStrategyBase) objectMapper.getPropertyNamingStrategy()).translate(fieldName);
    }

}
