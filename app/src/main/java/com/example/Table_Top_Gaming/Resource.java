package com.example.Table_Top_Gaming;

/**
 * This class represents a resource that player would have and lets them manipulate that value
 */
public class Resource {
    private String name;
    private int amount;
    private double weight;

    /**
     * Create a new resource and set the name and amount value to the parameters passed into the function
     * @param name set the name of the new resource to this name
     * @param amount set the amount value of the new resource to this amount
     */
    public Resource(String name, int amount) {
        this.name = name;
        this.amount = amount;
        weight = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
