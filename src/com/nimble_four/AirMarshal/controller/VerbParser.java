package com.nimble_four.AirMarshal.controller;

import com.apps.util.Prompter;
import com.nimble_four.AirMarshal.Item;
import com.nimble_four.AirMarshal.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class VerbParser {

    private Prompter prompter = new Prompter(new Scanner(System.in));

    public String parseVerb(String choice, String activeRoom, Player player) throws IOException, ParseException {

        JSONObject allRooms = getRoomData();
        JSONObject currentRoomData = (JSONObject) allRooms.get(activeRoom);

        switch(findChoiceSynonyms(choice)){
            case "move":
                activeRoom = movePlayer(activeRoom, currentRoomData, allRooms, player);
                break;
            case "talk":
                talkToCharacters(currentRoomData);
                break;
            case "items":
                handleItems(currentRoomData, player);
                break;
            default:
                System.out.println("Enter a valid verb");
                break;
        }
        return activeRoom;
    }

    private JSONObject getRoomData() throws IOException, ParseException {
        Object roomData = new JSONParser().parse(new FileReader("resources/room_data.json"));
        JSONObject room = (JSONObject) roomData;
        JSONObject rooms = (JSONObject) room.get("rooms");
        return rooms;
    }

    private JSONObject getCharacterDialogueData() throws IOException, ParseException {
        JSONObject characterDialogueData = (JSONObject) new JSONParser().parse(new FileReader("resources/character_dialogue.json"));
        return characterDialogueData;
    }

    private String movePlayer(String activeRoom, JSONObject currentRoomData, JSONObject allRooms, Player player){
        System.out.println(currentRoomData.get("directions"));
        String directionChoice = prompter.prompt("Which direction would you like to go?");
        JSONObject directions = (JSONObject) currentRoomData.get("directions");
        //checks to see if player has the item needed to enter room they are trying to
        if (authorizePlayerToEnter((String)directions.get(directionChoice), player)){
            activeRoom = (String) directions.get(directionChoice);
            System.out.println(allRooms.get(activeRoom));
            return activeRoom;
        };
       return activeRoom;
    }

    private void talkToCharacters(JSONObject currentRoomData) throws IOException, ParseException {
        System.out.println(currentRoomData.get("characters"));
        JSONObject characterDialogueData = getCharacterDialogueData();
        String characterChoice = prompter.prompt("Who would you like to talk to?");
        JSONObject characterDialogue = (JSONObject) characterDialogueData;
        System.out.println(characterDialogue.get(characterChoice));
    }

    private void handleItems(JSONObject currentRoomData, Player player){
        System.out.println(currentRoomData.get("items"));
        String itemSelected = prompter.prompt("Which item would you like to get?").toUpperCase();
        String item = itemSelected.replace(" ", "_");

        // converts the JSON to a JSONARRAY
        JSONArray itemsArray = (JSONArray) currentRoomData.get("items");

        // Checks if the item entered by user is valid ie is in that specific room
        boolean isValidItem = itemsArray.stream().anyMatch(it -> it.equals(itemSelected.toLowerCase()));

        if(isValidItem) {
            // checks if item is already in our inventory
            if (player.getInventory().contains(Item.valueOf(item))) {
                System.out.println("Item was already added to inventory, Try selecting a different item");
            } else {
                player.addToInventory(Item.valueOf(item));
                System.out.println("Item successfully added");
                System.out.println("You currently have: \r" + player.getInventory());
            }
        } else {
            System.out.println("You entered an Invalid item");
        }
    }

    private String findChoiceSynonyms(String choice){
        String[] moveSynonyms = {"move", "walk", "run", "change room"};
        String[] talkSynonyms = {"talk", "speak", "converse", "chat"};
        String[] itemSynonyms = {"get", "items", "item", "take", "look", "find"};
        for(String word : moveSynonyms){
            if (word.equals(choice)){
                return "move";
            }
        }
        for (String word : talkSynonyms){
            if (word.equals(choice)){
                return "talk";
            }
        }
        for (String word : itemSynonyms){
            if (word.equals(choice)){
                return "items";
            }
        }
        return "NONE";
    }

    private boolean authorizePlayerToEnter(String directionChoice, Player player){

        switch(directionChoice){
            case "bathroom":
            case "first class":
            case "commercial class":
                return true;
            case "cockpit":
                if (player.getInventory().contains(Item.POSTER)){
                    System.out.println("You gained access with your tour POSTER!");
                    return true;
                }
            case "galley":
                if(player.getInventory().contains(Item.AIRCRAFT_GUIDE)){
                    System.out.println("Your AIRCRAFT GUIDE allows you to navigate the lower deck!");
                    return true;
                }
            case "cargo":
                if(player.getInventory().contains(Item.CARGO_KEY)){
                    System.out.println("You unlocked the cargo room door!");
                    return true;
                }
            default:
                System.out.println("You don't currently have access to this room");
                return false;
        }
    }
}
