package com.redsponge.nonviolent.game;

public enum MoveType {

    ROCK(1),
    PAPER(2),
    SCISSORS(0)


    ;
    private static MoveType[] ALL = {ROCK, PAPER, SCISSORS};

    private int beats;

    MoveType(int beats) {
        this.beats = beats;
    }

    public MoveType getBeats() {
        return ALL[beats];
    }
}
