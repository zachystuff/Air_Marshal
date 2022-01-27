package com.nimble_four.AirMarshal.controller;

import com.apps.util.Prompter;
import com.nimble_four.AirMarshal.Item;
import com.nimble_four.AirMarshal.Player;
import com.nimble_four.AirMarshal.Room;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
//import org.json.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.*;
import org.json.simple.JSONObject;


public class Game {
    private Player player = new Player();
    private Prompter prompter = new Prompter(new Scanner(System.in));
    private String activeRoom = "commercial class";


    public void execute() {
        startGame();
    }

    public void startGame() {
        try {
            Files.readAllLines(Path.of("data/game_intro.txt")).forEach(System.out::println);
            Files.readAllLines(Path.of("data/game_instructions.txt")).forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String test = prompter.prompt("Please enter yes if you want to play? ", "yes|y", "Invalid choice: enter yes");

        if (test.equals("yes") || test.equals("y")) {
            System.out.println("Enjoy the game");
            turnLoop();
        }
    }

    private void turnLoop(){
        //you are in room "whatever". What would you like to do?
        //"move" "get" "interact"
        while(true){
            try{
                Object roomData =new JSONParser().parse(new FileReader("resources/room_data.json"));
                Object characterDialogueData =new JSONParser().parse(new FileReader("resources/character_dialogue.json"));
                JSONObject room = (JSONObject) roomData;
                JSONObject rooms = (JSONObject) room.get("rooms");
                System.out.println("You are currently in the " + activeRoom);
                String choice = prompter.prompt("What would you like to do? ", "move|talk|items", "Invalid choice: move get or interact");
                JSONObject roomDirections = (JSONObject) rooms.get(activeRoom);
                if(choice.equals("move")){
                    System.out.println(roomDirections.get("directions"));
                    String directionChoice = prompter.prompt("Which direction would you like to go?");
                    JSONObject directions = (JSONObject) roomDirections.get("directions");
                    activeRoom = (String) directions.get(directionChoice);
                    System.out.println(rooms.get(activeRoom));
                }
                else if(choice.equals("talk")){
                    System.out.println(roomDirections.get("characters"));
                    String characterChoice = prompter.prompt("Who would you like to talk to?");
                    JSONObject characterDialogue = (JSONObject) characterDialogueData;
                    System.out.println(characterDialogue.get(characterChoice));
                }
                else if(choice.equals("items")){
                    System.out.println(roomDirections.get("items"));
                    String itemSelected = prompter.prompt("Which item would you like to get?").toUpperCase();
                    String item = itemSelected.replace(" ","_");

                    if(player.getInventory().contains(Item.valueOf(item))){
                        System.out.println("Item was already added to inventory, Try selecting a different item");
                    } else {
                        player.addToInventory(Item.valueOf(item));
                        System.out.println("Item successfully added");
                        System.out.println("You currently have: \r" + player.getInventory());
                    }
                }
                // display list of items
                // collect items
                // task 306 & 268

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
