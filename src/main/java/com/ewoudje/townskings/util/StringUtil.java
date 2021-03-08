package com.ewoudje.townskings.util;

import org.bukkit.NamespacedKey;

public class StringUtil {

    public static NamespacedKey getKey(String key) {
        String[] id = key.split(":");
        return new NamespacedKey(id[0], id[1]);
    }

}
