package com.nimble_four.AirMarshal;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String name;
    // has an array list of items
    private List<Item> inventory = new ArrayList<>();

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

    public List<Item> getInventory() {
        String items = "";
//        for (Item item: inventory) {
//            System.out.println(item);
//        }
        return inventory;
    }
}
