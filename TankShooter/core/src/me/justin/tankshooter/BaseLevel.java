package me.justin.tankshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Base for all levels
 */
public abstract class BaseLevel {

    public static final int TILE_SIZE = 32;

    public ArrayList<BaseTank> enemyTanks = new ArrayList<BaseTank>();
    public ArrayList<Projectile> projectiles = new ArrayList<Projectile>();

    public final TextureAtlas spriteSheet;
    public final BitmapFont font;
    protected final SpriteBatch batch;
    protected final ShapeRenderer shapeRenderer;

    public final OrthographicCamera camera;
    public final TiledMap map;
    public final TiledMapTileLayer mainLayer;
    public final boolean[][] collisionMap;

    protected final TiledMapRenderer mapRenderer;

    public static enum LevelState {
        PLAYING, WON, LOST
    }

    protected LevelState state = LevelState.PLAYING;

    public BaseLevel(String level) {
        this.spriteSheet = new TextureAtlas(Gdx.files.internal("spritesheet.atlas"));
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth() / Gdx.graphics.getDensity() * 2,
                Gdx.graphics.getHeight()/Gdx.graphics.getDensity() * 2);

        this.map = new TmxMapLoader().load(level);
        this.mainLayer = (TiledMapTileLayer) map.getLayers().get(0);
        this.collisionMap = new boolean[mainLayer.getWidth()][mainLayer.getHeight()];

        for (int x=0; x<mainLayer.getWidth(); x++) {
            for (int y=0; y<mainLayer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = mainLayer.getCell(x,y);
                if (cell == null) continue;

                collisionMap[x][y] = "true".equalsIgnoreCase(cell.getTile()
                        .getProperties().get("blocked", "false", String.class));
            }
        }

        this.mapRenderer = new OrthogonalTiledMapRenderer(map, 1);
        this.font = new BitmapFont(Gdx.files.internal("font/PressStart2p.fnt"));
    }

    public void dispose() {
        spriteSheet.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        map.dispose();
    }

    public void update(float delta) {
        if (state != LevelState.PLAYING) return;

        doUpdate(delta);

        camera.update();
        mapRenderer.setView(camera);

        Iterator<Projectile> itrp = projectiles.iterator();
        while (itrp.hasNext()) {
            Projectile p = itrp.next();
            p.update(delta, this);
            if (!p.active) itrp.remove();
        }

        Iterator<BaseTank> itrt = enemyTanks.iterator();
        while (itrt.hasNext()) {
            BaseTank t = itrt.next();
            t.update(delta);
            if (!t.isAlive()) itrt.remove();
        }

        if (enemyTanks.isEmpty()) state = LevelState.WON;
    }

    protected abstract void doUpdate(float delta);

    public void render() {
        mapRenderer.render();

        batch.begin();
        batch.setProjectionMatrix(camera.combined);

        for (BaseTank t : enemyTanks) t.render(batch);
        renderSprites(batch);
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setProjectionMatrix(camera.combined);
        for (Projectile p : projectiles) p.render(shapeRenderer);
        for (BaseTank t : enemyTanks) t.renderStats(shapeRenderer);
        renderShapes(shapeRenderer);
        shapeRenderer.end();

    }

    protected abstract void renderSprites(SpriteBatch batch);
    protected abstract void renderShapes(ShapeRenderer renderer);

    public boolean blocked(float x, float y) {
        if (x<0 || y<0 || x>=mainLayer.getWidth()
                || x>=mainLayer.getHeight()) return false;
        return collisionMap[(int) x][(int) y];
    }

    protected void addProjectile(Projectile p) {
        this.projectiles.add(p);
    }

    public LevelState getState() {
        return state;
    }
}
