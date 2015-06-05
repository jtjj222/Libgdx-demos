package me.justin;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

public class RadialGravityDemo extends ApplicationAdapter {

    public static final float pixelsPerMetre = 20;

    private RadialGravityPlayer player;

    private Box2DDebugRenderer dbgr;
    private OrthographicCamera dbCamera;

    public World world;
    public ArrayList<Body> terrainBodies = new ArrayList<Body>();
	
	@Override
	public void create () {
        //We will be doing gravity ourselves
        world = new World(new Vector2(0, 0), true);
        dbgr = new Box2DDebugRenderer(true, false, false, false, false, false);
        player = new RadialGravityPlayer(this);

        addPlanet(world, 0, 20, 80, 100);
        addPlanet(world, 250, 20, 80, 100);
        addRectangle(world, 125, 0, 50, 20, 50);

        dbCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        dbCamera.zoom = 1f/pixelsPerMetre;
        dbCamera.update();

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                if (contact.getFixtureA().getBody().getUserData() instanceof RadialGravityPlayer
                        || contact.getFixtureB().getBody().getUserData() instanceof RadialGravityPlayer)
                    player.onCollideGround(contact);
            }

            @Override
            public void endContact(Contact contact) {
                if (contact.getFixtureA().getBody().getUserData() instanceof RadialGravityPlayer
                        || contact.getFixtureB().getBody().getUserData() instanceof RadialGravityPlayer)
                    player.onLeaveGround(contact);
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}
        });
	}

	@Override
	public void render () {
        player.update(Gdx.graphics.getDeltaTime());

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        dbCamera.position.set(player.body.getWorldCenter().x, player.body.getWorldCenter().y, 0);
        Vector2 up = new Vector2(0,1).rotate(player.currentAngle);
        dbCamera.up.set(up.x, up.y, 0).nor();
        dbCamera.direction.set(0,0,-1);
        dbCamera.update();
        dbgr.render(world, dbCamera.combined);

        world.step(1 / 60f, 6, 2); //Advance the physics simulation
	}

    private void addPlanet(World world, int x, int y, float radius, float mass) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(x / pixelsPerMetre, y / pixelsPerMetre);
        Body body = world.createBody(def);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius/pixelsPerMetre);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        fixture.density = 1;
        fixture.friction = 0;

        body.createFixture(fixture);

        body.setUserData(mass);
        terrainBodies.add(body);
    }

    private void addRectangle(World world, int x, int y, float hw, float hh, float mass) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(x / pixelsPerMetre, y / pixelsPerMetre);
        Body body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(hw/pixelsPerMetre, hh/pixelsPerMetre);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        fixture.density = 1;
        fixture.friction = 0;

        body.createFixture(fixture);

        body.setUserData(mass);
        terrainBodies.add(body);
    }
}
