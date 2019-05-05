package com.example.Booga;

public class EventType {
    public String getEventTypeName() {
        return eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    private String eventTypeName;

    public EventType() { } //needed for firebase
    public EventType(String eventTypeName){
        this.eventTypeName = eventTypeName;
    }

}
