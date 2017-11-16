package de.ranagazoo.box;

import static de.ranagazoo.box.Config.CATEGORY_NONE;
import static de.ranagazoo.box.Config.CATEGORY_WAYPOINT;
import static de.ranagazoo.box.Config.MASK_NONE;
import static de.ranagazoo.box.Config.MASK_WAYPOINT;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;


public class Waypoint
{
  private Body waypointBody;
  
  public Waypoint(World world, float posX, float posY, int myIndexInTheArray)
  {
	CircleShape tempShape;
    FixtureDef tempFixtureDef = null;
    
    BodyDef bodyDef = new BodyDef();
    bodyDef.angularDamping = 2f;
    bodyDef.fixedRotation = false;
    bodyDef.linearDamping = 2f;
    bodyDef.position.set(posX, posY);
    
    waypointBody = world.createBody(bodyDef);
    waypointBody.setUserData(myIndexInTheArray);
    
    tempShape = new CircleShape();
    tempShape.setRadius(0.5f);
    
    tempFixtureDef = new FixtureDef();
    tempFixtureDef.density = 0.2f;
    tempFixtureDef.friction = 0.4f;
    tempFixtureDef.restitution = 0.1f;
    tempFixtureDef.shape = tempShape;
    
    tempFixtureDef.filter.categoryBits = CATEGORY_NONE;
    tempFixtureDef.filter.maskBits = MASK_NONE;
    
    tempFixtureDef.isSensor = true;
    tempFixtureDef.filter.categoryBits = CATEGORY_WAYPOINT;
    tempFixtureDef.filter.maskBits = MASK_WAYPOINT;
    waypointBody.createFixture(tempFixtureDef);
    tempShape.dispose();
    
  }
  
  public Vector2 getPosition()
  {
    return waypointBody.getPosition();
  }
}
