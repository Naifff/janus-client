package com.tanks.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

public class PlayerTank extends Tank {
    public enum Action {
        IDLE, MOVE_LEFT, MOVE_RIGHT, TURRET_UP, TURRET_DOWN, FIRE
    }

    private Action currentAction;

    public void setCurrentAction(Action currentAction) {
        this.currentAction = currentAction;
    }

    public PlayerTank(GameScreen game, Vector2 position) {
        super(game, position);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (game.isMyTurn(this) && !makeTurn) {
            if (currentAction == Action.TURRET_UP) {
                rotateTurret(1, dt);
            }
            if (currentAction == Action.TURRET_DOWN) {
                rotateTurret(-1, dt);
            }
            if (currentAction == Action.MOVE_LEFT) {
                move(-1, dt);
            }
            if (currentAction == Action.MOVE_RIGHT) {
                move(1, dt);
            }

            if (currentAction == Action.FIRE) {
                if (power < MINIMAL_POWER) {
                    power = MINIMAL_POWER + 1.0f;
                } else {
                    power += 400.0f * dt;
                    if (power > maxPower) {
                        power = maxPower;
                    }
                }
            } else {
                if (power > MINIMAL_POWER) {
                    float ammoPosX = weaponPosition.x + 4 + 28 * (float) Math.cos(Math.toRadians(turretAngle));
                    float ammoPosY = weaponPosition.y + 6 + 28 * (float) Math.sin(Math.toRadians(turretAngle));

                    float ammoVelX = power * (float) Math.cos(Math.toRadians(turretAngle));
                    float ammoVelY = power * (float) Math.sin(Math.toRadians(turretAngle));

                    game.getBulletEmitter().setup(this, BulletEmitter.BulletType.LASER, ammoPosX, ammoPosY, ammoVelX, ammoVelY);

                    power = 0.0f;

                    makeTurn = true;
                    currentAction = Action.IDLE;
                }
            }
//            currentAction = Action.IDLE;
        }
    }
}
