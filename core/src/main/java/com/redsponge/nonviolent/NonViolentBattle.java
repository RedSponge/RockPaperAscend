package com.redsponge.nonviolent;

import com.redsponge.nonviolent.game.GameScreen;
import com.redsponge.redengine.EngineGame;
import com.redsponge.redengine.desktop.DesktopUtil;
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
        instance = this;
//        DesktopUtil.toggleFullscreen(Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
        setScreen(new GameScreen(ga));
    }
}