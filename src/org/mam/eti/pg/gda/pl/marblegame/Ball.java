package org.mam.eti.pg.gda.pl.marblegame;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.mam.eti.pg.gda.pl.marblegame.utils.MathUtilities;

public class Ball extends Sprite {
    private int mWeight;
    public static final int BALL_WIDTH = 70;
    public static final int BALL_HEIGHT = 70;
    private Sprite mBall;
    private float x;
    private float y;
    private int ballColor;
    private boolean isX = false;
    public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}
	public static ITextureRegion getRandBall(BubblesGrid bubbles) {
		switch (MathUtilities.getRandInt(9)) {
		case 0:
			bubbles.setCurrentBallColor(0);
			return TextureRegion.mBallGreen;
		case 1:
			bubbles.setCurrentBallColor(1);
			return TextureRegion.mBallGrey;
		case 2:
			bubbles.setCurrentBallColor(2);
			return TextureRegion.mBallBlue;
		case 3:
			bubbles.setCurrentBallColor(3);
			return TextureRegion.mBallYellow;
		case 4:
			bubbles.setCurrentBallColor(4);
			return TextureRegion.mBallPurple;
		case 5:
			bubbles.setCurrentBallColor(5);
			return TextureRegion.mBallRed;
		case 6:
			if (MathUtilities.getRandInt(3) == 1) {
				bubbles.setCurrentBallColor(6);
				return TextureRegion.mBallX;
			} else {
				bubbles.setCurrentBallColor(3);
				return TextureRegion.mBallYellow;
			}
		case 7:
			bubbles.setCurrentBallColor(7);
			return TextureRegion.mBallAll;
		case 8:
			if (MathUtilities.getRandInt(3) == 1) {
				bubbles.setCurrentBallColor(8);
				return TextureRegion.mBallRand;
			} else {
				bubbles.setCurrentBallColor(4);
				return TextureRegion.mBallPurple;
			}
		default:
			bubbles.setCurrentBallColor(5);
			return TextureRegion.mBallRed;
		}
	}
	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	
    public int getBallColor() {
		return ballColor;
	}

	public void setBallColor(int ballColor) {
		this.ballColor = ballColor;
	}
	public boolean isX() {
		return this.isX;
	}
	
	public Ball(int color,float x, float y, float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        this.x = x;
        this.y = y;
        this.ballColor = color;
        
    }
	public Ball(boolean isX, int color,float x, float y, float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        this.x = x;
        this.y = y;
        this.ballColor = color;
        this.isX = isX;
        
    }

    public int getmWeight() {
        return mWeight;
    }
}
