package com.redsponge.nonviolent.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.redengine.light.FlickeringPointLight;
import com.redsponge.redengine.light.Light;

public class AscendedSoul {

    private Vector2 pos;
    private Vector2 vel;

    private float timeAlive;
    private float delayBeforeFloatUp;
    private float floatUpTime;

    private FlickeringPointLight light;
    private ParticleEffectPool.PooledEffect effect;

    public static final Color COLOR = Color.YELLOW.cpy().add(0, 0, 0.8f, 0);

    public AscendedSoul(int x, int y, float delayBeforeFloatUp, float floatUpTime) {
        this.delayBeforeFloatUp = delayBeforeFloatUp;
        this.floatUpTime = floatUpTime;
        this.pos = new Vector2(x, y);

        timeAlive = 0;
        light = new FlickeringPointLight(pos.x, pos.y, 200, 1, 20);
        light.setColor(COLOR);

        GameScreen.lightSystemS.addLight(light);
        effect = GameScreen.soulPool.obtain();
        effect.setPosition(pos.x, pos.y);
        effect.start();
        GameScreen.runningEffects.add(effect);
    }

    public void tick(float delta) {
        timeAlive += delta;
        if(timeAlive > delayBeforeFloatUp) {
            pos.y += 100 * delta;
        }
        if(timeAlive > floatUpTime + delayBeforeFloatUp) {
            remove();
        }
        light.getPosition().set(pos.x, pos.y);
        light.update(delta);
        effect.setPosition(pos.x, pos.y);
    }

    public void render(SpriteBatch batch) {
        TextureRegion frame = GameScreen.ascendedGhostAnimation.getKeyFrame(timeAlive);
        float w = frame.getRegionWidth() * 2;
        float h = frame.getRegionHeight() * 2;
        batch.draw(frame, pos.x - w / 2, pos.y - h / 2, w, h);
    }

    public void remove() {
        GameScreen.ascendedSoulsS.removeValue(this, true);
        GameScreen.lightSystemS.removeLight(light);
        effect.free();
        GameScreen.runningEffects.removeValue(effect, true);
    }

}
