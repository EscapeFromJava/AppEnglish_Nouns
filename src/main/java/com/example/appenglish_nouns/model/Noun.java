package com.example.appenglish_nouns.model;

public class Noun {
    public int id;
    public String inEnglish;
    public String inRussian;
    public int inGroup;
    public String inNameGroup;

    public int getId() {
        return id;
    }

    public String getInEnglish() {
        return inEnglish;
    }

    public String getInRussian() {
        return inRussian;
    }

    public int getInGroup() {
        return inGroup;
    }

    public String getInNameGroup() {
        return inNameGroup;
    }

    public Noun() {
    }

    public Noun(String inEnglish, String inRussian, int inGroup) {
        this.inEnglish = inEnglish.toLowerCase();
        this.inRussian = inRussian.toLowerCase();
        this.inGroup = inGroup;
    }

    public Noun(int id, String inEnglish, String inRussian, int inGroup, String inNameGroup) {
        this.id = id;
        this.inEnglish = inEnglish.toLowerCase();
        this.inRussian = inRussian.toLowerCase();
        this.inGroup = inGroup;
        this.inNameGroup = inNameGroup.toLowerCase();
    }

    @Override
    public String toString() {
        return id + " " + inEnglish + " " + inRussian + " " + inGroup;
    }
}
