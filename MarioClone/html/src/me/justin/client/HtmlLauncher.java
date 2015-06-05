package me.justin.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.google.gwt.user.client.ui.TextArea;

import me.justin.MarioCloneGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                GwtApplicationConfiguration config = new GwtApplicationConfiguration(800, 640);
                return config;
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new MarioCloneGame();
        }
}