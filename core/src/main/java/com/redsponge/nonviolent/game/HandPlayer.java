package com.redsponge.nonviolent.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.redsponge.nonviolent.game.ai.IAttackable;
import com.redsponge.nonviolent.input.GameInput;
import com.redsponge.redengine.physics.IUpdated;
import com.redsponge.redengine.physics.PActor;
import com.redsponge.redengine.physics.PhysicsWorld;
import com.redsponge.redengine.utils.Logger;
import com.redsponge.redengine.utils.MathUtilities;

public class HandPlayer extends PActor implements IUpdated, IAttackable {

    private GameInput input;
    private Rectangle representation;
    public boolean dead;

    public HandPlayer(PhysicsWorld worldIn) {
        super(worldIn);
        pos.set(100, 100);
        size.set(30, 30);

        input = new GameInput();
        representation = new Rectangle(pos.x, pos.y, size.x, size.y);
    }

    @Override
    public void update(float delta) {
        float speed = 300;
        moveX(input.getHorizontal() * speed * delta, null);
        moveY(input.getVertical() * speed * delta, null);
    }

    @Override
    public boolean attack(Rectangle attackRange, float damage) {
        updateRepresentation();

        if(attackRange.overlaps(representation)) {
            Logger.log(this, "Attacked! Damage:", damage);
            dead = true;
            return true;
        }

        return false;
    }

    private void updateRepresentation() {
        representation.set(pos.x, pos.y, size.x, size.y);
    }
}
