package com.redsponge.nonviolent.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.redengine.physics.PhysicsWorld;

public class EnemyScissors extends Enemy {

    private Rectangle representingRectangle;
    private Vector2 vel;
    private Vector2 self;
    private Vector2 playerVec;

    public EnemyScissors(PhysicsWorld worldIn, Player player, int x, int y, float speed) {
        super(worldIn, player, x, y, speed);
        representingRectangle = new Rectangle(x, y, size.x, size.y);
        vel = new Vector2();
        self = new Vector2();
        playerVec = new Vector2();

        size.set(30, 30);
    }

    @Override
    public void update(float delta) {
        self.set(pos.x + size.x / 2, pos.y + size.y / 2);
        playerVec.set(player.pos.x + player.size.x / 2, player.pos.y + player.size.y / 2);

        AIActions.follow(self, playerVec, vel, 0.1f, 0.3f);

        moveX(vel.x * speed * delta, null);
        moveY(vel.y * speed * delta, null);

        tryKill(MoveType.PAPER);
    }

    @Override
    public Rectangle getAttackRectangle() {
        updateRepresentation();
        return representingRectangle;
    }

    private void updateRepresentation() {
        representingRectangle.set(pos.x, pos.y, size.x, size.y);
    }
}
