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

public class ReverseGoomba extends Goomba {

    public ReverseGoomba(Level level, Vector2 position, MapProperties properties) {
        super(level, position, properties);
        velocity.y = 120;
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion tex = dying? deadKeyframe : anim.getKeyFrame(animState, true);
        //Draw flipped
        batch.draw(tex, position.x, position.y, 0, tex.getRegionHeight()/2f,
                tex.getRegionWidth(), tex.getRegionHeight(),
                1, -1, 0);
    }
}
