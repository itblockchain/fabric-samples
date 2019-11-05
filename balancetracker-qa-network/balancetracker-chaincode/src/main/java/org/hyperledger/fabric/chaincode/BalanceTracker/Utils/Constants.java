package org.hyperledger.fabric.chaincode.BalanceTracker.Utils;


import java.util.HashMap;

/**
 * Constant values of the BalanceTracker functionality
 */
public class Constants {

    /**
     * Constant values of the BalanceTracker functionality
     */
    public static int TIMEFRAME = 600000;
    public static String NONSTORAGE_CONFIG = "none";

    /**
     * Event names
     */
    public static class EventNames {

        public static final String ACCOUNT_CREATED = "ACCOUNT_CREATED";
        public static final String ACCOUNT_UPDATED = "ACCOUNT_UPDATED";
        public static final String ACTION_CREATED = "ACTION_CREATED";
        public static final String ACTION_UPDATED = "ACCOUNT_UPDATED";
        public static final String CERTIFICATE_ISSUED = "CERTIFICATE_ISSUED";
        public static final String CERTIFICATE_REVOKED = "CERTIFICATE_REVOKED";
        public static final String FLAVOR_CREATED = "FLAVOR_CREATED";
        public static final String FLAVOR_UPDATED = "FLAVOR_UPDATED";
        public static final String KEY_CREATED = "KEY_CREATED";
        public static final String SNAPSHOT_CREATED = "SNAPSHOT_CREATED";
        public static final String SNAPSHOT_UPDATED = "SNAPSHOT_UPDATED";
        public static final String KEY_UPDATED = "KEY_UPDATED";
        public static final String TOKEN_UPDATED = "TOKEN_UPDATED";
        public static final String TOKEN_ISSUED = "TOKEN_ISSUED";
        public static final String TOKEN_TRANSFERED = "TOKEN_TRANSFERED";
        public static final String TOKEN_RETIRED = "TOKEN_RETIRED";
        public static final String TRANSACTION_CREATED = "TRANSACTION_CREATED";
        public static final String CONFIG_CREATED = "CONFIG_CREATED";
    }

    /**
     * Event names
     */
    public static class MessageNames {
        public static final String INIT_SUCCEEDED = "INIT_SUCCEEDED";
        public static final String INIT_ERROR = "BALANCETRACKER ERROR: Error at initialization";
        public static final String INPUT_ERROR = "BALANCETRACKER ERROR: Input error";
        public static final String BALANCE_TRACKER_ERROR = "BALANCETRACKER ERROR: BalanceTracker Error";
        public static final String GENERAL_ERROR = "BALANCETRACKER ERROR: General Error";
        public static final String FATAL_ERROR = "BALANCETRACKER ERROR: Fatal Error";

    }

    /**
     * Authentication parameters
     */
    public static class Auth {
        public static final String DEFAULT_MSP = "Org1MSP";
    }


    /**
     * Names of the model types
     */
    public static class ModelTypeNames {
        public static final String ACCOUNT = "Account";
        public static final String FLAVOR = "Flavor";
        public static final String ACTION = "Action";
        public static final String CERTIFICATE = "Certificate";
        public static final String KEY = "Key";
        public static final String SNAPSHOT = "Snapshot";
        public static final String TOKEN = "Token";
        public static final String TRANSACTION = "Transaction";
    }
}
