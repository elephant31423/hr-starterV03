package com.example.hrstarter.enums;

public enum ShiftType {
    MORNING("早班", "#4CAF50"),
    MIDDLE("中班", "#2196F3"),
    NIGHT("夜班", "#9C27B0"),
    OFF("休假", "#BDBDBD");

    private final String label;
    private final String color;

    ShiftType(String s, String hashtag) {
        this.label = s;
        this.color = hashtag;
    }
    public String getLabel() {
        return label;
    }
    public String getColor() {
        return color;
    }
}
