package me.justin.tankshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Matrix4;

/**
 * A level for normal gameplay
 */
public class PlayerLevel extends BaseLevel {

    public final PlayerTank player;

    public PlayerLevel(String level) {
        super(level);
        for (int x=0; x<mainLayer.getWidth(); x++) {
            for (int y=0; y<mainLayer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = mainLayer.getCell(x,y);
                if (cell == null) continue;

                if ("true".equalsIgnoreCase(cell.getTile().getProperties()
                        .get("enemySpawn", "false", String.class))) {
                    enemyTanks.add(new EnemyTank(this, x, y));
                }
            }
        }
        this.player = new PlayerTank(this,
                Integer.parseInt(map.getProperties().get("playerX", String.class)),
                //convert coordinate space from top down to top up
                mainLayer.getHeight() - Integer.parseInt(map.getProperties().get("playerY", String.class)));
        camera.zoom = 0.5f;
    }

   public void doUpdate(float delta) {
       player.update(delta);
       camera.position.set(Math.round(player.getX()*TILE_SIZE),
               Math.round(player.getY()*TILE_SIZE), 0);
       if (Gdx.input.isKeyPressed(Input.Keys.Z)) camera.zoom = 0.5f;
       else camera.zoom = 0.3f;

       if (!player.isAlive()) state = LevelState.LOST;
    }

    protected void renderSprites(SpriteBatch batch) {
        player.render(batch);
    }

    protected void renderShapes(ShapeRenderer renderer) {
        player.renderStats(shapeRenderer);

        //Hud (points)
        Matrix4 oldProj = camera.combined;
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(-camera.viewportWidth*camera.zoom/2,
                -camera.viewportHeight*camera.zoom/2,
                camera.viewportWidth*camera.zoom/player.getMaxPoints() * player.getPoints(),
                10);
        shapeRenderer.setProjectionMatrix(oldProj);
    }
}
