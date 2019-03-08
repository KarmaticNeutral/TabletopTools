package com.example.Table_Top_Gaming;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String name;
    private String pathToImage;
    private List<PlayingCard> hand;
    private boolean canDrawHand;

    public Player(String name) {

        this.name = name;
        pathToImage = null;
        hand = new ArrayList<>();
        canDrawHand = true;
    }

    public String getName() {
        return name;
    }

    public boolean canDraw() {
        return canDrawHand;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathToImage() {
        return pathToImage;
    }

    public void setPathToImage(String pathToImage) {
        this.pathToImage = pathToImage;
    }

    public void setHand(List<PlayingCard> hand) {
        if (!canDrawHand) {
            return;
        }
        this.hand = hand;
        canDrawHand = false;
    }

    public List<PlayingCard> getHand() {
        return hand;
    }

    public void addCardToHand(PlayingCard card) {
        hand.add(card);
    }


}

