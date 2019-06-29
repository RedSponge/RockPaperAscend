package com.redsponge.nonviolent.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.redengine.input.InputTranslator;
import com.redsponge.redengine.input.SimpleInputTranslator;
import com.redsponge.redengine.light.Light;
import com.redsponge.redengine.light.PointLight;
import com.redsponge.redengine.physics.IUpdated;
import com.redsponge.redengine.physics.PActor;
import com.redsponge.redengine.physics.PhysicsWorld;
import com.redsponge.redengine.utils.Logger;

public class Player extends PActor implements IUpdated {

    public float timeSinceAscension;
    private InputTranslator input;
    private Rectangle representation;
    public boolean dead;
    private Light light;

    private Vector2 vel;
    private float timeAlive;

    private boolean isLeft;

    private float deadTime;
    private boolean flag;

    public Player(PhysicsWorld worldIn) {
        super(worldIn);
        pos.set(100, 100);
        size.set(30, 30);

        input = new SimpleInputTranslator();
        representation = new Rectangle(pos.x, pos.y, size.x, size.y);

        light = new PointLight(pos.x, pos.y, 300);
        GameScreen.lightSystemS.addLight(light);

        timeAlive = 0;
        vel = new Vector2();
    }

    @Override
    public void update(float delta) {
        if(dead) {
            deadTime += delta;
            if(deadTime > 3) {
                timeSinceAscension += delta;
                System.out.println(timeSinceAscension + " " + deadTime);
            }
            return;
        }
        timeAlive += delta;
        float speed = 400;
        vel.set(input.getHorizontal() * speed, input.getVertical() * speed);
        if(!isLeft && vel.x < 0 || isLeft && vel.x > 0) {
            isLeft = !isLeft;
        }
        moveX(vel.x * delta, null);
        moveY(vel.y * delta, null);
        light.getPosition().set(pos.x + size.x / 2, pos.y + size.y / 2);
    }

    public boolean attack(Rectangle attackRange, float damage) {
        if(dead) return false;
        updateRepresentation();

        if(attackRange.overlaps(representation)) {
            Logger.log(this, "Attacked! Damage:", damage);
            dead = true;
            deadTime = 0;
            GameScreen.playerHitSoundS.play();
            return true;
        }

        return false;
    }

    public void render(SpriteBatch batch) {
        if(dead) {
            deadAnimation(batch);
        } else {
            Animation<TextureRegion> anim = vel.isZero() ? GameScreen.playerIdleAnimation : GameScreen.playerRunAnimation;
            TextureRegion frame = anim.getKeyFrame(timeAlive);
            frame.flip(isLeft, false);
            float w = frame.getRegionWidth() * 2;
            float h = frame.getRegionHeight() * 2;
            batch.draw(frame, pos.x - w / 2 + size.x / 2f, pos.y - h / 2 + size.y / 2f, w, h);
            frame.flip(isLeft, false);
        }
    }

    private void deadAnimation(SpriteBatch batch) {
        float deadTime = this.deadTime;
        float x = 0;
        float y = 0;
        float time = 3;
        if(deadTime < time) {
            x = MathUtils.random(-deadTime, deadTime);
            y = MathUtils.random(-deadTime, deadTime);
        } else if(!flag) {
            flag = true;
            GameScreen.ascendSoundS.play();
            GameScreen.ascendedSoulsS.add(new AscendedSoul(pos.x + size.x / 2, pos.y + size.y / 2, 10000000, 100000));
        }
        if (GameScreen.playerAscend.isAnimationFinished(deadTime - time)) return;
        TextureRegion deathFrame = GameScreen.playerAscend.getKeyFrame(Math.max(deadTime - time, 0));
        float w = deathFrame.getRegionWidth() * 2;
        float h = deathFrame.getRegionHeight() * 2;
        batch.draw(deathFrame, pos.x - w / 2 + size.x / 2f + x, pos.y - h / 2 + size.y / 2f + y, w, h);
    }

    private void updateRepresentation() {
        representation.set(pos.x, pos.y, size.x, size.y);
    }

    public Rectangle getRepresentation() {
        updateRepresentation();
        return representation;
    }

    @Override
    protected void remove() {
        super.remove();
        GameScreen.lightSystemS.removeLight(light);
    }

}
