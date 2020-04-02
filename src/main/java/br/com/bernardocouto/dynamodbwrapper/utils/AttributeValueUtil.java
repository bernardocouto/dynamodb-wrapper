package br.com.bernardocouto.dynamodbwrapper.utils;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class AttributeValueUtil {

    public static AttributeValue fromObject(Object object) {
        if (object instanceof Number) {
            return AttributeValue.builder().n(object.toString()).build();
        }
        if (object instanceof SdkBytes) {
            return AttributeValue.builder().b((SdkBytes) object).build();
        }
        if (object instanceof String) {
            return AttributeValue.builder().s((String) object).build();
        }
        throw new IllegalArgumentException("The object must be instance of Number, SdkBytes or String");
    }

}
