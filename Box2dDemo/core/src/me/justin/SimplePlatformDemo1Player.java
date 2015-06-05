package me.justin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class SimplePlatformDemo1Player {

    private Vector2 position = new Vector2(50,200);
    private float width = 40, height = 40;
    private Body body;

    public PlayerState state = PlayerState.FALLING;

    //We store the number of floor sections we are colliding with
    //because we can have multiple collisions
    private int floorCollisions = 0;

    public static enum PlayerState {
        FALLING, WALKING, JUMPING;
    }

    public SimplePlatformDemo1Player(World world) {
        BodyDef playerDef = new BodyDef();
        playerDef.type = BodyDef.BodyType.DynamicBody;
        playerDef.position.set(position.x / SimplePlatformDemo1.pixelsPerMetre, position.y / SimplePlatformDemo1.pixelsPerMetre);
        playerDef.fixedRotation = true; //We don't want the player to topple over
        body = world.createBody(playerDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2f/ SimplePlatformDemo1.pixelsPerMetre, height/2f/ SimplePlatformDemo1.pixelsPerMetre);

        FixtureDef def = new FixtureDef();
        def.shape = shape;
        def.density = 1f;
        def.friction = 0; //We handle player friction ourselves, because we don't want the player to stick to walls
        // Platformers have many unrealistic physics effects that we will have to do ourselves,
        // but it sure beats handling all of the collision edge cases ourselves!
        def.restitution = 0; //We don't want any 'bounce'

        body.createFixture(def);
        body.setUserData(this); //We keep a reference to this object so that other objects
        //can get this class instance via body.getUserData()
    }

    public void update(float delta) {
        position.set(body.getPosition().x * SimplePlatformDemo1.pixelsPerMetre, body.getPosition().y * SimplePlatformDemo1.pixelsPerMetre);

        //Apply force (i.e, always acting upon the object until an opposite force is applied)
        if (Gdx.input.isKeyPressed(Input.Keys.A)) body.applyForceToCenter(-250,0,true);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) body.applyForceToCenter(250,0,true);

        if (state == PlayerState.WALKING && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            body.applyForceToCenter(0, 2500, true);
            state = PlayerState.JUMPING;
        }

        //TODO find a better way to handle variable height jumps
        if (state == PlayerState.JUMPING && !Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            body.applyLinearImpulse(0, -5, body.getWorldCenter().x, body.getWorldCenter().y, true);
        }

        if (state == PlayerState.JUMPING && body.getLinearVelocity().y < 0) state = PlayerState.FALLING;

        //We limit the linear velocity of the player so that they don't run too fast
        //Note that this isn't the only way to handle platformer controls (and not neccesarily the best)
        //but it is 'good enough.' For a different way, look at http://www.iforce2d.net/b2dtut/constant-speed
        //The code is in c++ but it should be easy enough to understand
        Vector2 linearVelocity = body.getLinearVelocity();
        linearVelocity.x *= 0.8f; //Our unrealistic but "platformer-like" friction
        if (linearVelocity.x > 10) linearVelocity.x = 10;
        else if (linearVelocity.x < -10) linearVelocity.x = -10;
        body.setLinearVelocity(linearVelocity);
    }

    public void onCollideGround() {
        //You would have to check the direction of the collision
        //in order to stop people from being able to jump from walls or ceilings
        state = PlayerState.WALKING;
        floorCollisions++;
    }

    public void onLeaveGround() {
        floorCollisions--;
        if (floorCollisions == 0 &&
                state == PlayerState.WALKING) state = PlayerState.FALLING;
    }

}
