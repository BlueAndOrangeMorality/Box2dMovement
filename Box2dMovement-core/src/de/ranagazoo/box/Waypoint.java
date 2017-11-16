package de.ranagazoo.box;

import static de.ranagazoo.box.Box2dBuilder.createBody;
import static de.ranagazoo.box.Box2dBuilder.createCircleShape;
import static de.ranagazoo.box.Box2dBuilder.createFixtureDef;
import static de.ranagazoo.box.Box2dBuilder.createStaticBodyDef;
import static de.ranagazoo.box.Config.CATEGORY_NONE;
import static de.ranagazoo.box.Config.CATEGORY_WAYPOINT;
import static de.ranagazoo.box.Config.MASK_NONE;
import static de.ranagazoo.box.Config.MASK_WAYPOINT;
import static de.ranagazoo.box.Config.TS;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;


public class Waypoint
{
  private Body waypointBody;
  private Sprite waypointSprite;
  
  public Waypoint(World world, Vector2 position, TextureRegion entityTriangleRegion, int myIndexInTheArray)
  {
    Shape      tempShape;
    FixtureDef tempFixtureDef = null;
    
    waypointBody = createBody(world, createStaticBodyDef(2, false, 2, position), myIndexInTheArray);
    tempShape = createCircleShape(0.5f);
    tempFixtureDef = createFixtureDef(0.2f, 0.4f, 0.1f, tempShape, CATEGORY_NONE, MASK_NONE);
    tempFixtureDef.isSensor = true;
    tempFixtureDef.filter.categoryBits = CATEGORY_WAYPOINT;
    tempFixtureDef.filter.maskBits = MASK_WAYPOINT;
    waypointBody.createFixture(tempFixtureDef);
    tempShape.dispose();
    
    waypointSprite = new Sprite(entityTriangleRegion);
    waypointSprite.setSize(TS, TS);
    waypointSprite.setOrigin(TS/2, TS/2);
  }
  
  public Vector2 getPosition()
  {
    return waypointBody.getPosition();
  }
}
