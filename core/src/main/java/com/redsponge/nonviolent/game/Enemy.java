package com.redsponge.nonviolent.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.nonviolent.game.ai.IAttackable;
import com.redsponge.nonviolent.game.ai.MoveAI;
import com.redsponge.nonviolent.game.ai.PaperAI;
import com.redsponge.nonviolent.game.ai.RockAI;
import com.redsponge.nonviolent.game.ai.ScissorsAI;
import com.redsponge.redengine.physics.IUpdated;
import com.redsponge.redengine.physics.PActor;
import com.redsponge.redengine.physics.PhysicsWorld;

public class Enemy extends PActor implements IUpdated, IAttackable {

    private MoveType type;
    private MoveAI moveAI;
    private Vector2 vel;

    private HandPlayer player;
    private float timeSinceAttack;

    public Enemy(PhysicsWorld worldIn, MoveType type, HandPlayer player) {
        super(worldIn);
        this.type = type;
        this.player = player;
        switch (type) {
            case ROCK:
                this.moveAI = new RockAI(200, 300, 0.1f);
                break;
            case PAPER:
                this.moveAI = new PaperAI(100, 1, 250);
                break;
            case SCISSORS:
                this.moveAI = new ScissorsAI(300);
        }
        this.vel = new Vector2();

        pos.set(250, 250);
        size.set(30, 30);

        timeSinceAttack = 0;
    }

    @Override
    public void update(float delta) {
        if(moveAI.shouldAttack(this, player)) {
            if(timeSinceAttack <= 0) {
                moveAI.attack(this, player);
                timeSinceAttack = moveAI.getAttackDelay();
            } else {
                timeSinceAttack -= delta;
            }
        } else {
            moveAI.wander(this, player, vel);
            moveX(vel.x * delta, null);
            moveY(vel.y * delta, null);
        }

    }

    @Override
    public boolean attack(Rectangle attackRange, float damage) {
        return false;
    }

    public PhysicsWorld getWorld() {
        return worldIn;
    }
}
