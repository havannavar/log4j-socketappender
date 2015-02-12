package com.log4j.event;
import java.io.Serializable;

/**
 *
 * @author sats
 */
public class LogMessage implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String logLevel;
    private String logMessage;
    private String sender;
    private long date;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
    private String loggerName;


    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }



    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString(){
        return "["+sender+"]" + "["+date+"]" + "["+logLevel+"]" + "["+loggerName+"]" + "["+logMessage+"]" ;
    }

    private String application;
    public void setApplication(String mdc)
    {
        this.application = mdc;
    }

    public String getApplication()
    {
        return this.application;
    }

}
