/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.network.server;

import manouvre.network.core.User;

/**
 *
 * @author Piotr
 */
interface AuthorizitionControl {
    
    boolean authorize(User user, String password);
    boolean addUser(User user, String password);

}
