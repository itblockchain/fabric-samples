package org.hyperledger.fabric.chaincode.BalanceTracker.Utils;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * JSONHelper
 */
public class JSONHelper {

    /**
     * create a json array from the input string
     *
     * @param  input primary key of the newly created id
     */
    public static String createJSONArrayString(List<String> input){
        if (input == null)
            return "";
        if (input.size() == 0)
            return "";

        StringBuilder str = new StringBuilder();
        str.append("[");
        for(int i = 0; i < input.size(); i ++){
            str.append("\"");
            str.append(input.get(i));
            str.append("\"");
            if (i < input.size() - 1)
                str.append(",");
        }
        str.append("]");

        return str.toString();
    }

    /**
     * convert a JSON Array to a normal Array
     *
     * @param  input primary key of the newly created id
     */
    public static List<String> convertJSONArrayToArray(JsonArray input){

        List<String> retList = new ArrayList<String>();

        for (int i=0; i < input.size(); i++) {

            retList.add(input.get(i).getAsString());
        }

        return retList;
    }

    public static List<String> createArrayFromString(String arrayString){

        ArrayList<String> ret = new ArrayList<>();
        if (arrayString == null)
            return ret;
        else if (arrayString.length() == 0)
            return ret;
        else if (arrayString.equals("[]"))
            return ret;
        else if (arrayString.startsWith("[") && arrayString.endsWith("]") && !arrayString.contains(",")){
            ret.add(arrayString.substring(1,arrayString.length() - 1));
            return ret;
        }

        String[] retList = arrayString.split(",") ;

        for (String elem : retList){
            if (!elem.equals("[") && !elem.equals("]")){
                ret.add(elem);
            }
        }
        return ret;
    }
}
