package com.log4j.socketappender;

import org.apache.log4j.AsyncAppender;

public class AsynchLogAppender extends AsyncAppender {

	private AsynchListener controller;
    public AsynchLogAppender()
    {
         new AsynchListener(this).start();
    }
}
