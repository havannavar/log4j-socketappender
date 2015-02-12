# log4j-socketappender
A custom Log4J's SocketAppender, communicates between logging server and various clients who invoke events.

How to use: 
AsynchListener - A notifier - Notifies whether a logging client is ready to invoke the notification when the logging server is ready on perticular port and machine.

EventListener - A serverseide event listener, which receives the event on the ports configured in a multicastaddress and pushes to a configured resource.

test/resources/log4j.xml - Shows how to apply the AsynchListener.


AsynchListener and EventListener can be deployed as two different modules in a server. A server can be either standalone or web application. 
