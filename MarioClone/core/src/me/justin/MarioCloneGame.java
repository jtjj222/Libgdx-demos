package me.justin;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import me.justin.entities.Player;

public class MarioCloneGame extends ApplicationAdapter {

    private Level levelInstance;
    private int level = 0, world = 1;
    private static final int MAX_LEVELS = 4, MAX_WORLDS = 1;

    private boolean won = false;

    private Texture win;
    private SpriteBatch batch;
	
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
        changeLevel();
        win = new Texture(Gdx.files.internal("win.png"));

        batch = new SpriteBatch();
	}

    @Override
	public void render () {
        if (won) {
            renderWon();
            return;
        }
        levelInstance.update(Gdx.graphics.getDeltaTime());
        if (levelInstance.state == Level.LevelState.WON) changeLevel();
        else if (levelInstance.state == Level.LevelState.RETRY) retryLevel();
        levelInstance.render();
	}

    private void changeLevel() {
        int coins = 0;
        Player.Powerup powerup = Player.Powerup.SMALL;
        if (levelInstance != null) {
            coins = levelInstance.player.coins;
            powerup = levelInstance.player.powerup;
            levelInstance.dispose();
        }
        level++;
        if (level > MAX_LEVELS) {
            level = 0;
            world ++;
        }

        if (world > MAX_WORLDS) won = true;
        else {
            levelInstance = new Level("levels/" + world + "-" + level + ".tmx");
            levelInstance.player.coins = coins;
            levelInstance.player.powerup = powerup;
            //TODO points
        }
    }

    private void retryLevel() {
        if (levelInstance != null) {
            levelInstance.dispose();
        }

        levelInstance = new Level("levels/" + world + "-" + level + ".tmx");
    }

    private void renderWon() {
        Gdx.gl.glClearColor(0, 0, 0,  1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(win, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }
}
