package com.marbles;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.StrokeFont;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;

import com.marbles.PathFinder.Node;
import com.marbles.utils.Logger;
import com.marbles.utils.MathUtilities;
import com.marblesheaven.R;

public class AStar extends SimpleBaseGameActivity implements
		IOnSceneTouchListener {
	private static final int ACHIEVEMENT_NUMBER_OF_MOVES = 10;
	private static int CAMERA_WIDTH = 1280;
	private static int CAMERA_HEIGHT = 800;
	private static int BALLS_AT_THE_BEGGINING = 20;
	private static int HOW_MANY_NEW_BALLS = 3;
	private ITextureRegion mBackgroundTextureRegion, mBack, mBallBlue,
			mStarYellow, mStarAchive, mClockAchive, mButton, mBallYellow,
			mBallGrey, mBallPurple, mBallRand, mBallRed, mBallGreen, mGrid,
			mMarked, mBallX, mBallAll;
	private BubblesGrid bubbles = new BubblesGrid();
	private Ball bubbleToMove = null;
	private Sprite grid, marked, starS, clockS;
	private int ballColor;
	private PathModifier mPathModifier;
	private IPathModifierListener mIPathModifierListener;
	private StrokeFont mStrokeFont;
	private ITexture fontTexture;
	private Text textStroke, textStrokeNextBalls;
	private int score = 0;
	private Ball[] nextBalls = new Ball[HOW_MANY_NEW_BALLS];
	private Ball[] nextBallsShow = new Ball[HOW_MANY_NEW_BALLS];
	private boolean isGameOver = false;
	private Music moveMusic;
	private Music scoreMusic;
	private Music failMusic;
	private Music gameOverMusic;
	private Grid gameGrid;
	private Text textStrokeAchievements;
	private static final int ACHIEVEMENT_T_SIGN = 1;
	private static final int ACHIEVEMENT_C_SIGN = 2;
	protected static final int ACHIEVEMENT_Y_SIGN = 3;
	private static int isColorUsed[] = new int[8];
	private static int combo = 0;
	private int howManyMoves = 0;
	private Sprite starYS;
	private boolean achiveColors, achiveMoves, achiveCombo;
	private Achievement stats;
	private ButtonSprite restartButton;
	private Sprite backgroundSprite;
	private SpriteBackground spriteBackground;
	private Scene scene = new Scene();

	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions en = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		en.getAudioOptions().setNeedsMusic(true);
		return en;
	}
	
	@Override
	protected void onCreateResources() {
		this.initTexturesAndSprites();
		this.initSounds();
	}
	
	@Override
	protected Scene onCreateScene() {
		
		backgroundSprite =  new Sprite(0, 0, mBack, getVertexBufferObjectManager());
		spriteBackground = 	new SpriteBackground(backgroundSprite);
		restartButton = 	new ButtonSprite(1150, 750, mButton, getVertexBufferObjectManager());
		
		scene.setBackground(spriteBackground);
		scene.attachChild(marked);
		scene.attachChild(grid);
		scene.attachChild(textStroke);
		scene.attachChild(textStrokeAchievements);
		scene.attachChild(textStrokeNextBalls);
		scene.attachChild(restartButton);
		
		stats = new Achievement();
		gameGrid = new Grid();
		initAchievements(scene);
		initScene();
		
		scene.setOnSceneTouchListener(this);
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		
		return scene;
	}
	private void loadPreferences() {
		SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
		achiveColors = prefs.getBoolean("achiveColors", false);
	}
	private void initScene() {
		int generatedBalls = 0;
		boolean gotRandColor = false;
		while (generatedBalls < BALLS_AT_THE_BEGGINING) {
			int x = MathUtilities.getRandInt(8);
			int y = MathUtilities.getRandInt(8);
			if (bubbles.isBubblesNull(x, y)) {
				ITextureRegion colorRegion = getRandBall();
				boolean colorx = ballColor == 6 || ballColor == 8 ? true
						: false;
				if (ballColor == 8) {
					gotRandColor = true;
				}
				Ball newBall = new Ball(colorx, this.ballColor, x, y, 15
						+ (30 + Ball.BALL_WIDTH) * x, 15 + (30 + Ball.BALL_HEIGHT) * y,
						colorRegion, getVertexBufferObjectManager());
				bubbles.setBubbles(x, y, newBall);
				scene.attachChild(bubbles.getBubble(x, y));
				generatedBalls++;
			}
		}
		if (gotRandColor) {
			bubbles.detachedRandBalls();
			gotRandColor = false;
		}
		
		loadPreferences();
		score = 0;
		howManyMoves = 0;
		for (int i = 0; i < 8; i++)
			isColorUsed[i] = 0;
		textStroke.setText("Score\n" + this.score);
		this.generateNextBalls(scene);
	}
	
	private void initAchievements(Scene scene) {
		SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
		achiveColors = prefs.getBoolean("achiveColors", false);
		if (achiveColors) {
			starYS = new Sprite(910, 280, mStarYellow,
					getVertexBufferObjectManager());

			scene.attachChild(starYS);
		}
		achiveMoves = prefs.getBoolean("achiveMoves", false);
		if (achiveMoves) {
			starS = new Sprite(850, 280, mStarAchive,
					getVertexBufferObjectManager());

			scene.attachChild(starS);
		}

		achiveCombo = prefs.getBoolean("achiveCombo", false);
		if (achiveCombo) {
			clockS = new Sprite(970, 280, mClockAchive,
					getVertexBufferObjectManager());
			scene.attachChild(clockS);
		}
	}
	private void initTexturesAndSprites() {
		try {
			
			ITexture strokeFontTexture = new BitmapTextureAtlas(
					this.getTextureManager(), 256, 512, TextureOptions.BILINEAR);
			this.mStrokeFont = new StrokeFont(this.getFontManager(),
					strokeFontTexture, Typeface.create(Typeface.DEFAULT,
							Typeface.BOLD), 64, true, Color.BLACK, 2,
					Color.WHITE);
			this.mStrokeFont.load();
			
			textStroke = new Text(850, 20, this.mStrokeFont,
					"Score\n" + this.score, 3000,
					this.getVertexBufferObjectManager());
			textStrokeAchievements = new Text(850, 190, this.mStrokeFont,
					"Achievements\n", 3000, this.getVertexBufferObjectManager());

			textStrokeNextBalls = new Text(850, 380, this.mStrokeFont,
					"Next marbles", 3000, this.getVertexBufferObjectManager());
			
			ITexture gridTexture = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/a-grid.png");
						}
					});
			gridTexture.load();
			this.mGrid = TextureRegionFactory.extractFromTexture(gridTexture);
			grid = new Sprite(0, 0, mGrid, getVertexBufferObjectManager());
			
			ITexture starYellow = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/star_yellow.png");
						}
					});
			starYellow.load();
			this.mStarYellow = TextureRegionFactory.extractFromTexture(starYellow);

			ITexture back = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/back.jpg");
						}
					});
			back.load();
			this.mBack = TextureRegionFactory.extractFromTexture(back);
			
			ITexture markedTexture = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/marked.png");
						}
					});
			markedTexture.load();
			this.mMarked = TextureRegionFactory.extractFromTexture(markedTexture);
			marked = new Sprite(0, 0, mMarked, getVertexBufferObjectManager());
			marked.setVisible(false);
			
			ITexture button = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/buttonrestart.png");
						}
					});
			button.load();
			this.mButton = TextureRegionFactory.extractFromTexture(button);

			ITexture ballBlue = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/ballblue.png");
						}
					});
			ballBlue.load();
			this.mBallBlue = TextureRegionFactory.extractFromTexture(ballBlue);
			
			ITexture ballGrey = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/ballgrey.png");
						}
					});
			ballGrey.load();
			this.mBallGrey = TextureRegionFactory.extractFromTexture(ballGrey);
			
			ITexture ballGreen = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/ballgreen.png");
						}
					});
			ballGreen.load();
			this.mBallGreen = TextureRegionFactory.extractFromTexture(ballGreen);
			
			ITexture ballPurple = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/ballpurple.png");
						}
					});
			ballPurple.load();
			this.mBallPurple = TextureRegionFactory.extractFromTexture(ballPurple);
			
			ITexture ballRed = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/ballred.png");

						}
					});
			ballRed.load();
			this.mBallRed = TextureRegionFactory.extractFromTexture(ballRed);
			
			ITexture ballYellow = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/ballyellow.png");
						}
					});
			ballYellow.load();
			this.mBallYellow = TextureRegionFactory.extractFromTexture(ballYellow);
			
			ITexture ballX = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/ballx.png");
						}
					});
			ballX.load();
			this.mBallX = TextureRegionFactory.extractFromTexture(ballX);
			
			ITexture ballAll = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/ballall.png");
						}
					});
			ballAll.load();
			this.mBallAll = TextureRegionFactory.extractFromTexture(ballAll);
			
			ITexture ballRand = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/ballrand.png");
						}
					});
			ballRand.load();
			this.mBallRand = TextureRegionFactory.extractFromTexture(ballRand);
			
			ITexture star = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/star.png");
						}
					});
			star.load();
			this.mStarAchive = TextureRegionFactory.extractFromTexture(star);

			ITexture clock = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/clock.png");
						}
					});
			clock.load();
			this.mClockAchive = TextureRegionFactory.extractFromTexture(clock);
			
		} catch(IOException e) {
			Log.e(Logger.LOG_TEXTURE_LOAD_ERROR, 
						"Error during loading textures, check if texture file exists");
			e.printStackTrace();
			
		}
	}
	
	private void initSounds() {
		try {
			
			failMusic = MusicFactory.createMusicFromAsset(
					mEngine.getMusicManager(), this, "snd/fail.mp3");
			moveMusic = MusicFactory.createMusicFromAsset(
					mEngine.getMusicManager(), this, "snd/moveMusic.wav");
			scoreMusic = MusicFactory.createMusicFromAsset(
					mEngine.getMusicManager(), this, "snd/scoreMusic.mp3");
			gameOverMusic = MusicFactory.createMusicFromAsset(
					mEngine.getMusicManager(), this,
					"snd/gameOverMusic.wav");
		} catch (IOException e) {
			Log.e(Logger.LOG_MUSIC_LOAD_ERROR,
						"Error during sound loading, check if sound file exists");
			e.printStackTrace();
		}
	}
	

	private ITextureRegion getRandBall() {
		switch (MathUtilities.getRandInt(9)) {
		case 0:
			ballColor = 0;
			return mBallGreen;
		case 1:
			ballColor = 1;
			return mBallGrey;
		case 2:
			ballColor = 2;
			return mBallBlue;
		case 3:
			ballColor = 3;
			return mBallYellow;
		case 4:
			ballColor = 4;
			return mBallPurple;
		case 5:
			ballColor = 5;
			return mBallRed;
		case 6:
			if (MathUtilities.getRandInt(3) == 1) {
				ballColor = 6;
				return mBallX;
			} else {
				ballColor = 3;
				return mBallYellow;
			}
		case 7:
			ballColor = 7;
			return mBallAll;
		case 8:
			if (MathUtilities.getRandInt(3) == 1) {
				ballColor = 8;
				return mBallRand;
			} else {
				ballColor = 4;
				return mBallPurple;
			}
		default:
			ballColor = 5;
			return mBallRed;
		}
	}
	
	private ITextureRegion getColor(int i) {
		switch (i) {
		case 0:
			return mBallGreen;
		case 1:
			return mBallGrey;
		case 2:
			return mBallBlue;
		case 3:
			return mBallYellow;
		case 4:
			return mBallPurple;
		case 5:
			return mBallRed;
		case 6:
			return mBallX;
		case 7:
			return mBallAll;
		case 8:
			return mBallRand;
		default:
			return mBallRed;
		}
	}

	

	@Override
	public boolean onSceneTouchEvent(Scene scene, TouchEvent pSceneTouchEvent) {
		int myEventAction = pSceneTouchEvent.getAction();
		switch (myEventAction) {
		case MotionEvent.ACTION_DOWN:
			this.handleGridClick2(pSceneTouchEvent);
			break;
		}
		return true;
	}

	

	

	private void generateNextBalls(Scene scene) {
		int generatedBalls = 0;
		while (generatedBalls < HOW_MANY_NEW_BALLS) {
			ITextureRegion colorRegion = getRandBall();
			boolean colorx = ballColor == 6 ? true : false;
			if (colorx == true) {
				Log.e("ss", "Jestem sztoska X.");
			}
			this.nextBallsShow[generatedBalls] = new Ball(colorx,
					this.ballColor, 0, 0, 850, 500 + (100 * generatedBalls),
					colorRegion, getVertexBufferObjectManager());
			scene.attachChild(this.nextBallsShow[generatedBalls++]);
		}

	}

	private void addGeneratedBalls(Scene scene) {
		int generatedBalls = 0;
		while (generatedBalls < HOW_MANY_NEW_BALLS) {
			int x = MathUtilities.getRandInt(8);
			int y = MathUtilities.getRandInt(8);
			if (bubbles.isBubblesNull(x, y)) {
				int colorIndex = this.nextBallsShow[generatedBalls]
						.getBallColor();
				ITextureRegion color = this.getColor(colorIndex);
				this.nextBalls[generatedBalls] = new Ball(
						colorIndex == 6 ? true : false, colorIndex, 0, 0, 850,
						420 + (100 * generatedBalls), color,
						getVertexBufferObjectManager());
				this.nextBalls[generatedBalls].setX(x);
				this.nextBalls[generatedBalls].setY(y);
				bubbles.setBubbles(x, y, this.nextBalls[generatedBalls]);
				Point gridXY = gameGrid.getGridCoordinates(x, y, Ball.BALL_WIDTH, Ball.BALL_HEIGHT);
				bubbles.getBubble(x, y).setPosition(gridXY.x + 15, gridXY.y + 15);
				//bubbles[x][y].setPosition(gridXY.x + 15, gridXY.y + 15);
				scene.attachChild(bubbles.getBubble(x, y));
				generatedBalls++;
				boolean checkResult = bubbles.checkPattern(bubbles.getBubble(x,y).getBallColor(), stats);
				if(checkResult) {
					scoreMusic.play();
					textStroke.setText("Score\n" + stats.getScore());
				}
				if (colorIndex == 8)
					bubbles.detachedRandBalls();

			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case ACHIEVEMENT_T_SIGN:
			mEngine.stop();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Achievement unlocked!")
					.setMessage(
							"Congratulation, you have done at least 10 moves!")
					.setIcon(R.drawable.star)
					.setPositiveButton("Ok.",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									mEngine.start();

								}
							});
			return builder.create();
		case ACHIEVEMENT_C_SIGN:
			mEngine.stop();
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Achievement unlocked!")
					.setMessage(
							"Congratulation, you have score 2 matches in a row!")
					.setIcon(R.drawable.clock)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									mEngine.start();

								}
							});
			return builder.create();
		case ACHIEVEMENT_Y_SIGN:
			mEngine.stop();
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Achievement unlocked!")
					.setMessage(
							"Congratulations, you have used all of the balls colors!")
					.setIcon(R.drawable.star_yellow)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									mEngine.start();

								}
							});
			return builder.create();
		default:
			return null;
		}
	}

	@Override
	protected synchronized void onResume() {
		super.onResume();
		SharedPreferences myPrefs = getSharedPreferences("myPrefs",
				MODE_PRIVATE);
		// myPrefs.edit().clear().commit();
		if (!achiveMoves) {
			achiveMoves = myPrefs.getBoolean("achiveMoves", false);
		}
		if (!achiveCombo) {
			achiveCombo = myPrefs.getBoolean("achiveCombo", false);
		}
		if (!achiveColors) {
			achiveColors = myPrefs.getBoolean("achiveColors", false);
		}
	}

	private void checkAchivements() {
		SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
		Editor prefsEditor = prefs.edit();

		if (howManyMoves == ACHIEVEMENT_NUMBER_OF_MOVES && achiveMoves != true) {
			prefsEditor.putBoolean("achiveMoves", true);
			prefsEditor.apply();
			achiveMoves = true;
			starS = new Sprite(850, 280, mStarAchive,
					getVertexBufferObjectManager());
			mEngine.getScene().attachChild(starS);
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showDialog(ACHIEVEMENT_T_SIGN);
				}
			});
		}
		if (combo > 1 && achiveCombo != true) {
			achiveCombo = true;
			prefsEditor.putBoolean("achiveCombo", true);
			prefsEditor.apply();
			clockS = new Sprite(970, 280, mClockAchive,
					getVertexBufferObjectManager());
			mEngine.getScene().attachChild(clockS);
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showDialog(ACHIEVEMENT_C_SIGN);
				}
			});
		}
		boolean areAllUsed = true;
		for (int i = 0; i < 6; i++) {
			if (isColorUsed[i] != 1)
				areAllUsed = false;
		}
		if (areAllUsed && !achiveColors) {
			prefsEditor.putBoolean("achiveColors", true);
			prefsEditor.apply();
			achiveColors = true;
			starYS = new Sprite(910, 280, mStarYellow,
					getVertexBufferObjectManager());
			mEngine.getScene().attachChild(starYS);
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showDialog(ACHIEVEMENT_Y_SIGN);
				}
			});
		}

	}
	
	private void resetGame() {
		bubbles.resetBubbles();
		initScene();
		score = 0;
	}

	private void handleBubbleMove(final Point bubbleXY, final int XcurrentBall, final int YCurrentBall) {

		
	}
	private void handleGameOver() {
		textStroke.setText("Final Score\n" + this.score);
		if (!isGameOver) {
			for (int i = 0; i < 3; i++) {
				this.nextBallsShow[i].detachSelf();
				this.nextBallsShow[i] = null;
				this.textStrokeNextBalls.setText("");
			}
			gameOverMusic.play();
			isGameOver = true;
		}
	}
	private void handleGridClick(TouchEvent pSceneTouchEvent) {
		// Getting bubble coordinates from touched point.
		final Point bubbleXY = gameGrid.getBubbleCoordinates(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		
		// When we click reset button
		// TODO: delete it and make onspritebuttonclick method
		if (bubbleXY.x == 12 && bubbleXY.y == 7) {
			resetGame();
			
		// When we click outside the grid, do nothing
		// TODO: make game only clickable on grid area
		} else if (bubbleXY.x >= 8 || bubbleXY.y > 7) {
			
		// We clicked bubble on a grid
		} else if (!bubbles.isBubblesNull(bubbleXY.x, bubbleXY.y)) {
			// Make an object of "bubble that will be moved"
			bubbleToMove = bubbles.getBubble(bubbleXY.x, bubbleXY.y);
			// Get coordinates of grid 
			Point gridXY = gameGrid.getGridCoordinates(bubbleXY.x, bubbleXY.y, Ball.BALL_WIDTH, Ball.BALL_HEIGHT);
			// We marked our clicked bubble with semi-transparent sprite
			marked.setPosition(gridXY.x, gridXY.y);
			// At the beginning sprite is invisible, so we set its visibility
			if (marked.isVisible() == false) {
				marked.setVisible(true);
				
		// We click X ball, do nothing
		} else if (bubbleToMove != null && bubbleToMove.isX() == true) {
		
		// We clicked empty point, do nothing	
		} else if (marked.isVisible() == false) {
			
		// We clicked empty point and there is bubble to move, to move it there
		} else {
			if (marked.isVisible() == true && bubbles.isBubblesFull() == false) {
		
			// Set marked sprite invisible
			marked.setVisible(false);
			
			// Fill array of "all-of-the-colors-used" achievement
			isColorUsed[bubbleToMove.getBallColor()] = 1;
			
			// Get coordinates of clicked ball
			final int XcurrentBall = (int) bubbleToMove.getX();
			final int YCurrentBall = (int) bubbleToMove.getY();
			
			// Move ball to clicked place
			// bubbleXY - clicked empty space on a grid
			// X,YcurrentBall - clicked ball to move
			//handleBubbleMove(bubbleXY,XcurrentBall, YCurrentBall);
			
			gridXY = gameGrid.getGridCoordinates(bubbleXY.x, bubbleXY.y, Ball.BALL_WIDTH, Ball.BALL_HEIGHT);
			Point gridstartXY = gameGrid.getGridCoordinates(bubbleXY.x,
					bubbleXY.y,Ball.BALL_WIDTH, Ball.BALL_HEIGHT);

			final int[][] gridMap = gameGrid.getPathMap(bubbles);
			NewPathFinder finder = new NewPathFinder(gridMap);
			NewPathFinder.Point startPoint = finder.new Point(
					XcurrentBall + 1, YCurrentBall + 1);
			NewPathFinder.Point endPoint = finder.new Point(bubbleXY.x + 1,
					bubbleXY.y + 1);

			List<Node> nodes = finder.solve(startPoint, endPoint);
			Path path = null;
			if (nodes != null) {
				marked.setVisible(false); // wylaczamy ozaczenie klikniecia

				int length = nodes != null ? nodes.size() : 2;
				path = new Path(length != 0 ? length : 2);
				if (nodes != null) {
					for (Node point : nodes) {
						gridXY = gameGrid.getGridCoordinates(point.x, point.y, Ball.BALL_WIDTH, Ball.BALL_HEIGHT);
						path.to(gridXY.x + 15, gridXY.y + 15);
					}
				}
			}
			howManyMoves++;
			if (path == null) {
				failMusic.play();
				return;
			}
			PathModifier mPathModifier = new PathModifier(
					path.getLength() / 400, path); // wyrownanie predkosci
			mIPathModifierListener = new IPathModifierListener() {
				@Override
				public void onPathStarted(final PathModifier pPathModifier,
						final IEntity pEntity) {
					Debug.d("onPathStarted");
				}

				@Override
				public void onPathWaypointStarted(
						PathModifier pPathModifier, IEntity pEntity,
						int pWaypointIndex) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onPathWaypointFinished(
						PathModifier pPathModifier, IEntity pEntity,
						int pWaypointIndex) {

				}

				@Override
				public void onPathFinished(PathModifier pPathModifier,
						IEntity pEntity) {
					getEngine().runOnUpdateThread(new Runnable() {
						@Override
						public void run() {
							bubbles.setBubbles(bubbleXY.x, bubbleXY.y, bubbles.getBubble(XcurrentBall, YCurrentBall));
							bubbles.getBubble(bubbleXY.x, bubbleXY.y).setX(bubbleXY.x);
							bubbles.getBubble(bubbleXY.x, bubbleXY.y).setY(bubbleXY.y);
							bubbles.setBubbleNull(XcurrentBall, YCurrentBall);
							bubbleToMove = null;

							if (score > 0)
								score--;
							textStroke.setText("Score\n" + score);
							boolean checkResult = bubbles.checkPattern(
									bubbles.getBubble(bubbleXY.x, bubbleXY.y).getBallColor(), stats);
							if(checkResult) {
								scoreMusic.play();
								textStroke.setText("Score\n" + stats.getScore());
							}
							checkAchivements();
							addGeneratedBalls(getEngine().getScene());
							generateNextBalls(getEngine().getScene());
						}
					});
				}
			};
			mPathModifier.setPathModifierListener(mIPathModifierListener);
			mPathModifier.setAutoUnregisterWhenFinished(true);
			bubbles.getBubble(XcurrentBall, YCurrentBall).registerEntityModifier(mPathModifier);
			
			moveMusic.play();
			
			// There no more space to move the ball, finish game
			} else {
				handleGameOver();
				
			}
		}

		}

	}
	

	private void handleGridClick2(TouchEvent pSceneTouchEvent) {
		final Point bubbleXY = gameGrid.getBubbleCoordinates(
				pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		// gdy w kliknietym miejscu jest babelek
		if (bubbleXY.x == 12 && bubbleXY.y == 7) {

			resetGame();

		} else if (bubbleXY.x >= 8 || bubbleXY.y > 7) {
			// kliknieta poza siatke, nic nie robimy
		} else if (!bubbles.isBubblesNull(bubbleXY.x, bubbleXY.y)) {
			bubbleToMove = bubbles.getBubble(bubbleXY.x, bubbleXY.y); // oznaczamy, ze
															// bedzie przesuwany
			Point gridXY = gameGrid.getGridCoordinates(bubbleXY.x, bubbleXY.y, Ball.BALL_WIDTH, Ball.BALL_HEIGHT); // dostajemy
																			// koordynaty
																			// siatki
			// oznaczamy odpowiednia grafik?? zaznaczony babelek
			if (marked.isVisible() == false) {
				marked.setPosition(gridXY.x, gridXY.y);
				marked.setVisible(true);
			} else {
				marked.setPosition(gridXY.x, gridXY.y);
			}
		} else if (bubbleToMove != null && bubbleToMove.isX() == true) {
			// nic nie robomy kliknieta nieprzesuwaln?? kulk??.
		} else if (marked.isVisible() == false) {
			// nic nie rob kliknieto puste pole
		}

		else { // klikniete zostalo puste pole - przenosimy tam babelek

			if (marked.isVisible() == true && bubbles.isBubblesFull() == false) {
				marked.setVisible(false); // wylaczamy ozaczenie klikniecia
				// dostajemy koordynaty siatki
				isColorUsed[bubbleToMove.getBallColor()] = 1;
				final int XcurrentBall = (int) bubbleToMove.getX();
				final int YCurrentBall = (int) bubbleToMove.getY();

				Point gridXY = gameGrid.getGridCoordinates(bubbleXY.x, bubbleXY.y, Ball.BALL_WIDTH, Ball.BALL_HEIGHT);
				Point gridstartXY = gameGrid.getGridCoordinates(bubbleXY.x, bubbleXY.y, Ball.BALL_WIDTH, Ball.BALL_HEIGHT);

				final int[][] gridMap = gameGrid.getPathMap(bubbles);
				NewPathFinder finder = new NewPathFinder(gridMap);
				NewPathFinder.Point startPoint = finder.new Point(
						XcurrentBall + 1, YCurrentBall + 1);
				NewPathFinder.Point endPoint = finder.new Point(bubbleXY.x + 1,
						bubbleXY.y + 1);

				List<Node> nodes = finder.solve(startPoint, endPoint);
				Path path = null;
				if (nodes != null) {
					marked.setVisible(false); // wylaczamy ozaczenie klikniecia

					int length = nodes != null ? nodes.size() : 2;
					path = new Path(length != 0 ? length : 2);
					if (nodes != null) {
						for (Node point : nodes) {
							gridXY = gameGrid.getGridCoordinates(bubbleXY.x, bubbleXY.y, Ball.BALL_WIDTH, Ball.BALL_HEIGHT);
							path.to(gridXY.x + 15, gridXY.y + 15);
						}
					}
				}
				howManyMoves++;
				int lenght = nodes != null ? nodes.size() : 2;
				if (path == null) {
					failMusic.play();
					return;
				}
				PathModifier mPathModifier = new PathModifier(
						path.getLength() / 400, path); // wyrownanie predkosci
				mIPathModifierListener = new IPathModifierListener() {
					@Override
					public void onPathStarted(final PathModifier pPathModifier,
							final IEntity pEntity) {
						Debug.d("onPathStarted");
					}

					@Override
					public void onPathWaypointStarted(
							PathModifier pPathModifier, IEntity pEntity,
							int pWaypointIndex) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onPathWaypointFinished(
							PathModifier pPathModifier, IEntity pEntity,
							int pWaypointIndex) {

					}

					@Override
					public void onPathFinished(PathModifier pPathModifier,
							IEntity pEntity) {
						int j = 0;
						j =6;
						
						getEngine().runOnUpdateThread(new Runnable() {
							@Override
							public void run() {
								
							}
						});
					}
				};
				bubbles.setBubbles(bubbleXY.x, bubbleXY.y, bubbles.getBubble(XcurrentBall, YCurrentBall));
				bubbles.getBubble(bubbleXY.x, bubbleXY.y).setX(bubbleXY.x);
				bubbles.getBubble(bubbleXY.x, bubbleXY.y).setY(bubbleXY.y);
				bubbles.setBubbleNull(XcurrentBall, YCurrentBall);
				bubbleToMove = null;
				if (score > 0)
					score--;
				textStroke.setText("Score\n" + score);
				bubbles.checkPattern(bubbles.getBubble(bubbleXY.x, bubbleXY.y).getBallColor(), stats);
				checkAchivements();
				addGeneratedBalls(getEngine().getScene());
				generateNextBalls(getEngine().getScene());
				mPathModifier.setPathModifierListener(mIPathModifierListener);
				mPathModifier.setAutoUnregisterWhenFinished(true);
				bubbles.getBubble(XcurrentBall, YCurrentBall).registerEntityModifier(mPathModifier);
				moveMusic.play();

			} else {
				textStroke.setText("Final Score\n" + this.score);
				if (!isGameOver) {
					for (int i = 0; i < 3; i++) {
						this.nextBallsShow[i].detachSelf();
						this.nextBallsShow[i] = null;
						this.textStrokeNextBalls.setText("");
					}
					gameOverMusic.play();
					isGameOver = true;
				}
			}

		}

	}
	
	
}