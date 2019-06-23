package com.redsponge.nonviolent;

import com.redsponge.nonviolent.game.GameScreen;
import com.redsponge.redengine.EngineGame;
import com.redsponge.redengine.screen.DefaultScreen;
import com.redsponge.redengine.screen.SplashScreenScreen;
import com.redsponge.redengine.transitions.TransitionTemplates;

import java.util.function.BiConsumer;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class NonViolentBattle extends EngineGame {

    public static NonViolentBattle instance;

    public NonViolentBattle(boolean desktop, BiConsumer<Integer, Integer> desktopMoveAction) {
        super(desktop, desktopMoveAction);
    }

    @Override
    public void init() {
//        setScreen(new SplashScreenScreen(ga, new GameScreen(ga), TransitionTemplates.sineSlide(1)));
        instance = this;
        setScreen(new GameScreen(ga));
    }
}