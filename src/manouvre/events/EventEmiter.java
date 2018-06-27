/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.events;

import java.util.Observable;
import manouvre.game.CardCommandFactory;


/**
 *
 * @author piotr_grudzien
 */
public class EventEmiter extends Observable {

    public void notifyAbout(Object eventType) {
        setChanged();
        notifyObservers(eventType);
    }


}
