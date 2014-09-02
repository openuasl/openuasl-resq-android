package openuasl.resq.android.activity;

import openuasl.resq.android.R;
import openuasl.resq.android.app.ResquerApp;
import openuasl.resq.android.uavcontrol.UavControlCommunication;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SplashActivity extends Activity {
	protected static final Context SplashActivity = null;
	int chk = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		initialize();
	}

	private void initialize() {
		Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				startActivity(new Intent(SplashActivity.this,
						LoginActivity.class));
				
//				startActivity(new Intent(SplashActivity.this,
//						ControllerActivity.class));
				chk=1;
			}
		};
		
		handler.sendEmptyMessageDelayed(0, 1000); 
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(chk == 1){
		startActivity(new Intent(SplashActivity.this,
				LoginActivity.class));
		
//		startActivity(new Intent(SplashActivity.this,
//				ControllerActivity.class));
		
			chk=0;
		}
	}
	
	@Override
	protected void onDestroy() {
				/*
		new Thread(new Runnable() {	
			@Override
			public void run() {
				ResquerApp app = (ResquerApp)getApplication();
				((UavControlCommunication)app.commMW).sendControlEnd();
				((UavControlCommunication)app.commMW).Close();
			}
		}).start();*/
						
		super.onDestroy();
	}
}
