package de.ranagazoo.box;

//import static de.ranagazoo.box.Box2dBuilder.createBody;
//import static de.ranagazoo.box.Box2dBuilder.createChainShape;
//import static de.ranagazoo.box.Box2dBuilder.createFixtureDef;
//import static de.ranagazoo.box.Box2dBuilder.createStaticBodyDef;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
//import com.badlogic.gdx.physics.box2d.FixtureDef;
//import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import de.ranagazoo.box.Box2dContactListener;
import de.ranagazoo.box.DebugOutput;
import de.ranagazoo.box.Enemy;
import de.ranagazoo.box.Obstacle;
import de.ranagazoo.box.Player;
import de.ranagazoo.box.Waypoint;

public class Box2dMovement extends ApplicationAdapter {
	

	  public static final short CATEGORY_PLAYER  = 0x0001;  // 0000000000000001 in binary
	  public static final short CATEGORY_MONSTER = 0x0002;  // 0000000000000010 in binary
	  public static final short CATEGORY_MSENSOR = 0x0008;  // 0000000000001000 in binary
	  public static final short CATEGORY_SCENERY = 0x0004;  // 0000000000000100 in binary
	  public static final short CATEGORY_NONE    = 0x0032;  // 0000000000100000 in binary ???
	  public static final short CATEGORY_WAYPOINT    = 0x0064;  // 0000000001000000 in binary ???
	  
	  public static final short MASK_PLAYER =  CATEGORY_MONSTER | CATEGORY_SCENERY | CATEGORY_MSENSOR; // or ~CATEGORY_PLAYER
	  public static final short MASK_MONSTER = CATEGORY_PLAYER | CATEGORY_SCENERY | CATEGORY_WAYPOINT; // or ~CATEGORY_MONSTER
	  //Monstersensor, reagiert nur auf player
	  public static final short MASK_MSENSOR = CATEGORY_PLAYER; // or ~CATEGORY_MONSTER
	  public static final short MASK_SCENERY = -1;
	  public static final short MASK_NONE = CATEGORY_SCENERY;
	  public static final short MASK_WAYPOINT = CATEGORY_MONSTER;

	  public static final int TS = 32;

	  public enum Status
	  {
	    IDLE, ATTACK, NEW_TARGET;
	  }
	  
	 
	  
	  
	  //next
	  //http://www.iforce2d.net/b2dtut/sensors
	  
	  //Elder Retry Chaprettt, Arcy Trang, Greego Larcin, 
	  
	  
	  private OrthographicCamera cameraSprites;
	  private OrthographicCamera cameraBox2dDebug;
	  private SpriteBatch batch;
	  private Texture libgdxTexture, entitiesBigTexture;
	  
	  private ShapeRenderer shapeRenderer;
	  private Random random;
	  
	  private World world;
	  private Box2DDebugRenderer debugRenderer;

	  //My Objects
	  private Player player;
	  private ArrayList<Enemy> enemies;
	  private ArrayList<Waypoint> waypoints;
	  private ArrayList<Obstacle> obstacles;
//	  private Body borderBody; 
	  private DebugOutput debugOutput;
	  
	  
	@Override
	public void create () {

	    //box2dworld
	    world = new World(new Vector2(0, 0), true);
	    world.setContactListener(new Box2dContactListener());

	    //Renderer / Cameras
	    debugRenderer = new Box2DDebugRenderer();
	    
	    cameraSprites = new OrthographicCamera();
	    cameraSprites.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	    cameraSprites.update();
	    
	    cameraBox2dDebug = new OrthographicCamera();
	    cameraBox2dDebug.setToOrtho(false, Gdx.graphics.getWidth() / TS, Gdx.graphics.getHeight() / TS);
	    cameraBox2dDebug.update();
	    
	    batch = new SpriteBatch();
	    shapeRenderer = new ShapeRenderer();
	    
	    //Texturen für die Sprites
	    libgdxTexture = new Texture(Gdx.files.internal("data/libgdx.png"));
	    libgdxTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	    TextureRegion region = new TextureRegion(libgdxTexture, 0, 0, 512, 275);

	    entitiesBigTexture = new Texture(Gdx.files.internal("data/entities-big.png"));
	    entitiesBigTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	    TextureRegion entityTriangleRegion = new TextureRegion(entitiesBigTexture, 1* TS, 6 * TS, TS, TS);
	    TextureRegion entityPlayerRegion   = new TextureRegion(entitiesBigTexture, 8 * TS, 1 * TS, TS, TS);

	    
	    //Random für Random
	    random = new Random();
	    
	    debugOutput = new DebugOutput();

	    //Ein Spieler
	    player = new Player(world, new Vector2(16, 10), entityPlayerRegion);
	    
	    //Beliebig viele entities
	    enemies = new ArrayList<Enemy>();
	    enemies.add(new Enemy(world, new Vector2(16, 22), entityTriangleRegion));
	    enemies.add(new Enemy(world, new Vector2(20,  2), entityTriangleRegion));
	    enemies.add(new Enemy(world, new Vector2(23,  2), entityTriangleRegion));
	    enemies.add(new Enemy(world, new Vector2(17, 22), entityTriangleRegion));
	    enemies.add(new Enemy(world, new Vector2(20, 22), entityTriangleRegion));
	    enemies.add(new Enemy(world, new Vector2(23, 22), entityTriangleRegion));
	    enemies.add(new Enemy(world, new Vector2(10, 22), entityTriangleRegion));

	    //Beliebig viele Waypoints, zwischen denen die Entities hin- und hertuckern
	    waypoints = new ArrayList<Waypoint>();
	    waypoints.add(new Waypoint(world, new Vector2(3,  3),  entityTriangleRegion, waypoints.size()));
	    waypoints.add(new Waypoint(world, new Vector2(10, 3),  entityTriangleRegion, waypoints.size()));
	    waypoints.add(new Waypoint(world, new Vector2(10, 6),  entityTriangleRegion, waypoints.size()));
	    waypoints.add(new Waypoint(world, new Vector2(6,  9),  entityTriangleRegion, waypoints.size()));
	    waypoints.add(new Waypoint(world, new Vector2(3,  10), entityTriangleRegion, waypoints.size()));
	    waypoints.add(new Waypoint(world, new Vector2(20, 13), entityTriangleRegion, waypoints.size()));
	    waypoints.add(new Waypoint(world, new Vector2(21, 10), entityTriangleRegion, waypoints.size()));
	    waypoints.add(new Waypoint(world, new Vector2(22, 20), entityTriangleRegion, waypoints.size()));
	    waypoints.add(new Waypoint(world, new Vector2(23, 15), entityTriangleRegion, waypoints.size()));
	    waypoints.add(new Waypoint(world, new Vector2(24, 3),  entityTriangleRegion, waypoints.size()));
	    waypoints.add(new Waypoint(world, new Vector2(25, 20), entityTriangleRegion, waypoints.size()));

	    //Hindernisse (statisch)
	    obstacles = new ArrayList<Obstacle>();
	    obstacles.add(new Obstacle(world, new Vector2(16,4), region));
//	    obstacles.add(new Obstacle(world, new Vector2(16,18), region));
//	    obstacles.add(new Obstacle(world, new Vector2(25,12), region));
//	    obstacles.add(new Obstacle(world, new Vector2(10,12), region));
	    
	    //Jedem Enemy initial einen Waypoint zuweisen
	    for(Enemy enemy : enemies)
	    {
	      enemy.setCurrentTargetId(getRandomWaypointIndex(0));
	    }
	    

	    //TODO Box2dChainShape funktioniert nicht mit 1.9.7
	    
	    //Border, könnte man auch noch auslagern
//	    Shape      tempShape;
//	    FixtureDef tempFixtureDef = null;
//	      
//	    borderBody = createBody(world, createStaticBodyDef(2, false, 2, new Vector2(16, 2)), "userData");
//	    tempShape = createChainShape(new Vector2[]{new Vector2(-15f, -1f), new Vector2(15f, -1f), new Vector2(15f, 21f), new Vector2(-15f, 21f), new Vector2(-15f, -1f)});
	    
	    //tempFixtureDef = createFixtureDef(0.0f, 0.4f, 0.1f, tempShape, CATEGORY_SCENERY, MASK_SCENERY);
	    //borderBody.createFixture(tempFixtureDef);
	    //tempShape.dispose();
	    
	}

	@Override
	public void render () {
		 if(Gdx.input.isKeyPressed(Keys.ESCAPE))
		      Gdx.app.exit();

		    Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 0);
		    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		    //Hier wird abgefragt, was der spieler eingibt
		    player.move();
		    
		    //Hier wird dem enemy ein bewegungsbefehl gegeben, je nach status
		    for(Enemy enemy : enemies)
		    {
		      enemy.move(this);
		    }
		  
		    
//		    stateTime += Gdx.graphics.getDeltaTime();
//		    TextureRegion currentFrame = aaa.getKeyFrame(stateTime, true);
//		    Sprite s = new Sprite(currentFrame);
//		    s.setPosition(128, 40);
		    
		    
		    
		    batch.setProjectionMatrix(cameraSprites.combined);
		    batch.begin();
		    
//		      s.draw(batch);
		    
		      player.render(batch);
		      
		      for(Enemy enemy : enemies)
		      {
		        enemy.render(batch);
		      } 
		      for(Obstacle obstacle : obstacles)
		      {
		        obstacle.render(batch);
		      } 
		      debugOutput.render(batch);
		      
		    batch.end();

		    shapeRenderer.setProjectionMatrix(cameraSprites.combined);
		    shapeRenderer.begin(ShapeType.Line);

		      for(int i = 0; i < waypoints.size()-1; i++)
		      {
		        shapeRenderer.line(waypoints.get(i).getPosition().scl(32), waypoints.get(i+1).getPosition().scl(32));
		      }
		      shapeRenderer.line(waypoints.get(waypoints.size()-1).getPosition().scl(32), waypoints.get(0).getPosition().scl(32));
		    
		    shapeRenderer.end();
		    
		    
		    world.step(1 / 60f, 6, 2);

		    debugRenderer.render(world, cameraBox2dDebug.combined);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	    libgdxTexture.dispose();
	    shapeRenderer.dispose();
	}
	

	  public Body getPlayerBody()
	  {
	    return this.player.getBody();
	  }


	    
	  //Return a temp random index, but not the given one
	  public int getRandomWaypointIndex(int notThisOne)
	  {
	    int temp = random.nextInt(waypoints.size());
	    while(notThisOne == temp)
	    {
	      temp = random.nextInt(waypoints.size());
	    }
	    
	    return temp;
	  }

	  public int getNextWaypointIndex(int currentone)
	  {
	    int temp = currentone+1;
	    if(temp >= waypoints.size())
	      temp = 0;
	    return temp;
	  }
	  
	  public Waypoint getWaypoint(int parameter)
	  {
	    return waypoints.get(parameter);
	  }
}
