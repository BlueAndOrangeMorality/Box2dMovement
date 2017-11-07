package de.ranagazoo.box2dmovement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;



public class Box2dContactListener implements ContactListener
{
  short aCatBits, bCatBits;

  @Override
  public void beginContact(Contact contact)
  {
//    
//    if(contact.getFixtureA().getFilterData().categoryBits == Box2dMovement.CATEGORY_PLAYER 
//    && contact.getFixtureB().getFilterData().categoryBits == Box2dMovement.CATEGORY_MONSTER)
//    {
//      Sprite triangleSprite = (Sprite)contact.getFixtureB().getBody().getUserData();
//      triangleSprite.setColor(255, 0, 0, 1);
////            Gdx.app.log("Aaaa", "Aua");
//      
//      
//    }
//    
////    Integer entityIdA = (Integer)contact.getFixtureA().getBody().getUserData();
////    Integer entityIdB = (Integer)contact.getFixtureB().getBody().getUserData();
//    
  }

  @Override
  public void endContact(Contact contact)
  {
//    if(contact.getFixtureA().getFilterData().categoryBits == Box2dMovement.CATEGORY_PLAYER 
//    && contact.getFixtureB().getFilterData().categoryBits == Box2dMovement.CATEGORY_MONSTER)
//    {
//      Sprite triangleSprite = (Sprite)contact.getFixtureB().getBody().getUserData();
//      triangleSprite.setColor(0, 255, 0, 1);
//    }
  }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse)
  {
    // TODO Auto-generated method stub
    
  }

}
