package com.redsponge.nonviolent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.redsponge.nonviolent.intro.IntroScreen;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.screen.splashscreen.SplashScreenRenderer;
import com.redsponge.redengine.transitions.TransitionTemplate;
import com.redsponge.redengine.utils.GameAccessor;

public class MySplashScreenScreen extends AbstractScreen{
/**
 * Splash Screen - Renders the RedSponge splashscreen using a {@link SplashScreenRenderer}
 */

    private SplashScreenRenderer splashScreenRenderer;
    private boolean skipped;
    private TransitionTemplate transition;

    public MySplashScreenScreen(GameAccessor ga, TransitionTemplate transition) {
        super(ga);
        this.transition = transition;
        splashScreenRenderer = new SplashScreenRenderer(batch);
    }

    @Override
    public void show() {
        splashScreenRenderer.begin();
        skipped = false;
    }

    @Override
    public void tick(float delta) {
        splashScreenRenderer.tick(delta);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.9f, 0.7f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        splashScreenRenderer.render();

        if((splashScreenRenderer.isComplete() || skipped) && !transitioning) {
            ga.transitionTo(new IntroScreen(ga), transition);
        }
    }



    @Override
    public void resize(int width, int height) {
        splashScreenRenderer.resize(width, height);
    }

    @Override
    public void dispose() {
        splashScreenRenderer.dispose();
    }
}
