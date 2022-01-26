package com.nimble_four.AirMarshal;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RoomTest {
    Room roomTest = null;

    @Before
    public void setUp() throws Exception {
        roomTest = new Room("Test Player", new Player(), new Items());
    }

    @Test
    public void addCharactersPositive() {
        roomTest.addCharacters(new Character());
        roomTest.addCharacters(new Character());
        roomTest.addCharacters(new Character());
        assertEquals(3, roomTest.getCharacters().size());
    }

    @Test
    public void addCharactersNegative(){
        roomTest.addCharacters(null);
        assertEquals(0,roomTest.getCharacters().size());
    }

}