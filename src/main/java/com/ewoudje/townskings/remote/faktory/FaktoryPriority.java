package com.ewoudje.townskings.remote.faktory;

import com.ewoudje.townskings.api.Named;

public enum FaktoryPriority implements com.github.sikandar.faktory.JobQueue, Named {
    MCHIGH,
    MC,
    MID,
    MCLOW,
    DEFAULT,
    API,
    LOW
    ;

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
