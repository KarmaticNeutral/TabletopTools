package com.example.Table_Top_Gaming;

import java.util.Random;

public class Die implements Comparable<Die>{

    private String name;
    private int numSides;
    private Random rand;
    private int numRolled;

    public Die() {
        numSides = 6;
        numRolled = 0;
        rand = new Random();
        name = "d" + Integer.toString(numSides);
    }

    public Die(int numSides) {
        this.numSides = numSides;
        numRolled = 0;
        rand = new Random();
        name = "d" + Integer.toString(numSides);
    }

    public int getNumSides() {
        return numSides;
    }

    public void setNumSides(int numSides) {
        this.numSides = numSides;
    }

    public int getNumRolled() {
        return numRolled;
    }

    public void setNumRolled(int numRolled) {
        this.numRolled = numRolled;
    }

    public void roll() {
        numRolled = (rand.nextInt(numSides) % numSides) + 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Die o) {
        return this.getNumSides() - (o.getNumSides());
    }
}
