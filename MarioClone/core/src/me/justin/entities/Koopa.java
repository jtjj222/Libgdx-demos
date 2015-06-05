package me.justin.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

import me.justin.BoundingRectangle;
import me.justin.DynamicEntity;
import me.justin.Entity;
import me.justin.Level;

public class Koopa extends DynamicEntity {

    protected Animation koopaAnim, parakoopaAnim;
    protected TextureRegion shell1, shell2;
    protected boolean kicked = false;
    protected float animState = 0;

    protected int paraMovement = 26;
    protected final float originalY;

    public KoopaState state = KoopaState.KOOPA;

    public static enum KoopaState {
        KOOPA, PARAKOOPA, SHELL
    }

    public Koopa(Level level, Vector2 position, MapProperties properties) {
        super(level, position, properties);

        if (properties != null && properties.get("state", "koopa", String.class).equalsIgnoreCase("parakoopa")) {
            state = KoopaState.PARAKOOPA;
        }

        originalY = position.y;

        koopaAnim = new Animation(1/10f, level.spritesheet.findRegion("Koopa1"),
                level.spritesheet.findRegion("Koopa2"));
        parakoopaAnim = new Animation(1/10f, level.spritesheet.findRegion("ParaKoopa1"),
                level.spritesheet.findRegion("ParaKoopa2"));
        shell1 = level.spritesheet.findRegion("KoopaShell1");
        shell2 = level.spritesheet.findRegion("KoopaShell2");

        boundingBox.set(1, 1, koopaAnim.getKeyFrame(0).getRegionWidth()-1, koopaAnim.getKeyFrame(0).getRegionHeight()-1);
        updateBoundingBox();

        isSolid = false;
        if (state == KoopaState.KOOPA) velocity.x = -40;
        else velocity.x = 0;

        if (state == KoopaState.KOOPA) velocity.y = -120;
        else velocity.y = 40;
    }

    @Override
    public void update(float delta) {
        animState += delta;
        if (Math.abs(position.x - level.player.position.x) > 500) return;

        if (state == KoopaState.PARAKOOPA) {
            if (position.y > originalY + paraMovement) velocity.y = -40;
            else if (position.y < originalY - paraMovement) velocity.y = 40;
        }

        super.doMovementOrCollision(velocity.x * delta, velocity.y*delta, delta);
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion tex;
        switch (state) {
            case KOOPA:
                velocity.y = -120;
                tex = koopaAnim.getKeyFrame(animState, true);
                break;
            case PARAKOOPA:
                tex = parakoopaAnim.getKeyFrame(animState, true);
                break;
            case SHELL:
                velocity.y = -120;
                tex = kicked? shell2 : shell1;
                break;
            default: tex = null;
        }

        batch.draw(tex, position.x, position.y, tex.getRegionWidth()/2f, 0,
                tex.getRegionWidth(), tex.getRegionHeight(),
                Math.signum(velocity.x) > 0? -1 : 1, 1, 0);
    }

    public void onStomp() {
        if (state == KoopaState.PARAKOOPA) state = KoopaState.KOOPA;
        else if (state == KoopaState.KOOPA) state = KoopaState.SHELL;
        else {
            if (kicked) {
                velocity.x = 0;
                kicked = false;
            }
            else onKick();
            return;
        }

        if (state == KoopaState.KOOPA) velocity.x = -40;
        else velocity.x = 0;

        if (state == KoopaState.KOOPA) velocity.y = -120;
        else velocity.y = 0;
    }

    public void onKick() {
        if (state != KoopaState.SHELL) return;

        velocity.x = Math.signum(level.player.velocity.x) * 150;
        kicked = true;
    }

    @Override
    protected void onSolidEntityCollisionX(Entity e, float delta) {
        velocity.x = -velocity.x;
    }

    @Override
    protected void onTileCollisionX(BoundingRectangle r, float delta) {
        velocity.x = -velocity.x;
    }

    @Override
    protected void onEntityCollision(Entity e, float delta) {
        if (e instanceof KoopaStop && state != KoopaState.SHELL) velocity.x =
                Math.signum((position.x + boundingBox.width/2f)
                - (e.position.x + e.boundingBox.width/2f)) * Math.abs(velocity.x);

        if (state == KoopaState.SHELL) e.alive = false; //TODO die method
    }
}
