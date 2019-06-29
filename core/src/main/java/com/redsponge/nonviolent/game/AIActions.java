package com.redsponge.nonviolent.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.nonviolent.Utils;

public class AIActions {

    public static Vector2 follow(Vector2 self, Vector2 followed, Vector2 out, float drift, float fail) {
        Vector2 dir = Utils.getDirectionVector(self, followed);
        out.lerp(dir, drift);
        out.add(MathUtils.random(-fail, fail), MathUtils.random(-fail, fail)).nor();
        return out;
    }

    public static Vector2 wander(Vector2 self, Vector2 currentTarget, Vector2 out, Rectangle bounds) {
        if(self.dst2(currentTarget) < 1 || currentTarget.isZero()) {
            currentTarget.set(MathUtils.random(bounds.x, bounds.x + bounds.width), MathUtils.random(bounds.y, bounds.y + bounds.height));
        }
        return follow(self, currentTarget, out, 1, 0);
    }

}
