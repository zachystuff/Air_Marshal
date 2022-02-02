package com.nimble_four.AirMarshal.controller;
import com.apps.util.Console;
import com.apps.util.Prompter;
import com.nimble_four.AirMarshal.Item;
import com.nimble_four.AirMarshal.Player;
import com.nimble_four.AirMarshal.music.MusicPlayer;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Game {
    private Player player = new Player();
    private Scanner scanner = new Scanner(System.in);
    private Prompter prompter = new Prompter(scanner);
    private String activeRoom = "commercial class";
    private VerbParser verbParser = new VerbParser();
    private GameTimeKeeper timer;


    public void execute() {
        gameIntro();
        startGame();
    }

    private void gameIntro() {
        // Reads game intro and instructions from data/json files at the beginning of the game
        try {
            Files.readAllLines(Path.of("resources/data/game_intro.txt")).forEach(System.out::println);
            Files.readAllLines(Path.of("resources/data/game_instructions.txt")).forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startGame() {
        player.setName(prompter.prompt("What is your name? "));
        String test = prompter.prompt("Please enter yes if you want to play? ", "yes|y", "Invalid choice: enter yes to play");
        // player is prompted, typing "yes" or "y" allows them to enter the game
        if (test.equals("yes") || test.equals("y")) {
            System.out.println("Enjoy the game Air Marshal " + player.getName());
            timer = new GameTimeKeeper(player, scanner);
            MusicPlayer.controller();
            turnLoop();
        }
    }

    private void turnLoop() {
        while (player.isPlaying()) {

            Console.clear();// clears console after every turn a player takes

            try {
                statusBar();
                System.out.println("You are currently in the " + activeRoom);
                if (player.getInventory().contains(Item.AIRCRAFT_GUIDE)) {
                    subMenu();
                }
                else {
                    menu();
                }
                String choice = prompter.prompt("What would you like to do? ");
                if (timer.isTimeLeft()) {
                    if (choice.equals("save")){
                        saveGame();
                        player.setPlaying(false);
                    }
                    else{
                        activeRoom = verbParser.parseVerb(choice, activeRoom, player); //this handles moving, talking, and taking items
                    }
                }
                else {
                    timer.gameOver(player, scanner);
                }

                // catch block because verbParser.parseVerb(...) uses a fileReader that requires exception handling
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    /*
     * to save we need 1. Player name 2. Player inventory 3. Current activeRoom 4. Current time left in game.
     * Then write data to JSON file (resources/saves/games.json). Key for JSON will be players name(?)
     */
    private void saveGame() {
        System.out.println("SAVE THE GAME NOW!");
        System.out.println("PLAYERS NAME: " + player.getName()); //1
        System.out.println("PLAYER INVENTORY:" + player.getInventory()); //2
        System.out.println("ACTIVE ROOM:" + activeRoom); //3
        System.out.println("CURRENT TIME LEFT" + timer.getCurrentTime()); //4
        HashMap<String,Object> data = new HashMap<>();
        data.put("inventory", player.getInventory());
        data.put("activeRoom", activeRoom);
        data.put("timeleft", timer.getCurrentTime());
        JSONObject newSaveData = new JSONObject();
        newSaveData.put(player.getName(), data);
        try{
            FileWriter file = new FileWriter("resources/saves/games.json");
            file.write(newSaveData.toJSONString());
            file.close();
            System.out.println("File Saved!");
        }catch (IOException e){
            System.out.println(e.getLocalizedMessage());
        }
    }

    // displays the actions a player can choose to do
    public void menu() {
        System.out.println("-- Menu Options --");
        System.out.println(
                "Your options are: \n" +
                        "  Move\n" +
                        "  Talk\n" +
                        "  Items \n" +
                        "  Inventory\n" +
                        "  Save"
        );
    }

    // add sub-menu
    // sub-menu will only show once the required item has been gathered
    public void subMenu() {
        System.out.println("-- Menu Options --");
        System.out.println(
                "Your options are: \n" +
                        "  Move\n" +
                        "  Talk\n" +
                        "  Items \n" +
                        "  Inventory\n" +
                        "  Map\n "
        );
    }

    // Displays a tab containing player name, current room & the time left
    public void statusBar() {

        System.out.println(
                " \n***************************************\n| "
                        + player.getName() + " | "
                        +  activeRoom + " | "
                        + timer.getCurrentTime() + " | " +
                        "\n***************************************\n"
        );
    }

    public void playAgain() {
        String response = prompter.prompt("Do you want to play again? yes or no? ", "yes|no|y|n", "Invalid Choice");
        if (response.equals("yes")|| response.equals("y")) {
            startGame();
        } else if (response.equals("no")|| response.equals("n")) {
            System.out.println("Thank you for playing! Hope you will play again!");
            System.exit(0);
        }
    }

}

