package com.tanks.game;

import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ShortArray;

public class Map {
    private TextureRegion[] textureClouds;
    private Vector3[] clouds;
    private float windPower;

    private PolygonSpriteBatch polyBatch;
    private Texture textureSolid;
    private PolygonSprite polySprite;

    private static final int MAP_BLOCKS_WIDTH = 90;
    private static final int BLOCK_LENGHT = 30;
    private float[] vertices;
    private int[] vHeightMap = new int[MAP_BLOCKS_WIDTH];

    public static final int WORLD_WIDTH = (MAP_BLOCKS_WIDTH - 2) * BLOCK_LENGHT;
    public static final int WORLD_HEIGHT = 1440;


    public PolygonSpriteBatch getPolyBatch() {
        return polyBatch;
    }

    public PolygonSprite getPolySprite() {
        return polySprite;
    }

    public Map() {
        this.textureClouds = new TextureRegion[3];
        TextureRegion[][] tmp = new TextureRegion(Assets.getInstance().getAtlas().findRegion("clouds")).split(256, 128);
        for (int i = 0; i < 3; i++) {
            textureClouds[i] = tmp[i][0];
        }
        this.generate();
        this.clouds = new Vector3[14];
        for (int i = 0; i < clouds.length; i++) {
            clouds[i] = new Vector3(MathUtils.random(-640, WORLD_WIDTH + 640), MathUtils.random(640, 800), MathUtils.random(0, 2));
        }
        this.windPower = 20.0f;

        polyBatch = new PolygonSpriteBatch(); // To assign at the beginning

        // polyBatch.setProjectionMatrix(camera.combined);

        Pixmap pix = new Pixmap(2, 400, Pixmap.Format.RGBA8888);
        for (int i = 0; i < 400; i += 2) {
            pix.setColor(0.0f, i / 400.0f, 0.0f, 1.0f);
            pix.fillRectangle(0, i * 2, 2, 4);
        }

        textureSolid = new Texture(pix);
        TextureRegion textureRegion = new TextureRegion(textureSolid);
        textureRegion.flip(false, true);

        // 1   3   5   7
        // **  **  **  *
        // * * * * * * *
        // *  **  **  **
        // 0   2   4   6 ... to be continued...

        vertices = new float[MAP_BLOCKS_WIDTH * 4];

        for (int i = 0; i < MAP_BLOCKS_WIDTH; i++) {
            vertices[i * 4 + 0] = i * BLOCK_LENGHT;
            vertices[i * 4 + 1] = 0;
            vertices[i * 4 + 2] = i * BLOCK_LENGHT;
            vertices[i * 4 + 3] = vHeightMap[i];
        }

        short[] indices = new short[MAP_BLOCKS_WIDTH * 6];

        for (int i = 0; i < MAP_BLOCKS_WIDTH - 1; i++) {
            indices[i * 6 + 0] = (short) (i * 2 + 0);
            indices[i * 6 + 1] = (short) (i * 2 + 1);
            indices[i * 6 + 2] = (short) (i * 2 + 2);
            indices[i * 6 + 3] = (short) (i * 2 + 1);
            indices[i * 6 + 4] = (short) (i * 2 + 3);
            indices[i * 6 + 5] = (short) (i * 2 + 2);
        }

        PolygonRegion polyReg = new PolygonRegion(textureRegion, vertices, indices);

        polySprite = new PolygonSprite(polyReg);
    }

    public void generate() {
        vHeightMap[0] = MathUtils.random(150, 600);
        vHeightMap[vHeightMap.length - 1] = MathUtils.random(150, 600);

        split(vHeightMap, 0, vHeightMap.length - 1, 120, 20);
        for (int i = 0; i < 2; i++) {
            slideWindow(vHeightMap, 3);
        }
    }

    public void split(int[] arr, int x1, int x2, int iter, int iterDecrease) {
        int center = (x1 + x2) / 2;
        if (iter < 0) {
            iter = 0;
        }
        arr[center] = (arr[x1] + arr[x2]) / 2 + MathUtils.random(-iter, iter);
        if (x2 - x1 > 2) {
            split(arr, x1, center, iter - iterDecrease, iterDecrease);
            split(arr, center, x2, iter - iterDecrease, iterDecrease);
        }
    }

    public void slideWindow(int[] arr, int halfWin) {
        for (int i = 0; i < arr.length; i++) {
            int x1 = i - halfWin;
            int x2 = i + halfWin;
            if (x1 < 0) {
                x1 = 0;
            }
            if (x2 > arr.length - 1) {
                x2 = arr.length - 1;
            }
            int avg = 0;
            for (int j = x1; j <= x2; j++) {
                avg += arr[j];
            }
            avg /= (x2 - x1 + 1);
            arr[i] = avg;
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < clouds.length; i++) {
            batch.draw(textureClouds[(int) clouds[i].z], clouds[i].x, clouds[i].y);
        }
    }

    public boolean isGround(float x, float y) {
        return getHeightInX((int) x) > y;
    }

    public void clearGround(float x, float y, int r) {
        for (float i = 0; i < 6.28f; i += 0.2f) {
            float dx = x + r * (float) Math.cos(i);
            float dy = y + r * (float) Math.sin(i);

            if (getHeightInX(dx) > dy) {
                changeHeightInX(dx, dy);
            }
        }
    }

    public int getHeightInX(float x) {
        int cell = (int) (x / BLOCK_LENGHT);
        return (int) (vHeightMap[cell] + (vHeightMap[cell + 1] - vHeightMap[cell]) * (float) (x - cell * BLOCK_LENGHT) / BLOCK_LENGHT);
    }

    public void changeHeightInX(float x, float newY) {
        int cell = (int) (x / BLOCK_LENGHT);
        polySprite.getVertices()[cell * 10 + 6] = newY;
        polySprite.getVertices()[(cell + 1) * 10 + 6] = newY;
        vHeightMap[cell] = (int)newY;
        vHeightMap[cell + 1] = (int)newY;
    }

    float time = 0.0f;

    public void update(float dt) {
        time += dt;

        for (int i = 0; i < clouds.length; i++) {
            clouds[i].x += windPower * dt;
            if (clouds[i].x < -640) {
                clouds[i].set(MathUtils.random(WORLD_WIDTH, WORLD_WIDTH + 640), MathUtils.random(640, 800), MathUtils.random(0, 2));
            }
            if (clouds[i].x > WORLD_WIDTH + 640) {
                clouds[i].set(MathUtils.random(-640, -256), MathUtils.random(640, 800), MathUtils.random(0, 2));
            }
        }
    }
}
