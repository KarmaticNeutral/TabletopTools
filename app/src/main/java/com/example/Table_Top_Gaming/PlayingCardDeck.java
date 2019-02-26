package com.example.Table_Top_Gaming;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayingCardDeck implements Deck{
    private List<PlayingCard> deck;

    /*
    This function creates the normal 52 cards in a deck and stores them in a list, then after all
    PlayingCards have been added the list is shuffled
     */
    public PlayingCardDeck() {
        // Create an empty list of PlayingCards
        deck = new ArrayList<>();

        // Create a card to be added to the deck
        PlayingCard card;

        int cardIndex = 1;
        int suitCount = 1;
        Suit suit;

        // Create the 52 unique PlayingCards
        for (int i = 0; i < 52; i++, cardIndex++) {
            if (cardIndex == 14) {
                cardIndex = 1;
                suitCount++;
            }

            suit = Suit.Hearts;

            if (suitCount == 2) {
                suit = Suit.Spades;
            }

            if (suitCount == 3) {
                suit = Suit.Clubs;
            }

            if (suitCount == 4) {
                suit = Suit.Diamonds;
            }

            card = new PlayingCard(cardIndex, suit);
            deck.add(card);
        }

        shuffle();
    }

    @Override
    public void shuffle() {
        // This shuffles the List of Playing Cards
        Collections.shuffle(deck);
    }

    @Override
    public Card drawCard() {
        return null;
    }

    @Override
    public List<Card> drawHand(int numCards) {
        return null;
    }
}
