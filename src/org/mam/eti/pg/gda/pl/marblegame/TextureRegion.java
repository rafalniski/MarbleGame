package org.mam.eti.pg.gda.pl.marblegame;

import org.andengine.opengl.texture.region.ITextureRegion;
/*
 * Class for storing ITextureRegion objects
 * If you want to use any new texture in a game, add it here
 */
public class TextureRegion {
	/* Main screen background texture */
	public static ITextureRegion mBack;
	/* Blue ball texture */
	public static ITextureRegion mBallBlue;
	/* Texture for achievement of using all of the ball colors */
	public static ITextureRegion mStarYellow; 
	/* Texture for having 10 moves achievement */
	public static ITextureRegion mStarAchive; 
	/* Texture for having 2 scores in a row */
	public static ITextureRegion mClockAchive;
	/* Texture for reset button */
	public static ITextureRegion mButton;
	/* Yellow ball texture */
	public static ITextureRegion mBallYellow;
	/* Grey ball texture */
	public static ITextureRegion mBallGrey;
	/* Purple ball texture */
	public static ITextureRegion mBallPurple; 
	/* Random ball texture - after showing, all of the balls with that color are detached */
	public static ITextureRegion mBallRand;
	/* Red ball texture */
	public static ITextureRegion mBallRed;
	/* Green ball texture */
	public static ITextureRegion mBallGreen; 
	/* Background grid - currently 8x8 */
	public static ITextureRegion mGrid;
	/* Semi-transparent texture for marking ball to move */
	public static ITextureRegion mMarked; 
	/* X ball texture - it's immovable */
	public static ITextureRegion mBallX;
	/* All ball texture - it can be matched with any color */
	public static ITextureRegion mBallAll;
	
	public static ITextureRegion getColor(int i) {
		switch (i) {
		case 0:
			return TextureRegion.mBallGreen;
		case 1:
			return TextureRegion.mBallGrey;
		case 2:
			return TextureRegion.mBallBlue;
		case 3:
			return TextureRegion.mBallYellow;
		case 4:
			return TextureRegion.mBallPurple;
		case 5:
			return TextureRegion.mBallRed;
		case 6:
			return TextureRegion.mBallX;
		case 7:
			return TextureRegion.mBallAll;
		case 8:
			return TextureRegion.mBallRand;
		default:
			return TextureRegion.mBallRed;
		}
	}
}
