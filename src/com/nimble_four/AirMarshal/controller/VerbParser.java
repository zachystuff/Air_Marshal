package com.nimble_four.AirMarshal.controller;

import com.apps.util.Console;
import com.apps.util.Prompter;
import com.nimble_four.AirMarshal.Item;
import com.nimble_four.AirMarshal.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VerbParser {

    private static Prompter prompter = new Prompter(new Scanner(System.in));

    //every choice the player makes passes through here. Then delegates the task to function calls.
    //Functions calls should be abstracted out to a class to handle specific verb
    public String parseVerb(String choice, String activeRoom, Player player) throws IOException, ParseException {
        JSONObject allRooms = getRoomData();  //read in from resources/room_data.json
        JSONObject currentRoomData = (JSONObject) allRooms.get(activeRoom);

        Console.clear();

        switch(findChoiceSynonyms(choice)){
            case "move":
                activeRoom = CharacterMover.movePlayer(activeRoom, currentRoomData, allRooms, player);
                break;
            case "talk":
                ConversationHandler.talkToCharacters(currentRoomData, player);
                break;
            case "items":
                ItemHandler.handleItems(currentRoomData, player);
                break;
            case "inventory":
                InventoryHandler.handleInventory(player);
                break;
            default:
                System.out.println("Enter a valid verb");
                break;
        }
        return activeRoom;
    }

    //reads in data for use in game
    private JSONObject getRoomData() throws IOException, ParseException {
        //NOTE: "resources/room_data.json" can be edited to change in game items, characters, etc.
        Object roomData = new JSONParser().parse(new FileReader("resources/room_data.json"));
        JSONObject room = (JSONObject) roomData;
        JSONObject rooms = (JSONObject) room.get("rooms");
        return rooms;
    }

    //funnels player input into 1 of 5 possibilities: move, talk, items, inventory, or "invalid entry"
    private String findChoiceSynonyms(String choice){
        //allows multiple verbs inputted by the user to trigger 'synonym' of in-game action
        String[] moveSynonyms = {"move", "walk", "run", "change room"};
        String[] talkSynonyms = {"talk", "speak", "converse", "chat"};
        String[] itemSynonyms = {"get", "items", "item", "take", "look", "find"};
        String[] inventorySynonyms = {"inventory", "check inventory", "view inventory", "my items"};

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
        for (String word : inventorySynonyms){
            if (word.equals(choice)){
                return "inventory";
            }
        }
        return "NONE";
    }


// static classes below. Each to handle 1. character moving 2. Conversations with NPC's 3. Items and 4. Inventory
    private static class CharacterMover {

        static String movePlayer(String activeRoom, JSONObject currentRoomData, JSONObject allRooms, Player player) throws IOException, ParseException {
            System.out.println(currentRoomData.get("directions"));
            String directionChoice = prompter.prompt("Which direction would you like to go?", "up|down|backwards|forward",
                    "Invalid direction chosen.");
            JSONObject directions = (JSONObject) currentRoomData.get("directions");
            //checks to see if player has the item needed to enter room they are trying to
            if (authorizePlayerToEnter((String) directions.get(directionChoice), player)) {
                //only change the active room if authorization to enter
                activeRoom = (String) directions.get(directionChoice);
                System.out.println(allRooms.get(activeRoom));
                return activeRoom;
            }
            ;
            return activeRoom;
        }

        //prevents player from entering certain rooms if they don't already have access to it via required items
        // POSTER -->  COCKPIT -- | -- AIRCRAFT GUIDE --> GALLEY -- | -- CARGO KEY --> CARGO
        static boolean authorizePlayerToEnter(String directionChoice, Player player) throws IOException, ParseException {
            switch (directionChoice) {
                //these require no keys or items to enter.
                case "bathroom":
                case "first class":
                case "commercial class":
                    return true;
                case "cockpit": //requires poster
                    if (player.getInventory().contains(Item.POSTER)) {
                        System.out.println("You gained access with your tour POSTER!");
                        return true;
                    }
                case "galley": //requires aircraft guide
                    if (player.getInventory().contains(Item.AIRCRAFT_GUIDE)) {
                        System.out.println("Your AIRCRAFT GUIDE allows you to navigate the lower deck!");
                        return true;
                    }
                case "cargo": //requires cargo key
                    if (player.getInventory().contains(Item.CARGO_KEY)) {
                        System.out.println("You unlocked the cargo room door!");
                        return true;
                    }
                default:
                    JSONObject closedGateDialogue = (JSONObject) new JSONParser().parse(new FileReader("resources/closed_gate_dialogue.json"));
                    System.out.println(closedGateDialogue.get(directionChoice));
                    return false;
            }
        }
    }

    private static class ConversationHandler{
        private static void talkToCharacters(JSONObject currentRoomData, Player player) throws IOException, ParseException {
            System.out.println(currentRoomData.get("characters"));
            JSONObject characterDialogueData = getCharacterDialogueData();
            String characterChoice = prompter.prompt("Who would you like to talk to?");
            if (characterChoice.equals("stewardess")){
                //this is how game ends
                if (player.getInventory().contains(Item.POISON) & player.getInventory().contains(Item.BOARDING_PASS)){
                    //NOTE: endgame dialogue can be edited in "resources/endgame.json"
                    JSONObject endGameDialogue =(JSONObject) new JSONParser().parse(new FileReader("resources/endgame.json"));
                    System.out.println(endGameDialogue.get("end"));
                    player.setPlaying(false); //set isPlaying to "false" to break the game loop
                }
                return;
            }
            JSONObject characterDialogue = (JSONObject) characterDialogueData;
            System.out.println(characterDialogue.get(characterChoice));
        }

        private static JSONObject getCharacterDialogueData() throws IOException, ParseException {
            // NOTE: In game dialogue can be edited in "resources/character_dialogue.json"
            JSONObject characterDialogueData = (JSONObject) new JSONParser().parse(new FileReader("resources/character_dialogue.json"));
            return characterDialogueData;
        }
    }

    private static class ItemHandler{
        private static void handleItems(JSONObject currentRoomData, Player player){
            System.out.println(currentRoomData.get("items"));
            String itemSelected = prompter.prompt("Which item would you like to get?").toUpperCase();
            String item = itemSelected.replace(" ", "_");
            JSONArray itemsArray = (JSONArray) currentRoomData.get("items"); // converts the JSON to a JSONARRAY
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

    }

    private static class InventoryHandler{
        //view inventory and / or drop item from inventory
        private static void handleInventory(Player player){
            if (player.getInventory().isEmpty()){
                System.out.println("You don't have any items in your inventory");
            } else{
                System.out.println(player.getInventory());
                String dropItem = prompter.prompt("Would you like to drop any items in your inventory, yes or no?");
                if(dropItem.equals("yes") || dropItem.equals("y")){
                    String itemSelected = prompter.prompt("Which of the above items would you like to drop from your inventory?");
                    System.out.println(deletedFromInventory(player, itemSelected));
                }
            }
        }

        //            MAKING SURE USER INPUT IS A VALID ENUM
        private static String deletedFromInventory(Player player, String itemSelected){
            List<String> itemsInTheGame = enumList();
            boolean itemExistOnAirCraft = itemsInTheGame.contains(itemSelected);
            if(itemExistOnAirCraft) {
                String item = itemSelected.replace(" ", "_").toUpperCase();
                String isDeleted = player.dropItem(Item.valueOf(item)) ? itemSelected
                        + " has been successfully deleted" : itemSelected + " doesn't exist in your inventory";
                return isDeleted;
            } else{
                return itemSelected + " doesn't exist in your inventory";
            }
        }

        // method that generates list of String Enums
        private static List<String> enumList() {
            List<String> itemList = new ArrayList<>();
            for (Item i : Item.values()) {
                itemList.add(i.toString());
            }
            return itemList;
        }
    }
}


