package com.example.appenglish_nouns.model;

public class Noun {
    public int id;
    public String inEnglish;
    public String inRussian;
    public int inGroup;

    public Noun() {
    }

    public Noun(int id, String inEnglish, String inRussian) {
        this.id = id;
        this.inEnglish = inEnglish.toLowerCase();
        this.inRussian = inRussian.toLowerCase();
        this.inGroup = 0;
    }

    public Noun(int id, String inEnglish, String inRussian, int inGroup) {
        this.id = id;
        this.inEnglish = inEnglish.toLowerCase();
        this.inRussian = inRussian.toLowerCase();
        this.inGroup = inGroup;
    }

    public Noun(String inEnglish, String inRussian, int inGroup) {
        this.inEnglish = inEnglish.toLowerCase();
        this.inRussian = inRussian.toLowerCase();
        this.inGroup = inGroup;
    }

    public Noun(String inEnglish, String inRussian) {
        this.inEnglish = inEnglish.toLowerCase();
        this.inRussian = inRussian.toLowerCase();
    }

    @Override
    public String toString() {
        return  id + " " + inEnglish + " " + inRussian + " " + inGroup;
    }
}
