package dalcoms.pub.fingerbrickbreaker;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import dalcoms.pub.fingerbrickbreaker.scene.SceneGame;

public class HaloOfBallSprite extends Sprite {

	private SceneGame mSceneGame;
	private boolean flagSelected = false;

	public HaloOfBallSprite( float pX, float pY, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager ) {
		super( pX, pY, pTextureRegion, pVertexBufferObjectManager );
	}

	public HaloOfBallSprite setSceneGame( SceneGame pSceneGame ) {
		this.mSceneGame = pSceneGame;
		return this;
	}

	private void catchBall( ) {
		mSceneGame.getMainBall().getBody().setLinearVelocity( 0f, 0f );
	}

	public boolean isSelected( ) {
		return flagSelected;
	}

	public void setFlagSelected( boolean pFlagSelected ) {
		this.flagSelected = pFlagSelected;
		float alpha = pFlagSelected ? 0.8f : 0.2f;
		this.setAlpha( alpha );
	}

	public void setSelectedAlpha( boolean isSelected ) {
		if ( isSelected ) {
			this.setAlpha( 0.85f );
		} else {
			this.setAlpha( 0.2f );
		}
	}

	public float getCenterX( ) {
		return this.getX() + this.getWidth() * 0.5f;
	}

	public float getCenterY( ) {
		return this.getY() + this.getHeight() * 0.5f;
	}

	public void setCenterPosition( float pCenterX, float pCenterY ) {
		this.setPosition( pCenterX - this.getWidth() * 0.5f, pCenterY - this.getHeight() * 0.5f );
	}

}
