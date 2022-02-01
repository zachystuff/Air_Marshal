package com.nimble_four.AirMarshal.controller;
import com.apps.util.Console;
import com.apps.util.Prompter;
import com.nimble_four.AirMarshal.Item;
import com.nimble_four.AirMarshal.Player;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Scanner;
import org.json.simple.parser.*;
import java.util.Timer;
import java.util.TimerTask;

public class Game {
    private Player player = new Player();
    private Prompter prompter = new Prompter(new Scanner(System.in));
    private String activeRoom = "commercial class"; // By default game starts commercial class
    private VerbParser verbParser = new VerbParser();   // VerbParser is responsible for handling player actions
    private String currentTime = "3:20";
    final Timer timer = new Timer();

    public void execute() {
        startGame();
    }

    private void startGame() {
        // Reads game intro and instructions from data/json files at the beginning of the game
        try {
            Files.readAllLines(Path.of("data/game_intro.txt")).forEach(System.out::println);
            Files.readAllLines(Path.of("data/game_instructions.txt")).forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setName(prompter.prompt("What is your name? "));
        String test = prompter.prompt("Please enter yes if you want to play? ", "yes|y", "Invalid choice: enter yes to play");
        // player is prompted, typing "yes" or "y" allows them to enter the game
        if (test.equals("yes") || test.equals("y")) {
            System.out.println("Enjoy the game Air Marshal " + player.getName());

            // NOTE: The game is timed and once the timer runs out, it game over.
            // Timer starts once the app gets run
            timer.scheduleAtFixedRate(new TimerTask() {
                int i = Integer.parseInt("200");
                int displayMinutes;
                int displaySeconds;
                DecimalFormat formatter = new DecimalFormat("00");
                String secondsFormatted;
                public void run() {
                    displayMinutes = (i / 60) % 60;
                    displaySeconds = i % 60;
                    secondsFormatted = formatter.format(displaySeconds);
                    i--;
                    currentTime = displayMinutes + ":" + secondsFormatted + " left";
//                System.out.println(displayMinutes + ":" + displaySeconds);
                    if (i< 0) { // if the time runs out
                        Console.clear();    // console is cleared
                        timer.cancel(); // timer is stopped
                        System.out.println("\nGAME OVER: THE PASSENGER HAS BEEN MURDERED!");
                    }
                }
            }, 0, 1000);
            // Once timer has been loaded, the player is able to start playing the game
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
                // handle options from the menu, such as moving, talking, taking items and inventory
                activeRoom = verbParser.parseVerb(choice, activeRoom, player);

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

    // displays the actions a player can choose to do
    public void menu() {
        System.out.println("-- Menu Options --");
        System.out.println(
                "Your options are: \n" +
                        "  Move\n" +
                        "  Talk\n" +
                        "  Items \n" +
                        "  Inventory\n "
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
                        + currentTime + " | " +
                        "\n***************************************\n"
        );
    }
}

