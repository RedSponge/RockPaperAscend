package com.redsponge.nonviolent.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.redengine.physics.PhysicsWorld;

public class EnemyScissors extends Enemy {

    private Rectangle representingRectangle;
    private Vector2 vel;
    private Vector2 self;
    private Vector2 playerVec;
    private float timeAlive;
    private float timeToPlay;

    private static final float MAX_TIME_TO_PLAY = 0.3f;
    private static final int ATTACK_SOUND_PLAY_MIN_DST = 300;


    public EnemyScissors(PhysicsWorld worldIn, Player player, int x, int y, float speed) {
        super(worldIn, player, x, y, speed);
        representingRectangle = new Rectangle(x, y, size.x, size.y);
        vel = new Vector2();
        self = new Vector2();
        playerVec = new Vector2();

        size.set(30, 30);
        timeAlive = 0;
        timeToPlay = MAX_TIME_TO_PLAY;
    }

    @Override
    public void additionalUpdate(float delta) {
        timeAlive += delta;
        timeToPlay -= delta;
        self.set(pos.x + size.x / 2, pos.y + size.y / 2);
        playerVec.set(player.pos.x + player.size.x / 2, player.pos.y + player.size.y / 2);

        AIActions.follow(self, playerVec, vel, 0.1f, 0.3f);
        if(timeToPlay <= 0) {
            tryPlaySound();
            timeToPlay = MAX_TIME_TO_PLAY;
        }

        moveX(vel.x * speed * delta, null);
        moveY(vel.y * speed * delta, null);

        tryKill(MoveType.PAPER);
    }

    private void tryPlaySound() {
        float dst2 = Vector2.dst2(pos.x + size.x / 2, pos.y + size.y / 2, player.pos.x + player.size.x / 2, player.pos.y + player.size.y / 2);
        float minDst2 = ATTACK_SOUND_PLAY_MIN_DST * ATTACK_SOUND_PLAY_MIN_DST;
        System.out.println(dst2 + " " + minDst2);
        if(dst2 < minDst2) {
            float vol = (minDst2 - dst2) / minDst2;
            GameScreen.scissorsAttackSoundS.play(vol, 1, 0);
        }
    }

    @Override
    public void additionalRender(SpriteBatch batch) {
        TextureRegion frame = GameScreen.scissorsAnimation.getKeyFrame(timeAlive);
        float w = frame.getRegionWidth() * 2;
        float h = frame.getRegionHeight() * 2;
        batch.draw(frame, pos.x - w / 2 + size.x / 2f, pos.y - h / 2 + size.y / 2f, w, h);
    }

    @Override
    public Rectangle getAttackRectangle() {
        updateRepresentation();
        return representingRectangle;
    }

    private void updateRepresentation() {
        representingRectangle.set(pos.x, pos.y, size.x, size.y);
    }

    @Override
    public Color getRepresentingColor() {
        return Color.GOLDENROD;
    }

    @Override
    public Animation<TextureRegion> getAscendAnimation() {
        return GameScreen.scissorsAscend;
    }
}
