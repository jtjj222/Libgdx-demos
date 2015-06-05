package me.justin.tankshooter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class BaseTank {

    public static final int TANK_SIZE = 32;
    public static final float TANK_MOVE_SPEED = 1.5f;

    //Name of the frames in the tank animation (i.e pt0)
    protected static final int FRAME_MAX = 6;
    protected static final String FRAME_DEAD = "_dead";

    private float x=0, y=0; //Middle of tank, in tiles
    //Angle of player tank, in degrees
    private float tankAngle;

    //Direction vector
    private float directionVectorX;
    private float directionVectorY;

    private TankState state = TankState.NORMAL;
    private static enum TankState {
        NORMAL, DYING, DEAD;
    }

    private float animElapsedTime = 0;
    private Animation tankAnimation;
    private TextureRegion deadTexture;

    protected int maxHealth = 100, health = maxHealth;

    //Countdown for when to remove the tank after it dies
    private float dyingTimer = 0;

    //For limiting bullets
    private float shootCounter = 0;
    protected float shootCounterMax = 80/60f;

    protected BaseLevel level;

    public BaseTank(BaseLevel level, String spriteFrameBase,
                    int x, int y) {
        this.level = level;
        this.x = x;
        this.y = y;
        this.tankAngle = 0;

        TextureRegion[] frames = new TextureRegion[FRAME_MAX + 1];
        for (int i=0; i<=FRAME_MAX; i++)
            frames[i] = level.spriteSheet.findRegion(spriteFrameBase + i);

        this.tankAnimation = new Animation(1/15f, frames);
        this.deadTexture = level.spriteSheet.findRegion(spriteFrameBase + FRAME_DEAD);

        updateDirectionVector();
    }

    private void updateDirectionVector() {
        directionVectorX = (float) Math.sin(Math.toRadians(tankAngle));
        directionVectorY = (float) -Math.cos(Math.toRadians(tankAngle));
    }

    private void updateAnimation(float delta) {
        animElapsedTime += delta;
    }

    public void update(float delta) {
        shootCounter+=delta;

        if (state == TankState.NORMAL) doMovement(delta);
        else if (state == TankState.DYING) {
            dyingTimer+=delta;
            if (dyingTimer > 20/60f) state = TankState.DEAD;
        }
    }

    /**
     * call get/setTankAngle to get/set tank angle,
     * call move to attempt to move, while stopping if there is a collision
     * @param delta - Time since last update, in ms. Multiply all of your movements by this.
     */
    protected abstract void doMovement(float delta);

    protected void moveForward(float delta) {
        move(getDirectionVectorX()*delta*TANK_MOVE_SPEED,
                getDirectionVectorY()*delta*TANK_MOVE_SPEED, delta);
    }

    /**
     * moves while respecting collisions and updating animations
     */
    protected void move(float x, float y, float delta) {
        if (tryMove(x,y)) updateAnimation(delta);
    }

    protected boolean shoot() {
        if (shootCounter < shootCounterMax) return false;

        Projectile p = new Projectile(this, x * BaseLevel.TILE_SIZE,
                y * BaseLevel.TILE_SIZE,
                (float) Math.sin(Math.toRadians(getTankAngle())),
                (float) -Math.cos(Math.toRadians(getTankAngle())), TANK_MOVE_SPEED*2f* BaseLevel.TILE_SIZE);
        level.addProjectile(p);

        if (100*Math.random() < 30) health -= 5; //Discourage spamming

        shootCounter = 0;
        return true;
    }

    public void onHit(BaseTank shooter) {
        //Random damage to break ties.
        for (int i=0; i<10; i++)
            if (100*Math.random() <= 30) health--;

        if (health <=0) {
            health = 0;
            state = TankState.DYING;
        }

        //Give the player points if they hit us
        shooter.givePoints(20);
    }

    /**
     * Tries to move while respecting collisions. Returns false if no movement.
     */
    private boolean tryMove(float x, float y) {
        float newX = this.x + x;
        float newY = this.y + y;
        //If the new position is blocked, check if we can move along the x and y axis
        if (!level.blocked(newX, newY)) {
            this.x = newX;
            this.y = newY;
            return true;
        }
        else if (!level.blocked(newX, this.y)) {
            this.x = newX;
            return true;
        }
        else if (!level.blocked(this.x, newY)) {
            this.y = newY;
            return true;
        }
        else return false;
    }

    public void givePoints(int p) {
        //only the player should get points
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getDirectionVectorX() {
        return directionVectorX;
    }

    public float getDirectionVectorY() {
        return directionVectorY;
    }

    public float getTankAngle() {
        return tankAngle;
    }

    public void setTankAngle(float tankAngle) {
        this.tankAngle = tankAngle;
        updateDirectionVector();
    }

    public boolean isAlive() {
        return state != TankState.DEAD;
    }

    public void render(SpriteBatch batch) {
        batch.draw(state == TankState.NORMAL?
                        tankAnimation.getKeyFrame(animElapsedTime, true)
                        : deadTexture,
                x * BaseLevel.TILE_SIZE - TANK_SIZE/2,
                y * BaseLevel.TILE_SIZE - TANK_SIZE/2,
                TANK_SIZE/2,TANK_SIZE/2,
                TANK_SIZE, TANK_SIZE,
                1,1, 180+tankAngle);
    }

    public void renderStats(ShapeRenderer renderer) {
        renderer.setColor(Color.RED);
        renderer.rect(x* BaseLevel.TILE_SIZE - TANK_SIZE, y* BaseLevel.TILE_SIZE - TANK_SIZE,
                (float) TANK_SIZE*2/maxHealth*health, 10);
    }

    public double getDistance(BaseTank other) {
        return Math.sqrt((getX() - other.getX())*(getX() - other.getX())
                + (getY() - other.getY())*(getY()-other.getY()));
    }

    public float getAngle(BaseTank other) {
        return getAngle(other.getX(), other.getY());
    }

    public float getAngle(float x, float y) {
        //Using tan=opp/adj, we find the angle to the tank
        float opp = x - getX();
        float adj = y - getY();
        float ang = (float) Math.toDegrees(Math.atan(opp/Math.abs(adj)));
        //compensate for when the other tank is beneath
        if (adj > 0) ang = 180 - ang;

        return ang;
    }

    protected BaseTank findClosestTank() {
        BaseTank enemy = null;
        double distance = Float.MAX_VALUE;

        for (BaseTank e : level.enemyTanks) {
            if (e == this) continue;
            double eDist = getDistance(e);
            if (eDist < distance) {
                enemy = e;
                distance = eDist;
            }
        }

        if (level instanceof PlayerLevel) {
            PlayerTank p = ((PlayerLevel) level).player;
            if (p == this) return enemy;
            else if (getDistance(p) < distance) return p;
        }

        return enemy;
    }
}
