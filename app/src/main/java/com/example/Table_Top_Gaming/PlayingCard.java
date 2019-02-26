package com.example.Table_Top_Gaming;

public class PlayingCard implements Card {
    private Suit suit;
    private int number;


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
    public void display() {

    }
}
