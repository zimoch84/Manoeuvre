package manouvre.game.interfaces;

import java.util.ArrayList;
import manouvre.game.Card;

/**
 *
 * @author Piotr
 */
public interface CardSetInterface {

   /**
    * Deal the card to another set, f.ex. to USED CADS
    * @param cardToDeal
    * @param otherCardSet 
    */
   public void moveCardTo(Card cardToDeal, CardSetInterface otherCardSet);
   
   /**
    * Add specyfic card to this set
    * @param newCard 
    */
   public void addCard(Card newCard);
   
   
   /**
    * Return card based on its position in the set
    * @param cardPosition
    * @return 
    */
   public Card getCardByPosInSet(int cardPosition);
   
  /**
   * As cards are dealt from the deck, the number of 
   * cards left decreases.  This function returns the 
   * number of cards that are still left in the deck.
   */
   public int size();


}