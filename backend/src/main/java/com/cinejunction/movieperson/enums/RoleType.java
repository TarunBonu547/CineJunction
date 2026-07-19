package com.cinejunction.movieperson.enums;

import lombok.Getter;

@Getter
public enum RoleType {

    ACTOR("Actor"),
    DIRECTOR("Director"),
    WRITER("Writer"),
    PRODUCER("Producer"),
    MUSIC("Music"),
    EDITOR("Editor"),
    CINEMATOGRAPHER("Cinematographer");

    private final String displayName;

    RoleType(String displayName) {
        this.displayName = displayName;
    }
}
