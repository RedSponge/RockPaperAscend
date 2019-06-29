package com.redsponge.nonviolent.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.nonviolent.Constants;
import com.redsponge.nonviolent.Utils;
import com.redsponge.redengine.light.PointLight;
import com.redsponge.redengine.physics.PhysicsWorld;

public class EnemyRock extends Enemy {

    private Vector2 target;
    private Vector2 self;
    private Vector2 vel;

    private float timeUntilAttack;
    private float timeAlive;

    private Color c;

    public EnemyRock(PhysicsWorld worldIn, Player player, int x, int y, float speed) {
        super(worldIn, player, x, y, speed);
        target = new Vector2();
        self = new Vector2();
        vel = new Vector2();
        size.set(30, 30);

        timeUntilAttack = 3;
        timeAlive = 0;

        c = getRepresentingColor().cpy().add(0, 0, 0, 0.5f);
        ((PointLight) light).setColor(c);
    }

    @Override
    public void additionalUpdate(float delta) {
        timeAlive += delta;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                target.set(0, 0);
                System.out.println("COLLISION OF ROCK!!!!");
            }
        };

        self.set(pos.x + size.x / 2, pos.y + size.y / 2);
        AIActions.wander(self, target, vel, Constants.GAME_BOUNDS);

        moveX(vel.x * speed * delta, runnable);
        moveY(vel.y * speed * delta, runnable);

        if (Vector2.dst2(pos.x + size.x / 2, pos.y + size.y / 2, player.pos.x + player.size.x / 2, player.pos.y + player.size.y / 2) < 500 * 500) {
            timeUntilAttack -= delta;
            c.a = (3 - Math.max(timeUntilAttack, 0)) / 3;
            if (timeUntilAttack <= 0) {

                shootStone();
                timeUntilAttack = 3;
                Utils.playSoundRandomlyPitched(GameScreen.rockAttackSoundS);
            }
        }
        tryKill(MoveType.SCISSORS);
    }

    @Override
    public void additionalRender(SpriteBatch batch) {
        TextureRegion frame = GameScreen.rockAnimation.getKeyFrame(timeAlive);
        float w = frame.getRegionWidth() * 2;
        float h = frame.getRegionHeight() * 2;
        batch.draw(frame, pos.x - w / 2 + size.x / 2f, pos.y - h / 2 + size.y / 2f, w, h);
    }

    private void shootStone() {
        StoneBullet bullet = new StoneBullet(worldIn, pos.copy().add(size.x / 2, size.y / 2), Utils.getDirectionVector(pos.x, pos.y, player.pos.x, player.pos.y), player);
        worldIn.addActor(bullet);
    }

    @Override
    public Rectangle getAttackRectangle() {
        return null;
    }


    @Override
    public Color getRepresentingColor() {
        return Color.LIGHT_GRAY;
    }

    @Override
    public Animation<TextureRegion> getAscendAnimation() {
        return GameScreen.rockAscend;
    }
}
