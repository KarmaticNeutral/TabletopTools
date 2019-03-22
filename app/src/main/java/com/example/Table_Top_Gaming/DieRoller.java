package com.example.Table_Top_Gaming;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DieRoller {

    private List<Die> dice;
    private int sum;
    private int d4s;
    private int d6s;
    private int d8s;
    private int d10s;
    private int d20s;

    public DieRoller() {
        dice = new ArrayList<>();
        sum = 0;
        d4s = 0;
        d6s = 0;
        d8s = 0;
        d10s = 0;
        d20s = 0;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getSum() {
        return sum;
    }

    public List<Die> getDice() {
        return dice;
    }

    public void setDice(List<Die> dice) {
        this.dice = dice;
    }

    public void addDie(Die die) {
        dice.add(die);
    }

    public void removeDie(Die die) {
        int lastDie = dice.size() - 1;

        dice.remove(lastDie);
    }


    //rolls all dice that the user wants
    public void rollAllDice() {
        sum = 0;
        d4s = 0;
        d6s = 0;
        d8s = 0;
        d10s = 0;
        d20s = 0;

        for (int i = 0; i < dice.size(); i++) {
            dice.get(i).roll();
            sum += dice.get(i).getNumRolled();
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

    public String display() {
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
        rolls += " = " + Integer.toString(sum);

        return rolls;
    }
}
