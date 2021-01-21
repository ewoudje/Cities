package com.ewoudje.townskings.api;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public interface UReference extends UObject {
    static <U extends UReference> U create(UUID uuid, Class<U> clazz) {
        try {
            return clazz.getConstructor(UUID.class).newInstance(uuid);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}
