/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.network.core;

import java.io.Serializable;

/**
 *
 * @author Piotr
 */
public class User implements Serializable{
    
    public String name;

    public User(String name) {
        this.name = name;
    }

    public User() {
    }
   
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        User usrIn =  (User) obj;
        return name.equals(usrIn.getName());
    }
    
    
}
