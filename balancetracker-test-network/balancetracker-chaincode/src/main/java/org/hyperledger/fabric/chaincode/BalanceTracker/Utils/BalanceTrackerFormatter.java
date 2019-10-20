package org.hyperledger.fabric.chaincode.BalanceTracker.Utils;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class BalanceTrackerFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        return "\n BalanceTracker::"
                +record.getThreadID()+"::"
                +record.getSourceClassName()+"::"
                +record.getSourceMethodName()+"::"
                +new Date(record.getMillis())+"::"
                +record.getMessage()+"\n";
    }
}
