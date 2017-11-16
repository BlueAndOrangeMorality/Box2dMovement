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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
//import com.badlogic.gdx.physics.box2d.FixtureDef;
//import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class Box2dMovement extends ApplicationAdapter {

      public static final String TEXTURE_LIBGDX = "data/libgdx.png"; 
	  public static final String TEXTURE_ENTITIES = "data/entities-big.png";
	
	  private AssetManager assetManager;
	  
	  private OrthographicCamera cameraSprites;
	  private OrthographicCamera cameraBox2dDebug;
	  private SpriteBatch batch;
	  private Texture libgdxTexture, entitiesBigTexture;
	  
	  private ShapeRenderer shapeRenderer;
	  private Random random;
	  
	  private World world;
	  private Box2DDebugRenderer debugRenderer;

	  //My Objects
	  private ArrayList<BoxEntity> boxEntities;
	  private ArrayList<Waypoint> waypoints;
	   
	  private DebugOutput debugOutput;
	  
	  
	@Override
	public void create () {

		assetManager = new AssetManager();
		loadAssets();
		
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
	    //libgdxTexture = new Texture(Gdx.files.internal("data/libgdx.png"));
	    libgdxTexture = assetManager.get(TEXTURE_LIBGDX);
	    libgdxTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	    TextureRegion region = new TextureRegion(libgdxTexture, 0, 0, 512, 275);

	    //entitiesBigTexture = new Texture(Gdx.files.internal("data/entities-big.png"));
	    entitiesBigTexture = assetManager.get(TEXTURE_ENTITIES);
	    entitiesBigTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	    TextureRegion entityTriangleRegion = new TextureRegion(entitiesBigTexture, 1* TS, 6 * TS, TS, TS);
	    TextureRegion entityPlayerRegion   = new TextureRegion(entitiesBigTexture, 8 * TS, 1 * TS, TS, TS);

	    //Random für Random
	    random = new Random();
	    
	    debugOutput = new DebugOutput();

	    
        boxEntities = new ArrayList<BoxEntity>();
        
        //Ein Spieler
	    boxEntities.add(new Player(world, new Vector2(16, 10), entityPlayerRegion));
	    
	    //Beliebig viele entities
	    boxEntities.add(new Enemy(this, 16, 22));
	    boxEntities.add(new Enemy(this, 20,  2));
	    boxEntities.add(new Enemy(this, 23,  2));
	    boxEntities.add(new Enemy(this, 17, 22));
	    boxEntities.add(new Enemy(this, 20, 22));
	    boxEntities.add(new Enemy(this, 23, 22));
	    boxEntities.add(new Enemy(this, 10, 22));
	    
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
	    boxEntities.add(new Obstacle(world, new Vector2(16,4), region));
//	    boxEntities.add(new Obstacle(world, new Vector2(16,18), region));
//	    boxEntities.add(new Obstacle(world, new Vector2(25,12), region));
//	    boxEntities.add(new Obstacle(world, new Vector2(10,12), region));
	    
	    //Jedem Enemy initial einen Waypoint zuweisen
	    for(BoxEntity boxEntity : boxEntities)
	    {
	      if(boxEntity.getClass() == Enemy.class)
	        ((Enemy)boxEntity).setCurrentTargetId(getRandomWaypointIndex(0));
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
	    
		    //Hier wird dem enemy ein bewegungsbefehl gegeben, je nach status
		    for(BoxEntity boxEntity : boxEntities)
		    {
		      boxEntity.move(this);
		    }	  
		    
//		    stateTime += Gdx.graphics.getDeltaTime();
//		    TextureRegion currentFrame = aaa.getKeyFrame(stateTime, true);
//		    Sprite s = new Sprite(currentFrame);
//		    s.setPosition(128, 40);
		    
		    
		    batch.setProjectionMatrix(cameraSprites.combined);
		    batch.begin();
		    
//		      s.draw(batch);
		      
		    for(BoxEntity boxEntity : boxEntities)
		    {
		      boxEntity.render(batch);
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
	    assetManager.dispose();
	}
	

	  public Vector2 getPlayerPosition()
	  {
	    
	    
	    for(BoxEntity boxEntity : boxEntities)
	    {
	      if(boxEntity.getClass() == Player.class)
	        return ((Player)boxEntity).getBody().getPosition();
	    }
	    return new Vector2(0,0);
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
	  
  public void loadAssets(){
    assetManager.load(TEXTURE_LIBGDX, Texture.class);
    assetManager.load(TEXTURE_ENTITIES, Texture.class);
    assetManager.finishLoading();
  }
  
  public AssetManager getAssetManager() {
    return assetManager;
  }
  
  public World getWorld() {
		return world;
  }
}
