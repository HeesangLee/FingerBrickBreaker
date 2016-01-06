package dalcoms.pub.fingerbrickbreaker;

import dalcoms.pub.fingerbrickbreaker.scene.SceneGame;

public class RectangleBrick extends RectanglePhysics {

	public RectangleBrick( float pX, float pY, float pWidth, float pHeight, SceneGame pSceneGame ) {
		super( pX, pY, pWidth, pHeight, pSceneGame );
	}

	public RectangleBrick( float pWidth, float pHeight, SceneGame pSceneGame ) {
		super( 0, 0, pWidth, pHeight, pSceneGame );
	}
}
