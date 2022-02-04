package com.nimble_four.AirMarshal.controller;
import com.apps.util.Console;
import com.apps.util.Prompter;
import com.nimble_four.AirMarshal.Item;
import com.nimble_four.AirMarshal.Player;
import com.nimble_four.AirMarshal.music.MusicPlayer;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Game {
    private Player player;
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
        Console.clear();
        try {
            Files.readAllLines(Path.of("resources/data/game_banner.txt")).forEach(System.out::println);
            Thread.sleep(2000);
            Console.clear();
            Files.readAllLines(Path.of("resources/data/game_intro.txt")).forEach(System.out::println);
            Thread.sleep(5000);
            String move = prompter.prompt("Enter to continue");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startGame() {
        Console.clear();
        // player is prompted with play game menu options
        try {
            Files.readAllLines(Path.of("resources/data/game_banner.txt")).forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
        printMenu("gameOptions");
        //playGameOptions();
        player = new Player();
        String choice = prompter.prompt("Please enter your choice: \n-> ", "1|2|3|4", "Invalid choice: enter 1, 2, 3, or 4");
        Console.clear();
        if(Integer.parseInt(choice) == 2) {
            System.out.println("Hope you will come back again!");
            System.exit(0);
        }
        if (Integer.parseInt(choice) == 3) {
            // Reads game instructions from data/json if player chooses to from the play game menu
            try {
                Files.readAllLines(Path.of("resources/data/game_instructions.txt")).forEach(System.out::println);
                String move = prompter.prompt("Enter to continue");
                Console.clear();
                startGame();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(Integer.parseInt(choice) == 4){
            player.setName(prompter.prompt("What is your name?\n-> "));
            loadGame(player.getName());
        }
        else if(Integer.parseInt(choice) == 1){
            player.setName(prompter.prompt("What is your name?\n-> "));
            System.out.println("Enjoy the game Air Marshal " + player.getName());
            timer = GameTimeKeeper.getInstance(player, scanner);
            MusicPlayer.init();
            turnLoop();
        }
    }

    private void loadGame(String name) {
        try{
            System.out.println("Loading game for " + name);
            JSONObject loadedFile = (JSONObject) new JSONParser().parse(new FileReader("resources/saves/"+name+".json")); //the entire data file
            JSONObject loadedData = (JSONObject) loadedFile.get(name); // this is the user's specific data
            //Load room
            String loadedRoom = (String) loadedData.get("activeRoom");
            activeRoom = loadedRoom;
            //set players inventory
            String itemString = (String) loadedData.get("inventory");
            String[] arr = itemString.split(", |\\[|\\]|,");
            List<Item> inventory = new ArrayList<>();
            for (String s : arr){
                for (Item i : Item.values()){
                    if(i.getName().equals(s)){
                        inventory.add(i);
                    }
                }
            }
            player.setInventory(inventory);
            //Get time and parse it
            String timeleft = (String) loadedData.get("timeleft");
            int hour = Integer.parseInt(timeleft.substring(0,1)) * 60;
            int min = Integer.parseInt(timeleft.substring(2,4));
            int sum = min + hour;
            //kick off game with saved data
            timer = GameTimeKeeper.getInstance(player, scanner, sum);
            MusicPlayer.init();
            turnLoop();
        } catch(Exception e){
            System.out.println("ERROR: Could not locate your save file");
            startGame();
        }
 }

    private void turnLoop() {
        while (player.isPlaying()) {

            Console.clear();// clears console after every turn a player takes

            try {
                statusBar();
                System.out.println("You are currently in the " + activeRoom);
                if (player.getInventory().contains(Item.AIRCRAFT_GUIDE)) {
                    printMenu("map");
                }
                else {
                    printMenu("turn");
                }
                String choice = prompter.prompt("What would you like to do?\n-> ");
                if (timer.isTimeLeft()) {
                    if(choice.toLowerCase().equals("options")){
                        displayOptions();
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

    private void displayOptions() throws IOException, ParseException {
        printMenu("options");
        String choice = prompter.prompt("Type an option\n-> ", "sound|Sound|quit|Quit|Save|save", "Please enter a valid option");
        switch(choice.toLowerCase()){
            case "quit":
                quitGame();
                break;
            case "sound":
                verbParser.parseVerb("sound", activeRoom, player);
                break;
            case "save":
                saveGame();
                break;
        }

    }

    private void quitGame() {
        player.setPlaying(false);
        MusicPlayer.controller(1); //this stops the music
        timer = null;
        execute();
    }

    /*
     * to save we need 1. Player name 2. Player inventory 3. Current activeRoom 4. Current time left in game.
     * Then write data to JSON file (resources/saves/{name}.json). Key for JSON will be players name(?)
     */
    private void saveGame() {
        JSONObject data = new JSONObject();
        data.put("inventory", player.getInventory().toString());
        data.put("activeRoom", activeRoom);
        data.put("timeleft", timer.getCurrentTime());
        JSONObject newSaveData = new JSONObject();
        newSaveData.put(player.getName(), data);
        try{
            FileWriter file = new FileWriter("resources/saves/"+player.getName()+".json");
            file.write(newSaveData.toJSONString());
            file.close();
            System.out.println("File Saved!");
            String move = prompter.prompt("Enter to continue");
        }catch (IOException e){
            System.out.println(e.getLocalizedMessage());
        }
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
        String response = prompter.prompt("Do you want to play again? yes or no?\n-> ", "yes|no|y|n", "Invalid Choice");
        if (response.equals("yes")|| response.equals("y")) {
            MusicPlayer.controller(1);
            startGame();
        } else if (response.equals("no")|| response.equals("n")) {
            System.out.println("Thank you for playing! Hope you will play again!");
            System.exit(0);
        }
    }


    public void printMenu(String menu){
        String[] optionsMenu = {"Save","Sound","Quit"};
        String[] turnMenu = {"Move", "Talk", "Items", "Inventory", "Options"};
        String[] mapMenu = {"Move", "Talk", "Items", "Inventory", "Map", "Options"};
        String[] playGameOptions = {"Enter 1: To play", "Enter 2: Leave the Game", "Enter 3: To Read the Instructions and then Play", "Enter 4: Load" };
        String[] selectedArr = {};
        System.out.println("\nYour options are:");
        System.out.println("\n-----------------\n");
        switch (menu){

            case "options":
                selectedArr = optionsMenu;
                break;
            case "turn":
                selectedArr = turnMenu;
                break;
            case "map":
               selectedArr = mapMenu;
                break;
            case "gameOptions":
                selectedArr = playGameOptions;
                break;
        }
        for (String word : selectedArr){
            System.out.println("   " + word);
        }
        System.out.println("\n-----------------\n");


    }
}

