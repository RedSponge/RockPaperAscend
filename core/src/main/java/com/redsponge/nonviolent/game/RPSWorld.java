package com.redsponge.nonviolent.game;

import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.redsponge.redengine.physics.PActor;
import com.redsponge.redengine.physics.PhysicsWorld;

public class RPSWorld extends PhysicsWorld {

    private DelayedRemovalArray<Enemy> enemies;
    private DelayedRemovalArray<StoneBullet> bullets;

    private DelayedRemovalArray<Enemy> rocks;
    private DelayedRemovalArray<Enemy> papers;
    private DelayedRemovalArray<Enemy> scissors;

    public RPSWorld() {
        enemies = new DelayedRemovalArray<Enemy>();
        bullets = new DelayedRemovalArray<StoneBullet>();
        rocks = new DelayedRemovalArray<Enemy>();
        papers = new DelayedRemovalArray<Enemy>();
        scissors = new DelayedRemovalArray<Enemy>();
    }

    @Override
    public void addActor(PActor actor) {
        super.addActor(actor);
        if(actor instanceof Enemy) {
            enemies.add((Enemy) actor);
            switch (((Enemy) actor).getType()) {
                case ROCK:
                    rocks.add((Enemy) actor);
                    break;
                case PAPER:
                    papers.add((Enemy) actor);
                    break;
                case SCISSORS:
                    scissors.add((Enemy) actor);
                    break;
            }

        } else if(actor instanceof StoneBullet) {
            bullets.add((StoneBullet) actor);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        for (Enemy enemy : enemies) {
            if(enemy.isRemoved()) {
                enemies.removeValue(enemy, true);
                switch (enemy.getType()) {
                    case ROCK:
                        rocks.removeValue(enemy, true);
                        break;
                    case PAPER:
                        papers.removeValue(enemy, true);
                        break;
                    case SCISSORS:
                        scissors.removeValue(enemy, true);
                        break;
                }
            }
        }
    }

    public DelayedRemovalArray<Enemy> getEnemies() {
        return enemies;
    }

    public DelayedRemovalArray<StoneBullet> getBullets() {
        return bullets;
    }

    public DelayedRemovalArray<Enemy> getRocks() {
        return rocks;
    }

    public DelayedRemovalArray<Enemy> getPapers() {
        return papers;
    }

    public DelayedRemovalArray<Enemy> getScissors() {
        return scissors;
    }

    public DelayedRemovalArray<Enemy> getBeaten(MoveType type) {
        switch (type) {
            case ROCK:
                return scissors;
            case PAPER:
                return rocks;
            case SCISSORS:
                return papers;
        }
        return null;
    }
}
