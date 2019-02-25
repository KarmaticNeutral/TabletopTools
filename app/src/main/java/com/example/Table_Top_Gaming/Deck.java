package com.example.Table_Top_Gaming;

import java.util.List;

public interface Deck {
    public void shuffle();
    public Card drawCard();
    public List<Card> drawHand(int numCards);
}
