package me.justin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import me.justin.entities.Player;

public class Level implements Disposable {

    public final TextureAtlas spritesheet;

    public final TiledMap level;
    public final TiledMapTileLayer collisionLayer;
    public final MapLayer objectLayer;
    private final OrthogonalTiledMapRenderer levelRenderer;
    private final SpriteBatch batch, gameOverBatch;

    //Background colour
    private float r,g,b;

    private Texture intro, gameover;

    public final OrthographicCamera camera;
    public final Player player;

    public float time = 350;

    public final ArrayList<Entity> entities = new ArrayList<Entity>();

    private BoundingRectangle tileCollisionInstance; //recycled by getClosestTile to avoid gc
    //recycled by getClosestTile to check for tile collisions
    private BoundingRectangle tileBoundingRectangleInstance;

    private HUD hud;

    public final Sound gameOver, jump, powerup, mushroom, damage, stomp,
            clearLevel, coin, brickSmash, bump, flagpole;
    public final Music music;

    public LevelState state = LevelState.INTRO;
    private float introCounter = 0;

    public static enum LevelState {
        NORMAL, WON, DEAD, INTRO, RETRY
    }

    public Level (String file) {
        batch = new SpriteBatch();
        gameOverBatch = new SpriteBatch();
        spritesheet = new TextureAtlas(Gdx.files.internal("sprites.pack"));

        level = new TmxMapLoader().load(file);
        this.collisionLayer = (TiledMapTileLayer) level.getLayers().get("Collision");
        this.objectLayer = level.getLayers().get("Objects");
        this.levelRenderer = new OrthogonalTiledMapRenderer(level, 1);

        float ar = Gdx.graphics.getHeight()/(float)Gdx.graphics.getWidth();
        float viewportWidth =  18*collisionLayer.getTileWidth();
        float viewportHeight = viewportWidth*ar;
        this.camera = new OrthographicCamera(viewportWidth, viewportHeight);

        player = new Player(this, new Vector2(
                objectLayer.getObjects().get("Player").getProperties().get("x", 0f, Float.class),
                objectLayer.getObjects().get("Player").getProperties().get("y", 0f, Float.class)), null);

        //create our map objects by finding their class in me.justin.entites
        for (MapObject o : objectLayer.getObjects()) {
            if (o.getProperties().get("type",String.class).equalsIgnoreCase("player")) continue;

            float x = o.getProperties().get("x", 0f, Float.class);
            float y = o.getProperties().get("y", 0f, Float.class);
            String type = o.getProperties().get("type", String.class);

            spawnEntity(type, new Vector2(x,y), o.getProperties());
        }

        tileCollisionInstance = new BoundingRectangle(0,0,
                collisionLayer.getTileWidth(),
                collisionLayer.getTileHeight());
        tileBoundingRectangleInstance = new BoundingRectangle(0,0,
                collisionLayer.getTileWidth(),
                collisionLayer.getTileHeight());

        this.hud = new HUD(this);

        this.gameOver = Gdx.audio.newSound(Gdx.files.internal("sound/gameover.ogg"));
        this.brickSmash = Gdx.audio.newSound(Gdx.files.internal("sound/brick_smash.wav"));
        this.clearLevel = Gdx.audio.newSound(Gdx.files.internal("sound/clear.ogg"));
        this.coin = Gdx.audio.newSound(Gdx.files.internal("sound/coin.wav"));
        this.jump = Gdx.audio.newSound(Gdx.files.internal("sound/jump.wav"));
        this.mushroom = Gdx.audio.newSound(Gdx.files.internal("sound/mushroom.wav"));
        this.powerup = Gdx.audio.newSound(Gdx.files.internal("sound/powerup.wav"));
        this.stomp = Gdx.audio.newSound(Gdx.files.internal("sound/stomp.wav"));
        this.damage = Gdx.audio.newSound(Gdx.files.internal("sound/damage.wav"));
        this.bump = Gdx.audio.newSound(Gdx.files.internal("sound/bump.wav"));
        this.flagpole = Gdx.audio.newSound(Gdx.files.internal("sound/flagpole.wav"));
        this.music = Gdx.audio.newMusic(Gdx.files.internal(level.getProperties()
                .get("music", "sound/above.ogg", String.class)));
        music.setLooping(true);

        this.intro = new Texture(Gdx.files.internal("intro.png"));
        this.gameover = new Texture(Gdx.files.internal("gameover.png"));

        this.r = Float.parseFloat(level.getProperties().get("r", ""+92/255f, String.class));
        this.g = Float.parseFloat(level.getProperties().get("g", "" + 148 / 255f, String.class));
        this.b = Float.parseFloat(level.getProperties().get("b", "" + 252 / 255f, String.class));
    }

    public void update(float delta) {

        if (state == LevelState.INTRO){
            introCounter += delta;
            if (introCounter > 2) {
                state = LevelState.NORMAL;
                music.play();
            }
            return;
        }
        else if (state == LevelState.DEAD) {
            if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) state = LevelState.RETRY;
        }

        if (state != LevelState.NORMAL) return;

        time -= delta;

        Iterator<Entity> eitr = entities.iterator();
        while (eitr.hasNext()) {
            Entity e = eitr.next();
            e.update(delta);
            if (!e.alive) eitr.remove();
        }

        player.update(delta);

        if (player.position.x + player.getWidth() > camera.position.x) {
            camera.position.x = player.position.x + player.getWidth();
        }
        if (player.position.x < camera.position.x - camera.viewportWidth/2f) {
            player.position.x = camera.position.x - camera.viewportWidth/2f;
            player.velocity.x = 0;
        }

        camera.position.y = camera.viewportHeight/2f + collisionLayer.getTileHeight()/2f;
        camera.update();
        levelRenderer.setView(camera);

        if (!player.alive || time <= 0) {
            state = LevelState.DEAD;
            music.pause();
            gameOver.play();
        }
    }

    public Entity spawnEntity(String type, Vector2 position, MapProperties properties) {
        try {
            //We use the libgdx reflection tools instead of the java ones so it works in js
            //There is an entry in the gwt properties file to include these classes in the
            //reflection cache for the html project
            Class claz = ClassReflection.forName("me.justin.entities." + type);
            Constructor constructor = ClassReflection.getConstructor(claz, Level.class, Vector2.class, MapProperties.class);
            Entity e = (Entity) constructor.newInstance(this, position, properties);
            entities.add(e);
            return e;

        } catch (ReflectionException e) {
            Gdx.app.log("level", "could not instantiate entity of type " + type + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Gdx.app.log("level", "Error:", e);
        }
        return null;
    }

    public BoundingRectangle getClosestTileInPathX(BoundingRectangle bounds, float movementX) {
        int tileX = (int) (bounds.getWorldX() / collisionLayer.getTileWidth());
        int widthTiles = (int) (Math.ceil(bounds.width / collisionLayer.getTileWidth()));
        int tileY = (int) (bounds.getWorldY() / collisionLayer.getTileHeight());
        int heightTiles = (int) (Math.ceil(bounds.height / collisionLayer.getTileHeight()));

        int movementTiles = movementX > 0 ?
                (int) (Math.ceil(movementX / collisionLayer.getTileHeight())) :
                (int) (Math.floor(movementX / collisionLayer.getTileHeight()));

        //if we are facing right, we find closest solid left edge, vice versa
        //we iterate through all tiles in the player's path

        int startTile = movementX > 0 ? tileX : tileX - widthTiles + movementTiles;
        int endTile = movementX > 0 ? tileX + widthTiles + movementTiles : tileX + widthTiles;

        int closestTileX=-1, closestTileY=-1;
        float closestDistance = Float.MAX_VALUE;

        for (int x = startTile; x <= endTile; x++) {
            for (int y = tileY; y <= tileY + heightTiles; y++) {
                TiledMapTileLayer.Cell cell = collisionLayer.getCell(x, y);

                if (cell != null && cell.getTile().getProperties()
                        .get("blocked", "false", String.class).equalsIgnoreCase("true")) {

                    tileBoundingRectangleInstance.setOrigin(x * collisionLayer.getTileWidth(),
                            y * collisionLayer.getTileHeight());

                    //if we don't overlap in the y axis, there can't be a collision
                    if (!bounds.overlapsWorldY(tileBoundingRectangleInstance)) continue;

                    float myDistance = Float.MAX_VALUE;

                    if (movementX > 0) { //facing right, so find the distance to left edge
                        //if object is to the left
                        if (tileBoundingRectangleInstance.getWorldRight() < bounds.getWorldLeft()) continue;

                        myDistance = tileBoundingRectangleInstance.getWorldLeft() - bounds.getWorldRight();
                    }
                    else if (movementX < 0) { //facing left
                        if (bounds.getWorldRight() < tileBoundingRectangleInstance.getWorldLeft()) continue;

                        myDistance = bounds.getWorldLeft() - tileBoundingRectangleInstance.getWorldRight();
                    }

                    if (myDistance < closestDistance) {
                        closestTileX = x;
                        closestTileY = y;
                        closestDistance = myDistance;
                    }
                }
            }
        }

        if (closestTileX == -1) return null;

        tileCollisionInstance.setOrigin(
                closestTileX * collisionLayer.getTileWidth(),
                closestTileY * collisionLayer.getTileHeight());
        return tileCollisionInstance;
    }

    //Make sure bounds spans from original position to new position to avoid bullet through paper issue
    public BoundingRectangle getClosestTileInPathY(BoundingRectangle bounds, float movementY) {
        int tileX = (int) (bounds.getWorldX() / collisionLayer.getTileWidth());
        int widthTiles = (int) (Math.ceil(bounds.width / collisionLayer.getTileWidth()));
        int tileY = (int) (bounds.getWorldY() / collisionLayer.getTileHeight());
        int heightTiles = (int) (Math.ceil(bounds.height / collisionLayer.getTileHeight()));

        int movementTiles = movementY > 0 ?
                (int) (Math.ceil(movementY / collisionLayer.getTileHeight())) :
                (int) (Math.floor(movementY / collisionLayer.getTileHeight()));

        int startTile = movementY > 0 ? tileY : tileY - heightTiles + movementTiles;
        int endTile = movementY > 0 ? tileY + heightTiles + movementTiles : tileY + heightTiles;

        int closestTileX=-1, closestTileY=-1;
        float closestDistance = Float.MAX_VALUE;

        for (int y = startTile; y <= endTile; y++) {
            for (int x = tileX; x <= tileX + widthTiles; x++) {
                TiledMapTileLayer.Cell cell = collisionLayer.getCell(x, y);
                if (cell == null) continue;

                String blocked = cell.getTile().getProperties().get("blocked", "false", String.class);

                if (blocked.equalsIgnoreCase("true")
                        || movementY <= 0 && blocked.equalsIgnoreCase("one-way")) {
                    tileBoundingRectangleInstance.setOrigin(x * collisionLayer.getTileWidth(),
                            y * collisionLayer.getTileHeight());

                    //if we don't overlap in the y axis, there can't be a collision
                    if (!bounds.overlapsWorldX(tileBoundingRectangleInstance)) continue;

                    float myDistance = Float.MAX_VALUE;

                    if (movementY > 0) { //heading up, so find the distance to bottom edge
                        //if object is below
                        //if (tileBoundingRectangleInstance.getWorldBottom() < bounds.getWorldTop()) continue;

                        myDistance = tileBoundingRectangleInstance.getWorldTop() - bounds.getWorldBottom();
                    }
                    else if (movementY < 0) { //facing down
                        //if (bounds.getWorldTop() < tileBoundingRectangleInstance.getWorldBottom()) continue;

                        myDistance = bounds.getWorldBottom() - tileBoundingRectangleInstance.getWorldTop();
                    }

                    if (myDistance < closestDistance) {
                        closestTileX = x;
                        closestTileY = y;
                        closestDistance = myDistance;
                    }
                }
            }
        }

        if (closestTileX == -1) return null;
;
        tileCollisionInstance.setOrigin(
                closestTileX * collisionLayer.getTileWidth(),
                closestTileY * collisionLayer.getTileHeight());
        return tileCollisionInstance;
    }

    public Entity getClosestEntityInPathX(Entity me, float movementX) {
        Entity closest = null;
        double dist = Float.MAX_VALUE;

        for (Entity e : entities) {
            if (e == me || !e.isSolid || e.oneWay) continue;
            //Even if it is closest along X, there can't possibly be a collision
            if (!me.boundingBox.overlapsWorldY(e.boundingBox)) continue;
            float edist = Float.MAX_VALUE;

            if (movementX > 0) { //facing right, so find the distance to left edge
                //if object is to the left
                if (e.boundingBox.getWorldRight() < me.boundingBox.getWorldLeft()) continue;

                edist = e.boundingBox.getWorldLeft() - me.boundingBox.getWorldRight();
            }
            else if (movementX < 0) { //facing left
                if (me.boundingBox.getWorldRight() < e.boundingBox.getWorldLeft()) continue;

                edist = me.boundingBox.getWorldLeft() - e.boundingBox.getWorldRight();
            }

            if (edist < dist) {
                closest = e;
                dist = edist;
            }
        }

        return closest;
    }

    public Entity getClosestEntityInPathY(Entity me, float movementY) {
        Entity closest = null;
        float distY = Float.MAX_VALUE;
        float distX = Float.MAX_VALUE;

        for (Entity e : entities) {
            if (e == me || !e.isSolid) continue;
            if (e.oneWay && movementY > 0) continue;

            if (!me.boundingBox.overlapsWorldX(e.boundingBox)) continue;
            float eDistY = Float.MAX_VALUE, eDistX = Float.MAX_VALUE;

            if (movementY > 0) { //facing up, so find the distance to bottom edge
                //if object is to the left
                if (e.boundingBox.getWorldBottom() < me.boundingBox.getWorldTop()) continue;

                eDistY = e.boundingBox.getWorldTop() - me.boundingBox.getWorldBottom();
            }
            else if (movementY < 0) { //facing down
                if (me.boundingBox.getWorldTop() < e.boundingBox.getWorldBottom()) continue;

                eDistY = me.boundingBox.getWorldBottom() - e.boundingBox.getWorldTop();
            }

            //We want blocks that are closer to the player to be hit
            eDistX = Math.abs((e.boundingBox.getWorldLeft() + e.boundingBox.width / 2f)
                    - (me.boundingBox.getWorldLeft() + me.boundingBox.width / 2f));

            //May be some precision issues with the ==
            if (eDistY < distY || eDistY == distY && eDistX < distX) {
                closest = e;
                distY = eDistY;
                distX = eDistX;
            }
        }

        return closest;
    }

    public void render() {
        if (state == LevelState.NORMAL) renderNormal();
        else if (state == LevelState.DEAD || state == LevelState.RETRY) renderDead();
        else if (state == LevelState.INTRO) renderIntro();
    }

    public void renderDead() {
        Gdx.gl.glClearColor(0, 0, 0,  1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameOverBatch.begin();
        gameOverBatch.draw(gameover, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameOverBatch.end();
    }

    public void renderIntro() {
        Gdx.gl.glClearColor(0, 0, 0,  1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(intro, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        hud.render();
    }

    public void renderNormal() {
        Gdx.gl.glClearColor(r,g,b,  1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        levelRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (Entity e : entities) e.render(batch);
        player.render(batch);

        batch.end();

        hud.render();
    }

    public void dispose() {
        spritesheet.dispose();
        batch.dispose();
        level.dispose();
        levelRenderer.dispose();
        hud.dispose();
    }
}
