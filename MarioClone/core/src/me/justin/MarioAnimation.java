package me.justin;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.TimeUtils;

import me.justin.entities.Player;

public class MarioAnimation {

    private static final String SPRITE = "Mario";

    private float distanceWalked = 0;
    private int walkingFrame = 0;

    private float powerupTimer = 0;
    private String lastDir = "Right";

    private Player player;
    private TextureAtlas spritesheet;

    private TextureRegion currentKeyframe = null;

    public MarioAnimation(Player player, TextureAtlas spritesheet) {
        this.player = player;
        this.spritesheet = spritesheet;
    }

    public void updateAnimation(float delta) {
        String state = "";
        if (player.state == Player.PlayerState.WALKING && !player.turnaround) {
            distanceWalked += Math.abs(player.velocity.x*delta);
            if (distanceWalked > 5) {
                walkingFrame++;
                distanceWalked = 0;
                if (walkingFrame > 2) walkingFrame = 0;
            }
            state = "Walking" + walkingFrame;
        }
        else distanceWalked = 0;

        if (player.state == Player.PlayerState.FALLING
                || player.state == Player.PlayerState.REST) state = "Rest";
        else if (player.state == Player.PlayerState.JUMPING) state = "Jump";


        if (player.turnaround) state = "Turnaround";
        if (!player.alive) state = "Dead";

        String dir = "";
        if (player.velocity.x > 0) dir = "Right";
        else if (player.velocity.x < 0) dir = "Left";
        else dir = lastDir;

        lastDir = dir;

        String power = player.powerup.keyframeName;
        if (player.powerupAnim) {
            powerupTimer += delta;
            if (powerupTimer > 0.5) {
                powerupTimer = 0;
                player.powerupAnim = false;
                player.lastPowerup = null;
            }
            else {
                int time = (int) (powerupTimer*1000);
                if (time%2 == 0) power = player.lastPowerup.keyframeName;
            }
        }
        else powerupTimer = 0;

        String keyframeName = SPRITE + state + dir + power;

        this.currentKeyframe = spritesheet.findRegion(keyframeName);
        assert this.currentKeyframe != null : "Frame: " + keyframeName + " does not exist!";
    }

    public TextureRegion getKeyFrame() {
        return currentKeyframe;
    }
}
