package org.hyperledger.fabric.chaincode.BalanceTracker.Models;

import com.google.gson.Gson;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.ActionTypeEnum;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;

import java.util.List;

/**
 * Certificate model
 */
public class Certificate extends ModelBase   {

    protected String certificateId;
    protected String certificateValue;
    protected String certificateName;
    protected long issuedAt;
    protected long revokedAt;
    protected String ownerId;


    /**
     * Constructor - minimal
     */
    public Certificate(String _certificateId){

        this.certificateId = _certificateId;
        this.modelType = "Certificate";
    }

    /**
     * Constructor - normal
     */
    public Certificate(String _certificateId, String _certificateValue, String _certificateName, long  _issuedAt, Integer _revokedAt, String _ownerId){
        this.certificateId = _certificateId;
        this.certificateValue = _certificateValue;
        this.certificateName = _certificateName;
        this.issuedAt = _issuedAt;
        this.revokedAt = _revokedAt;
        this.ownerId = _ownerId;
        this.modelType = "Certificate";
    }

    /**
     * Getters
     */
    public String getCertificateId (){
        return certificateId;
    }

    public String getCertificateValue (){
        return certificateValue;
    }

    public String getCertificateName (){
        return certificateName;
    }

    public long getIssuedAt (){
        return issuedAt;
    }

    public long getRevokedAt (){
        return revokedAt;
    }

    public String getOwnerId (){
        return ownerId;
    }

    /**
     * Create action
     */
    public static Certificate createCertificate(String certificateString) throws Certificate.DeserializationException {

        try {

            Gson gson = new Gson();
            Certificate cert = gson.fromJson(certificateString, Certificate.class);

            return cert;
        }
        catch (Exception _ex){
            throw new Certificate.DeserializationException("There is a problem at reading out the certification from the blockchain ",  _ex);
        }
    }



    /**
     * EXCEPTIONS
     */

    /**
     * DeserializationException
     */
    public static class DeserializationException extends BalanceTrackerException
    {
        protected Integer code = 5058;

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
        protected Integer code = 5068;

        public SerializationException(String msg, Exception _ex) {
            super(msg, _ex);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }

    }



}
