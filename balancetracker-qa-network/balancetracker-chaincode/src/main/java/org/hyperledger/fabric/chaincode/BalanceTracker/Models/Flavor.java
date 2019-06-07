package org.hyperledger.fabric.chaincode.BalanceTracker.Models;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.JSONHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Flavor model
 */
public class Flavor extends ModelBase {

    protected String flavorId;
    protected boolean isFungible;
    protected List<String> tags;
    protected Integer quorum;
    protected List<String> keyIds;

    /**
     * Constructor
     */
    public Flavor(String _flavorId, boolean _isFungible, List<String> _tags, Integer _quorum, List<String> _keyIds) {
        this.isFungible = _isFungible;
        this.flavorId = _flavorId;
        this.tags = _tags;
        this.quorum = _quorum;
        this.keyIds = _keyIds;
    }

    /**
     * Constructor
     */
    public Flavor(String _flavorId, boolean _isFungible, List<String> _tags) {
        this(_flavorId, _isFungible, _tags,1, new ArrayList<String>());
    }

    /**
     * Constructor
     */
    public Flavor(String _flavorId, boolean _isFungible, Integer _quorum, List<String> _keyIds) {
        this(_flavorId,_isFungible, new ArrayList<String>(), _quorum, _keyIds);
    }

    /**
     * Constructor
     */
    public Flavor(String _flavorId, boolean _isFungible) {
        this(_flavorId,_isFungible, new ArrayList<String>(), 1, new ArrayList<String>());
    }

    /**
     * Constructor
     */
    public Flavor(String _flavorId) {
        this(_flavorId,true, new ArrayList<String>(), 1, new ArrayList<String>());
    }

    /**
     * Getters
     */
    public boolean isFungible(){ return isFungible; }

    public String getFlavorId() {
        return flavorId;
    }

    public List<String> getTags() {
        return tags;
    }

    public Integer getQuorum() {
        return quorum;
    }

    public List<String> getKeyIds() {
        return keyIds;
    }

    /**
     * Factory function
     */
    public static Flavor createFlavor(String flavorString) throws BalanceTrackerException {

        try {

            JsonObject jsonObject = new JsonParser().parse(flavorString).getAsJsonObject();

            String flavorId = jsonObject.get("flavorId").getAsString();
            boolean isFungible = jsonObject.get("fungible").getAsBoolean();
            Integer quorum = jsonObject.get("quorum").getAsInt();
            List<String> keyIds = JSONHelper.convertJSONArrayToArray(jsonObject.getAsJsonArray("keyIds"));
            List<String> tags = JSONHelper.convertJSONArrayToArray(jsonObject.getAsJsonArray("tags"));

            Flavor flv = new Flavor(flavorId, isFungible, tags, quorum, keyIds);
            return flv;
        }
        catch (Throwable ex){
            throw new BalanceTrackerException(ex);
        }
    }

    /**
     * rewriting tags
     */
    public void setTags(List<String> _tags){
        this.tags = _tags;
    }

    /**
     * adding tags
     */
    public void addTags(List<String> _tags){
        this.tags.addAll(_tags);
    }


}
