package me.justin.tankshooter;

/**
 * Enemy tank during normal play
 */
public class EnemyTank extends BaseTank {

    public EnemyTank(PlayerLevel level, int x, int y) {
        super(level, "et", x, y);
        this.shootCounterMax*=4;
    }

    @Override
    protected void doMovement(float delta) {
        PlayerTank player = ((PlayerLevel) level).player;

        setTankAngle(getAngle(player));
        
        double dist = Math.sqrt((player.getX() - getX())*(player.getX() - getX())
                + (player.getY() - getY())*(player.getY() - getY()));
        
        if (dist <20 && dist > 2)         
            moveForward(delta);

        if (dist <= 12) {
            shoot();
        }

    }
}