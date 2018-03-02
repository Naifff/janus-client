package com.geekbrains.projectjanus;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by WoW on 02.03.2018.
 */

public class Player {
    protected Texture textureBase;
    protected boolean ai = true;
    protected Vector2 position;
    protected Vector2 tmp=new Vector2(0,0);
    protected Vector2 velosity;
    protected GameScreen game;
    protected Circle hitArea;
    protected float speed;
    protected float hp;
    protected StringBuilder tmpStrBuilder = new StringBuilder();
    protected float time=20f;
    protected float tempTime=220f;

    public Vector2 getPosition() {
        return position;
    }

    public final static float TURRET_ROTATION_ANGULAR_SPEED = 90.0f; // скорость поворота турели
    public final static float MINIMAL_POWER = 0.01f; // минимальная сила выстрела
    public final static int MAX_MOVEMENT_DY = 10; // максимальная разница в высоте земли при движении

    public Circle getHitArea() {
        return hitArea;
    }

    public boolean isAlive() {
        return hp > 0;
    }


    public Player(GameScreen game, Vector2 position, boolean ai) {
        this.game = game;
        this.position = position;
        this.textureBase = new Texture("badlogic.jpg");
        this.hp = 100;
        this.hitArea = new Circle(position, textureBase.getWidth() * 0.4f);
        this.speed = 1000.0f;
        this.ai = ai;

    }

    public void render(SpriteBatch batch) {
//        batch.draw(imgMe, playerX, playerY, imgMe.getWidth() * lvl, imgMe.getHeight() * lvl);
        batch.draw(textureBase, position.x, position.y,textureBase.getWidth()*hp/100,textureBase.getHeight()*hp/100);
        batch.setColor(1, 1, 1, 1);
    }


    public void update(float dt) {

if (ai){
    tempTime+=dt;
    if (tempTime>time){
        tempTime=0f;
        tmp.x=MathUtils.random(-1,1);
        tmp.y=MathUtils.random(-1,1);
        velosity=tmp.nor().scl(speed);
    }
    position.x+=velosity.x*dt;
    position.y+=velosity.y*dt;
    if (position.x<-game.world){
        position.x=-game.world;
        velosity.x*=-1;
    }
    if (position.y<-game.world){
        position.y=-game.world;
        velosity.y*=-1;
    }
    if (position.x>game.world-textureBase.getWidth()*hp/100){
        position.x=game.world-textureBase.getWidth()*hp/100;
        velosity.x*=-1;
    }
    if (position.y>game.world-textureBase.getHeight()*hp/100){
        position.y=game.world-textureBase.getHeight()*hp/100;
        velosity.y*=-1;
    }
}
//        Player aim = null;
//
//        do {
//            aim = game.getPlayers().get(MathUtils.random(0, game.getPlayers().size() - 1));
//        } while (aim == this);

    }

    //    public boolean takeDamage(Vector2 bulletPosition, int dmg) {
//        hp -= dmg;
//        reddish += 1.0f;
//        pulse.y += 400.0f;
//        pulse.x -= (bulletPosition.x - position.x) * 10;
//        if (reddish > 1.0f) {
//            reddish = 1.0f;
//        }
//        // game.getInfoSystem().addMessage("-" + dmg, position.x + 20, position.y + 100, FlyingText.Colors.RED);
//        if (hp <= 0) {
//            return true;
//        }
//        return false;
//    }
    public boolean goDown(float dt) {
        position.y -= speed * dt;
        if (position.y < -game.world) {
            position.y = -game.world;
            return false;
        }
        return true;
    }


    public boolean goLeft(float dt) {
        position.x -= speed * dt;
        if (position.x < -game.world) {
            position.x = -game.world;
            return false;
        }
        return true;
    }

    public boolean goRight(float dt) {
        position.x += speed * dt;
        if (position.x > game.world - textureBase.getWidth() * hp / 100) {
            position.x = game.world - textureBase.getWidth() * hp / 100;
            return false;
        }
        return true;
    }

    public boolean goUp(float dt) {
        position.y += speed * dt;
        if (position.y > game.world - textureBase.getHeight() * hp / 100) {
            position.y = game.world - textureBase.getHeight() * hp / 100;
            return false;
        }
        return true;
    }

    public void playerCamera(Camera camera) {
        camera.position.x = position.x + (textureBase.getWidth() * hp / 100) / 2;
        camera.position.y = position.y + (textureBase.getHeight() * hp / 100) / 2;

    }

    public void up() {

        hp += 0.5;
        if (position.x > game.world - textureBase.getWidth() * hp / 100) {
            position.x = game.world - (int) (textureBase.getWidth() * hp / 100);
        }
        if (position.y > game.world - textureBase.getHeight() * hp / 100) {
            position.y = game.world - (int) (textureBase.getHeight() * hp / 100);

        }
        if (!ai) {
            System.out.println("hp="+hp);
            game.camera.viewportWidth = textureBase.getWidth() * (hp / 100 )* 4 * ScreenManager.VIEW_WIDTH / ScreenManager.VIEW_HEIGHT;
            game.camera.viewportHeight = textureBase.getWidth() * 4 * (hp / 100);
        }


    }

}

