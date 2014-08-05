package openuasl.resq.android.activity;

import java.util.HashMap;

import openuasl.resq.android.R;
import openuasl.resq.android.app.AlertDialogManager;
import openuasl.resq.android.app.SessionManager;
import openuasl.resq.android.auth.OnQRCodeCertResultListener;
import openuasl.resq.android.ucstream.UavCameraStreamer;
import android.app.*;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginmainActivity extends Activity {
	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	// Session Manager Class
	SessionManager session;

	// Button Logout
	Button btnLogout;

	protected void onCreate(Bundle savedInstanceState) {
		Log.i("uiu", "main");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login_main);
		// Session class instance
		session = new SessionManager(getApplicationContext());
		Log.i("uiu", "enter");
		TextView lblName = (TextView) findViewById(R.id.lblName);
		TextView lblEmail = (TextView) findViewById(R.id.lblEmail);
		Log.i("uiu", "text");
		// Button logout
		btnLogout = (Button) findViewById(R.id.btnLogout);

		Toast.makeText(getApplicationContext(),
				"User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG)
				.show();

		/**
		 * Call this function whenever you want to check user login This will
		 * redirect user to LoginActivity is he is not logged in
		 * */
		session.checkLogin();

		// get user data from session
		HashMap<String, String> user = session.getUserDetails();

		// name
		String name = user.get(SessionManager.KEY_NAME);

		// email
		String email = user.get(SessionManager.KEY_EMAIL);

		// displaying user data
		lblName.setText(Html.fromHtml("Name: <b>" + name + "</b>"));
		lblEmail.setText(Html.fromHtml("Device Id: <b>" + email + "</b>"));

		/**
		 * Logout button click event
		 * */
		btnLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Clear the session data
				// This will clear all session data and
				// redirect user to LoginActivity
				session.logoutUser();
			}
		});
	}

}
