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
  public static int DICE1d6=6;
  public static int DICE1d8=8;
  public static int DICE1d10=10;
  public static int DICE2d6=12;
  public static int DICE2d8=16;
  public static int DICE2d10=20;
    
  public int getDiceType();  
  
  
  
}
