package com.log4j.socket.comms;

import java.util.HashMap;
import java.util.Map;

public enum LogLevel 
{
    INFO,
    DEBUG,
    WARN,
    ERROR,
    FATAL,
    TRACE;
    
    Map<String, LogLevel> logLevel = new HashMap<>();
    
    public Map<String, LogLevel> getValues(){
        
        logLevel.put("INFO",LogLevel.INFO);
        logLevel.put("DEBUG",LogLevel.DEBUG);
        logLevel.put("WARN",LogLevel.WARN);
        logLevel.put("ERROR",LogLevel.ERROR);
        logLevel.put("FATAL",LogLevel.FATAL);
        logLevel.put("TRACE",LogLevel.TRACE);
        
        return logLevel;
    }
    
}
