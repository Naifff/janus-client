package com.geekbrains.projectjanus;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

public class Assets {
    private static final Assets ourInstance = new Assets();

    private AssetManager assetManager;
    private TextureAtlas atlas;

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public static Assets getInstance() {
        return ourInstance;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    private Assets() {
        assetManager = new AssetManager();
    }

    public void loadAssets(ScreenManager.ScreenType type) {
        switch (type) {
            case MENU:
                createStandardFont(32);
                createStandardFont(96);
                assetManager.load("MainPack.pack", TextureAtlas.class);
                assetManager.finishLoading();
                atlas = assetManager.get("MainPack.pack", TextureAtlas.class);
                break;
            case GAME:
                assetManager.load("MainPack.pack", TextureAtlas.class);
                assetManager.load("MainTheme.wav", Music.class);
                assetManager.load("explosion.wav", Sound.class);
                createStandardFont(24);
                createStandardFont(32);
                assetManager.finishLoading();
                atlas = assetManager.get("MainPack.pack", TextureAtlas.class);
                break;
        }
    }

    public void createStandardFont(int size) {
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        FreetypeFontLoader.FreeTypeFontLoaderParameter fontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        fontParameter.fontFileName = "zorque.ttf";
        fontParameter.fontParameters.size = size;
        fontParameter.fontParameters.color = Color.WHITE;
        fontParameter.fontParameters.borderWidth = 1;
        fontParameter.fontParameters.borderColor = Color.BLACK;
        fontParameter.fontParameters.shadowOffsetX = 0;
        fontParameter.fontParameters.shadowOffsetY = 0;
        fontParameter.fontParameters.shadowColor = Color.BLACK;
        assetManager.load("zorque" + size + ".ttf", BitmapFont.class, fontParameter);
    }

//    public void makeLinks() {
//        atlas = assetManager.get("mainPack.pack", TextureAtlas.class);
//    }

    public void clear() {
        assetManager.clear();
    }

    public void dispose() {
        assetManager.dispose();
    }
}
