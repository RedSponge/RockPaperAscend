package com.redsponge.nonviolent;

import com.badlogic.gdx.math.MathUtils;
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

}
