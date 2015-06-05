package me.justin.sprites;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

/**
 * A demo to show how to draw a player that can be animated in multiple directions
 */
public class AnimatedPlayerDemo extends ApplicationAdapter {

    private TextureAtlas spritesheet;
    private SpriteBatch batch;
    private AnimatedPlayer player;

	@Override
	public void create () {
        batch = new SpriteBatch();
        spritesheet = new TextureAtlas(Gdx.files.internal("sprites.pack"));
        player = new AnimatedPlayer(new Vector2(0,0), spritesheet);
	}

	@Override
	public void render () {
        player.update();

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Draw our sprite
        batch.begin();
        player.render(batch);
        batch.end();
	}
}
