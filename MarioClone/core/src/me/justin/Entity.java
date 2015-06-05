package me.justin;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {

    public final Vector2 position;
    public final BoundingRectangle boundingBox = new BoundingRectangle();
    public boolean isSolid = true; //If we block the player, or merely react with them
    public boolean oneWay = false;

    public boolean alive = true;

    protected Level level;

    public Entity(Level level, Vector2 position, MapProperties properties) {
        this.position = position;
        this.level = level;
    }

    public void updateBoundingBox() {
        boundingBox.setOrigin(position.x, position.y);
    }

    public abstract void update(float delta);
    public abstract void render(SpriteBatch batch);

}
