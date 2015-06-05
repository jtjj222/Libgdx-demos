package me.justin.tankshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class PlayerTank extends BaseTank {

    private int points = 0, maxPoints = 100;
    private boolean godMode = false;

    public PlayerTank(PlayerLevel level, int x, int y) {
        super(level, "pt", x, y);
    }

    @Override
    public void onHit(BaseTank shooter) {
        if (!godMode) super.onHit(shooter);
    }

    @Override
    protected void doMovement(float delta) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) godMode = !godMode;

        //Using tan=opp/adj, we aim the player at the mouse
        float opp = Gdx.input.getX() - Gdx.graphics.getWidth()/2f;
        //The screen uses a bottom-down coordinate space, while the level uses bottom up
        //so we subtract the value from it's height
        float adj = Gdx.graphics.getHeight() - Gdx.input.getY() - Gdx.graphics.getHeight()/2f;
        float ang = (float) Math.toDegrees(Math.atan(opp/Math.abs(adj)));
        //compensate for when the mouse is in the lower half of the screen
        if (adj > 0) ang = 180 - ang;

        setTankAngle(ang);

        if (Gdx.input.isTouched()) {
            moveForward(delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            shoot();
        }
    }

    @Override
    public void givePoints(int p) {
        points += p;
        if (points >= maxPoints) {
            health = Math.min(maxHealth, health + 1);
            points = 0;
        }
    }

    public int getPoints() {
        return points;
    }

    public int getMaxPoints() {
        return maxPoints;
    }
}
