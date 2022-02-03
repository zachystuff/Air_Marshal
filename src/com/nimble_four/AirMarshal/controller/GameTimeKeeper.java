package com.nimble_four.AirMarshal.controller;
import com.apps.util.Console;
import com.nimble_four.AirMarshal.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
public class GameTimeKeeper extends Timer {
    private Timer timer = new Timer();
    private String currentTime = "3:20";
    private static GameTimeKeeper timeKeeper = null;
    private boolean timeLeft = true;

    // ---- CONSTRUCTORS ----
    private GameTimeKeeper(Player player, Scanner scanner){
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
                if (i< 0) {
                    setTimeLeft(false);
                    timer.cancel();
                }
            }
        }, 0, 1000);
    }

    private GameTimeKeeper(Player player, Scanner scanner, int timeRemaining){
        timer.scheduleAtFixedRate(new TimerTask() {
            int i = timeRemaining;
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
                if (i< 0) {
                    setTimeLeft(false);
                    timer.cancel();
                }
            }
        }, 0, 1000);
    }

    // public access that calls private ctor if necessary
    public static GameTimeKeeper getInstance(Player player, Scanner scanner){
        if(timeKeeper == null){
            timeKeeper = new GameTimeKeeper(player, scanner);
        }

        return timeKeeper;
    }

    public static GameTimeKeeper getInstance(Player player, Scanner scanner, int timeRemaining){
        if(timeKeeper == null){
            timeKeeper = new GameTimeKeeper(player, scanner, timeRemaining);
        }
        return timeKeeper;
    }

    public boolean isTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(boolean timeLeft) {
        this.timeLeft = timeLeft;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void gameOver(Player player, Scanner scanner) {
        Console.clear();
        JSONObject gameOverDialogue = null;
        try {
            gameOverDialogue = (JSONObject) new JSONParser().parse(new FileReader("resources/endgame.json"));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        System.out.println("\nAir Marshal " + player.getName() + gameOverDialogue.get("game over"));
        player.setPlaying(false);
        timeKeeper = null;
        new Game().playAgain();
    }
}