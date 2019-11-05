package org.hyperledger.fabric.chaincode.BalanceTracker.Utils;

import java.util.ArrayList;
import java.util.List;

public class QueryFilter {

    protected List<QueryFilterElement> filterList;

    public QueryFilter(){
        filterList = new ArrayList<QueryFilterElement>();
    }

    public static QueryFilter createQueryFilterFromJSON(String jsonString){

        // implement json parsing and object init


        return new QueryFilter();
    }

    public List<QueryFilterElement> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<QueryFilterElement> filterList) {
        this.filterList = filterList;
    }



}
