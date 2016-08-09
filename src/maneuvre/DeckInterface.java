package maneuvre;

/**
 *
 * @author Piotr
 */
public interface DeckInterface {
/**
   * Constructor.  Create an unshuffled deck of cards.
   */
   public void DeckInterface();



  /**
   * Put all the used cards back into the deck,
   * and shuffle it into a random order.
   */
   public void shuffle();

  /**
   * As cards are dealt from the deck, the number of 
   * cards left decreases.  This function returns the 
   * number of cards that are still left in the deck.
   */
   public int cardsLeft();

  /**
   * Deals one card from the deck and returns it.
   * @throws IllegalStateException if no more cards are left.
   */
   public CardInterface dealCard();

}