package com.redsponge.nonviolent.game;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.redengine.physics.PhysicsWorld;

public class EnemyPaper extends Enemy {

    private Rectangle attackRectangle;
    private float timeUntilAttack;
    private boolean inAttack;
    private int range;
    private float timeSinceAttack;

    private Vector2 vel;
    private Vector2 self;
    private Vector2 playerVec;
    private float timeAlive;
    private boolean spawnedParticles;

    private float attackChargeTime;
    private boolean attackedAfterCharge;

    public EnemyPaper(PhysicsWorld worldIn, Player player, int x, int y, float speed, int range) {
        super(worldIn, player, x, y, speed);
        this.range = range;
        attackRectangle = null;
        timeUntilAttack = 0;
        timeSinceAttack = 0;
        vel = new Vector2();
        self = new Vector2();
        playerVec = new Vector2();

        size.set(30, 30);
        timeAlive = 0;

    }

    @Override
    public void update(float delta) {
        timeAlive += delta;
        self.set(pos.x, pos.y);
        playerVec.set(player.pos.x, player.pos.y);

        if(inRange() && !inAttack) {
            timeUntilAttack -= delta;
            System.out.println(timeUntilAttack);
            if(timeUntilAttack <= 0) {
                startAttack();
            }
        }

        if(inAttack) {
            if(attackChargeTime <= 0) {
                timeSinceAttack += delta;
            } else {
                attackChargeTime -= delta;
            }

            if(timeSinceAttack >= 0.2f && !attackedAfterCharge) {
                startAttackCharge();
            } else if(attackedAfterCharge && attackChargeTime <= 0) {
                if (!spawnedParticles) {
                    PooledEffect effect = GameScreen.paperSplashPool.obtain();
                    effect.setPosition(pos.x + size.x, pos.y + size.y);
                    effect.start();
                    GameScreen.runningEffects.add(effect);
                    spawnedParticles = true;
                    GameScreen.paperAttackSoundS.play(1, MathUtils.random(0.75f, 1.25f), 0);
                }
                if (attackRectangle == null && timeSinceAttack >= 0.45f) {
                    spawnAttack();
                }
                if (timeSinceAttack >= 1f) {
                    stopAttack();
                }
            }
        } else {
            AIActions.follow(self, playerVec, vel, 1, 0);
            moveX(vel.x * delta * speed, null);
            moveY(vel.y * delta * speed, null);
        }

        tryKill(MoveType.ROCK);
    }

    private void startAttackCharge() {
        attackChargeTime = 0.3f;
        attackedAfterCharge = true;
    }

    private void spawnAttack() {
        attackRectangle = new Rectangle(pos.x - range + size.x / 2, pos.y - range + size.y / 2, range * 2, range * 2);
    }

    private void stopAttack() {
        inAttack = false;
        attackRectangle = null;
        spawnedParticles = false;
        attackedAfterCharge = false;
    }

    private void startAttack() {
        inAttack = true;
        timeSinceAttack = 0;
        timeUntilAttack = 0;
        System.out.println("ATTACK!");
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion frame;
        if (inAttack) {
            frame = GameScreen.paperAttackAnimation.getKeyFrame(timeSinceAttack);
        } else {
            frame = GameScreen.paperAnimation.getKeyFrame(timeAlive);
        }
        try {
            float w = frame.getRegionWidth() * 2;
            float h = frame.getRegionHeight() * 2;
            batch.draw(frame, pos.x - w / 2 + size.x / 2f, (float) (pos.y - h / 2 + size.y / 2f + Math.sin(timeAlive * 5) * 20), w, h);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean inRange() {
        return Vector2.dst2(player.pos.x, player.pos.y, pos.x, pos.y) < range * range;
    }

    @Override
    public Rectangle getAttackRectangle() {
        return attackRectangle;
    }
}
