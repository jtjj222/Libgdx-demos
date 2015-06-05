package me.justin.tankshooter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TankShooterGame extends ApplicationAdapter {

    private static boolean battle = true;
    private String[] levels = {
            "levels/level-01.tmx", "levels/level-02.tmx",
            "levels/level-03.tmx"
    };
    private int currentLevel = 0, maxLevel = levels.length-1;
    private BaseLevel level;

    //1 second at 60fps
    private float levelChangeCounter = 0, levelChangeCounterMax = 1f;

    private SpriteBatch imageBatch;
    private Texture wonImage, lostImage, nextLevel;

	@Override
	public void create () {
        changeLevel(0);
        wonImage = new Texture(Gdx.files.internal("won.png"));
        lostImage = new Texture(Gdx.files.internal("lost.png"));
        nextLevel = new Texture(Gdx.files.internal("nextLevel.png"));
        imageBatch = new SpriteBatch();
        if (battle) maxLevel = 0;
	}

	@Override
	public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (levelChangeCounter > 0) {
            if (level.getState() == PlayerLevel.LevelState.LOST)
                renderImage(lostImage);
            else if (level.getState() == PlayerLevel.LevelState.WON
                    && currentLevel == maxLevel)
                if (battle) renderBattleScreen();
                else renderImage(wonImage);
            else renderImage(nextLevel);
                        
            levelChangeCounter+=Gdx.graphics.getDeltaTime();
            if (levelChangeCounter >= levelChangeCounterMax) {
                int l;
                if (level.getState() == PlayerLevel.LevelState.LOST) l = 0;
                else l = currentLevel + 1;

                if (l > maxLevel) return;

                changeLevel(l);
                levelChangeCounter = 0;
            }
            
            return;
        }

        //Limit delta to 30fps or higher to prevent bugs
        level.update(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));

        if (level.getState() != PlayerLevel.LevelState.PLAYING) {
            levelChangeCounter+=Gdx.graphics.getDeltaTime();
        }

        level.render();
	}

    private void renderBattleScreen() {
        imageBatch.begin();
        level.font.setColor(Color.WHITE);
        level.font.setScale(1);

        String text = "The winner is:";
        BitmapFont.TextBounds bounds = level.font.getBounds(text);
        float screenX = Gdx.graphics.getWidth()/2 - bounds.width/2;
        float screenY = Gdx.graphics.getHeight()/2 + bounds.height + 20;
        level.font.draw(imageBatch, text, screenX, screenY);

        text = level.enemyTanks.isEmpty() ? "Tie" : level.enemyTanks.get(0).getClass().getSimpleName();
        bounds = level.font.getBounds(text);
        screenX = Gdx.graphics.getWidth()/2 - bounds.width/2;
        screenY = Gdx.graphics.getHeight()/2 - bounds.height/2 - 20;
        level.font.draw(imageBatch, text, screenX, screenY);

        imageBatch.end();
    }

    private void renderImage(Texture image) {
        imageBatch.begin();
        int x = (Gdx.graphics.getWidth() - image.getWidth())/2;
        int y = (Gdx.graphics.getHeight() - image.getHeight())/2;
        if (x < 0 || y < 0) {
            imageBatch.draw(image, 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        else imageBatch.draw(image, x, y);
        imageBatch.end();
    }

    private void changeLevel(int newLevel) {
        this.currentLevel = newLevel;
        if (level != null) level.dispose();
        if (battle) level = new BattleLevel("levels/battle.tmx");
        else level = new PlayerLevel(levels[currentLevel]);
    }
}
