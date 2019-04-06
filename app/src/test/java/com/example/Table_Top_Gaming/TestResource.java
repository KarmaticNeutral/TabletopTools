package com.example.Table_Top_Gaming;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the Resource class.
 */
public class TestResource {
    /**
     * Tests the constructor by making 2 new resources.
     */
    @org.junit.Test
    public void testConstructor() {
        Resource r1 = new Resource("A", 1);
        assertEquals("A", r1.getName());
        assertEquals(1, r1.getAmount());

        Resource r2 = new Resource("", 0);
        assertEquals("", r2.getName());
        assertEquals(0, r2.getAmount());
    }

    /**
     * Tests the setName() method by making a new resource, then
     * changing the name of that resource multiple times.
     */
    @org.junit.Test
    public void testSetName() {
        Resource r1 = new Resource("A", 1);
        r1.setName("Trains");
        assertEquals("Trains", r1.getName());

        r1.setName("Planes");
        assertEquals("Planes", r1.getName());

        r1.setName(Integer.toString(100));
        assertEquals("100", r1.getName());
    }

    /**
     * Tests the setAmount() method by making a new resource, then
     * changing the amount to a very large int, then to 0.
     */
    @org.junit.Test
    public void testSetAmount() {
        Resource r1 = new Resource("Automobiles", 1);
        r1.setAmount(999999999);
        assertEquals(999999999, r1.getAmount());

        r1.setAmount(0);
        assertEquals(0, r1.getAmount());
    }
}
