package com.geekbrains.projectjanus;

import com.badlogic.gdx.math.Vector2;

public class Food implements Poolable {

    private Vector2 position;
    private boolean active;

    public Vector2 getPosition() {
        return position;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public Food() {
        position = new Vector2(0, 0);
        active = false;
    }

    public void deactivate() {
        active = false;
    }

    public void activate(float x, float y) {
        this.position.set(x, y);
        this.active = true;
    }
}
