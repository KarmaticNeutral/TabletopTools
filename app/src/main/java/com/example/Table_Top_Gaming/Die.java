package com.example.Table_Top_Gaming;

import java.util.Random;

public class Die {
    private int numSides;
    private Random rand;

    public void Die(int numSides) {

    }

    public int getNumSides() {
        return numSides;
    }

    public void setNumSides(int numSides) {
        this.numSides = numSides;
    }

    //returns a random number simulating a die roll
    int roll() {
        int num = rand.nextInt();
        num = (num % numSides) + 1;
        return num;
    }
}
