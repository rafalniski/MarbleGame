package org.mam.eti.pg.gda.pl.marblegame;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class SplashScreenActivity extends Activity {

	private ImageView new_game, settings, quit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		new_game = (ImageView) findViewById(R.id.new_game);
		settings = (ImageView) findViewById(R.id.settings);
		quit = (ImageView) findViewById(R.id.quit);
		new_game.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SplashScreenActivity.this, MarbleGameActivity.class);
				startActivity(i);
				
			}
		});
		
		settings.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SplashScreenActivity.this, SettingsActivity.class);
				startActivity(i);
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

}
