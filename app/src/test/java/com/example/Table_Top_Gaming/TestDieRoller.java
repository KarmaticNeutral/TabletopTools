package com.example.Table_Top_Gaming;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the Die Roller class.
 */
public class TestDieRoller {

    /**
     * Test the default constructor, which should make an empty list of dice.
     */
    @org.junit.Test
    public void testConstructor() {
        DieRoller roller = new DieRoller();
        assertEquals(0, roller.getNumDice());
    }

    /**
     * Test the addDice() method by adding individual die one by one.
     */
    @org.junit.Test
    public void testAddDice() {
        DieRoller roller = new DieRoller();
        Die d1 = new Die(4);
        Die d2 = new Die(6);
        Die d3 = new Die(8);

        roller.addDie(d1);
        assertEquals(1, roller.getNumDice());

        roller.addDie(d2);
        assertEquals(2, roller.getNumDice());

        roller.addDie(d3);
        assertEquals(3, roller.getNumDice());
    }

    /**
     * Tests the removeDice() method by removing dice one by one
     * from the DieRoller.
     */
    @org.junit.Test
    public void testRemoveDice() {
        DieRoller roller = new DieRoller();
        Die d1 = new Die(4);
        Die d2 = new Die(6);
        Die d3 = new Die(8);

        roller.addDie(d1);
        roller.addDie(d2);
        roller.addDie(d3);

        assertEquals(3, roller.getNumDice());

        roller.removeDie();
        assertEquals(2, roller.getNumDice());
    }

    /**
     * Tests the removeDice() method by attempting to remove dice from an empty list.
     */
    @org.junit.Test
    public void testRemoveAll() {
        DieRoller roller = new DieRoller();
        Die d1 = new Die(4);
        Die d2 = new Die(6);
        Die d3 = new Die(8);

        roller.addDie(d1);
        roller.addDie(d2);
        roller.addDie(d3);

        roller.removeDie();
        roller.removeDie();
        roller.removeDie();
        assertEquals(0, roller.getNumDice());

        // Try to remove a die from an empty list
        roller.removeDie();
        assertEquals(0, roller.getNumDice());
    }

    /**
     * Tests the setDice() method by creating an array list,
     * adding multiple individual dice to the list, then
     * adding the list to the DieRoller.
     */
    @org.junit.Test
    public void testSetDice() {
        DieRoller roller = new DieRoller();
        Die d1 = new Die(4);
        Die d2 = new Die(6);
        Die d3 = new Die(8);
        List<Die> diceList = new ArrayList<>();

        // Add dice to a list, NOT to the die roller.
        diceList.add(d1);
        diceList.add(d2);
        diceList.add(d3);

        // Add the list to the die roller.
        roller.setDice(diceList);
        assertEquals(3, roller.getNumDice());
    }

    /**
     * Tests the setDice() method by making two lists of dice,
     * setting one list, then setting the second list. The
     * DieRoller should override the first list.
     */
    @org.junit.Test
    public void testMultipleSetDice() {
        DieRoller roller = new DieRoller();

        List<Die> diceList = new ArrayList<>();
        Die d1 = new Die(4);
        Die d2 = new Die(6);
        Die d3 = new Die(8);

        // Add dice to a list, NOT to the die roller.
        diceList.add(d1);
        diceList.add(d2);
        diceList.add(d3);

        // Die Roller should now have 3 dice.
        roller.setDice(diceList);

        // Make a new dice list
        List<Die> diceList2 = new ArrayList<>();
        Die d4 = new Die(10);
        Die d5 = new Die(12);
        Die d6 = new Die(20);
        Die d7 = new Die(23);

        diceList2.add(d4);
        diceList2.add(d5);
        diceList2.add(d6);
        diceList2.add(d7);

        // We expect the Die Roller to have 4 dice now.
        roller.setDice(diceList2);
        assertEquals(4, roller.getNumDice());
    }
}
