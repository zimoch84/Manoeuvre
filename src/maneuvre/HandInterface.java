/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maneuvre;

/**
 *
 * @author Piotr
 */
  
    public interface HandInterface {

     /**
     * Constructor. Create a Hand object that is initially empty.
     */
    public void HandInterface();
    /**
     * Discard all cards from the hand, making the hand empty.
     */
    public void clear();

    /**
     * Add the card c to the hand.  c should be non-null.
     * @throws NullPointerException if c is null.
     */
    public void addCard(CardInterface c);

    /**
     * If the specified card is in the hand, it is removed.
     */
    public void removeCard(CardInterface c);

    /**
     * Remove the card in the specified position from the
     * hand.  Cards are numbered counting from zero.
     * @throws IllegalArgumentException if the specified 
     *    position does not exist in the hand.
     */
    public void removeCard(int position);

    /**
     * Return the number of cards in the hand.
     */
    public int getCardCount();

    /**
     * Get the card from the hand in given position, where 
     * positions are numbered starting from 0.
     * @throws IllegalArgumentException if the specified 
     *    position does not exist in the hand.
     */
    public CardInterface getCard(int position);

    /**
     * Sorts the cards in the hand so that cards of the same 
     * suit are grouped together, and within a suit the cards 
     * are sorted by value.
     */
    public void sortBySuit();

    /**
     * Sorts the cards in the hand so that cards are sorted into
     * order of increasing value.  Cards with the same value 
     * are sorted by suit. Note that aces are considered
     * to have the lowest value.
     */
    public void sortByValue();

}