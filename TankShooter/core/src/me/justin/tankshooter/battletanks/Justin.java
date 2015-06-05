package me.justin.tankshooter.battletanks;

import me.justin.tankshooter.BaseTank;
import me.justin.tankshooter.BattleLevel;
import me.justin.tankshooter.BattleTank;

public class Justin extends BattleTank {

    public Justin(BattleLevel level, int x, int y) {
        super(level, x, y);
    }

    @Override
    protected void doMovement(float delta) {
        BaseTank closest = findClosestTank();
        if (closest == null) return;

        setTankAngle(getAngle(closest));

        double dist = Math.sqrt((closest.getX() - getX())*(closest.getX() - getX())
                + (closest.getY() - getY())*(closest.getY() - getY()));

        if (dist > 2)
            moveForward(delta);

        shoot();
    }
}
