package com.redsponge.nonviolent.game;

public enum MoveType {

    ROCK(1) {
        @Override
        public Enemy create(RPSWorld world, Player player, int x, int y) {
            return new EnemyRock(world, player, x, y, 100);
        }
    },
    PAPER(2) {
        @Override
        public Enemy create(RPSWorld world, Player player, int x, int y) {
            return new EnemyPaper(world, player, x, y, 50, 150);
        }
    },
    SCISSORS(0) {
        @Override
        public Enemy create(RPSWorld world, Player player, int x, int y) {
            return new EnemyScissors(world, player, x, y, 300);
        }
    }


    ;
    private static MoveType[] ALL = {ROCK, PAPER, SCISSORS};

    private int beats;

    MoveType(int beats) {
        this.beats = beats;
    }

    public MoveType getBeats() {
        return ALL[beats];
    }

    public abstract Enemy create(RPSWorld world, Player player, int x, int y);
}
