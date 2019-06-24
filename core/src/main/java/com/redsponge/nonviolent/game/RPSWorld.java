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
            Enemy enemy = (Enemy) actor;

            if(enemy instanceof EnemyRock) {
                rocks.add(enemy);
            } else if(enemy instanceof EnemyPaper) {
                papers.add(enemy);
            } else if(enemy instanceof EnemyScissors) {
                scissors.add(enemy);
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
                if(enemy instanceof EnemyRock) {
                    rocks.removeValue(enemy, true);
                } else if(enemy instanceof EnemyPaper) {
                    papers.removeValue(enemy, true);
                } else if(enemy instanceof EnemyScissors) {
                    scissors.removeValue(enemy, true);
                }
            }
        }

        for (StoneBullet bullet : bullets) {
            if(bullet.isRemoved()) {
                bullets.removeValue(bullet, true);
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

    public DelayedRemovalArray<Enemy> getByType(MoveType type) {
        switch (type) {
            case ROCK:
                return rocks;
            case PAPER:
                return papers;
            case SCISSORS:
                return scissors;
        }
        return null;
    }
}
