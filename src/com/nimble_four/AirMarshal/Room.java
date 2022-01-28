package com.nimble_four.AirMarshal;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String name;
    private String playerOptions; // NOT YET SURE WHAT THIS IS FOR
    private List<Character> characters = new ArrayList<>(); // List of Characters present in the room

    public Room(String name) {
        this.name = name;
    }

    public void addCharacters(Character characters) {
        if(characters != null){
            this.characters.add(characters);
        }
    }

    public String getName() {
        return name;
    }

    public String getPlayerOptions() {
        return playerOptions;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlayerOptions(String playerOptions) {
        this.playerOptions = playerOptions;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "name='" + getName() + '\'' +
                ", characters=" + getCharacters().toString() +
                '}';
    }
}
