package org.mam.eti.pg.gda.pl.marblegame;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

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
