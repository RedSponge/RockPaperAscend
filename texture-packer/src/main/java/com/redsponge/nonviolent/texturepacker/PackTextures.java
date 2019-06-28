package com.redsponge.nonviolent.texturepacker;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class PackTextures {

    public static void main(String[] args) {
        TexturePacker.processIfModified("raw_textures", "../assets/textures/", "textures");
        TexturePacker.processIfModified("particles", "../assets/particles/", "particles");
    }

}
