package me.justin.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import me.justin.FloppyBirdsGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
            GwtApplicationConfiguration config =  new GwtApplicationConfiguration(280, 500);
            return config;
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new FloppyBirdsGame();
        }
}