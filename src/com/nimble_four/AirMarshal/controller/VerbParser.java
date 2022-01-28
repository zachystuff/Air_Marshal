package com.nimble_four.AirMarshal.controller;

import com.apps.util.Prompter;
import com.nimble_four.AirMarshal.Item;
import com.nimble_four.AirMarshal.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
                activeRoom = movePlayer(activeRoom, currentRoomData, allRooms);
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

    private String movePlayer(String activeRoom, JSONObject currentRoomData, JSONObject allRooms){
        System.out.println(currentRoomData.get("directions"));
        String directionChoice = prompter.prompt("Which direction would you like to go?");
        JSONObject directions = (JSONObject) currentRoomData.get("directions");
        activeRoom = (String) directions.get(directionChoice);
        System.out.println(allRooms.get(activeRoom));
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
        if (player.getInventory().contains(Item.valueOf(item))) {
            System.out.println("Item was already added to inventory, Try selecting a different item");
        } else {
            player.addToInventory(Item.valueOf(item));
            System.out.println("Item successfully added");
            System.out.println("You currently have: \r" + player.getInventory());
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
                return "item";
            }
        }
        return "NONE";
    }
}