package dalcoms.pub.fingerbrickbreaker.scene;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import lib.dalcoms.andengineheesanglib.utils.HsMath;
import lib.dalcoms.data.GameLevel;

import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import dalcoms.pub.fingerbrickbreaker.R;
import dalcoms.pub.fingerbrickbreaker.ResourcesManager;
import dalcoms.pub.fingerbrickbreaker.level.JsonDataLevelData;

public class SceneManager {
	private final String TAG = this.getClass().getSimpleName();
	private static final SceneManager instance = new SceneManager();

	private boolean flagResultInterstitialAdOn = false;
	private boolean forTstore = false;

	private BaseScene currentScene;

	private BaseScene sceneHome;
	private BaseScene sceneSplash;
	private BaseScene sceneGame;
	private BaseScene sceneReplay;

	private SceneType currentSceneType;

	private int countReplay = 0;
	final int POP_AD_REPLAY = 15;

	private final float GAME_GRAVITY = 35f;
	private final float GAME_TIMER_TIME_SECOND = 0.1f; // 100msec

	//	private GameLevel mGameLevel = new GameLevel();

	private JsonDataLevelData mLevelData;

	public static SceneManager getInstance( ) {
		return instance;
	}

	public void initGameLevelData( ) {// Load data from local repository
										// database.

		new LoadJsonLevelDataTask().execute();
	}

	public JsonDataLevelData getLevelData( ) {
		return this.mLevelData;
	}

	private class LoadJsonLevelDataTask extends AsyncTask<Void, Void, JsonDataLevelData> {

		@Override
		protected JsonDataLevelData doInBackground( Void... params ) {
			String pJsonLevelString = loadJsonDataFile( "gameLevelData.json" );

			Type pLevelDataType = new TypeToken<JsonDataLevelData>() {
			}.getType();

			JsonDataLevelData result = new GsonBuilder().create().fromJson( pJsonLevelString, pLevelDataType );

			return result;
		}

		@Override
		protected void onPostExecute( JsonDataLevelData result ) {
			mLevelData = result;
		}

		private String loadJsonDataFile( String pJsonFileName ) {
			String strJson = null;
			try {

				InputStream is = ResourcesManager.getInstance().getActivity().getAssets()
						.open( pJsonFileName );
				int size = is.available();
				byte[] buffer = new byte[size];
				is.read( buffer );
				is.close();
				strJson = new String( buffer, "UTF-8" );
			} catch ( IOException e ) {
				e.printStackTrace();
				return null;
			}

			return strJson;
		}

	}

	public void setScene( BaseScene pScene ) {
		ResourcesManager.getInstance().getEngine().setScene( pScene );
		this.currentScene = pScene;
		this.currentSceneType = pScene.getSceneType();
	}

	public void setScene( SceneType pSceneType ) {
		switch ( pSceneType ) {

			case SCENE_HOME :
				setScene( sceneHome );
				break;
			case SCENE_SPLASH :
				setScene( sceneSplash );
				break;
			case SCENE_GAME :
				setScene( sceneGame );
			case SCENE_REPLAY :
				setScene( sceneReplay );
			default :
				setScene( sceneHome );
				Log.e( TAG, "Scene creation is done with unexpected routine" );
				break;
		}
	}

	public BaseScene getCurrentScene( ) {
		return this.currentScene;
	}

	public SceneType getCurrentSceneType( ) {
		return this.currentSceneType;
	}

	public void createSceneSplash( OnCreateSceneCallback pOnCreateSceneCallback ) {
		this.sceneSplash = new SceneSplash();
		this.currentScene = this.sceneSplash;
		this.currentSceneType = this.sceneSplash.getSceneType();

		pOnCreateSceneCallback.onCreateSceneFinished( this.currentScene );
	}

	public void createSceneSplash( ) {
		this.sceneSplash = new SceneSplash();
		this.clearScene( this.currentSceneType );
		this.setScene( this.sceneSplash );
	}

	public void createSceneGame( ) {
		this.sceneGame = new SceneGame();
		this.clearScene( this.currentSceneType );
		this.setScene( this.sceneGame );
	}

	public void createSceneHome( ) {
		this.sceneHome = new SceneHome();
		this.clearScene( this.currentSceneType );
		this.setScene( this.sceneHome );
	}

	public void createSceneReplay( ) {
		if ( ++countReplay > POP_AD_REPLAY ) {
			countReplay = 0;
			this.popAdmobInterstitialAd();
		}
		this.sceneReplay = new SceneReplay();
		this.clearScene( this.currentSceneType );
		this.setScene( this.sceneReplay );
	}

	private void clearScene( SceneType pSceneType ) {
		switch ( pSceneType ) {
			case SCENE_HOME :
				this.disposeSceneHome();
				break;
			case SCENE_SPLASH :
				this.disposeSceneSplash();
				break;
			case SCENE_GAME :
				this.disposeSceneGame();
				break;
			case SCENE_REPLAY :
				this.disposeSceneReplay();
			default :
				Log.v( "Dispose Scene Error", "Some Scene selection is not correct" );
				break;
		}
	}

	private void disposeSceneSplash( ) {
		this.sceneSplash.disposeScene();
		this.sceneSplash = null;
	}

	private void disposeSceneHome( ) {
		this.sceneHome.disposeScene();
		this.sceneHome = null;
	}

	private void disposeSceneGame( ) {
		this.sceneGame.disposeScene();
		this.sceneGame = null;
	}

	private void disposeSceneReplay( ) {
		this.sceneReplay.disposeScene();
		this.sceneReplay = null;
	}

	private void displayAdmobInterstitialAd( InterstitialAd pAd ) {
		if ( pAd.isLoaded() ) {
			pAd.show();
		}
	}

	public void popAdmobInterstitialAd( ) {
		final InterstitialAd adMobInterstitialAd = new InterstitialAd( ResourcesManager.getInstance()
				.getActivity() );
		adMobInterstitialAd.setAdUnitId( ResourcesManager.getInstance().getActivity()
				.getString( R.string.admob_interstitial_id ) );
		final AdRequest adRequest = new AdRequest.Builder().build();
		ResourcesManager.getInstance().getActivity().runOnUiThread( new Runnable() {

			@Override
			public void run( ) {
				adMobInterstitialAd.loadAd( adRequest );
			}
		} );

		adMobInterstitialAd.setAdListener( new AdListener() {
			public void onAdLoaded( ) {
				displayAdmobInterstitialAd( adMobInterstitialAd );
			}
		} );
	}

	public boolean isResultInterstitialAdOn( ) {
		return flagResultInterstitialAdOn;
	}

	public boolean isForTStore( ) {
		return forTstore;
	}

	public float getGameGravity( ) {
		return ResourcesManager.getInstance().applyResizeFactor( GAME_GRAVITY );
	}

	public float getGameTimerTimeSecond( ) {
		return GAME_TIMER_TIME_SECOND;
	}
}
