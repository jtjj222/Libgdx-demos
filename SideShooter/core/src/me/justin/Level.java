package me.justin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.Iterator;

public class Level implements Disposable {

    public final TextureAtlas spritesheet;

    public final Player player;
    public final ArrayList<Enemy> enemies = new ArrayList<Enemy>();

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera =
            new OrthographicCamera(Gdx.graphics.getWidth()/Math.max(1, Gdx.graphics.getDensity()),
                Gdx.graphics.getHeight()/Math.max(1, Gdx.graphics.getDensity()));

    public MapLayer objectLayer;
    public TiledMapTileLayer mainLayer;

    private SpriteBatch batch = new SpriteBatch();

    public Level(String file) {
        this.spritesheet = new TextureAtlas(Gdx.files.internal("sprites.pack"));
        this.map = new TmxMapLoader().load(file);
        this.renderer = new OrthogonalTiledMapRenderer(map);

        objectLayer = map.getLayers().get("Objects");
        mainLayer = (TiledMapTileLayer) map.getLayers().get("Main");

        MapObject player = objectLayer.getObjects().get("Player");
        float x = player.getProperties().get("x", 0f, Float.class);
        float y = player.getProperties().get("y", 0f, Float.class);
        this.player = new Player(this, x,y);

        camera.position.y = camera.viewportHeight/2f;

        for (MapObject o : objectLayer.getObjects()) {
            if (o.getProperties().get("type", " ", String.class).equalsIgnoreCase("enemy")) {
                float ex = o.getProperties().get("x", 0f, Float.class);
                float ey = o.getProperties().get("y", 0f, Float.class);
                enemies.add(new Enemy(this, ex, ey));
            }
        }
    }

    public void update(float delta) {
        if (!player.isAlive) {
            return;
        }

        player.update(delta);

        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy e = enemyIterator.next();
            e.update(delta);
            if (!e.isAlive) enemyIterator.remove();
        }

        camera.position.x = player.position.x - 20;
        camera.update();
    }

    public void render() {
        renderer.setView(camera);
        renderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        for (Enemy e : enemies) e.render(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        spritesheet.dispose();
    }
}
