package com.example.Table_Top_Gaming;

/**
 * This class represents a individual playing card with a number and suit
 */
public class PlayingCard {
    private Suit suit;
    private int number;


    /**
     * Create a new card with a number and suit
     * @param number set the number of the card to this number
     * @param suit set the suit of the card to this suit
     */
    PlayingCard(int number, Suit suit) {
        this.number = number;
        this.suit = suit;
    }

    Suit getSuit() {
        return suit;
    }

    public void setSuit(Suit suit) {
        this.suit = suit;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int compareTo(PlayingCard o) {
        return this.number - (o.getNumber());
    }
}
