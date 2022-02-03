package com.nimble_four.AirMarshal.controller;

import com.nimble_four.AirMarshal.Player;
import junit.framework.TestCase;
import org.json.simple.JSONObject;
import org.junit.Test;

public class VerbParserTest extends TestCase {
    private VerbParser parser;
    private Player player;
    @Override
    public void setUp() throws Exception {
        parser = new VerbParser();
        player = new Player();
    }

    // ---- VERBPARSER ----
    @Test
    public void testParseVerb_invalidVerbs_shouldNotChangeRoom() throws Exception {
       String test = parser.parseVerb("invalid", "cockpit", player);
       assertEquals(test, "cockpit");
       String test2 = parser.parseVerb("invalid", "bathroom", player);
       assertEquals(test2, "bathroom");
    }

    //---- FINDCHOICESYNONYMS ----
    //Made public for testing
    @Test
    public void testFindChoiceSynonyms_shouldReturnSynonym_whenGivenWordInArrayPool() {
        String move = "move";
        String talk = "talk";
        String items = "items";
        String inventory = "inventory";
        String map = "map";

        //move synonyms
        assertEquals(parser.findChoiceSynonyms("move"), move);
        assertEquals(parser.findChoiceSynonyms("walk"), move);
        assertEquals(parser.findChoiceSynonyms("run"), move);
        assertEquals(parser.findChoiceSynonyms("change room"), move);

        //talking synonyms
        assertEquals(parser.findChoiceSynonyms("talk"), talk);
        assertEquals(parser.findChoiceSynonyms("speak"), talk);
        assertEquals(parser.findChoiceSynonyms("chat"), talk);
        assertEquals(parser.findChoiceSynonyms("interact"), talk);

        //items synonyms
        assertEquals(parser.findChoiceSynonyms("items"), items);
        assertEquals(parser.findChoiceSynonyms("get"), items);
        assertEquals(parser.findChoiceSynonyms("item"), items);
        assertEquals(parser.findChoiceSynonyms("take"), items);

        //inventory synonyms
        assertEquals(parser.findChoiceSynonyms("inventory"), inventory);
        assertEquals(parser.findChoiceSynonyms("check inventory"), inventory);
        assertEquals(parser.findChoiceSynonyms("view inventory"), inventory);

        //map synonyms
        assertEquals(parser.findChoiceSynonyms("map"), map);
        assertEquals(parser.findChoiceSynonyms("view map"), map);
        assertEquals(parser.findChoiceSynonyms("check map"), map);
    }

    @Test
    public void testFindChoiceSynonyms_shouldReturnNONE_whenGivenInvalidChoice(){
        String none = "NONE";

        assertEquals(parser.findChoiceSynonyms("invalid"), none);
        assertEquals(parser.findChoiceSynonyms("mAp"), none);
        assertEquals(parser.findChoiceSynonyms("iNVEntory"), none);
    }


}