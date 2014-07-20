package openuasl.resq.android.activity;

import openuasl.resq.android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SplashActivity extends Activity {
	protected static final Context SplashActivity = null;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		Log.i("uiu", "splash");
		initialize();
	}

	private void initialize() {
		Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				Log.i("uiu", "intent");
				startActivity(new Intent(SplashActivity.this,
						LoginmainActivity.class));
				
//				startActivity(new Intent(SplashActivity.this,
//						ControllerActivity.class));
				
				finish(); 

			}
		};
		
		Log.i("uiu", "handler");
		handler.sendEmptyMessageDelayed(0, 1000); 
	}
}
