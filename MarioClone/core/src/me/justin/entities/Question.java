package me.justin.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

import me.justin.DynamicEntity;
import me.justin.Entity;
import me.justin.Level;

public class Question extends Entity {

    private Animation anim;
    private TextureRegion usedTexture;

    private float animState = 0;

    private boolean used = false;
    private DynamicEntity spawned = null;

    private String contents = null;

    public Question(Level level, Vector2 position, MapProperties properties) {
        super(level, position, properties);

        anim = new Animation(0.1f, level.spritesheet.findRegion("Question1"),
                level.spritesheet.findRegion("Question2"), level.spritesheet.findRegion("Question2"),
                level.spritesheet.findRegion("Question4"), level.spritesheet.findRegion("Question5"),
                level.spritesheet.findRegion("Question6"));
        usedTexture = level.spritesheet.findRegion("QuestionUsed");

        contents = properties.get("contains", null, String.class);

        boundingBox.set(0,0,anim.getKeyFrame(0).getRegionWidth(), anim.getKeyFrame(0).getRegionHeight());
        updateBoundingBox();
    }

    @Override
    public void update(float delta) {
        if (!used) animState += delta;
        if (spawned != null) {
            spawned.velocity.y = 60;
            spawned.velocity.x = 0;
            if (spawned.boundingBox.getWorldBottom() >= boundingBox.getWorldTop()) {
                spawned.velocity.y = -120;
                spawned.velocity.x = level.player.velocity.x > 0? 60 : -60;
                spawned = null;
                used = true;
            }
        }

    }

    @Override
    public void render(SpriteBatch batch) {
        if (used) batch.draw(usedTexture, position.x, position.y);
        else batch.draw(anim.getKeyFrame(animState, true), position.x, position.y);
    }

    public void onCollidePlayer(Player p, float delta) {
        if (p.boundingBox.getWorldTop() <= boundingBox.getWorldBottom()) {
            spawnContents();
        }
    }

    private void spawnContents() {
        if (contents == null || used) return;

        level.mushroom.play();

        spawned = (DynamicEntity) level.spawnEntity(contents, position.cpy(), null);
        used = true;
    }
}
