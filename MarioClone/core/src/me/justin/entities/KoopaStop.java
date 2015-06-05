package me.justin.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

import me.justin.Entity;
import me.justin.Level;

//Used to stop koopas from leaving a certain area
public class KoopaStop extends Entity {

    public KoopaStop(Level level, Vector2 position, MapProperties properties) {
        super(level, position, properties);

        boundingBox.set(0,0,16, 16);
        updateBoundingBox();

        this.isSolid = false;
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void render(SpriteBatch batch) {
    }
}
