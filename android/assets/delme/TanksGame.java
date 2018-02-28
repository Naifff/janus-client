package com.tanks.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class TanksGame extends Game {
    private SpriteBatch batch;

    // Домашнее задание
    // 1. Разобраться с кодом +
    // 2. Реализовать систему оружий
    // ------------------------------------------------------------------
    // Планы на будущее:
    // Система частиц +
    // Побольше интерактинвых элементов
    // Разное поведение пулям +
    // Камера и большая карта
    // Генерация ландшафта
    // Добить интерфейс
    // Музыка/звуки
    // Оптимизация (рендер в буфер)
    // ВЕТЕР
    // Команды танков
    // Перенести в Android Studio +
    // 2. Вылетающие надписи, сколько урона получил с затуханием
    // 3. При получении урона танк должен немного отскакивать в сторону
    // *4. Продумайте модификаторы пуль (как им сделать разное поведение)

    @Override
    public void create() {
        batch = new SpriteBatch();
        ScreenManager.getInstance().init(this, batch);
        ScreenManager.getInstance().switchScreen(ScreenManager.ScreenType.MENU);
    }


    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        getScreen().render(dt);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
