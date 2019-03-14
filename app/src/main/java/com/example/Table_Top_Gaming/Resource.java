package com.example.Table_Top_Gaming;

public class Resource {
    private String name;
    private int amount;
    private double weight;

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
