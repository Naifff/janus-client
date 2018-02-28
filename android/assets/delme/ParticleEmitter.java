package com.tanks.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by FlameXander on 12.02.2018.
 */

public class ParticleEmitter extends ObjectPool<Particle> {
    public enum BulletEffectType {
        NONE, FIRE, SMOKE, LASER, TRACER, BOMB
    }

    private TextureRegion particleTexture;

    public ParticleEmitter() {
        this.particleTexture = Assets.getInstance().getAtlas().findRegion("star16");
    }

    @Override
    protected Particle newObject() {
        return new Particle();
    }

    public void setup(float x, float y, float vx, float vy, float duration, float size1, float size2, float r1, float g1, float b1, float a1, float r2, float g2, float b2, float a2) {
        Particle item = getActiveElement();
        item.init(x, y, vx, vy, duration, size1, size2, r1, g1, b1, a1, r2, g2, b2, a2);
    }

    public void makeBulletEffect(BulletEffectType type, float x, float y) {
        switch (type) {
            case NONE:
                return;
            case FIRE:
                for (int i = 0; i < 3; i++) {
                    setup(x, y, MathUtils.random(-15, 15), MathUtils.random(-15, 15), 0.3f, 2.0f, 0.6f, 1, 0.2f, 0, 1, 1, 1f, 0, 0.5f);
                }
                break;
            case LASER:
                for (int i = 0; i < 1; i++) {
                    setup(x, y, 0, 0, 1.0f, 0.5f, 0.4f, 1, 0f, 0, 1, 0.5f, 0f, 0, 1f);
                }
                break;
            case BOMB:
                for (int i = 0; i < 1; i++) {
                    setup(x, y, 0, 0, 0.1f, 1.1f, 2f, 0.6f, 0.1f, 0, 0.5f, 0.4f, 0f, 0, 0.3f);
                }
                break;
            case TRACER:
                for (int i = 0; i < 1; i++) {
                    setup(x, y, 0, 0, 3.0f, 0.1f, 0.1f, 0.5f, 0.5f, 0.5f, 1, 0.5f, 0.5f, 0.5f, 1f);
                }
                break;
        }
    }

    Vector2 v2tmp = new Vector2(0, 0);

    public void makeExplosion(float x, float y) {
        for (int k = 0; k < 120; k++) {
            v2tmp.set(MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f));
            v2tmp.nor();
            v2tmp.scl(MathUtils.random(50f, 120f));
            setup(x, y, v2tmp.x, v2tmp.y, 1.0f, 1.8f, 3.5f, 1, 0, 0, 1, 1, 0.6f, 0, 1);
        }
    }

    public void render(SpriteBatch batch) {
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        for (int i = 0; i < activeList.size(); i++) {
            Particle o = activeList.get(i);
            float t = o.getTime() / o.getDuration();
            float scale = lerp(o.getSize1(), o.getSize2(), t);
            if (Math.random() < 0.04) {
                scale *= 3.0f;
            }
            batch.setColor(lerp(o.getR1(), o.getR2(), t), lerp(o.getG1(), o.getG2(), t), lerp(o.getB1(), o.getB2(), t), lerp(o.getA1(), o.getA2(), t));
            batch.draw(particleTexture, o.getPosition().x - 8, o.getPosition().y - 8, 8, 8, 16, 16, scale, scale, 0);
        }
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        for (int i = 0; i < activeList.size(); i++) {
            Particle o = activeList.get(i);
            float t = o.getTime() / o.getDuration();
            float scale = lerp(o.getSize1(), o.getSize2(), t);
            if (Math.random() < 0.12) {
                scale *= 3.0f;
            }
            batch.setColor(lerp(o.getR1(), o.getR2(), t), lerp(o.getG1(), o.getG2(), t), lerp(o.getB1(), o.getB2(), t), lerp(o.getA1(), o.getA2(), t));
            batch.draw(particleTexture, o.getPosition().x - 8, o.getPosition().y - 8, 8, 8, 16, 16, scale, scale, 0);
        }
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
    }

    public float lerp(float value1, float value2, float point) {
        return value1 + (value2 - value1) * point;
    }
}
