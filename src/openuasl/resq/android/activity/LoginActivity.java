package openuasl.resq.android.activity;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import bssm.blueeyes.R;
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
	MessageDigest md;
	// Email, password edittext
	EditText txtUsername, txtPassword;
	public int ck = -1;
	// login button
	Button btnLogin, qrbutton;
	
	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	public static byte[] devid_bytes = null;
	public static String devid_hex_num = new String();
	
	// Session Manager Class
	SessionManager session;

	/*************************************************/
	TextView tv_devid;
	TextView tv_uavid;
		
	/*************************************************/
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Session Manager
		session = new SessionManager(getApplicationContext());

		/*************************************************/
		tv_devid = (TextView)findViewById(R.id.textv_devid_string);
		tv_uavid = (TextView)findViewById(R.id.textv_uavid_string);
		
		setDeviceId();
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
	
	private void setDeviceId(){
		
		if(devid_hex_num.compareTo("") != 0) return;
		
		
		final TelephonyManager tm = (TelephonyManager) getBaseContext()
				.getSystemService(Context.TELEPHONY_SERVICE);

		final String androidId;
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
		try {
			md = MessageDigest.getInstance("SHA-256");
			int tmdevid = androidId.hashCode();
			final String deviceId = tmdevid + "";
			try {
				md.update(deviceId.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // Change this to "UTF-16"
				// if
				// needed
			devid_bytes = md.digest();

			for (int i = 0; i < devid_bytes.length; i++) {
				devid_hex_num += Integer
						.toHexString(0xff & (int) devid_bytes[i]);
			}

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		 Log.i("devid", devid_hex_num );
	}
	
}