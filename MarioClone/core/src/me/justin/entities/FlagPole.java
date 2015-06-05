package me.justin.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

import me.justin.Entity;
import me.justin.Level;

public class FlagPole extends Entity {

    private TextureRegion flagpole, flag;
    private boolean movingFlag = false, hit = false;

    private int flagHeight = 143, minFlagHeight = 16;

    private float finalAnimTimer = 0;

    public FlagPole(Level level, Vector2 position, MapProperties properties) {
        super(level, position, properties);
        flagpole = level.spritesheet.findRegion("Flagpole");
        flag = level.spritesheet.findRegion("FlagpoleFlag");
        boundingBox.set(0,0,flagpole.getRegionWidth(), flagpole.getRegionHeight());
        updateBoundingBox();

        this.isSolid = false;
    }

    @Override
    public void update(float delta) {
        if (movingFlag) {
            if (flagHeight <= minFlagHeight) {
                level.clearLevel.play();
                movingFlag = false;
                finalAnimTimer += delta;
            }
            else flagHeight --;

            level.player.position.x = position.x;
            level.player.position.y -= 60*delta;
            if (level.player.position.y < position.y) level.player.position.y = position.y;
            level.player.updateBoundingBox();
        }
        else if (finalAnimTimer > 0) {
            finalAnimTimer += delta;
            level.player.velocity.x = 60;
            if (finalAnimTimer > 5)
                level.state = Level.LevelState.WON;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(flagpole, position.x, position.y);
        batch.draw(flag, position.x-8, position.y + flagHeight);
    }

    public void onCollidePlayer(Player p, float delta) {
        if (hit) return;
        hit = true;
        movingFlag = true;
        level.music.pause();
        level.flagpole.play();
        p.controllable = false;
        p.acceleration.set(0,0);
        p.velocity.set(0,0);
    }
}
