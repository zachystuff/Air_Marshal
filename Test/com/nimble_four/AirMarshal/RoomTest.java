package com.nimble_four.AirMarshal;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RoomTest {
    Room roomTest = null;

    @Before
    public void setUp() throws Exception {
        roomTest = new Room("Test Player");
    }

    @Test
    public void addCharactersPositive() {
        roomTest.addCharacters(new Character("pilot", "May day"));
        roomTest.addCharacters(new Character("doctor","hello"));
        roomTest.addCharacters(new Character("teacher", "hello too"));
        assertEquals(3, roomTest.getCharacters().size());
    }

    @Test
    public void addCharactersNegative(){
        roomTest.addCharacters(null);
        assertEquals(0,roomTest.getCharacters().size());
    }

}