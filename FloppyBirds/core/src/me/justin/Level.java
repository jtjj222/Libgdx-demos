package me.justin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public class Level implements Disposable {

    public final FloppyBird bird;
    private final Texture background, ground;
    public final TextureRegion pipe;
    private BitmapFont font;
    private SpriteBatch spriteBatch;

    public OrthographicCamera camera =
            new OrthographicCamera(Gdx.graphics.getWidth()/Math.max(1, Gdx.graphics.getDensity()),
                Gdx.graphics.getHeight()/Math.max(1, Gdx.graphics.getDensity()));

    public float[] pipeHeights = {150};
    public float pipeDisance = 150;
    public float pipeOffset = 250;
    public float pipeGap = 80;
    private int pipeRenderPadding = 4; //extra pipes on each side to draw

    private int score = 0;

    public TextureAtlas spriteAtlas;
    public int groundHeight;

    public Level() {
        this.font = new BitmapFont(Gdx.files.internal("font.fnt"));
        this.spriteAtlas = new TextureAtlas(Gdx.files.internal("sprites.pack"));
        this.pipe = spriteAtlas.findRegion("pipe_green");
        this.background = new Texture(Gdx.files.internal("bg_dark.png"));
        this.ground = new Texture(Gdx.files.internal("ground.png"));

        this.spriteBatch = new SpriteBatch();
        this.bird = new FloppyBird(this);

        groundHeight = ground.getHeight();
    }

    public void update() {
        if (!bird.isAlive) return;

        bird.update();
        //Camera position is center of screen
        //We give 1/5th of the screen to the left of the bird
        camera.position.set(bird.x + camera.viewportWidth/5f, camera.viewportHeight/2f, 0);
        camera.update();

        if (bird.x >= score*(pipeDisance + pipe.getRegionWidth())
                + pipeOffset + pipe.getRegionWidth()/2f) {
            score++;
        }

        //We are too close to the end
        if (bird.x >= (getPipesLength() - 5) *(pipeDisance + pipe.getRegionWidth())
                + pipeOffset + pipe.getRegionWidth()/2f) {
            float[] old = pipeHeights;
            pipeHeights = new float[old.length * 2];
            for (int i=0; i<old.length; i++) pipeHeights[i] = old[i];
            for (int i=old.length; i<pipeHeights.length; i++) {
                pipeHeights[i] = (float) (Math.random()*
                        (camera.viewportHeight-groundHeight - pipeGap - 40 - 40)) + 40;

                if (pipeHeights[i] < 40) pipeHeights[i] = 40;
                if (pipeHeights[i] > 250) pipeHeights[i] = 250;
            }
        }
    }

    public void render() {
        Gdx.gl.glClearColor(0, 135/255f, 147/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.draw(background, camera.position.x - camera.viewportWidth/2f,0,
                camera.viewportWidth, camera.viewportHeight);
        renderPipes();
        renderGround();
        bird.render(spriteBatch);
        renderScore();
        if (!bird.isAlive) renderDeath();
        spriteBatch.end();
    }

    public int getScore() {
        return score;
    }

    private void renderDeath() {
        String text = "You Lost";
        BitmapFont.TextBounds b = font.getBounds(text);
        font.draw(spriteBatch, text, camera.position.x - b.width/2f,
                camera.viewportHeight/2f);

        text = "Tap to play again";
        BitmapFont.TextBounds b2 = font.getBounds(text);
        font.draw(spriteBatch, text, camera.position.x - b2.width/2f,
                camera.viewportHeight/2f - b.height - 10);
    }

    private void renderScore() {
        BitmapFont.TextBounds b = font.getBounds(""+getScore());
        font.draw(spriteBatch, ""+getScore(), camera.position.x - b.width/2f,
                camera.viewportHeight - 20);
    }

    private void renderGround() {
        float leftX = camera.position.x - (bird.x%ground.getWidth()) - camera.viewportWidth/2f;

        for (float x = leftX; x <= camera.position.x + camera.viewportWidth; x+=ground.getWidth()) {
            spriteBatch.draw(ground, x, 0);
        }
    }

    public int getCurrentPipe() {
        return (int) ((bird.x - pipeOffset) / (pipeDisance + pipe.getRegionWidth()));
    }

    public float getPipeHeight(int pipe) {
        return pipeHeights[pipe] + groundHeight;
    }

    public int getPipesLength() {
        return pipeHeights.length;
    }

    private void renderPipes() {
        int currentPipe = getCurrentPipe();

        for (int p = currentPipe - pipeRenderPadding; p <= currentPipe+pipeRenderPadding; p++) {
            if (p >= pipeHeights.length) continue;
            if (p < 0) continue;

            spriteBatch.draw(pipe,
                    p*(pipeDisance + pipe.getRegionWidth()) + pipeOffset,
                    getPipeHeight(p) - pipe.getRegionHeight());
            spriteBatch.draw(pipe,
                    p*(pipeDisance + pipe.getRegionWidth()) + pipeOffset,
                    getPipeHeight(p) + pipeGap + pipe.getRegionHeight(),
                    0, 0,
                    pipe.getRegionWidth(),
                    pipe.getRegionHeight(),
                    1, -1, 0);
        }
    }

    @Override
    public void dispose() {
        this.spriteAtlas.dispose();
        this.background.dispose();
    }
}
