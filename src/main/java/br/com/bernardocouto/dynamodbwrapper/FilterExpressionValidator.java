package br.com.bernardocouto.dynamodbwrapper;

public interface FilterExpressionValidator {

    void validate(Entity entity, FilterExpression filterExpression);

}
