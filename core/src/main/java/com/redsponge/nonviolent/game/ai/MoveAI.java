package com.redsponge.nonviolent.game.ai;

import com.badlogic.gdx.math.Vector2;
import com.redsponge.nonviolent.game.Enemy;
import com.redsponge.redengine.physics.PActor;

public interface MoveAI {

    Vector2 wander(PActor actor, PActor target, Vector2 out);

    boolean shouldAttack(PActor actor, PActor target);

    float getAttackDelay();

    void attack(Enemy actor, IAttackable attacked);

}
