package com.redsponge.nonviolent.game.ai;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.nonviolent.game.Enemy;
import com.redsponge.redengine.physics.PActor;
import com.redsponge.redengine.utils.Logger;
import com.redsponge.redengine.utils.MathUtilities;

public class PaperAI implements MoveAI {

    private Vector2 selfPos, targetPos;
    private float speed;
    private float driftiness;

    private int attackRad;
    private int attackRadSq;

    public PaperAI(float speed, float driftiness, int attackRad) {
        this.speed = speed;
        this.driftiness = driftiness;
        this.attackRad = attackRad;
        this.attackRadSq = attackRad * attackRad;

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
        float vx = -MathUtils.cos(angle);
        float vy = -MathUtils.sin(angle);

        if(out == null) {
            return new Vector2(vx, vy).scl(speed);
        }
        Vector2 lerped = out.lerp(new Vector2(vx, vy).scl(speed), driftiness);
        return out.set(lerped);
    }

    @Override
    public float getAttackDelay() {
        return 5;
    }

    @Override
    public boolean shouldAttack(PActor actor, PActor target) {
        return Vector2.dst2(actor.pos.x, actor.pos.y, target.pos.x, target.pos.y) < attackRadSq;
    }

    @Override
    public void attack(Enemy actor, IAttackable attacked) {
        attacked.attack(new Rectangle(actor.pos.x - attackRad, actor.pos.y - attackRad, attackRad * 2, attackRad * 2),50);
    }
}
