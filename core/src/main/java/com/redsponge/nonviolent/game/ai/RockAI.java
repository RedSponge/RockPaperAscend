package com.redsponge.nonviolent.game.ai;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.nonviolent.Constants;
import com.redsponge.nonviolent.game.Enemy;
import com.redsponge.nonviolent.game.HandPlayer;
import com.redsponge.nonviolent.game.StoneBullet;
import com.redsponge.redengine.physics.PActor;

public class RockAI implements MoveAI {

    private Vector2 selfPos, targetPos;
    private float speed;
    private float attackRad;
    private float driftiness;

    private float vx;
    private float vy;

    private Vector2 nextTarget;

    public RockAI(float speed, float attackRad, float driftiness) {
        this.speed = speed;
        this.attackRad = attackRad;
        this.driftiness = driftiness;

        selfPos = new Vector2();
        targetPos = new Vector2();
        nextTarget = generateRandomTarget();
    }

    private Vector2 generateRandomTarget() {
        return new Vector2(MathUtils.random(Constants.GAME_WIDTH), MathUtils.random(Constants.GAME_HEIGHT));
    }

    @Override
    public Vector2 wander(PActor actor, PActor target, Vector2 out) {
        if(selfPos.dst2(nextTarget) < speed * speed) {
            nextTarget = generateRandomTarget();
        }

        selfPos.set(actor.pos.x, actor.pos.y).add(actor.size.x / 2, actor.size.y / 2);

        float x = actor.pos.x - nextTarget.x;
        float y = actor.pos.y - nextTarget.y;

        float angle = (float) Math.atan2(y, x);
        float vx = -MathUtils.cos(angle);
        float vy = -MathUtils.sin(angle);

        if(out == null) {
            return new Vector2(vx, vy).scl(speed);
        }

        return out.set(vx, vy).scl(speed);
    }

    @Override
    public boolean shouldAttack(PActor actor, PActor target) {
        float x = actor.pos.x - target.pos.x;
        float y = actor.pos.y - target.pos.y;

        float angle = (float) Math.atan2(y, x);
        vx = -MathUtils.cos(angle);
        vy = -MathUtils.sin(angle);

        return Vector2.dst2(actor.pos.x, actor.pos.y, target.pos.x, target.pos.y) < attackRad * attackRad;
    }

    @Override
    public float getAttackDelay() {
        return 0.5f;
    }

    @Override
    public void attack(Enemy actor, IAttackable attacked) {
        actor.getWorld().addActor(new StoneBullet(actor.getWorld(), actor.pos.copy(), new Vector2(vx, vy), (HandPlayer) attacked));
    }

}
