package me.justin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Player {

    private final static String prefix = "ship-";
    private final static int NUMBER_FRAMES = 28;

    public Vector2 position;
    public float velocityY = 0;
    public boolean isAlive = true;

    private Level level;
    private Animation animation;
    private float animState = 0;

    //boxes, specified from bottom right of ship
    //if these collide with a solid tile, we end the game
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

    public Player(Level level, float x, float y) {
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

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            velocityY = 120;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            velocityY = -120;
        }
        else {
            velocityY = 0;
        }

        position.x += 90 * delta;
        position.y += velocityY * delta;
        animState += delta;

        for (Rectangle boundingBox : boundingBoxes) {
            //convert from bottom right to bottom left representation
            worldSpaceBoundingBox.x = position.x - boundingBox.x - boundingBox.width;
            worldSpaceBoundingBox.y = position.y + boundingBox.y;
            worldSpaceBoundingBox.width = boundingBox.width;
            worldSpaceBoundingBox.height = boundingBox.height;

            for (Rectangle c : getOverlappingSolidTiles()) {
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

        int leftTile = (int) ((position.x - frame.getRegionWidth())
                /level.mainLayer.getTileWidth());
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
        batch.draw(frame, position.x - frame.getRegionWidth(), position.y);

        //Draw bounding box, for debugging
//        for (Rectangle boundingBox : boundingBoxes) {
//            batch.draw(level.mainLayer.getCell(6,0).getTile().getTextureRegion(),
//                    position.x - boundingBox.x - boundingBox.width,
//                    position.y + boundingBox.y,  boundingBox.width, boundingBox.height);
//        }
    }
}
