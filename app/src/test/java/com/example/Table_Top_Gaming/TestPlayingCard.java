package com.example.Table_Top_Gaming;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the PlayingCard class.
 */
public class TestPlayingCard {

    /**
     * Tests the constructor by adding 3 cards. The first is a simple
     * 2 of diamonds, the second is a jack of clubs, represented by the
     * number 11, and the third is a non-existing, but still valid,
     * 100 of diamond.
     */
    @org.junit.Test
    public void testConstructor() {
        PlayingCard c1 = new PlayingCard(2, Suit.diamond);
        assertEquals(2, c1.getNumber());
        assertEquals(Suit.diamond, c1.getSuit());

        PlayingCard c2 = new PlayingCard(11, Suit.club);
        assertEquals(11, c2.getNumber());
        assertEquals(Suit.club, c2.getSuit());

        PlayingCard c3 = new PlayingCard(100, Suit.diamond);
        assertEquals(100, c3.getNumber());
    }

    /**
     * Tests the setSuit() method by creating a new card, then changing
     * the suit of that card.
     */
    @org.junit.Test
    public void testSetSuit() {
        PlayingCard c1 = new PlayingCard(2, Suit.club);
        c1.setSuit(Suit.spade);
        assertEquals(Suit.spade, c1.getSuit());

        c1.setSuit(Suit.heart);
        assertEquals(Suit.heart, c1.getSuit());
    }

    /**
     * Tests the setNumber() method by creating a new card, then
     * changing the number of that card.
     */
    @org.junit.Test
    public void testSetNumber() {
        PlayingCard c1 = new PlayingCard(2, Suit.club);
        c1.setNumber(7);
        assertEquals(7, c1.getNumber());
    }

    /**
     * Tests the compareTo() method by creating two new cards,
     * then comparing one to the other.
     */
    @org.junit.Test
    public void testCompareTo() {
        PlayingCard c1 = new PlayingCard(2, Suit.diamond);
        PlayingCard c2 = new PlayingCard(10, Suit.diamond);

        int compare1 = c2.compareTo(c1);
        assertEquals(8, compare1);

        int compare2 = c1.compareTo(c2);
        assertEquals(-8, compare2);
    }
}
