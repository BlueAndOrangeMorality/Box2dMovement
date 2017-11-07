
package de.ranagazoo.box2dmovement;

import static de.ranagazoo.box2dmovement.Box2dBuilder.*;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.*;

public class Box2dMovement
    extends ApplicationAdapter
{

  public static final short CATEGORY_PLAYER = 0x0001; // 0000000000000001 in binary
  public static final short CATEGORY_MONSTER = 0x0002; // 0000000000000010 in binary
  public static final short CATEGORY_SCENERY = 0x0004; // 0000000000000100 in binary
  public static final short CATEGORY_NONE = 0x0032; // 0000000000100000 in binary ???
  public static final short MASK_PLAYER = CATEGORY_MONSTER | CATEGORY_SCENERY; // or
                                                                               // ~CATEGORY_PLAYER
  public static final short MASK_MONSTER = CATEGORY_PLAYER | CATEGORY_SCENERY; // or
                                                                               // ~CATEGORY_MONSTER
  public static final short MASK_SCENERY = -1;
  public static final short MASK_NONE = CATEGORY_SCENERY;

  // next
  // http://www.iforce2d.net/b2dtut/rotate-to-angle
  // http://www.iforce2d.net/b2dtut/sensors
  // http://www.iforce2d.net/b2dtut/constant-speed

  private OrthographicCamera cameraSprites;
  private OrthographicCamera cameraBox2dDebug;
  private SpriteBatch batch;
  private Texture libgdxTexture, entitiesBigTexture;
  private Sprite playerSprite, bottomSprite, triangleSprite;
  private ShapeRenderer shapeRenderer;
  private World world;
  private Box2DDebugRenderer debugRenderer;

  public static final int TS = 32;

  Body dynamicBody, secondDynBody, thirdDynBody, groundBody, triangleBody, borderBody;
  RevoluteJoint drehgelenk1, drehgelenk2;
  FrictionJoint frictionJoint2;
  DistanceJoint distanceJoint2;

  // nur für Debugtest, verwendet auch Spritebatch
  private BitmapFont font;

  @Override
  public void create()
  {
    world = new World(new Vector2(0, 0), true);
    world.setContactListener(new Box2dContactListener());

    debugRenderer = new Box2DDebugRenderer();

    cameraSprites = new OrthographicCamera();
    cameraSprites.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    cameraSprites.update();

    cameraBox2dDebug = new OrthographicCamera();
    cameraBox2dDebug.setToOrtho(false, Gdx.graphics.getWidth() / TS, Gdx.graphics.getHeight() / TS);
    cameraBox2dDebug.update();

    // nur für Debugtest, verwendet auch Spritebatch
    font = new BitmapFont(Gdx.files.internal("fonts/vdj8pxwhite.fnt"), false);

    batch = new SpriteBatch();

    libgdxTexture = new Texture(Gdx.files.internal("data/libgdx.png"));
    libgdxTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

    TextureRegion region = new TextureRegion(libgdxTexture, 0, 0, 512, 275);

    entitiesBigTexture = new Texture(Gdx.files.internal("data/entities-big.png"));
    entitiesBigTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    TextureRegion entityTriangleRegion = new TextureRegion(entitiesBigTexture, 1 * TS, 6 * TS, TS, TS);
    TextureRegion entityPlayerRegion = new TextureRegion(entitiesBigTexture, 8 * TS, 1 * TS, TS, TS);

    playerSprite = new Sprite(entityPlayerRegion);
    playerSprite.setSize(TS, TS);
    playerSprite.setOrigin(TS / 2, TS / 2);

    bottomSprite = new Sprite(region);
    bottomSprite.setSize(320f, 64f);
    bottomSprite.setOrigin(16, 16);

    triangleSprite = new Sprite(entityTriangleRegion);
    triangleSprite.setSize(TS, TS);
    triangleSprite.setOrigin(TS / 2, TS / 2);

    // BodyDef tempBodyDef = null;
    Shape tempShape;
    FixtureDef tempFixtureDef = null;

    shapeRenderer = new ShapeRenderer();
    // Bei allen Objekten gilt:

    // createBody kriegt ein vordefiniertes bodyDef, da wir das mit createDynamicBodyDef
    // vordefinieren, übergeben wir es direkt.
    // wenn doch noch zusatzeinstellungen gemacht werden sollen, kann man erst ein tempBodyDef
    // erzeugen, weitere Variablen belegen,
    // dann erst übergeben

    // Jedes Shape für ein fixture muss an das fixture übergeben werden, das fixture wiederum an
    // Body,
    // das shape MUSS dann disposed werden, aber erst NACHDEM das fixture am Body erzeugt wurde.
    // Kniffelig

    // UserData ist ein beliebiges Object, dass an den Body übergeben werden kann, und zum Beispiel
    // im ContactListener abgefragt wird

    // -------------- dynamic ----------------------

    dynamicBody = createBody(world, createDynamicBodyDef(0, false, 0, new Vector2(16, 18)), "userData");

    tempShape = assignPolygonShape(new float[]{-0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f});
    tempFixtureDef = createFixtureDef(0.5f, 0.4f, 0.1f, tempShape, CATEGORY_PLAYER, MASK_PLAYER);
    dynamicBody.createFixture(tempFixtureDef);
    tempShape.dispose();

    // ------------------------- Zweiter Dynamic für Joints

    secondDynBody = createBody(world, createDynamicBodyDef(0, false, 0, new Vector2(19, 15)), "userData");

    tempShape = assignPolygonShape(new float[]{-0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f});
    tempFixtureDef = createFixtureDef(0.1f, 0.4f, 0.1f, tempShape, CATEGORY_PLAYER, MASK_PLAYER);
    secondDynBody.createFixture(tempFixtureDef);
    tempShape.dispose();

    // ------------------------- Dritter Dynamic für Joints

    thirdDynBody = createBody(world, createDynamicBodyDef(0, false, 0, new Vector2(14, 15)), "userData");

    tempShape = assignPolygonShape(new float[]{-0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f});
    tempFixtureDef = createFixtureDef(0.1f, 0.4f, 0.1f, tempShape, CATEGORY_PLAYER, MASK_PLAYER);
    thirdDynBody.createFixture(tempFixtureDef);
    tempShape.dispose();

    // --------------------------------- mein triangle ---------------------------------

    triangleBody = createBody(world, createDynamicBodyDef(2, false, 2, new Vector2(16.1f, 20)), triangleSprite);

    tempShape = assignPolygonShape(new float[]{-0.5f, -0.5f, 0, -2, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f});
    tempFixtureDef = createFixtureDef(0.2f, 0.4f, 0.1f, tempShape, CATEGORY_MONSTER, MASK_MONSTER);
    triangleBody.createFixture(tempFixtureDef);
    tempShape.dispose();

    // Triangle sensor, einfach nur ein neues shape, neues fixturedef, an den bestehenden Body
    // gekleistert
    // tempShape = assignPolygonShape(new float[]{-2,-1, 2,-1, 2,3});
    tempShape = Box2dBuilder.assignCircleShape(3);
    tempFixtureDef = createFixtureDef(0, 0.4f, 0.1f, tempShape, CATEGORY_MONSTER, MASK_MONSTER);
    tempFixtureDef.isSensor = true;
    triangleBody.createFixture(tempFixtureDef);
    tempShape.dispose();

    // ------------------- static

    groundBody = createBody(world, createDefaultBodyDef(2, false, 2, new Vector2(16f, 2)), "userData");

    tempShape = assignPolygonShape(new float[]{-5f, -1f, 5f, -1f, 5f, 1f, -5f, 1f});
    tempFixtureDef = createFixtureDef(0.5f, 0.4f, 0.1f, tempShape, CATEGORY_SCENERY, MASK_SCENERY);
    groundBody.createFixture(tempFixtureDef);
    tempShape.dispose();

    // ---------- border

    borderBody = createBody(world, createDefaultBodyDef(2, false, 2, new Vector2(16, 2)), "userData");

    tempShape = assignChainShape(new Vector2[]{new Vector2(-15f, -1f), new Vector2(15f, -1f), new Vector2(15f, 21f), new Vector2(-15f, 21f)});
    tempFixtureDef = createFixtureDef(0.0f, 0.4f, 0.1f, tempShape, CATEGORY_SCENERY, MASK_SCENERY);
    borderBody.createFixture(tempFixtureDef);
    tempShape.dispose();

    // ==============================================================================================================
    // Joints
    // ==============================================================================================================

    // drehgelenke
    drehgelenk1 = (RevoluteJoint)world.createJoint(createRevoluteJointDef(dynamicBody, secondDynBody, new Vector2(-1f, -0.75f), new Vector2(0f, 0f), (-0.5f * 3.14f)));
    drehgelenk2 = (RevoluteJoint)world.createJoint(createRevoluteJointDef(dynamicBody, thirdDynBody, new Vector2(1f, -0.75f), new Vector2(0f, 0f), (-0.5f * 3.14f)));
    // frictionJoint2 = (FrictionJoint)world.createJoint(createFrictionJointDef(dynamicBody,
    // thirdDynBody));
    // distanceJoint2 = (DistanceJoint)world.createJoint(createDistanceJointDef(dynamicBody,
    // thirdDynBody, 2));

    drehgelenk1.setMotorSpeed(2);
    drehgelenk1.enableMotor(true);
    drehgelenk1.setMaxMotorTorque(10.0f);
    drehgelenk2.setMotorSpeed(2);
    drehgelenk2.enableMotor(true);
    drehgelenk2.setMaxMotorTorque(10.0f);
  }

  @Override
  public void render()
  {
    if(Gdx.input.isKeyPressed(Keys.ESCAPE))
      Gdx.app.exit();
    Gdx.gl.glClearColor(0, 0, 0, 0);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    if(Gdx.input.isKeyPressed(Keys.A))
      dynamicBody.applyLinearImpulse(new Vector2(-0.2f, 0), dynamicBody.getPosition(), true);
    if(Gdx.input.isKeyPressed(Keys.D))
      dynamicBody.applyLinearImpulse(new Vector2(0.2f, 0), dynamicBody.getPosition(), true);
    if(Gdx.input.isKeyPressed(Keys.W))
      dynamicBody.applyLinearImpulse(new Vector2(0, 0.6f), dynamicBody.getPosition(), true);
    if(Gdx.input.isKeyPressed(Keys.S))
      dynamicBody.applyLinearImpulse(new Vector2(0, -0.2f), dynamicBody.getPosition(), true);

    if(Gdx.input.isKeyPressed(Keys.LEFT))
      triangleBody.applyLinearImpulse(new Vector2(-0.2f, 0), triangleBody.getPosition(), true);
    if(Gdx.input.isKeyPressed(Keys.RIGHT))
      triangleBody.applyLinearImpulse(new Vector2(0.2f, 0), triangleBody.getPosition(), true);
    if(Gdx.input.isKeyPressed(Keys.UP))
      triangleBody.applyLinearImpulse(new Vector2(0, 0.6f), triangleBody.getPosition(), true);
    if(Gdx.input.isKeyPressed(Keys.DOWN))
      triangleBody.applyLinearImpulse(new Vector2(0, -0.2f), triangleBody.getPosition(), true);

    // if(Gdx.input.isKeyPressed(Keys.J))
    // triangleBody.applyTorque(1, true);;
    // if(Gdx.input.isKeyPressed(Keys.K))
    // triangleBody.applyTorque(-1, true);;

    // aktuelle Rotation des Dreieicks
    float currentTriangleRotationDeg = (MathUtils.radiansToDegrees * triangleBody.getAngle());

    // normalizedDifferenceVector: Differenzvector: dynamicBody->triangleBody, normalisiert
    Vector2 normalizedDifferenceVector = triangleBody.getPosition().sub(dynamicBody.getPosition()).nor();
    // atan aus normalizedDifferenceVector mit -y, warum auch immer, ergibt rad-Wert,
    float normalizedDifferenceRotationRad = MathUtils.atan2(-normalizedDifferenceVector.x, normalizedDifferenceVector.y);

    // Das dreht den wert um 90°
    // float normalizedDifferenceRotationRad = MathUtils.atan2( normalizedDifferenceVector.y,
    // normalizedDifferenceVector.x );

    // if(normalizedDifferenceRotationRad < -180 * MathUtils.degreesToRadians)
    // normalizedDifferenceRotationRad += 360*MathUtils.degreesToRadians;
    // if(normalizedDifferenceRotationRad > 180 * MathUtils.degreesToRadians)
    // normalizedDifferenceRotationRad -= 360*MathUtils.degreesToRadians;
    //

    // float nextAngle = triangleBody.getAngle() + triangleBody.getAngularVelocity() / 60.0f;
    // float totalRotation = normalizedDifferenceRotationRad - nextAngle;//use angle in next time
    // step
    //
    // if(totalRotation < -180 * MathUtils.degreesToRadians) totalRotation +=
    // 360*MathUtils.degreesToRadians;
    // if(totalRotation > 180 * MathUtils.degreesToRadians) totalRotation -=
    // 360*MathUtils.degreesToRadians;
    //
    //
    // //Aktuell drehen wir das Dreieck brav dem Player hinterher
    // if(true || Gdx.input.isKeyPressed(Keys.R))
    // {
    // // triangleBody.setAngularVelocity(0);
    // // triangleBody.setTransform(triangleBody.getPosition(), normalizedDifferenceRotationRad);
    //
    // // triangleBody.applyTorque((normalizedDifferenceRotationRad < 0 ? -10 : 10), true);
    // triangleBody.applyTorque((totalRotation < 0 ? -10 : 10), true);
    // }
    //
    // --test

    float nextAngle = triangleBody.getAngle() + triangleBody.getAngularVelocity() / 60.0f;
    float totalRotation = normalizedDifferenceRotationRad - nextAngle;
    while(totalRotation < -180 * MathUtils.degreesToRadians)
      totalRotation += 360 * MathUtils.degreesToRadians;
    while(totalRotation > 180 * MathUtils.degreesToRadians)
      totalRotation -= 360 * MathUtils.degreesToRadians;
    float desiredAngularVelocity = totalRotation * 60;
    float change = 20 * MathUtils.degreesToRadians; // allow 1 degree rotation per time step
    desiredAngularVelocity = Math.min(change, Math.max(-change, desiredAngularVelocity));
    float impulse = triangleBody.getInertia() * desiredAngularVelocity;
    triangleBody.applyAngularImpulse(impulse, true);

    if(Gdx.input.isKeyPressed(Keys.T))
    {
      Vector2 achtMalVier = dynamicBody.getPosition().cpy().sub(triangleBody.getPosition()).nor().cpy().scl(1.0f / 5.0f);
      triangleBody.applyLinearImpulse(achtMalVier, triangleBody.getPosition(), true);
    }

    playerSprite.setPosition((dynamicBody.getPosition().x - 0.5f) * TS, (dynamicBody.getPosition().y - 0.5f) * TS);
    playerSprite.setRotation(MathUtils.radiansToDegrees * dynamicBody.getAngle());

    triangleSprite.setPosition((triangleBody.getPosition().x - 0.5f) * TS, (triangleBody.getPosition().y - 0.5f) * TS);
    triangleSprite.setRotation(MathUtils.radiansToDegrees * triangleBody.getAngle());

    bottomSprite.setPosition((groundBody.getPosition().x - 5f) * 32, (groundBody.getPosition().y - 1f) * 32);

    batch.setProjectionMatrix(cameraSprites.combined);
    batch.begin();
    playerSprite.draw(batch);
    bottomSprite.draw(batch);
    triangleSprite.draw(batch);

    // in Debug-Ausgabe wird der dann noch in ° geändert, wegen der Lesbarkeit
    float normalizedDifferenceRotationDeg = MathUtils.radiansToDegrees * normalizedDifferenceRotationRad;

    font.draw(batch, "totalRotation: " + (MathUtils.radiansToDegrees * totalRotation), 50, 108);
    font.draw(batch, "normalizedDifferenceRotationDeg: " + normalizedDifferenceRotationDeg, 50, 92);
    // font.draw(batch, "currentTriangleRotationDeg: "+currentTriangleRotationDeg, 50, 76);
    // font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 50, 60);
    batch.end();

    shapeRenderer.setProjectionMatrix(cameraSprites.combined);
    shapeRenderer.begin(ShapeType.Line);
    shapeRenderer.line(dynamicBody.getPosition().scl(32), triangleBody.getPosition().scl(32));
    shapeRenderer.end();

    world.step(1 / 60f, 6, 2);

    debugRenderer.render(world, cameraBox2dDebug.combined);
  }

  @Override
  public void dispose()
  {
    batch.dispose();
    libgdxTexture.dispose();
    shapeRenderer.dispose();
  }
}
