package com.ewoudje.townskings;

import com.ewoudje.townskings.api.Named;
import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.remote.RemoteTown;

import java.util.*;
import java.util.function.Function;

public enum NamedPrefix {
    TOWN("t", RemoteTown::new),
    PLAYER("p", OfflinePlayer::getFromUUID);

    private static final Map<String, NamedPrefix> prefixMap = new HashMap<>();

    static {
        Arrays.stream(NamedPrefix.values()).forEach((v) -> prefixMap.put(v.prefix, v));
    }

    private final String prefix;
    private final Function<UUID, Named> getNamed;

    NamedPrefix(String prefix, Function<UUID, Named> getNamed) {
        this.prefix = prefix.toLowerCase(Locale.ROOT);
        this.getNamed = getNamed;
    }

    public static NamedPrefix getByPrefix(String prefix) {
        return prefixMap.get(prefix.toLowerCase(Locale.ROOT));
    }

    public String getName(UUID uuid) {
        return getNamed.apply(uuid).getName();
    }
}
