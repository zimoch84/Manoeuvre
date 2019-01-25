/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.network.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.logging.log4j.LogManager;
/**
 *
 * @author Piotr
 */
public class PingClient {
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(PingClient.class.getName()); 
    private Socket socket;
    private boolean tryToReconnect = true;
    private final Thread heartbeatThread;
    private long heartbeatDelayMillis = 5000;

    public PingClient(final String server, final int port) {
        connect(server, port);
        heartbeatThread = new Thread() {
            public void run() {
                while (tryToReconnect) {
                    //send a test signal
                    try {
                        socket.getOutputStream().write(666);
                        sleep(heartbeatDelayMillis);
                    } catch (InterruptedException e) {
                        // You may or may not want to stop the thread here
                        // tryToReconnect = false;
                        
                        
                        
                        
                    } catch (IOException e) {
                        LOGGER.warn("Server is offline");
                        connect(server, port);
                    }
                }
            };
        };
        heartbeatThread.start();
    }

    private void connect(String server, int port){
        try {
            socket = new Socket(server, port);
        } catch (UnknownHostException e) {
            LOGGER.error(e, e);
        } catch (IOException e) {
            LOGGER.error(e, e);
        }
    }

    public void shutdown() {
        tryToReconnect = false;
    }
}

