package de.ranagazoo.box;

//import static de.ranagazoo.box.Box2dBuilder.createBody;
//import static de.ranagazoo.box.Box2dBuilder.createChainShape;
//import static de.ranagazoo.box.Box2dBuilder.createFixtureDef;
//import static de.ranagazoo.box.Box2dBuilder.createStaticBodyDef;

import static de.ranagazoo.box.Config.TS;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
//import com.badlogic.gdx.physics.box2d.FixtureDef;
//import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class Box2dMovement extends ApplicationAdapter
{

  public static final String TEXTURE_LIBGDX = "data/libgdx.png";
  public static final String TEXTURE_ENTITIES = "data/entities-big.png";
  public static final String TEXTURE_MECHA = "data/mecha32.png";

  private AssetManager assetManager;

  private OrthographicCamera cameraSprites;
  private OrthographicCamera cameraBox2dDebug;
  private SpriteBatch batch;

  private Texture libgdxTexture, entitiesBigTexture, mechaTexture;
  private TextureRegion entityPlayerRegion;

  private Animation<TextureRegion> animation;

  private ShapeRenderer shapeRenderer;
  private Random random;

  private World world;
  private Box2DDebugRenderer debugRenderer;

  private BoxEntityFactory boxEntityFactory;
  
  // My Objects
  private ArrayList<BoxEntity> boxEntities;
  private ArrayList<Waypoint> waypoints;

  private DebugOutput debugOutput;

  @Override
  public void create()
  {

    assetManager = new AssetManager();
    loadAssets();

    // box2dworld
    world = new World(new Vector2(0, 0), true);
    world.setContactListener(new Box2dContactListener());

    boxEntityFactory = new BoxEntityFactory();
    
    // Renderer / Cameras
    debugRenderer = new Box2DDebugRenderer();

    cameraSprites = new OrthographicCamera();
    cameraSprites.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    cameraSprites.update();

    cameraBox2dDebug = new OrthographicCamera();
    cameraBox2dDebug.setToOrtho(false, Gdx.graphics.getWidth() / TS, Gdx.graphics.getHeight() / TS);
    cameraBox2dDebug.update();

    batch = new SpriteBatch();
    shapeRenderer = new ShapeRenderer();

    // Texturen für die Sprites
    // libgdxTexture = new Texture(Gdx.files.internal("data/libgdx.png"));
    libgdxTexture = assetManager.get(TEXTURE_LIBGDX);
    libgdxTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    TextureRegion region = new TextureRegion(libgdxTexture, 0, 0, 512, 275);

    // entitiesBigTexture = new
    // Texture(Gdx.files.internal("data/entities-big.png"));
    entitiesBigTexture = assetManager.get(TEXTURE_ENTITIES);
    entitiesBigTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    entityPlayerRegion = new TextureRegion(entitiesBigTexture, 8 * TS, 1 * TS, TS, TS);

    // Random für Random
    random = new Random();

    debugOutput = new DebugOutput();

    mechaTexture = assetManager.get(TEXTURE_MECHA);
    mechaTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

    TextureRegion[] animationFrames = new TextureRegion[5];
    animationFrames[0] = new TextureRegion(mechaTexture, 0, 384, 64, 64);
    animationFrames[1] = new TextureRegion(mechaTexture, 64, 384, 64, 64);
    animationFrames[2] = new TextureRegion(mechaTexture, 128, 384, 64, 64);
    animationFrames[3] = new TextureRegion(mechaTexture, 192, 384, 64, 64);
    animationFrames[4] = new TextureRegion(mechaTexture, 256, 384, 64, 64);
    animation = new Animation<TextureRegion>(0.1f, animationFrames);

    boxEntities = new ArrayList<BoxEntity>();

    // Ein Spieler
    boxEntities.add(new Player(this, 16, 10));

    // Beliebig viele entities
    boxEntities.add(new Enemy(this, 16, 22));
    boxEntities.add(new Enemy(this, 20, 2));
    boxEntities.add(new Enemy(this, 23, 2));
    boxEntities.add(new Enemy(this, 17, 22));
    boxEntities.add(new Enemy(this, 20, 22));
    boxEntities.add(new Enemy(this, 23, 22));
    boxEntities.add(new Enemy(this, 10, 22));

    // Beliebig viele Waypoints, zwischen denen die Entities hin- und hertuckern
    waypoints = new ArrayList<Waypoint>();
    waypoints.add(new Waypoint(this, 3, 3));
    waypoints.add(new Waypoint(this, 10, 3));
    waypoints.add(new Waypoint(this, 10, 6));
    waypoints.add(new Waypoint(this, 6, 9));
    waypoints.add(new Waypoint(this, 3, 10));
    waypoints.add(new Waypoint(this, 20, 13));
    waypoints.add(new Waypoint(this, 21, 10));
    waypoints.add(new Waypoint(this, 22, 20));
    waypoints.add(new Waypoint(this, 23, 15));
    waypoints.add(new Waypoint(this, 24, 3));
    waypoints.add(new Waypoint(this, 25, 20));

    // Hindernisse (statisch)
    boxEntities.add(new Obstacle(this, 16, 4, region));
    boxEntities.add(new Obstacle(this, 18, 8, region));

    // Jedem Enemy initial einen Waypoint zuweisen
    for (BoxEntity boxEntity : boxEntities)
    {
      if (boxEntity.getClass() == Enemy.class)
        ((Enemy) boxEntity).setCurrentTargetId(getRandomWaypointIndex(0));
    }

    // TODO Box2dChainShape funktioniert nicht mit 1.9.7

    // Border, könnte man auch noch auslagern
    // Shape tempShape;
    // FixtureDef tempFixtureDef = null;
    //
    // borderBody = createBody(world, createStaticBodyDef(2, false, 2, new
    // Vector2(16, 2)), "userData");
    // tempShape = createChainShape(new Vector2[]{new Vector2(-15f, -1f), new
    // Vector2(15f, -1f), new Vector2(15f, 21f), new Vector2(-15f, 21f), new
    // Vector2(-15f, -1f)});

    // tempFixtureDef = createFixtureDef(0.0f, 0.4f, 0.1f, tempShape,
    // CATEGORY_SCENERY, MASK_SCENERY);
    // borderBody.createFixture(tempFixtureDef);
    // tempShape.dispose();

  }

  @Override
  public void render()
  {
    if (Gdx.input.isKeyPressed(Keys.ESCAPE))
      Gdx.app.exit();

    // Move all entities
    for (BoxEntity boxEntity : boxEntities)
    {
      boxEntity.move();
    }

    
    
    Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 0);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    batch.setProjectionMatrix(cameraSprites.combined);
    batch.begin();

    //Render all entities
    for (BoxEntity boxEntity : boxEntities)
    {
      boxEntity.render(batch);
    }

    //Render debug messages
    debugOutput.render(batch);

    batch.end();

    shapeRenderer.setProjectionMatrix(cameraSprites.combined);
    shapeRenderer.begin(ShapeType.Line);

    for (int i = 0; i < waypoints.size() - 1; i++)
    {
      shapeRenderer.line(waypoints.get(i).getPosition().scl(32), waypoints.get(i + 1).getPosition().scl(32));
    }
    shapeRenderer.line(waypoints.get(waypoints.size() - 1).getPosition().scl(32), waypoints.get(0).getPosition().scl(32));

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
    assetManager.dispose();
  }

  public Vector2 getPlayerPosition()
  {
    for (BoxEntity boxEntity : boxEntities)
    {
      if (boxEntity.getClass() == Player.class)
        return ((Player) boxEntity).getBody().getPosition();
    }
    return new Vector2(0, 0);
  }

  // Return a temp random index, but not the given one
  public int getRandomWaypointIndex(int notThisOne)
  {
    int temp = random.nextInt(waypoints.size());
    while (notThisOne == temp)
    {
      temp = random.nextInt(waypoints.size());
    }

    return temp;
  }

  public int getNextWaypointIndex(int currentone)
  {
    int temp = currentone + 1;
    if (temp >= waypoints.size())
      temp = 0;
    return temp;
  }

  public Waypoint getWaypoint(int parameter)
  {
    return waypoints.get(parameter);
  }

  public void loadAssets()
  {
    assetManager.load(TEXTURE_LIBGDX, Texture.class);
    assetManager.load(TEXTURE_ENTITIES, Texture.class);
    assetManager.load(TEXTURE_MECHA, Texture.class);
    assetManager.finishLoading();
  }

  public AssetManager getAssetManager()
  {
    return assetManager;
  }

  public World getWorld()
  {
    return world;
  }

  public Animation<TextureRegion> getAnimation()
  {
    return animation;
  }

  public TextureRegion getEntityPlayerRegion()
  {
    return entityPlayerRegion;
  }

  public ArrayList<Waypoint> getWaypoints()
  {
    return waypoints;
  }

  public BoxEntityFactory getBoxEntityFactory()
  {
    return boxEntityFactory;
  }
}
