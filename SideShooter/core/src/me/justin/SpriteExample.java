package me.justin;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteExample extends ApplicationAdapter {

    private final static String prefix = "enemy-";
    private final static int NUMBER_FRAMES = 28;

    private TextureAtlas spritesheet;
    private Animation animation;
    private float animationTime = 0;

    private SpriteBatch batch;

    @Override
    public void create () {
        this.spritesheet = new TextureAtlas(Gdx.files.internal("sprites.pack"));

        TextureRegion[] frames = new TextureRegion[NUMBER_FRAMES];
        for (int i=0; i<NUMBER_FRAMES; i++) {
            frames[i] = spritesheet.findRegion(prefix + i);
        }
        this.animation = new Animation(1/30f, frames);

        this.batch = new SpriteBatch();
    }

    private void update() {
        animationTime+=Gdx.graphics.getDeltaTime();
    }

    @Override
    public void render () {
        update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        //takes time, looping parameters. We pass true so the anim loops
        TextureRegion frame = animation.getKeyFrame(animationTime, true);
        batch.draw(frame, 20,20);
        batch.end();
    }

}
