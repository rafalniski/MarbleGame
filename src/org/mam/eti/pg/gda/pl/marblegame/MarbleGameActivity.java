package org.mam.eti.pg.gda.pl.marblegame;

import java.util.List;

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
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;
import org.mam.eti.pg.gda.pl.marblegame.PathFinder.Node;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.view.MotionEvent;

public class MarbleGameActivity extends SimpleBaseGameActivity implements
		IOnSceneTouchListener {
	
	/* Game screen variables */
	private static final int CAMERA_WIDTH = 1280;
	private static final int CAMERA_HEIGHT = 800;
	
	/* Helper classes objects */
	private BubblesGrid bubbles;
	private Grid gameGrid;
	private Achievement stats;
	private Ball bubbleToMove;
	private Scene scene;
	
	/* Game graphics*/
	private Sprite starS, clockS, starYS;
	
	/* Helper booleans and counters */
	private static int isColorUsed[] = new int[8];
	private int howManyMoves = 0;
	private boolean achiveColors, achiveMoves, achiveCombo;

	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions en = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(
						CAMERA_WIDTH,CAMERA_HEIGHT), camera);
		en.getAudioOptions().setNeedsMusic(true);
		return en;
	}
	
	@Override
	protected void onCreateResources() {
		TextureRegion.initTextures(this);
		TextureRegion.initSprites(this);
		MusicHelper.initSounds(mEngine, this);
	}
	
	@Override
	protected Scene onCreateScene() {
		scene = new Scene();
		scene.setBackground(TextureRegion.spriteBackground);
		scene.attachChild(TextureRegion.marked);
		scene.attachChild(TextureRegion.grid);
		scene.attachChild(TextureRegion.textStroke);
		scene.attachChild(TextureRegion.textStrokeAchievements);
		scene.attachChild(TextureRegion.textStrokeNextBalls);
		scene.attachChild(TextureRegion.restartButton);
		
		bubbles 		= new BubblesGrid();
		stats 			= new Achievement();
		gameGrid 		= new Grid();
		
		initAchievements(scene);
		initScene();
		
		scene.setOnSceneTouchListener(this);
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		
		return scene;
	}
	
	private void initScene() {
		bubbles.generateBallsAtTheBeggining(scene,this);
		stats.setScore(0);
		howManyMoves = 0;
		for (int i = 0; i < 8; i++)
			isColorUsed[i] = 0;
		TextureRegion.textStroke.setText("Score\n" + stats.getScore());
		TextureRegion.textStrokeNextBalls.setText("Next marbles");
		bubbles.generateNextBalls(scene, this);
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
					"Congratulations, you have done at least 10 moves!", 
					R.drawable.star);
		case Achievement.ACHIEVEMENT_COMBO:
			return createDialog(
					"Achievement unlocked!", 
					"Congratulations, you have score 2 matches in a row!", 
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
		TextureRegion.marked.setVisible(false);
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
			if (TextureRegion.marked.isVisible() == false) {
				TextureRegion.marked.setPosition(gridXY.x, gridXY.y);
				TextureRegion.marked.setVisible(true);
			} else {
				TextureRegion.marked.setPosition(gridXY.x, gridXY.y);
			}
		} else if (bubbleToMove != null && bubbleToMove.isX() == true) {
			// nic nie robomy kliknieta nieprzesuwalną kulkę.
		} else if (TextureRegion.marked.isVisible() == false) {
			// nic nie rob kliknieto puste pole
		}

		else { // klikniete zostalo puste pole - przenosimy tam babelek

			if (TextureRegion.marked.isVisible() == true && bubbles.isBubblesFull() == false) {
				TextureRegion.marked.setVisible(false); // wylaczamy ozaczenie klikniecia
				// dostajemy koordynaty siatki
				isColorUsed[bubbleToMove.getBallColor()] = 1;
				final int XcurrentBall = (int) bubbleToMove.getX();
				final int YCurrentBall = (int) bubbleToMove.getY();

				Point gridXY = gameGrid.getGridCoordinates(bubbleXY.x, bubbleXY.y,Ball.BALL_WIDTH, Ball.BALL_HEIGHT);
				final int[][] gridMap = gameGrid.getPathMap(bubbles);
				NewPathFinder finder = new NewPathFinder(gridMap);
				NewPathFinder.Point startPoint = finder.new Point(
						XcurrentBall + 1, YCurrentBall + 1);
				NewPathFinder.Point endPoint = finder.new Point(bubbleXY.x + 1,
						bubbleXY.y + 1);

				List<Node> nodes = finder.solve(startPoint, endPoint);
				Path path = null;
				if (nodes != null) {
					TextureRegion.marked.setVisible(false); // wylaczamy ozaczenie klikniecia

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
				if (path == null) {
					MusicHelper.failMusic.play();
					return;
				}
				PathModifier mPathModifier = new PathModifier(
						path.getLength() / 800, path); // wyrownanie predkosci
				IPathModifierListener mIPathModifierListener = new IPathModifierListener() {
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
									MusicHelper.scoreMusic.play();
									stats.setComboAchievementCounter(1);
								} else {
									stats.resetCombo();
								}
								TextureRegion.textStroke.setText("Score\n" + stats.getScore());
								checkAchivements();
								bubbles.addGeneratedBalls(getEngine().getScene(), MarbleGameActivity.this, gameGrid, stats);
								bubbles.generateNextBalls(getEngine().getScene(), MarbleGameActivity.this);
							}
						});
					}
				};
				
				mPathModifier.setPathModifierListener(mIPathModifierListener);
				mPathModifier.setAutoUnregisterWhenFinished(true);
				bubbles.getBubble(XcurrentBall, YCurrentBall).registerEntityModifier(mPathModifier);

				MusicHelper.moveMusic.play();

			} else {
				TextureRegion.textStroke.setText("Final Score\n" + stats.getScore());
					bubbles.detachNextBalls();
					MusicHelper.gameOverMusic.play();
			}

		}

	}
}