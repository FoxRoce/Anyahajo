package com.project.anyahajo.model;

public enum CarrierType {

    Wrap("Kendő"),
    Buckle("Csatos"),
    OtherShaped("Egyéb formázott");

    public final String hunName;

    CarrierType(String hunName) {
        this.hunName = hunName;
    }

    public CarrierType getByHunName(String hunName) {
        for (CarrierType c : CarrierType.values()) {
            if (c.name().equals(hunName)){
                return c;
            }
        }
        return null;
    }
}
