package com.redsponge.nonviolent.game.ai;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.nonviolent.game.Enemy;
import com.redsponge.nonviolent.game.StoneBullet;
import com.redsponge.redengine.physics.PActor;
import com.redsponge.redengine.utils.Logger;

public class RockAI implements MoveAI {

    private Vector2 selfPos, targetPos;
    private float speed;
    private float attackRad;
    private float driftiness;

    private float vx;
    private float vy;

    public RockAI(float speed, float attackRad, float driftiness) {
        this.speed = speed;
        this.attackRad = attackRad;
        this.driftiness = driftiness;

        selfPos = new Vector2();
        targetPos = new Vector2();
    }

    @Override
    public Vector2 wander(PActor actor, PActor target, Vector2 out) {
        selfPos.set(actor.pos.x, actor.pos.y).add(actor.size.x / 2, actor.size.y / 2);
        targetPos.set(target.pos.x, target.pos.y).add(target.size.x / 2, target.size.y / 2);

        float x = actor.pos.x - target.pos.x;
        float y = actor.pos.y - target.pos.y;

        float angle = (float) Math.atan2(y, x);
        vx = -MathUtils.cos(angle);
        vy = -MathUtils.sin(angle);

        Logger.log(this, x, y, angle, vx, vy);
        if(out == null) {
            return new Vector2(vx, vy).scl(speed);
        }
        Vector2 lerped = out.lerp(new Vector2(vx, vy).scl(speed), driftiness);
        return out.set(lerped);
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
        actor.getWorld().addActor(new StoneBullet(actor.getWorld(), actor.pos.copy(), new Vector2(vx, vy)));
    }

}
