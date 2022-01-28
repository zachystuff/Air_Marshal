package com.nimble_four.AirMarshal;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerTest {

    Player player = null;

    @Before
    public void setUp() throws Exception {
        player = new Player();
    }

    @Test
    public void addToInventoryPositive() {
        player.addToInventory(Item.AIRCRAFT_GUIDE);
        player.addToInventory(Item.BOARDING_PASS);
        assertEquals(2,player.getInventory().size());
    }

    @Test
    public void addToInventoryNegative() {
        player.addToInventory(null);
        assertNotEquals(1,player.getInventory().size());
    }

    @Test
    public void dropItemPostive() {
        player.addToInventory(Item.AIRCRAFT_GUIDE);
        player.addToInventory(Item.BOARDING_PASS);
        player.dropItem(Item.BOARDING_PASS);
        assertEquals(1,player.getInventory().size());
    }

    @Test
    public void dropItemNegative(){
        player.addToInventory(Item.AIRCRAFT_GUIDE);
        player.addToInventory(Item.BOARDING_PASS);
        player.dropItem(null);
        assertEquals(2,player.getInventory().size());
    }
}