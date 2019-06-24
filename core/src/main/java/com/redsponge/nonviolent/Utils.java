package com.redsponge.nonviolent;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.redsponge.redengine.utils.MathUtilities;

public class Utils {

    public static <T> T getByChance(T[] arr, int[] chances) {


        double totalWeight = 0.0d;
        for (int i = 0; i < arr.length; i++) {
            totalWeight += chances[i];
        }

// Now choose a random item
        int randomIndex = -1;
        double random = Math.random() * totalWeight;
        for (int i = 0; i < arr.length; ++i)
        {
            random -= chances[i];
            if (random <= 0.0d)
            {
                randomIndex = i;
                break;
            }
        }
        return arr[randomIndex];
    }

    public static Vector2 getDirectionVector(Vector2 a, Vector2 b) {
        return getDirectionVector(a.x, a.y, b.x, b.y);
    }

    public static Vector2 getDirectionVector(float ax, float ay, float bx, float by) {
        float x = ax - bx;
        float y = ay - by;

        float angle = MathUtils.atan2(y, x);

        float vx = -MathUtils.cos(angle);
        float vy = -MathUtils.sin(angle);

        return new Vector2(vx, vy);
    }

    public static Animation<TextureRegion> parseAnimation(TextureAtlas atlas, String name, int start, int end, float dur, PlayMode playMode) {
        TextureRegion[] frames = new TextureRegion[end - start + 1];
        for (int i = start; i <= end; i++) {
            frames[i - start] = atlas.findRegion(name, i);
        }
        return new Animation<TextureRegion>(dur, new Array<TextureRegion>(frames), playMode);
    }

}
