package com.tanks.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameScreen implements Screen {
    private SpriteBatch batch;
    private TextureRegion textureBackground;
    private Map map;
    private BulletEmitter bulletEmitter;
    private List<Tank> players;
    private int currentPlayerIndex;
    private BitmapFont font24;

    private Stage stage;
    private Skin skin;
    private Group playerJoystick;
    private BitmapFont font32;
    private Music music;
    private Sound soundExplosion;
    private ShapeRenderer shapeRenderer;

    private ParticleEmitter particleEmitter;

    private InfoSystem infoSystem;

    private static final int BOTS_COUNT = 14;

    private Camera camera;

    public InfoSystem getInfoSystem() {
        return infoSystem;
    }

    public ParticleEmitter getParticleEmitter() {
        return particleEmitter;
    }

    public List<Tank> getPlayers() {
        return players;
    }

    public BulletEmitter getBulletEmitter() {
        return bulletEmitter;
    }

    public Map getMap() {
        return map;
    }

    public static final float GLOBAL_GRAVITY = 250.0f;

    public boolean isMyTurn(Tank tank) {
        return tank == players.get(currentPlayerIndex);
    }

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;
    }

    public Tank getCurrentTank() {
        return players.get(currentPlayerIndex);
    }

    private boolean gameOver;
    private boolean paused;
    private float timeToNext;

    public void checkNextTurn(float dt) {
        if (players.size() == 1) {
            gameOver = true;
            return;
        }
        if (!players.get(currentPlayerIndex).makeTurn) {
            return;
        }
        if (!bulletEmitter.empty()) {
            return;
        }
        if (timeToNext < 1.0f) {
            timeToNext += dt;
            return;
        }
        timeToNext = 0.0f;
        do {
            currentPlayerIndex++;
            if (currentPlayerIndex >= players.size()) {
                currentPlayerIndex = 0;
            }
        } while (!players.get(currentPlayerIndex).isAlive());
        players.get(currentPlayerIndex).takeTurn();
    }

    Vector2 v2tmp = new Vector2(0, 0);

    public void update(float dt) {
        if (!gameOver && !paused) {
            playerJoystick.setVisible(getCurrentTank() instanceof PlayerTank);
            if (!getCurrentTank().isMakeTurn()) {
                camera.position.set(getCurrentTank().position, 0);
            }
            if (!bulletEmitter.empty()) {
                camera.position.set(bulletEmitter.getActiveList().get(0).getPosition(), 0);
            }
            if (camera.position.x < 640) {
                camera.position.x = 640;
            }
            if (camera.position.x > Map.WORLD_WIDTH - 640) {
                camera.position.x = Map.WORLD_WIDTH - 640;
            }
            if (camera.position.y < 360) {
                camera.position.y = 360;
            }
            camera.update();
            stage.act(dt);
            map.update(dt);
            for (int i = 0; i < players.size(); i++) {
                players.get(i).update(dt);
            }
            bulletEmitter.update(dt);
            bulletEmitter.checkPool();
            checkNextTurn(dt);

            particleEmitter.update(dt);
            particleEmitter.checkPool();
            infoSystem.update(dt);
        }
    }

    public boolean traceCollision(Tank aim, Bullet bullet, float dt) {
        if (bullet.isActive()) {
            List<Tank> result = bulletEmitter.updateBullet(bullet, dt, true);
            if (result.contains(aim)) {
                return true;
            }
        }
        return false;
    }

    public void destroyPlayer(Tank tank) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i) == tank) {
                players.remove(i);
                soundExplosion.play();
                if (i < currentPlayerIndex) {
                    currentPlayerIndex--;
                }
                particleEmitter.makeExplosion(tank.getHitArea().x, tank.getPosition().y);
            }
        }
    }

    public void restart() {
        map = new Map();
        players = new ArrayList<Tank>();
        gameOver = false;
        paused = false;
        // players.add(new PlayerTank(this, new Vector2(120, map.getHeightInX(400))));
        for (int i = 0; i < BOTS_COUNT; i++) {
            Tank tank = new AiTank(this, new Vector2(0, 0));
            players.add(tank);
            boolean collision = false;
            do {
                collision = false;
                int posX = MathUtils.random(0, Map.WORLD_WIDTH - 120);
                tank.getPosition().set(posX, map.getHeightInX(posX));
                tank.getHitArea().setPosition(posX, map.getHeightInX(posX));
                for (int j = 0; j < players.size() - 1; j++) {
                    for (int k = j + 1; k < players.size(); k++) {
                        if (players.get(j).getHitArea().overlaps(players.get(k).getHitArea())) {
                            collision = true;
                        }
                    }
                }
            } while (collision);
        }
        currentPlayerIndex = 0;
        players.get(currentPlayerIndex).takeTurn();
        bulletEmitter = new BulletEmitter(this, 50);
        infoSystem = new InfoSystem();
        particleEmitter = new ParticleEmitter();
    }

    @Override
    public void show() {
        font24 = Assets.getInstance().getAssetManager().get("zorque24.ttf", BitmapFont.class);
        font32 = Assets.getInstance().getAssetManager().get("zorque32.ttf", BitmapFont.class);
        textureBackground = Assets.getInstance().getAtlas().findRegion("background");
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        restart();
        createGUI();
        music = Assets.getInstance().getAssetManager().get("MainTheme.wav", Music.class);
        music.setVolume(0.2f);
        music.setLooping(true);
        // music.play();
        camera = new OrthographicCamera(1280, 720);
        soundExplosion = Assets.getInstance().getAssetManager().get("explosion.wav", Sound.class);
        InputProcessor ip = new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                if (getCurrentTank() instanceof PlayerTank && keycode == Input.Keys.SPACE) {
                    ((PlayerTank) getCurrentTank()).setCurrentAction(PlayerTank.Action.FIRE);
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (getCurrentTank() instanceof PlayerTank && keycode == Input.Keys.SPACE) {
                    ((PlayerTank) getCurrentTank()).setCurrentAction(PlayerTank.Action.IDLE);
                    return true;
                }
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
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(textureBackground, 0, 0);
        batch.end();
        map.getPolyBatch().setProjectionMatrix(camera.combined);
        map.getPolyBatch().begin();
        map.getPolySprite().draw(map.getPolyBatch());
        map.getPolyBatch().end();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        map.render(batch);
        for (int i = 0; i < players.size(); i++) {
            players.get(i).render(batch);
        }
        // bulletEmitter.render(batch);
        for (int i = 0; i < players.size(); i++) {
            players.get(i).renderHUD(batch, font24);
        }
        particleEmitter.render(batch);
        infoSystem.render(batch, font24);
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
        TextButton btnExit = new TextButton("EXIT", skin, "tbs");
        TextButton btnRestart = new TextButton("RESTART", skin, "tbs");
        TextButton btnPause = new TextButton("II", skin, "tbs");

        btnLeft.setPosition(20, 100);
        btnRight.setPosition(260, 100);
        btnUp.setPosition(140, 180);
        btnDown.setPosition(140, 20);
        btnFire.setPosition(1060, 100);
        btnExit.setPosition(1060, 600);
        btnRestart.setPosition(880, 600);
        btnPause.setPosition(700, 600);

        playerJoystick.addActor(btnLeft);
        playerJoystick.addActor(btnRight);
        playerJoystick.addActor(btnUp);
        playerJoystick.addActor(btnDown);
        playerJoystick.addActor(btnFire);

        stage.addActor(btnExit);
        stage.addActor(btnRestart);
        stage.addActor(btnPause);

        stage.addActor(playerJoystick);

        btnExit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().switchScreen(ScreenManager.ScreenType.MENU);
            }
        });

        btnPause.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                paused = !paused;
            }
        });

        btnRestart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                restart();
            }
        });

        btnLeft.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (getCurrentTank() instanceof PlayerTank) {
                    ((PlayerTank) getCurrentTank()).setCurrentAction(PlayerTank.Action.MOVE_LEFT);
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (getCurrentTank() instanceof PlayerTank) {
                    ((PlayerTank) getCurrentTank()).setCurrentAction(PlayerTank.Action.IDLE);
                }
            }
        });
        btnRight.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (getCurrentTank() instanceof PlayerTank) {
                    ((PlayerTank) getCurrentTank()).setCurrentAction(PlayerTank.Action.MOVE_RIGHT);
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (getCurrentTank() instanceof PlayerTank) {
                    ((PlayerTank) getCurrentTank()).setCurrentAction(PlayerTank.Action.IDLE);
                }
            }
        });
        btnLeft.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (getCurrentTank() instanceof PlayerTank) {
                    ((PlayerTank) getCurrentTank()).setCurrentAction(PlayerTank.Action.MOVE_LEFT);
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (getCurrentTank() instanceof PlayerTank) {
                    ((PlayerTank) getCurrentTank()).setCurrentAction(PlayerTank.Action.IDLE);
                }
            }
        });
        btnUp.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (getCurrentTank() instanceof PlayerTank) {
                    ((PlayerTank) getCurrentTank()).setCurrentAction(PlayerTank.Action.TURRET_UP);
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (getCurrentTank() instanceof PlayerTank) {
                    ((PlayerTank) getCurrentTank()).setCurrentAction(PlayerTank.Action.IDLE);
                }
            }
        });
        btnDown.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (getCurrentTank() instanceof PlayerTank) {
                    ((PlayerTank) getCurrentTank()).setCurrentAction(PlayerTank.Action.TURRET_DOWN);
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (getCurrentTank() instanceof PlayerTank) {
                    ((PlayerTank) getCurrentTank()).setCurrentAction(PlayerTank.Action.IDLE);
                }
            }
        });
        btnFire.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (getCurrentTank() instanceof PlayerTank) {
                    ((PlayerTank) getCurrentTank()).setCurrentAction(PlayerTank.Action.FIRE);
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (getCurrentTank() instanceof PlayerTank) {
                    ((PlayerTank) getCurrentTank()).setCurrentAction(PlayerTank.Action.IDLE);
                }
            }
        });
    }
}
