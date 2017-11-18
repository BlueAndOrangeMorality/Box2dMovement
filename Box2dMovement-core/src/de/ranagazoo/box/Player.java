package de.ranagazoo.box;

import static de.ranagazoo.box.Config.TS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Player implements BoxEntity
{
  private Body playerBody;
  private Sprite playerSprite;

  public Player(Box2dMovement box2dMovement, float posX, float posY)
  {
//    BodyDef bodyDef = new BodyDef();
//    bodyDef.angularDamping = 5;
//    bodyDef.fixedRotation = false;
//    bodyDef.linearDamping = 5;
//    bodyDef.position.set(posX, posY);
//    bodyDef.type = BodyType.DynamicBody;
//
//    FixtureDef fixtureDef = new FixtureDef();
//    fixtureDef.density = 0.5f;
//    fixtureDef.friction = 0.4f;
//    fixtureDef.restitution = 0.1f;
//    fixtureDef.filter.categoryBits = CATEGORY_PLAYER;
//    fixtureDef.filter.maskBits = MASK_PLAYER;
//
//    PolygonShape shape = new PolygonShape();
//    shape.set(new float[]{-0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f});
//    fixtureDef.shape = shape;
//
    //playerBody = box2dMovement.getWorld().createBody(bodyDef);
    //playerBody.createFixture(fixtureDef);
    //shape.dispose();
    
    playerBody = box2dMovement.getWorld().createBody(box2dMovement.getBoxEntityFactory().getBodyDef(posX, posY));
    playerBody.createFixture(box2dMovement.getBoxEntityFactory().getFixtureDef());
    playerBody.setUserData("userData");


    playerSprite = new Sprite(box2dMovement.getEntityPlayerRegion());
    playerSprite.setSize(TS, TS);
    playerSprite.setOrigin(TS / 2, TS / 2);
  }

  public void move()
  {
    if (Gdx.input.isKeyPressed(Keys.A))
      playerBody.applyLinearImpulse(new Vector2(-1.2f, 0), playerBody.getPosition(), true);
    if (Gdx.input.isKeyPressed(Keys.D))
      playerBody.applyLinearImpulse(new Vector2(1.2f, 0), playerBody.getPosition(), true);
    if (Gdx.input.isKeyPressed(Keys.W))
      playerBody.applyLinearImpulse(new Vector2(0, 1.2f), playerBody.getPosition(), true);
    if (Gdx.input.isKeyPressed(Keys.S))
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
