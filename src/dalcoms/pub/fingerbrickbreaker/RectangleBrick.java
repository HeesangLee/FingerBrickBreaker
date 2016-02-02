package dalcoms.pub.fingerbrickbreaker;

import dalcoms.pub.fingerbrickbreaker.scene.SceneGame;

public class RectangleBrick extends RectanglePhysics {

	private int mBreakLevel = 1;

	public RectangleBrick( float pX, float pY, float pWidth, float pHeight, SceneGame pSceneGame ) {
		super( pX, pY, pWidth, pHeight, pSceneGame );
	}

	public RectangleBrick( float pWidth, float pHeight, SceneGame pSceneGame ) {
		super( 0, 0, pWidth, pHeight, pSceneGame );
	}

	public RectangleBrick setBreakLevel( int pBreakLevel ) {
		this.mBreakLevel = pBreakLevel;
		return this;
	}

	public int getBreakLevel( ) {
		return this.mBreakLevel;
	}

	@Override
	public void onUpdateCheck( ) {
		if ( checkBreakMySelf( checkCollisionWithBall() ) == true ) {
			this.scaleAlphaByeBye( 0.5f );
		}
	}

	private boolean checkCollisionWithBall( ) {
		boolean result = false;
		if ( this.collidesWith( getGameScene().getMainBall() ) ) {
			result = true;
		}
		return result;
	}

	private boolean checkBreakMySelf( boolean pBreak ) {
		//		if break level reach to zero, byebye myself
		boolean result = false;
		if ( pBreak ) {
			final int pBreakLevel = getBreakLevel() - 1;
			if ( pBreakLevel > 0 ) {
				setBreakLevel( pBreakLevel );
				this.breakMySelf();
			} else if ( pBreakLevel == 0 ) {
				result = true;
			}
		}
		return result;
	}

	private void breakMySelf( ) {
		if ( this.getWidth() > this.getHeight() ) {
			this.setWidth( this.getWidth() * 0.5f );
		} else {
			this.setHeight( this.getHeight() * 0.5f );
		}
	}
}
