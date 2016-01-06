package dalcoms.pub.fingerbrickbreaker;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import dalcoms.pub.fingerbrickbreaker.scene.SceneGame;

public abstract class BallSprite extends Sprite {
	private Body body;
	private Camera mCamera;
	private PhysicsWorld mPhysicsWorld;
	private SceneGame mSceneGame;
	PhysicsConnector mPhysicsConnector;

	public BallSprite( float pX, float pY, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, Camera camera ) {

		super( pX, pY, pTextureRegion, pVertexBufferObjectManager );
		mCamera = camera;
	}

	public BallSprite setCamera( Camera pCamera ) {
		this.mCamera = pCamera;
		return this;
	}

	public BallSprite setPhysicsWorld( PhysicsWorld pPhysicsWorld ) {
		this.mPhysicsWorld = pPhysicsWorld;
		return this;
	}

	public BallSprite setSceneGame( SceneGame pSceneGame ) {
		this.mSceneGame = pSceneGame;
		return this;
	}

	public BallSprite createPhysics( ) {
		final float pDensity = ResourcesManager.getInstance()
				.applyResizeFactor( 0.1f );
		final float pElasticity = ResourcesManager.getInstance()
				.applyResizeFactor( 0.6f );
		final float pFriction = ResourcesManager.getInstance()
				.applyResizeFactor( 0.10f );

		FixtureDef pFixtureDef = PhysicsFactory.createFixtureDef( pDensity,
				pElasticity, pFriction );

		this.body = PhysicsFactory.createCircleBody( this.mPhysicsWorld, 0f, 0f,
				this.getHeight() * 0.5f, BodyType.DynamicBody, pFixtureDef );

		body.setTransform(
				( this.getX() + this.getWidth() * 0.5f ) / PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT,
				( this.getY() + this.getHeight() * 0.5f ) / PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT,
				body.getAngle() );

		this.body.setUserData( "ball" );

		this.mPhysicsConnector = new PhysicsConnector( this, this.body, true,
				true ) {
			@Override
			public void onUpdate( float pSecondsElapsed ) {
				super.onUpdate( pSecondsElapsed );
				onUpdateCheck();
			}
		};

		this.mPhysicsWorld.registerPhysicsConnector( this.mPhysicsConnector );

		return this;
	}

	private void onUpdateCheck( ) {

	}

	public Body getBody( ) {
		return this.body;
	}

	public abstract void onDie( );

}
