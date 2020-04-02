package br.com.bernardocouto.dynamodbwrapper.exceptions;

import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class OperationException extends RuntimeException {

    public OperationException(String message, Exception exception) {
        super(exception instanceof DynamoDbException ? ((DynamoDbException) exception).awsErrorDetails().errorMessage() : message);
    }

}
