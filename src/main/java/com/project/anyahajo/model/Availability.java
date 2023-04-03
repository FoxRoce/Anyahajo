package com.project.anyahajo.model;

public enum Availability {
    Available("Szabad"),
    Reserved("Foglalt"),
    NotAvailable("Nem elérhető");

    public final String hunName;

    Availability(String hunName) {
        this.hunName = hunName;
    }

    public Availability getByHunName(String hunName) {
        for (Availability c : Availability.values()) {
            if (c.name().equals(hunName)){
                return c;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return String.valueOf(this).isEmpty();
    }
}
