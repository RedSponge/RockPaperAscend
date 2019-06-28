package com.redsponge.nonviolent.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.redsponge.nonviolent.Constants;
import com.redsponge.nonviolent.Utils;
import com.redsponge.redengine.assets.Asset;
import com.redsponge.redengine.physics.PhysicsDebugRenderer;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.transitions.TransitionTemplates;
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

    @Asset(path = "textures/textures.atlas")
    private TextureAtlas gameTextures;

    public static Animation<TextureRegion> scissorsAnimation;
    public static Animation<TextureRegion> paperAnimation;
    public static Animation<TextureRegion> rockAnimation;
    public static Animation<TextureRegion> paperAttackAnimation;

    @Asset(path = "particles/particles.atlas")
    private TextureAtlas particleTextures;
    private ParticleEffect paperSplash;
    public static ParticleEffectPool paperSplashPool;

    public static TextureRegion stoneBulletTexture;
    public static TextureRegion playerIcon;

    public static DelayedRemovalArray<PooledEffect> runningEffects = new DelayedRemovalArray<PooledEffect>();

    private boolean transitioned;

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
        loadAnimations();

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

        paperSplash = new ParticleEffect();
        paperSplash.load(Gdx.files.internal("particles/paper_explosion.p"), particleTextures);
        paperSplashPool = new ParticleEffectPool(paperSplash, 20, 100);
        transitioned = true;
    }

    private void loadAnimations() {
        scissorsAnimation = Utils.parseAnimation(gameTextures, "enemy/scissors/run", 1, 4, 0.1f, PlayMode.LOOP);
        rockAnimation = Utils.parseAnimation(gameTextures, "enemy/rock/run", 1, 4,0.25f, PlayMode.LOOP);
        paperAnimation = Utils.parseAnimation(gameTextures, "enemy/paper/run", 1, 4, 0.1f, PlayMode.LOOP);
        paperAttackAnimation = Utils.parseAnimation(gameTextures, "enemy/paper/attack", 1, 7, 0.1f, PlayMode.LOOP);
        stoneBulletTexture = gameTextures.findRegion("enemy/rock/bullet");
        playerIcon = gameTextures.findRegion("player");
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

        for (PooledEffect runningEffect : runningEffects) {
            runningEffect.update(delta);
            if(runningEffect.isComplete()) {
                runningEffect.free();
                runningEffects.removeValue(runningEffect, true);
            }
        }

        if((player.dead || Gdx.input.isKeyPressed(Keys.ESCAPE)) && transitioned) {
            ga.transitionTo(new GameScreen(ga), TransitionTemplates.linearFade(1));
            transitioned = false;
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

        paperChance = 23973423;

        return Utils.getByChance(MoveType.values(), new int[] {rockChance, paperChance, scissChance});
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float zoom = 1;/*0.6f;*/
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
        for (Enemy enemy : world.getEnemies()) {
            enemy.render(batch);
        }
        for (StoneBullet bullet : world.getBullets()) {
            bullet.render(batch);
        }
        player.render(batch);
        for (PooledEffect runningEffect : runningEffects) {
            runningEffect.draw(batch);
        }
        batch.end();

//        pdr.render(world, viewport.getCamera().combined);

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

    @Override
    public void dispose() {
        paperSplash.dispose();
        for (PooledEffect runningEffect : runningEffects) {
            runningEffect.free();
        }
        runningEffects.clear();
    }
}
                                                                                                                                                                                                                          