package me.justin.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

import me.justin.BoundingRectangle;
import me.justin.DynamicEntity;
import me.justin.Entity;
import me.justin.Level;

public class Brick extends Entity {

    private TextureRegion texture;
    private final float originalY;
    public BrickState state = BrickState.NORMAL;

    public static enum BrickState {
        NORMAL, NUDGED_UP, REBOUND;
    }

    public Brick(Level level, Vector2 position, MapProperties properties) {
        super(level, position, properties);
        originalY = position.y;
        if (properties.get("colour", "normal", String.class).equalsIgnoreCase("blue"))
             texture = level.spritesheet.findRegion("BrickBlue");
        else texture = level.spritesheet.findRegion("Brick");
        boundingBox.set(0,0,texture.getRegionWidth(), texture.getRegionHeight());
        updateBoundingBox();
    }

    @Override
    public void update(float delta) {
        switch (state) {
            case NUDGED_UP:
                position.y += 60*delta;
                updateBoundingBox();
                if (position.y > originalY + texture.getRegionHeight()/2f) state = BrickState.REBOUND;
                break;
            case REBOUND:
                position.y -= 60*delta;
                updateBoundingBox();
                if (position.y < originalY) {
                    position.y = originalY;
                    state = BrickState.NORMAL;
                }
                break;
            default:
                break;
        }
    }

    public void onCollidePlayer(Player player, float delta) {
        if (player.velocity.y > 0) {
            if (player.powerup == Player.Powerup.SMALL) {
                state = BrickState.NUDGED_UP;
                level.bump.play();
            }
            else {
                alive = false;
                level.brickSmash.play();
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

}
