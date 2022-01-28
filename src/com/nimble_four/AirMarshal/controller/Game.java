package com.nimble_four.AirMarshal.controller;
import com.apps.util.Prompter;
import com.nimble_four.AirMarshal.Player;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import org.json.simple.parser.*;

public class Game {
    private Player player = new Player();
    private Prompter prompter = new Prompter(new Scanner(System.in));
    private String activeRoom = "commercial class";
    private VerbParser verbParser = new VerbParser();

    public void execute() {
        startGame();
    }

    private void startGame() {
        try {
            Files.readAllLines(Path.of("data/game_intro.txt")).forEach(System.out::println);
            Files.readAllLines(Path.of("data/game_instructions.txt")).forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setName(prompter.prompt("What is your name? "));
        String test = prompter.prompt("Please enter yes if you want to play? ", "yes|y", "Invalid choice: enter yes");

        if (test.equals("yes") || test.equals("y")) {
            System.out.println("Enjoy the game Air Marshal " + player.getName());
            turnLoop();
        }
    }

    private void turnLoop() {
        while (true) {
            try {
                System.out.println("You are currently in the " + activeRoom);
                menu();
                String choice = prompter.prompt("What would you like to do? ");
                activeRoom = verbParser.parseVerb(choice, activeRoom, player); //this handles moving, talking, and taking items
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
}

