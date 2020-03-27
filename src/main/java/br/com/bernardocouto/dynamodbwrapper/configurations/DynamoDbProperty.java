package br.com.bernardocouto.dynamodbwrapper.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("dynamodb")
public class DynamoDbProperty {

    private String endpointOverride;

    private String region;

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

}
