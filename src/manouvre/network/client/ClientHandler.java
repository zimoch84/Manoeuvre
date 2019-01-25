/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.network.client;

import manouvre.network.core.Message;

/**
 *
 * @author Piotr
 */
public interface ClientHandler {
    public void handle (Message msg);
    
    
    
}
