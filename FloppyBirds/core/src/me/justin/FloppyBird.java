package me.justin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;

public class FloppyBird {

    private Level level;
    private Animation animation;
    private TextureRegion falling;
    private float animationState = 0;
    private Circle boundingCircle; //We use a circle so everything works when we do rotations

    public int width, height;
    public float x, y, angle = 0;
    //Acceleration is constant, so that the bird will always start falling
    private float accelerationY = -800, velocityY = 0;
    public boolean isAlive = true;

    public FloppyBird(Level level) {
        this.level = level;
        this.animation = new Animation(0.2f,
                level.spriteAtlas.findRegion("bird_middle"),
                level.spriteAtlas.findRegion("bird_down"),
                level.spriteAtlas.findRegion("bird_middle"),
                level.spriteAtlas.findRegion("bird_up"));
        this.falling = level.spriteAtlas.findRegion("bird_down");
        this.width = level.spriteAtlas.findRegion("bird_middle").getRegionWidth();
        this.height = level.spriteAtlas.findRegion("bird_middle").getRegionHeight();
        this.x = 0;
        this.y = level.camera.viewportHeight/2f;

        this.boundingCircle = new Circle(0,0,0);
    }

    public void update() {
        if (!isAlive) return;

        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isTouched()) {
            velocityY = 300;
        }

        velocityY += accelerationY * Gdx.graphics.getDeltaTime();
        if (velocityY < -1000) accelerationY = -1000;

        y += velocityY*Gdx.graphics.getDeltaTime();

        if (y > level.camera.viewportHeight - height)
            y = level.camera.viewportHeight - height;
        if (y <= level.groundHeight) die();

        x += Gdx.graphics.getDeltaTime() * 150;

        if (velocityY < 0) angle -= Gdx.graphics.getDeltaTime() * 200;
        else angle += Gdx.graphics.getDeltaTime() * 200;
        if (angle > 20) angle = 20;
        if (angle < -20) angle = -20;

        boundingCircle.set(x + width/2f, y + height/2f, height/2f);
        animationState+=Gdx.graphics.getDeltaTime();
        if (collision()) die();
    }

    private void die() {
        isAlive = false;
    }

    private boolean collision() {
        int currentPipe = level.getCurrentPipe();
        return collision(currentPipe) || collision(currentPipe - 1) || collision(currentPipe + 1);
    }

    private boolean collision(int pipe) {
        if (pipe < 0 || pipe >= level.getPipesLength()) return false;

        float pipeWidth = level.pipe.getRegionWidth();
        float pipeLeft = pipe*(pipeWidth + level.pipeDisance) + level.pipeOffset;
        float pipeRight = pipeLeft + pipeWidth;
        float bottomPipeTop = level.getPipeHeight(pipe);
        float topPipeBottom = bottomPipeTop + level.pipeGap;

        //Find the closest point on the rectangle to the circle, and check if it is inside the circle
        float closestX = MathUtils.clamp(boundingCircle.x, pipeLeft, pipeRight);
        float closestTopY = MathUtils.clamp(boundingCircle.y, topPipeBottom, Float.MAX_VALUE);
        float closestBottomY = MathUtils.clamp(boundingCircle.y, 0, bottomPipeTop);

        float distanceSquaredToTop = (closestX - boundingCircle.x)*(closestX - boundingCircle.x)
                + (closestTopY - boundingCircle.y)*(closestTopY - boundingCircle.y);
        float distanceSquaredToBottom = (closestX - boundingCircle.x)*(closestX - boundingCircle.x)
                + (closestBottomY - boundingCircle.y)*(closestBottomY - boundingCircle.y);
        float radiusSquared = boundingCircle.radius*boundingCircle.radius;

        //If distance is smaller we are in the circle
        return distanceSquaredToBottom <= radiusSquared
                || distanceSquaredToTop <= radiusSquared;
    }

    public void render(SpriteBatch spriteBatch) {
        if (velocityY > 0) {
            spriteBatch.draw(animation.getKeyFrame(animationState, true),
                    x,y,width/2f,height/2f, width, height, 1,1, angle);
        }
        else {
            spriteBatch.draw(falling,
                    x, y, width/2f,height/2f, width, height, 1,1, angle);
        }
    }
}
