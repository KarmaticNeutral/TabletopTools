package com.example.Table_Top_Gaming;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the Game class.
 */
public class TestGame {
    /**
     * Tests the default constructor by asserting that there are 0 players,
     * 0 cards discarded, and 52 cards in the deck.
     */
    @org.junit.Test
    public void testConstructor() {
        Game g = new Game();

        // We expect to have 0 players, 0 cards discarded, and 52 cards
        // in the deck.
        assertEquals(0, g.getPlayers().size());
        assertEquals(0, g.getDiscardPile().size());
        assertEquals(52, g.getDeck().getDeck().size());
    }

    /**
     * Tests the non-default constructor by adding 3 players to a list,
     * and passing that list to a new game.
     */
    @org.junit.Test
    public void testNonDefaultConstructor() {
        Player p1 = new Player("A");
        Player p2 = new Player("B");
        Player p3 = new Player("C");
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);

        Game g = new Game(players);
        assertEquals(3, g.getPlayers().size());
    }

    /**
     * Tests the addPlayer() method by adding 3 players directly to
     * a game.
     */
    @org.junit.Test
    public void testAddPlayer() {
        Player p1 = new Player("A");
        Player p2 = new Player("B");
        Player p3 = new Player("C");
        Game g = new Game();

        assertEquals(0, g.getPlayers().size());
        g.addPlayer(p1);
        assertEquals(1, g.getPlayers().size());
        g.addPlayer(p2);
        g.addPlayer(p3);
        assertEquals(3, g.getPlayers().size());
    }

    /**
     * Tests the removePlayer() method by adding 3 players to a
     * game and then removing those players one by one.
     */
    @org.junit.Test
    public void testRemovePlayer() {
        Player p1 = new Player("A");
        Player p2 = new Player("B");
        Player p3 = new Player("C");
        Game g = new Game();

        g.addPlayer(p1);
        g.addPlayer(p2);
        g.addPlayer(p3);

        g.removePlayer(0);
        assertEquals(2, g.getPlayers().size());
        g.removePlayer(1);
        assertEquals(1, g.getPlayers().size());
        g.removePlayer(2);
        assertEquals(0, g.getPlayers().size());
    }

    /**
     * Tests the removePlayer() method by attempting to remove a
     * non-existing Player
     */
    @org.junit.Test
    public void testRemoveAllPlayers() {
        Game g = new Game();
        g.removePlayer(0);

        assertEquals(0, g.getPlayers().size());
    }

    /**
     * Tests the setPlayers() method by adding 3 players to a list,
     * and adding that list to a game.
     */
    @org.junit.Test
    public void testSetPlayers() {
        Player p1 = new Player("A");
        Player p2 = new Player("B");
        Player p3 = new Player("C");
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);

        Game g = new Game();

        g.setPlayers(players);
        assertEquals(3, g.getPlayers().size());
    }

    /**
     * Tests the setPlayers() method by making two lists of players,
     * setting one list to the game, then setting the second list
     * to the game. The game should use only the second list.
     */
    @org.junit.Test
    public void testSetMultiplePlayers() {
        Player p1 = new Player("A");
        Player p2 = new Player("B");
        Player p3 = new Player("C");
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);

        Game g = new Game();

        g.setPlayers(players);

        Player p4 = new Player("D");
        Player p5 = new Player("E");
        Player p6 = new Player("F");
        Player p7 = new Player("G");
        List<Player> players2 = new ArrayList<>();
        players2.add(p4);
        players2.add(p5);
        players2.add(p6);
        players2.add(p7);

        g.setPlayers(players2);

        assertEquals(4, g.getPlayers().size());
    }

    /**
     * Tests the setDeck() method by creating a new deck, drawing
     * some cards from that deck, then setting the game deck to that
     * deck.
     */
    @org.junit.Test
    public void testSetDeck() {
        PlayingCardDeck deck = new PlayingCardDeck();
        assertEquals(52, deck.getDeck().size());

        deck.drawCard();
        assertEquals(51, deck.getDeck().size());

        deck.drawHand(10);
        assertEquals(41, deck.getDeck().size());

        Game g = new Game();
        g.setDeck(deck);
        assertEquals(41, g.getDeck().getDeck().size());
    }

    /**
     * Tests the setDiscardPile() method by making a discard list,
     * adding 3 cards to that list, and adding that list to a game.
     */
    @org.junit.Test
    public void testSetDiscardPile() {
        List<PlayingCard> discard = new ArrayList<>();
        PlayingCard c1 = new PlayingCard(2, Suit.diamond);
        PlayingCard c2 = new PlayingCard(2, Suit.club);
        PlayingCard c3 = new PlayingCard(2, Suit.heart);

        discard.add(c1);
        discard.add(c2);
        discard.add(c3);

        Game g = new Game();
        g.setDiscardPile(discard);
        assertEquals(3, g.getDiscardPile().size());
    }
}
