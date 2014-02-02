package com.marbles.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.games.GamesClient;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.marblesheaven.R;

public class SplashScreenActivity extends BaseGameActivity implements View.OnClickListener {

	private ImageView new_game, settings, quit, leaderboard;
	private GamesClient.Builder mGamesClient;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		mGamesClient = new GamesClient.Builder(this, mHelper, null);
		mGamesClient.setShowConnectingPopup(false);
		new_game = (ImageView) findViewById(R.id.new_game);
		settings = (ImageView) findViewById(R.id.settings);
		leaderboard = (ImageView) findViewById(R.id.leaderboard);
		findViewById(R.id.sign_in_button).setOnClickListener(this);
	    findViewById(R.id.sign_out_button).setOnClickListener(this);  
		quit = (ImageView) findViewById(R.id.quit);
		new_game.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SplashScreenActivity.this, MarbleGameActivity.class);
				startActivity(i);
				
			}
		});
		leaderboard.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivityForResult(getGamesClient().getLeaderboardIntent(
						getResources().getString(R.string.leaderboard_high_scores)),
						3);
				
			}
		});
		settings.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isSignedIn()) {
		            startActivityForResult(getGamesClient().getAchievementsIntent(),1);
		        } else {
		            showAlert("Not available.");
		        }			
			}
		});
		
		quit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				finish();
			}
		});
	}
	@Override
	public void onSignInFailed() {
		findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
	    findViewById(R.id.sign_out_button).setVisibility(View.GONE);
		
	}
	@Override
	public void onSignInSucceeded() {
		 findViewById(R.id.sign_in_button).setVisibility(View.GONE);
		 findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
		
	}
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.sign_in_button) {
	        beginUserInitiatedSignIn();
	    }
	    else if (view.getId() == R.id.sign_out_button) {
	        signOut();
	        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
	        findViewById(R.id.sign_out_button).setVisibility(View.GONE);
	    }
		
	}

}
