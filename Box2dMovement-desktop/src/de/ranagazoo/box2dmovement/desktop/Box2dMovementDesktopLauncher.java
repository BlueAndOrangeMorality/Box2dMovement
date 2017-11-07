package de.ranagazoo.box2dmovement.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.ranagazoo.box2dmovement.Box2dMovement;

public class Box2dMovementDesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Box2dMovement";
		config.width = 1024;
		config.height = 768;
		new LwjglApplication(new Box2dMovement(), config);
	}
}
