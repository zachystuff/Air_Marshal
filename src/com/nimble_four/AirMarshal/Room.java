package com.nimble_four.AirMarshal;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String name;
    private Player player;
    private String playerOptions; // NOT YET SURE WHAT THIS IS FOR
    private Item items;    // So far each room has one item
    private List<Character> characters = new ArrayList<>(); // List of Characters present in the room

    // Neighboring Rooms
    private String forward;
    private String backwards;
    private String down;
    private String up;

    public Room(String name) {
        this.name = name;
        //this.items = items;
    }


    public void addCharacters(Character characters) {
        if(characters != null){
            this.characters.add(characters);
        }
    }

    public String getName() {
        return name;
    }

    public Player getPlayer() {
        return player;
    }

    public String getPlayerOptions() {
        return playerOptions;
    }

    public Item getItems() {
        return items;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public String getFoward() {
        return forward;
    }

    public String getBackwards() {
        return backwards;
    }

    public String getDown() {
        return down;
    }

    public String getUp() {
        return up;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlayerOptions(String playerOptions) {
        this.playerOptions = playerOptions;
    }

    public void setItems(Item items) {
        this.items = items;
    }

    public void setFoward(String foward) {
        this.forward = foward;
    }

    public void setBackwards(String backwards) {
        this.backwards = backwards;
    }

    public void setDown(String down) {
        this.down = down;
    }

    public void setUp(String up) {
        this.up = up;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "name='" + getName() + '\'' +
                ", items=" + getItems() + '\'' +
                ", characters=" + getCharacters().toString() +
                '}';
    }
}
