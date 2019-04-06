package com.example.Table_Top_Gaming;

import java.util.List;

/**
 * Interface to allow flexability around Deck Objects
 */
public interface Deck {
    void shuffle();
    PlayingCard drawCard();
    List<PlayingCard> drawHand(int numCards);
}
