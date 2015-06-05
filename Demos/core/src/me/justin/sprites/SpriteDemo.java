package me.justin.sprites;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * A demo to show how to draw a texture from a spritesheet to the screen
 * Also shows how to
 */
public class SpriteDemo extends ApplicationAdapter {

    private TextureAtlas spritesheet;
    private TextureRegion ourImage;
    private SpriteBatch batch;

    //Vector2 hasan x and a y value, and can be used to represent a point
    private Vector2 position = new Vector2();

	@Override
	public void create () {
        batch = new SpriteBatch();

        //We create the spritesheet. You should only do this once in your code,
        //and have anything that uses it reference the existing spritesheet
        spritesheet = new TextureAtlas(Gdx.files.internal("sprites.pack"));
        //We find our texture region. findRegion() has to look through every sprite,
        //so we store a reference to the texture region
        ourImage = spritesheet.findRegion("ourImage"); //Use the name of the file before
        // you packed it into the spritesheet
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) position.y ++;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) position.y --;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) position.x --;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) position.x ++;

        //Draw our sprite
        batch.begin();
        // You can pass a texture region just like a texture to batch.draw()
        batch.draw(ourImage, position.x, position.y);
        batch.end();
	}
}
