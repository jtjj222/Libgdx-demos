package me.justin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

import java.util.ArrayList;

public class RadialGravityPlayer {

    public Body body;
    public float currentAngle = 0;

    public PlayerState state = PlayerState.FALLING;

    private ArrayList<Fixture> floorCollisions = new ArrayList<Fixture>();
    private Vector2 gravity = new Vector2();

    private RadialGravityDemo demo;

    public static enum PlayerState {
        FALLING, WALKING, JUMPING
    }

    public RadialGravityPlayer(RadialGravityDemo demo) {
        this.demo = demo;

        BodyDef playerDef = new BodyDef();
        playerDef.type = BodyDef.BodyType.DynamicBody;
        playerDef.position.set(0 / SimplePlatformDemo1.pixelsPerMetre, 200 / SimplePlatformDemo1.pixelsPerMetre);
        body = demo.world.createBody(playerDef);

        {
            CircleShape shape = new CircleShape();
            shape.setRadius(20f / SimplePlatformDemo1.pixelsPerMetre);

            FixtureDef def = new FixtureDef();
            def.shape = shape;
            def.density = 1f;
            def.friction = 0;
            def.restitution = 0;

            body.createFixture(def);
        }
        body.setUserData(this);
    }

    public void update(float delta) {

        float angle = gravity.cpy().nor().scl(-1).angle() - 90;

        currentAngle = 0.9f*currentAngle + 0.1f*angle;

        Vector2 force = new Vector2();
        Vector2 impulse = new Vector2();

        if (Gdx.input.isKeyPressed(Input.Keys.A)) force.x = -250;
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) force.x = 250;

        if (state == PlayerState.WALKING && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            force.y = 1000*body.getMass();
            state = PlayerState.JUMPING;
        }

        if (state == PlayerState.JUMPING && !Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            impulse.y = -5;
        }

        force.rotate(angle);
        impulse.rotate(angle);
        body.applyForceToCenter(force, true);
        body.applyForceToCenter(gravity, true);
        body.applyLinearImpulse(impulse, body.getWorldCenter(), true);

        if (state == PlayerState.JUMPING && body.getLinearVelocity().y < 0) state = PlayerState.FALLING;

        Vector2 linearVelocity = body.getLinearVelocity().rotate(-angle);
        linearVelocity.x *= 0.8f;
        if (linearVelocity.x > 10) linearVelocity.x = 10;
        else if (linearVelocity.x < -10) linearVelocity.x = -10;
        body.setLinearVelocity(linearVelocity.rotate(angle));

        updateGravity();
    }

    private void updateGravity() {
        Vector2 rayStart = body.getWorldCenter();
        Body closestBody = getClosestBody();

        Vector2 rayEnd = closestBody.getWorldCenter();

        body.getWorld().rayCast(new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                gravity.set(normal).scl(-1).scl(getGravityForce(body, fixture.getBody()));
                return fraction;
            }
        }, rayStart, rayEnd);
    }

    private Body getClosestBody() {
        float minDist = Float.MAX_VALUE;
        Body maxBody = null;

        for (Body b : demo.terrainBodies) {
            float dist = body.getWorldCenter().dst2(b.getWorldCenter());
            if (dist < minDist) {
                minDist = dist;
                maxBody = b;
            }
        }

        return maxBody;
    }

    private float getGravityForce(Body b1, Body b2) {
        //Static bodies have no mass, so we store it in the terrain userdata
        float m1 = b1.getMass();
        if (m1 == 0) m1 = (Float) b1.getUserData();
        float m2 = b2.getMass();
        if (m2 == 0) m2 = (Float) b2.getUserData();

        return (m1 * m2); //We ignore the distance because it makes the game feel weird
    }

    public void onCollideGround(Contact contact) {
        state = PlayerState.WALKING;

        if (contact.getFixtureA().getBody() == body) {
            floorCollisions.add(contact.getFixtureB());
        }
        else {
            floorCollisions.add(contact.getFixtureA());
        }
    }

    public void onLeaveGround(Contact contact) {

        if (contact.getFixtureA().getBody() == body) {
            floorCollisions.remove(contact.getFixtureB());
        }
        else {
            floorCollisions.remove(contact.getFixtureA());
        }

        if (floorCollisions.isEmpty() &&
                state == PlayerState.WALKING) state = PlayerState.FALLING;
    }

}
