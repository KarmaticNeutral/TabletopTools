package com.example.Table_Top_Gaming;

import java.util.List;

public interface Deck {
    public void shuffle();
    public PlayingCard drawCard();
    public List<PlayingCard> drawHand(int numCards);
}
