package com.example.Table_Top_Gaming;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestPlayer {
    @org.junit.Test
    public void testConstructor() {
        Player p1 = new Player("A");
        assertEquals("A", p1.getName());

        Player p2 = new Player(Integer.toString(1));
        assertEquals("1", p2.getName());
        assertTrue(p2.canDraw());
        assertEquals(1, p2.getResources().size());
    }

    @org.junit.Test
    public void testSetName() {
        Player p1 = new Player("A");
        p1.setName("NotA");
        assertEquals("NotA", p1.getName());
    }

    @org.junit.Test
    public void testSetHand() {
        PlayingCard c1 = new PlayingCard(2, Suit.diamond);
        PlayingCard c2 = new PlayingCard(2, Suit.club);
        PlayingCard c3 = new PlayingCard(2, Suit.heart);

        List<PlayingCard> cards = new ArrayList<>();
        cards.add(c1);
        cards.add(c2);
        cards.add(c3);

        Player p1 = new Player("Tony Stark");
        p1.setHand(cards);
        assertEquals(3, p1.getHand().size());
    }

    @org.junit.Test
    public void testAddCard() {
        PlayingCard c1 = new PlayingCard(2, Suit.diamond);

        Player p1 = new Player("Steve Rogers");
        p1.addCardToHand(c1);
        assertEquals(1, p1.getHand().size());
    }

    @org.junit.Test
    public void testSetResources() {
        Resource r1 = new Resource("Arrows", 100);
        Resource r2 = new Resource("Knives", 2);
        List<Resource> resources = new ArrayList<>();

        Player p1 = new Player("Clint Barton");
        p1.setResources(resources);

        assertEquals(2, p1.getResources().size());
    }
}
