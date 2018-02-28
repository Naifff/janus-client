package com.geekbrains.projectjanus;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;



public class ScreenManager {
    public enum ScreenType {
        MENU, GAME;
    }

    private pJGame janusGame;
    private Viewport viewport;
    private GameScreen gameScreen;
    private MenuScreen menuScreen;
//    private LoadingScreen loadingScreen;
//    private Screen targetScreen;

    public static final int VIEW_WIDTH = 1280;
    public static final int VIEW_HEIGHT = 720;

    public pJGame getJanusGame() {
        return janusGame;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void init(pJGame janusGame, SpriteBatch batch) {
        this.janusGame = janusGame;
        this.gameScreen = new GameScreen(batch);
        this.menuScreen = new MenuScreen(batch);
//        this.loadingScreen = new LoadingScreen(batch);
        this.viewport = new FitViewport(VIEW_WIDTH, VIEW_HEIGHT);
        this.viewport.apply();
    }

    private static final ScreenManager ourInstance = new ScreenManager();

    public static ScreenManager getInstance() {
        return ourInstance;
    }

    private ScreenManager() {
    }

    public void onResize(int width, int height) {
        viewport.update(width, height, true);
        viewport.apply();
    }

    public void switchScreen(ScreenType type) {
        Screen currentScreen = janusGame.getScreen();
        Assets.getInstance().clear();
        if (currentScreen != null) {
            currentScreen.dispose();
        }
//        rpgGame.setScreen(loadingScreen);
        switch (type) {
            case MENU:
                currentScreen = menuScreen;
                Assets.getInstance().loadAssets(ScreenType.MENU);
                break;
            case GAME:
                currentScreen = gameScreen;
                Assets.getInstance().loadAssets(ScreenType.GAME);
                break;
        }
        janusGame.setScreen(currentScreen);
    }

//    public void goToTarget() {
//        rpgGame.setScreen(targetScreen);
//        targetScreen = null;
//    }
//
//    public void dispose() {
//        Assets.getInstance().dispose();
//        rpgGame.getScreen().dispose();
//    }

}
