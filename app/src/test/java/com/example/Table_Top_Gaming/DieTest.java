package com.example.Table_Top_Gaming;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the Die Class
 */
public class DieTest {
    /**
    Test each of the dice that we have on the Dice dialog.
     */
    @org.junit.Test
    public void testD4() {
        Die d4 = new Die(4);
        assertEquals(4, d4.getNumSides());
    }
    @org.junit.Test
    public void testD6() {
        Die d6 = new Die(6);
        assertEquals(6, d6.getNumSides());
    }
    @org.junit.Test
    public void testD8() {
        Die d8 = new Die(8);
        assertEquals(8, d8.getNumSides());
    }
    @org.junit.Test
    public void testD10() {
        Die d10 = new Die(10);
        assertEquals(10, d10.getNumSides());
    }
    @org.junit.Test
    public void testD20() {
        Die d20 = new Die(20);
        assertEquals(20, d20.getNumSides());
    }

    /**
     * Test a very large die
     */
    @org.junit.Test
    public void testDieLarge() {
        Die d = new Die(1000000);
        assertEquals(1000000, d.getNumSides());
    }

    /**
     * Test a very small die
     */
    @org.junit.Test
    public void testDieSmall() {
        Die d = new Die(0);
        assertEquals(0, d.getNumSides());
    }

    /**
     * Test an odd number of sides
     */
    public void testDieOdd() {
        Die d = new Die(13);
        assertEquals(13, d.getNumSides());
    }

    /**
     * Tests the setNumSides method of the Die class
     */
    @org.junit.Test
    public void testSetSides() {
        Die d = new Die(6);
        d.setNumSides(10);
        assertEquals(10, d.getNumSides());
    }

    /**
     * Tests the setNumRolled method
     */
    @org.junit.Test
    public void testSetRolled() {
        Die d = new Die(6);
        d.setNumRolled(5);
        assertEquals(5, d.getNumRolled());
    }

    /**
     * Tests the setName method
     */
    @org.junit.Test
    public void testSetName() {
        Die d = new Die(6);
        d.setName("Dice with 6 sides");
        assertEquals("Dice with 6 sides", d.getName());
    }

    /**
     * Tests the comparison method
     */
    @org.junit.Test
    public void testComapre() {
        Die d1 = new Die(6);
        Die d2 = new Die(10);
        int diff = d1.compareTo(d2);
        assertEquals(-4, diff);
    }

}
