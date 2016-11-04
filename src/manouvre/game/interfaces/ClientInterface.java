/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.interfaces;

import manouvre.network.client.Message;

/**
 *
 * @author Piotr
 */
public interface ClientInterface {
    
    public void send(Message msgOut);
    
    public void handle(Message msgIn);
    
}
