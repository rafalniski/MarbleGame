package org.mam.eti.pg.gda.pl.marblegame;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
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
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseLinear;
import org.mam.eti.pg.gda.pl.marblegame.PathFinder.Node;
import org.mam.eti.pg.gda.pl.marblegame.utils.MathUtilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class MarbleGameActivity extends SimpleBaseGameActivity implements
		IOnSceneTouchListener {
	private static int CAMERA_WIDTH = 1280;
	private static int CAMERA_HEIGHT = 800;
	private static int BALLS_AT_THE_BEGGINING = 20;
	private static int BALL_WIDTH = 70;
	private static int BALL_HEIGHT = 70;
	private static int HOW_MANY_NEW_BALLS = 3;
	private ITextureRegion mBackgroundTextureRegion, mBack, mBallBlue,
			mStarYellow, mStarAchive, mClockAchive, mButton, mBallYellow,
			mBallGrey, mBallPurple, mBallRand, mBallRed, mBallGreen, mGrid,
			mMarked, mBallX, mBallAll;
	private Ball[][] bubbles = new Ball[10][10];
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
	private Text textStrokeAchievements;
	private static final int ACHIEVEMENT_T_SIGN = 1;
	private static final int ACHIEVEMENT_C_SIGN = 2;
	protected static final int ACHIEVEMENT_Y_SIGN = 3;
	private static int isColorUsed[] = new int[8];
	private static int combo = 0;
	private int howManyMoves = 0;
	private Sprite starYS;
	private boolean achiveColors, achiveMoves, achiveCombo;

	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions en = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		en.getAudioOptions().setNeedsMusic(true);
		return en;
	}
	private void initTextures() {
		try {
			
			ITexture strokeFontTexture = new BitmapTextureAtlas(
					this.getTextureManager(), 256, 512, TextureOptions.BILINEAR);
			
			this.mStrokeFont = new StrokeFont(this.getFontManager(),
					strokeFontTexture, Typeface.create(Typeface.DEFAULT,
							Typeface.BOLD), 64, true, Color.BLACK, 2,
					Color.WHITE);
			this.mStrokeFont.load();
			
			
			ITexture grid = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/a-grid.png");
						}
					});
			grid.load();
			this.mGrid = TextureRegionFactory.extractFromTexture(grid);
			
			ITexture starYellow = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/star_yellow.png");
						}
					});
			starYellow.load();
			
			ITexture back = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/back.jpg");
						}
					});
			back.load();
			
			ITexture marked = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/marked.png");
						}
					});
			marked.load();
			this.mMarked = TextureRegionFactory.extractFromTexture(marked);
			ITexture button = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/buttonrestart.png");
						}
					});
			button.load();
			
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
			ITexture clock = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/clock.png");
						}
					});
			clock.load();

			this.mButton = TextureRegionFactory.extractFromTexture(button);
			
			this.mStarAchive = TextureRegionFactory.extractFromTexture(star);
			this.mClockAchive = TextureRegionFactory.extractFromTexture(clock);
			this.mStarYellow = TextureRegionFactory
					.extractFromTexture(starYellow);
			this.mBack = TextureRegionFactory.extractFromTexture(back);
		} catch(IOException e) {
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
			e.printStackTrace();
		}
	}
	@Override
	protected void onCreateResources() {
		this.initTextures();
		this.initSounds();
	}

	private ITextureRegion getRandBall() {
		Random generator = new Random();
		switch (generator.nextInt(9)) {
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
			if (generator.nextInt(3) == 1) {
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
			if (generator.nextInt(3) == 1) {
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

	private Point getGridCoordinates(int x, int y) {

		int gridX = (30 + BALL_WIDTH) * x;
		int gridY = (30 + BALL_HEIGHT) * y;
		Point point = new Point(gridX, gridY);
		return point;
	}

	private Point getBubbleCoordinates(float x, float y) {
		int xBubble = (int) x / 100;
		int yBubble = (int) (y + 4) / 100;
		Log.v("klik", "Punkt klikniety x: " + (int) x / 100 + " y: "
				+ (int) ((y + 4) / 100));

		Point point = new Point(xBubble, yBubble);
		return point;
	}

	private void detachedRandBalls() {
		int randColor = MathUtilities.getRandInt(6);
		Log.e("detach", "Wywolanie programu!!!");
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (bubbles[i][j] != null
						&& (bubbles[i][j].getBallColor() == randColor || bubbles[i][j]
								.getBallColor() == 8)) {
					bubbles[i][j].detachSelf();
					Log.e("Detach", "wywołano detach na x i y : " + i + " " + j);
					bubbles[i][j] = null;
				}
			}
		}
	}

	@Override
	public boolean onSceneTouchEvent(Scene scene, TouchEvent pSceneTouchEvent) {
		int myEventAction = pSceneTouchEvent.getAction();
		switch (myEventAction) {
		case MotionEvent.ACTION_DOWN:
			this.handleGridClick(pSceneTouchEvent);
			break;
		}
		return true;
	}

	@Override
	protected Scene onCreateScene() {
		// 1 - Create new scene
		final Scene scene = new Scene();
		Sprite backgr = new Sprite(0, 0, mBack, getVertexBufferObjectManager());
		SpriteBackground backg = new SpriteBackground(backgr);
		scene.setBackground(backg);
		// scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		grid = new Sprite(0, 0, mGrid, getVertexBufferObjectManager());
		marked = new Sprite(0, 0, mMarked, getVertexBufferObjectManager());
		textStroke = new Text(850, 20, this.mStrokeFont,
				"Score\n" + this.score, 3000,
				this.getVertexBufferObjectManager());
		textStrokeAchievements = new Text(850, 190, this.mStrokeFont,
				"Achievements\n", 3000, this.getVertexBufferObjectManager());

		textStrokeNextBalls = new Text(850, 380, this.mStrokeFont,
				"Next marbles", 3000, this.getVertexBufferObjectManager());

		marked.setVisible(false);
		ButtonSprite restartButton = new ButtonSprite(1150, 750, mButton,
				getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pTouchEvent.isActionDown()) {

					// Log.e("weszlo","klik restart");
				}
				return super.onAreaTouched(pTouchEvent, pTouchAreaLocalX,
						pTouchAreaLocalY);
			}
		};
		scene.attachChild(marked);
		scene.attachChild(grid);
		scene.attachChild(textStroke);
		scene.attachChild(textStrokeAchievements);
		scene.attachChild(textStrokeNextBalls);
		scene.attachChild(restartButton);

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

		int generatedBalls = 0;
		boolean gotRandColor = false;
		while (generatedBalls < BALLS_AT_THE_BEGGINING) {
			int x = MathUtilities.getRandInt(8);
			int y = MathUtilities.getRandInt(8);
			if (bubbles[x][y] == null) {
				ITextureRegion colorRegion = getRandBall();
				boolean colorx = ballColor == 6 || ballColor == 8 ? true
						: false;
				if (colorx == true) {
					Log.e("ss", "Jestem sztoska X.");
				}
				if (ballColor == 8) {
					gotRandColor = true;
				}
				bubbles[x][y] = new Ball(colorx, this.ballColor, x, y, 15
						+ (30 + BALL_WIDTH) * x, 15 + (30 + BALL_HEIGHT) * y,
						colorRegion, getVertexBufferObjectManager());
				scene.attachChild(bubbles[x][y]);
				generatedBalls++;
			}
		}
		if (gotRandColor) {
			this.detachedRandBalls();
			gotRandColor = false;
		}
		this.generateNextBalls(scene);
		scene.setOnSceneTouchListener(this);
		scene.setTouchAreaBindingOnActionDownEnabled(true);

		return scene;
	}

	private void initScene() {
		int generatedBalls = 0;
		boolean gotRandColor = false;
		while (generatedBalls < BALLS_AT_THE_BEGGINING) {
			int x = MathUtilities.getRandInt(8);
			int y = MathUtilities.getRandInt(8);
			if (bubbles[x][y] == null) {
				ITextureRegion colorRegion = getRandBall();
				boolean colorx = ballColor == 6 || ballColor == 8 ? true
						: false;
				if (colorx == true) {
					Log.e("ss", "Jestem sztoska X.");
				}
				if (ballColor == 8) {
					gotRandColor = true;
				}
				bubbles[x][y] = new Ball(colorx, this.ballColor, x, y, 15
						+ (30 + BALL_WIDTH) * x, 15 + (30 + BALL_HEIGHT) * y,
						colorRegion, getVertexBufferObjectManager());
				mEngine.getScene().attachChild(bubbles[x][y]);
				generatedBalls++;
			}
		}
		if (gotRandColor) {
			this.detachedRandBalls();
			gotRandColor = false;
		}

		SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
		achiveColors = prefs.getBoolean("achiveColors", false);

		score = 0;
		howManyMoves = 0;
		for (int i = 0; i < 8; i++)
			isColorUsed[i] = 0;
		textStroke.setText("Score\n" + this.score);
		this.generateNextBalls(mEngine.getScene());
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

	private int[][] getPathMap() {
		int map[][] = new int[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (bubbles[i][j] == null)
					map[i][j] = 1;
				else
					map[i][j] = 0;
			}
		}
		String s = "";
		String b = "";
		for (int[] row : map) {
			s += Arrays.toString(row) + "\n";
		}
		for (Ball[] row : bubbles) {
			b += Arrays.toString(row) + "\n";
		}
		// Log.e("Mapa",s);
		// Log.e("Mapa",b);
		return map;
	}

	private boolean isBubblesFull() {
		boolean isBubblesFull = false;
		int howManyEmptySpaces = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (bubbles[i][j] == null)
					howManyEmptySpaces++;
			}
		}
		if (howManyEmptySpaces <= 3)
			isBubblesFull = true;
		return isBubblesFull;
	}

	private void addGeneratedBalls(Scene scene) {
		int generatedBalls = 0;
		while (generatedBalls < HOW_MANY_NEW_BALLS) {
			int x = MathUtilities.getRandInt(8);
			int y = MathUtilities.getRandInt(8);
			if (bubbles[x][y] == null) {
				int colorIndex = this.nextBallsShow[generatedBalls]
						.getBallColor();
				ITextureRegion color = this.getColor(colorIndex);
				this.nextBalls[generatedBalls] = new Ball(
						colorIndex == 6 ? true : false, colorIndex, 0, 0, 850,
						420 + (100 * generatedBalls), color,
						getVertexBufferObjectManager());
				this.nextBalls[generatedBalls].setX(x);
				this.nextBalls[generatedBalls].setY(y);
				bubbles[x][y] = this.nextBalls[generatedBalls];
				Point gridXY = this.getGridCoordinates(x, y);
				bubbles[x][y].setPosition(gridXY.x + 15, gridXY.y + 15);
				scene.attachChild(bubbles[x][y]);
				generatedBalls++;
				this.checkPattern(bubbles[x][y].getBallColor());
				if (colorIndex == 8)
					detachedRandBalls();

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

		if (howManyMoves == 10 && achiveMoves != true) {
			prefsEditor.putBoolean("achiveMoves", true);
			prefsEditor.apply();
			achiveMoves = true;
			starS = new Sprite(850, 280, mStarAchive,
					getVertexBufferObjectManager());
			mEngine.getScene().attachChild(starS);
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					MarbleGameActivity.this.showDialog(ACHIEVEMENT_T_SIGN);
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
					MarbleGameActivity.this.showDialog(ACHIEVEMENT_C_SIGN);
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
					MarbleGameActivity.this.showDialog(ACHIEVEMENT_Y_SIGN);
				}
			});
		}

	}

	private void checkPattern(int ballColor) {

		// wyszukiwanie w kolumnach
		boolean czyZnaleziono = false;
		for (int h = 0; h < 8; h++) {
			for (int i = 0; i < 8 - 5 + 1; i++) {
				int j = 1;
				if (bubbles[h][i + j - 1] != null) {
					while (j <= 5
							&& bubbles[h][i + j - 1] != null
							&& (bubbles[h][i + j - 1].getBallColor() == ballColor || bubbles[h][i
									+ j - 1].getBallColor() == 7)) {
						j++;
						if (j == 6) {
							Log.w("pattern",
									"Hurra! Znaleziono 5 kulek - wiersz: " + h
											+ " kolumna: " + i + "\n");
							scoreMusic.play();
							for (int k = 0; k < 5; k++) {
								bubbles[h][i].setBlendFunction(
										GL10.GL_SRC_ALPHA,
										GL10.GL_ONE_MINUS_SRC_ALPHA);
								IEntityModifier iem = new AlphaModifier(2f, 0,
										255);
								iem.setAutoUnregisterWhenFinished(true);
								bubbles[h][i].registerEntityModifier(iem);
								bubbles[h][i].detachSelf();
								bubbles[h][i] = null;
								i++;
							}
							czyZnaleziono = true;
							combo++;
							score = score + 50;
							textStroke.setText("Score\n" + this.score);
							return;
						}

					}
				}
			}
		}
		// wyszukiwanie w wierszach
		for (int h = 0; h < 8; h++) {
			for (int i = 0; i < 8 - 5 + 1; i++) {
				int j = 1;
				if (bubbles[i + j - 1][h] != null) {
					while (j <= 5
							&& bubbles[i + j - 1][h] != null
							&& (bubbles[i + j - 1][h].getBallColor() == ballColor || bubbles[i
									+ j - 1][h].getBallColor() == 7)) {
						j++;
						if (j == 6) {
							Log.w("pattern",
									"Hurra! Znaleziono 5 kulek - wiersz: " + h
											+ " kolumna: " + i + "\n");
							scoreMusic.play();
							for (int k = 0; k < 5; k++) {
								bubbles[i][h].setBlendFunction(
										GL10.GL_SRC_ALPHA,
										GL10.GL_ONE_MINUS_SRC_ALPHA);
								IEntityModifier iem = new AlphaModifier(2f, 0,
										255);
								iem.setAutoUnregisterWhenFinished(true);
								bubbles[i][h].registerEntityModifier(iem);
								bubbles[i][h].detachSelf();
								bubbles[i][h] = null;
								i++;
							}
							czyZnaleziono = true;
							combo++;
							score = score + 50;
							textStroke.setText("Score\n" + this.score);
							return;
						}

					}
				}
			}
		}
		// wyszukiwanie po przekatnej w lewo
		for (int h = 0; h < 8; h++) {
			for (int i = 0; i < 8 - 5 + 1; i++) {
				int j = 1;
				if (bubbles[i + j - 1][h + j - 1] != null) {
					while (j <= 5
							&& bubbles[i + j - 1][h + j - 1] != null
							&& (bubbles[i + j - 1][h + j - 1].getBallColor() == ballColor || bubbles[i
									+ j - 1][h + j - 1].getBallColor() == 7)) {
						j++;
						if (j == 6) {
							scoreMusic.play();
							for (int k = 0; k < 5; k++) {
								bubbles[i][h].detachSelf();
								bubbles[i][h] = null;
								i++;
								h++;
							}
							czyZnaleziono = true;
							combo++;
							score = score + 50;
							textStroke.setText("Score\n" + this.score);
							return;
						}

					}
				}
			}
		}
		// wyszukiwanie po przekatnej w prawo
		for (int h = 0; h < 8 - 5 + 1; h++) {
			for (int i = 0; i < 8; i++) {
				int j = 1;
				int w = -1;
				if (bubbles[i + w + 1][h + j - 1] != null) {
					while (w >= -5
							&& (i + w - 1) >= 0
							&& bubbles[i + w + 1][h + j - 1] != null
							&& (bubbles[i + w + 1][h + j - 1].getBallColor() == ballColor || bubbles[i
									+ w + 1][h + j - 1].getBallColor() == 7)) {
						j++;
						w--;
						if (w == -6) {
							scoreMusic.play();
							for (int k = 0; k < 5; k++) {
								bubbles[i][h].detachSelf();
								bubbles[i][h] = null;
								i--;
								h++;
							}
							czyZnaleziono = true;
							combo++;
							score = score + 50;
							textStroke.setText("Score\n" + this.score);
							return;
						}
					}
				}
			}
		}
		combo = 0;
	}

	private void resetGame() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (bubbles[i][j] != null) {
					bubbles[i][j].detachSelf();
					bubbles[i][j] = null;
				}
			}
		}
		initScene();
		score = 0;
	}

	private int[][] getPathMap2() {
		int map[][] = new int[10][10];
		for (int i = 0; i < 10; i++) {
			map[0][i] = 1;
			map[9][i] = 1;
			map[i][0] = 1;
			map[i][9] = 1;
		}
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (bubbles[i][j] == null)
					map[j + 1][i + 1] = 0;
				else
					map[j + 1][i + 1] = 1;
			}
		}
		return map;
	}

	private void handleGridClick(TouchEvent pSceneTouchEvent) {
		final Point bubbleXY = this.getBubbleCoordinates(
				pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		// gdy w kliknietym miejscu jest babelek
		if (bubbleXY.x == 12 && bubbleXY.y == 7) {

			resetGame();

		} else if (bubbleXY.x >= 8 || bubbleXY.y > 7) {
			// kliknieta poza siatke, nic nie robimy
		} else if (bubbles[bubbleXY.x][bubbleXY.y] != null) {
			bubbleToMove = bubbles[bubbleXY.x][bubbleXY.y]; // oznaczamy, ze
															// bedzie przesuwany
			Point gridXY = this.getGridCoordinates(bubbleXY.x, bubbleXY.y); // dostajemy
																			// koordynaty
																			// siatki
			// oznaczamy odpowiednia grafiką zaznaczony babelek
			if (marked.isVisible() == false) {
				marked.setPosition(gridXY.x, gridXY.y);
				marked.setVisible(true);
			} else {
				marked.setPosition(gridXY.x, gridXY.y);
			}
		} else if (bubbleToMove != null && bubbleToMove.isX() == true) {
			// nic nie robomy kliknieta nieprzesuwalną kulkę.
		} else if (marked.isVisible() == false) {
			// nic nie rob kliknieto puste pole
		}

		else { // klikniete zostalo puste pole - przenosimy tam babelek

			if (marked.isVisible() == true && isBubblesFull() == false) {
				marked.setVisible(false); // wylaczamy ozaczenie klikniecia
				// dostajemy koordynaty siatki
				isColorUsed[bubbleToMove.getBallColor()] = 1;
				final int XcurrentBall = (int) bubbleToMove.getX();
				final int YCurrentBall = (int) bubbleToMove.getY();

				Point gridXY = this.getGridCoordinates(bubbleXY.x, bubbleXY.y);
				Point gridstartXY = this.getGridCoordinates(bubbleXY.x,
						bubbleXY.y);

				final int[][] gridMap = this.getPathMap2();
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
							gridXY = this.getGridCoordinates(point.x, point.y);
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
						getEngine().runOnUpdateThread(new Runnable() {
							@Override
							public void run() {
								bubbles[bubbleXY.x][bubbleXY.y] = bubbles[XcurrentBall][YCurrentBall];
								bubbles[bubbleXY.x][bubbleXY.y]
										.setX(bubbleXY.x);
								bubbles[bubbleXY.x][bubbleXY.y]
										.setY(bubbleXY.y);
								bubbles[XcurrentBall][YCurrentBall] = null;
								bubbleToMove = null;

								if (score > 0)
									score--;
								textStroke.setText("Score\n" + score);
								checkPattern(bubbles[bubbleXY.x][bubbleXY.y]
										.getBallColor());
								checkAchivements();
								addGeneratedBalls(getEngine().getScene());
								generateNextBalls(getEngine().getScene());
							}
						});
					}
				};
				mPathModifier.setPathModifierListener(mIPathModifierListener);
				mPathModifier.setAutoUnregisterWhenFinished(true);
				bubbles[XcurrentBall][YCurrentBall]
						.registerEntityModifier(mPathModifier);
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