package com.redsponge.nonviolent.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.redsponge.redengine.physics.IUpdated;
import com.redsponge.redengine.physics.PActor;
import com.redsponge.redengine.physics.PhysicsWorld;
import com.redsponge.redengine.utils.IntVector2;
import com.redsponge.redengine.utils.MathUtilities;

public abstract class Enemy extends PActor implements IUpdated {

    protected Player player;
    protected float speed;

    public Enemy(PhysicsWorld worldIn, Player player, int x, int y, float speed) {
        super(worldIn);
        this.player = player;
        this.speed = speed;

        pos.set(x, y);
    }

    public abstract void update(float delta);

    public abstract Rectangle getAttackRectangle();

    protected void tryKill(MoveType type) {
        Rectangle myAttack = getAttackRectangle();
        if(myAttack != null) {
            for (Enemy enemy : ((RPSWorld) worldIn).getByType(type)) {
                if (myAttack.overlaps(new Rectangle(enemy.pos.x, enemy.pos.y, enemy.size.x, enemy.size.y))) {
                    enemy.remove();
                }
            }

            if(MathUtilities.rectanglesIntersect(new IntVector2((int) myAttack.x, (int) myAttack.y), new IntVector2((int) myAttack.width, (int) myAttack.height), player.pos, player.size)) {
                player.attack(myAttack, 100);
            }
        }
    }

    public abstract void render(SpriteBatch batch);

    @Override
    public void remove() {
        super.remove();
    }
}
