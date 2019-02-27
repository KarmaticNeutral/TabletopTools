package com.example.Table_Top_Gaming;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Test {
    @org.junit.Test
    public void testNames() {
        Player p = new Player("Joe");
        assertEquals("Joe", p.getName());
    }

    @org.junit.Test
    public void testNumPlayers() {
        Game g = new Game();
        List<Player> playerList = g.getPlayers();
        Player p1 = new Player("1");
        Player p2 = new Player("2");
        Player p3 = new Player("3");
        playerList.add(p1);
        playerList.add(p2);
        playerList.add(p3);

        g.setPlayers(playerList);

        assertEquals(3, g.getPlayers().size());
    }

    @org.junit.Test
    public void testPlayingCardDeck(){
        PlayingCardDeck d = new PlayingCardDeck();
        assertEquals(52, d.getDeck().size());


    }
}