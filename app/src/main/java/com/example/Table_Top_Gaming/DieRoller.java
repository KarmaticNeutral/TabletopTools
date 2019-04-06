package com.example.Table_Top_Gaming;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is a list of dice objects that lets you roll the individual die and will display the
 * results in an orderly manner
 */
public class DieRoller {

    private List<Die> dice;
    private int sum;
    private int d4s;
    private int d6s;
    private int d8s;
    private int d10s;
    private int d20s;

    /**
     * Creates a new and empty list of dice and sets all variables to 0 because the list is empty
     */
    DieRoller() {
        dice = new ArrayList<>();
        sum = 0;
        d4s = 0;
        d6s = 0;
        d8s = 0;
        d10s = 0;
        d20s = 0;
    }

    /**
     * A setter for sum which is the sum total of all the dice after they are rolled
     * @param sum set the sum total of the dice rolled
     */
    public void setSum(int sum) {
        this.sum = sum;
    }

    /**
     * A getter for sum which is the sum total of all the dice after the are rolled
     * @return the sum total of the dice rolled
     */
    public int getSum() {
        return sum;
    }

    /**
     * A getter for the list of dice
     * @return the list of dice
     */
    List<Die> getDice() {
        return dice;
    }

    /**
     * A setter for the list of dice
     * @param dice sets this list of dice to the list of dice passed in
     */
    public void setDice(List<Die> dice) {
        this.dice = dice;
    }

    /**
     * Adds a die to the end of the list of dice
     * @param die is a die object to be added to the list of die
     */
    void addDie(Die die) {
        dice.add(die);
    }

    /**
     * Gets the size of the list of dice.
     */
    int getNumDice() {
        return dice.size();
    }

    /**
     * Removes a die from the end of the list of die
     */
    public void removeDie() {
        int lastDie = dice.size() - 1;

        if (!dice.isEmpty()) {
            dice.remove(lastDie);
        }
    }


    /**
     * Rolls all the dice, adds up all the rolls total into "sum" and counts the total of each
     * individual kind of die
     */
    void rollAllDice() {
        sum = 0;
        d4s = 0;
        d6s = 0;
        d8s = 0;
        d10s = 0;
        d20s = 0;

        // Loop through the list of dice
        for (int i = 0; i < dice.size(); i++) {
            // Roll all the dice
            dice.get(i).roll();

            // Add the number rolled to the sum
            sum += dice.get(i).getNumRolled();

            // Count the individual die types
            if (dice.get(i).getName().equals("d4")) {
                d4s++;
            }

            if (dice.get(i).getName().equals("d6")) {
                d6s++;
            }

            if (dice.get(i).getName().equals("d8")) {
                d8s++;
            }

            if (dice.get(i).getName().equals("d10")) {
                d10s++;
            }

            if (dice.get(i).getName().equals("d20")) {
                d20s++;
            }
        }
    }

    /**
     * Organize all the rolls into a string along with the total of all the rolls
     * @return a string of all the nicely organized total of the rolls and each individual roll
     */
    String display() {
        String rolls = "";
        Collections.sort(dice);

        if (d4s > 0) {
            rolls += Integer.toString(d4s) + "d4";

            if (d6s > 0 || d8s > 0 || d10s > 0 || d20s > 0) {
                rolls += " + ";
            }
        }

        if (d6s > 0) {
            rolls += Integer.toString(d6s) + "d6";

            if (d8s > 0 || d10s > 0 || d20s > 0) {
                rolls += " + ";
            }
        }

        if (d8s > 0) {
            rolls += Integer.toString(d8s) + "d8";

            if (d10s > 0 || d20s > 0) {
                rolls += " + ";
            }
        }

        if (d10s > 0) {
            rolls += Integer.toString(d10s) + "d10";

            if (d20s > 0) {
                rolls += " + ";
            }
        }

        if (d20s > 0) {
            rolls += Integer.toString(d20s) + "d20";
        }

        rolls += "\n";

        rolls += Integer.toString(sum) + " = ";

        rolls += "(";
        for (int i = 0; i < dice.size(); i ++) {
            if (dice.get(i).getName().equals("d4")) {
                rolls += Integer.toString(dice.get(i).getNumRolled());

                if ((i + 1) < dice.size()) {
                    if (dice.get(i + 1).getName().equals("d4")) {
                        rolls += " + ";
                    }
                    if (!dice.get(i + 1).getName().equals("d4")) {
                        rolls += ") + (";
                    }
                }
                if ((i + 1) == dice.size()) {
                    rolls += ")";
                }
            }

            if (dice.get(i).getName().equals("d6")) {
                rolls += Integer.toString(dice.get(i).getNumRolled());
                if ((i + 1) < dice.size()) {
                    if (dice.get(i + 1).getName().equals("d6")) {
                        rolls += " + ";
                    }
                    if (!dice.get(i + 1).getName().equals("d6")) {
                        rolls += ") + (";
                    }
                }
                if ((i + 1) == dice.size()) {
                    rolls += ")";
                }
            }

            if (dice.get(i).getName().equals("d8")) {
                rolls += Integer.toString(dice.get(i).getNumRolled());
                if ((i + 1) < dice.size()) {
                    if (dice.get(i + 1).getName().equals("d8")) {
                        rolls += " + ";
                    }
                    if (!dice.get(i + 1).getName().equals("d8")) {
                        rolls += ") + (";
                    }
                }
                if ((i + 1) == dice.size()) {
                    rolls += ")";
                }
            }

            if (dice.get(i).getName().equals("d10")) {
                rolls += Integer.toString(dice.get(i).getNumRolled());
                if ((i + 1) < dice.size()) {
                    if (dice.get(i + 1).getName().equals("d10")) {
                        rolls += " + ";
                    }
                    if (!dice.get(i + 1).getName().equals("d10")) {
                        rolls += ") + (";
                    }
                }
                if ((i + 1) == dice.size()) {
                    rolls += ")";
                }
            }

            if (dice.get(i).getName().equals("d20")) {
                rolls += Integer.toString(dice.get(i).getNumRolled());
                if ((i + 1) < dice.size()) {
                    if (dice.get(i + 1).getName().equals("d20")) {
                        rolls += " + ";
                    }
                }
                if ((i + 1) == dice.size()) {
                    rolls += ")";
                }
            }
        }

        return rolls;
    }
}
