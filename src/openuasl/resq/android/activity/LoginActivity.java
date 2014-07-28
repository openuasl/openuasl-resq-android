package openuasl.resq.android.activity;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import openuasl.resq.android.R;
import openuasl.resq.android.app.AlertDialogManager;
import openuasl.resq.android.app.ResquerApp;
import openuasl.resq.android.app.SessionManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	private ResquerApp app;
	
	EditText txtUsername, txtPassword;
	public int ck = -1;
	// login button
	Button btnLogin, qrbutton;
	
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
		
		// Email, Password input text
		txtPassword = (EditText) findViewById(R.id.editText1);

		Toast.makeText(getApplicationContext(),
				"User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG)
				.show();

		

		// Login button
		btnLogin = (Button) findViewById(R.id.button2);
		qrbutton = (Button) findViewById(R.id.button1);

		// Login button click event
		qrbutton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ck = 1;
				startActivity(new Intent(LoginActivity.this, QRCodeActivity.class));
				
				
			}
		});

		// Login button click event
		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				/*
				
				// Get username, password from EditText
				String password = txtPassword.getText().toString();

				// Check if username, password is filled
				if (ck == 1 && password.trim().length() > 0) {

					if (QRCodeActivity.qrvalueresult.equals("BlueEyes1")
							&& password.equals(devid_hex_num)) {

						session.createLoginSession("BlueEyes1",
								devid_hex_num);

						Intent i = new Intent(getApplicationContext(),
								MainActivity.class);
						startActivity(i);
						finish();

					} else {

						alert.showAlertDialog(LoginActivity.this,
								"Login failed..",
								"Username/Password is incorrect", false);
					}
				} else {

					alert.showAlertDialog(LoginActivity.this, "Login failed..",
							"Please enter username and password", false);
				}
				 
				*/
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