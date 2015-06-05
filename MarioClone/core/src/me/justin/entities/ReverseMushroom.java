package me.justin.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

import me.justin.BoundingRectangle;
import me.justin.DynamicEntity;
import me.justin.Entity;
import me.justin.Level;

public class ReverseMushroom extends DynamicEntity {

    private TextureRegion texture;

    public ReverseMushroom(Level level, Vector2 position, MapProperties properties) {
        super(level, position, properties);

        texture = level.spritesheet.findRegion("ReverseMushroom");

        boundingBox.set(0, 0, texture.getRegionWidth(), texture.getRegionHeight());
        updateBoundingBox();

        isSolid = false;
        velocity.x = 60;
        velocity.y = 120;
    }

    @Override
    public void update(float delta) {
        super.doMovementOrCollision(velocity.x * delta, velocity.y*delta, delta);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    @Override
    protected void onSolidEntityCollisionX(Entity e, float delta) {
        velocity.x = -velocity.x;
    }

    @Override
    protected void onTileCollisionX(BoundingRectangle r, float delta) {
        velocity.x = -velocity.x;
    }
}
