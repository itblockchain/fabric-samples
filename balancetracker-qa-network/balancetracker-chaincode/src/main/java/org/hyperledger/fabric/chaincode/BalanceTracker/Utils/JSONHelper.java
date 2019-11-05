package org.hyperledger.fabric.chaincode.BalanceTracker.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.*;

/**
 * JSONHelper
 */
public class JSONHelper {

    /**
     * create a json array from the input string
     *
     * @param  input primary key of the newly created id
     */
    public static HashMap<String,String> createHashMapFromString(String _tagJson){

        if ((_tagJson == null) || (_tagJson.isEmpty())){
            return new HashMap<String,String>();
        }

        Gson gson = new Gson();
        HashMap map = gson.fromJson(_tagJson, HashMap.class);

        return map;
    }


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

        List<String> ret = new ArrayList<String>();
        JsonArray array = new Gson().fromJson(arrayString, JsonArray.class);
        for (int i = 0; i < array.size(); i ++) {
            JsonElement element = array.get(i);
            ret.add(element.getAsString());
        }
        return ret;
    }


    public static String createJsonStringFromHashMap(HashMap<String, String> _hashMap){

        Gson gson = new Gson();
        String mapString = gson.toJson(_hashMap);

        return mapString;
    }
}
