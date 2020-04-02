package br.com.bernardocouto.dynamodbwrapper;

import br.com.bernardocouto.dynamodbwrapper.utils.AttributeValueUtil;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;

public class Criteria {

    public static final String PARTITION_KEY = "partition_key";

    public static final String SORT_KEY = "sort_key";

    protected static final String PARTITION_KEY_ALIAS = "#" + PARTITION_KEY;

    protected static final String SORT_KEY_ALIAS = "#" + SORT_KEY;

    private static final String AND = " and ";

    private static final String ATTRIBUTE_EXISTS = "attribute_exists";

    private static final String ATTRIBUTE_NOT_EXISTS = "attribute_not_exists";

    private String key;

    protected List<Criteria> criteriaList;

    protected LinkedHashMap<String, Object> criteriaLinkedHashMap = new LinkedHashMap<>();

    public Criteria(String key) {
        this.criteriaList = new ArrayList<>();
        this.criteriaList.add(this);
        this.key = key;
    }

    public Criteria(String key, List<Criteria> criteriaList) {
        this.criteriaList = criteriaList;
        this.criteriaList.add(this);
        this.key = key;
    }

    public Criteria and(String key) {
        return new Criteria(key, this.criteriaList);
    }

    public Criteria exists() {
        this.criteriaLinkedHashMap.put(ATTRIBUTE_EXISTS, key);
        return this;
    }

    private String formatFilterExpression(String parameter, String expression) {
        if (ATTRIBUTE_EXISTS.equals(expression) || ATTRIBUTE_NOT_EXISTS.equals(expression)) {
            return String.format("%s(%s)", expression, this.key);
        }
        return String.format("%s %s %s", this.key, expression, parameter);
    }

    public FilterExpression getCriteriaObject() {
        if (this.criteriaList.size() == 1) {
            return criteriaList.get(0).getSingleCriteriaObject();
        } else if (CollectionUtils.isEmpty(this.criteriaList) && !CollectionUtils.isEmpty(this.criteriaLinkedHashMap)) {
            return getSingleCriteriaObject();
        } else {
            StringJoiner expression = new StringJoiner(AND);
            Map<String, AttributeValue> attributeValueMap = new LinkedHashMap<>();
            for (Criteria criteria : this.criteriaList) {
                FilterExpression filterExpression = criteria.getSingleCriteriaObject();
                expression.add(filterExpression.getExpression());
                attributeValueMap.putAll(filterExpression.getExpressionAttributeValueMap());
            }
            return new FilterExpression(expression.toString(), attributeValueMap);
        }
    }

    public FilterExpression getSingleCriteriaObject() {
        Map<String, AttributeValue> attributeValueMap = new LinkedHashMap<>();
        StringJoiner expression = new StringJoiner(AND);
        int count = 0;
        for (Map.Entry<String, Object> entry : this.criteriaLinkedHashMap.entrySet()) {
            String parameter = String.format(":%s%d", this.key, ++count).replace("#", "");
            expression.add(formatFilterExpression(parameter, entry.getKey()));
            attributeValueMap.put(parameter, AttributeValueUtil.fromObject(entry.getValue()));
        }
        return new FilterExpression(expression.toString(), attributeValueMap);
    }

    public Criteria gt(Object object) {
        this.criteriaLinkedHashMap.put(">", object);
        return this;
    }

    public Criteria gte(Object object) {
        this.criteriaLinkedHashMap.put(">=", object);
        return this;
    }

    public Criteria is(Object object) {
        this.criteriaLinkedHashMap.put("=", object);
        return this;
    }

    public Criteria lt(Object object) {
        this.criteriaLinkedHashMap.put("<", object);
        return this;
    }

    public Criteria lte(Object object) {
        this.criteriaLinkedHashMap.put("<=", object);
        return this;
    }

    public Criteria notExists() {
        this.criteriaLinkedHashMap.put(ATTRIBUTE_NOT_EXISTS, key);
        return this;
    }

    public static String partitionKey() {
        return PARTITION_KEY_ALIAS;
    }

    public static String sortKey() {
        return SORT_KEY_ALIAS;
    }

    public static Criteria where(String key) {
        return new Criteria(key);
    }

}
