package com.example.bringo.database;

import com.orm.SugarRecord;

/**
 * Created by huojing on 4/19/17.
 */

public class ScenarioAlarmDB extends SugarRecord{
    private int id;

    private int hour;
    private int minute;
    private boolean sunday;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;

    private boolean repeat;

    public ScenarioAlarmDB() {}

    public ScenarioAlarmDB(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getHour() {
       return hour;
    }
    public int getMinute() { return minute; }
    public String getTime() {
        return String.format("%02d", hour) + ":" + String.format("%02d", minute);
    }

    public void checkDayOfWeek(int index) {
        switch (index) {
            case 0:
                sunday = true;
                break;
            case 1:
                monday = true;
                break;
            case 2:
                tuesday = true;
                break;
            case 3:
                wednesday = true;
                break;
            case 4:
                thursday = true;
                break;
            case 5:
                friday = true;
                break;
            case 6:
                saturday = true;
                break;
        }
    }

    public void uncheckDayOfWeek(int index) {
        switch (index) {
            case 0:
                sunday = false;
                break;
            case 1:
                monday = false;
                break;
            case 2:
                tuesday = false;
                break;
            case 3:
                wednesday = false;
                break;
            case 4:
                thursday = false;
                break;
            case 5:
                friday = false;
                break;
            case 6:
                saturday = false;
                break;
        }
    }

    public boolean[] getDayOfWeek() {
        boolean[] dayOfWeek = new boolean[7];
        dayOfWeek[0] = sunday;
        dayOfWeek[1] = monday;
        dayOfWeek[2] = tuesday;
        dayOfWeek[3] = wednesday;
        dayOfWeek[4] = thursday;
        dayOfWeek[5] = friday;
        dayOfWeek[6] = saturday;
        return dayOfWeek;
    }

    public void checkRepeat() {repeat = true;}

    public void uncheckRepeat() {repeat = false;}

    public boolean getRepeat() {
        return repeat;
    }

}
