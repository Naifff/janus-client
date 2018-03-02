package com.geekbrains.projectjanus;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FoodEmitter extends ObjectPool<Food> {

    private GameScreen game;
    private TextureRegion foodTexture;


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

    public void foodCollision(Food f) {
        for (int i = 0; i < game.getPlayers().size(); i++) {
            if (f.getPosition().x > game.getPlayers().get(i).position.x
                    & f.getPosition().x < game.getPlayers().get(i).position.x + game.getPlayers().get(i).textureBase.getWidth() * game.getPlayers().get(i).hp / 100
                    & f.getPosition().y > game.getPlayers().get(i).position.y
                    & f.getPosition().y < game.getPlayers().get(i).position.y + game.getPlayers().get(i).textureBase.getHeight() * game.getPlayers().get(i).hp / 100) {
                game.getPlayers().get(i).up();
                f.deactivate();
            }
        }

    }

    public Food setup(float x, float y) {
        Food f = getActiveElement();
        f.activate(x, y);
        return f;
    }
}
