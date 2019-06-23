package com.redsponge.nonviolent.game;

import com.badlogic.gdx.math.Vector2;
import com.redsponge.redengine.physics.IUpdated;
import com.redsponge.redengine.physics.PActor;
import com.redsponge.redengine.physics.PhysicsWorld;
import com.redsponge.redengine.utils.IntVector2;

public class StoneBullet extends PActor implements IUpdated {

    private Vector2 vel;
    private float timeLeft;
    private float speed;

    public StoneBullet(PhysicsWorld worldIn, IntVector2 pos, Vector2 vel) {
        super(worldIn);
        this.vel = vel;
        this.pos.set(pos);
        size.set(20, 20);

        speed = 500;

        timeLeft = 1;
    }

    @Override
    public void update(float delta) {
        moveX(vel.x * speed * delta, null);
        moveY(vel.y * speed * delta, null);

        timeLeft -= delta;
        if(timeLeft <= 0) {
            remove();
        }
    }
}
