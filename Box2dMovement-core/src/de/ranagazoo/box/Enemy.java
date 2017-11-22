package de.ranagazoo.box;

import static de.ranagazoo.box.Config.CATEGORY_MONSTER;
import static de.ranagazoo.box.Config.CATEGORY_MSENSOR;
import static de.ranagazoo.box.Config.MASK_MONSTER;
import static de.ranagazoo.box.Config.MASK_MSENSOR;
import static de.ranagazoo.box.Config.TS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Enemy implements BoxEntity
{
  public static final int STATUS_IDLE = 1;
  public static final int STATUS_ATTACK = 2;
  public static final int STATUS_NEW_TARGET = 3;

  private Box2dMovement box2dMovement;
  private Body enemyBody;
  private int currentTargetIndex;
  private int currentStatus;

  private Animation<TextureRegion> animation;
  private float stateTime;

  public Enemy(Box2dMovement box2dMovement, int posX, int posY)
  {
    this.box2dMovement = box2dMovement;
    currentStatus = STATUS_IDLE;
    stateTime = 0f;

    BodyDef bodyDef = new BodyDef();
    bodyDef.angularDamping = 2;
    bodyDef.fixedRotation = false;
    bodyDef.linearDamping = 2;
    bodyDef.position.set(posX, posY);
    bodyDef.type = BodyType.DynamicBody;

    // Wedge Shape for the enemy's body
    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.density = 0.2f;
    fixtureDef.friction = 0.4f;
    fixtureDef.restitution = 0.1f;
    fixtureDef.filter.categoryBits = CATEGORY_MONSTER;
    fixtureDef.filter.maskBits = MASK_MONSTER;

    PolygonShape shape = new PolygonShape();
    shape.set(new float[]{-0.25f, -0.25f, 0, -1, 0.25f, -0.25f, 0.25f, 0.25f, -0.25f, 0.25f});
    fixtureDef.shape = shape;

    // Circle Shape for the enemy's sensor
    FixtureDef fixtureDefSensor = new FixtureDef();
    fixtureDefSensor.density = 0f;
    fixtureDefSensor.friction = 0.4f;
    fixtureDefSensor.restitution = 0.1f;
    fixtureDefSensor.filter.categoryBits = CATEGORY_MSENSOR;
    fixtureDefSensor.filter.maskBits = MASK_MSENSOR;
    fixtureDefSensor.isSensor = true;

    CircleShape shapeSensor = new CircleShape();
    shapeSensor.setRadius(3);
    fixtureDefSensor.shape = shapeSensor;

    enemyBody = box2dMovement.getWorld().createBody(bodyDef);
    enemyBody.createFixture(fixtureDef);
    enemyBody.createFixture(fixtureDefSensor);
    shape.dispose();
    shapeSensor.dispose();
    
    
    enemyBody.setUserData(this);

    animation = box2dMovement.getAnimation();
  }

  /*
   * Drei möglichkeiten, was in move passieren soll - ATTACK: Drehung und
   * bewegung zum Spieler - NEW_TARGET: Neuen Waypoint aussuchen und auf IDLE
   * wechseln - IDLE: Auf aktuellen Waypoint zulaufen
   */
  public void move()
  {
    if (currentStatus == STATUS_ATTACK)
    {
      moveToPosition(box2dMovement.getPlayerPosition());
    }
    else if (currentStatus == STATUS_NEW_TARGET)
    {
      currentTargetIndex = box2dMovement.getNextWaypointIndex(currentTargetIndex);
      // currentTargetIndex =
      // box2dMovement.getRandomWaypointIndex(currentTargetIndex);
      currentStatus = STATUS_IDLE;
    }
    else
    {
      moveToPosition(box2dMovement.getWaypoint(currentTargetIndex).getPosition());
    }
  }

  /*
   * Hier geschieht die Magie: Nach komplexen Formeln dreht das Enemy sich in
   * die Richtung der angegebenen Position und bewegt sich auf diese zu.
   */
  public void moveToPosition(Vector2 position)
  {

    // Berechnung findet folgendermaßen statt:
    // triangleBody.getAngle() gibt die aktuelle Rotation des Dreiecks in
    // Polarkoordinaten (Radial) an
    // Um die notwendige Rotation festzustellen, wird ein (normalisierter)
    // Differenzvektor zwischen dem Spieler und dem Dreieck errechnet
    Vector2 normalizedDifferenceVector = enemyBody.getPosition().sub(position).nor();
    //
    // Mittels atan2-Funktion, die den ArcusTangenz dieses Vektors ermittelt,
    // hat man die gewünschte Rotation (Radial)
    float normalizedDifferenceRotationRad = MathUtils.atan2(-normalizedDifferenceVector.x, normalizedDifferenceVector.y);
    //
    // Das dreht den Wert um 90° oder 180°
    // Leider weiß ich noch nicht, wass 0° eigentlich sind
    // float normalizedDifferenceRotationRad = MathUtils.atan2(
    // normalizedDifferenceVector.y, normalizedDifferenceVector.x );
    // float normalizedDifferenceRotationRad = MathUtils.atan2(
    // -normalizedDifferenceVector.y, -normalizedDifferenceVector.x );

    // Eine direkte Rotation mit Torque ist möglich, aber nicht sinnvoll, dies
    // übergeht die Physik
    // float nextAngle = triangleBody.getAngle() +
    // triangleBody.getAngularVelocity() / 60.0f;
    // float totalRotation = normalizedDifferenceRotationRad - nextAngle;//use
    // angle in next time step
    // if(totalRotation < -180 * MathUtils.degreesToRadians) totalRotation +=
    // 360*MathUtils.degreesToRadians;
    // if(totalRotation > 180 * MathUtils.degreesToRadians) totalRotation -=
    // 360*MathUtils.degreesToRadians;
    // triangleBody.applyTorque((totalRotation < 0 ? -10 : 10), true);

    // Alternativ Schönere Variante, berücksichtigt Geschwindigkeit und Masse
    //
    // nextAngle erhält nicht nur die aktuelle Rotation sondern auch die
    // aktuelle Rotationsgeschwindigkeit
    float nextAngle = enemyBody.getAngle() + enemyBody.getAngularVelocity() / 60.0f;
    //
    // dies wird von dr gewünschten Rotation abgezogen, so dreht sich das
    // Dreieck bei jedem render hin und her, bis die Rotation korrekt ist
    float totalRotation = normalizedDifferenceRotationRad - nextAngle;
    //
    // Hier wird dafür gesorgt, dass sich das Dreick nicht bis unendlich dreht,
    // sondern im 360°-Bereich bleibt
    while (totalRotation < -180 * MathUtils.degreesToRadians)
      totalRotation += 360 * MathUtils.degreesToRadians;
    while (totalRotation > 180 * MathUtils.degreesToRadians)
      totalRotation -= 360 * MathUtils.degreesToRadians;
    //
    // Drehgeschwindigkeit, 60 ist quasi direkt auf dem Spieler, kleinere Werte
    // (0.1f) sorgen für langsamere Drehung, wie ein Kompass
    // Achtung, vergößert wegen kleinerem Körper, also weniger masse
    float desiredAngularVelocity = totalRotation * 120;
    //
    // Angabe, um wieviel Grad pro timestep rotiert werden darf (aktuell -20° -
    // +20°)
    // Sorgt auch für unterschiedliche Drehgeschwindigkeit, in Anhängigkeit der
    // Masse des Dreiecks
    // Achtung, vergößert wegen kleinerem Körper, also weniger masse
    float change = 90 * MathUtils.degreesToRadians;
    desiredAngularVelocity = Math.min(change, Math.max(-change, desiredAngularVelocity));
    float impulse = enemyBody.getInertia() * desiredAngularVelocity;
    //
    // if(Gdx.input.isKeyPressed(Keys.R))

    // Ehemals if, jetzt else
    // Drehung
    enemyBody.applyAngularImpulse(impulse, true);
    // Aktuell manuell angetriggerte Verfolgung
    Vector2 achtMalVier = position.cpy().sub(enemyBody.getPosition()).nor().cpy().scl(0.01f);
    enemyBody.applyLinearImpulse(achtMalVier, enemyBody.getPosition(), true);
    // Ehemals if, jetzt else

  }

  public void render(SpriteBatch batch)
  {
    stateTime += Gdx.graphics.getDeltaTime();
    TextureRegion currentFrame = (TextureRegion) animation.getKeyFrame(stateTime, true);
    Sprite s = new Sprite(currentFrame);
    s.setPosition((enemyBody.getPosition().x - 1) * TS, (enemyBody.getPosition().y - 1) * TS);
    s.setRotation(MathUtils.radiansToDegrees * enemyBody.getAngle() + 180);
    s.draw(batch);
  }

  public void setCurrentTargetId(int currentTargetId)
  {
    this.currentTargetIndex = currentTargetId;
  }

  public int getCurrentTargetIndex()
  {
    return currentTargetIndex;
  }

  public void setStatus(int status)
  {
    this.currentStatus = status;
  }
}
