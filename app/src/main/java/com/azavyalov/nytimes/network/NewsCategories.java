package com.azavyalov.nytimes.network;

public enum NewsCategories {
    HOME("home"),
    SCIENCE("science"),
    SPORTS("sports"),
    TRAVEL("travel");

    private final String name;

    NewsCategories(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
