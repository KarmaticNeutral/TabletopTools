package com.example.Table_Top_Gaming;

public class PlayingCard implements Card, Comparable<PlayingCard> {
    private Suit suit;
    private int number;


    public PlayingCard(int number, Suit suit) {
        this.number = number;
        this.suit = suit;
    }

    public Suit getSuit() {
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

    @Override
    public int compareTo(PlayingCard o) {
        return this.number - (o.getNumber());
    }

    @Override
    public void display() {

    }
}
