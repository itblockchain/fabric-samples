package org.hyperledger.fabric.chaincode.BalanceTracker.Utils;

public class QueryFilterElement {

    protected String key;
    protected QueryOperatorEnum op;
    protected String value;


    public String getKey() {
        return key;
    }

    public QueryOperatorEnum getOp() {
        return op;
    }

    public String getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setOp(QueryOperatorEnum op) {
        this.op = op;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
