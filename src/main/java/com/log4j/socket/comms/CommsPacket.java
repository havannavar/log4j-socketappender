package com.log4j.socket.comms;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class CommsPacket implements Serializable
{
    private static final long serialVersionUID = -7669316691867252753L;

    private InetAddress sender;
    /**
     * A machines IP address or a '*' to indicate all machines
     */
    private String target;
    private String info;
    private Command command;
    private LogLevel logLevel;
    private int port;

    public CommsPacket()
    {
        try
        {
            sender = Inet4Address.getLocalHost();
        }
        catch (UnknownHostException ex)
        {
            // Not sure we should be logging here
            // log.error("Could not determine host name");
            System.out.println("Could not determine host name");
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Default getters and Setters">
    public InetAddress getSender()
    {
        return sender;
    }

    public void setSender(final Inet4Address sender)
    {
        this.sender = sender;
    }

    public Command getCommand()
    {
        return command;
    }

    public void setCommand(final Command command)
    {
        this.command = command;
    }

    public String getInfo()
    {
        return info;
    }

    public void setInfo(String info)
    {
        this.info = info;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(final String target)
    {
        this.target = target;
    }
    
    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }
    


    //</editor-fold>

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }
}
