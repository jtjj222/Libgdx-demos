package me.justin.gameui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * A demo to show how to make gui's using libgdx
 * NOTE: If you wanted to make a desktop app, use swing or javafx
 * If you wanted to make an android app, use the android ui tools
 * Scene2d is verbose and messy, but it is flexible and great for games
 * It is for cross-platform, skinnable game ui's
 */
public class Scene2dDemo extends ApplicationAdapter {

    private Stage stage; //Used to hold the UI elements

    //The squares to be drawn in the demo
    private int[] squares = {20};
    private ShapeRenderer renderer;

	@Override
	public void create () {
        renderer = new ShapeRenderer();

        stage = new Stage();
        HorizontalGroup row1 = new HorizontalGroup().space(10).pad(5).fill();
        HorizontalGroup row2 = new HorizontalGroup().space(10).pad(5).fill();
        VerticalGroup column = new VerticalGroup().space(10).pad(5);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json")); //Handles the look of our ui
        //You can customize this to fit your game. More on the libgdx github wiki

        //These are final so they can be used inside the anonymous inner class
        final TextField numSquares = new TextField("1", skin, "default");
        final TextButton nextBtn = new TextButton("Next", skin, "default");
        final Label squaresLabel = new Label("1 square", skin, "default");

        nextBtn.addListener(new ClickListener() {
            public void clicked (InputEvent event, float x, float y) {
                try {
                    int num = Integer.parseInt(numSquares.getText());
                    squaresLabel.setText("" + num + " squares");

                    squares = new int[num];
                    for (int i=0; i<squares.length; i++) {
                        squares[i] = 20 + 20*i;
                    }

                } catch (NumberFormatException e) {
                    squaresLabel.setText("Not a number");
                }
            }
        });
        row1.addActor(numSquares);
        row1.addActor(nextBtn);
        row2.addActor(squaresLabel);
        column.addActor(row1);
        column.addActor(row2);
        column.setPosition(Gdx.graphics.getWidth()/2f, column.getPrefHeight());

        stage.addActor(column);
        Gdx.input.setInputProcessor(stage); //Allow stage to handle input events
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime()); //Update everything, handle click events, etc..
        stage.draw(); //Draw to the screen

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int square : squares) renderer.rect(square, 200, 15, 15);
        renderer.end();
	}
}
