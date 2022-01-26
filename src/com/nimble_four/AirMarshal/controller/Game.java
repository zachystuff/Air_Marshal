package com.nimble_four.AirMarshal.controller;

import com.apps.util.Prompter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Game {

    private Prompter prompter = new Prompter(new Scanner(System.in));

    public void execute() {
        startGame();
    }

    public void startGame() {
        try {
            Files.readAllLines(Path.of("data/game_intro")).forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Play");
        String test = prompter.prompt("Please enter yes if you see this? ", "yes", "Invalid choice: enter yes");

        if (test.equals("yes")) {
            System.out.println("Successful");
        }
    }
}
