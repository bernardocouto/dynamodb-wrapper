package br.com.bernardocouto.dynamodbwrapper.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("dynamodb")
public class DynamoDbProperty {

    private String endpointOverride;

    private String region;

    private ConverterProperties converterProperties = new ConverterProperties();

    public String getEndpointOverride() {
        return endpointOverride;
    }

    public void setEndpointOverride(String endpointOverride) {
        this.endpointOverride = endpointOverride;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public ConverterProperties getConverterProperties() {
        return converterProperties;
    }

    public void setConverterProperties(ConverterProperties converterProperties) {
        this.converterProperties = converterProperties;
    }

    protected static class ConverterProperties {

        private String propertyNamingStrategy = "SNAKE_CASE";

        public ConverterProperties() {
            super();
        }

        public String getPropertyNamingStrategy() {
            return propertyNamingStrategy;
        }

        public void setPropertyNamingStrategy(String propertyNamingStrategy) {
            this.propertyNamingStrategy = propertyNamingStrategy;
        }

    }

}
