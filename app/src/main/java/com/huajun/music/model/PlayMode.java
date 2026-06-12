package com.huajun.music.model;

public enum PlayMode {
    SEQUENCE(0),
    RANDOM(1),
    SINGLE(2);

    private final int value;

    PlayMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PlayMode fromValue(int value) {
        for (PlayMode mode : values()) {
            if (mode.value == value) return mode;
        }
        return SEQUENCE;
    }

    public PlayMode next() {
        return fromValue((value + 1) % 3);
    }
}
