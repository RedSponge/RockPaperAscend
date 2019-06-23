package com.redsponge.nonviolent.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.redsponge.nonviolent.Constants;
import com.redsponge.redengine.desktop.DesktopUtil;
import com.redsponge.redengine.physics.PhysicsDebugRenderer;
import com.redsponge.redengine.physics.PhysicsWorld;
import com.redsponge.redengine.save.Player;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.utils.GameAccessor;
import com.redsponge.redengine.utils.GeneralUtils;

public class GameScreen extends AbstractScreen {

    private HandPlayer player;
    private PhysicsWorld world;
    private PhysicsDebugRenderer pdr;

    private FitViewport viewport;
    private Vector3 temp;

    private float timeUntilSpawn;

    public GameScreen(GameAccessor ga) {
        super(ga);
    }

    @Override
    public void show() {
        viewport = new FitViewport(Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
        viewport.apply(true);

        world = new PhysicsWorld();
        player = new HandPlayer(world);
        world.addActor(player);

        world.addActor(new Enemy(world, MoveType.SCISSORS, player));

        pdr = new PhysicsDebugRenderer();
        temp = new Vector3();
        timeUntilSpawn = 2;
    }

    @Override
    public void tick(float delta) {
        world.update(delta);
        timeUntilSpawn -= delta;
        if(timeUntilSpawn <= 0) {
            world.addActor(new Enemy(world, GeneralUtils.randomItem(MoveType.values()), player));
            timeUntilSpawn = 2;
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        pdr.render(world, viewport.getCamera().combined);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
