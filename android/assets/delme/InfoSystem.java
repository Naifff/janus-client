package com.tanks.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class InfoSystem {
    private FlyingText[] msgs;
    private int msgCount;

    public InfoSystem() {
        this.msgs = new FlyingText[20];
        for (int i = 0; i < msgs.length; i++) {
            this.msgs[i] = new FlyingText();
        }
    }

    public void addMessage(String text, float x, float y, FlyingText.Colors color) {
        for (int i = 0; i < msgs.length; i++) {
            if (!msgs[i].isActive()) {
                msgs[i].setup(text, x, y - msgCount * 20, color);
                break;
            }
        }
        msgCount++;
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        for (int i = 0; i < msgs.length; i++) {
            if (msgs[i].isActive()) {
                msgs[i].render(batch, font);
            }
        }
    }

    public void update(float dt) {
        msgCount = 0;
        for (int i = 0; i < msgs.length; i++) {
            if (msgs[i].isActive()) {
                msgs[i].update(dt);
            }
        }
    }
}
