package br.com.bernardocouto.dynamodbwrapper.exceptions;

public class InvalidUsageException extends RuntimeException {

    public InvalidUsageException(String message) {
        super(message);
    }

}
