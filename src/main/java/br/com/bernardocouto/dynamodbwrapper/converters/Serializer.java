package br.com.bernardocouto.dynamodbwrapper.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructMap;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class Serializer extends JsonSerializer<AttributeValue> {

    public static final DefaultSdkAutoConstructList<?> EMPTY_ATTRIBUTE_LIST = DefaultSdkAutoConstructList.getInstance();

    public static final DefaultSdkAutoConstructMap<?, ?> EMPTY_ATTRIBUTE_MAP = DefaultSdkAutoConstructMap.getInstance();

    @Override
    public void serialize(AttributeValue attributeValue, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (attributeValue == null) {
            jsonGenerator.writeNull();
        } else {
            if (attributeValue.b() != null) {
                jsonGenerator.writeBinary(attributeValue.b().asByteArray());
            } else if (attributeValue.bool() != null) {
                jsonGenerator.writeBoolean(attributeValue.bool());
            } else if (attributeValue.bs() != EMPTY_ATTRIBUTE_LIST) {
                List<SdkBytes> list = attributeValue.bs();
                Integer size = list.size();
                jsonGenerator.writeStartArray(size);
                for (int i = 0; i < size; i++) {
                    jsonGenerator.writeBinary(list.get(i).asByteArray());
                }
                jsonGenerator.writeEndArray();
            } else if (attributeValue.l() != EMPTY_ATTRIBUTE_LIST) {
                List<AttributeValue> list = attributeValue.l();
                jsonGenerator.writeStartArray();
                for (AttributeValue child : list) {
                    serialize(child, jsonGenerator, serializerProvider);
                }
                jsonGenerator.writeEndArray();
            } else if (attributeValue.m() != EMPTY_ATTRIBUTE_MAP) {
                jsonGenerator.writeStartObject();
                Map<String, AttributeValue> map = attributeValue.m();
                for (Map.Entry<String, AttributeValue> entry : map.entrySet()) {
                    jsonGenerator.writeFieldName(entry.getKey());
                    serialize(entry.getValue(), jsonGenerator, serializerProvider);
                }
                jsonGenerator.writeEndObject();
            } else if (attributeValue.n() != null) {
                jsonGenerator.writeNumber(new BigDecimal(attributeValue.n()));
            } else if (attributeValue.ns() != EMPTY_ATTRIBUTE_LIST) {
                List<String> list = attributeValue.ns();
                Integer size = list.size();
                jsonGenerator.writeStartArray(size);
                for (int i = 0; i < size; i++) {
                    jsonGenerator.writeNumber(new BigDecimal(list.get(i)));
                }
                jsonGenerator.writeEndArray();
            } else if (attributeValue.nul() != null) {
                jsonGenerator.writeNull();
            } else if (attributeValue.nul() != null && attributeValue.nul()) {
                jsonGenerator.writeNull();
            } else if (attributeValue.s() != null) {
                jsonGenerator.writeString(attributeValue.s());
            } else if (attributeValue.ss() != EMPTY_ATTRIBUTE_LIST) {
                List<String> list = attributeValue.ss();
                Integer size = list.size();
                jsonGenerator.writeStartArray(size);
                for (int i = 0; i < size; i++) {
                    jsonGenerator.writeString(list.get(i));
                }
                jsonGenerator.writeEndArray();
            }
        }
    }
}
