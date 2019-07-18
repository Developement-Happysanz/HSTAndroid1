package com.skilex.customer.bean.support;

public class StoreTimeSlot {

    private String timeId;
    private String timeName;

    public StoreTimeSlot(String timeId, String timeName) {
        this.timeId = timeId;
        this.timeName = timeName;
    }

    public String getTimeId() {
        return timeId;
    }

    public void setTimeId(String stateId) {
        this.timeId = timeId;
    }

    public String getTimeName() {
        return timeName;
    }

    public void setTimeName(String timeName) {
        this.timeName = timeName;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return timeName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StoreTimeSlot) {
            StoreTimeSlot c = (StoreTimeSlot) obj;
            if (c.getTimeName().equals(timeName) && c.getTimeId() == timeId) return true;
        }

        return false;
    }
}