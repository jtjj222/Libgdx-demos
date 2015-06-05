package me.justin.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * A player that can walk on a map with simple collision detection
 */
public class WalkingOnMapPlayer {

    private final WalkingOnMapLevel level;

    private final Animation left, right, up, down;
    private Animation lastAnim; //If the player lets go of the controls, we keep them facing the same direction
    private float animState = 0; //Our current animation time

    public final Vector2 position, velocity;
    private float spriteWidth = 12, spriteHeight = 16;
    //The player's collision box. We use this to check if his head
    private Rectangle collisionBox = new Rectangle(0, 1, 12, 7);

    public WalkingOnMapPlayer(Vector2 position, WalkingOnMapLevel level) {
        //Takes the time between frames, and varags or array of texture regions
        left = new Animation(0.1f, level.spritesheet.findRegion("pl_left1"), level.spritesheet.findRegion("pl_left2"));
        right = new Animation(0.1f, level.spritesheet.findRegion("pl_right1"), level.spritesheet.findRegion("pl_right2"));
        up = new Animation(0.1f, level.spritesheet.findRegion("pl_up1"), level.spritesheet.findRegion("pl_up2"));
        down = new Animation(0.1f, level.spritesheet.findRegion("pl_down1"), level.spritesheet.findRegion("pl_down2"));
        lastAnim = up;

        this.position = new Vector2(position); //We do this so that nobody else has a reference to our position
        this.velocity = new Vector2();

        this.level = level;
    }

    public void update() {
        if (velocity.x != 0 || velocity.y != 0) animState += Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.W)) velocity.y = 60;
        else if (Gdx.input.isKeyPressed(Input.Keys.S)) velocity.y = -60;
        else velocity.y = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) velocity.x =-60;
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) velocity.x = 60;
        else velocity.x = 0;

        tryMoveOrCollide();
    }

    // Just a note: if the player can move more than one tile in a frame, they can
    // jump through solid tiles. This is known as the bullet through paper problem, and
    // it can be solved using raycasting. The simplest solution, however, is just to
    // limit the amount the player can move.
    // Also note that lag spikes can cause the player to move too far, so if this becomes an issue,
    // you would need to make sure that delta time is always no larger than some value
    private void tryMoveOrCollide() {
        //The new *centre* of the player
        float newX = position.x + velocity.x*Gdx.graphics.getDeltaTime();
        float newY = position.y + velocity.y*Gdx.graphics.getDeltaTime();

        //If the new position is not blocked, move there
        if (!level.blocked(newX + collisionBox.x - spriteWidth/2f, newY + collisionBox.y - spriteHeight/2f,
                collisionBox.width, collisionBox.height)) {
            position.set(newX, newY);
        }
        //Otherwise, we check each axis individually
        else if (!level.blocked(newX + collisionBox.x - spriteWidth/2f, position.y + collisionBox.y - spriteHeight/2f,
                collisionBox.width, collisionBox.height)) {
            position.x = newX;
            velocity.y = 0;
        }
        else if (!level.blocked(position.x + collisionBox.x - spriteWidth/2f, newY + collisionBox.y - spriteHeight/2f,
                collisionBox.width, collisionBox.height)) {
            position.y = newY;
            velocity.x = 0;
        }
        else {
            //We have a collision, so we don't move them and we stop them from moving further
            velocity.set(0,0);
        }
    }

    public void render(SpriteBatch batch) {
        Animation anim = lastAnim;

        if (velocity.x > 0) anim = right;
        else if (velocity.x < 0) anim = left;
        else if (velocity.y > 0) anim = up;
        else if (velocity.y < 0) anim = down;

        lastAnim = anim;

        TextureRegion keyframe = anim.getKeyFrame(animState, true);
        //We want the position to be their center, but draw takes its bottom left corner
        batch.draw(keyframe, position.x - keyframe.getRegionWidth()/2f, position.y - keyframe.getRegionHeight()/2f);
    }
}
