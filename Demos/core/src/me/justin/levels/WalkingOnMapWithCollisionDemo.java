package me.justin.levels;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * A demo to show how to draw a player that can be animated in multiple directions
 */
public class WalkingOnMapWithCollisionDemo extends ApplicationAdapter {

    private WalkingOnMapLevel level;

	@Override
	public void create () {
        level = new WalkingOnMapLevel(new TextureAtlas(Gdx.files.internal("sprites.pack")), "level.tmx");
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        level.update();
        level.render();
	}
}
