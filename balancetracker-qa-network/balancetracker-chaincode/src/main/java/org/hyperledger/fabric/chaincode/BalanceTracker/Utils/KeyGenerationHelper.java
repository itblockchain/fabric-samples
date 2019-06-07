package org.hyperledger.fabric.chaincode.BalanceTracker.Utils;


/**
 * Helper class for automatic and deterministic key generation
 */
public  class KeyGenerationHelper {

    public static String actionPrefix = "_a";
    protected int actionNumber = 1;

    public static String tokenPrefix = "_t";

    protected String transactionPrefix = "_tr";
    protected int transactionNumber = 1;

    protected static String snapshotPrefix = "_sh";

    public String getActionIdFromTokeId(String tokenId) {
        actionNumber ++;
        return tokenId + KeyGenerationHelper.actionPrefix + actionNumber;
    }

    public String getTransactionIdFromTokeId(String tokenId) {
        transactionNumber ++;
        return tokenId + transactionPrefix + transactionNumber;
    }

    public static String getSnapshotIdFromActionId(String actionId){
        return actionId + snapshotPrefix;
    }
}
