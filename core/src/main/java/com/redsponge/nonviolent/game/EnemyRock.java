package com.redsponge.nonviolent.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.nonviolent.Constants;
import com.redsponge.nonviolent.Utils;
import com.redsponge.redengine.physics.PhysicsWorld;

public class EnemyRock extends Enemy {

    private Vector2 target;
    private Vector2 self;
    private Vector2 vel;

    private float timeUntilAttack;

    public EnemyRock(PhysicsWorld worldIn, Player player, int x, int y, float speed) {
        super(worldIn, player, x, y, speed);
        target = new Vector2();
        self = new Vector2();
        vel = new Vector2();
        size.set(30, 30);

        timeUntilAttack = 3;
    }

    @Override
    public void update(float delta) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                vel.set(0, 0);
                System.out.println("COLLISION OF ROCK!!!!");
            }
        };

        self.set(pos.x + size.x / 2, pos.y + size.y / 2);
        AIActions.wander(self, target, vel, Constants.GAME_BOUNDS);

        moveX(vel.x * speed * delta, runnable);
        moveY(vel.y * speed * delta, runnable);

        timeUntilAttack -= delta;
        if(timeUntilAttack <= 0) {
            shootStone();
            timeUntilAttack = 3;
        }

        tryKill(MoveType.SCISSORS);
    }

    private void shootStone() {
        StoneBullet bullet = new StoneBullet(worldIn, pos.copy().add(size.x / 2, size.y / 2), Utils.getDirectionVector(pos.x, pos.y, player.pos.x, player.pos.y), player);
        worldIn.addActor(bullet);
    }

    @Override
    public Rectangle getAttackRectangle() {
        return null;
    }
}