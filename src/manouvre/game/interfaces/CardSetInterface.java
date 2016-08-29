package manouvre.game.interfaces;

import manouvre.game.Card;

/**
 *
 * @author Piotr
 */
public interface CardSetInterface {

   
   /**
   * Shhuffle DECK into a random order
   */
   public void makeDeck(int range);
   
   /**
    * Add random cards from the DECK to HAND
    * @param range
    * @param otherCardSet 
    */
   public void addRandomCardsFromOtherSet(int range, CardSetInterface otherCardSet);
   
   /**
    * Deal the card to another set, f.ex. to USED CADS
    * @param cardToDeal
    * @param otherCardSet 
    */
   public void dealCardToOtherSet(Card cardToDeal, CardSetInterface otherCardSet);
   
   /**
    * Add specyfic card to this set
    * @param newCard 
    */
   public void addCardToThisSet(Card newCard);
   
   /**
    * Deal random card from this set
    * @return 
    */
   public Card dealRandomCardFromThisSet();
   
   /**
    * Show all the Card Names in the set
    */
   public void getAllCardsNamesFromSet();
   
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
   public int cardsLeftInSet();


}