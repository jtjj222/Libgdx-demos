package me.justin;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

/*
 * Superclass for anything that needs collision detection
 */
public abstract class DynamicEntity extends Entity {

    public static final float UNIT_CONVERSION = 60f / 10.6f;

    public final Vector2 velocity = new Vector2();
    public static final Vector2 gravity = new Vector2(0,-91*UNIT_CONVERSION);
    public final Vector2 acceleration = new Vector2().set(gravity);

    public DynamicEntity(Level level, Vector2 position, MapProperties properties) {
        super(level, position, properties);
        this.isSolid = true;
    }

    /**
     * This is my first attempt at collision detection, implementing the higher order fun
     * method (http://higherorderfun.com/blog/2012/05/20/the-guide-to-implementing-2d-platformers/)
     *
     * My impl. works, but operates under the assumption that you are never inside a block at
     * the beginning of a frame. TODO fix this before calling this method
     */
    protected void doMovementOrCollision(float movementX, float movementY, float delta) {
        updateBoundingBox();

        //Resolve movement along x axis first
        solveXSolidCollisions(movementX, delta);
        //Then along the y axis
        solveYSolidCollisions(movementY, delta);

        //We check to see if the new position overlaps
        //some enemies or other non-solid entities. They don't stop the player from moving,
        //but might kill him, etc..

        for (Entity e : level.entities) {
            if (e == this || e.isSolid) continue;
            if (e.boundingBox.overlapsWorldSpace(boundingBox)) onEntityCollision(e, delta);
        }
    }

    /**
     * We move the player along the x axis the minimum between movementX and the closest
     * obstacle.
    */
    private void solveXSolidCollisions(float movementX, float delta) {
        if (velocity.x == 0) return;

        BoundingRectangle xTileObstacle = level.getClosestTileInPathX(boundingBox, movementX);
        Entity xEntityObstacle = level.getClosestEntityInPathX(this, movementX);

        float unblockedMovementXTile = Float.MAX_VALUE, unblockedMovementXEntity = Float.MAX_VALUE;

        if (xTileObstacle != null) {
            if (velocity.x > 0) { //facing right
                unblockedMovementXTile =  xTileObstacle.getWorldLeft() - boundingBox.getWorldRight();
            }
            else if (velocity.x < 0) {
                unblockedMovementXTile = xTileObstacle.getWorldRight() - boundingBox.getWorldLeft();
            }
        }

        if (xEntityObstacle != null) {
            if (velocity.x > 0) { //facing right
                unblockedMovementXEntity =  xEntityObstacle.boundingBox.getWorldLeft() - boundingBox.getWorldRight();
            }
            else if (velocity.x < 0) {
                unblockedMovementXEntity = xEntityObstacle.boundingBox.getWorldRight() - boundingBox.getWorldLeft();
            }
        }

        if (Math.abs(unblockedMovementXTile) < Math.abs(unblockedMovementXEntity)) {
            if (Math.abs(unblockedMovementXTile) < Math.abs(movementX)) {
                position.x += unblockedMovementXTile;
                onTileCollisionX(xTileObstacle, delta);
            }
            else position.x += movementX;
        }
        else {
            if (Math.abs(unblockedMovementXEntity) < Math.abs(movementX)) {
                position.x += unblockedMovementXEntity;
                onSolidEntityCollisionX(xEntityObstacle, delta);
            }
            else position.x += movementX;
        }

        updateBoundingBox();
    }

    private void solveYSolidCollisions(float movementY, float delta) {
        if (velocity.y == 0) return;

        BoundingRectangle yObstacleTile = level.getClosestTileInPathY(boundingBox, movementY);
        Entity yObstacleEntity = level.getClosestEntityInPathY(this, movementY);
        float unblockedMovementYTile = Float.MAX_VALUE, unblockedMovementYEntity = Float.MAX_VALUE;

        //TODO remove duplicate code
        if (yObstacleTile != null) {
            if (velocity.y > 0) { //facing up
                unblockedMovementYTile =  yObstacleTile.getWorldBottom() - boundingBox.getWorldTop();
            }
            else if (velocity.y < 0) {
                unblockedMovementYTile = yObstacleTile.getWorldTop() - boundingBox.getWorldBottom();
            }
        }

        if (yObstacleEntity!= null) {
            if (velocity.y > 0) { //facing up
                unblockedMovementYEntity =  yObstacleEntity.boundingBox.getWorldBottom() - boundingBox.getWorldTop();
            }
            else if (velocity.y < 0) {
                unblockedMovementYEntity = yObstacleEntity.boundingBox.getWorldTop() - boundingBox.getWorldBottom();
            }
        }

        if (Math.abs(unblockedMovementYTile) < Math.abs(unblockedMovementYEntity)) {
            if (Math.abs(unblockedMovementYTile) < Math.abs(movementY)) {
                position.y += unblockedMovementYTile;
                onTileCollisionY(yObstacleTile, delta);
            }
            else position.y += movementY;
        }
        else {
            if (Math.abs(unblockedMovementYEntity) < Math.abs(movementY)) {
                //give the object a chance to change how far the player moves
                position.y += unblockedMovementYEntity;
                onSolidEntityCollisionY(yObstacleEntity, delta);
            }
            else position.y += movementY;
        }

        updateBoundingBox();
    }

    //Callbacks for handling collisions

    protected void onTileCollisionX(BoundingRectangle r, float delta) {}
    protected void onTileCollisionY(BoundingRectangle r, float delta) {}
    protected void onSolidEntityCollisionX(Entity e, float delta) {}
    protected void onSolidEntityCollisionY(Entity e, float delta) {}
    protected void onEntityCollision(Entity e, float delta) {}
}
