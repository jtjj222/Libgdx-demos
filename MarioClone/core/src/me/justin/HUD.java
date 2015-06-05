package me.justin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import javax.xml.soap.Text;

public class HUD implements Disposable {

    private SpriteBatch batch;
    private Camera camera;

    private BitmapFont font;
    private TextureRegion coinIcon;

    private Level level;

    public HUD(Level level) {
        camera = new OrthographicCamera(level.camera.viewportWidth, level.camera.viewportHeight);
        camera.update();
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("font.fnt"));
        font.setScale(camera.viewportWidth/Gdx.graphics.getWidth(),
                camera.viewportHeight/Gdx.graphics.getHeight());
        this.level = level;
        this.coinIcon = level.spritesheet.findRegion("coinIcon");
    }

    public void render() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        String player = "MARIO", world = "WORLD", time = "TIME";
        String points = "000000", coins = "  X " + level.player.coins, currWorld = "1-1", currTime = ""+(int)level.time;

        drawText(player, 1.5f, 0.8f);
        drawText(world, 9, 0.8f);
        drawText(time, 12.5f, 0.8f);

        drawText(points, 1.5f, 1.3f);
        drawText(coins, 5.5f, 1.3f);

        batch.draw(coinIcon, getScreenX(5.5f), getScreenY(1.3f) - coinIcon.getRegionHeight());

        //Draw centered
        BitmapFont.TextBounds worldBounds = font.getBounds(currWorld);
        font.draw(batch, currWorld, getScreenX(10.25f) - worldBounds.width / 2f, getScreenY(1.3f));

        //Draw right aligned
        float cWidth = font.getBounds(currTime).width;
        float tWidth = font.getBounds(time).width;
        font.draw(batch, currTime, getScreenX(12.5f) + tWidth - cWidth, getScreenY(1.3f));

        batch.end();
    }

    private void drawText(String s, float tileX, float tileY) {
        font.draw(batch, s, getScreenX(tileX), getScreenY(tileY));
    }

    //Get x coordinates (from left edge) for the tile
    private float getScreenX(float tile) {
        return tile * (camera.viewportWidth / level.collisionLayer.getTileWidth()) - camera.viewportWidth/2f;
    }

    //from top left
    private float getScreenY(float tile) {
        float tileHeight = level.collisionLayer.getTileHeight();
        return camera.viewportHeight/2f - tile * tileHeight;
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
