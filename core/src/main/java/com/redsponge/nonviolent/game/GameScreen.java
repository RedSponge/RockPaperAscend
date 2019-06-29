package com.redsponge.nonviolent.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.redsponge.nonviolent.Constants;
import com.redsponge.nonviolent.Utils;
import com.redsponge.redengine.assets.Asset;
import com.redsponge.redengine.assets.Fonts;
import com.redsponge.redengine.light.LightSystem;
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
    private FitViewport lightViewport;
    private Vector3 temp;

    private float timeUntilSpawn;

    @Asset(path = "grass_background.png")
    private Texture grassBackground;

    @Asset(path = "textures/textures.atlas")
    private TextureAtlas gameTextures;

    public static Animation<TextureRegion> scissorsAnimation;
    public static Animation<TextureRegion> scissorsAscend;

    public static Animation<TextureRegion> paperAnimation;
    public static Animation<TextureRegion> paperAscend;
    public static Animation<TextureRegion> paperAttackAnimation;

    public static Animation<TextureRegion> rockAnimation;
    public static Animation<TextureRegion> rockAscend;

    public static Animation<TextureRegion> playerIdleAnimation;
    public static Animation<TextureRegion> playerRunAnimation;
    public static Animation<TextureRegion> playerAscend;

    public static Animation<TextureRegion> ascendedGhostAnimation;

    @Asset(path = "particles/particles.atlas")
    private TextureAtlas particleTextures;

    @Asset(path = "sounds/paper_attack.wav")
    private Sound paperAttackSound;
    public static Sound paperAttackSoundS;

    @Asset(path = "sounds/rock_attack.wav")
    private Sound rockAttackSound;
    public static Sound rockAttackSoundS;

    @Asset(path = "sounds/scissors_attack.wav")
    private Sound scissorsAttackSound;
    public static Sound scissorsAttackSoundS;

    @Asset(path = "sounds/ascend.wav")
    private Sound ascendSound;
    public static Sound ascendSoundS;

    @Asset(path = "sounds/player_hit.wav")
    private Sound playerHitSound;
    public static Sound playerHitSoundS;

    private ParticleEffect paperSplash;

    public static ParticleEffectPool paperSplashPool;

    public static TextureRegion stoneBulletTexture;
    public static TextureRegion playerIcon;

    public static DelayedRemovalArray<PooledEffect> runningEffects = new DelayedRemovalArray<PooledEffect>();

    private boolean transitioned;

    @Asset(path = "light/point_light.png")
    private Texture pointLight;

    private LightSystem lightSystem;
    public static LightSystem lightSystemS;
    private TextureRegion lightTexture;

    private DelayedRemovalArray<AscendedSoul> ascendedSouls;

    private FitViewport guiViewport;

    private TextureRegion soulIcon;

    public static final IntVector2[] SPAWN_POSITIONS = {
        new IntVector2(50, 50),
        new IntVector2(50, Constants.GAME_HEIGHT - 50),
        new IntVector2(Constants.GAME_WIDTH - 50, Constants.GAME_HEIGHT - 50),
        new IntVector2(Constants.GAME_WIDTH - 50, 50)
    };

    public static DelayedRemovalArray<AscendedSoul> ascendedSoulsS;
    public static int soulsAscended = 0;


    public GameScreen(GameAccessor ga) {
        super(ga);
    }

    @Override
    public void show() {
        loadAnimations();

        viewport = new FitViewport(Constants.GAME_WIDTH / 2, Constants.GAME_HEIGHT  / 2);
        viewport.apply(true);

        lightViewport = new FitViewport(viewport.getWorldWidth(), viewport.getWorldHeight());
        guiViewport = new FitViewport(Constants.GUI_WIDTH, Constants.GUI_HEIGHT);
        lightSystem = new LightSystem(batch, assets, lightViewport);
        lightSystem.setAmbianceColor(Color.BLUE.cpy().add(0.5f, 0.5f, 0.5f, 0));
        lightTexture = new TextureRegion();

        lightSystemS = lightSystem;

        world = new RPSWorld();
        player = new Player(world);
        player.pos.set((int) viewport.getWorldWidth() / 2, (int) viewport.getWorldHeight() / 2);
        world.addActor(player);

        world.addSolid(new Wall(world, 0, 0, 1, Constants.GAME_HEIGHT));
        world.addSolid(new Wall(world, 0, Constants.GAME_HEIGHT - 1, Constants.GAME_WIDTH, 1));
        world.addSolid(new Wall(world, 0, 0, Constants.GAME_WIDTH, 1));
        world.addSolid(new Wall(world, Constants.GAME_WIDTH - 1, 0, 1, Constants.GAME_HEIGHT));

        pdr = new PhysicsDebugRenderer();
        temp = new Vector3();
        timeUntilSpawn = 2;

        paperSplash = new ParticleEffect();
        paperSplash.load(Gdx.files.internal("particles/paper_explosion.p"), particleTextures);
        paperSplashPool = new ParticleEffectPool(paperSplash, 20, 100);
        transitioned = true;

        paperAttackSoundS = paperAttackSound;
        rockAttackSoundS = rockAttackSound;
        scissorsAttackSoundS = scissorsAttackSound;

        ascendedSouls = new DelayedRemovalArray<AscendedSoul>();
        ascendedSoulsS = ascendedSouls;

        ascendSoundS = ascendSound;
        playerHitSoundS = playerHitSound;
    }

    private void loadAnimations() {
        scissorsAnimation = Utils.parseAnimation(gameTextures, "enemy/scissors/run", 1, 4, 0.1f, PlayMode.LOOP);
        scissorsAscend = Utils.parseAnimation(gameTextures, "enemy/scissors/ascend", 1, 5, 0.1f, PlayMode.NORMAL);

        rockAnimation = Utils.parseAnimation(gameTextures, "enemy/rock/run", 1, 4,0.25f, PlayMode.LOOP);
        rockAscend = Utils.parseAnimation(gameTextures, "enemy/rock/ascend", 1, 5,0.1f, PlayMode.NORMAL);

        paperAnimation = Utils.parseAnimation(gameTextures, "enemy/paper/run", 1, 4, 0.1f, PlayMode.LOOP);
        paperAttackAnimation = Utils.parseAnimation(gameTextures, "enemy/paper/attack", 1, 7, 0.1f, PlayMode.LOOP);
        paperAscend = Utils.parseAnimation(gameTextures, "enemy/paper/ascend", 1, 5,0.1f, PlayMode.NORMAL);

        stoneBulletTexture = gameTextures.findRegion("enemy/rock/bullet");

        playerIdleAnimation = Utils.parseAnimation(gameTextures, "player/idle", 1, 2, 0.4f, PlayMode.LOOP);
        playerRunAnimation = Utils.parseAnimation(gameTextures, "player/run", 1, 8, 0.05f, PlayMode.LOOP);
        playerAscend = Utils.parseAnimation(gameTextures, "player/ascend", 1, 4, 0.1f, PlayMode.NORMAL);

        ascendedGhostAnimation = Utils.parseAnimation(gameTextures, "ascended_soul", 1, 3, 0.1f, PlayMode.LOOP);

        soulIcon = gameTextures.findRegion("ascended_soul_icon");
    }

    @Override
    public void tick(float delta) {
        if(!player.dead) {
            world.update(delta);
            timeUntilSpawn -= delta;
            if (timeUntilSpawn <= 0) {
                IntVector2 spawn = getSpawnPosition();
                world.addActor(getBestSpawnChoice().create(world, player, spawn.x, spawn.y));
                timeUntilSpawn = 2;
            }
        } else {
            player.update(delta);
        }

        for (PooledEffect runningEffect : runningEffects) {
            runningEffect.update(delta);
            if(runningEffect.isComplete()) {
                runningEffect.free();
                runningEffects.removeValue(runningEffect, true);
            }
        }

        if((player.dead && Gdx.input.isKeyPressed(Keys.R)) && transitioned) {
            ga.transitionTo(new GameScreen(ga), TransitionTemplates.linearFade(1));
            transitioned = false;
        }

        for (AscendedSoul ascendedSoul : ascendedSouls) {
            ascendedSoul.tick(delta);
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
            scissChance -= 200;
        }

        return Utils.getByChance(MoveType.values(), new int[] {rockChance, paperChance, scissChance});
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float zoom = 1;
        if(player.dead) {
            zoom = Interpolation.pow5Out.apply(1, 0.5f,  Math.min(player.timeSinceAscension / 3, 1));
        }
        ((OrthographicCamera)viewport.getCamera()).zoom = zoom;
        Vector3 camPos = viewport.getCamera().position;
        camPos.set(player.pos.x, player.pos.y, 0);

        float minX = viewport.getWorldWidth() / 2 * zoom;
        float maxX = Constants.GAME_WIDTH - viewport.getWorldWidth() / 2 * zoom;
        if(camPos.x < minX) {
            camPos.x = minX;
        }
        else if(camPos.x > maxX) {
            camPos.x = maxX;
        }

        float minY = viewport.getWorldHeight() / 2 * zoom;
        float maxY = Constants.GAME_HEIGHT - viewport.getWorldHeight() / 2 * zoom;
        if(camPos.y < minY) {
            camPos.y = minY;
        }
        else if(camPos.y > maxY) {
            camPos.y = maxY;
        }

        viewport.apply();


        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        batch.draw(grassBackground, 0,0, Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
        for (AscendedSoul ascendedSoul : ascendedSouls) {
            ascendedSoul.render(batch);
        }
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

        lightSystem.render();

        lightTexture.setRegion(lightSystem.getLightMap());
        lightTexture.flip(false, true);

        lightViewport.apply();
        batch.setProjectionMatrix(lightViewport.getCamera().combined);
        batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_COLOR);
        batch.begin();
        batch.draw(lightTexture, 0, 0);
        batch.end();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        renderGUI();
    }

    private void renderGUI() {
        guiViewport.apply();
        batch.setProjectionMatrix(guiViewport.getCamera().combined);

        batch.begin();
        batch.draw(soulIcon, 8, guiViewport.getWorldHeight() - 40, 32, 32);
        Fonts.pixelMix16.draw(batch, "" + soulsAscended, 40, guiViewport.getWorldHeight() - 16);

        if(player.dead) {
            Color c = new Color(1, 1, 1, Math.min(player.timeSinceAscension / 3, 1));
            Fonts.pixelMix32.setColor(c);
            Fonts.pixelMix16.setColor(c);
            Fonts.pixelMix32.draw(batch, "You have ascended!", 0, 100, guiViewport.getWorldWidth(), Align.center, true);
            Fonts.pixelMix16.draw(batch, "Press R To Play Again", 0, 50, guiViewport.getWorldWidth(), Align.center, true);
            Fonts.pixelMix16.setColor(Color.WHITE);
            Fonts.pixelMix32.setColor(Color.WHITE);
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        lightViewport.update(width, height, true);
        guiViewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        paperSplash.dispose();
        for (PooledEffect runningEffect : runningEffects) {
            runningEffect.free();
        }
        runningEffects.clear();
        lightSystem.dispose();
        ascendedSouls.clear();
    }
}