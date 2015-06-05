package me.justin.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

import me.justin.BoundingRectangle;
import me.justin.DynamicEntity;
import me.justin.Entity;
import me.justin.Level;

public class Goomba extends DynamicEntity {

    protected Animation anim;
    protected TextureRegion deadKeyframe;
    protected float animState = 0;

    protected boolean dying = false;
    protected float dyingCounter = 0;

    public Goomba(Level level, Vector2 position, MapProperties properties) {
        super(level, position, properties);

        anim = new Animation(1/10f, level.spritesheet.findRegion("GoombaWalking"),
                level.spritesheet.findRegion("GoombaWalking1"));
        deadKeyframe = level.spritesheet.findRegion("GoombaDead");

        boundingBox.set(1, 1, anim.getKeyFrame(0).getRegionWidth()-1, anim.getKeyFrame(0).getRegionHeight()-1);
        updateBoundingBox();

        isSolid = false;
        velocity.x = -40;
        velocity.y = -120;
    }

    public void die() {
        dying = true;
    }

    @Override
    public void update(float delta) {
        animState += delta;
        if (Math.abs(position.x - level.player.position.x) > 500) return;
        if (dying) {
            dyingCounter += delta;
            if (dyingCounter > 0.5f) alive = false;
        }
        else {
            super.doMovementOrCollision(velocity.x * delta, velocity.y*delta, delta);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(dying? deadKeyframe : anim.getKeyFrame(animState, true), position.x, position.y);
    }

    @Override
    protected void onSolidEntityCollisionX(Entity e, float delta) {
        velocity.x = -velocity.x;
    }

    @Override
    protected void onTileCollisionX(BoundingRectangle r, float delta) {
        velocity.x = -velocity.x;
    }

    @Override
    protected void onEntityCollision(Entity e, float delta) {
        if (e instanceof Goomba) velocity.x = Math.signum((position.x + boundingBox.width/2f)
                - (e.position.x + e.boundingBox.width/2f)) * Math.abs(velocity.x);
    }
}
