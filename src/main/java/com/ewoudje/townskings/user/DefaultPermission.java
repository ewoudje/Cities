package com.ewoudje.townskings.user;

import com.ewoudje.townskings.api.town.Permission;

import java.util.UUID;

public enum DefaultPermission implements Permission {
    BUILD,
    MANAGE
    ;

    private UUID uuid;

    DefaultPermission() {
        uuid = UUID.nameUUIDFromBytes(("permissions:::" + name()).getBytes());
    }

    @Override
    public String getName() {
        return name().toLowerCase();
    }

    @Override
    public UUID getUID() {
        return uuid;
    }
}
