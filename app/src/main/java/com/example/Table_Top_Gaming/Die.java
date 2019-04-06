package com.example.Table_Top_Gaming;

import java.util.Random;

/**
 * This class sets all the basic functions and variables that a "Die" should have
 */
public class Die implements Comparable<Die>{

    private String name;
    private int numSides;
    private Random rand;
    private int numRolled;

    /**
     * This non-default constructor accepts a variable that sets the number of sides and creates a
     * die with that amount set in the parameters.
     * @param numSides indicates the number of sides the die will have
     */
    Die(int numSides) {
        this.numSides = numSides;
        numRolled = 0;
        rand = new Random();
        name = "d" + Integer.toString(numSides);
    }

    /**
     * A getter for the number of sides on the die
     * @return the number of sides the die has
     */
    int getNumSides() {
        return numSides;
    }

    /**
     * A setter for the number of sides on the die
     * @param numSides sets the number of sides on the die
     */
    public void setNumSides(int numSides) {
        this.numSides = numSides;
    }

    /**
     * A getter for the random number rolled on the die
     * @return the randomly rolled number of the die
     */
    int getNumRolled() {
        return numRolled;
    }

    /**
     * A setter for the number rolled die (this function is never implemented)
     * @param numRolled set the number that was rolled on the die
     */
    public void setNumRolled(int numRolled) {
        this.numRolled = numRolled;
    }

    /**
     * Roll the die and set the randomly rolled number to "numRolled"
     */
    void roll() {
        numRolled = (rand.nextInt(numSides) % numSides) + 1;
    }

    /**
     * A getter for the name of the die
     * @return the name of the die
     */
    public String getName() {
        return name;
    }

    /**
     * A setter for the name of the die
     * @param name set the name of the die to the string passed in the parameters
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Overrides the compare function so dice can be compared and organized in a List
     * @param o is a die being passed in to be compared to this die
     * @return an int letting you know which die is greater or lesser than the other by the number of
     * sides they have
     */
    @Override
    public int compareTo(Die o) {
        return this.getNumSides() - (o.getNumSides());
    }
}
