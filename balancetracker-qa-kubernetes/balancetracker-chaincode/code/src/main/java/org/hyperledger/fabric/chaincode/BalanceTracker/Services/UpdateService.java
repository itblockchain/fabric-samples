package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import org.hyperledger.fabric.chaincode.BalanceTracker.Main.BalanceTrackerBase;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.MasterBL;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Config;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerFormatter;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.JSONHelper;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Update service, reconfiguring the services
 */
public class UpdateService  extends ServiceBase implements ServiceInterface {
    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(InitServices.class.getName());

    {
        logger.setLevel(BalanceTrackerBase.logLevel);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new BalanceTrackerFormatter());
        if (logger.getHandlers().length == 0)
            logger.addHandler(ch);
    }

    /**
     * Required services
     */
    public List<String> requiredServiceNames(){
        List<String> requiredServices = new ArrayList<String>();
        return requiredServices;
    }

    /**
     * Creating key
     * Key id has to be externally generated
     *
     * @param  args arguments of the call
     * @param  _stub arguments of the call*
     * @return       object response
     */
    @Override
    public Object callService(List<String> args, ChaincodeStub _stub) throws BalanceTrackerException {
        logger.entering(this.getClass().getSimpleName(), "updateService()");

        // check basic config and auth
        super.callService(args, _stub);

        List<String> services;
        HashMap<String,String> storageModel;

        if (args.size() != 2)
            throw new InitServices.ParametersIncorrectNumberException("Incorrect number of arguments, expecting exactly 2");

        if (!checkString(args.get(0)))
            throw new InitServices.ParametersNotAStringException("Invalid argument, service list does not have a string format");

        if (!checkString(args.get(1)))
            throw new InitServices.ParametersNotAStringException("Invalid argument, storage model does not have a string format");

        try{

            storageModel = JSONHelper.createHashMapFromString(args.get(1));

        } catch (Exception ex){
            throw new CreateFlavor.ParameterNotCompatibleException("Invalid argument, storage model can not be converted to a value key store", ex);
        }

        try{

            services = JSONHelper.createArrayFromString(args.get(0));

        } catch (Exception ex){
            throw new CreateFlavor.ParameterNotCompatibleException("Invalid argument, services can not be converted to a string array", ex);
        }

        MasterBL masterBL = new MasterBL(_stub);
        Config ret = masterBL.updateConfig(services,storageModel);

        logger.exiting(this.getClass().getSimpleName(), "updateService()");
        return ret;
    }

    /**
     * Create service can run if it is still not set
     */
    @Override
    public boolean isConfiguredToRun(ChaincodeStub _stub) throws BalanceTrackerException{
        MasterBL masterBL = new MasterBL(_stub);
        Config config = masterBL.getConfig();
        if (config == null){
            return true;
        }
        else {
            return false;
        }
    }



}
