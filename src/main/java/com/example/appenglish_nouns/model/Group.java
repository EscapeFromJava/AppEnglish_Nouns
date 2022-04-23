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

    public Group(int id, String name) {
        this.id = id;
        this.name = name.toLowerCase();
    }

    @Override
    public String toString() { return id + " " + name; }

}
