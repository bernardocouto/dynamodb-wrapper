package br.com.bernardocouto.dynamodbwrapper.configurations;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

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

}
