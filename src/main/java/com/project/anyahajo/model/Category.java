package com.project.anyahajo.model;

import java.util.Arrays;

public enum Category {
    hordozó1("hunName"),
    hordozó2("hunName"),
    hordozó3("hunName"),
    hordozó4("hunName"),
    babaÁpolás("hunName"),
    book("könyv");

    public final String hunName;

    Category(String hunName) {
        this.hunName = hunName;
    }

    public Category getByHunName(String hunName) {
        for (Category c : Category.values()) {
            if (c.name().equals(hunName)){
                return c;
            }
        }
        return null;
    }
}
