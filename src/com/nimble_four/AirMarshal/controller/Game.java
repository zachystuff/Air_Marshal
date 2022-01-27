package com.nimble_four.AirMarshal.controller;

import com.apps.util.Prompter;
import com.nimble_four.AirMarshal.Room;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
//import org.json.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.*;
import org.json.simple.JSONObject;


public class Game {

    private Prompter prompter = new Prompter(new Scanner(System.in));
    private String activeRoom = "commercial class";


    public void execute() {
        startGame();
    }

    public void startGame() {
        try {
            Files.readAllLines(Path.of("data/game_intro")).forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String test = prompter.prompt("Please enter yes if you see this? ", "yes", "Invalid choice: enter yes");

        if (test.equals("yes")) {
            System.out.println("Successful");
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
                String choice = prompter.prompt("What would you like to do? ", "move|talk", "Invalid choice: move get or interact");
                if(choice.equals("move")){
                    JSONObject roomDirections = (JSONObject) rooms.get(activeRoom);
                    System.out.println(roomDirections.get("directions"));
                    String directionChoice = prompter.prompt("Which direction would you like to go?");
                    JSONObject directions = (JSONObject) roomDirections.get("directions");
                    activeRoom = (String) directions.get(directionChoice);
                    System.out.println(rooms.get(activeRoom));
                }
                else if(choice.equals("talk")){
                    JSONObject currentRoom = (JSONObject) rooms.get(activeRoom);
                    System.out.println(currentRoom.get("characters"));
                    String characterChoice = prompter.prompt("Who would you like to talk to?");
                    JSONObject characterDialogue = (JSONObject) characterDialogueData;
                    System.out.println(characterDialogue.get(characterChoice));
                }
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
