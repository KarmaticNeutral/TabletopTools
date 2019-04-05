package com.example.Table_Top_Gaming;

import java.util.ArrayList;
import java.util.List;

/**
 * This class keeps all the information of the game and individual players including a list of
 * players a deck of cards and a discard pile
 */
public class Game {
    private List<Player> players;
    private PlayingCardDeck deck;
    private List<PlayingCard> discardPile;

    /**
     *  Default constructor for the Game Class. Gives default values for the contained variables.
     */
    public Game() {
        players = new ArrayList<>();
        deck = new PlayingCardDeck();
        discardPile = new ArrayList<>();
    }

    /**
     * Alternate Constructor for the game class that takes in a predefined List of players.
     * @param players - List of Player objects to be represented in score tracking.
     */
    public Game(List<Player> players) {
        this.players = players;
        deck = new PlayingCardDeck();
    }

    List<PlayingCard> getDiscardPile() {
        return discardPile;
    }

    public void setDiscardPile(List<PlayingCard> discardPile) {
        this.discardPile = discardPile;
    }

    PlayingCardDeck getDeck() {
        return deck;
    }

    public void setDeck(PlayingCardDeck deck) {
        this.deck = deck;
    }

    List<Player> getPlayers() {
        return players;
    }

    /**
     * Basic setter for the player list that will write over existing players.
     * @param players A list of the players playing the game
     */
    void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * Add a new Player.
     * @param newPlayer - The player to be added.
     */
    public void addPlayer(Player newPlayer) {
        players.add(newPlayer);
    }

    /**
     * Remove the player at the designated index.
     * @param index the number of player that you want to remove
     */
    public void removePlayer(int index) {
        players.remove(index);
    }
}
