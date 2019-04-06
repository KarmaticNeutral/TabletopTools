package com.example.Table_Top_Gaming;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the PlayingCardDeck
 */
public class TestPlayingCardDeck {

    /**
     * Tests the constructor by creating a new deck and checking
     * that there are 52 cards in the deck.
     */
    @org.junit.Test
    public void testConstructor() {
        PlayingCardDeck d1 = new PlayingCardDeck();
        assertEquals(52, d1.getDeck().size());
    }

    /**
     * Tests the drawCard() method by creating a new deck,
     * then drawing cards from that deck.
     */
    @org.junit.Test
    public void testDrawCard() {
        PlayingCardDeck d1 = new PlayingCardDeck();
        d1.drawCard();
        assertEquals(51, d1.getDeck().size());

        d1.drawCard();
        assertEquals(50, d1.getDeck().size());
    }

    /**
     * Tests the drawHand() method by creating a new deck,
     * then drawing multiple cards from that deck until it
     * is empty.
     */
    @org.junit.Test
    public void testDrawHand() {
        PlayingCardDeck d1 = new PlayingCardDeck();
        d1.drawHand(1);
        assertEquals(51, d1.getDeck().size());

        d1.drawHand(10);
        assertEquals(41, d1.getDeck().size());

        d1.drawHand(41);
        assertEquals(0, d1.getDeck().size());
    }

    /**
     * Tests the drawHand() and drawCard() methods by attempting
     * to draw cards from an empty hand.
     */
    @org.junit.Test
    public void testDrawFromEmpty() {
        PlayingCardDeck d1 = new PlayingCardDeck();
        d1.drawHand(52);

        // We expect the deck to have 0 cards, rather than a
        // negative number of cards.
        d1.drawCard();
        assertEquals(0, d1.getDeck().size());

        d1.drawHand(10);
        assertEquals(0, d1.getDeck().size());
    }
}
