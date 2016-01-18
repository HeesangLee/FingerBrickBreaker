package dalcoms.pub.fingerbrickbreaker.scene;

import java.util.ArrayList;

import lib.dalcoms.andengineheesanglib.utils.HsMath;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import android.app.Activity;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import dalcoms.pub.fingerbrickbreaker.BallSprite;
import dalcoms.pub.fingerbrickbreaker.HaloOfBallSprite;
import dalcoms.pub.fingerbrickbreaker.RectangleBrick;
import dalcoms.pub.fingerbrickbreaker.RectangleGround;
import dalcoms.pub.fingerbrickbreaker.ResourcesManager;
import dalcoms.pub.fingerbrickbreaker.ThisAppCommon;
import dalcoms.pub.fingerbrickbreaker.level.JsonDataBrick;
import dalcoms.pub.fingerbrickbreaker.level.JsonDataEntity;
import dalcoms.pub.fingerbrickbreaker.level.JsonDataLevel;

public class SceneGame extends BaseScene implements IOnSceneTouchListener {
	private final String TAG = this.getClass().getSimpleName();

	private PhysicsWorld physicsWorld;

	ThisAppCommon appComm;
	HsMath hsMath;

	ArrayList<IAreaShape> mIAreaShapeDetachOnPlay = new ArrayList<IAreaShape>();

	HaloOfBallSprite mHaloOfBallSprite;
	BallSprite mMainBall;

	private int mGameTimePerGameTimer = 0;
	//	final private int tankNum = 5;

	private int mGameLevel = 0;
	private int mGameHeartCount = 3;
	ArrayList<Sprite> mGameHearts = new ArrayList<Sprite>();

	private JsonDataLevel mLevelData;

	private VelocityTracker mVelocityTracker = null;
	private final int VELOCITY_TRACKER_CAL_UNIT = 30;

	@Override
	public void createScene( ) {
		appComm = new ThisAppCommon();
		hsMath = new HsMath();

		this.mLevelData = loadGameLevel();

		this.setBackground( new Background( this.appColor.APP_BACKGROUND ) );

		initialPhysicsWorld();

		registerUpdateHandlerForGame( sceneManager.getGameTimerTimeSecond() );

		initPools();

		this.engine.runOnUpdateThread( new Runnable() {
			@Override
			public void run( ) {
				attachSprites();
			}
		} );

		this.engine.registerUpdateHandler( new TimerHandler( 0.1f, new ITimerCallback() {

			@Override
			public void onTimePassed( TimerHandler pTimerHandler ) {
				engine.unregisterUpdateHandler( pTimerHandler );
				setOnSceneTouchListenerDelay();
			}
		} ) );

	}

	private void setOnSceneTouchListenerDelay( ) {
		setOnSceneTouchListener( this );
	}

	private void initPools( ) {

	}

	private void registerUpdateHandlerForGame( float timeSecond ) {
		this.engine.registerUpdateHandler( new TimerHandler( timeSecond, true, new ITimerCallback() {

			@Override
			public void onTimePassed( final TimerHandler pTimerHandler ) {
				mGameTimePerGameTimer++;
			}
		} ) );

	}

	private JsonDataLevel loadGameLevel( ) {
		//		this.mGameLevel = sceneManager.getGameLevelData().getSelectedLevel();
		return sceneManager.getLevelData().getLevels().get( this.mGameLevel );
	}

	private int getGameLevel( ) {
		return this.mGameLevel;
	}

	private void initialPhysicsWorld( ) {
		//		this.physicsWorld = new PhysicsWorld( new Vector2( 0, sceneManager.getGameGravity() ), false );
		this.physicsWorld = new PhysicsWorld( new Vector2( 0, 0 ), false );
		registerUpdateHandler( physicsWorld );
		this.physicsWorld.setContactListener( createContactListener() );

	}

	private ContactListener createContactListener( ) {
		ContactListener contactListener = new ContactListener() {

			@Override
			public void preSolve( Contact contact, Manifold oldManifold ) {
				// TODO Auto-generated method stub

			}

			@Override
			public void postSolve( Contact contact, ContactImpulse impulse ) {
				// TODO Auto-generated method stub

			}

			@Override
			public void endContact( Contact contact ) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();
			}

			@Override
			public void beginContact( Contact contact ) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();
			}
		};

		return contactListener;
	}

	@Override
	public void attachSprites( ) {
		attachDefaultSprite();

		for ( JsonDataBrick pBrick : mLevelData.getBricks() ) {
			attachBrick( pBrick );
		}

		for ( JsonDataEntity pEntity : mLevelData.getEntities() ) {
			attachEntity( pEntity );
		}

	}

	private void attachBrick( JsonDataBrick pJsonDataBrick ) {
		if ( pJsonDataBrick.getName().equals( "rect" ) ) {
			attachRectBrick( pJsonDataBrick );
		}
	}

	private void attachEntity( JsonDataEntity pJsonDataEntity ) {
		if ( pJsonDataEntity.getName().equals( "groundRect" ) ) {
			attachGroundRectEntity( pJsonDataEntity );
		} else if ( pJsonDataEntity.getName().equals( "halo" ) ) {
			attachHaloOfBall( pJsonDataEntity );
		} else if ( pJsonDataEntity.getName().equals( "mainBall" ) ) {
			attachBall( pJsonDataEntity );
		}

	}

	private void attachGroundRectEntity( JsonDataEntity pJsonDataEntity ) {
		//Strongly don't use position.layout of brick.
		final float pX = resourcesManager.applyResizeFactor( pJsonDataEntity.getX() );
		final float pY = resourcesManager.applyResizeFactor( pJsonDataEntity.getY() );
		final float pWidth = resourcesManager.applyResizeFactor( pJsonDataEntity.getWidth() );
		final float pHeight = resourcesManager.applyResizeFactor( pJsonDataEntity.getHeight() );

		RectangleGround tRect = new RectangleGround( pX, pY, pWidth, pHeight, this );
		tRect.createPhysics( pJsonDataEntity.getName(), pJsonDataEntity.getBodyType(),
				pJsonDataEntity.getFixtureDef() );
		tRect.setColor( pJsonDataEntity.getColor() );
		attachChild( tRect );
	}

	private void attachRectBrick( JsonDataBrick pJsonDataBrick ) {
		//Strongly don't use position.layout of brick.
		final float pX = resourcesManager.applyResizeFactor( pJsonDataBrick.getX() );
		final float pY = resourcesManager.applyResizeFactor( pJsonDataBrick.getY() );
		final float pWidth = resourcesManager.applyResizeFactor( pJsonDataBrick.getWidth() );
		final float pHeight = resourcesManager.applyResizeFactor( pJsonDataBrick.getHeight() );

		RectangleBrick tRect = new RectangleBrick( pX, pY, pWidth, pHeight, this );
		tRect.createPhysics( pJsonDataBrick.getName(), pJsonDataBrick.getBodyType(),
				pJsonDataBrick.getFixtureDef() );
		tRect.setColor( pJsonDataBrick.getColor() );
		attachChild( tRect );
	}

	private void attachHaloOfBall( JsonDataEntity pJsonDataEntity ) {
		final float pX = resourcesManager.applyResizeFactor( pJsonDataEntity.getX() );
		final float pY = resourcesManager.applyResizeFactor( pJsonDataEntity.getY() );

		this.mHaloOfBallSprite = new HaloOfBallSprite( pX, pY, resourcesManager.regionCicle225, vbom )
				.setSceneGame( this );
		this.mHaloOfBallSprite.setColor( pJsonDataEntity.getColor() );
		attachChild( mHaloOfBallSprite );
		registerTouchArea( mHaloOfBallSprite );
	}

	public HaloOfBallSprite getHaloOfBall( ) {
		return this.mHaloOfBallSprite;
	}

	public BallSprite getMainBall( ) {
		return this.mMainBall;
	}

	private void attachBall( JsonDataEntity pJsonDataEntity ) {
		//Strongly don't use position.layout of brick.
		float pX = 0;
		float pY = 0;
		if ( this.mHaloOfBallSprite == null ) {
			pX = resourcesManager.applyResizeFactor( pJsonDataEntity.getX() );
			pY = resourcesManager.applyResizeFactor( pJsonDataEntity.getY() );
		} else {
			pX = this.mHaloOfBallSprite.getCenterX() - resourcesManager.regionCicle80.getWidth() * 0.5f;
			pY = this.mHaloOfBallSprite.getCenterY() - resourcesManager.regionCicle80.getHeight() * 0.5f;
		}

		final Color pInnerColor = pJsonDataEntity.getColor();

		this.mMainBall = new BallSprite( pX, pY, resourcesManager.regionCicle80, appColor.BALL,
				resourcesManager.regionCicle38, pInnerColor, this ) {

			@Override
			public void onDie( ) {
				this.safeByeBye();

			}
		};

		mMainBall.createPhysics( "mainBall", pJsonDataEntity.getBodyType(), pJsonDataEntity.getFixtureDef() );
		attachChild( mMainBall );
	}

	private void attachDefaultSprite( ) {
		attachGameHeart();
		attachGameLevel();
	}

	private void attachGameLevel( ) {
		final float pRightOffset = resourcesManager.applyResizeFactor( 25f );
		final float pXOffset = resourcesManager.applyResizeFactor( 10f );
		final float pRectWidth = 80f;
		final float pRectHeight = pRectWidth;

		float pX = camera.getWidth() - ( pRightOffset + pRectWidth );
		final float pY = resourcesManager.applyResizeFactor( 47.8f );

		final int pLevel_1 = ( this.mGameLevel + 1 ) % 10;
		final int pLevel_10 = ( int ) ( this.mGameLevel + 1 ) / 10;

		Rectangle tRectBox1 = new Rectangle( pX, pY, pRectWidth, pRectHeight, vbom );
		tRectBox1.setColor( appColor.LEVEL_BOX );
		Text pText1 = new Text( 0f, 0f, resourcesManager.getFontDefault(), String.valueOf( pLevel_1 ), vbom );
		pText1.setPosition( 0.5f * ( pRectWidth - pText1.getWidth() ),
				0.5f * ( pRectHeight - pText1.getHeight() ) );
		tRectBox1.attachChild( pText1 );
		attachChild( tRectBox1 );

		if ( this.mGameLevel > 9 ) {
			Rectangle tRectBox2 = new Rectangle( pX - ( pRectWidth + pXOffset ), pY, pRectWidth, pRectHeight,
					vbom );
			tRectBox2.setColor( appColor.LEVEL_BOX );
			Text pText2 = new Text( 0f, 0f, resourcesManager.getFontDefault(), String.valueOf( pLevel_10 ),
					vbom );
			pText2.setPosition( 0.5f * ( pRectWidth - pText2.getWidth() ),
					0.5f * ( pRectHeight - pText2.getHeight() ) );
			tRectBox2.attachChild( pText2 );
			attachChild( tRectBox2 );
		}
	}

	private void attachGameHeart( ) {
		final float pLeftOffset = resourcesManager.applyResizeFactor( 25f );
		final float pXOffset = resourcesManager.applyResizeFactor( 10f );
		float pX = pLeftOffset;
		final float pY = resourcesManager.applyResizeFactor( 73f );

		for ( int i = 0 ; i < this.mGameHeartCount ; i++ ) {
			this.mGameHearts.add( new Sprite( pX, pY, resourcesManager.regionGameHeart, vbom ) );
			pX = pX + resourcesManager.regionGameHeart.getWidth() + pXOffset;
		}
		for ( Sprite pHeart : this.mGameHearts ) {
			attachChild( pHeart );
			pHeart.registerEntityModifier( new ScaleModifier( 0.5f, 0.1f, 1f ) );
		}
	}

	private void moveOutIAreaShapeEntityAfterPlayButtonTouch( ) {//Just move it to screen out.
		float diffLeftRight = 0;
		float pToX = 0;
		boolean goneDirection = false; // f : left, t:right
		for ( IAreaShape pIAreaShape : mIAreaShapeDetachOnPlay ) {
			diffLeftRight = pIAreaShape.getX()
					- ( camera.getWidth() - pIAreaShape.getX() - pIAreaShape.getWidth() );

			if ( diffLeftRight == 0 ) {//To where ? 
				if ( goneDirection == true ) {//To Left
					goneDirection = false;
					pToX = -1.1f * pIAreaShape.getWidth();
				} else {//To right
					goneDirection = true;
					pToX = 1.1f * camera.getWidth();
				}
			} else if ( diffLeftRight > 0 ) {//To right
				goneDirection = true;
				pToX = 1.1f * camera.getWidth();
			} else {//To Left
				goneDirection = false;
				pToX = -1.1f * pIAreaShape.getWidth();
			}

			pIAreaShape.registerEntityModifier( new MoveXModifier( 0.38f, pIAreaShape.getX(), pToX ) );
		}
	}

	private void destroyIAreaShapeEntityAfterPlayButtonTouch( ) {
		for ( IAreaShape pIAreaShape : mIAreaShapeDetachOnPlay ) {
			pIAreaShape.setIgnoreUpdate( true );
			pIAreaShape.setVisible( false );
			detachChild( pIAreaShape );
		}
		this.engine.runOnUpdateThread( new Runnable() {

			@Override
			public void run( ) {
				mIAreaShapeDetachOnPlay.clear();
			}
		} );

	}

	private void attachText( JsonDataEntity pEntity ) {
		//		Text pText = new Text( 0, 0, resourcesManager.getFontDefault(), pEntity.getText(), vbom );
		//
		//		final PointF entityPosition = getJsonDataEntityPosition( pEntity.getPosition(), pText );
		//		final float pX = entityPosition.x;
		//		final float pY = entityPosition.y;
		//
		//		pText.setPosition( pX, pY );
		//		attachChild( pText );
		//
		//		if ( pEntity.isDetachOnPlay() ) {
		//			this.mIAreaShapeDetachOnPlay.add( pText );
		//		}
		//
		//		if ( pEntity.getName().equals( "fingerText" ) ) {
		//			pText.registerEntityModifier( new LoopEntityModifier( new SequenceEntityModifier(
		//					new RotationModifier( 0.5f, 0, 7 ), new RotationModifier( 0.5f, 7, 0 ) ) ) );
		//		}
	}

	private void attachFingerSprite( JsonDataEntity pEntity ) {
		//		Sprite fingerSprite = new Sprite( 0f, 0f, resourcesManager.regionFinger, vbom );
		//
		//		final PointF entityPosition = getJsonDataEntityPosition( pEntity.getPosition(), fingerSprite );
		//		final float pX = entityPosition.x;
		//		final float pY = entityPosition.y;
		//
		//		fingerSprite.setPosition( pX, pY );
		//		attachChild( fingerSprite );
		//
		//		if ( pEntity.isDetachOnPlay() ) {
		//			this.mIAreaShapeDetachOnPlay.add( fingerSprite );
		//		}
		//
		//		if ( pEntity.getName().equals( "fingerIndicatorSprite" ) ) {
		//			fingerSprite.registerEntityModifier( new LoopEntityModifier( new SequenceEntityModifier(
		//					new MoveYModifier( 0.5f, pY, pY - resourcesManager.applyResizeFactor( 24f ) ),
		//					new MoveYModifier( 0.5f, pY - resourcesManager.applyResizeFactor( 24f ), pY ) ) ) );
		//		}
	}

	@Override
	public void onBackKeyPressed( ) {
		sceneManager.createSceneHome();

	}

	@Override
	public SceneType getSceneType( ) {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene( ) {
		// TODO Auto-generated method stub

	}

	public enum GameStatus {
		GAME_PREPLAY, GAME_PLAYING, GAME_END
	}

	public PhysicsWorld getPhysicsWorld( ) {
		return this.physicsWorld;
	}

	@Override
	public Engine getEngine( ) {
		return this.engine;
	}

	@Override
	public Activity getActivity( ) {
		return this.activity;
	}

	@Override
	public VertexBufferObjectManager getVbom( ) {
		return this.vbom;
	}

	@Override
	public Camera getCamera( ) {
		return this.camera;
	}

	@Override
	public ResourcesManager getResourcesManager( ) {
		return this.resourcesManager;
	}

	@Override
	public SceneManager getSceneManager( ) {
		return this.sceneManager;
	}

	@Override
	public boolean onSceneTouchEvent( Scene pScene, TouchEvent pSceneTouchEvent ) {
		MotionEvent pMotionEvent = pSceneTouchEvent.getMotionEvent();
		int pointerId = pSceneTouchEvent.getPointerID();

		switch ( pSceneTouchEvent.getAction() ) {
			case TouchEvent.ACTION_DOWN :
				if ( mVelocityTracker == null ) {
					mVelocityTracker = VelocityTracker.obtain();
				} else {
					mVelocityTracker.clear();
					mVelocityTracker.addMovement( pMotionEvent );
				}
			case TouchEvent.ACTION_MOVE :
				if ( this.mHaloOfBallSprite.isSelected() ) {
					//					this.mHaloOfBallSprite.setCenterPosition( pSceneTouchEvent.getX(),
					//							pSceneTouchEvent.getY() );

					mVelocityTracker.addMovement( pMotionEvent );
					mVelocityTracker.computeCurrentVelocity( VELOCITY_TRACKER_CAL_UNIT );

					Log.d( "",
							String.valueOf( VelocityTrackerCompat.getXVelocity( mVelocityTracker, pointerId ) )
									+ ","
									+ String.valueOf( VelocityTrackerCompat.getYVelocity( mVelocityTracker,
											pointerId ) ) );

					mMainBall.setFingerThrowVelocity(
							VelocityTrackerCompat.getXVelocity( mVelocityTracker, pointerId ),
							VelocityTrackerCompat.getYVelocity( mVelocityTracker, pointerId ) );
				} else {
					if ( mVelocityTracker != null ) {
						mVelocityTracker.clear();
					}
				}
				break;
			default :
				if ( mHaloOfBallSprite.isSelected() == true ) {
					this.mHaloOfBallSprite.setFlagSelected( false );
					testThowBall();
				}

				if ( mVelocityTracker != null ) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
				break;
		}

		return false;
	}

	public void testThowBall( ) {
		if ( this.mMainBall.getBody().getType() == BodyType.DynamicBody ) {
			Log.d( "", "dynamic body" );
		} else if ( this.mMainBall.getBody().getType() == BodyType.KinematicBody ) {
			Log.d( "", "kinematic body" );
		} else if ( this.mMainBall.getBody().getType() == BodyType.StaticBody ) {
			Log.d( "", "static body" );
		} else {
			Log.d( "", "what?" );
		}
		this.mMainBall.getBody().setLinearVelocity( this.mMainBall.getFingerThrowVelocity() );

	}

}