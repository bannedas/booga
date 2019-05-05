package com.example.Booga;

public class EventTags {
    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    String tagName;

    public EventTags() { } //needed for firebase
    public EventTags(String tagName){
        this.tagName = tagName;
    }

}