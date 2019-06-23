package com.redsponge.nonviolent.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.nonviolent.game.ai.IAttackable;
import com.redsponge.nonviolent.game.ai.MoveAI;
import com.redsponge.nonviolent.game.ai.PaperAI;
import com.redsponge.nonviolent.game.ai.RockAI;
import com.redsponge.nonviolent.game.ai.ScissorsAI;
import com.redsponge.redengine.physics.IUpdated;
import com.redsponge.redengine.physics.PActor;
import com.redsponge.redengine.physics.PhysicsWorld;
import com.redsponge.redengine.utils.IntVector2;

public class Enemy extends PActor implements IUpdated, IAttackable {

    private MoveType type;
    private MoveAI moveAI;
    private Vector2 vel;

    private HandPlayer player;
    private float timeSinceAttack;

    public Enemy(PhysicsWorld worldIn, MoveType type, HandPlayer player, int x, int y) {
        super(worldIn);
        this.type = type;
        this.player = player;
        switch (type) {
            case ROCK:
                this.moveAI = new RockAI(100, 300, 0.1f);
                break;
            case PAPER:
                this.moveAI = new PaperAI(50, 1, 250);
                break;
            case SCISSORS:
                this.moveAI = new ScissorsAI(300);
                break;
            default:
                throw new RuntimeException("Unknown type " + type);
        }
        this.vel = new Vector2();

        pos.set(x, y);
        size.set(30, 30);

        timeSinceAttack = 0;
    }

    @Override
    public void update(float delta) {
        if(moveAI.shouldAttack(this, player)) {
            if(timeSinceAttack <= 0) {
                moveAI.attack(this, player);
                timeSinceAttack = moveAI.getAttackDelay();
            } else {
                timeSinceAttack -= delta;
            }
        }

        moveAI.wander(this, player, vel);
        moveX(vel.x * delta, null);
        moveY(vel.y * delta, null);

        for (Enemy enemy : ((RPSWorld) worldIn).getBeaten(type)) {
            Rectangle myAttack = getAttackRectangle();
            if(myAttack != null && myAttack.overlaps(new Rectangle(enemy.pos.x, enemy.pos.y, enemy.size.x, enemy.size.y))) {
                enemy.remove();
            }
        }
    }

    public Rectangle getAttackRectangle() {
        switch (type) {
            case SCISSORS:
                return new Rectangle(pos.x, pos.y, size.x, size.y);
            case ROCK:
                return null; // Rock can't attack, its bullets can
            case PAPER:
                return null; // TODO: Separate Into Classes and Have Attack Rectangle Held
        }
        return null;
    }

    @Override
    public boolean attack(Rectangle attackRange, float damage) {
        return false;
    }

    public PhysicsWorld getWorld() {
        return worldIn;
    }

    @Override
    public void remove() {
        super.remove();
    }

    public MoveType getType() {
        return type;
    }
}
