package de.ranagazoo.box;

import static de.ranagazoo.box.Config.CATEGORY_WAYPOINT;
import static de.ranagazoo.box.Config.MASK_WAYPOINT;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Waypoint
{
  private Body waypointBody;

  public Waypoint(Box2dMovement box2dMovement, float posX, float posY)
  {
    BodyDef bodyDef = new BodyDef();
    bodyDef.angularDamping = 2f;
    bodyDef.fixedRotation = false;
    bodyDef.linearDamping = 2f;
    bodyDef.position.set(posX, posY);

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.density = 0.2f;
    fixtureDef.friction = 0.4f;
    fixtureDef.restitution = 0.1f;
    fixtureDef.isSensor = true;
    fixtureDef.filter.categoryBits = CATEGORY_WAYPOINT;
    fixtureDef.filter.maskBits = MASK_WAYPOINT;
  
    CircleShape shape = new CircleShape();
    shape.setRadius(0.5f);
    fixtureDef.shape = shape;
    
    waypointBody = box2dMovement.getWorld().createBody(bodyDef);
    waypointBody.createFixture(fixtureDef);
    waypointBody.setUserData(box2dMovement.getWaypoints().size());
    
    shape.dispose();
  }

  public Vector2 getPosition()
  {
    return waypointBody.getPosition();
  }
}
