package br.com.bernardocouto.dynamodbwrapper.converters;

import br.com.bernardocouto.dynamodbwrapper.annotations.PartitionKey;
import br.com.bernardocouto.dynamodbwrapper.annotations.SortKey;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import org.springframework.util.StringUtils;

import java.util.Objects;

public class AnnotationIntrospector extends JacksonAnnotationIntrospector {

    private PropertyName findPropertyName(Annotated annotated) {
        PartitionKey partitionKey = _findAnnotation(annotated, PartitionKey.class);
        if (partitionKey != null && !StringUtils.isEmpty(partitionKey.value())) {
            return PropertyName.construct(partitionKey.value());
        }
        SortKey sortKey = _findAnnotation(annotated, SortKey.class);
        if (sortKey != null && !StringUtils.isEmpty(sortKey.value())) {
            return PropertyName.construct(sortKey.value());
        }
        return null;
    }

    @Override
    public PropertyName findNameForDeserialization(Annotated annotated) {
        PropertyName propertyName = findPropertyName(annotated);
        if (Objects.nonNull(propertyName)) {
            return propertyName;
        }
        return super.findNameForDeserialization(annotated);
    }

    @Override
    public PropertyName findNameForSerialization(Annotated annotated) {
        PropertyName propertyName = findPropertyName(annotated);
        if (Objects.nonNull(propertyName)) {
            return propertyName;
        }
        return super.findNameForSerialization(annotated);
    }

}
