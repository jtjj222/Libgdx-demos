package tools;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class PackSprites {

    public static void main(String... args) {
        TexturePacker.process("sprites", "android/assets", "spritesheet.atlas");
    }
}
