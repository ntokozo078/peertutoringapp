package com.niquewrld.tutorspace.models;

public class Category {
    private String id;
    private String name;
    private String iconText;        // For text icons (like "zh")
    private Integer iconResourceId; // For image icons (like R.drawable.ic_help)
    private boolean useTextIcon;    // To determine which type of icon to display

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(String id, String name, Integer iconResourceId) {
        this.id = id;
        this.name = name;
        this.iconResourceId = iconResourceId;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public Integer getIconResourceId() { return iconResourceId; }
}
