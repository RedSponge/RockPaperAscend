package com.redsponge.nonviolent.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.redsponge.redengine.input.InputTranslator;

public class GameInput implements InputTranslator {

    public float getHorizontal() {
        int left = Gdx.input.isKeyPressed(Keys.A) ? 1 : 0;
        int right = Gdx.input.isKeyPressed(Keys.D) ? 1 : 0;
        return (float)(right - left);
    }

    public float getVertical() {
        int down = Gdx.input.isKeyPressed(Keys.S) ? 1 : 0;
        int up = Gdx.input.isKeyPressed(Keys.W) ? 1 : 0;
        return (float)(up - down);
    }

    public boolean isJumping() {
        return Gdx.input.isKeyPressed(62);
    }

    public boolean isJustJumping() {
        return Gdx.input.isKeyJustPressed(62);
    }
}
