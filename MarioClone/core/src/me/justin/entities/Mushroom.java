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

public class Mushroom extends DynamicEntity {

    private TextureRegion texture;

    public Mushroom(Level level, Vector2 position, MapProperties properties) {
        super(level, position, properties);

        texture = level.spritesheet.findRegion("Mushroom");

        boundingBox.set(0, 0, texture.getRegionWidth(), texture.getRegionHeight());
        updateBoundingBox();

        isSolid = false;
        velocity.x = 60;
        velocity.y = -120;
    }

    @Override
    public void update(float delta) {
        super.doMovementOrCollision(velocity.x * delta, velocity.y*delta, delta);

        if (velocity.y > -120) velocity.y -= 10;
        if (velocity.y < -120) velocity.y = -120;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    @Override
    protected void onSolidEntityCollisionY(Entity e, float delta) {
        //Bump check
        if (e instanceof Brick && ((Brick) e).state == Brick.BrickState.NUDGED_UP) {
            velocity.x = -velocity.x;
            velocity.y = 120;
        }
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
