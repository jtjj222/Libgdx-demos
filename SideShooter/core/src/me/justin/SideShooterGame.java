package me.justin;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SideShooterGame extends ApplicationAdapter {
    private Level level;
	
	@Override
	public void create () {
		level = new Level("level01.tmx");
	}

	@Override
	public void render () {
        level.update(Gdx.graphics.getDeltaTime());

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		level.render();
	}
}
