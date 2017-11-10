package de.ranagazoo.box;

import static de.ranagazoo.box.Box2dBuilder.createBody;
import static de.ranagazoo.box.Box2dBuilder.createDynamicBodyDef;
import static de.ranagazoo.box.Box2dBuilder.createFixtureDef;
import static de.ranagazoo.box.Box2dBuilder.createPolygonShape;
import static de.ranagazoo.box.Box2dMovement.CATEGORY_PLAYER;
import static de.ranagazoo.box.Box2dMovement.MASK_PLAYER;
import static de.ranagazoo.box.Box2dMovement.TS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;


public class Player
{
  
  private Body playerBody;
  private Sprite playerSprite;
  
  public Player(World world, Vector2 position, TextureRegion entityPlayerRegion)
  {
    Shape      tempShape;
    FixtureDef tempFixtureDef = null;
    
    playerBody = createBody(world, createDynamicBodyDef(5, false, 5, new Vector2(16, 10)), "userData");

    tempShape = createPolygonShape(new float[]{-0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f});
    tempFixtureDef = createFixtureDef(0.5f, 0.4f, 0.1f, tempShape, CATEGORY_PLAYER, MASK_PLAYER);
    playerBody.createFixture(tempFixtureDef);
    tempShape.dispose();
    
    playerSprite = new Sprite(entityPlayerRegion);
    playerSprite.setSize(TS, TS);
    playerSprite.setOrigin(TS/2, TS/2);
  }
  
  
  public void move()
  {
    if(Gdx.input.isKeyPressed(Keys.A))
      playerBody.applyLinearImpulse(new Vector2(-1.2f, 0), playerBody.getPosition(), true);
    if(Gdx.input.isKeyPressed(Keys.D))
      playerBody.applyLinearImpulse(new Vector2(1.2f, 0), playerBody.getPosition(), true);
    if(Gdx.input.isKeyPressed(Keys.W))
      playerBody.applyLinearImpulse(new Vector2(0, 1.2f), playerBody.getPosition(), true);
    if(Gdx.input.isKeyPressed(Keys.S))
      playerBody.applyLinearImpulse(new Vector2(0, -1.2f), playerBody.getPosition(), true);
  }
  
  public void render(SpriteBatch batch)
  {
    playerSprite.setPosition((playerBody.getPosition().x - 0.5f) * TS, (playerBody.getPosition().y - 0.5f) * TS);
    playerSprite.setRotation(MathUtils.radiansToDegrees * playerBody.getAngle());
    playerSprite.draw(batch);
  }

  public Body getBody()
  {
    return this.playerBody;
  }
}
