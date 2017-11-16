package de.ranagazoo.box;

import static de.ranagazoo.box.Config.CATEGORY_PLAYER;
import static de.ranagazoo.box.Config.MASK_PLAYER;
import static de.ranagazoo.box.Config.TS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;


public class Player implements BoxEntity
{
  private Body playerBody;
  private Sprite playerSprite;
  
  public Player(Box2dMovement box2dMovement, float posX, float posY)
  {
	PolygonShape tempShape;
    FixtureDef tempFixtureDef = null;
    
    BodyDef bodyDef = new BodyDef();
    bodyDef.angularDamping = 5;
    bodyDef.fixedRotation = false;
    bodyDef.linearDamping = 5;
    bodyDef.position.set(posX, posY);
    bodyDef.type = BodyType.DynamicBody;
    
    playerBody = box2dMovement.getWorld().createBody(bodyDef);
    playerBody.setUserData("userData");

    tempShape= new PolygonShape();
    tempShape.set(new float[]{-0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f});
    
    tempFixtureDef = new FixtureDef();
    tempFixtureDef.density = 0.5f;
    tempFixtureDef.friction = 0.4f;
    tempFixtureDef.restitution = 0.1f;
    tempFixtureDef.shape = tempShape;
    tempFixtureDef.filter.categoryBits = CATEGORY_PLAYER;
    tempFixtureDef.filter.maskBits = MASK_PLAYER;
    
    playerBody.createFixture(tempFixtureDef);
    
    tempShape.dispose();
    
    playerSprite = new Sprite(box2dMovement.getEntityPlayerRegion());
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
