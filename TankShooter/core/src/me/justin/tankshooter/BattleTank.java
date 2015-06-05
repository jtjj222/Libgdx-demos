package me.justin.tankshooter;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class BattleTank extends BaseTank {

    public BattleTank(BattleLevel level, int x, int y) {
        super(level, "et", x, y);
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        level.font.setScale(0.5f);
        String text = getClass().getSimpleName();
        BitmapFont.TextBounds bounds = level.font.getBounds(text);
        level.font.draw(batch, text,
                getX()*BaseLevel.TILE_SIZE - bounds.width/2,
                getY()*BaseLevel.TILE_SIZE + TANK_SIZE/2 + bounds.height + 20);
    }
}
