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

public class VerticalElevator extends Entity {

    private TextureRegion texture;
    private float direction = 0;

    public VerticalElevator(Level level, Vector2 position, MapProperties properties) {
        super(level, position, properties);

        texture = level.spritesheet.findRegion("MovingPlatform");

        boundingBox.set(0, 0, texture.getRegionWidth(), texture.getRegionHeight());
        updateBoundingBox();

        isSolid = true;
        oneWay = true;

        if (properties.get("direction", "down", String.class).equalsIgnoreCase("up")) direction = 40;
        else direction = -40;

        updateBoundingBox();
    }

    @Override
    public void update(float delta) {

        if (direction < 0 &&
                level.player.boundingBox.overlapsWorldX(boundingBox)
                && level.player.velocity.y <= 0
                && level.player.position.y >= position.y
                //We give a bit of room above the player to upp them down, but not so much
                //that they can't jump. This is a bit of a hack, but it works
                && level.player.position.y <= position.y + 10) {
            level.player.position.y += direction*delta;
            level.player.updateBoundingBox();
        }

        position.y += direction*delta;
        updateBoundingBox();

        if (direction > 0 && position.y > level.camera.viewportHeight) position.y = -texture.getRegionHeight();
        if (direction < 0 && position.y < -texture.getRegionHeight()) position.y = level.camera.viewportHeight;

        if (direction > 0 &&
                level.player.boundingBox.overlapsWorldSpace(boundingBox)
                && level.player.velocity.y <= 0
                && level.player.position.y >= position.y) {
            level.player.position.y += direction*delta;
            level.player.updateBoundingBox();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }
}
