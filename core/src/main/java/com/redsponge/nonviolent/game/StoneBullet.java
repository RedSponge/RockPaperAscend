package com.redsponge.nonviolent.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.redengine.physics.IUpdated;
import com.redsponge.redengine.physics.PActor;
import com.redsponge.redengine.physics.PhysicsWorld;
import com.redsponge.redengine.utils.IntVector2;
import com.redsponge.redengine.utils.Logger;
import com.redsponge.redengine.utils.MathUtilities;

public class StoneBullet extends PActor implements IUpdated {

    private Vector2 vel;
    private float timeLeft;
    private float speed;
    private Player player;

    public StoneBullet(PhysicsWorld worldIn, IntVector2 pos, Vector2 vel, Player player) {
        super(worldIn);
        this.vel = vel;
        this.player = player;
        this.pos.set(pos);
        size.set(20, 20);

        speed = 500;

        timeLeft = 1;
    }

    @Override
    public void update(float delta) {
        Runnable remove = new Runnable() {
            @Override
            public void run() {
                remove();
            }
        };

        moveX(vel.x * speed * delta, remove);
        moveY(vel.y * speed * delta, remove);

        timeLeft -= delta;
        if(timeLeft <= 0) {
            remove();
        }

        checkIfKilled();
    }

    private void checkIfKilled() {
        for (Enemy enemy : ((RPSWorld) worldIn).getScissors()) {
            if(MathUtilities.rectanglesIntersect(pos, size, enemy.pos, enemy.size)) {
                Logger.log(this, "Removed Scissors!");
                enemy.remove();
                this.remove();
                return;
            }
        }

        if(MathUtilities.rectanglesIntersect(pos, size, player.pos, player.size)) {
            Logger.log(this, "Removed Player!");
            player.attack(new Rectangle(pos.x, pos.y, size.x, size.y), 100);
            this.remove();
        }
    }
}
