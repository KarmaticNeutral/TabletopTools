package com.example.Table_Top_Gaming;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the information that each player will need, including points, resources, picture, name, etc.
 */
public class Player {

    private String name;
    private String pathToImage;
    private URI image;
    private List<PlayingCard> hand;
    private boolean canDrawHand;
    private List<Resource> resources;

    /**
     * Creates a player using the specified name. Also, gives that player an empty hand, score of 0,
     * and the default image.
     * @param name Name of player.
     */
    public Player(String name) {

        this.name = name;
        pathToImage = null;
        hand = new ArrayList<>();
        canDrawHand = true;
        resources = new ArrayList<>();
        resources.add(new Resource("Score", 0));
    }

    /**
     * Retrieves the player name, which should already be set.
     * @return Name of player.
     */
    public String getName() {
        return name;
    }

    /**
     * Indicates whether the player is able to draw a card. A player cannot draw a card if the deck
     * is empty.
     * @return True if player can draw a card. Otherwise false.
     */
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

    public void setHand(List<PlayingCard> hand2) {
        if (!canDrawHand || hand2 == null) {
            return;
        }
        this.hand = hand2;
        canDrawHand = false;
    }

    public List<PlayingCard> getHand() {
        return hand;
    }

    public void addCardToHand(PlayingCard card) {
        hand.add(card);
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public List<Resource> getResources(){
        return resources;
    }
}

