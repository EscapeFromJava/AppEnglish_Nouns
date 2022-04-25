package com.example.appenglish_nouns.model;

public class Group {
    public int id;
    public String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Group(String name) {
        this.name = name;
    }

    public Group(int id, String name) {
        this.id = id;
        this.name = name.toLowerCase();
    }

    public boolean isNull(){
        return name.length() < 1;
    }
    @Override
    public String toString() { return id + " " + name; }

}
