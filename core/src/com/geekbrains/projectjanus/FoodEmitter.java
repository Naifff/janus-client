package com.geekbrains.projectjanus;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

/**
 * Created by WoW on 02.03.2018.
 */

public class FoodEmitter extends ObjectPool<Food> {


    private boolean gravity;
    private boolean groundhog;
    private boolean bouncing;
    private boolean piercing;
    private int groundClearingSize;
    private float maxTime;
    private int maxPower;
    private int damage;


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


    private GameScreen game;
    private TextureRegion foodTexture;
    private Vector2 v2tmp = new Vector2(0, 0);


    @Override
    protected Food newObject() {
        return new Food();
    }

    public FoodEmitter(GameScreen game, int size) {
        super(size);
        this.game = game;
        this.foodTexture = Assets.getInstance().getAtlas().findRegion("ammo");

    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            batch.draw(foodTexture, activeList.get(i).getPosition().x - 8, activeList.get(i).getPosition().y - 8);
        }
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            foodCollision(activeList.get(i));
        }
    }

//    imgMe.getWidth() * lvl

public void foodCollision(Food f){
        for (int i=0;i<game.getPlayers().size();i++){
        if(f.getPosition().x>game.getPlayers().get(i).position.x
                &f.getPosition().x<game.getPlayers().get(i).position.x+game.getPlayers().get(i).textureBase.getWidth()*game.getPlayers().get(i).hp/100
                &f.getPosition().y>game.getPlayers().get(i).position.y
                &f.getPosition().y<game.getPlayers().get(i).position.y+game.getPlayers().get(i).textureBase.getHeight()*game.getPlayers().get(i).hp/100)
        {game.getPlayers().get(i).up(); f.deactivate();
//            System.out.println("feed");
        }}

}
    public boolean empty() {
        return getActiveList().size() == 0;
    }

    public Food setup(float x, float y) {
        Food f = getActiveElement();
        f.activate(x, y);
        return f;
    }
}
