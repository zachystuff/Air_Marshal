package com.nimble_four.AirMarshal.controller;
import com.apps.util.Console;
import com.apps.util.Prompter;
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
    private String activeRoom = "commercial class";
    private VerbParser verbParser = new VerbParser();
    private String currentTime = "3:20";
    final Timer timer = new Timer();

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
                    if (i< 0) {
                        Console.clear();
                        timer.cancel();
                        System.out.println("\nGAME OVER: THE PASSENGER HAS BEEN MURDERED!");
                    }
                }
            }, 0, 1000);

            turnLoop();
        }
    }

    private void turnLoop() {
        while (true) {
            Console.clear();
            try {
                statusBar();
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

