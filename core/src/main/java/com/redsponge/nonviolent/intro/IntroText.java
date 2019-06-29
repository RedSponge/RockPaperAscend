package com.redsponge.nonviolent.intro;

import com.badlogic.gdx.graphics.Texture;

public class IntroText {

    public static final IntroText[] INTROES = {
            new IntroText("", "{SLOW}Once upon a time, on a planet named Handia, there were three species"),
            new IntroText("", "{SLOW}The Rocks. {WAIT}The Papers. {WAIT}And The Scissors."),
            new IntroText("", "{SLOW}They lived in peace for thousands of years{WAIT}.{WAIT}.{WAIT}.{WAIT}"),
            new IntroText("", "{SLOWER}Until one day, A species of aliens, who took upon them the task of spreading the knowledge of ascension, landed on Handia"),
            new IntroText("", "{SLOW}Ascension is the process of freeing one's soul from their body, and freeing it"),
            new IntroText("", "{SLOW}The way to ascend, the aliens said, is to come in contact with your perfect match."),
            new IntroText("", "{SLOW}Since the citizens of Handia, the Hands, didn't know their matches, the aliens sent someone to direct them."),
            new IntroText("", "{SLOW}And this is where you, come into play. You shall direct the Hands one into another, and help them ascend."),
            new IntroText("", "{SLOW}Remember though, you MUST not ascend with them, as we have more worlds to help after that"),
    };

    private Texture texture;
    private String text;

    public IntroText(String texture, String text) {
//        this.texture = new Texture(texture);
        this.text = text;
    }

    public Texture getTexture() {
        return texture;
    }

    public String getText() {
        return text;
    }


}
