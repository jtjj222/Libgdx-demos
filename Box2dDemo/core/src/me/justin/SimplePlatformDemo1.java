package me.justin;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class SimplePlatformDemo1 extends ApplicationAdapter {

    public static final float pixelsPerMetre = 20;

    private SimplePlatformDemo1Player player;

    private Box2DDebugRenderer dbgr;
    private OrthographicCamera dbCamera;

    private World world;
	
	@Override
	public void create () {
        world = new World(new Vector2(0, -9.8f), true);
        //We use the debug renderer to show you how box2d is working
        //You would only use this to debug physics issues in an actual game
        dbgr = new Box2DDebugRenderer(true, false, false, false, true, false);
        player = new SimplePlatformDemo1Player(world);
        //Box2d handles objects from their centre, not bottom left
        addTerrain(world, 100, 10, 200, 20, 0);
        addTerrain(world, 210, 60, 20,  80, 0);
        addTerrain(world, 620, 90, 800, 20, 0);

        dbCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        dbCamera.zoom = 1f/pixelsPerMetre;
        //So that the middle of the camera is positioned in a way that the bottom left edge is at 0,0
        dbCamera.position.y = dbCamera.viewportHeight/2f * dbCamera.zoom;
        dbCamera.position.x = dbCamera.viewportWidth/2f * dbCamera.zoom;
        dbCamera.update();

        world.setContactListener(new ContactListener() {
            //Read more: http://www.iforce2d.net/b2dtut/collision-callbacks
            @Override
            public void beginContact(Contact contact) {
                if (contact.getFixtureA().getBody().getUserData() instanceof SimplePlatformDemo1Player
                        || contact.getFixtureB().getBody().getUserData() instanceof SimplePlatformDemo1Player)
                    player.onCollideGround(); //In a more sophisticated game, you would have some kind of
                    //onCollision method inside the individual objects involved that would be called here
                    //Note aswell that you can use sensor fixtures to get events for certain areas of the player,
                    //such as when their feet collide with the object: http://www.iforce2d.net/b2dtut/sensors
            }

            @Override
            public void endContact(Contact contact) {
                if (contact.getFixtureA().getBody().getUserData() instanceof SimplePlatformDemo1Player
                        || contact.getFixtureB().getBody().getUserData() instanceof SimplePlatformDemo1Player)
                    player.onLeaveGround();
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

        dbgr.render(world, dbCamera.combined);

        world.step(1 / 60f, 6, 2); //Advance the physics simulation
	}

    private void addTerrain(World world, int x, int y, int width, int height, float angle) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(x / SimplePlatformDemo1.pixelsPerMetre, y / SimplePlatformDemo1.pixelsPerMetre);
        def.angle = angle;
        Body body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2f/ SimplePlatformDemo1.pixelsPerMetre, height/2f/ SimplePlatformDemo1.pixelsPerMetre);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        fixture.density = 1;
        fixture.friction = 0; //We will handle friction ourselves so the player doesn't stick to walls

        body.createFixture(fixture);
    }
}
