package com.example.appenglish_nouns.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Session {
    private String name;
    private Date start;
    private Date finish;

    public Session(String name) {
        this.name = name;
        setStart();
    }

    private void setStart() {
        start = new Date();
    }

    private void setFinish() {
        finish = new Date();
    }

    public String getSessionTime() {
        setFinish();
        Date period = new Date(finish.getTime() - start.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return name + "\n" + simpleDateFormat.format(period);
    }

    @Override
    public String toString() {
        return "Session{" +
                "name='" + name + '\'' +
                ", start=" + start +
                ", finish=" + finish +
                '}';
    }
}
