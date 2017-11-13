package de.ranagazoo.box;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface BoxEntity {

	public void move(Box2dMovement box2dMovement);
	public void render(SpriteBatch batch);
}
