package com.nimble_four.AirMarshal;

class Character {
    public String name;
    public String dialog;

    public Character(String name, string dialog) {
        this.name = name;
        this.dialog = dialog;
    }

    public String getItem() {
        return "Collected Item!"
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @java.lang.Override
    public java.lang.String toString() {
        return "Character{" +
                "name='" + name + '\'' +
                ", dialog='" + dialog + '\'' +
                '}';
    }
}
