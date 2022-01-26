package com.nimble_four.AirMarshal;

class enum Item {
    YELLOW_HANDKERCHIEF("yellow handkerchief"),
    OVERHEAD_LUGGAGE("overhead luggage"),
    STETHOSCOPE("stethoscope"),
    CARTON_OF_CIGARETTES("carton of cigarettes"),
    SKI_POLE("ski pole"),
    CHECK_BOOK("check book"),
    FLIGHT_MANUAL("flight manual"),
    AIRCRAFT_GUIDE("aircraft guide"),
    POSTER("poster"),
    DRINKS("drinks"),
    FOOD("food"),
    SPATULA("spatula"),
    LAWYERS_BAG("lawyers bag"),
    DOCTORS_BAG("doctors bag"),
    ANONYMOUS_BAG("anonymous bag"),
    BOARDING_PASS("boarding pass"),
    POISON('poison');



    // ---- FIELDS ----
    private string name;

    // ---- CONSTRUCTOR ----
    Item(String name){
        this.name = name;
    }

    // ---- HELPERS ----


    public string getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}

