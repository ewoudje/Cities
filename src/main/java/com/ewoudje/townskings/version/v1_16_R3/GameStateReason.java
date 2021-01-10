package com.ewoudje.townskings.version.v1_16_R3;

public enum GameStateReason {
    NO_RESPAWN(0),
    END_RAIN(1),
    START_RAIN(2),
    GAMEMODE(3),
    WIN_GAME(4),
    DEMO(5),
    ARROW_HIT(6),
    RAIN_LEVEL_CHANGE(7),
    THUNDER_LEVEL_CHANGE(8),
    PUFFER_FISH(9),
    ELDER_GUARDIAN(10),
    ENABLE_RESPAWN_SCRN(11);

    private int i;

    GameStateReason(int i) {
        this.i = i;
    }

    public int getValue() {
        return i;
    }
}
