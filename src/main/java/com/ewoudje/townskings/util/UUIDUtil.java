package com.ewoudje.townskings.util;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public class UUIDUtil {

    @Nonnull
    public static Optional<UUID> fromString(String s) {
        if (s == null) return Optional.empty();

        return Optional.of(UUID.fromString(s));
    }

}
