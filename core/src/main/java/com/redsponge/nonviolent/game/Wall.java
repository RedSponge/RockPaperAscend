package com.redsponge.nonviolent.game;

import com.redsponge.redengine.physics.PSolid;
import com.redsponge.redengine.physics.PhysicsWorld;

public class Wall extends PSolid {

    public Wall(PhysicsWorld worldIn, int x, int y, int w, int h) {
        super(worldIn);
        pos.set(x, y);
        size.set(w, h);
    }
}
