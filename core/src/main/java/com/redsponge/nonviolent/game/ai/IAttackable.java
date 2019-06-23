package com.redsponge.nonviolent.game.ai;

import com.badlogic.gdx.math.Rectangle;

public interface IAttackable {

    boolean attack(Rectangle attackRange, float damage);

}
