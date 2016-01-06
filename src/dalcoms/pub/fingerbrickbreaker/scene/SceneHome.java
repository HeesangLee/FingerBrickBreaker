package dalcoms.pub.fingerbrickbreaker.scene;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Random;

import lib.dalcoms.andengineheesanglib.buttons.IconTextButtonSprite;
import lib.dalcoms.andengineheesanglib.buttons.LevelViewButton;
import lib.dalcoms.andengineheesanglib.utils.HsMath;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.ease.EaseBackOut;
import org.andengine.util.modifier.ease.EaseCircularIn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.support.v7.app.NotificationCompat.Builder;
import android.util.Log;
import dalcoms.pub.fingerbrickbreaker.GoMarketSharStarAnimatedSprite;
import dalcoms.pub.fingerbrickbreaker.Gotype;
import dalcoms.pub.fingerbrickbreaker.R;
import dalcoms.pub.fingerbrickbreaker.ResourcesManager;

public class SceneHome extends BaseScene {
	final String TAG = this.getClass().getSimpleName();

	HsMath hsMath = new HsMath();

	boolean flag_interstitialAdOn = false;
	ArrayList<LevelViewButton> mLevelViewButtons = new ArrayList<LevelViewButton>();

	Rectangle mRoundImgBgRect;
	Text mTextRoundNum;
	Text mTextRoundPoint;

	@Override
	public void createScene( ) {
		this.setBackground( new Background( this.appColor.APP_BACKGROUND ) );
		// prepareNotificationTest();

		this.engine.runOnUpdateThread( new Runnable() {
			@Override
			public void run( ) {
				attachSprites();
			}
		} );
	}

	@Override
	public void attachSprites( ) {
		this.attachMarketShareStarAnimatedSprites();
		this.attachTitileText();
		this.attachCompanyText();
		this.attachPlayButtonSprite();
		this.attachRoundImgBgRect();
		this.attachRoundNumberPointText();
		//		this.attachLevelButtons();
	}

	private int getSelectedLevel( ) {
		int ret = 0;
		for ( LevelViewButton btn : mLevelViewButtons ) {
			if ( btn.isSelected() ) {
				ret = btn.getIntTag();
				break;
			}
		}
		return ret;
	}

	private void attachRoundNumberPointText( ) {
		final float pWidth = camera.getWidth();
		final float pHeight = resourcesManager.applyResizeFactor( 117.439f );
		Rectangle bgRectRoundNum = new Rectangle( 0f, this.mRoundImgBgRect.getY() - pHeight, pWidth, pHeight,
				vbom );
		bgRectRoundNum.setColor( appColor.ROUND_NUMPOINT );

		Rectangle bgRectRoundPoint = new Rectangle( 0f, this.mRoundImgBgRect.getY()
				+ this.mRoundImgBgRect.getHeight(), pWidth,
				pHeight, vbom );
		bgRectRoundPoint.setColor( appColor.ROUND_NUMPOINT );

		attachChild( bgRectRoundNum );
		attachChild( bgRectRoundPoint );

		mTextRoundNum = new Text( 0, 0, resourcesManager.getFontButton(), "ROUND ##", vbom );
		mTextRoundPoint = new Text( 0, 0, resourcesManager.getFontButton(), "### POINT", vbom );
		bgRectRoundNum.attachChild( mTextRoundNum );
		bgRectRoundPoint.attachChild( mTextRoundPoint );

		final float pXRoundNum = hsMath.getAlignCenterFloat( mTextRoundNum.getWidth(), camera.getWidth() );
		final float pYRoundNum = hsMath.getAlignCenterFloat( mTextRoundNum.getHeight(),
				bgRectRoundNum.getHeight() );

		final float pXRoundPoint = hsMath.getAlignCenterFloat( mTextRoundPoint.getWidth(), camera.getWidth() );
		final float pYRoundPoint = hsMath.getAlignCenterFloat( mTextRoundPoint.getHeight(),
				bgRectRoundPoint.getHeight() );

		mTextRoundNum.setPosition( pXRoundNum, pYRoundNum );
		mTextRoundPoint.setPosition( pXRoundPoint, pYRoundPoint );
	}

	private void setRoundNumberPointText( int pRoundNum, int pRoundPoint, boolean pNewTextEffect ) {
		mTextRoundNum.setText( "ROUND " + String.valueOf( pRoundNum ) );
		mTextRoundPoint.setText( String.valueOf( pRoundPoint ) + " POINT" );

		final float pXRoundNum = hsMath.getAlignCenterFloat( mTextRoundNum.getWidth(), camera.getWidth() );
		final float pXRoundPoint = hsMath.getAlignCenterFloat( mTextRoundPoint.getWidth(), camera.getWidth() );
		mTextRoundNum.setPosition( pXRoundNum, mTextRoundNum.getY() );
		mTextRoundPoint.setPosition( pXRoundPoint, mTextRoundPoint.getY() );

		if ( pNewTextEffect == true ) {
			mTextRoundNum.registerEntityModifier( new ScaleModifier( 0.4f, 0.05f, 1f ) );
			mTextRoundPoint.registerEntityModifier( new ScaleModifier( 0.4f, 0.05f, 1f ) );
		}

	}

	private void attachRoundImgBgRect( ) {
		final float pWidth = camera.getWidth();
		final float pHeight = resourcesManager.applyResizeFactor( 408f );
		this.mRoundImgBgRect = new Rectangle( 0, 0, pWidth, pHeight, vbom );
		final float pX = 0f;
		final float pY = hsMath.getAlignCenterFloat( mRoundImgBgRect.getHeight(), camera.getHeight() );
		mRoundImgBgRect.setPosition( pX, pY );
		mRoundImgBgRect.setColor( appColor.ROUND_IMG_BG );
		attachChild( mRoundImgBgRect );

	}

	private void attachTitileText( ) {
		final float pY = resourcesManager.applyResizeFactor( 138f );
		Text pTitleText = new Text( 0, 0, resourcesManager.getFontDefault(),
				activity.getString( R.string.app_name ), vbom );
		pTitleText.setPosition( appComm.getAlignCenterFloat( pTitleText.getWidth(), camera.getWidth() ), pY );
		attachChild( pTitleText );
		pTitleText.setColor( appColor.FONT_DEFAULT );

		pTitleText
				.registerEntityModifier( new ScaleModifier( 2.5f, 0.1f, 1f, 1f, 1f, EaseBackOut.getInstance() ) );
	}

	private void attachCompanyText( ) {
		final float pY = resourcesManager.applyResizeFactor( 1500f );
		Text pText = new Text( 0, 0, resourcesManager.getFontDefault(),
				activity.getString( R.string.company_name ), vbom );
		pText.setPosition( appComm.getAlignCenterFloat( pText.getWidth(), camera.getWidth() ), pY );
		attachChild( pText );
		pText.setColor( appColor.FONT_DEFAULT );
		pText
				.registerEntityModifier( new ScaleModifier( 2.5f, 0.1f, 1f, 1f, 1f, EaseBackOut.getInstance() ) );
	}

	private void attachMarketShareStarAnimatedSprites( ) {
		final float pY = resourcesManager.applyResizeFactor( 1285f );
		float[] pX = appComm.getDistributedCenterOrgPosition(
				resourcesManager.regionMarketShareStar.getWidth(), 3,
				resourcesManager.applyResizeFactor( 640f ),
				( camera.getWidth() - resourcesManager.applyResizeFactor( 640f ) ) / 2f );

		AnimatedSprite aSpriteMarket = new GoMarketSharStarAnimatedSprite( pX[0], pY,
				resourcesManager.regionMarketShareStar, vbom ).activityOn( activity ).goType(
				Gotype.GO_MARKET );

		aSpriteMarket.animate( new long[] { 500, 500 }, 0, 1, true );

		AnimatedSprite aSpriteShare = new GoMarketSharStarAnimatedSprite( pX[1], pY,
				resourcesManager.regionMarketShareStar, vbom )
				.activityOn( activity )
				.goType( Gotype.GO_SHARE )
				.shareInformation( activity.getResources().getString( R.string.share_subject ),
						activity.getString( R.string.share_text ), activity.getString( R.string.app_id ) );

		aSpriteShare.animate( new long[] { 500, 500 }, 2, 3, true );

		AnimatedSprite aSpriteStar = new GoMarketSharStarAnimatedSprite( pX[2], pY,
				resourcesManager.regionMarketShareStar, vbom ).activityOn( activity ).goType( Gotype.GO_STAR )
				.appId( activity.getString( R.string.app_id ) );

		aSpriteStar.animate( new long[] { 500, 500 }, 4, 5, true );

		attachChild( aSpriteMarket );
		registerTouchArea( aSpriteMarket );
		attachChild( aSpriteStar );
		registerTouchArea( aSpriteStar );
		attachChild( aSpriteShare );
		registerTouchArea( aSpriteShare );

		aSpriteMarket.registerEntityModifier( new ScaleModifier( 0.5f, 0f, 1f ) );
		aSpriteStar.registerEntityModifier( new ScaleModifier( 0.5f, 0f, 1f ) );
		aSpriteShare.registerEntityModifier( new ScaleModifier( 0.5f, 0f, 1f ) );
	}

	private void attachPlayButtonSprite( ) {
		final float pXHalo = hsMath.getAlignCenterFloat( resourcesManager.regionCicle225.getWidth(),
				camera.getWidth() );
		final float pYHalo = resourcesManager.applyResizeFactor( 262f );

		final float pXPlayCircle = hsMath.getAlignCenterFloat( resourcesManager.regionCicle80.getWidth(),
				camera.getWidth() );
		final float pYPlayCircle = hsMath.getAlignCenterFloat( resourcesManager.regionCicle80.getHeight(),
				resourcesManager.regionCicle225.getHeight() ) + pYHalo;
		Sprite pHalo = new Sprite( pXHalo, pYHalo, resourcesManager.regionCicle225, vbom );
		pHalo.setAlpha( 0.5f );

		pHalo.registerEntityModifier( new LoopEntityModifier( new SequenceEntityModifier(
				new ScaleModifier( 0.8f, 1f, 0.2f, EaseCircularIn.getInstance() ),
				new ScaleModifier( 1f, 0.2f, 1f, EaseCircularIn.getInstance() )
				) ) );

		attachChild( pHalo );

		Sprite pPlayCircle = new Sprite( pXPlayCircle, pYPlayCircle, resourcesManager.regionCicle80, vbom ) {

			@Override
			public boolean onAreaTouched( TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
					float pTouchAreaLocalY ) {
				if ( pSceneTouchEvent.isActionDown() ) {
					this.setScale( 1.5f );
				} else {
					this.setScale( 1f );
					if ( pSceneTouchEvent.isActionUp() ) {
						onPlayButtonClick();
					}
				}
				return super.onAreaTouched( pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY );
			}

		};
		pPlayCircle.setColor( appColor.BALL );
		Sprite pTriangle = new Sprite(
				resourcesManager.applyResizeFactor( 27.5f ),
				hsMath.getAlignCenterFloat(
						resourcesManager.regionTriangle.getHeight(), pPlayCircle.getHeight() ),
				resourcesManager.regionTriangle, vbom );
		pTriangle.setColor( appColor.getShuffledPlayButtonColor() );

		pPlayCircle.attachChild( pTriangle );
		attachChild( pPlayCircle );
		registerTouchArea( pPlayCircle );

	}

	private void onPlayButtonClick( ) {
		sceneManager.createSceneGame();
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
	public void onBackKeyPressed( ) {
		Random rand = new Random();
		if ( rand.nextInt( 20 ) < 4 ) {
			if ( flag_interstitialAdOn == false ) {
				flag_interstitialAdOn = true;
				sceneManager.popAdmobInterstitialAd();
			}
		}
		appComm.backKeyPressed( 0.85f );
	}

	@Override
	public SceneType getSceneType( ) {
		return SceneType.SCENE_HOME;
	}

	@Override
	public void disposeScene( ) {
		// TODO Auto-generated method stub

	}

	@SuppressLint( "NewApi" )
	private void prepareNotificationTest( ) {// Test 1'st

		NotificationCompat.Builder mNotiBuilder = ( Builder ) new NotificationCompat.Builder( this.activity )
				.setSmallIcon( R.drawable.ic_launcher )
				.setContentTitle( this.activity.getText( R.string.app_name ) )
				.setContentText( "Upgrade your Level." ).setAutoCancel( true );

		NotificationManager notificationManager = ( NotificationManager ) this.activity
				.getSystemService( Context.NOTIFICATION_SERVICE );

		Intent intent = new Intent( Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE, Uri.parse( this.getClass()
				.getPackage().getName() ) );

		TaskStackBuilder stackBuilder = TaskStackBuilder.create( this.activity );
		PendingIntent resulPendingIntent = PendingIntent.getActivity( this.activity, 0, intent,
				Intent.FLAG_ACTIVITY_NEW_TASK );

		mNotiBuilder.setContentIntent( resulPendingIntent );

		notificationManager.notify( ( int ) System.currentTimeMillis(), mNotiBuilder.build() );

	}

}