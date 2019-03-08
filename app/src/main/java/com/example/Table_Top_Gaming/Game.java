package com.example.Table_Top_Gaming;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<Player> players;
    private PlayingCardDeck deck;

    public Game() {
        players = new ArrayList<>();
        deck = new PlayingCardDeck();
    }

    public Game(List<Player> players) {
        this.players = players;
        deck = new PlayingCardDeck();
    }

    public PlayingCardDeck getDeck() {
        return deck;
    }

    public void setDeck(PlayingCardDeck deck) {
        this.deck = deck;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
