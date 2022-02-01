package com.nimble_four.AirMarshal.controller;
import com.apps.util.Console;

import java.sql.Time;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;
public class GameTimeKeeper extends Timer {
    private Timer timer = new Timer();
    private String currentTime = "3:20";

    public GameTimeKeeper(){
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
                    Console.clear();
                    timer.cancel();
                    System.out.println("\nGAME OVER: THE PASSENGER HAS BEEN MURDERED!");
                }
            }
        }, 0, 1000);
    }

    public String getCurrentTime() {
        return currentTime;
    }
}