package com.tanks.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BulletEmitter extends ObjectPool<Bullet> {
    public enum BulletType {
        LIGHT_AMMO(ParticleEmitter.BulletEffectType.FIRE, true, false, false, false, 90, 15.0f, 4500, 25),
        LASER(ParticleEmitter.BulletEffectType.LASER, false, false, false, true, 1, 10.0f, -15000, 8),
        GROUNDHOG(ParticleEmitter.BulletEffectType.BOMB, true, false, true, false, 60, 15.0f, 4500, 15);

        private ParticleEmitter.BulletEffectType effect;
        private boolean gravity;
        private boolean groundhog;
        private boolean bouncing;
        private boolean piercing;
        private int groundClearingSize;
        private float maxTime;
        private int maxPower;
        private int damage;

        public ParticleEmitter.BulletEffectType getEffect() {
            return effect;
        }

        public boolean isGravity() {
            return gravity;
        }

        public boolean isBouncing() {
            return bouncing;
        }

        public boolean isGroundhog() {
            return groundhog;
        }

        public boolean isPiercing() {
            return piercing;
        }

        public int getDamage() {
            return damage;
        }

        public int getGroundClearingSize() {
            return groundClearingSize;
        }

        public float getMaxTime() {
            return maxTime;
        }

        public float getMaxPower() {
            return maxPower;
        }

        BulletType(ParticleEmitter.BulletEffectType effect, boolean gravity, boolean bouncing, boolean groundhog, boolean piercing, int groundClearingSize, float maxTime, int maxPower, int damage) {
            this.effect = effect;
            this.gravity = gravity;
            this.bouncing = bouncing;
            this.groundhog = groundhog;
            this.piercing = piercing;
            this.groundClearingSize = groundClearingSize;
            this.maxTime = maxTime;
            this.maxPower = maxPower;
            this.damage = damage;
        }
    }

    private GameScreen game;
    private TextureRegion bulletTexture;
    private Vector2 v2tmp = new Vector2(0, 0);
    private List<Tank> damaged;

    @Override
    protected Bullet newObject() {
        return new Bullet();
    }

    public BulletEmitter(GameScreen game, int size) {
        super(size);
        this.game = game;
        this.bulletTexture = Assets.getInstance().getAtlas().findRegion("ammo");
        this.damaged = new ArrayList<Tank>();
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            batch.draw(bulletTexture, activeList.get(i).getPosition().x - 8, activeList.get(i).getPosition().y - 8);
        }
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            updateBullet(activeList.get(i), dt, false);
        }
    }

    public List<Tank> updateBullet(Bullet b, float dt, boolean virtual) {
        b.addTime(dt);
        if (b.getType().isGravity()) {
            b.getVelocity().y -= GameScreen.GLOBAL_GRAVITY * dt;
        }

        damaged.clear();
        v2tmp.set(b.getVelocity()).scl(dt);
        float len = v2tmp.len() / 3;
        v2tmp.nor().scl(3);

        for (int i = 0; i < len; i++) {
            b.getPosition().add(v2tmp);

            if (b.getPosition().x < 0 || b.getPosition().x > Map.WORLD_WIDTH || b.getPosition().y > Map.WORLD_HEIGHT || b.getPosition().y < 0) {
                if (!b.getType().isBouncing()) {
                    b.deactivate();
                    break;
                } else {
                    if (b.getPosition().x < 0 && b.getVelocity().x < 0) {
                        b.getVelocity().x *= -1;
                        v2tmp.x *= -1;
                    }
                    if (b.getPosition().x > Map.WORLD_WIDTH && b.getVelocity().x > 0) {
                        b.getVelocity().x *= -1;
                        v2tmp.x *= -1;
                    }
                    if (b.getPosition().y > Map.WORLD_HEIGHT && b.getVelocity().y > 0) {
                        b.getVelocity().y *= -1;
                        v2tmp.y *= -1;
                    }
                }
            }

            if (game.getMap().isGround(b.getPosition().x, b.getPosition().y)) {
                if (!b.getType().isGroundhog()) {
                    b.deactivate();
                    for (int k = 0; k < game.getPlayers().size(); k++) {
                        if (!damaged.contains(game.getPlayers().get(k)) && Vector2.dst(b.getPosition().x, b.getPosition().y, game.getPlayers().get(k).getHitArea().x, game.getPlayers().get(k).getHitArea().y) < b.getType().getGroundClearingSize() + game.getPlayers().get(k).getHitArea().radius) {
                            damaged.add(game.getPlayers().get(k));
                        }
                    }
                } else {
                    if (b.getVelocity().y < 0) {
                        v2tmp.y *= -0.8f;
//                        v2tmp.x *= 0.95f;
                        b.getVelocity().y *= -0.8f;
//                        b.getVelocity().x *= 0.95f;
                    }
                    b.getPosition().y += GameScreen.GLOBAL_GRAVITY * dt;
                }

                if (!virtual) {
                    if (!b.getType().isGroundhog()) {
                        game.getMap().clearGround(b.getPosition().x, b.getPosition().y, b.getType().getGroundClearingSize());
                    }
                    for (int j = 0; j < 10; j++) {
                        v2tmp.set(MathUtils.random(-1f, 1f), MathUtils.random(-1, 0.1f));
                        v2tmp.nor();
                        v2tmp.scl(MathUtils.random(100f, 150f));
                        game.getParticleEmitter().setup(b.getPosition().x, b.getPosition().y, v2tmp.x, v2tmp.y, 0.4f, 1.2f, 0.8f, 0, 0.3f, 0, 1, 0, 0.2f, 0, 0.2f);
                    }
                }
            }

            for (int j = 0; j < game.getPlayers().size(); j++) {
                if (b.getOwner() != game.getPlayers().get(j) && game.getPlayers().get(j).getHitArea().contains(b.getPosition())) {
                    if (!b.getType().isPiercing()) {
                        b.deactivate();
                    }
                    for (int k = 0; k < game.getPlayers().size(); k++) {
                        if (!damaged.contains(game.getPlayers().get(k)) && Vector2.dst(b.getPosition().x, b.getPosition().y, game.getPlayers().get(k).getHitArea().x, game.getPlayers().get(k).getHitArea().y) < b.getType().getGroundClearingSize() + game.getPlayers().get(k).getHitArea().radius) {
                            damaged.add(game.getPlayers().get(k));
                        }
                    }

                    if (!virtual && !b.getType().isPiercing()) {
                        game.getMap().clearGround(b.getPosition().x, b.getPosition().y, b.getType().getGroundClearingSize());

                        for (int k = 0; k < 15; k++) {
                            v2tmp.set(MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f));
                            v2tmp.nor();
                            v2tmp.scl(MathUtils.random(50f, 120f));
                            v2tmp.mulAdd(b.getVelocity(), 0.3f);
                            game.getParticleEmitter().setup(b.getPosition().x, b.getPosition().y, v2tmp.x, v2tmp.y, 0.4f, 1.8f, 0.4f, 1, 0, 0, 1, 1, 0.6f, 0, 1);
                        }
                    }
                }
            }

            if (!virtual && b.getType().getEffect() != ParticleEmitter.BulletEffectType.NONE) {
                game.getParticleEmitter().makeBulletEffect(b.getType().getEffect(), b.getPosition().x, b.getPosition().y);
            }
            if (!b.isActive()) {
                break;
            }
        }

        if (damaged.size() > 0) {
            b.deactivate();
            if (!virtual) {
                for (int k = 0; k < damaged.size(); k++) {
                    if (damaged.get(k).takeDamage(b.getPosition(), b.getType().getDamage())) {
                        game.destroyPlayer(damaged.get(k));
                    }
                }
            }
        }

        return damaged;
    }

    public void makeSparkle() {

    }

    public boolean empty() {
        return getActiveList().size() == 0;
    }

    public Bullet setup(Tank owner, BulletType type, float x, float y, float vx, float vy) {
        Bullet b = getActiveElement();
        b.activate(owner, type, x, y, vx, vy);
        return b;
    }
}
