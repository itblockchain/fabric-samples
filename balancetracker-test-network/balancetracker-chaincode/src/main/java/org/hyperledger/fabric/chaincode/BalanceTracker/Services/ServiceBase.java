package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import org.hyperledger.fabric.chaincode.BalanceTracker.Main.MasterBL;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Config;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.Constants;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.protos.msp.*;


import java.util.List;

/**
 * Base ancestor class for services
 */
public abstract class ServiceBase implements ServiceInterface {

    public abstract List<String> requiredServiceNames();

    protected boolean checkString(String str) {
        if (str.trim().length() <= 0 || str == null)
            return false;
        return true;
    }

    /**
     * default implementation for the allowToRun
     * admin user is allowed to do everything
     * Org1MSP users are allowed to do everything
     * Plus test useage: if the getCreator is null, it is the same
     * It can only happen in test scenarios
     */
    public boolean isAllowedToRun(ChaincodeStub _stub) throws BalanceTrackerException{
        byte[] creator = _stub.getCreator();
        if (creator == null){
            return true;
        }

        try {
            Identities.SerializedIdentity identity = Identities.SerializedIdentity.parseFrom(creator);
            if (identity.getMspid().equals(Constants.Auth.DEFAULT_MSP)){
                return true;
            }
        }
        catch (Exception _ex){
            return false;
        }
        return false;
/*
        try {

            Identities.SerializedIdentity identity = Identities.SerializedIdentity.parseFrom(_stub.getCreator());

            StringReader reader = new StringReader(identity.getIdBytes().toStringUtf8());
            PemReader pr = new PemReader(reader);
            byte[] x509Data = pr.readPemObject().getContent();
            CertificateFactory factory = CertificateFactory.getInstance("X509");
            Certificate certificate = factory.generateCertificate(new ByteArrayInputStream(x509Data));

        } catch (Exception e) {
            e.printStackTrace();
        }

 */
    }

    /**
     * Default implementation for the decision If the service is configured to run
     */
    public boolean isConfiguredToRun(ChaincodeStub _stub) throws BalanceTrackerException{
        MasterBL masterBL = new MasterBL(_stub);
        Config config = masterBL.getConfig();
        if (config == null){
            throw new ConfigDoesNotExistException("Service config does not exsist");
        }

        List<String> services = config.getServices();
        String className = this.getClass().getSimpleName();
        // check if service exist
        if (services.contains(className)){
            List<String>  requiredServices = requiredServiceNames();
            // check dependency
            if (services.containsAll(requiredServices)){
                return true;
            }
        }
        return false;
    }

    /**
     * default implementation for the call service containing common patterns, like authentification and global configuration
     */
    public Object callService(List<String> args, ChaincodeStub _stub) throws BalanceTrackerException{
        // configured to run
        if (!isConfiguredToRun(_stub)){
            throw new NotConfiguredServiceException("Service is not correctly configured to run : " + this.getClass().getName());
        }
        // allowed to run
        if (!isAllowedToRun(_stub)){
            throw new NotAllowedServiceException("Service is not allowed to run : " + this.getClass().getName());
        }
        return null;
    }


    /**
     * EXCEPTION CLASSES
     */

    /**
     * Service not configured to run
     */
    public static class NotConfiguredServiceException extends BalanceTrackerException
    {
        protected Integer code = 5099;

        public NotConfiguredServiceException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }

    }

    /**
     * Service not configured to run
     */
    public static class ServiceDependencyNotMeet extends BalanceTrackerException
    {
        protected Integer code = 5099;

        public ServiceDependencyNotMeet(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }

    }


    /**
     * Service is not allowed to run for current user
     */
    public static class NotAllowedServiceException extends BalanceTrackerException
    {
        protected Integer code = 5099;

        public NotAllowedServiceException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

    /**
     * Service not configured to run
     */
    public static class ConfigDoesNotExistException extends BalanceTrackerException
    {
        protected Integer code = 5099;

        public ConfigDoesNotExistException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }

    }


}

