package com.example.cascade;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.ToggleButton;

public class Continuous extends Activity {
	
	CountDownTimer timer;
	int seconds = 30;
	double speedFactor = .48;
	TextView counter;
	ToggleButton redButton, greenButton, blueButton, leftButton, centerButton, rightButton;
	Chronometer clock;
	long time = 0;
	int score = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.continuous);
		
		initialize();
		startClock();
		
		//Randomizes the winning set at the bottom 
		Randomizer winningSet = new Randomizer();
		winningSet.random(leftButton, centerButton, rightButton);
		
		
		SharedPreferences getData = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String secValues = getData.getString("speed", "1");
		
		if(secValues.contentEquals("1")){
			seconds = 30;
			speedFactor = .56 ;
		}else if (secValues.contentEquals("2")){
			seconds = 20;
			speedFactor = .85;
		}else if (secValues.contentEquals("3")){
			seconds = 10;
			speedFactor = 1.8;	
		}else if (secValues.contentEquals("4")){
			seconds = 5;
			speedFactor = 3.4;	
		}
		
		//Controls the count down timer from 30 seconds and displays 
		//in 1 second intervals
		
			timer = new CountDownTimer((seconds*1000), 1) {
				@Override
				public void onTick(long millisUntilFinished) {
		         
		         //each millisecond it ticks the circle drops by 1/speedFactor 
		         //of the height of the path
		         moveButtons(redButton);
		         moveButtons(greenButton);
		         moveButtons(blueButton);
		         
		         Swap swap = new Swap();
		         
		         swap.swapButton(greenButton, redButton);
		         swap.swapButton(blueButton, redButton);
		         swap.swapButton(redButton, greenButton);
		         swap.swapButton(blueButton, greenButton);
		         swap.swapButton(redButton, blueButton);
		         swap.swapButton(greenButton, blueButton);
		                 
				}
				
				@Override
				public void onFinish() {
				
				WinTest winner = new WinTest();
				
				if(winner.test(leftButton,redButton) &&
				   winner.test(centerButton, blueButton) &&
				   winner.test(rightButton, greenButton)||
				   winner.test(leftButton,redButton) &&
				   winner.test(centerButton, greenButton) &&
				   winner.test(rightButton, blueButton)||
				   winner.test(leftButton, greenButton) &&
				   winner.test(centerButton, redButton) &&
				   winner.test(rightButton, blueButton) ||
				   winner.test(leftButton, greenButton) &&
				   winner.test(centerButton, blueButton) &&
				   winner.test(rightButton, redButton) ||
				   winner.test(leftButton, blueButton) &&
				   winner.test(centerButton, redButton) &&
				   winner.test(rightButton, greenButton) ||
				   winner.test(leftButton, blueButton) &&
				   winner.test(centerButton, greenButton) &&
				   winner.test(rightButton, redButton)){
				   
					score++;
					redButton.setY(0);
timer.start();}
				}
			}.start();;
	}
	
	public void initialize(){
		clock = (Chronometer) findViewById(R.id.clock);
		counter = (TextView) findViewById (R.id.countdown);
		redButton = (ToggleButton) findViewById (R.id.red);
		greenButton = (ToggleButton) findViewById (R.id.green);
		blueButton = (ToggleButton) findViewById (R.id.blue);
		leftButton = (ToggleButton) findViewById (R.id.leftButton);
		centerButton = (ToggleButton) findViewById (R.id.centerButton);
		rightButton = (ToggleButton) findViewById (R.id.rightButton);
	}
	
	public void startClock(){
		clock.setBase(SystemClock.elapsedRealtime()+time);
		clock.start();
	}
	
	//method that moves the circle down the path
	public void moveButtons (ToggleButton color){
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int height = size.y;
		
		final TextView path = (TextView) findViewById (R.id.colorPath);	  
		path.setY(height);
		
		if (color.getY()< path.getBottom()){
			color.setY((float) (color.getY()+ speedFactor));
		}		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.in_game_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()){

		case R.id.new_game:
		    Intent intent = getIntent();
		    overridePendingTransition(0, 0);
		    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		    finish();
			timer.cancel();
		    overridePendingTransition(0, 0);
		    startActivity(intent);
			break;

		case R.id.main_menu:
			timer.cancel();
		    finish();
			break;
			
		case R.id.exit:
			timer.cancel();
			finish();
			System.exit(0);
			break;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		timer.cancel();
		finish();	
	}
}
