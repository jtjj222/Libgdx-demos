package me.justin.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * A level, storing information about the map and where obstacles are.
 */
public class WalkingOnMapLevel {

    public final TextureAtlas spritesheet;

    private final TiledMap map;
    private final TiledMapTileLayer collisionLayer;
    private final TiledMapRenderer mapRenderer;
    private final OrthographicCamera camera;
    private final WalkingOnMapPlayer player;

    private final SpriteBatch batch;

    public WalkingOnMapLevel(TextureAtlas spritesheet, String levelFilename) {
        this.spritesheet = spritesheet;

        map = new TmxMapLoader().load(levelFilename);
        collisionLayer = (TiledMapTileLayer) map.getLayers().get("Collision");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 1/2f;

        MapObject player = map.getLayers().get("Objects").getObjects().get("Player");

        Vector2 position = new Vector2();
        position.x = player.getProperties().get("x", 0f, Float.class);
        position.y = player.getProperties().get("y", 0f, Float.class);

        this.player = new WalkingOnMapPlayer(position, this);

        batch = new SpriteBatch();
    }

    public void update() {
        player.update();

        camera.position.set(player.position.x, player.position.y, 0);
        camera.update();
    }

    public void render() {
        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined); //The final (combined) transform matrix for the camera
        batch.begin();
        player.render(batch);
        batch.end();
    }

    //Returns true if this area contains a blocked tile
    //Takes a rectangle (positioned from the bottom left in world space)
    public boolean blocked(float x, float y, float width, float height) {

        int leftX = (int) Math.floor(x / collisionLayer.getTileWidth());
        int rightX = (int) Math.floor((x + width) / collisionLayer.getTileWidth());
        int bottomY = (int) Math.floor(y / collisionLayer.getTileHeight());
        int topY = (int) Math.floor((y + height) / collisionLayer.getTileHeight());

        //Iterate through all tile positions inside this rectangle
        for (int tx = leftX; tx <= rightX; tx++) {
            for (int ty=bottomY; ty <= topY; ty++) {

                TiledMapTileLayer.Cell cell = collisionLayer.getCell(tx, ty);
                if (cell != null && cell.getTile().getProperties().get("solid", "false", String.class)
                    .equalsIgnoreCase("true")) return true;
            }
        }

        return false;
    }

}
