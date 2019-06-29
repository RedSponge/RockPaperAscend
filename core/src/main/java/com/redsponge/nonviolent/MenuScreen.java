package com.redsponge.nonviolent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.redsponge.nonviolent.game.GameScreen;
import com.redsponge.redengine.assets.Asset;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.transitions.TransitionTemplates;
import com.redsponge.redengine.utils.GameAccessor;

public class MenuScreen extends AbstractScreen {

    private FitViewport viewport;
    private Stage stage;
    private Skin skin;

    private Runnable nextMenu;
    private int toRemove;

    @Asset(path = "ascend_text.png")
    private Texture ascend;

    @Asset(path = "menu_background.png")
    private Texture background;

    private BitmapFont titleFont;

    public MenuScreen(GameAccessor ga) {
        super(ga);
    }

    @Override
    public void show() {

        titleFont = new BitmapFont(Gdx.files.internal("fonts/title_font.fnt"));

        skin = new Skin(Gdx.files.internal("skins/editor/editor_skin.json"));

        viewport = new FitViewport(Constants.GUI_WIDTH, Constants.GUI_HEIGHT);
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

        buildMain();

    }

    private void hideScene() {
        toRemove = stage.getActors().size;
        for (Actor actor : stage.getActors()) {
            Action action;
            if(actor instanceof Group) {
                action = Actions.scaleTo(0, 0, 1, Interpolation.swingIn);
            } else {
                action = Actions.moveTo(stage.getWidth() / 2 - actor.getWidth() / 2, stage.getHeight() + actor.getHeight() + 10, 1, Interpolation.swingIn);
            }
            actor.addAction(Actions.sequence(action, Actions.run(() -> toRemove--), Actions.removeActor()));
        }
    }

    private void buildCredits() {
        String[] lbls = {
                "Art - RedSponge",
                "Music & Sounds - RedSponge",
                "Programming - RedSponge",
                "Procrastination - RedSponge",
                "Tools Used: Skin Composer, Audacity, Bosca Ceoil"
        };
        Actor[] labels = new Actor[lbls.length + 2];
        for(int i = 0; i < lbls.length; i++) {
            Label lbl = new Label(lbls[i], skin);
            labels[i] = lbl;
            lbl.setAlignment(Align.center);
        }

        TextButton back = new TextButton("Back", skin);

        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hideScene();
                nextMenu = MenuScreen.this::buildMain;
            }
        });

        labels[labels.length - 2] = null;
        labels[labels.length - 1] = back;
        slideIn(250, 40, labels);
    }

    private void buildMain() {

        TextButton start = new TextButton("Start Game", skin);
        TextButton credits = new TextButton("Credits", skin);
        TextButton exit = new TextButton("Exit", skin);

        start.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ga.transitionTo(new GameScreen(ga), TransitionTemplates.sineSlide(1));
            }
        });

        credits.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hideScene();
                nextMenu = MenuScreen.this::buildCredits;
            }
        });

        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        slideIn(200, 60, start, credits, exit);
    }

    private void slideIn(int maxY, int space, Actor... buttons) {
        for (int i = 0; i < buttons.length; i++) {
            if(buttons[i] == null) continue;
            float x = stage.getWidth() / 2 - buttons[i].getWidth() / 2;
            float y = maxY - space * i;
            if(buttons[i] instanceof Group) {
                ((Group) buttons[i]).setTransform(true);
                buttons[i].setScale(0);
                buttons[i].setOrigin(Align.center);
                buttons[i].setPosition(x, y);
                buttons[i].addAction(Actions.delay(i * 0.5f, Actions.scaleTo(1, 1, 1, Interpolation.swingOut)));
            } else {
                buttons[i].setPosition( x, 0 - buttons[i].getHeight() - 10);
                buttons[i].addAction(Actions.delay(i * 0.5f, Actions.moveTo(x, y, 2, Interpolation.exp5Out)));
            }
            stage.addActor(buttons[i]);
        }
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void tick(float delta) {
        stage.act(delta);
        if(toRemove == 0 && nextMenu != null) {
            nextMenu.run();
            nextMenu = null;
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(background, 0, 0);
        float w = ascend.getWidth() * 3;
        float h = ascend.getHeight() * 3;
        batch.draw(ascend, viewport.getWorldWidth() / 2 - w / 2, 250, w, h);
        batch.end();

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        skin.dispose();
        titleFont.dispose();
    }
}
