package com.nimble_four.AirMarshal.controller;
import com.apps.util.Console;
import com.nimble_four.AirMarshal.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
public class GameTimeKeeper extends Timer {
    private Timer timer = new Timer();
    private String currentTime = "3:20";

    public boolean isTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(boolean timeLeft) {
        this.timeLeft = timeLeft;
    }

    private boolean timeLeft = true;

    public GameTimeKeeper(Player player, Scanner scanner){
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
        new Game().playAgain();
    }
}