package de.ranagazoo.box;

import static de.ranagazoo.box.Box2dBuilder.createBody;
import static de.ranagazoo.box.Box2dBuilder.createFixtureDef;
import static de.ranagazoo.box.Box2dBuilder.createPolygonShape;
import static de.ranagazoo.box.Box2dBuilder.createStaticBodyDef;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;


public class Obstacle
{
  private Sprite bottomSprite; 
  private Body groundBody;
 
  public Obstacle(World world, Vector2 position, TextureRegion region)
  {
    Shape      tempShape;
    FixtureDef tempFixtureDef = null;

    groundBody = createBody(world, createStaticBodyDef(2, false, 2, position), "userData");
    
    tempShape = createPolygonShape(new float[]{-2f, -1f, 2f, -1f, 2f, 1f, -2f, 1f});
    tempFixtureDef = createFixtureDef(0.5f, 0.4f, 0.1f, tempShape, Box2dMovement.CATEGORY_SCENERY, Box2dMovement.MASK_SCENERY);
    groundBody.createFixture(tempFixtureDef);
    tempShape.dispose();
    
    bottomSprite = new Sprite(region);
    bottomSprite.setSize(320f, 64f);
    bottomSprite.setOrigin(16, 16);
  }
  
  
  public void render(SpriteBatch batch)
  {
    bottomSprite.setPosition((groundBody.getPosition().x - 5f) * 32, (groundBody.getPosition().y - 1f) * 32);
    //Nur ausgeblendet, nicht entfernen!
    //bottomSprite.draw(batch);     
  
  }
 
}
