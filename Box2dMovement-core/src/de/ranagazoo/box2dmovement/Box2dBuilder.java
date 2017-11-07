package de.ranagazoo.box2dmovement;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.JointDef.JointType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;


public class Box2dBuilder
{
  
  public static BodyDef createDynamicBodyDef(float angularDamping, boolean fixedRotation, float linearDamping, Vector2 position)
  {
    BodyDef bodyDef = createDefaultBodyDef(angularDamping, fixedRotation, linearDamping, position);
    bodyDef.type = BodyType.DynamicBody;
    return bodyDef;
  }
  
  public static BodyDef createDefaultBodyDef(float angularDamping, boolean fixedRotation, float linearDamping, Vector2 position)
  {
    BodyDef bodyDef = new BodyDef();
    bodyDef.angularDamping = angularDamping;
    bodyDef.fixedRotation = fixedRotation;
    bodyDef.linearDamping = linearDamping;
    bodyDef.position.set(position);
    return bodyDef;
  }
  
  public static FixtureDef createFixtureDef(float density,  float friction, float restitution, Shape shape)
  {
    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.density = density;
    fixtureDef.friction = friction;
    fixtureDef.restitution = restitution;  // Make it bounce a little bit
    fixtureDef.shape = shape;
    return fixtureDef;
  }
  
  public static FixtureDef createFixtureDef(float density,  float friction, float restitution, Shape shape, short categoryBits, short maskBits)
  {
    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.density = density;
    fixtureDef.friction = friction;
    fixtureDef.restitution = restitution;  // Make it bounce a little bit
    fixtureDef.shape = shape;
    fixtureDef.filter.categoryBits = categoryBits;
    fixtureDef.filter.maskBits = maskBits;
    return fixtureDef;
  }
  
  
  public static Body createBody(World world, BodyDef bodyDef, Object userData)
  {
    Body body = world.createBody(bodyDef);
    body.setUserData(userData);
    
    return body;
  }
  
  
  public static JointDef createDefaultJointDef(Body bodyA, Body bodyB, JointType type)
  {
    JointDef jointDef = new JointDef();
    jointDef.bodyA = bodyA;    
    jointDef.bodyB = bodyB;
    jointDef.type = type;
    return jointDef;
  }

  //Starre Achse zwischen zwei Objekten, möglicherweise auch drehbar 
  public static RevoluteJointDef createRevoluteJointDef(Body bodyA, Body bodyB, Vector2 localAnchorA, Vector2 localAnchorB, float lowerAngle)
  {
    RevoluteJointDef jointDef = new RevoluteJointDef();
    jointDef.bodyA = bodyA;    
    jointDef.bodyB = bodyB;
    jointDef.type = JointType.RevoluteJoint;
    jointDef.localAnchorA.set(localAnchorA);
    //jointDef.upperAngle
    jointDef.lowerAngle = lowerAngle;
    return jointDef;
  }
  
  public static FrictionJointDef createFrictionJointDef(Body bodyA, Body bodyB)
  {
    FrictionJointDef jointDef = new FrictionJointDef();
    jointDef.bodyA = bodyA;
    jointDef.bodyB = bodyB;
    jointDef.type = JointType.FrictionJoint;
    return jointDef;
  }
  
  // Einfach nur feste entfernung zwischen den Objekten, wie ein Uhrzeiger
  public static DistanceJointDef createDistanceJointDef(Body bodyA, Body bodyB, float length)
  {
    DistanceJointDef jointDef = new DistanceJointDef();
    jointDef.bodyA = bodyA;
    jointDef.bodyB = bodyB;
    jointDef.type = JointType.DistanceJoint;
    jointDef.length = length;
    return jointDef;
  }
  
  
  
  public static PolygonShape assignPolygonShape(float[] floatArray)
  {
    PolygonShape temp = new PolygonShape();
    temp.set(floatArray);
    return temp;
  }
  
  public static ChainShape assignChainShape(Vector2[] vectorArray)
  {
    ChainShape temp = new ChainShape();
    temp.createLoop(vectorArray);
    return temp;
  }
  
  public static CircleShape assignCircleShape(float radius)
  {
    CircleShape temp = new CircleShape();
    temp.setRadius(radius);
    return temp;
  }
}
