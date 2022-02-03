package com.nimble_four.AirMarshal.controller;

import com.nimble_four.AirMarshal.Player;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.Scanner;

public class GameTimeKeeperTest extends TestCase {
    GameTimeKeeper keeper;


    // ---- ISTIMELEFT ----
    @Test
    public void testIsTimeLeft_shouldReturnFalse_whenTimeKeeperTimeLeftIsZero() throws InterruptedException {
        keeper = GameTimeKeeper.getInstance(new Player(), new Scanner(System.in), 0);
        //sleep the thread as takes time to run through the gametimekeeperctor
        // and set current time to 0 after instantiation
        Thread.sleep(1000);
        System.out.println(keeper.getCurrentTime());
        assertEquals(false, keeper.isTimeLeft());
    }

    // ---- GAMEOVER ----
    @Test
    public void testGameOver_shouldChangePlayerIsPlayingToFalse(){
        Player player = new Player();
        keeper.gameOver(player, new Scanner(System.in));
        assertEquals(false, player.isPlaying());
    }
}