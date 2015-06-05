package me.justin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Enemy {

    private final static String prefix = "enemy-";
    private final static int NUMBER_FRAMES = 28;

    public Vector2 position;
    public boolean isAlive = true;

    private Level level;
    private Animation animation;
    private float animState = 0;

    //boxes, specified from bottom left of ship
    private Rectangle[] boundingBoxes = {
            new Rectangle(0,5,37,7), //bottom middle
            new Rectangle(5,13,37,7),//top middle
            new Rectangle(17,0,5,9), //bottom wing
            new Rectangle(35,21,4,14), //top back wing
            new Rectangle(26,20,10,10), //top front wing
    };
    private Rectangle worldSpaceBoundingBox = new Rectangle(0,0,0,0);

    //recycled by getOverlappingTiles
    private ArrayList<Rectangle> overlapingTiles = new ArrayList<Rectangle>();

    public Enemy(Level level, float x, float y) {
        this.level = level;
        this.position = new Vector2(x,y);

        TextureRegion[] frames = new TextureRegion[NUMBER_FRAMES];
        for (int i=0; i<NUMBER_FRAMES; i++) {
            frames[i] = level.spritesheet.findRegion(prefix + i);
        }
        this.animation = new Animation(1/30f, frames);
    }

    public void update(float delta) {
        if (!isAlive) return;

        if (position.x - level.player.position.x <= 200) position.x -= 90 * delta;
        animState += delta;

        for (Rectangle boundingBox : boundingBoxes) {
            for (Rectangle c : getOverlappingSolidTiles()) {
                //convert to world space
                worldSpaceBoundingBox.x = position.x - boundingBox.x;
                worldSpaceBoundingBox.y = position.y + boundingBox.y;
                worldSpaceBoundingBox.width = boundingBox.width;
                worldSpaceBoundingBox.height = boundingBox.height;
                if (c.overlaps(worldSpaceBoundingBox)) {
                    isAlive = false;
                    break;
                }
            }
        }
    }

    private Iterable<Rectangle> getOverlappingSolidTiles() {
        overlapingTiles.clear();

        TextureRegion frame = animation.getKeyFrame(animState, true);

        int leftTile = (int) (position.x/level.mainLayer.getTileWidth());
        int bottomTile = (int) (position.y/level.mainLayer.getTileHeight());
        int widthTiles = (int) Math.ceil(frame.getRegionWidth()/level.mainLayer.getTileWidth());
        int heightTiles = (int) Math.ceil(frame.getRegionHeight()/level.mainLayer.getTileHeight());

        for (int x= leftTile; x <= leftTile + widthTiles; x++) {
            for (int y = bottomTile; y <= bottomTile + heightTiles; y++) {
                TiledMapTileLayer.Cell c = level.mainLayer.getCell(x, y);
                if (c != null && c.getTile().getProperties()
                        .get("blocked", "false", String.class).equalsIgnoreCase("true")) {
                    overlapingTiles.add(new Rectangle(
                            x*level.mainLayer.getTileWidth(),y*level.mainLayer.getTileHeight(),
                            level.mainLayer.getTileWidth(), level.mainLayer.getTileHeight()));
                }
            }
        }

        return overlapingTiles;
    }

    public void render(SpriteBatch batch) {
        TextureRegion frame = animation.getKeyFrame(animState, true);

        //We draw from right bottom
        batch.draw(frame, position.x, position.y);
//        for (Rectangle boundingBox : boundingBoxes) {
//            batch.draw(level.mainLayer.getCell(6,0).getTile().getTextureRegion(), boundingBox.x + position.x,
//                    boundingBox.y + position.y, boundingBox.width, boundingBox.height);
//        }
    }
}
