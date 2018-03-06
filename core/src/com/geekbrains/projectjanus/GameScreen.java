package com.geekbrains.projectjanus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.List;


public class GameScreen implements Screen {
    protected int world = 2000;
    private FoodEmitter foodEmitter;
    private SpriteBatch batch;
    private List<Player> players;

    public List<Player> getPlayers() {
        return players;
    }

    private BitmapFont font24;
    private Stage stage;
    private Skin skin;
    private Group playerJoystick;
    private BitmapFont font32;
    private Music music;
    private Sound soundExplosion;
    private ShapeRenderer shapeRenderer;
    private boolean goLeft = false;
    private boolean goRight = false;
    private boolean goUp = false;
    private boolean goDown = false;
    private Texture border;
    private static final int BOTS_COUNT = 1;
    protected Camera camera;

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;
    }

    private boolean gameOver;
    private boolean paused;

    public void update(float dt) {
        if (!gameOver && !paused) {

            for (int i = 0; i < players.size(); i++) {
                players.get(i).update(dt);
            }
            if (goDown) {

                goDown = players.get(0).goDown(dt);
            }
            if (goLeft) {
                goLeft = players.get(0).goLeft(dt);
            }
            if (goRight) {
                goRight = players.get(0).goRight(dt);
            }
            if (goUp) {
                goUp = players.get(0).goUp(dt);
            }
            playerJoystick.setVisible(true);
            if (foodEmitter.getActiveList().size() < 200) {
                for (int i = 0; i < 50; i++) {
                    foodEmitter.setup(MathUtils.random(-world, world), MathUtils.random(-world, world));
                }
            }
            foodEmitter.update(dt);
            foodEmitter.checkPool();
            players.get(0).playerCamera(camera);
        }

        camera.update();
        stage.act(dt);
    }


    public void restart() {
        gameOver = false;
        paused = false;
        players = new ArrayList<Player>();

        foodEmitter = new FoodEmitter(this, 50);
        for (int i = 0; i < 50; i++) {
            foodEmitter.setup(MathUtils.random(-world, world), MathUtils.random(-world, world));
        }
//        players.add(new Player(this, new Vector2(MathUtils.random(0, 1280), MathUtils.random(0, 1024)), false));
//        for (int i = 0; i < BOTS_COUNT; i++) {
//            players.add(new Player(this, new Vector2(MathUtils.random(0, 1280), MathUtils.random(0, 1024)), true));
//        }

        players.add(new Player(this, new Vector2(0, 0), false));
        for (int i = 0; i < BOTS_COUNT; i++) {
            players.add(new Player(this, new Vector2(400, 400), true));
        }

    }

    @Override
    public void show() {
        font24 = Assets.getInstance().getAssetManager().get("zorque24.ttf", BitmapFont.class);
        font32 = Assets.getInstance().getAssetManager().get("zorque32.ttf", BitmapFont.class);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        border = new Texture("bc.png");
        camera = new OrthographicCamera(0, 0);
        restart();
        createGUI();
        music = Assets.getInstance().getAssetManager().get("MainTheme.wav", Music.class);
        music.setVolume(0.2f);
        music.setLooping(true);
//         music.play();
        soundExplosion = Assets.getInstance().getAssetManager().get("explosion.wav", Sound.class);
        InputProcessor ip = new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(int amount) {
                return false;
            }
        };
        InputMultiplexer im = new InputMultiplexer(stage, ip);
        Gdx.input.setInputProcessor(im);
    }

    @Override
    public void render(float dt) {
        update(dt);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        foodEmitter.render(batch);
        batch.draw(border, -world, -world, 2, 4000);
        batch.draw(border, -world, world, 4000, 2);
        batch.draw(border, -world, -world, 4000, 2);
        batch.draw(border, world, -world, 2, 4000);
        for (int i = 0; i < players.size(); i++) {
            players.get(i).render(batch);
        }
        batch.end();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        ScreenManager.getInstance().onResize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void createGUI() {
        stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        skin = new Skin(Assets.getInstance().getAtlas());
        playerJoystick = new Group();
        Gdx.input.setInputProcessor(stage);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("btn2");

        textButtonStyle.font = font32;
        skin.add("tbs", textButtonStyle);

        TextButton btnLeft = new TextButton("LEFT", skin, "tbs");
        TextButton btnRight = new TextButton("RIGHT", skin, "tbs");
        TextButton btnUp = new TextButton("UP", skin, "tbs");
        TextButton btnDown = new TextButton("DOWN", skin, "tbs");
        TextButton btnFire = new TextButton("FIRE", skin, "tbs");
//        TextButton btnExit = new TextButton("EXIT", skin, "tbs");
        TextButton btnRestart = new TextButton("RESTART", skin, "tbs");
//        TextButton btnPause = new TextButton("II", skin, "tbs");

        btnLeft.setPosition(20, 100);
        btnRight.setPosition(260, 100);
        btnUp.setPosition(140, 180);
        btnDown.setPosition(140, 20);
        btnFire.setPosition(1060, 100);
//        btnExit.setPosition(1060, 600);
        btnRestart.setPosition(880, 600);
//        btnPause.setPosition(700, 600);

        playerJoystick.addActor(btnLeft);
        playerJoystick.addActor(btnRight);
        playerJoystick.addActor(btnUp);
        playerJoystick.addActor(btnDown);
        playerJoystick.addActor(btnFire);

//        stage.addActor(btnExit);
        stage.addActor(btnRestart);
//        stage.addActor(btnPause);

        stage.addActor(playerJoystick);

//        btnExit.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                ScreenManager.getInstance().switchScreen(ScreenManager.ScreenType.MENU);
//            }
//        });
//
//        btnPause.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                paused = !paused;
//            }
//        });
//
        btnRestart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                restart();
            }
        });

        btnLeft.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("LeftBtn pressed");
                goLeft = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("LeftBtn release");
                goLeft = false;

            }
        });
        btnRight.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("RightBtn pressed");
                goRight = true;
                return true;

            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("RightBtn release");
                goRight = false;
            }
        });
        btnUp.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("UpBtn pressed");
                goUp = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("UpBtn release");
                goUp = false;
            }
        });
        btnDown.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("DownBtn pressed");
                goDown = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("DownBtn released");
                goDown = false;
            }
        });
        btnFire.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("FireBtn pressed");
                for(int i=0;i<200;i++){
                players.get(0).up();}
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("FireBtn released");
            }
        });


    }


}
