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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VerbParser {

    private static Prompter prompter = new Prompter(new Scanner(System.in));


    private Object roomData;

    //every choice the player makes passes through here. Then delegates the task to function calls.
    //Functions calls should be abstracted out to a class to handle specific verb
    public String parseVerb(String choice, String activeRoom, Player player) throws IOException, ParseException {

        JSONObject allRooms = getRoomData();  //read in from resources/room_data.json
        JSONObject currentRoomData = (JSONObject) allRooms.get(activeRoom);

        Console.clear();

        switch (findChoiceSynonyms(choice)) {
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
                InventoryHandler.handleInventory(currentRoomData, player);
                break;
            case "map":
                MapHandler.handleMap(activeRoom);
                break;
            default:
                System.out.println("Enter a valid verb");
                break;
        }
        return activeRoom;

    }

    //reads in data for use in game
    //public to test
    public JSONObject getRoomData() throws IOException, ParseException {
        if (roomData == null) {
            roomData = new JSONParser().parse(new FileReader("resources/room_data.json"));
        }

        //NOTE: "resources/room_data.json" can be edited to change in game items, characters, etc.
        JSONObject room = (JSONObject) roomData;
        JSONObject rooms = (JSONObject) room.get("rooms");
        return rooms;
    }



    //funnels player input into 1 of 6 possibilities: move, talk, items, inventory, map, or "invalid entry"
    //Made public for testing
    public String findChoiceSynonyms(String choice) {
        //allows multiple verbs inputted by the user to trigger 'synonym' of in-game action
        String[] moveSynonyms = {"move", "walk", "run", "change room", "move room"};
        String[] talkSynonyms = {"talk", "speak", "converse", "chat", "interact"};
        String[] itemSynonyms = {"get", "items", "item", "take", "look", "find", "pick up"};
        String[] inventorySynonyms = {"inventory", "check inventory", "view inventory", "Inventory"};
        String[] mapSynonyms = {"map", "view map", "check map", "Map"};

        for (String word : moveSynonyms) {
            if (word.equals(choice)) {
                return "move";
            }
        }
        for (String word : talkSynonyms) {
            if (word.equals(choice)) {
                return "talk";
            }
        }
        for (String word : itemSynonyms) {
            if (word.equals(choice)) {
                return "items";
            }
        }
        for (String word : inventorySynonyms) {
            if (word.equals(choice)) {
                return "inventory";
            }
        }
        for (String word : mapSynonyms) {
            if (word.equals(choice)) {
                return "map";
            }
        }
        return "NONE";
    }


    // static classes below. Each to handle 1. character moving 2. Conversations with NPC's 3. Items and 4. Inventory
    private static class CharacterMover {

        static String movePlayer(String activeRoom, JSONObject currentRoomData, JSONObject allRooms, Player player) throws IOException, ParseException {
            JSONObject directions = (JSONObject) currentRoomData.get("directions");
            formatter.displayDoubleTable(directions,"\u001B[36m", "Direction", "Room");
            String leaveRoom = prompter.prompt("Would you like to leave the room, yes or no?", "yes|y|no|n","Invalid entry, please enter yes or no");
            if (leaveRoom.equals("yes") || leaveRoom.equals("y")) {
            String directionChoice = prompter.prompt("Which direction would you like to go?", "up|down|back|forward",
                    "Please enter a valid direction option.");
                //check if its valid direction for the current room
                if(directions.containsKey(directionChoice)) {
                    System.out.println("Moving to the requested direction " + directionChoice);
                } else {
                    System.out.println("Invalid direction. Can not move " + directionChoice + " from this room.");
                    return activeRoom;
                }
            //checks to see if player has the item needed to enter room they are trying to
            if (authorizePlayerToEnter((String) directions.get(directionChoice), player)) {
                //only change the active room if authorization to enter
                activeRoom = (String) directions.get(directionChoice);
                System.out.println(allRooms.get(activeRoom));
                return activeRoom;
            }
        }
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

    private static class ConversationHandler {
        private static void talkToCharacters(JSONObject currentRoomData, Player player) throws IOException, ParseException {
            formatter.displaySingleTable((JSONArray) currentRoomData.get("characters"),"\u001B[31m","CHARACTERS");
            JSONArray characters = (JSONArray) currentRoomData.get("characters");
            JSONObject characterDialogueData = getCharacterDialogueData();
            if(((JSONArray) currentRoomData.get("characters")).isEmpty()){
                System.out.println("There are no characters in this room to talk to!");
                return;
            }
            String characterChoice = prompter.prompt("Who would you like to talk to?");
            if (characters.contains(characterChoice)) {
                System.out.println("Valid character for the room");
                if (characterChoice.equals("stewardess")) {
                    //this is how game ends
                    if (player.getInventory().contains(Item.POISON) & player.getInventory().contains(Item.BOARDING_PASS)) {
                        //NOTE: endgame dialogue can be edited in "resources/endgame.json"
                        JSONObject endGameDialogue = (JSONObject) new JSONParser().parse(new FileReader("resources/endgame.json"));
                        System.out.println(endGameDialogue.get("end"));
                        player.setPlaying(false); //set isPlaying to "false" to break the game loop
                        GameTimeKeeper.timeKeeper = null;
                        new Game().execute();
                        return;
                    }
                }
            } else {
                System.out.println("Not a valid name or character is in another room");
                return;
            }
            JSONObject characterDialogue = (JSONObject) characterDialogueData; //this might be redundant
            System.out.println(characterDialogue.get(characterChoice));
            String command = prompter.prompt("Enter to exit");
        }

        private static JSONObject getCharacterDialogueData() throws IOException, ParseException {
            // NOTE: In game dialogue can be edited in "resources/character_dialogue.json"
            JSONObject characterDialogueData = (JSONObject) new JSONParser().parse(new FileReader("resources/character_dialogue.json"));
            return characterDialogueData;
        }
    }

    private static class ItemHandler {
        private static void handleItems(JSONObject currentRoomData, Player player) {
            JSONArray itemsArray = (JSONArray) currentRoomData.get("items"); // converts the JSON to a JSONARRAY
                // If the room has items ie all the items in the room haven't yet been Picked up
                if (itemsArray.size() != 0) {
                    // method that generates a list of the items into a more readable format
                    formatter.displaySingleTable(itemsArray,"\u001B[32m","ITEMS");
                    String addItem = prompter.prompt("Would you like to add any items to your inventory, yes or no?", "yes|y|no|n","Invalid entry, please enter yes or no");
                    if (addItem.equals("yes") || addItem.equals("y")) {
                        String itemSelected = prompter.prompt("Which item would you like to get?").toUpperCase();
                        String item = itemSelected.replace(" ", "_");
                        // Checks if the item entered by user is valid ie is in that specific room
                        boolean isValidItem = itemsArray.stream().anyMatch(it -> it.equals(itemSelected.toLowerCase()));

                        if (isValidItem) {
                            // checks if item is already in our inventory
                            if (player.getInventory().contains(Item.valueOf(item))) {
                                System.out.println("Item was already added to inventory, Try selecting a different item");
                            } else {
                                // Once an item is picked up, it is removed from the room
                                Object itemz = (Object) itemSelected.toLowerCase();
                                itemsArray.remove(itemz);
                                // Added to players inventory
                                player.addToInventory(Item.valueOf(item));
                                System.out.println("Item successfully added");
                            }
                        } else {
                            System.out.println("You entered an Invalid item");
                        }
                    }
                } else {
                    System.out.println("No items left, You've picked up all the items in the room");
                }
            String command = prompter.prompt("Enter to exit");
        }
    }

    private static class InventoryHandler {
        //view inventory and / or drop item from inventory
        private static void handleInventory(JSONObject currentRoomData, Player player) {
            if (player.getInventory().isEmpty()) {
                System.out.println("You don't have any items in your inventory");
            } else {
                player.displayInventory();
                String dropItem = prompter.prompt("Would you like to drop any items in your inventory, yes or no?", "yes|y|no|n","Invalid entry, please enter yes or no");
                if (dropItem.equals("yes") || dropItem.equals("y")) {
                    String itemSelected = prompter.prompt("Which of the above items would you like to drop from your inventory?");
                    System.out.println(deletedFromInventory(currentRoomData, player, itemSelected));
                }
            }
            String command = prompter.prompt("Enter to exit");
        }

        //            MAKING SURE USER INPUT IS A VALID ENUM
        private static String deletedFromInventory(JSONObject currentRoomData, Player player, String itemSelected) {
            List<String> itemsInTheGame = enumList();
            boolean itemExistOnAirCraft = itemsInTheGame.contains(itemSelected);
            if (itemExistOnAirCraft) {
                String item = itemSelected.replace(" ", "_").toUpperCase();
                String isDeleted = player.dropItem(Item.valueOf(item)) ? itemSelected
                        + " has been successfully removed" : itemSelected + " doesn't exist in your inventory";

                JSONArray itemsArray = (JSONArray) currentRoomData.get("items"); // converts the JSON to a JSONARRAY
                // Once an item is picked up, it is removed from the room
                Object itemz = (Object) itemSelected.toLowerCase();
                itemsArray.add(itemz);

                return isDeleted;
            } else {
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

    private static class MapHandler {
        // the activeRoom will be used to dynamically read the text file containing the corresponding room map
        private static void handleMap (String activeRoom) {
            try {
                Files.readAllLines(Path.of("resources/maps/" + activeRoom +".txt")).forEach(System.out::println);
                String command = prompter.prompt("Enter to exit");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//  outputs data in a more readeable and designed format
    private static class formatter{
         static void displaySingleTable(JSONArray data, String color, String title){
            int count = 15 - title.length();
            String leftAlignFormat = "| %-30s |%n";
            System.out.format(color + "*--------------------------------*%n");
            System.out.format("| "  + title + " ".repeat(count)+ "                |%n");
            System.out.format("+--------------------------------+%n");
            for (Object item: data) {
                System.out.format(leftAlignFormat, item);
            }
            System.out.format("*--------------------------------*%n" + "\u001B[0m");
        }

        static void displayDoubleTable(JSONObject data, String color, String title1, String title2){
             int count1 = 15 - title1.length();
             int count2 = 17 - title2.length();
            String leftAlignFormat = "| %-15s | %-17s |%n";
            System.out.format(color + "*-----------------+-------------------*%n");
            System.out.format("| " + title1 + " ".repeat(count1) + " | " + title2 + " ".repeat(count2) +  " |%n");
            System.out.format("+-----------------+-------------------+%n");
            data.forEach((key,value) -> System.out.format(leftAlignFormat,key, value));
            System.out.format("*-----------------+-------------------*%n" + "\u001B[0m");
        }
    }
}


