package dalcoms.pub.fingerbrickbreaker.scene;

import lib.dalcoms.andengineheesanglib.utils.HsMath;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.ease.EaseBackOut;
import org.andengine.util.modifier.ease.EaseCircularIn;
import org.andengine.util.modifier.ease.EaseExponentialIn;
import org.andengine.util.modifier.ease.IEaseFunction;

import android.app.Activity;
import android.text.style.EasyEditSpan;
import dalcoms.pub.fingerbrickbreaker.R;
import dalcoms.pub.fingerbrickbreaker.ResourcesManager;

public class SceneSplash extends BaseScene {

	HsMath hsMath = new HsMath();
	private int numberOfBackKey = 0;

	@Override
	public void createScene( ) {
		this.setBackground( new Background( this.appColor.APP_BACKGROUND ) );

		this.engine.runOnUpdateThread( new Runnable() {

			@Override
			public void run( ) {
				attachTexts( activity.getResources().getString( R.string.app_name ), activity.getResources()
						.getString( R.string.company_name ) );
				attachSprites();
			}
		} );

	}

	@Override
	public void attachSprites( ) {
		attachProgressRectangles( 6 );

	}

	private void attachProgressRectangles( int pRectCount ) {
		final float pRectWidth = resourcesManager.applyResizeFactor( 80f );
		final float pRectHeight = pRectWidth;
		final float pOffset = 170f;

		final float pXs[] = hsMath.getDistributedCenterOrgPosition( pRectWidth, pRectCount, camera.getWidth()
				- 2 * pOffset, pOffset );
		final float pY = hsMath.getAlignCenterFloat( pRectHeight, camera.getHeight() );

		for ( int i = 0 ; i < pRectCount ; i++ ) {
			Rectangle pRect = new Rectangle( 0f, pY, pRectWidth, pRectHeight, vbom );
			pRect.setColor( appColor.RECT_LOADING );
			attachChild( pRect );
			pRect.registerEntityModifier( new ParallelEntityModifier(
					new ScaleModifier( 1.2f, 0.05f, 1f ),
					new SequenceEntityModifier(
							new MoveXModifier( 1.2f, camera.getWidth(), pXs[i],
									EaseCircularIn.getInstance() ),
							new RotationModifier( 0.3f, 0f, 45f, EaseCircularIn.getInstance() ),
							new RotationModifier( 0.3f, 45f, 0f, EaseCircularIn.getInstance() )
					) )
					);
		}
	}

	private void attachTexts( String pUpperString, String pLowerString ) {
		Text pUpperText, pLowerText;

		pUpperText = new Text( 0, 0, resourcesManager.getFontDefault(), pUpperString, vbom );
		pUpperText.setPosition( hsMath.getAlignCenterFloat( pUpperText.getWidth(), camera.getWidth() ),
				resourcesManager.applyResizeFactor( 678f ) );

		pLowerText = new Text( 0, 0, resourcesManager.getFontDefault(), pLowerString, vbom );
		pLowerText.setPosition( hsMath.getAlignCenterFloat( pLowerText.getWidth(), camera.getWidth() ),
				resourcesManager.applyResizeFactor( 1055f ) );

		pUpperText.setColor( appColor.FONT_DEFAULT );
		pLowerText.setColor( appColor.FONT_DEFAULT );

		attachChild( pUpperText );
		attachChild( pLowerText );

		pUpperText
				.registerEntityModifier( new ScaleModifier( 2f, 0.1f, 1f, 1f, 1f, EaseBackOut.getInstance() ) );
		pLowerText
				.registerEntityModifier( new ScaleModifier( 2f, 0.1f, 1f, 1f, 1f, EaseBackOut.getInstance() ) );

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
		if ( numberOfBackKey++ == 3 ) {
			sceneManager.popAdmobInterstitialAd();
		} else if ( numberOfBackKey > 10 ) {
			resourcesManager.getActivity().onDestroy();
		}

	}

	@Override
	public SceneType getSceneType( ) {
		return SceneType.SCENE_SPLASH;
	}

	@Override
	public void disposeScene( ) {
		// TODO Auto-generated method stub

	}

}