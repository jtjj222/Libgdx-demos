package me.justin.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

import me.justin.BoundingRectangle;
import me.justin.DynamicEntity;
import me.justin.Entity;
import me.justin.Level;
import me.justin.MarioAnimation;

public class Player extends DynamicEntity {

    public PlayerState state = PlayerState.REST;
    public Powerup powerup = Powerup.SMALL, lastPowerup = null;
    public boolean turnaround = false, powerupAnim = false;

    public int coins = 0;

    private MarioAnimation animation = new MarioAnimation(this, level.spritesheet);
    public boolean controllable = true;

    public static enum PlayerState {
        REST, WALKING, JUMPING, FALLING;
    }

    public static enum Powerup {
        SMALL("Small"), BIG("Big"), REVERSE("Reverse");

        public String keyframeName;

        private Powerup(String keyframeName) {
            this.keyframeName = keyframeName;
        }
    }

    public Player (Level level, Vector2 pos, MapProperties properties) {
        super(level, pos, properties);
        animation.updateAnimation(0);
    }

    @Override
    public void update(float delta) {

        if (controllable) doInput(delta);

        if (powerup == Powerup.REVERSE) gravity.y = Math.abs(gravity.y);
        else gravity.y = -Math.abs(gravity.y);

        acceleration.y += gravity.y;
        if (powerup == Powerup.REVERSE && acceleration.y > gravity.y) acceleration.y = gravity.y;
        else if (powerup != Powerup.REVERSE && acceleration.y < gravity.y) acceleration.y = gravity.y;

        //friction
        velocity.x *= 0.95f;
        velocity.add(acceleration.x*delta, acceleration.y*delta);

        //terminal velocities
        if (velocity.x > 250) velocity.x = 250;
        else if (velocity.x < -250) velocity.x = -250;

        if (powerup == Powerup.REVERSE) {
            if (velocity.y > 250) velocity.y = 250;
            else if (velocity.y < -150) velocity.y = -150;
        }
        else {
            if (velocity.y > 150) velocity.y = 150;
            else if (velocity.y < -250) velocity.y = -250;
        }

        //Set to falling, and if we get a collision, we know we are still walking
        if (state == PlayerState.WALKING || state == PlayerState.REST) state = PlayerState.FALLING;
        doMovementOrCollision(velocity.x * delta, velocity.y*delta, delta);
        if (state == PlayerState.WALKING && Math.abs(velocity.x) < 5) state = PlayerState.REST;

        animation.updateAnimation(delta);
        updateBoundingBox();

        if (powerup != Powerup.REVERSE && position.y < 0 || powerup == Powerup.REVERSE &&
                position.y > 16*level.collisionLayer.getTileHeight()) alive = false;
    }

    private void doInput(float delta) {

        boolean left, right, jump;

        //These controller keycodes should be made user configurable, but I'm too lazy
        if (Controllers.getControllers().size > 0) {
            Controller controller = Controllers.getControllers().get(0);
            left = controller.getAxis(0) < -0.1;
            right = controller.getAxis(0) > 0.1;
            jump = controller.getButton(0) || controller.getButton(1) || controller.getButton(2) || controller.getButton(3);
        }
        else {
            left  = Gdx.input.isKeyPressed(Input.Keys.A);
            right = Gdx.input.isKeyPressed(Input.Keys.D);
            jump  = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        }

        if (left) acceleration.x = -300;
        else if (right) acceleration.x = 300;
        else acceleration.x = 0;

        if (Math.signum(velocity.x) != Math.signum(acceleration.x) && acceleration.x != 0
                && velocity.x != 0) turnaround = true;
        else turnaround = false;

        if (jump) {
            if (state == PlayerState.WALKING || state == PlayerState.REST) {
                if (powerup == Powerup.REVERSE)
                     acceleration.y = -1750*UNIT_CONVERSION;
                else acceleration.y = 1750*UNIT_CONVERSION;
                state = PlayerState.JUMPING;
                level.jump.play();
            }
        }
        else if (state == PlayerState.JUMPING && acceleration.y > 0) {
            //Check if their direction is reverse
            if (powerup == Powerup.REVERSE)
                 acceleration.y += 1200*UNIT_CONVERSION*60*delta; //If they stop holding the button they jump less
            else acceleration.y -= 1200*UNIT_CONVERSION*60*delta;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(animation.getKeyFrame(), position.x, position.y);
    }

    public int getWidth() {return animation.getKeyFrame().getRegionWidth();}
    public int getHeight() {return animation.getKeyFrame().getRegionHeight();}

    @Override
    public void updateBoundingBox() {
        this.boundingBox.set(2,1,12, powerup == Powerup.SMALL ? 14 : 30);
        super.updateBoundingBox();
    }

    @Override
    protected void onSolidEntityCollisionX(Entity e, float delta) {
        velocity.x = 0;
    }

    @Override
    protected void onSolidEntityCollisionY(Entity e, float delta) {
        if (e instanceof Question) ((Question) e).onCollidePlayer(this, delta);
        else if (e instanceof Brick) ((Brick) e).onCollidePlayer(this, delta);

        if (powerup == Powerup.REVERSE && velocity.y > 0
                || powerup != Powerup.REVERSE && velocity.y < 0) state = PlayerState.WALKING;
        velocity.y = 0;
    }

    //TODO replace this with an entity.onCollidePlayer method, this is really messy

    @Override
    protected  void onEntityCollision(Entity e, float delta) {
        if (e instanceof Mushroom) onMushroomCollision(e,delta);
        else if (e instanceof ReverseMushroom) onReverseMushroomCollision(e,delta);
        else if (e instanceof ReverseGoomba) onReverseGoombaCollision(e,delta);
        else if (e instanceof Goomba) onGoombaCollision(e,delta);
        else if (e instanceof Koopa) onKoopaCollision((Koopa) e, delta);
        else if (e instanceof Coin) onCoinCollision(e, delta);
        else if (e instanceof FlagPole) ((FlagPole) e).onCollidePlayer(this, delta);
        else if (e instanceof RemoveReverse) onRemoveReverse();
        else if (e instanceof Warp) onWarpCollision((Warp) e, delta);
        else if (e instanceof Website) ((Website) e).gotoWebsite();
    }

    @Override
    protected void onTileCollisionX(BoundingRectangle r, float delta) {
        velocity.x = 0;
    }

    @Override
    protected void onTileCollisionY(BoundingRectangle r, float delta) {
        if (powerup == Powerup.REVERSE && r.getWorldBottom() >= boundingBox.getWorldTop()
                || powerup != Powerup.REVERSE && r.getWorldTop() <= boundingBox.getWorldBottom())
            state = PlayerState.WALKING;

        velocity.y = 0;
    }

    private void onMushroomCollision(Entity mushroom, float delta) {
        mushroom.alive = false;

        if (powerup != Powerup.SMALL) return;
        powerupAnim = true;
        powerup = Powerup.BIG;
        lastPowerup = Powerup.SMALL;

        level.powerup.play();
    }

    private void onReverseMushroomCollision(Entity mushroom, float delta) {
        mushroom.alive = false;

        powerupAnim = true;
        lastPowerup = powerup;
        powerup = Powerup.REVERSE;

        level.powerup.play();
    }

    private void onGoombaCollision(Entity goomba, float delta) {
        if (boundingBox.getWorldBottom() >= goomba.boundingBox.getWorldBottom() + goomba.boundingBox.height/2f) {
            ((Goomba) goomba).die();
            velocity.y = 1200;
            level.stomp.play();
        }
        else {
            takeDamage();
        }
    }

    private void onReverseGoombaCollision(Entity goomba, float delta) {
        if (boundingBox.getWorldTop() <= goomba.boundingBox.getWorldTop() - goomba.boundingBox.height/2f) {
            ((Goomba) goomba).die();
            velocity.y = -1200;
            level.stomp.play();
        }
        else {
            takeDamage();
        }
    }

    //This should be in the koopa class
    private void onKoopaCollision(Koopa koopa, float delta) {
        if (boundingBox.getWorldBottom() >= koopa.boundingBox.getWorldBottom() + koopa.boundingBox.height/2f) {
            koopa.onStomp();
            if (koopa.state == Koopa.KoopaState.PARAKOOPA) velocity.y = 2500;
            else velocity.y = 1200;
            state = PlayerState.WALKING;
            level.stomp.play();
        }
        else {
            if (koopa.state == Koopa.KoopaState.SHELL && !koopa.kicked) koopa.onKick();
            else takeDamage();
        }
    }

    private void onCoinCollision(Entity coin, float delta) {
        coin.alive = false;
        coins++;
        level.coin.play();
    }

    private void onWarpCollision(Warp warp, float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            level.powerup.play();
            position.set(warp.x, warp.y);
            updateBoundingBox();
            level.camera.position.x = warp.x;
        }
    }

    private void onRemoveReverse() {
        if (powerup == Powerup.REVERSE) powerup = Powerup.BIG;
    }

    private void takeDamage() {
        if (powerupAnim) return;
        if (powerup == Powerup.SMALL) {
            alive = false;
        }
        else {
            powerupAnim = true;
            lastPowerup = powerup;
            powerup = Powerup.SMALL;
            level.damage.play();
        }
    }
}
