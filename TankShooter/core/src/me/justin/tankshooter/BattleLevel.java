package me.justin.tankshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import me.justin.tankshooter.battletanks.JoshSucks;
import me.justin.tankshooter.battletanks.Justin;

/**
 * A level for ai battles
 */
public class BattleLevel extends BaseLevel {

    public BattleLevel(String level) {
        super(level);

        enemyTanks.add(new Justin(this, 0,0));
        enemyTanks.add(new JoshSucks(this, 0,0));

        for (BaseTank t : enemyTanks) {
            int x, y;
            do {
                x = (int) (Math.random() * mainLayer.getWidth());
                y = (int) (Math.random() * mainLayer.getHeight());
            } while (blocked(x,y));

            t.setX(x);
            t.setY(y);
        }

        camera.zoom = .5f;
   }

   public void doUpdate(float delta) {
       if (enemyTanks.size() == 1) this.state = LevelState.WON;

       float avgX=0, avgY=0;

       for (BaseTank t : enemyTanks) {
           t.update(delta);
           avgX += t.getX();
           avgY += t.getY();
       }

       avgX /= enemyTanks.size();
       avgY /= enemyTanks.size();
       camera.position.set(avgX*TILE_SIZE, avgY*TILE_SIZE, 0);

       //zoom out
       if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
           camera.zoom += delta*5;
           if (camera.zoom > 1.5f) camera.zoom = 1.5f;
       }

       //zoom in
       else if (Gdx.input.isKeyPressed(Input.Keys.X)) {
           camera.zoom -= delta*5;
           if (camera.zoom < .25f) camera.zoom = .25f;
       }

       //normal
       else if (camera.zoom > .8) {
           camera.zoom -= delta*5;
       }
       else if (camera.zoom < .7) {
           camera.zoom += delta*5;
       }
       else camera.zoom = .75f;
   }

   protected void renderSprites(SpriteBatch batch) {}

   protected void renderShapes(ShapeRenderer renderer) {}
}
