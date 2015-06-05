package me.justin.levels;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

/**
 * A demo to show how to load a map into your game
 */
public class RenderMapDemo extends ApplicationAdapter {

    private TiledMap map; //Stores data about the map, such as tile properties
    private TiledMapRenderer mapRenderer; //Lets us render the map

    private OrthographicCamera camera; //We use this to handle where the viewport can see

	@Override
	public void create () {
        map = new TmxMapLoader().load("level.tmx"); //We use the TmxMapLoader helper class
        //This is done because there are multiple formats that you may want to load tile-based maps from
        mapRenderer = new OrthogonalTiledMapRenderer(map); //Can also do isometric

        //Takes the width and height of the viewport, in this case the screen
        //You can play with these values to see how it stretches to fill the screen
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 1/2f; //We want to zoom in. This should probably be called scale instead

        //We find the object (inside the layer named Objects) called player,
        //and use its position as our initial position. The next demo will use
        //this as a spawn point
        //This will throw an java.lang.NullPointerException if player doesn't exist,
        //or if the Objects layer doesn't exist, because each of these get methods
        //will return null if it doesn't find what its looking for
        MapObject player = map.getLayers().get("Objects").getObjects().get("Player");

        //We get the property x of the player object. Every object has x, y, width, height and type
        //properties in the tiled editor. Also, you can set your own properties from within the editor,
        //(such as whether a tile is solid or not), and get that value here.

        //get() takes two or three arguments:
        //a) get(name, default value, type of the property (usually string except for x and y))
        //b) get(name, type)
        camera.position.x = player.getProperties().get("x", 0f, Float.class);
        camera.position.y = player.getProperties().get("y", 0f, Float.class);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) camera.position.x -= 10;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) camera.position.x += 10;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) camera.position.y += 10;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) camera.position.y -= 10;

        camera.update(); //Updates the camera transformation matrix

        //Draw the map
        //If you want to draw characters, you would draw them after this
        mapRenderer.setView(camera); //Tell the map renderer to use the camera
        //Spritebatch has a similar method
        mapRenderer.render();
	}
}
