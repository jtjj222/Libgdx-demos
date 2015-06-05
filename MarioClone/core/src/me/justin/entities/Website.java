package me.justin.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

import me.justin.Entity;
import me.justin.Level;

public class Website extends Entity {

    private final String url;

    public Website(Level level, Vector2 position, MapProperties properties) {
        super(level, position, properties);

        url =properties.get("url", String.class);

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

    public void gotoWebsite() {
        if (!Gdx.input.isKeyPressed(Input.Keys.W)) return;
        //trooooooooooooooooooooooooooooooll
        Gdx.net.openURI(url);
        Gdx.app.exit();
    }
}
