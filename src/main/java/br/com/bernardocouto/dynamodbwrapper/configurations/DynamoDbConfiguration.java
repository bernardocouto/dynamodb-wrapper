package br.com.bernardocouto.dynamodbwrapper.configurations;

import br.com.bernardocouto.dynamodbwrapper.EntityMapper;
import br.com.bernardocouto.dynamodbwrapper.Request;
import br.com.bernardocouto.dynamodbwrapper.converters.AnnotationIntrospector;
import br.com.bernardocouto.dynamodbwrapper.converters.Converter;
import br.com.bernardocouto.dynamodbwrapper.converters.Deserializer;
import br.com.bernardocouto.dynamodbwrapper.converters.Serializer;
import br.com.bernardocouto.dynamodbwrapper.converters.implementations.ConverterImplementation;
import br.com.bernardocouto.dynamodbwrapper.implementations.FilterExpressionValidatorImplementation;
import br.com.bernardocouto.dynamodbwrapper.implementations.OperationImplementation;
import br.com.bernardocouto.dynamodbwrapper.implementations.RequestImplementation;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.lang.reflect.Field;
import java.net.URI;

@ConditionalOnClass(DynamoDbClient.class)
@Configuration
@EnableConfigurationProperties(DynamoDbProperty.class)
public class DynamoDbConfiguration {

    private final DynamoDbProperty dynamoDbProperty;

    public DynamoDbConfiguration(DynamoDbProperty dynamoDbProperty) {
        this.dynamoDbProperty = dynamoDbProperty;
    }

    @Bean
    @ConditionalOnMissingBean
    public Converter converter(@Qualifier("objectMapper") ObjectMapper objectMapper) {
        return new ConverterImplementation(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamoDbClient dynamoDbClient() {
        DynamoDbClientBuilder dynamoDbClientBuilder = DynamoDbClient.builder();
        if (!StringUtils.isEmpty(dynamoDbProperty.getEndpointOverride())) {
            dynamoDbClientBuilder.endpointOverride(URI.create(dynamoDbProperty.getEndpointOverride()));
        }
        if (!StringUtils.isEmpty(dynamoDbProperty.getRegion())) {
            dynamoDbClientBuilder.region(Region.of(dynamoDbProperty.getRegion()));
        }
        return dynamoDbClientBuilder.build();
    }

    @Bean("objectMapper")
    public ObjectMapper objectMapper() throws ClassNotFoundException {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(AttributeValue.class, new Deserializer());
        simpleModule.addSerializer(AttributeValue.class, new Serializer());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.registerModule(simpleModule);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setAnnotationIntrospector(new AnnotationIntrospector());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setPropertyNamingStrategy(propertyNamingStrategy(dynamoDbProperty.getConverterProperties().getPropertyNamingStrategy()));
        return objectMapper;
    }

    @Bean
    public OperationImplementation operationImplementation(DynamoDbClient dynamoDbClient, Converter converter) {
        EntityMapper entityMapper = new EntityMapper(converter);
        Request request = new RequestImplementation(new FilterExpressionValidatorImplementation());
        return new OperationImplementation(converter, dynamoDbClient, entityMapper, request);
    }

    private PropertyNamingStrategy propertyNamingStrategy(String propertyNamingStrategy) {
        Field field = ReflectionUtils.findField(PropertyNamingStrategy.class, propertyNamingStrategy, PropertyNamingStrategy.class);
        try {
            return (PropertyNamingStrategy) field.get(null);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
