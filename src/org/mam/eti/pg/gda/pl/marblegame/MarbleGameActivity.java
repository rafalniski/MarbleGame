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
import org.mam.eti.pg.gda.pl.marblegame.utils.Logger;
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
	private static final int CAMERA_WIDTH = 1280;
	private static final int CAMERA_HEIGHT = 800;
	private BubblesGrid bubbles;
	private Ball bubbleToMove;
	private Sprite grid, marked, starS, clockS;
	private PathModifier mPathModifier;
	private IPathModifierListener mIPathModifierListener;
	private StrokeFont mStrokeFont;
	private ITexture fontTexture;
	private Text textStroke, textStrokeNextBalls;
	private Ball[] nextBalls = new Ball[BubblesGrid.HOW_MANY_NEW_BALLS];
	private Ball[] nextBallsShow = new Ball[BubblesGrid.HOW_MANY_NEW_BALLS];
	private boolean isGameOver = false;
	private Music moveMusic;
	private Music scoreMusic;
	private Music failMusic;
	private Music gameOverMusic;
	private Grid gameGrid;
	private Text textStrokeAchievements;
	
	private static int isColorUsed[] = new int[8];
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
		backgroundSprite =  new Sprite(0, 0, TextureRegion.mBack, getVertexBufferObjectManager());
		spriteBackground = 	new SpriteBackground(backgroundSprite);
		restartButton = 	new ButtonSprite(1150, 750, TextureRegion.mButton, getVertexBufferObjectManager());
		
		scene.setBackground(spriteBackground);
		scene.attachChild(marked);
		scene.attachChild(grid);
		scene.attachChild(textStroke);
		scene.attachChild(textStrokeAchievements);
		scene.attachChild(textStrokeNextBalls);
		scene.attachChild(restartButton);
		
		bubbles = new BubblesGrid();
		stats = new Achievement();
		gameGrid = new Grid();
		initAchievements(scene);
		initScene();
		
		scene.setOnSceneTouchListener(this);
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		
		return scene;
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
	public boolean onSceneTouchEvent(Scene scene, TouchEvent pSceneTouchEvent) {
		int myEventAction = pSceneTouchEvent.getAction();
		switch (myEventAction) {
		case MotionEvent.ACTION_DOWN:
			this.handleGridClick(pSceneTouchEvent);
			break;
		}
		return true;
	}

	
	
	private void loadPreferences() {
		SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
		achiveColors = prefs.getBoolean("achiveColors", false);
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
					"Score\n" + "0", 3000,
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
			TextureRegion.mGrid = TextureRegionFactory.extractFromTexture(gridTexture);
			grid = new Sprite(0, 0, TextureRegion.mGrid, getVertexBufferObjectManager());
			
			ITexture starYellow = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/star_yellow.png");
						}
					});
			starYellow.load();
			TextureRegion.mStarYellow = TextureRegionFactory.extractFromTexture(starYellow);

			ITexture back = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/back.jpg");
						}
					});
			back.load();
			TextureRegion.mBack = TextureRegionFactory.extractFromTexture(back);
			
			ITexture markedTexture = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/marked.png");
						}
					});
			markedTexture.load();
			TextureRegion.mMarked = TextureRegionFactory.extractFromTexture(markedTexture);
			marked = new Sprite(0, 0, TextureRegion.mMarked, getVertexBufferObjectManager());
			marked.setVisible(false);
			
			ITexture button = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/buttonrestart.png");
						}
					});
			button.load();
			TextureRegion.mButton = TextureRegionFactory.extractFromTexture(button);

			ITexture ballBlue = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/ballblue.png");
						}
					});
			ballBlue.load();
			TextureRegion.mBallBlue = TextureRegionFactory.extractFromTexture(ballBlue);
			
			ITexture ballGrey = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/ballgrey.png");
						}
					});
			ballGrey.load();
			TextureRegion.mBallGrey = TextureRegionFactory.extractFromTexture(ballGrey);
			
			ITexture ballGreen = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/ballgreen.png");
						}
					});
			ballGreen.load();
			TextureRegion.mBallGreen = TextureRegionFactory.extractFromTexture(ballGreen);
			
			ITexture ballPurple = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/ballpurple.png");
						}
					});
			ballPurple.load();
			TextureRegion.mBallPurple = TextureRegionFactory.extractFromTexture(ballPurple);
			
			ITexture ballRed = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/ballred.png");

						}
					});
			ballRed.load();
			TextureRegion.mBallRed = TextureRegionFactory.extractFromTexture(ballRed);
			
			ITexture ballYellow = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/ballyellow.png");
						}
					});
			ballYellow.load();
			TextureRegion.mBallYellow = TextureRegionFactory.extractFromTexture(ballYellow);
			
			ITexture ballX = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/ballx.png");
						}
					});
			ballX.load();
			TextureRegion.mBallX = TextureRegionFactory.extractFromTexture(ballX);
			
			ITexture ballAll = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/ballall.png");
						}
					});
			ballAll.load();
			TextureRegion.mBallAll = TextureRegionFactory.extractFromTexture(ballAll);
			
			ITexture ballRand = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/ballrand.png");
						}
					});
			ballRand.load();
			TextureRegion.mBallRand = TextureRegionFactory.extractFromTexture(ballRand);
			
			ITexture star = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/star.png");
						}
					});
			star.load();
			TextureRegion.mStarAchive = TextureRegionFactory.extractFromTexture(star);

			ITexture clock = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/clock.png");
						}
					});
			clock.load();
			TextureRegion.mClockAchive = TextureRegionFactory.extractFromTexture(clock);
			
		} catch(IOException e) {
			Log.e(Logger.LOG_TEXTURE_LOAD_ERROR, 
						"Error during loading textures, check if texture file exists");
			e.printStackTrace();
			
		}
	}
	private void initAchievements(Scene scene) {
		SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
		achiveColors = prefs.getBoolean("achiveColors", false);
		if (achiveColors) {
			starYS = new Sprite(910, 280, TextureRegion.mStarYellow,
					getVertexBufferObjectManager());

			scene.attachChild(starYS);
		}
		achiveMoves = prefs.getBoolean("achiveMoves", false);
		if (achiveMoves) {
			starS = new Sprite(850, 280, TextureRegion.mStarAchive,
					getVertexBufferObjectManager());

			scene.attachChild(starS);
		}

		achiveCombo = prefs.getBoolean("achiveCombo", false);
		if (achiveCombo) {
			clockS = new Sprite(970, 280, TextureRegion.mClockAchive,
					getVertexBufferObjectManager());
			scene.attachChild(clockS);
		}
	}
	
	private void initScene() {
		int generatedBalls = 0;
		boolean gotRandColor = false;
		while (generatedBalls < BubblesGrid.BALLS_AT_THE_BEGGINING) {
			int x = MathUtilities.getRandInt(8);
			int y = MathUtilities.getRandInt(8);
			if (bubbles.isBubblesNull(x, y)) {
				ITextureRegion colorRegion = Ball.getRandBall(bubbles);
				boolean colorx = bubbles.getCurrentBallColor() == 6 || bubbles.getCurrentBallColor()  == 8 ? true
						: false;
				if (bubbles.getCurrentBallColor()  == 8) {
					gotRandColor = true;
				}
				Ball newBall = new Ball(colorx, bubbles.getCurrentBallColor() , x, y, 15
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
		stats.setScore(0);
		howManyMoves = 0;
		for (int i = 0; i < 8; i++)
			isColorUsed[i] = 0;
		textStroke.setText("Score\n" + stats.getScore());
		this.generateNextBalls(scene);
	}

	private void generateNextBalls(Scene scene) {
		int generatedBalls = 0;
		while (generatedBalls < BubblesGrid.HOW_MANY_NEW_BALLS) {
			ITextureRegion colorRegion = Ball.getRandBall(bubbles);
			boolean colorx = bubbles.getCurrentBallColor()  == 6 ? true : false;
			if (colorx == true) {
				Log.e("ss", "Jestem sztoska X.");
			}
			this.nextBallsShow[generatedBalls] = new Ball(colorx,
					bubbles.getCurrentBallColor() , 0, 0, 850, 500 + (100 * generatedBalls),
					colorRegion, getVertexBufferObjectManager());
			scene.attachChild(this.nextBallsShow[generatedBalls++]);
		}

	}

	

	private void addGeneratedBalls(Scene scene) {
		int generatedBalls = 0;
		while (generatedBalls < BubblesGrid.HOW_MANY_NEW_BALLS) {
			int x = MathUtilities.getRandInt(BubblesGrid.GRID_COLUMNS-1);
			int y = MathUtilities.getRandInt(BubblesGrid.GRID_ROWS-1);
			if (bubbles.isBubblesNull(x, y)) {
				int colorIndex = this.nextBallsShow[generatedBalls]
						.getBallColor();
				ITextureRegion color = TextureRegion.getColor(colorIndex);
				this.nextBalls[generatedBalls] = new Ball(
						colorIndex == 6 ? true : false, colorIndex, 0, 0, 850,
						420 + (100 * generatedBalls), color,
						getVertexBufferObjectManager());
				this.nextBalls[generatedBalls].setX(x);
				this.nextBalls[generatedBalls].setY(y);
				bubbles.setBubbles(x, y,this.nextBalls[generatedBalls]);
				Point gridXY = gameGrid.getGridCoordinates(x, y, Ball.BALL_WIDTH, Ball.BALL_HEIGHT);
				bubbles.getBubble(x, y).setPosition(gridXY.x + 15, gridXY.y + 15);
				scene.attachChild(bubbles.getBubble(x, y));
				generatedBalls++;
				boolean checkResult = bubbles.checkPattern(
						bubbles.getBubble(x,y).getBallColor(), stats);
				if(checkResult == true) {
					scoreMusic.play();
					stats.setComboAchievementCounter(1);
				} else {
					stats.resetCombo();
				}
				textStroke.setText("Score\n" + stats.getScore());
				if (colorIndex == 8)
					bubbles.detachedRandBalls();

			}
		}
	}
	private AlertDialog createDialog(String title, String message, int icon) {
		mEngine.stop();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title)
				.setMessage(message)
				.setIcon(icon)
				.setPositiveButton("Ok.",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {

								mEngine.start();

							}
						});
		return builder.create();
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Achievement.ACHIEVEMENT_MOVES:
			return createDialog(
					"Achievement unlocked!", 
					"Congratulation, you have done at least 10 moves!", 
					R.drawable.star);
		case Achievement.ACHIEVEMENT_COMBO:
			return createDialog(
					"Achievement unlocked!", 
					"Congratulation, you have score 2 matches in a row!", 
					R.drawable.clock);
		case Achievement.ACHIEVEMENT_COLORS:
			return createDialog(
					"Achievement unlocked!", 
					"Congratulations, you have used all of the balls colors!", 
					R.drawable.star_yellow);
		default:
			return null;
		}
	}

	@Override
	protected synchronized void onResume() {
		super.onResume();
		SharedPreferences myPrefs = getSharedPreferences("myPrefs",
				MODE_PRIVATE);
		//myPrefs.edit().clear().commit();
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
			starS = new Sprite(850, 280, TextureRegion.mStarAchive,
					getVertexBufferObjectManager());
			mEngine.getScene().attachChild(starS);
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					MarbleGameActivity.this.showDialog(Achievement.ACHIEVEMENT_MOVES);
				}
			});
		}
		if (stats.getComboAchievementCounter() > 1 && achiveCombo != true) {
			achiveCombo = true;
			prefsEditor.putBoolean("achiveCombo", true);
			prefsEditor.apply();
			clockS = new Sprite(970, 280, TextureRegion.mClockAchive,
					getVertexBufferObjectManager());
			mEngine.getScene().attachChild(clockS);
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					MarbleGameActivity.this.showDialog(Achievement.ACHIEVEMENT_COMBO);
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
			starYS = new Sprite(910, 280, TextureRegion.mStarYellow,
					getVertexBufferObjectManager());
			mEngine.getScene().attachChild(starYS);
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					MarbleGameActivity.this.showDialog(Achievement.ACHIEVEMENT_COLORS);
				}
			});
		}

	}

	private void resetGame() {
		bubbles.resetBubbles();
		initScene();
		stats.setScore(0);
	}


	private void handleGridClick(TouchEvent pSceneTouchEvent) {
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

			if (marked.isVisible() == true && bubbles.isBubblesFull() == false) {
				marked.setVisible(false); // wylaczamy ozaczenie klikniecia
				// dostajemy koordynaty siatki
				isColorUsed[bubbleToMove.getBallColor()] = 1;
				final int XcurrentBall = (int) bubbleToMove.getX();
				final int YCurrentBall = (int) bubbleToMove.getY();

				Point gridXY = gameGrid.getGridCoordinates(bubbleXY.x, bubbleXY.y,Ball.BALL_WIDTH, Ball.BALL_HEIGHT);
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
							gridXY = gameGrid.getGridCoordinates(point.x, point.y,Ball.BALL_WIDTH, Ball.BALL_HEIGHT);
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
								bubbles.setBubbles(bubbleXY.x, bubbleXY.y, bubbles.getBubble(XcurrentBall, YCurrentBall));
								bubbles.getBubble(bubbleXY.x, bubbleXY.y).setX(bubbleXY.x);
								bubbles.getBubble(bubbleXY.x, bubbleXY.y).setY(bubbleXY.y);
								bubbles.setBubbleNull(XcurrentBall, YCurrentBall);
								bubbleToMove = null;
								if(stats.getScore() > 0)
									stats.setScore((stats.getScore()-1));
								boolean checkResult = bubbles.checkPattern(
										bubbles.getBubble(bubbleXY.x, bubbleXY.y).getBallColor(), stats);
								if(checkResult == true) {
									scoreMusic.play();
									stats.setComboAchievementCounter(1);
								} else {
									stats.resetCombo();
								}
								textStroke.setText("Score\n" + stats.getScore());
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

			} else {
				textStroke.setText("Final Score\n" + stats.getScore());
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