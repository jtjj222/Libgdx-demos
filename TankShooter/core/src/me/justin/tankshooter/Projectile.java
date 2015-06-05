package me.justin.tankshooter;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Projectiles that are shot by the tanks
 */
public class Projectile {

    private BaseTank shooter;
    public float x,y;
    public boolean active = true;
    public final float directionVectorX, directionVectorY;
    public final float speed;

    public Projectile(BaseTank shooter, float x, float y, float dirX, float dirY, float speed) {
        this.shooter = shooter;
        this.x = x;
        this.y = y;
        this.directionVectorX = dirX;
        this.directionVectorY = dirY;
        this.speed = speed;
    }

    //TODO This will cause issues if it is moving fast enough to jump over the tank in one frame
    public void update(float delta, BaseLevel level) {
        if (checkCollision(level)) return;

        x += directionVectorX * delta * speed;
        y += directionVectorY * delta * speed;

        if (x >= level.mainLayer.getWidth() * PlayerLevel.TILE_SIZE
                || y >= level.mainLayer.getHeight() * PlayerLevel.TILE_SIZE
                || x<0 || y<0) active = false;
    }

    private boolean checkCollision(BaseLevel level) {
        if (!active) return false;

        //uncomment if we want to destroy the projectile
        //if we run into other projectiles
//        for (Projectile p : level.projectiles) {
//            if (p == this) continue;
//
//            if (x >= p.x-6 && y >= p.y-6
//                    && x <= p.x + 6
//                    && y <= p.y + 6) {
//                p.active = false;
//                active = false;
//            }
//        }
//        if (!active) return true;

        //Hitting enemy tanks
        for (BaseTank t : level.enemyTanks) {
            //We only care about referential equality
            if (t == shooter) continue;

            //TODO take into account rotation
            float tx = t.getX() * PlayerLevel.TILE_SIZE - BaseTank.TANK_SIZE/2;
            float ty = t.getY() * PlayerLevel.TILE_SIZE - BaseTank.TANK_SIZE/2;

            if (x >= tx && y >= ty
                    && x <= tx + BaseTank.TANK_SIZE
                    && y <= ty + BaseTank.TANK_SIZE) {
                t.onHit(shooter);
                active = false;
            }
        }

        if (!active) return true;

        //Normally, we would use inheritance, but this is easier for now
        if (level instanceof PlayerLevel) {
            PlayerLevel l = (PlayerLevel) level;

            if (shooter == l.player) return false;

            float tx = l.player.getX() * PlayerLevel.TILE_SIZE - BaseTank.TANK_SIZE/2;
            float ty = l.player.getY() * PlayerLevel.TILE_SIZE - BaseTank.TANK_SIZE/2;

            if (x >= tx && y >= ty
                    && x <= tx + BaseTank.TANK_SIZE
                    && y <= ty + BaseTank.TANK_SIZE) {
                l.player.onHit(shooter);
                active = false;
                return true;
            }
        }

        return false;
    }

    public void render(ShapeRenderer renderer) {
        renderer.setColor(1,1,1,1);
        renderer.rect(x,y,3,3);
    }
}
