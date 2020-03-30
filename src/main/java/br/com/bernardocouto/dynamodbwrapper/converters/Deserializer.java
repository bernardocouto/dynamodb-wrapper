package br.com.bernardocouto.dynamodbwrapper.converters;

import br.com.bernardocouto.dynamodbwrapper.exceptions.ConverterException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.io.IOException;
import java.util.*;

public class Deserializer extends JsonDeserializer<AttributeValue> {

    private static final Integer MAXIMUM_DEPTH = 64;

    private List<AttributeValue> arrayToList(final JsonNode jsonNode, final Integer depth) throws ConverterException {
        assertDepth(depth);
        if (jsonNode != null && jsonNode.isArray()) {
            final List<AttributeValue> attributeValueList = new ArrayList<>();
            final Iterator<JsonNode> jsonNodeIterator = jsonNode.elements();
            while (jsonNodeIterator.hasNext()) {
                final JsonNode child = jsonNodeIterator.next();
                attributeValueList.add(getAttributeValue(child, depth));
            }
            return attributeValueList;
        }
        throw new ConverterException("Expected JSON Array");
    }

    private void assertDepth(final Integer depth) throws ConverterException {
        if (depth > MAXIMUM_DEPTH) {
            throw new ConverterException("Maximum depth reached");
        }
    }

    private AttributeValue getAttributeValue(final JsonNode jsonNode, final Integer depth) throws ConverterException {
        assertDepth(depth);
        switch (jsonNode.asToken()) {
            case START_ARRAY:
                return AttributeValue.builder().l(arrayToList(jsonNode, depth)).build();
            case START_OBJECT:
                return AttributeValue.builder().m(objectToMap(jsonNode, depth)).build();
            case VALUE_FALSE:
            case VALUE_TRUE:
                return AttributeValue.builder().bool(jsonNode.booleanValue()).build();
            case VALUE_NULL:
                return AttributeValue.builder().nul(true).build();
            case VALUE_NUMBER_FLOAT:
            case VALUE_NUMBER_INT:
                return AttributeValue.builder().n(jsonNode.numberValue().toString()).build();
            case VALUE_STRING:
                return AttributeValue.builder().s(jsonNode.textValue()).build();
            default:
                throw new ConverterException("Unknown type node");
        }
    }

    private JsonNode getJsonNode(final AttributeValue attributeValue, final Integer depth) throws ConverterException {
        assertDepth(depth);
        if (attributeValue.bool() != null) {
            return JsonNodeFactory.instance.booleanNode(attributeValue.bool());
        } else if (attributeValue.l() != null) {
            return listToArray(attributeValue.l(), depth);
        } else if (attributeValue.m() != null) {
            return mapToObject(attributeValue.m(), depth);
        } else if (attributeValue.n() != null) {
            try {
                return JsonNodeFactory.instance.numberNode(Float.parseFloat(attributeValue.n()));
            } catch (final NumberFormatException firstException) {
                try {
                    return JsonNodeFactory.instance.numberNode(Integer.parseInt(attributeValue.n()));
                } catch (NumberFormatException secondException) {
                    throw new ConverterException(firstException.getMessage());
                }
            }
        } else if (attributeValue.nul() != null) {
            return JsonNodeFactory.instance.nullNode();
        } else if (attributeValue.s() != null) {
            return JsonNodeFactory.instance.textNode(attributeValue.s());
        } else {
            throw new ConverterException("Unknown type value");
        }
    }

    private JsonNode listToArray(final List<AttributeValue> item, final Integer depth) throws ConverterException {
        assertDepth(depth);
        if (item != null) {
            final ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
            for (final AttributeValue attributeValue : item) {
                arrayNode.add(getJsonNode(attributeValue, depth + 1));
            }
            return arrayNode;
        }
        throw new ConverterException("Item cannot be null");
    }

    private JsonNode mapToObject(final Map<String, AttributeValue> item, final Integer depth) throws ConverterException {
        if (item != null) {
            final ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
            for (final Map.Entry<String, AttributeValue> entry : item.entrySet()) {
                objectNode.set(entry.getKey(), getJsonNode(entry.getValue(), depth + 1));
            }
            return objectNode;
        }
        throw new ConverterException("Item cannot be null");
    }

    private Map<String, AttributeValue> objectToMap(final JsonNode jsonNode, final Integer depth) throws ConverterException {
        assertDepth(depth);
        if (jsonNode != null && jsonNode.isObject()) {
            final Map<String, AttributeValue> attributeValueMap = new HashMap<>();
            final Iterator<String> stringIterator = jsonNode.fieldNames();
            while (stringIterator.hasNext()) {
                final String key = stringIterator.next();
                attributeValueMap.put(key, getAttributeValue(jsonNode.get(key), depth + 1));
            }
            return attributeValueMap;
        }
        throw new ConverterException("Expected JSON Object");
    }

    @Override
    public AttributeValue deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectCodec objectCodec = jsonParser.getCodec();
        JsonNode jsonNode = objectCodec.readTree(jsonParser);
        return getAttributeValue(jsonNode, 0);
    }
}
