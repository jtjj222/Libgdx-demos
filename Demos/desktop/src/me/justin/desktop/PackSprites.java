package me.justin.desktop;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import java.io.File;

public class PackSprites {
    public static void main (String[] arg) {
        // Make sure that you set the working directory of this run configuration
        // To your assets folder. Do this in eclipse by clicking the down arrow (beside the run button),
        // clicking run configurations, clicking the configuration for this class, clicking the
        // output tab, and clicking the workspace button in the working directory area.

        String inputFolder = "sprites";
        String outputFolder = ".";
        String fileName = "sprites.pack";

        //Delete the existing pack file
        if (!deleteFile(outputFolder + File.separator + "sprites.pack")) System.out.println("No pack file");
        if (!deleteFile(outputFolder + File.separator + "sprites.png")) System.out.println("No image file");

        //Create the spritesheet

        //If you get errors about not finding texturepacker, open your build.gradle file
        //at the root of your project, add:
        //compile "com.badlogicgames.gdx:gdx-tools:$gdxVersion"
        //inside the project(":desktop") portion after the other compile commands,
        //open a command prompt at the root of the project, run gradlew eclipse,
        //then delete and re-import the project in eclipse (without deleting project files).
        TexturePacker.process(inputFolder, outputFolder, fileName);
	}

    private static boolean deleteFile(String name) {
        File file = new File(name); //We use Java's built in file handling api
        if (file != null) return file.delete();
        else return false;
    }
}
