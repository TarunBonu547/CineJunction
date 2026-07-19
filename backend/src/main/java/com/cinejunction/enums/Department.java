package com.cinejunction.enums;

import lombok.Getter;

@Getter
public enum Department {

    ACTOR("Actor"),
    DIRECTOR("Director"),
    WRITER("Writer"),
    PRODUCER("Producer"),
    MUSIC("Music Director"),
    EDITOR("Editor"),
    CINEMATOGRAPHY("Cinematographer");

    private final String displayName;

    Department(String displayName) {
        this.displayName = displayName;
    }
}
