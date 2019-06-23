package com.redsponge.nonviolent.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.redsponge.nonviolent.Constants;
import com.redsponge.nonviolent.NonViolentBattle;
import com.redsponge.nonviolent.Utils;
import com.redsponge.redengine.assets.Asset;
import com.redsponge.redengine.physics.PhysicsDebugRenderer;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.utils.GameAccessor;
import com.redsponge.redengine.utils.GeneralUtils;
import com.redsponge.redengine.utils.IntVector2;

public class GameScreen extends AbstractScreen {

    private Player player;
    private RPSWorld world;
    private PhysicsDebugRenderer pdr;

    private FitViewport viewport;
    private Vector3 temp;

    private float timeUntilSpawn;

    @Asset(path = "grass_background.png")
    private Texture grassBackground;

    public static final IntVector2[] SPAWN_POSITIONS = {
        new IntVector2(50, 50),
        new IntVector2(50, Constants.GAME_HEIGHT - 50),
        new IntVector2(Constants.GAME_WIDTH - 50, Constants.GAME_HEIGHT - 50),
        new IntVector2(Constants.GAME_WIDTH - 50, 50)
    };


    public GameScreen(GameAccessor ga) {
        super(ga);
    }

    @Override
    public void show() {
        viewport = new FitViewport(Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
        viewport.apply(true);

        world = new RPSWorld();
        player = new Player(world);
        player.pos.set((int) viewport.getWorldWidth() / 2, (int) viewport.getWorldHeight() / 2);
        world.addActor(player);

        world.addSolid(new Wall(world, 0, 0, 1, (int) viewport.getWorldHeight()));
        world.addSolid(new Wall(world, 0, (int) viewport.getWorldHeight() - 1, (int) viewport.getWorldWidth(), 1));
        world.addSolid(new Wall(world, 0, 0, (int) viewport.getWorldWidth(), 1));
        world.addSolid(new Wall(world, (int) (viewport.getWorldWidth() - 1), 0, 1, (int) viewport.getWorldHeight()));

        pdr = new PhysicsDebugRenderer();
        temp = new Vector3();
        timeUntilSpawn = 2;
    }

    @Override
    public void tick(float delta) {
        world.update(delta);
        timeUntilSpawn -= delta;
        if(timeUntilSpawn <= 0) {
            IntVector2 spawn = getSpawnPosition();
            world.addActor(getBestSpawnChoice().create(world, player, spawn.x, spawn.y));
            timeUntilSpawn = 2;
        }

        if(player.dead) {
            NonViolentBattle.instance.setScreen(new GameScreen(ga));
        }
    }

    private IntVector2 getSpawnPosition() {
        return GeneralUtils.randomItem(SPAWN_POSITIONS);
    }

    private MoveType getBestSpawnChoice() {
        int numRocks = world.getRocks().size;
        int numPaper = world.getPapers().size;
        int numSciss = world.getScissors().size;

        int rockChance = 20;
        int scissChance = 100;
        int paperChance = 5;

        if(numRocks == 0) {
            rockChance += 20;
        }
        if(numPaper == 0 && numRocks > 0) {
            paperChance += 5;
        }
        if(numPaper == 0 && numRocks >= 3) {
            paperChance += 20;
        }
        if(numRocks > 0 && numSciss < 3) {
            scissChance += 30;
        }
        if(numSciss > 10 && numRocks < 4) {
            rockChance += 40;
        }
        if(numRocks > 6 && numPaper < 1) {
            paperChance += 80;
        }
        if(numSciss > 6) {
            scissChance -= 80;
        }

        return Utils.getByChance(MoveType.values(), new int[] {rockChance, paperChance, scissChance});
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float zoom = 0.7f;
        ((OrthographicCamera)viewport.getCamera()).zoom = zoom;
        Vector3 camPos = viewport.getCamera().position;
        camPos.lerp(new Vector3(player.pos.x, player.pos.y, 0), 0.1f);

        if(camPos.x < viewport.getWorldWidth() / 2 * zoom) {
            camPos.x = viewport.getWorldWidth() / 2 * zoom;
        }
        else if(camPos.x > (viewport.getWorldWidth() - viewport.getWorldWidth() / 2 * zoom)) {
            camPos.x = viewport.getWorldWidth() - viewport.getWorldWidth() / 2 * zoom;
        }

        if(camPos.y < viewport.getWorldHeight() / 2 * zoom) {
            camPos.y = viewport.getWorldHeight() / 2 * zoom;
        }
        else if(camPos.y > (viewport.getWorldHeight() - viewport.getWorldHeight() / 2 * zoom)) {
            camPos.y = viewport.getWorldHeight() - viewport.getWorldHeight() / 2 * zoom;
        }

        viewport.apply();


        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        batch.draw(grassBackground, 0,0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();

        pdr.render(world, viewport.getCamera().combined);

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        for (Enemy paper : world.getPapers()) {
            Rectangle attack = paper.getAttackRectangle();
            if(attack != null) {
                shapeRenderer.rect(attack.x, attack.y, attack.width, attack.height);
            }
        }
        shapeRenderer.end();


    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
