/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.interfaces;

/**
 *
 * @author Bartosz
 */
public interface DiceInterface {
  public static int DICE1d6=0;
  public static int DICE1d8=1;
  public static int DICE1d10=2;
  public static int DICE2d6=3;
  public static int DICE2d8=4;
  public static int DICE2d10=5;
    
  public int getDiceType();  
  
  
  
}
