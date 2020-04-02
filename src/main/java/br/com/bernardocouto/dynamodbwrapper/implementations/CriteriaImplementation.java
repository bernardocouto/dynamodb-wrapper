package br.com.bernardocouto.dynamodbwrapper.implementations;

import br.com.bernardocouto.dynamodbwrapper.Criteria;
import br.com.bernardocouto.dynamodbwrapper.FilterExpression;

public class CriteriaImplementation {

    private Criteria criteria;

    public CriteriaImplementation(Criteria criteria) {
        this.criteria = criteria;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public static CriteriaImplementation from(Criteria criteria) {
        return new CriteriaImplementation(criteria);
    }

    public FilterExpression getFilterExpression() {
        return this.criteria.getCriteriaObject();
    }

}
