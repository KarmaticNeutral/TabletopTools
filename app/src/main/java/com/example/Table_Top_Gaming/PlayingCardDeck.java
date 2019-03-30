package com.example.Table_Top_Gaming;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents an normal deck of 52 cards
 */
public class PlayingCardDeck implements Deck{
    private List<PlayingCard> deck;

    /**
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

            suit = Suit.heart;

            if (suitCount == 2) {
                suit = Suit.spade;
            }

            if (suitCount == 3) {
                suit = Suit.club;
            }

            if (suitCount == 4) {
                suit = Suit.diamond;
            }

            card = new PlayingCard(cardIndex, suit);
            deck.add(card);
        }

        // All the PlayingCards are in the deck so it's time to shuffle it
        shuffle();
    }

    /**
     * Shuffle the deck of cards
     */
    @Override
    public void shuffle() {
        // This shuffles the List of Playing Cards
        Collections.shuffle(deck);
    }

    /**
     * Draw a card
     * @return the card drawn
     */
    @Override
    public PlayingCard drawCard() {
        if (!deck.isEmpty()) {
            PlayingCard card = deck.get(0);
            deck.remove(0);
            return card;
        }
        else {
            return null;
        }
    }

    /**
     * Draw a hand from the deck
     * @param numCards is the number of cards to be drawn
     * @return a list of cards that represents the cards drawn
     */
    @Override
    public List<PlayingCard> drawHand(int numCards) {
        List<PlayingCard> hand = new ArrayList<>();
        if (!deck.isEmpty()) {
            while (!deck.isEmpty() && numCards > 0) {
                hand.add(deck.get(0));
                deck.remove(0);
                numCards--;
            }
        }
        if (deck.isEmpty()) {
            return null;
        }
        return hand;
    }


    public List<PlayingCard> getDeck() {
        return this.deck;
    }
}
