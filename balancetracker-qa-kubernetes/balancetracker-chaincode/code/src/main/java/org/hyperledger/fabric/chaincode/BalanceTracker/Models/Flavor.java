package org.hyperledger.fabric.chaincode.BalanceTracker.Models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.JSONHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Flavor model
 */
public class Flavor extends ModelBase {

    protected String flavorId;
    protected boolean fungible;
    protected HashMap<String,String> tags;
    protected Integer quorum;
    protected List<String> keyIds;

    /**
     * Constructor
     */
    public Flavor(String _flavorId, boolean _fungible, HashMap<String,String> _tags, Integer _quorum, List<String> _keyIds) {
        this.fungible = _fungible;
        this.flavorId = _flavorId;
        this.tags = _tags;
        this.quorum = _quorum;
        this.keyIds = _keyIds;
        this.modelType = "Flavor";

    }

    /**
     * Constructor
     */
    public Flavor(String _flavorId, boolean _fungible, HashMap<String,String> _tags) {
        this(_flavorId, _fungible, _tags,1, new ArrayList<String>());
    }

    /**
     * Constructor
     */
    public Flavor(String _flavorId, boolean _fungible, Integer _quorum, List<String> _keyIds) {
        this(_flavorId,_fungible, new HashMap<String,String>(), _quorum, _keyIds);
    }

    /**
     * Constructor
     */
    public Flavor(String _flavorId, boolean _fungible) {
        this(_flavorId,_fungible, new HashMap<String,String>(), 1, new ArrayList<String>());
    }

    /**
     * Constructor
     */
    public Flavor(String _flavorId) {
        this(_flavorId,true, new HashMap<String,String>(), 1, new ArrayList<String>());
    }

    /**
     * Getters
     */
    public boolean isFungible(){ return fungible; }

    public String getFlavorId() {
        return flavorId;
    }

    public HashMap<String,String> getTags() {
        if (tags == null)
            return new HashMap<String,String>();
        return tags;
    }

    public Integer getQuorum() {
        return quorum;
    }

    public List<String> getKeyIds() {
        if (keyIds == null)
            return new ArrayList<String>();
        return keyIds;
    }

    /**
     * Factory function
     */
    public static Flavor createFlavor(String flavorString) throws Flavor.DeserializationException {

        try {

            Gson gson = new Gson();
            Flavor flv = gson.fromJson(flavorString, Flavor.class);

            return flv;
        }
        catch (Exception _ex){
            throw new Flavor.DeserializationException("There is a problem at reading out the flavor from the blockchain ",  _ex);
        }
    }

    /**
     * rewriting tags
     */
    public void setTags(HashMap<String,String> _tags){
        this.tags = _tags;
    }

    /**
     * adding tags
     */
    public void addTags(HashMap<String,String> _tags){
        this.tags.putAll(_tags);
    }

    /**
     * EXCEPTIONS
     */

    /**
     * DeserializationException
     */
    public static class DeserializationException extends BalanceTrackerException
    {
        protected Integer code = 5053;

        public DeserializationException(String msg, Exception _ex) {
            super(msg, _ex);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }

    }

    /**
     * SerializationException
     */
    public static class SerializationException extends BalanceTrackerException
    {
        protected Integer code = 5063;

        public SerializationException(String msg, Exception _ex) {
            super(msg, _ex);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }

    }

}
