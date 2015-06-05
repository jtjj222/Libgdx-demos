package me.justin.tankshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * An example of how to use pathfinding
 */
public class PlayerAiTank extends PlayerTank {
    public PlayerAiTank(PlayerLevel level, int x, int y) {
        super(level, x, y);
    }

    //Used to render path when P is pressed
    private ArrayList<PathNode> path;
    private TextureRegion tex = level.spriteSheet.findRegion("pt_dead");

    @Override
    protected void doMovement(float delta) {
        BaseTank closestEnemy = findClosestTank();
        double distance = getDistance(closestEnemy);

        //If the enemy is within shooting distance, aim and shoot
        if (distance < 7) {
            setTankAngle(getAngle(closestEnemy));
            if (distance > 2) moveForward(delta);
            shoot();
            return;
        }

        //Otherwise, move to the enemy
        path = getPathTo(new PathNode((int)closestEnemy.getX(), (int)closestEnemy.getY(), null));

        if (path.size() > 4) {
            PathNode goTo = path.get(1);
            setTankAngle(getAngle(goTo.x + 0.5f, goTo.y + 0.5f));
            if (distance >= 2) moveForward(delta);
        }

        //If we are aiming at the enemy, shoot
        if (Math.abs(getTankAngle() - getAngle(closestEnemy)) < 20
                && distance < 25) {
            setTankAngle(getAngle(closestEnemy));
            shoot();
        }

    }

    protected static class PathNode {
        public final int x, y;
        public PathNode parentNode;

        public PathNode(int x, int y, PathNode parent) {
            this.x = x;
            this.y = y;
            this.parentNode = parent;
        }

        //Calculate the cost from the beginning to this node
        public int calculateCost() {
            return calculateCost(parentNode);
        }

        public int calculateCost(PathNode parentNode) {
            if (parentNode == null) return 0;
            else {
                if (x == parentNode.x || y == parentNode.y)
                    return 1 + parentNode.calculateCost();
                else return 2 + parentNode.calculateCost();
            }
        }

        //We want nodes with the same position to be considered equal
        @Override
        public boolean equals(Object other) {
            if (other == null) return false;
            if (!(other instanceof PathNode)) return false;
            PathNode o = (PathNode) other;
            return x == o.x && y == o.y;
        }

        //Always override hash code whenever you change equals
        @Override
        public int hashCode() {
            return x*31 + y*17;
        }
    }

    protected ArrayList<PathNode> getPathTo(PathNode destination) {
        //Nodes to visit next
        ArrayList<PathNode> toVisit = new ArrayList<PathNode>();
        //Nodes that have been seen and should never be looked at again
        HashSet<PathNode> seen = new HashSet<PathNode>();

        PathNode current = new PathNode((int)getX(), (int)getY(), null);
        while (!current.equals(destination)) {
            //We never want to check this node again
            seen.add(current);

            //examine every neighbouring node
            for (int x=current.x-1; x<=current.x+1; x++) {
                for (int y=current.y-1; y<=current.y+1; y++) {
                    //If this position is blocked, don't consider it.
                    if (level.blocked(x,y)) continue;

                    PathNode currentNode = new PathNode(x, y, current);
                    //We don't consider items that we have already seen
                    if (seen.contains(currentNode)) continue;

                    //If we have already visited this node, see if this way is cheaper
                    if (toVisit.contains(currentNode)) {
                        PathNode oldNode = getNodeByPosition(x, y, toVisit);
                        //If going through our current path is cheaper, use it
                        if (oldNode.calculateCost() > oldNode.calculateCost(currentNode.parentNode)) {
                            toVisit.remove(oldNode);
                            toVisit.add(currentNode);
                        }
                    }
                    else toVisit.add(currentNode);
                }
            }

            if (toVisit.isEmpty()) break;
            current = findCheapestNode(toVisit);
            toVisit.remove(current);
        }

        return getPath(current);
    }

    private ArrayList<PathNode> getPath(PathNode end) {
        ArrayList<PathNode> path = new ArrayList<PathNode>();
        PathNode p = end;
        while(p != null) {
            path.add(p);
            p = p.parentNode;
        }

        Collections.reverse(path);

        return path;
    }

    private PathNode findCheapestNode(Iterable<PathNode> list) {
        PathNode node = null;
        int cost = Integer.MAX_VALUE;

        for (PathNode e : list) {
            int c = e.calculateCost();
            if (c < cost) {
                node = e;
                cost = c;
            }
        }

        return node;
    }

    private PathNode getNodeByPosition(int x, int y, Iterable<PathNode> list) {
        for (PathNode n : list) {
            if (n.x == x && n.y == y) return n;
        }
        return null;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (Gdx.input.isKeyPressed(Input.Keys.P))
            for (PathNode p : path) batch.draw(tex, p.x*32, p.y*32);
        super.render(batch);
    }
}
