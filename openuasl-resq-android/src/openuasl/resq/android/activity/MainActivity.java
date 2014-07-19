package openuasl.resq.android.activity;

import java.util.HashMap;

import openuasl.resq.android.activity.AlertDialogManager;
import openuasl.resq.android.activity.SessionManager;
import bssm.blueeyes.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("uiu", "start");
		startActivity(new Intent(this, SplashActivity.class));
		//
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.i("uiu", "middle");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
