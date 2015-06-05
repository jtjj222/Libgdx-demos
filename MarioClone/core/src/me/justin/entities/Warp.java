package me.justin.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

import me.justin.Entity;
import me.justin.Level;

public class Warp extends Entity {

    public final float x,y;

    public Warp(Level level, Vector2 position, MapProperties properties) {
        super(level, position, properties);

        x = Float.parseFloat(properties.get("warpX", String.class));
        y = Float.parseFloat(properties.get("warpY", String.class));

        boundingBox.set(0,0,16,16);
        updateBoundingBox();

        this.isSolid = false;
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void render(SpriteBatch batch) {
        //batch.draw(tex, position.x, position.y, 4*16, 20*16);
    }
}
