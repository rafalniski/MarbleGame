package com.marbles;



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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.games.GamesClient;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;
import com.marbles.PathFinder.Node;
import com.marblesheaven.R;

public class MarbleGameActivity extends SimpleBaseGameActivity implements
		IOnSceneTouchListener, GameHelperListener, OnConnectionFailedListener, ConnectionCallbacks {
	
	/* Game screen variables */
	private static final int CAMERA_WIDTH = 1280;
	private static final int CAMERA_HEIGHT = 800;
	
	/* Helper classes objects */
	private BubblesGrid bubbles;
	private Grid gameGrid;
	private Achievement stats;
	private Ball bubbleToMove;
	private Scene scene;
	private GamesClient.Builder mGamesClient;
	private GamesClient mGames;
	
	/* Game graphics*/
	private Sprite starS, clockS, starYS;
	
	/* Helper booleans and counters */
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
		
		
		bubbles = new BubblesGrid();
		stats = new Achievement();
		gameGrid = new Grid();
		
		initAchievements(scene);
		initScene();
		
		scene.setOnSceneTouchListener(this);
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		return scene;
	}
	
	private void initScene() {
		bubbles.generateBallsAtTheBeggining(scene,this);
		stats.setScore(0);
		stats.resetMoves();
		for (int i = 0; i < 8; i++)
			stats.setIsColorUsed(i, 0);
		TextureRegion.textStroke.setText("Score\n" + stats.getScore());
		TextureRegion.textStrokeNextBalls.setText("Next marbles");
		bubbles.generateNextBalls(scene, this);
	}
	
	private void resetGame() {
		bubbles.resetBubbles();
		initScene();
		TextureRegion.marked.setVisible(false);
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
		if (stats.getMovesAchievementCounter() == Achievement.ACHIEVEMENT_MOVES_NUMBER_OF_MOVES && achiveMoves != true) {
			prefsEditor.putBoolean("achiveMoves", true);
			prefsEditor.apply();
			achiveMoves = true;
			starS = new Sprite(850, 280, TextureRegion.mStarAchive,
					getVertexBufferObjectManager());
			mEngine.getScene().attachChild(starS);
			if(mGames != null && mGames.isConnected()) {
				mGames.unlockAchievement("CgkIit_l_sYeEAIQAw");
			}
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
			if(mGames != null && mGames.isConnected()) {
				mGames.unlockAchievement("CgkIit_l_sYeEAIQBA");
			}
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					MarbleGameActivity.this.showDialog(Achievement.ACHIEVEMENT_COMBO);
				}
			});
		}
		boolean areAllUsed = true;
		for (int i = 0; i < 6; i++) {
			if (stats.getIsColorUsed(i) != 1)
				areAllUsed = false;
		}
		if (areAllUsed && !achiveColors) {
			prefsEditor.putBoolean("achiveColors", true);
			prefsEditor.apply();
			achiveColors = true;
			starYS = new Sprite(910, 280, TextureRegion.mStarYellow,
					getVertexBufferObjectManager());
			mEngine.getScene().attachChild(starYS);
			if(mGames != null && mGames.isConnected()) {
				mGames.unlockAchievement("CgkIit_l_sYeEAIQAQ");
			}
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					MarbleGameActivity.this.showDialog(Achievement.ACHIEVEMENT_COLORS);
				}
			});
		}

	}
	
	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
		mGamesClient = new GamesClient.Builder(getBaseContext(), this, this);
		mGames = mGamesClient.create();
		mGames.connect();
	}

	private void handleGridClick(TouchEvent pSceneTouchEvent) {
		// Getting bubble coordinates of clicked point in (x,y) format
		final Point bubbleXY = gameGrid.getBubbleCoordinates(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		// There is reset button there - reset game
		// TODO: delete this shit and implement onButtonClicked
		if (bubbleXY.x == 12 && bubbleXY.y == 7) {
			resetGame();
		// Clicked outside the grid - do nothing
		} else if (bubbleXY.x >= 8 || bubbleXY.y > 7) {
		
		// Clicked some bubble
		} else if (!bubbles.isBubblesNull(bubbleXY.x, bubbleXY.y)) {
			// We initialize it with clicked bubble - it's going to be moved (probably)
			bubbleToMove = bubbles.getBubble(bubbleXY.x, bubbleXY.y);
			// Grid coordinates of clicked bubble in px x px format 
			Point gridXY = gameGrid.getGridCoordinates(bubbleXY.x, bubbleXY.y, Ball.BALL_WIDTH, Ball.BALL_HEIGHT);
			// We marked clicked bubble with semi-transparent sprite
			if (TextureRegion.marked.isVisible() == false) {
				TextureRegion.marked.setPosition(gridXY.x, gridXY.y);
				TextureRegion.marked.setVisible(true);
			} else {
				TextureRegion.marked.setPosition(gridXY.x, gridXY.y);
			}
		// We clicked X bubble - nothing to do	
		} else if (bubbleToMove != null && bubbleToMove.isX() == true) {
		
		// We clicked empty field and there was no bubble prepared to be moved - do nothing
		} else if (TextureRegion.marked.isVisible() == false) {
		
		// We clicked empty filed, there is bubble to move and we still have free space in a grid. So, move it there:)
		} else if (TextureRegion.marked.isVisible() == true && bubbles.isBubblesFull() == false) {
				// Getting coordinates of ball that will be moved in format (x,y)
				final int XcurrentBall = (int) bubbleToMove.getX();
				final int YCurrentBall = (int) bubbleToMove.getY();
				// Set marked sprite invisible, it's no sense to show it any more now
				TextureRegion.marked.setVisible(false);
				// Helper binary table for finding shortest path
				final int[][] gridMap = gameGrid.getPathMap(bubbles);
				// Getting shortest path from clicked point to bubble to move point
				NewPathFinder finder = new NewPathFinder(gridMap);
				NewPathFinder.Point startPoint = finder.new Point(XcurrentBall + 1, YCurrentBall + 1);
				NewPathFinder.Point endPoint = finder.new Point(bubbleXY.x + 1,bubbleXY.y + 1);

				List<Node> nodes = finder.solve(startPoint, endPoint);
				Path path = null;
				if (nodes != null) {
					// count how many moved we have to destination
					int length = nodes.size();
					path = new Path(length);
					Point gridXY = null;
					for (Node point : nodes) {
						// get exact grid coordinates of all of the moves
						gridXY = gameGrid.getGridCoordinates(point.x, point.y,Ball.BALL_WIDTH, Ball.BALL_HEIGHT);
						path.to(gridXY.x + 15, gridXY.y + 15);
					}
				// There is no path to end point, so move is impossible
				} else {
					MusicHelper.failMusic.play();
					return;
				}
				// Increment moves achievement counter
				stats.setMovesAchievementCounter(1);
				// Increment color achievement counter
				stats.setIsColorUsed(bubbleToMove.getBallColor(),1);
				
				// Move the ball
				PathModifier mPathModifier = new PathModifier(path.getLength() / 800, path);
				IPathModifierListener mIPathModifierListener = new IPathModifierListener() {
					@Override
					public void onPathStarted(final PathModifier pPathModifier,
							final IEntity pEntity) {}

					@Override
					public void onPathWaypointStarted(
							PathModifier pPathModifier, IEntity pEntity,
							int pWaypointIndex) {}

					@Override
					public void onPathWaypointFinished(
							PathModifier pPathModifier, IEntity pEntity,
							int pWaypointIndex) {}

					@Override
					public void onPathFinished(PathModifier pPathModifier,
							IEntity pEntity) {
						// We finished moving the ball, so we can handle logic
						getEngine().runOnUpdateThread(new Runnable() {
							@Override
							public void run() {
								// TODO: move it to BubblesGrid
								// Set new bubble in a clicked point
								bubbles.setBubbles(bubbleXY.x, bubbleXY.y, bubbles.getBubble(XcurrentBall, YCurrentBall));
								// Set its coordinates
								bubbles.getBubble(bubbleXY.x, bubbleXY.y).setX(bubbleXY.x);
								bubbles.getBubble(bubbleXY.x, bubbleXY.y).setY(bubbleXY.y);
								// Bubble to move is now empty, so delete object
								bubbles.setBubbleNull(XcurrentBall, YCurrentBall);
								bubbleToMove = null;
								// For every move with punished with 1 point
								if(stats.getScore() > 0)
									stats.setScore((stats.getScore()-1));
								// Check if there is any pattern detected
								boolean checkResult = bubbles.checkPattern(
										bubbles.getBubble(bubbleXY.x, bubbleXY.y).getBallColor(), stats);
								// If yes, play sound and increment combo achievement counter
								if(checkResult == true) {
									MusicHelper.scoreMusic.play();
									stats.setComboAchievementCounter(1);
								// If not, c-c-c-combo breaker
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
			// No free space in a grid - end the game
			} else {
					TextureRegion.textStroke.setText("Final Score\n" + stats.getScore());
					bubbles.detachNextBalls();
					MusicHelper.gameOverMusic.play();
			}

	}

	@Override
	public void onSignInFailed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSignInSucceeded() {
		mGames.connect();
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

}