package openuasl.resq.android.activity;

import openuasl.resq.android.R;
import openuasl.resq.android.app.AlertDialogManager;
import openuasl.resq.android.app.ResquerApp;
import openuasl.resq.android.app.SessionManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	private ResquerApp app;
	
	public int ck = -1;
	// login button
	Button qrbutton;
	
	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();
	
	
	// Session Manager Class
	SessionManager session;

	/*************************************************/
	TextView tv_devid;
	TextView tv_uavid;
		
	/*************************************************/
	
	public static String devid_hex_num = new String();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		app = (ResquerApp)getApplication();
		
		devid_hex_num = app.getDeviceId();
		
		// Session Manager
		session = new SessionManager(getApplicationContext());

		/*************************************************/
		tv_devid = (TextView)findViewById(R.id.textv_devid_string);
		tv_uavid = (TextView)findViewById(R.id.textv_uavid_string);
		
		tv_devid.setText(devid_hex_num);
		/*************************************************/
		
		Toast.makeText(getApplicationContext(),
				"User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG)
				.show();

		// Login button
		qrbutton = (Button) findViewById(R.id.button1);

		// Login button click event
		qrbutton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ck = 1;
				startActivity(new Intent(LoginActivity.this, QRCodeActivity.class));
				
				
			}
		});
		
	}
		
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		tv_uavid.setText(QRCodeActivity.qrvalueresult);

		if (QRCodeActivity.qrvalueresult.compareTo("") != 0) {
			startActivity(new Intent(LoginActivity.this,
					ControllerActivity.class));
		}
	}
	
	
	
}