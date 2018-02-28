package com.tanks.game;

import com.badlogic.gdx.math.Vector2;


import java.io.BufferedOutputStream;

public class Bullet implements Poolable {
    private Tank owner;
    private BulletEmitter.BulletType type;
    private Vector2 position;
    private Vector2 velocity;
    private boolean active;
    private float time;

    public Tank getOwner() {
        return owner;
    }

    public Vector2 getPosition() {
        return position;
    }

    public BulletEmitter.BulletType getType() {
        return type;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public boolean isArmed() {
        return time > 0.2f;
    }

    public Bullet() {
        position = new Vector2(0, 0);
        velocity = new Vector2(0, 0);
        active = false;
        time = 0.0f;
        type = BulletEmitter.BulletType.LIGHT_AMMO;
    }

    public void deactivate() {
        active = false;
    }

    public void activate(Tank owner, BulletEmitter.BulletType type, float x, float y, float vx, float vy) {
        this.owner = owner;
        this.type = type;
        this.position.set(x, y);
        this.velocity.set(vx, vy);
        this.active = true;
        this.time = 0.0f;
    }

    public void addTime(float dt) {
        time += dt;
        if (time > type.getMaxTime()) {
            deactivate();
        }
    }
}
