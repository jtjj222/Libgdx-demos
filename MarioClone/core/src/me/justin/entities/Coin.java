package me.justin.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

import me.justin.BoundingRectangle;
import me.justin.DynamicEntity;
import me.justin.Entity;
import me.justin.Level;

public class Coin extends Entity {

    private Animation anim;
    private float animState = 0;

    public Coin(Level level, Vector2 position, MapProperties properties) {
        super(level, position, properties);

        anim = new Animation(1/2f, level.spritesheet.findRegion("Coin1"),
                level.spritesheet.findRegion("Coin2"),level.spritesheet.findRegion("Coin3"),
                level.spritesheet.findRegion("Coin4"));

        boundingBox.set(-2, -2, anim.getKeyFrame(0).getRegionWidth()+2, anim.getKeyFrame(0).getRegionHeight()+2);
        updateBoundingBox();

        isSolid = false;
    }

    @Override
    public void update(float delta) {
        animState += delta;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(anim.getKeyFrame(animState, true), position.x, position.y);
    }
}
