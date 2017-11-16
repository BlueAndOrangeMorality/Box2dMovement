package de.ranagazoo.box;

import static de.ranagazoo.box.Config.TS;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;


public class Obstacle implements BoxEntity
{
  private Sprite bottomSprite; 
  private Body groundBody;
 
  public Obstacle(Box2dMovement box2dMovement, float posX, float posY, TextureRegion region)
  {
	PolygonShape tempShape;
    FixtureDef fixtureDef;
    
    BodyDef bodyDef = new BodyDef();
    bodyDef.angularDamping = 2;
    bodyDef.fixedRotation = false;
    bodyDef.linearDamping = 2;
    bodyDef.position.set(posX, posY);
    
    groundBody = box2dMovement.getWorld().createBody(bodyDef);
    groundBody.setUserData("userData");
    
    tempShape = new PolygonShape();
    tempShape.set(new float[]{-2f, -1f, 2f, -1f, 2f, 1f, -2f, 1f});
    
    fixtureDef = new FixtureDef();
    fixtureDef.density = 0.5f;
    fixtureDef.friction = 0.4f;
    fixtureDef.restitution = 0.1f;
    fixtureDef.shape = tempShape;
    
    fixtureDef.filter.categoryBits = Config.CATEGORY_SCENERY;
    fixtureDef.filter.maskBits = Config.MASK_SCENERY;
    
    groundBody.createFixture(fixtureDef);
    tempShape.dispose();
    
    bottomSprite = new Sprite(region);
    bottomSprite.setSize(4f*TS, 2f*TS);
    bottomSprite.setOrigin(TS/2, TS/2);
  }
  
  
  public void render(SpriteBatch batch)
  {
    bottomSprite.setPosition((groundBody.getPosition().x - 2f) * TS, (groundBody.getPosition().y - 1f) * TS);
    bottomSprite.draw(batch);     
  }


  @Override
  public void move() {}
 
}
