package me.justin.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

/**
 * An animated player that can walk in four directions
 */
public class AnimatedPlayer {

    private final Animation left, right, up, down;
    private Animation lastAnim; //If the player lets go of the controls, we keep them facing the same direction
    private float animState = 0; //Our current animation time

    private final Vector2 position, velocity;

    public AnimatedPlayer(Vector2 position, TextureAtlas spritesheet) {
        //Takes the time between frames, and varags or array of texture regions
        left = new Animation(0.3f, spritesheet.findRegion("pl_left1"), spritesheet.findRegion("pl_left2"));
        right = new Animation(0.3f, spritesheet.findRegion("pl_right1"), spritesheet.findRegion("pl_right2"));
        up = new Animation(0.3f, spritesheet.findRegion("pl_up1"), spritesheet.findRegion("pl_up2"));
        down = new Animation(0.3f, spritesheet.findRegion("pl_down1"), spritesheet.findRegion("pl_down2"));
        lastAnim = up;

        this.position = new Vector2(position); //We do this so that nobody else has a reference to our position
        this.velocity = new Vector2();
    }

    public void update() {
        animState += Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.W)) velocity.y = 60;
        else if (Gdx.input.isKeyPressed(Input.Keys.S)) velocity.y = -60;
        else velocity.y = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) velocity.x =-60;
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) velocity.x = 60;
        else velocity.x = 0;

        position.x += velocity.x * Gdx.graphics.getDeltaTime();
        position.y += velocity.y * Gdx.graphics.getDeltaTime();
    }

    public void render(SpriteBatch batch) {
        Animation anim = lastAnim;

        if (velocity.x > 0) anim = right;
        else if (velocity.x < 0) anim = left;
        else if (velocity.y > 0) anim = up;
        else if (velocity.y < 0) anim = down;

        batch.draw(anim.getKeyFrame(animState, true), position.x, position.y);
    }

}
