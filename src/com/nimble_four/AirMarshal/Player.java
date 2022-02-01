package com.nimble_four.AirMarshal;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String name;
    // has an array list of items
    private List<Item> inventory = new ArrayList<>();
    // boolean that lets dictates whether the player is playing.
    // if true, keeps the while loop in the Game.java running
    private boolean playing = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addToInventory(Item item){
        if(item != null){
            addItem(item);
        }
    }

    private void addItem(Item item){
        inventory.add(item);
    }

    public boolean dropItem(Item item){
        return inventory.remove(item);
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public List<Item> getInventory() {
        String items = "";
//        for (Item item: inventory) {
//            System.out.println(item);
//        }
        return inventory;
    }
}
