package com.redsponge.nonviolent.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.redsponge.redengine.light.Light;
import com.redsponge.redengine.light.PointLight;
import com.redsponge.redengine.physics.IUpdated;
import com.redsponge.redengine.physics.PActor;
import com.redsponge.redengine.physics.PhysicsWorld;
import com.redsponge.redengine.utils.IntVector2;
import com.redsponge.redengine.utils.MathUtilities;

public abstract class Enemy extends PActor implements IUpdated {

    protected Player player;
    protected float speed;

    protected Light light;
    protected Light innerLight;

    protected boolean dead;
    private float deadTime;

    public Enemy(PhysicsWorld worldIn, Player player, int x, int y, float speed) {
        super(worldIn);
        this.player = player;
        this.speed = speed;

        pos.set(x, y);
        light = new PointLight(pos.x, pos.y, 250);
        Color c = (getRepresentingColor());
        c.a = 0.4f;
        ((PointLight)light).setColor(c);
        GameScreen.lightSystemS.addLight(light);

        innerLight = new PointLight(pos.x, pos.y, 100);
        Color inner = c.cpy();
        inner.add(0.3f, 0.3f, 0.3f, 0.3f);
        ((PointLight)innerLight).setColor(inner);
        GameScreen.lightSystemS.addLight(innerLight);
    }

    public void update(float delta) {
        if(dead) {
            deadTime += delta;
            if(getAscendAnimation().isAnimationFinished(deadTime)) {
                remove();
            }
        } else {
            additionalUpdate(delta);
            light.getPosition().set(pos.x + size.x / 2, pos.y + size.y / 2);
            innerLight.getPosition().set(pos.x + size.x / 2, pos.y + size.y / 2);
        }
    }

    public abstract void additionalUpdate(float delta);

    public abstract Rectangle getAttackRectangle();

    protected void tryKill(MoveType type) {
        if(dead) {
            return;
        }

        Rectangle myAttack = getAttackRectangle();
        if(myAttack != null) {
            for (Enemy enemy : ((RPSWorld) worldIn).getByType(type)) {
                if (myAttack.overlaps(new Rectangle(enemy.pos.x, enemy.pos.y, enemy.size.x, enemy.size.y))) {
                    enemy.kill();
                }
            }

            if(MathUtilities.rectanglesIntersect(new IntVector2((int) myAttack.x, (int) myAttack.y), new IntVector2((int) myAttack.width, (int) myAttack.height), player.pos, player.size)) {
                player.attack(myAttack, 0);
                remove();
            }
        }
    }

    public abstract void additionalRender(SpriteBatch batch);

    public void render(SpriteBatch batch) {
        if(dead) {
            TextureRegion ascension = getAscendAnimation().getKeyFrame(deadTime);
            float w = ascension.getRegionWidth() * 2;
            float h = ascension.getRegionHeight() * 2;

            batch.draw(ascension, pos.x - w / 2 + size.x / 2f, pos.y - h / 2 + size.y / 2f, w, h);
        } else {
            additionalRender(batch);
        }
    }

    public void kill() {
        if(dead) return;
        GameScreen.lightSystemS.removeLight(light);
        GameScreen.lightSystemS.removeLight(innerLight);
        GameScreen.ascendedSoulsS.add(new AscendedSoul(pos.x, pos.y, 2, 3));
        dead = true;
        deadTime = 0;
        GameScreen.ascendSoundS.play();
        GameScreen.soulsAscended++;
    }

    public abstract Color getRepresentingColor();

    public abstract Animation<TextureRegion> getAscendAnimation();
}
