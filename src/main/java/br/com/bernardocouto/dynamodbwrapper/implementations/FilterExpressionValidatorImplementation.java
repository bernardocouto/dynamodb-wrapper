package br.com.bernardocouto.dynamodbwrapper.implementations;

import br.com.bernardocouto.dynamodbwrapper.Criteria;
import br.com.bernardocouto.dynamodbwrapper.Entity;
import br.com.bernardocouto.dynamodbwrapper.FilterExpression;
import br.com.bernardocouto.dynamodbwrapper.FilterExpressionValidator;
import br.com.bernardocouto.dynamodbwrapper.exceptions.InvalidUsageException;
import org.springframework.util.StringUtils;

public class FilterExpressionValidatorImplementation implements FilterExpressionValidator {

    private boolean invalidPartitionKeyExpression(FilterExpression filterExpression, String partitionKey) {
        int firstIndex = filterExpression.getExpression().indexOf(partitionKey + " = ");
        int lastIndex = filterExpression.getExpression().lastIndexOf(partitionKey + " = ");
        return firstIndex >= 0 && firstIndex != lastIndex;
    }

    private boolean invalidSortKeyExpression(FilterExpression filterExpression, String sortKey) {
        return StringUtils.countOccurrencesOf(filterExpression.getExpression(), sortKey) > 2;
    }

    @Override
    public void validate(Entity entity, FilterExpression filterExpression) {
        if (invalidPartitionKeyExpression(filterExpression, Criteria.PARTITION_KEY) || invalidPartitionKeyExpression(filterExpression, entity.getPartitionKey())) {
            throw new InvalidUsageException("Query operations must only contain equals operation for Partition Key");
        }
        if (invalidSortKeyExpression(filterExpression, Criteria.SORT_KEY) || invalidSortKeyExpression(filterExpression, entity.getSortKey())) {
            throw new InvalidUsageException("Query operations must only contain one condition per Key");
        }
    }

}
