package com.ewoudje.townskings.remote.faktory;

public enum FaktoryPriority implements com.github.sikandar.faktory.JobQueue {
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
