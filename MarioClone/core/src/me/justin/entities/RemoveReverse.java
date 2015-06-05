package me.justin.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

import java.util.Iterator;

import me.justin.Entity;
import me.justin.Level;

public class RemoveReverse extends Entity {

    //private TextureRegion tex;

    public RemoveReverse(Level level, Vector2 position, MapProperties properties) {
        super(level, position, properties);

        //tex = level.spritesheet.findRegion("1up");

        position.y = -20;

        boundingBox.set(0,0,4*16, 30*16);
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
