package org.mam.eti.pg.gda.pl.marblegame;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.StrokeFont;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.color.Color;

import android.graphics.Typeface;
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
	public static ButtonSprite restartButton;
	public static SpriteBackground spriteBackground;
	public static StrokeFont mStrokeFont;
	public static Text textStrokeAchievements,textStroke, textStrokeNextBalls;
	public static Sprite grid, marked;
	
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
	
	public static void initSprites(MarbleGameActivity activity) {
		marked 						=  new Sprite(0, 0, TextureRegion.mMarked, activity.getVertexBufferObjectManager());
		grid 						=  new Sprite(0, 0, TextureRegion.mGrid, activity.getVertexBufferObjectManager());
		spriteBackground 			=  new SpriteBackground(new Sprite(0, 0, TextureRegion.mBack, activity.getVertexBufferObjectManager()));
		restartButton 				=  new ButtonSprite(1150, 750, TextureRegion.mButton, activity.getVertexBufferObjectManager());
		textStroke 					=  new Text(850, 20, mStrokeFont,"Score\n" + "0", 3000,activity.getVertexBufferObjectManager());
		textStrokeAchievements 		=  new Text(850, 190, mStrokeFont,"Achievements\n", 3000, activity.getVertexBufferObjectManager());
		textStrokeNextBalls 		=  new Text(850, 380, mStrokeFont,"Next marbles", 3000, activity.getVertexBufferObjectManager());
		marked.setVisible(false);
	}

	public static void initTextures(final MarbleGameActivity activity) {
		try {
			ITexture gridTexture = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/a-grid.png");
						}
					});
			gridTexture.load();
			TextureRegion.mGrid = TextureRegionFactory.extractFromTexture(gridTexture);
			
			ITexture strokeFontTexture = new BitmapTextureAtlas(
					activity.getTextureManager(), 256, 512, TextureOptions.BILINEAR);
			mStrokeFont = new StrokeFont(activity.getFontManager(),
					strokeFontTexture, Typeface.create(Typeface.DEFAULT,
							Typeface.BOLD), 64, true, Color.BLACK, 2,
					Color.WHITE);
			mStrokeFont.load();
			
			ITexture starYellow = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/star_yellow.png");
						}
					});
			starYellow.load();
			TextureRegion.mStarYellow = TextureRegionFactory.extractFromTexture(starYellow);

			ITexture back = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/back.jpg");
						}
					});
			back.load();
			TextureRegion.mBack = TextureRegionFactory.extractFromTexture(back);
			
			ITexture markedTexture = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/marked.png");
						}
					});
			markedTexture.load();
			TextureRegion.mMarked = TextureRegionFactory.extractFromTexture(markedTexture);
			
			
			ITexture button = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/buttonrestart.png");
						}
					});
			button.load();
			TextureRegion.mButton = TextureRegionFactory.extractFromTexture(button);

			ITexture ballBlue = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/ballblue.png");
						}
					});
			ballBlue.load();
			TextureRegion.mBallBlue = TextureRegionFactory.extractFromTexture(ballBlue);
			
			ITexture ballGrey = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/ballgrey.png");
						}
					});
			ballGrey.load();
			TextureRegion.mBallGrey = TextureRegionFactory.extractFromTexture(ballGrey);
			
			ITexture ballGreen = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/ballgreen.png");
						}
					});
			ballGreen.load();
			TextureRegion.mBallGreen = TextureRegionFactory.extractFromTexture(ballGreen);
			
			ITexture ballPurple = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/ballpurple.png");
						}
					});
			ballPurple.load();
			TextureRegion.mBallPurple = TextureRegionFactory.extractFromTexture(ballPurple);
			
			ITexture ballRed = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/ballred.png");

						}
					});
			ballRed.load();
			TextureRegion.mBallRed = TextureRegionFactory.extractFromTexture(ballRed);
			
			ITexture ballYellow = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/ballyellow.png");
						}
					});
			ballYellow.load();
			TextureRegion.mBallYellow = TextureRegionFactory.extractFromTexture(ballYellow);
			
			ITexture ballX = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/ballx.png");
						}
					});
			ballX.load();
			TextureRegion.mBallX = TextureRegionFactory.extractFromTexture(ballX);
			
			ITexture ballAll = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/ballall.png");
						}
					});
			ballAll.load();
			TextureRegion.mBallAll = TextureRegionFactory.extractFromTexture(ballAll);
			
			ITexture ballRand = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/ballrand.png");
						}
					});
			ballRand.load();
			TextureRegion.mBallRand = TextureRegionFactory.extractFromTexture(ballRand);
			
			ITexture star = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/star.png");
						}
					});
			star.load();
			TextureRegion.mStarAchive = TextureRegionFactory.extractFromTexture(star);

			ITexture clock = new BitmapTexture(activity.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/clock.png");
						}
					});
			clock.load();
			TextureRegion.mClockAchive = TextureRegionFactory.extractFromTexture(clock);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
