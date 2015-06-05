package me.justin;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class FloppyBirdsGame extends ApplicationAdapter {

    private Level level;
	
	@Override
	public void create () {
        level = new Level();
	}

	@Override
	public void render () {
        level.update();
        level.render();

        if (!level.bird.isAlive) {
            if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.UP)) {
                level.dispose();
                level = new Level();
            }
        }
	}
}
